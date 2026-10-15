package dev.saeta.milf.blocks.basin;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.ItemHandlerBlockEntity;
import dev.saeta.milf.blocks.ProgressProvider;
import dev.saeta.milf.networking.payloads.BasinStirSyncPayload;
import dev.saeta.milf.recipes.basin.StoneBasinRecipeInput;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.registries.MILFRecipeTypes;
import dev.saeta.milf.util.RecipeUtil;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StoneBasinBlockEntity extends ItemHandlerBlockEntity implements ProgressProvider {
    private final static int STIR_USE_TICKS = 15;
    private final static int MAX_USE_TICKS = 50;
    private int useTicks = 0;

    protected int prevClientUseTicks = 0;
    protected int clientUseTicks = 0;

    private float stirAngle = 0;
    private float prevStirAngle = 0;

    private float stirOffset = 0;
    private float prevStirOffset = 0;

    protected int lastServerUseTicks = -1;

    private ItemStack currentOutput = ItemStack.EMPTY;
    private int currentRecipeStirs = -109;
    private int stirs = 0;

    private final AbstractBEItemHandler itemHandler = new AbstractBEItemHandler(4) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            if(level == null) return;

            checkRecipe();

            setChanged();
        }

        @Override
        protected boolean testInput(ItemStack stack) {
            return !stack.isEmpty();
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if(level == null) return super.insertItem(slot, stack, simulate);
            ItemStack toInsert = stack.copy();
            toInsert.set(MILFDataComponents.RANDOM_SEED, level.random.nextInt(109, 109109));
            return super.insertItem(slot, toInsert, simulate);

        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack stack = super.extractItem(slot, amount, simulate);

            stack.remove(MILFDataComponents.RANDOM_SEED);

            return stack;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if(level == null || stack.isEmpty()) return false;

            var recipes = RecipeUtil.getAllRecipes(level, MILFRecipeTypes.STONE_BASIN);

            return recipes.anyMatch(holder -> holder.value().inputs().get(slot).test(stack));

        }
    };

    protected final FluidTank fluidTank = new FluidTank(500) {
        @Override
        protected void onContentsChanged() {
            if(level == null) return;
            checkRecipe();
            setChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            var basinRecipes = RecipeUtil.getAllRecipes(level, MILFRecipeTypes.STONE_BASIN);
            return basinRecipes.anyMatch(holder -> holder.value().fluid().test(stack));
        }
    };

    public StoneBasinBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.STONE_BASIN.get(), pos, blockState);
    }

    private void checkRecipe(){
        List<ItemStack> inputs = new ArrayList<>();

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inputs.add(itemHandler.getStackInSlot(i));
        }

        StoneBasinRecipeInput recipeInput = new StoneBasinRecipeInput(inputs, getFluidTank().getFluid());

        var recipe = RecipeUtil.getValidRecipe(level, MILFRecipeTypes.STONE_BASIN, recipeInput);

        if(recipe.isPresent()){
            stirs = 0;
            currentRecipeStirs = recipe.get().value().stirs();
            currentOutput = recipe.get().value().output();
        } else {
            stirs = 0;
            currentRecipeStirs = -109;
            currentOutput = ItemStack.EMPTY;
        }
    }

    public void onFullStir(){
        if(level == null) return;

        stirs++;
        setChanged();

        if(stirs == currentRecipeStirs){

            List<ItemStack> inputs = new ArrayList<>();

            for (int i = 0; i < itemHandler.getSlots(); i++) {
                inputs.add(itemHandler.getStackInSlot(i));
            }

            StoneBasinRecipeInput recipeInput = new StoneBasinRecipeInput(inputs, getFluidTank().getFluid());

            var recipe = RecipeUtil.getValidRecipe(level, MILFRecipeTypes.STONE_BASIN, recipeInput);

            if(recipe.isPresent()){

                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                }

                var output = recipe.get().value().assemble(recipeInput, level.registryAccess());

                int slot = 0;

                for (int i = 0; i < output.getCount(); i++) {

                    int iteration = slot / 4 + 1;

                    itemHandler.setStackInSlot(slot % 4, output.copyWithCount(iteration));
                    slot++;

                }

                fluidTank.drain(recipe.get().value().fluid().amount(), IFluidHandler.FluidAction.EXECUTE);

            }

            stirs = 0;
            currentRecipeStirs = -109;
            currentOutput = ItemStack.EMPTY;

            setChanged();
        }
    }

    public void stir(){
        useTicks = (int) Mth.approach(useTicks, MAX_USE_TICKS, STIR_USE_TICKS);
        clientUseTicks = (int) Mth.approach(clientUseTicks, MAX_USE_TICKS, STIR_USE_TICKS);
        lastServerUseTicks = useTicks;
        setChanged();
    }

    public boolean canStir(){
        return currentOutput != ItemStack.EMPTY;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, StoneBasinBlockEntity stoneBasinBlockEntity){
        stoneBasinBlockEntity.serverTick();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, StoneBasinBlockEntity stoneBasinBlockEntity){
        stoneBasinBlockEntity.clientTick();
    }

    public int getUseTicks() {
        return useTicks;
    }

    public float getInterpolatedStirSpeed(float partialTick){
        if(clientUseTicks == 0) return 0;
        float ticks = Mth.clampedLerp(prevClientUseTicks, clientUseTicks, partialTick);
        return Mth.clamp(ticks / MAX_USE_TICKS, 0f, 1f);
    }

    public float getInterpolatedOffset(float partialTick){
        if(stirOffset == 0) return 0;
        float ticks = Mth.clampedLerp(prevStirOffset, stirOffset, partialTick);
        return Mth.clamp(ticks / MAX_USE_TICKS, 0f, 1f);
    }

    private void clientTick(){
        prevClientUseTicks = clientUseTicks;
        prevStirAngle = stirAngle;
        prevStirOffset = stirOffset;
        stirOffset = Mth.approachDegrees(stirOffset, clientUseTicks, canStir() ? 1 : 5);
        if(clientUseTicks >0) {
            clientUseTicks = (int) Mth.approachDegrees(clientUseTicks, 0, canStir() ? -1 : -5);
            float speed = Mth.clamp((float) clientUseTicks / MAX_USE_TICKS, 0f, 1f);
            speed = RenderUtil.EasingFunctions.easeInOutSine(speed);
            stirAngle += speed * 360 / 20;
            if(stirAngle >= 360){
                stirAngle %= 360f;
                stirs++;
                PacketDistributor.sendToServer(new BasinStirSyncPayload(worldPosition));
            }
        }

        if(useTicks != lastServerUseTicks){
            lastServerUseTicks = useTicks;
            clientUseTicks = useTicks;
        }



    }

    public float getInterpolatedStirAngle(float partialTick) {

        if (stirAngle < prevStirAngle) return Mth.lerp(partialTick, prevStirAngle, stirAngle + 360) % 360f;
        return Mth.lerp(partialTick, prevStirAngle, stirAngle) % 360f;
    }

    private void serverTick(){
        if(level == null) return;
        if(useTicks >0){
            useTicks--;
        }

    }

    @Override
    public AbstractBEItemHandler getItemHandler() {
        return itemHandler;
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean shouldDropContent() {
        return true;
    }

    @Override
    public @NotNull BlockPos getOutputPos() {
        return worldPosition;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("fluid", fluidTank.writeToNBT(registries, new CompoundTag()));

        tag.putInt("stirs", stirs);

        tag.putInt("currentRecipeStirs", currentRecipeStirs);

        tag.put("currentOutput",currentOutput.saveOptional(registries));


    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("fluid")) {
            fluidTank.readFromNBT(registries, tag.getCompound("fluid"));
        }

        if(tag.contains("stirs")){
            stirs = tag.getInt("stirs");
        }

        if(tag.contains("currentRecipeStirs")){
            currentRecipeStirs = tag.getInt("currentRecipeStirs");
        }

        if(tag.contains("currentOutput")){
            currentOutput = ItemStack.parseOptional(registries, tag.getCompound("currentOutput"));
        }

    }

    @Override
    public float getCurrentProgress() {
        if(currentRecipeStirs == -109) return 0;
        return (float) stirs / currentRecipeStirs;
    }
}
