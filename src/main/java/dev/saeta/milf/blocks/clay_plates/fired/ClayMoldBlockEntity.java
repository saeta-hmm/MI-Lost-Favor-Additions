package dev.saeta.milf.blocks.clay_plates.fired;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.ProgressProvider;
import dev.saeta.milf.blocks.clay_plates.AbstractClayPlateBlockEntity;
import dev.saeta.milf.networking.data.StoredItem;
import dev.saeta.milf.recipes.casting.CastingRecipeInput;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class ClayMoldBlockEntity extends AbstractClayPlateBlockEntity implements ProgressProvider {

    private int capacity = 1000;
    private int castingTicks = 0;
    private int clientCastingTicks = 0;
    private int lastServerCastingTicks = -1;

    private int castingMaxTicks = -109;

    private boolean isSolidifying = false;
    private ItemStack currentOutput = ItemStack.EMPTY;
    private ItemStack moldRemains = ItemStack.EMPTY;

    private final AbstractBEItemHandler itemHandler = new AbstractBEItemHandler(1) {

        @Override
        protected boolean testInput(ItemStack stack) {
            return isItemValid(0, stack);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }



        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {

            setChanged();


        }

    };

    private final FluidTank fluidTank = new FluidTank(capacity) {
        @Override
        protected void onContentsChanged() {
            if(level == null) return;

            RecipeManager recipeManager = level.getRecipeManager();
            CastingRecipeInput castingRecipeInput = new CastingRecipeInput(new ItemStack(getBlockState().getBlock()), fluidTank.getFluid());

            var recipe = recipeManager.getRecipeFor(MILFRecipeTypes.CASTING, castingRecipeInput, level);

            if(recipe.isPresent() && !isSolidifying){
                isSolidifying = true;
                castingMaxTicks = recipe.get().value().time();
                currentOutput = recipe.get().value().assemble(castingRecipeInput, level.registryAccess());
                moldRemains = recipe.get().value().moldRemains().copy();
            }

            setChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return !isSolidifying;
        }
    };

    public ClayMoldBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.CLAY_MOLD.get(), pos, blockState);
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public void setCapacity(int capacity){
        fluidTank.setCapacity(capacity);
        this.capacity = capacity;
    }

    public ItemStack getCurrentOutput() {
        return currentOutput;
    }

    public ItemStack getMoldRemains() {
        return moldRemains;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ClayMoldBlockEntity clayMoldBlockEntity){
        clayMoldBlockEntity.serverTick();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ClayMoldBlockEntity clayMoldBlockEntity){
        clayMoldBlockEntity.clientTick();
    }

    private void clientTick(){
        if(isSolidifying) {
            clientCastingTicks++;
        }

        if(castingTicks != lastServerCastingTicks){
            lastServerCastingTicks = castingTicks;
            clientCastingTicks = castingTicks;
        }
    }

    private void serverTick(){
        if(isSolidifying) {
            castingTicks++;

            if(castingTicks%10 == 0) setChanged();
        }

        if(castingTicks == castingMaxTicks){
            castingTicks = 0;
            castingMaxTicks = -109;

            if(level == null) return;

            RecipeManager recipeManager = level.getRecipeManager();
            CastingRecipeInput castingRecipeInput = new CastingRecipeInput(new ItemStack(getBlockState().getBlock()), fluidTank.getFluid());

            var recipe = recipeManager.getRecipeFor(MILFRecipeTypes.CASTING, castingRecipeInput, level);

            if(recipe.isPresent()){

                fluidTank.setFluid(FluidStack.EMPTY);
                itemHandler.setStackInSlot(0, currentOutput);
                currentOutput = ItemStack.EMPTY;
                isSolidifying = false;
            }

            setChanged();
        }
    }

    public boolean isSolidifying() {
        return isSolidifying;
    }

    @Override
    public float getCurrentProgress(){
        return (float) castingTicks /castingMaxTicks;
    }

    public boolean tryExtract(){
        if(isSolidifying || itemHandler.getStackInSlot(0).isEmpty() || !fluidTank.isEmpty() || level == null) return false;
        Block.popResource(level, worldPosition, moldRemains);
        Block.popResource(level, worldPosition, itemHandler.getStackInSlot(0));
        level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.playSound(null, worldPosition, SoundEvents.DECORATED_POT_SHATTER, SoundSource.BLOCKS);
        return true;

    }


    // I HATE NETWORKING!!!
    public float getInterpolatedClientProgress(float partialTick){
        return Mth.clamp((clientCastingTicks + partialTick) / (float) castingMaxTicks, 0, 1);
    }

    @Override
    public AbstractBEItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public @NotNull BlockPos getOutputPos() {
        return worldPosition;
    }

    @Override
    public boolean shouldDropContent() {
        return false;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if(!fluidTank.isEmpty()){
            components.set(MILFDataComponents.FLUID, SimpleFluidContent.copyOf(fluidTank.getFluid()));
        }

        if(!itemHandler.getStackInSlot(0).isEmpty()){
            components.set(MILFDataComponents.CAST_RESULT, new StoredItem(itemHandler.getStackInSlot(0).copy()) );
        }

        components.set(MILFDataComponents.SINGLE_STACK, new StoredItem(moldRemains.copy()) );
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);

        FluidStack fluidStack = componentInput.getOrDefault(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY).copy();
        fluidTank.setFluid(fluidStack);

        ItemStack castResultItem = componentInput.getOrDefault(MILFDataComponents.CAST_RESULT, new StoredItem(ItemStack.EMPTY.copy())).stack();
        itemHandler.setStackInSlot(0, castResultItem);

        moldRemains = componentInput.getOrDefault(MILFDataComponents.SINGLE_STACK, new StoredItem(ItemStack.EMPTY.copy())).stack();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("fluid", fluidTank.writeToNBT(registries, new CompoundTag()));

        tag.putInt("capacity", capacity);
        tag.putInt("castingTicks", castingTicks);
        tag.putInt("castingMaxTicks", castingMaxTicks);

        tag.putBoolean("isSolidifying", isSolidifying);

        tag.put("currentOutput", currentOutput.saveOptional(registries));
        tag.put("moldRemains", moldRemains.saveOptional(registries));


    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("fluid")) {
            fluidTank.readFromNBT(registries, tag.getCompound("fluid"));
        }

        if(tag.contains("capacity")){
            setCapacity(tag.getInt("capacity"));
        }

        if(tag.contains("castingTicks")){
            castingTicks = tag.getInt("castingTicks");
            clientCastingTicks = tag.getInt("castingTicks");
        }

        if(tag.contains("castingMaxTicks")){
            castingMaxTicks = tag.getInt("castingMaxTicks");
        }

        if(tag.contains("isSolidifying")){
            isSolidifying = tag.getBoolean("isSolidifying");;
        }

        if(tag.contains("currentOutput")){
            if(tag.getCompound("currentOutput").isEmpty()) {
                currentOutput = ItemStack.EMPTY;
            } else {
                currentOutput = ItemStack.parse(registries, tag.getCompound("currentOutput")).orElse(ItemStack.EMPTY);
            }

        }

        if(tag.contains("moldRemains")){
            if(tag.getCompound("moldRemains").isEmpty()) {
                moldRemains = ItemStack.EMPTY;
            } else {
                moldRemains = ItemStack.parse(registries, tag.getCompound("moldRemains")).orElse(ItemStack.EMPTY);
            }

        }
    }
}
