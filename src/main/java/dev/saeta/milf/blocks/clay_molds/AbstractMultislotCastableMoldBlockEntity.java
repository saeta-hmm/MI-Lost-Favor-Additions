package dev.saeta.milf.blocks.clay_molds;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.MultislotProgressProvider;
import dev.saeta.milf.networking.data.MultiFluidContents;
import dev.saeta.milf.networking.data.StoredItem;
import dev.saeta.milf.recipes.casting.CastingRecipeInput;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMultislotCastableMoldBlockEntity extends AbstractClayMoldBlockEntity implements CastableMoldBE, MultislotProgressProvider {

    protected int slots = getSlots();

    protected int[] castingTicks = new int[getSlots()];
    protected int[] clientCastingTicks = new int[getSlots()];
    protected int[] lastServerCastingTicks = new int[getSlots()];
    protected int[] castingMaxTicks = new int[getSlots()];
    protected boolean[] isSolidifying = new boolean[getSlots()];
    protected ItemStack[] currentOutputs = new ItemStack[getSlots()];
    protected ItemStack moldRemains = ItemStack.EMPTY;

    protected int capacity = getInitialCapacity();

    protected final AbstractMultislotCastableMoldItemHandler itemHandler = new AbstractMultislotCastableMoldItemHandler(getSlots());
    protected final AbstractMultislotCastableMoldFluidHandler fluidHandler = new AbstractMultislotCastableMoldFluidHandler();


    protected final FluidTank[] fluidTanks = new AbstractMultislotCastableMoldFluidTank[getSlots()];

    public AbstractMultislotCastableMoldBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

        for (int i = 0; i < slots; i++) {
            fluidTanks[i] = new AbstractMultislotCastableMoldFluidTank(capacity, i);
            currentOutputs[i] = ItemStack.EMPTY;
            castingMaxTicks[i] = -109;
        }

    }

    protected abstract int getInitialCapacity();
    public abstract int getSlots();

    @Override
    public float getCurrentProgress(int slot) {
        return (float) castingTicks[slot] /castingMaxTicks[slot];
    }

    @Override
    public float getProgressSlots() {
        return slots;
    }

    @Override
    public boolean canCastInto() {
        for (int i = 0; i < slots; i++) {
            if(itemHandler.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }

    @Override
    public AbstractBEItemHandler getItemHandler() {
        return itemHandler;
    }

    public AbstractMultislotCastableMoldFluidHandler getFluidHandler() {
        return fluidHandler;
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

    public FluidTank getFluidTank(int slot) {
        return fluidTanks[slot];
    }

    public ItemStack getCurrentOutput(int slot) {
        return currentOutputs[slot];
    }

    public boolean isSolidifying(int slot) {
        return isSolidifying[slot];
    }

    // I HATE NETWORKING!!!
    public float getInterpolatedClientProgress(float partialTick, int slot){
        return Mth.clamp((clientCastingTicks[slot] + partialTick) / (float) castingMaxTicks[slot], 0, 1);
    }

    protected void clientTick(){
        for (int i = 0; i < getSlots(); i++) {
            clientSlotTick(i);
        }
    }

    protected void clientSlotTick(int slot){
        if(isSolidifying[slot]) {
            clientCastingTicks[slot]++;
        }

        if(castingTicks[slot] != lastServerCastingTicks[slot]){
            lastServerCastingTicks[slot] = castingTicks[slot];
            clientCastingTicks[slot] = castingTicks[slot];
        }
    }

    protected void serverSlotTick(int slot){
        if(isSolidifying[slot]) {
            castingTicks[slot]++;

            if(castingTicks[slot]%10 == 0) setChanged();
        }

        if(castingTicks[slot] == castingMaxTicks[slot]){
            castingTicks[slot] = 0;
            castingMaxTicks[slot] = -109;

            if(level == null) return;

            RecipeManager recipeManager = level.getRecipeManager();
            CastingRecipeInput castingRecipeInput = new CastingRecipeInput(new ItemStack(getBlockState().getBlock()), fluidTanks[slot].getFluid());

            var recipe = recipeManager.getRecipeFor(MILFRecipeTypes.CASTING, castingRecipeInput, level);

            if(recipe.isPresent()){

                fluidTanks[slot].setFluid(FluidStack.EMPTY);
                itemHandler.setStackInSlot(slot, currentOutputs[slot]);
                currentOutputs[slot] = ItemStack.EMPTY;
                isSolidifying[slot] = false;
            }

            setChanged();
        }
    }

    protected void serverTick(){
        for (int i = 0; i < getSlots(); i++) {
            serverSlotTick(i);
        }
    }

    public void setCapacity(int capacity){
        this.capacity = capacity;
    }

    public boolean tryExtract(){

        if(level == null) return false;
        boolean[] hasOutput = new boolean[slots];
        boolean shouldExtract = false;

        for (int i = 0; i < getSlots(); i++) {
            if(isSolidifying[i] || itemHandler.getStackInSlot(i).isEmpty() || !fluidTanks[i].isEmpty()) continue;
            hasOutput[i] = true;
            shouldExtract = true;
        }

        if (!shouldExtract) return false;

        for (int i = 0; i < getSlots(); i++) {
            if(hasOutput[i]) {
                Block.popResource(level, worldPosition, itemHandler.getStackInSlot(i));
            };
        }

        Block.popResource(level, worldPosition, moldRemains);
        level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        level.playSound(null, worldPosition, SoundEvents.DECORATED_POT_SHATTER, SoundSource.BLOCKS);
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        ListTag fluids = new ListTag();
        for (int i = 0; i < slots; i++) {
            fluids.add(fluidTanks[i].writeToNBT(registries, new CompoundTag()));
        }
        tag.put("fluids", fluids);

        tag.putInt("capacity", capacity);

        tag.putIntArray("castingTicks", castingTicks);
        tag.putIntArray("castingMaxTicks", castingMaxTicks);

        byte[] solidifyingBytes = new byte[slots];
        for (int i = 0; i < slots; i++) {
            solidifyingBytes[i] = (byte) (isSolidifying[i] ? 1 : 0);
        }
        tag.putByteArray("isSolidifying", solidifyingBytes);



        ListTag outputs = new ListTag();
        for (int i = 0; i < slots; i++) {
            outputs.add(currentOutputs[i].saveOptional(registries));
        }
        tag.put("currentOutputs", outputs);

        tag.put("moldRemains", moldRemains.saveOptional(registries));

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);


        if(tag.contains("capacity")){
            capacity = tag.getInt("capacity");
            setCapacity(capacity);
        }

        if(tag.contains("castingTicks")){
            castingTicks = tag.getIntArray("castingTicks");
            clientCastingTicks = tag.getIntArray("castingTicks");
        }

        if(tag.contains("castingMaxTicks")){
            castingMaxTicks = tag.getIntArray("castingMaxTicks");
        }

        if (tag.contains("isSolidifying")) {
            byte[] byteArray = tag.getByteArray("isSolidifying");
            for (int i = 0; i < slots; i++) {
                isSolidifying[i] = i < byteArray.length && byteArray[i] != 0;
            }
        }

        if (tag.contains("fluids")) {
            ListTag list = tag.getList("fluids", Tag.TAG_COMPOUND);
            for (int i = 0; i < slots && i < list.size(); i++) {
                fluidTanks[i].readFromNBT(registries, list.getCompound(i));
            }
        }

        if (tag.contains("currentOutputs")) {
            ListTag list = tag.getList("currentOutputs", Tag.TAG_COMPOUND);
            for (int i = 0; i < slots; i++) {
                currentOutputs[i] = (i < list.size()) ? ItemStack.parseOptional(registries, list.getCompound(i)) : ItemStack.EMPTY;
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

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        List<FluidStack> fluids = new ArrayList<>();

        for (int i = 0; i < slots; i++) {
            FluidTank fluidTank = fluidTanks[i];
            FluidStack fluidStack = fluidTank.getFluid();
            fluids.add(fluidStack.copy());


        }

        List<ItemStack>stacks = new ArrayList<>();

        for (int i = 0; i < slots; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            stacks.add(stack.copy());

        }
        components.set(MILFDataComponents.FLUIDS, new MultiFluidContents(fluids) );

        components.set(MILFDataComponents.STACKS, ItemContainerContents.fromItems(stacks) );

        components.set(MILFDataComponents.SINGLE_STACK, new StoredItem(moldRemains.copy()) );
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);

        MultiFluidContents fluids = componentInput.getOrDefault(MILFDataComponents.FLUIDS, MultiFluidContents.EMPTY);
        var fluidStacks = fluids.fluids();

        for (int i = 0; i < slots; i++) {
            FluidStack fluid = i < fluidStacks.size() ? fluidStacks.get(i) : FluidStack.EMPTY;

            fluidTanks[i].setFluid(fluid);
        }

        ItemContainerContents stacks = componentInput.getOrDefault(MILFDataComponents.STACKS, ItemContainerContents.EMPTY);

        for (int i = 0; i < slots; i++) {
            ItemStack itemStack = i < stacks.getSlots() ? stacks.getStackInSlot(i) : ItemStack.EMPTY;

            itemHandler.setStackInSlot(i, itemStack);
        }

        moldRemains = componentInput.getOrDefault(MILFDataComponents.SINGLE_STACK, new StoredItem(ItemStack.EMPTY.copy())).stack();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(level != null && !level.isClientSide) checkCurrentRecipes();
    }

    public class AbstractMultislotCastableMoldItemHandler extends AbstractBEItemHandler{
        public AbstractMultislotCastableMoldItemHandler(int size) {
            super(size);
        }

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
    }

    public class AbstractMultislotCastableMoldFluidHandler implements IFluidHandler {


        @Override
        public int getTanks() {
            return getSlots();
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            return fluidTanks[tank].getFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            return fluidTanks[tank].getCapacity();
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return !isSolidifying[tank] && itemHandler.getStackInSlot(tank).isEmpty();
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            for (int i = 0; i < getSlots(); i++) {
                if(!itemHandler.getStackInSlot(i).isEmpty() || isSolidifying(i)) continue;
                if(fluidTanks[i].fill(resource, FluidAction.SIMULATE) != 0){
                    return fluidTanks[i].fill(resource, action);
                }
            }
            return 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            for (int i = 0; i < getSlots(); i++) {
                if(isSolidifying(i)) continue;
                if(!fluidTanks[i].drain(resource, FluidAction.SIMULATE).isEmpty()){
                    return fluidTanks[i].drain(resource, action);
                }
            }
            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            for (int i = 0; i < getSlots(); i++) {
                if(isSolidifying(i)) continue;
                if(!fluidTanks[i].drain(maxDrain, FluidAction.SIMULATE).isEmpty()){
                    return fluidTanks[i].drain(maxDrain, action);
                }
            }
            return FluidStack.EMPTY;
        }


    }

    public class AbstractMultislotCastableMoldFluidTank extends FluidTank {

        private final int slot;

        public AbstractMultislotCastableMoldFluidTank(int capacity, int slot) {
            super(capacity);
            this.slot =slot;
        }

        protected void onContentsChanged() {
            checkCurrentRecipeInSlot(slot);
        }

    }

    protected void checkCurrentRecipeInSlot(int slot){
        if(level == null) return;

        RecipeManager recipeManager = level.getRecipeManager();
        CastingRecipeInput castingRecipeInput = new CastingRecipeInput(new ItemStack(getBlockState().getBlock()), fluidTanks[slot].getFluid());

        var recipe = recipeManager.getRecipeFor(MILFRecipeTypes.CASTING, castingRecipeInput, level);

        if(recipe.isPresent() && !isSolidifying[slot]){
            isSolidifying[slot] = true;
            castingMaxTicks[slot] = recipe.get().value().time();
            currentOutputs[slot] = recipe.get().value().assemble(castingRecipeInput, level.registryAccess());
            moldRemains = recipe.get().value().moldRemains().copy();
        }

        setChanged();
    }

    public void checkCurrentRecipes(){
        for (int i = 0; i < slots; i++) {
            checkCurrentRecipeInSlot(i);
        }
    }

}
