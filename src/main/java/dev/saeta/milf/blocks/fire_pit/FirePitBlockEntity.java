package dev.saeta.milf.blocks.fire_pit;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.FlammableBlockEntity;
import dev.saeta.milf.blocks.ItemHandlerBlockEntity;
import dev.saeta.milf.blocks.ProgressProvider;
import dev.saeta.milf.blocks.kiln.fired.KilnBlock;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlockTags;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class FirePitBlockEntity extends ItemHandlerBlockEntity implements FlammableBlockEntity, ProgressProvider {

    private static final int LOG_SLOTS_COUNT = 4;
    public static final int COAL_SLOT = LOG_SLOTS_COUNT;
    public static final int COAL_CAPACITY = 16;
    private static final int NO_SLOT = -1;

    private int burnTime = 0;
    private int currentLogBurnTime = 0;
    private boolean isLit;

    private int burningSlot = NO_SLOT;
    private int burningSeq = 0;
    private final int[] logSeq = new int[LOG_SLOTS_COUNT];
    private int nextLogSeq = 1;

    private boolean imDead = false;

    private final AbstractBEItemHandler itemHandler = new AbstractBEItemHandler(LOG_SLOTS_COUNT + 1) {
        @Override
        public int getSlotLimit(int slot) {

            if (slot < COAL_SLOT) return 1;

            return COAL_CAPACITY;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (level == null) return false;

            if(stack.is(ItemTags.LOGS) && slot!=COAL_SLOT) return true;

            return false;


        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {

            int countBefore = getStackInSlot(slot).getCount();

            ItemStack returnStack = super.insertItem(slot, stack, simulate);

            // A partial insert returns a non-empty remainder but still puts logs in the pit,
            // so order by what actually landed in the slot rather than by the remainder.
            if(!simulate && slot < LOG_SLOTS_COUNT && getStackInSlot(slot).getCount() > countBefore){
                logSeq[slot] = nextLogSeq++;
            }

            return returnStack;

        }


        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

            if (level != null && !level.isClientSide()) {
                BlockState state = getBlockState();

                if(state.is(MILFBlocks.FIRE_PIT) && !imDead) {
                    //MILostFavor.LOGGER.info("mushroom ━┳━　━┳━");
                    level.setBlock(worldPosition, state.setValue(FirePitBlock.LOGS, getLogCount()), Block.UPDATE_NONE);
                }


            }
        }

        @Override
        protected boolean testInput(ItemStack stack) {
            return stack.is(ItemTags.LOGS);
        }

    };

    private int getLogBurnTime(ItemStack stack){
        return stack.getBurnTime(RecipeType.SMELTING) * 2;
    }

    public AbstractBEItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public boolean canExtract() {
        return !isLit();
    }

    @Override
    public boolean shouldDropContent() {
        return true;
    }

    @Override
    public void dropContent(BlockPos pos, Level level) {
        imDead = true;
        super.dropContent(pos, level);
    }

    @Override
    public @NotNull BlockPos getOutputPos() {
        return worldPosition.above();
    }

    @Override
    public Direction getOutputDirection() {
        if(level == null) return null;
        BlockState stateAbove =level.getBlockState(worldPosition.above());
        if(stateAbove.is(MILFBlocks.KILN)) return stateAbove.getValue(KilnBlock.FACING);
        return super.getOutputDirection();
    }

    public int getLogCount(){
        int count = 0;
        for (int i = 0; i < LOG_SLOTS_COUNT; i++) {
            ItemStack logStack = itemHandler.getStackInSlot(i);
            if(!logStack.isEmpty()) count++;
        }
        return count;
    }

    public int getCoalCount(){
        return itemHandler.getStackInSlot(COAL_SLOT).getCount();
    }

    private int nextLogSlot(){
        int bestSlot = NO_SLOT;
        int bestSeq = Integer.MAX_VALUE;

        for (int i = 0; i < LOG_SLOTS_COUNT; i++) {
            if(itemHandler.getStackInSlot(i).isEmpty()) continue;
            if(logSeq[i] < bestSeq){
                bestSeq = logSeq[i];
                bestSlot = i;
            }
        }

        return bestSlot;
    }

    private boolean startLog(){
        int slot = nextLogSlot();
        if(slot == NO_SLOT) return false;

        burningSlot = slot;
        burningSeq = logSeq[slot];
        burnTime = 0;
        currentLogBurnTime = getLogBurnTime(itemHandler.getStackInSlot(slot));
        return true;
    }

    private void finishLog(BlockState stateAbove){
        itemHandler.setStackInSlot(burningSlot, ItemStack.EMPTY);
        burningSlot = NO_SLOT;
        burningSeq = 0;
        burnTime = 0;

        int coalCount = getCoalCount();
        if (coalCount < COAL_CAPACITY){
            itemHandler.setStackInSlot(COAL_SLOT, new ItemStack(Items.CHARCOAL, coalCount + 1));
            setChanged();
        } else {
            setLit(false);

            var coalStack = new ItemStack(Items.CHARCOAL, 1);
            if(stateAbove.is(MILFBlocks.KILN)){
                Block.popResourceFromFace(level, worldPosition.above(),stateAbove.getValue(KilnBlock.FACING), coalStack);
            } else {
                Block.popResourceFromFace(level, worldPosition,Direction.UP,  coalStack);
            }
        }
    }

    public void increaseBurnTicks(int amount){
        if(!isLit) {

            if(burnTime != 0){
                burnTime = 0;
                setChanged();
            }
            return;
        };
        burnTime += amount;
    }

    @Override
    public float getCurrentProgress(){
        if(currentLogBurnTime <= 0) return 0f;
        return (float) burnTime / currentLogBurnTime;
    }

    public FirePitBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.FIRE_PIT.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FirePitBlockEntity firePitBlockEntity){
        firePitBlockEntity.tick();
    }

    private void tick(){
        if(level == null) return;
        if(!isLit) return;

        // A burn belongs to one log: the slot alone is not enough, the log in it can be swapped.
        boolean burning = burningSlot != NO_SLOT
                && !itemHandler.getStackInSlot(burningSlot).isEmpty()
                && logSeq[burningSlot] == burningSeq;

        if(!burning){
            burningSlot = NO_SLOT;

            if(!startLog()){
                burnTime = 0;
                setLit(false);
                return;
            }
        }

        burnTime++;
        BlockState stateAbove = level.getBlockState(worldPosition.above());
        if(burnTime > currentLogBurnTime){
            finishLog(stateAbove);

            if(!startLog()){
                burnTime = 0;
                setLit(false);
                return;
            }
        }

        if(burnTime % 10 == 0){
            setChanged();
        }

        if(stateAbove.is(MILFBlockTags.FIRE_PIT_CAN_PROCESS) && level.getBlockEntity(worldPosition.above()) instanceof FirePitProcessable firePitProcessable){
            firePitProcessable.firePitTick();
        }


    }

    @Override
    public boolean canBeIgnited() {
        return !isLit && getLogCount() > 0;
    }

    @Override
    public boolean isLit() {
        return isLit;
    }

    @Override
    public void ignite() {
        isLit = true;
        if (this.level == null || this.level.isClientSide) return;
        level.setBlock(worldPosition, getBlockState().setValue(BlockStateProperties.LIT, true), Block.UPDATE_NONE);
        setChanged();
    }

    public void setLit(boolean value) {
        isLit = value;
        if (this.level == null || this.level.isClientSide) return;
        level.setBlock(worldPosition, getBlockState().setValue(BlockStateProperties.LIT, value), Block.UPDATE_NONE);
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putBoolean("isLit", isLit);

        tag.putInt("burnTime", burnTime);

        tag.putInt("currentLogBurnTime", currentLogBurnTime);

        tag.putInt("burningSlot", burningSlot);

        tag.putInt("nextLogSeq", nextLogSeq);

        tag.putIntArray("logSeq", logSeq);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("isLit")) {
            isLit = tag.getBoolean("isLit");
        }

        if(tag.contains("burnTime")){
            burnTime = tag.getInt("burnTime");
        }

        if(tag.contains("currentLogBurnTime")){
            currentLogBurnTime = tag.getInt("currentLogBurnTime");
        }

        if(tag.contains("nextLogSeq")){
            nextLogSeq = tag.getInt("nextLogSeq");
        }

        burningSlot = tag.contains("burningSlot") ? tag.getInt("burningSlot") : NO_SLOT;
        if(burningSlot < 0 || burningSlot >= LOG_SLOTS_COUNT){
            burningSlot = NO_SLOT;
        }

        if (tag.contains("logSeq", Tag.TAG_INT_ARRAY)) {
            int[] storedSeq = tag.getIntArray("logSeq");
            System.arraycopy(storedSeq, 0, logSeq, 0, Math.min(storedSeq.length, logSeq.length));
        }

        burningSeq = burningSlot == NO_SLOT ? 0 : logSeq[burningSlot];

    }

}
