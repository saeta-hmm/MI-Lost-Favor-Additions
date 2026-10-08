package dev.saeta.milf.blocks.roasting_contraption;

import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntity;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.recipes.fire_pit.FirePitCookingRecipe;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RoastingContraptionBlockEntity extends BlockEntity {
    public RoastingContraptionBlockEntity( BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.ROASTING_CONTRAPTION.get(), pos, blockState);
    }

    public static final int SLOTS_COUNT = 6;
    private final int[] cookingTimes = new int[SLOTS_COUNT];
    private final boolean[] cookedSlots = new boolean[SLOTS_COUNT];

    private final static int MAX_COOKING_TIME = 200;


    private final RoastingContraptionItemHandler itemHandler = new RoastingContraptionItemHandler(SLOTS_COUNT);

    public static void serverTick(Level level, BlockPos pos, BlockState state, RoastingContraptionBlockEntity roastingContraptionBlockEntity){
        roastingContraptionBlockEntity.tick();
    }

    private void tick(){
        if(level == null) return;
        if(!itemHandler.hasItems()) return;
        if(!(level.getBlockEntity(worldPosition.below()) instanceof FirePitBlockEntity firePitBlockEntity)) return;
        if(!firePitBlockEntity.isLit()) return;

        for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
            if(itemHandler.getStackInSlot(i).isEmpty()) continue;
            if(cookedSlots[i]) continue;
            cookingTimes[i]++;

            if(cookingTimes[i] >= MAX_COOKING_TIME){

                cookingTimes[i] = 0;
                cookedSlots[i] = true;

                ItemStack input = itemHandler.getStackInSlot(i);
                RecipeManager recipeManager = level.getRecipeManager();

                var campfireRecipeOutput = recipeManager.getRecipeFor(
                        RecipeType.CAMPFIRE_COOKING,
                        new net.minecraft.world.item.crafting.SingleRecipeInput(input),
                        level
                );

                if(campfireRecipeOutput.isPresent()){
                    itemHandler.setStackInSlot(i, campfireRecipeOutput.get().value().getResultItem(level.registryAccess()).copy());
                    firePitBlockEntity.increaseBurnTicks(100);
                    continue;
                }

                var ownRecipeOutput = recipeManager.getRecipeFor(
                        MILFRecipeTypes.FIRE_PIT_COOKING,
                        new SingleRecipeInput(input),
                        level
                );

                if(ownRecipeOutput.isPresent()){
                    itemHandler.setStackInSlot(i, ownRecipeOutput.get().value().getResultItem(level.registryAccess()).copy());
                    firePitBlockEntity.increaseBurnTicks(153);
                    continue;
                }

            } else {
                if(cookingTimes[i] % (46 + i*4) == 0 && level.getRandom().nextFloat() < 0.25){
                    level.playSound(null, worldPosition,
                            SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 1f, 1f);
                }
            }


        }
    }

    public ItemStack insertAnywhere(ItemStack stack) {
        ItemStack remaining = stack;
        for (int i = 0; i < itemHandler.getSlots() && !remaining.isEmpty(); i++) {
            remaining = itemHandler.insertItem(i, remaining, false);
        }
        return remaining;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        ListTag itemStacks = new ListTag();

        for (int i = 0; i < SLOTS_COUNT; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            itemStacks.add(stack.isEmpty() ? new CompoundTag() : stack.save(registries));
        }

        tag.put("items", itemStacks);

        tag.putIntArray("cookingTimes", cookingTimes);

        byte[] cookedBytes = new byte[SLOTS_COUNT];
        for (int i = 0; i < SLOTS_COUNT; i++) {
            cookedBytes[i] = (byte) (cookedSlots[i] ? 1 : 0);
        }
        tag.putByteArray("cookedSlots", cookedBytes);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains("items", Tag.TAG_LIST)){
            ListTag itemStacks = tag.getList("items", Tag.TAG_COMPOUND);


            for (int i = 0; i < SLOTS_COUNT && i < itemStacks.size(); i++) {
                CompoundTag stackCompound = itemStacks.getCompound(i);
                ItemStack itemStack = stackCompound.isEmpty() ? ItemStack.EMPTY : ItemStack.parse(registries, stackCompound).orElse(ItemStack.EMPTY);
                itemHandler.setStackInSlot(i, itemStack);
            }

        }

        if (tag.contains("cookingTimes", Tag.TAG_INT_ARRAY)) {
            int[] loadedTimes = tag.getIntArray("cookingTimes");
            System.arraycopy(loadedTimes, 0, cookingTimes, 0, Math.min(loadedTimes.length, SLOTS_COUNT));
        }

        if (tag.contains("cookedSlots", Tag.TAG_BYTE_ARRAY)) {
            byte[] loadedCooked = tag.getByteArray("cookedSlots");
            for (int i = 0; i < SLOTS_COUNT && i < loadedCooked.length; i++) {
                cookedSlots[i] = loadedCooked[i] == 1;
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

    private class RoastingContraptionItemHandler extends ItemStackHandler{

        public RoastingContraptionItemHandler(int size) {
            super(size);
        }

        @Override
        public int getSlotLimit(int slot) {

            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (level == null) return false;

            RecipeManager recipeManager = level.getRecipeManager();

            List<FirePitCookingRecipe> allRecipes = FirePitCookingRecipe.getAllRecipes(recipeManager, level.registryAccess());
            return allRecipes.stream()
                    .anyMatch(firePitCookingRecipe -> {
                        return firePitCookingRecipe.input().test(stack);
                    });


        }

        @Override
        protected void onContentsChanged(int slot) {

            if (getStackInSlot(slot).isEmpty()) {
                cookedSlots[slot] = false;
                cookingTimes[slot] = 0;
            }

            setChanged();
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {

            if (!cookedSlots[slot] && cookingTimes[slot] != 0) {
                return ItemStack.EMPTY;
            }

            return super.extractItem(slot, amount, simulate);
        }

        public boolean hasItems() {
            for (int i = 0; i < getSlots(); i++) {
                if(!getStackInSlot(i).isEmpty()) return true;
            }
            return false;
        }

    }
}
