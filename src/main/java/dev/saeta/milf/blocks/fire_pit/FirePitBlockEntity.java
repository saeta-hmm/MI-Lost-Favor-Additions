package dev.saeta.milf.blocks.fire_pit;

import dev.saeta.milf.blocks.FlammableBlockEntity;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashSet;
import java.util.stream.Collectors;

public class FirePitBlockEntity extends BlockEntity implements FlammableBlockEntity {

    public static final int LOGS_SLOT = 0;

    private boolean isLit;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {

            switch (slot){
                case LOGS_SLOT: return 4;
            }

            return 0;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (level == null) return false;


            switch (slot){
                case LOGS_SLOT:
                    if(!stack.is(ItemTags.LOGS)) return false;

                    ItemStack logsStack = getStackInSlot(LOGS_SLOT);

                    if(logsStack.isEmpty() && stack.is(ItemTags.LOGS)){
                        return true;
                    } else if (ItemStack.isSameItemSameComponents(logsStack, stack) && logsStack.getCount() < getSlotLimit(LOGS_SLOT)){
                        return true;
                    }
            }

            return false;

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

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public int getLogCount(){
        return itemHandler.getStackInSlot(LOGS_SLOT).getCount();
    }

    public ItemStack insertAnywhere(ItemStack stack) {
        ItemStack remaining = stack;
        for (int i = 0; i < 1 && !remaining.isEmpty(); i++) {
            remaining = itemHandler.insertItem(i, remaining, false);
        }
        return remaining;
    }

    public FirePitBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.FIRE_PIT.get(), pos, blockState);
    }

    @Override
    public boolean canBeIgnited() {
        return !isLit && getLogCount() == 4;
    }

    @Override
    public boolean isLit() {
        return isLit;
    }

    @Override
    public void ignite() {
        isLit = true;
    }

    public void setLit(boolean value) {
        isLit = value;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        ListTag itemStacks = new ListTag();

        for (int i = 0; i < 1; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            itemStacks.add(stack.isEmpty() ? new CompoundTag() : stack.save(registries));
        }

        tag.put("items", itemStacks);

        tag.putBoolean("isLit", isLit);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains("items", Tag.TAG_LIST)){
            ListTag itemStacks = tag.getList("items", Tag.TAG_COMPOUND);


            for (int i = 0; i < 1 && i < itemStacks.size(); i++) {
                CompoundTag stackCompound = itemStacks.getCompound(i);
                ItemStack itemStack = stackCompound.isEmpty() ? ItemStack.EMPTY : ItemStack.parse(registries, stackCompound).orElse(ItemStack.EMPTY);
                itemHandler.setStackInSlot(i, itemStack);
            }

        }

        if (tag.contains("isLit")) {
            isLit = tag.getBoolean("isLit");
        }

    }

    @Override
    public void setChanged() {
        super.setChanged();
        if(level != null && !level.isClientSide() && level.isLoaded(worldPosition)){
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
}
