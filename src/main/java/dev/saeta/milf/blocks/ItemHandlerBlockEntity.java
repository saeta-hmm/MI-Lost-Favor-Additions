package dev.saeta.milf.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ItemHandlerBlockEntity extends BlockEntity {



    public ItemHandlerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);

    }

    public abstract AbstractBEItemHandler getItemHandler();

    public boolean canInsert(ItemStack stack){
        ItemStack remaining = stack.copy();
        for (int i = 0; i < getItemHandler().getSlots() && !remaining.isEmpty(); i++) {
            remaining = getItemHandler().insertItem(i, remaining, true);
        }
        return ItemStack.isSameItemSameComponents(stack, remaining);
    }

    public ItemStack insertAnywhere(ItemStack stack) {
        ItemStack remaining = stack;
        for (int i = 0; i < getItemHandler().getSlots() && !remaining.isEmpty(); i++) {
            remaining = getItemHandler().insertItem(i, remaining, false);
        }
        return remaining;
    }

    public abstract boolean canExtract();

    @NotNull
    public abstract BlockPos getOutputPos();

    public void dropContent(BlockPos pos, Level level){
        for (int i = 0; i < getItemHandler().getSlots(); i++) {
            ItemStack stack = getItemHandler().getStackInSlot(i);
            if(!stack.isEmpty()){
                Block.popResource(level, pos, stack);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        ListTag itemStacks = new ListTag();

        for (int i = 0; i < getItemHandler().getSlots(); i++) {
            ItemStack stack = getItemHandler().getStackInSlot(i);
            itemStacks.add(stack.isEmpty() ? new CompoundTag() : stack.save(registries));
        }

        tag.put("items", itemStacks);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains("items", Tag.TAG_LIST)){
            ListTag itemStacks = tag.getList("items", Tag.TAG_COMPOUND);


            for (int i = 0; i < getItemHandler().getSlots() && i < itemStacks.size(); i++) {
                CompoundTag stackCompound = itemStacks.getCompound(i);
                ItemStack itemStack = stackCompound.isEmpty() ? ItemStack.EMPTY : ItemStack.parse(registries, stackCompound).orElse(ItemStack.EMPTY);
                getItemHandler().setStackInSlot(i, itemStack);
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
