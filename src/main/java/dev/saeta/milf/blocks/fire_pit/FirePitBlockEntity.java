package dev.saeta.milf.blocks.fire_pit;

import dev.saeta.milf.blocks.FlammableBlockEntity;
import dev.saeta.milf.blocks.clay_plates.fire_pit.FirePitPlateBlockEntity;
import dev.saeta.milf.blocks.kiln.KilnBlock;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FirePitBlockEntity extends BlockEntity implements FlammableBlockEntity {

    private static final int LOG_SLOTS_COUNT = 4;
    public static final int COAL_SLOT = LOG_SLOTS_COUNT;
    public static final int COAL_CAPACITY = 16;

    private int burnTime = 0;
    private int currentLogBurnTime = 109;
    private boolean isLit;

    private final List<Integer> burnOrder = new ArrayList<>();

    private final ItemStackHandler itemHandler = new ItemStackHandler(LOG_SLOTS_COUNT + 1) {
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

            ItemStack returnStack = super.insertItem(slot, stack, simulate);

            if(returnStack.isEmpty()){
                if(!simulate && slot < LOG_SLOTS_COUNT){
                    if(getLogCount() == 1){
                        currentLogBurnTime =getLogBurnTime(stack);
                    }
                    burnOrder.add(slot);
                }
            }

            return returnStack;

        }


        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

            if (level != null && !level.isClientSide()) {
                BlockState state = getBlockState();

                level.setBlock(worldPosition, state.setValue(FirePitBlock.LOGS, getLogCount()), Block.UPDATE_NONE);

            }
        }

    };

    private int getLogBurnTime(ItemStack stack){
        return stack.getBurnTime(RecipeType.SMELTING) * 2;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
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

    private ItemStack getFirstLog(){
        for (int i = 0; i < LOG_SLOTS_COUNT; i++) {
            ItemStack logStack = itemHandler.getStackInSlot(i);
            if(!logStack.isEmpty()) return logStack;
        }
        return ItemStack.EMPTY;
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

    public float getCurrentProgress(){
        return (float) burnTime / currentLogBurnTime;
    }

    public ItemStack insertLogAnywhere(ItemStack stack) {
        ItemStack remaining = stack;
        for (int i = 0; i < LOG_SLOTS_COUNT && !remaining.isEmpty(); i++) {
            remaining = itemHandler.insertItem(i, remaining, false);
        }
        return remaining;
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
        if(getLogCount() == 0){
            burnTime = 0;
            setLit(false);
            return;
        }
        if(burnOrder.isEmpty()){
            burnTime = 0;
            setLit(false);
            return;
        }

        burnTime++;
        BlockState stateAbove = level.getBlockState(worldPosition.above());
        if(burnTime > currentLogBurnTime){
            itemHandler.setStackInSlot(burnOrder.getFirst(), ItemStack.EMPTY);
            int coalCount = getCoalCount();
            if (coalCount < COAL_CAPACITY){
                var coalStack = new ItemStack(Items.CHARCOAL, coalCount + 1);
                itemHandler.setStackInSlot(COAL_SLOT, coalStack);
                setChanged();
            } else {
                var coalStack = new ItemStack(Items.CHARCOAL, 1);

                setLit(false);

                if(stateAbove.is(MILFBlocks.KILN)){
                    Block.popResourceFromFace(level, worldPosition.above(),stateAbove.getValue(KilnBlock.FACING), coalStack);
                } else {
                    Block.popResourceFromFace(level, worldPosition,Direction.UP,  coalStack);
                }

            }

            burnOrder.removeFirst();
            burnTime =0;

            if(getLogCount() > 0){
                ItemStack nextLog = itemHandler.getStackInSlot(burnOrder.getFirst()) ;

                currentLogBurnTime = getLogBurnTime(nextLog);

                setChanged();
            } else {
                setLit(false);
            }

        }

        if(burnTime % 10 == 0){
            setChanged();
        }

        if(stateAbove.is(MILFBlocks.FIRE_PIT_PLATE) && level.getBlockEntity(worldPosition.above()) instanceof FirePitPlateBlockEntity firePitPlateBlockEntity){
            firePitPlateBlockEntity.firePitTick();
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

        ListTag itemStacks = new ListTag();

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            itemStacks.add(stack.isEmpty() ? new CompoundTag() : stack.save(registries));
        }

        tag.put("items", itemStacks);

        tag.putBoolean("isLit", isLit);

        tag.putInt("burnTime", burnTime);

        tag.putInt("currentLogBurnTime", currentLogBurnTime);

        tag.putIntArray("burnOrder", burnOrder.stream().mapToInt(Integer::intValue).toArray());

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains("items", Tag.TAG_LIST)){
            ListTag itemStacks = tag.getList("items", Tag.TAG_COMPOUND);


            for (int i = 0; i < itemHandler.getSlots() && i < itemStacks.size(); i++) {
                CompoundTag stackCompound = itemStacks.getCompound(i);
                ItemStack itemStack = stackCompound.isEmpty() ? ItemStack.EMPTY : ItemStack.parse(registries, stackCompound).orElse(ItemStack.EMPTY);
                itemHandler.setStackInSlot(i, itemStack);
            }

        }

        if (tag.contains("isLit")) {
            isLit = tag.getBoolean("isLit");
        }

        if(tag.contains("burnTime")){
            burnTime = tag.getInt("burnTime");
        }

        if(tag.contains("currentLogBurnTime")){
            currentLogBurnTime = tag.getInt("currentLogBurnTime");
        }

        burnOrder.clear();
        if (tag.contains("burnOrder", Tag.TAG_INT_ARRAY)) {
            for (int slot : tag.getIntArray("burnOrder")) {
                burnOrder.add(slot);
            }
        }

    }

    @Override
    public void setChanged() {
        super.setChanged();
        if(level != null && !level.isClientSide() && level.isLoaded(worldPosition)){
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
