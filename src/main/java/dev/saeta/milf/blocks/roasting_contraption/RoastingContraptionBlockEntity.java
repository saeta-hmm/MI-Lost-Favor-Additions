package dev.saeta.milf.blocks.roasting_contraption;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.ItemHandlerBlockEntity;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntity;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.recipes.fire_pit.FirePitCookingRecipe;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RoastingContraptionBlockEntity extends ItemHandlerBlockEntity {
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
    public Direction getOutputDirection() {
        return getBlockState().getValue(RoastingContraptionBlock.FACING);
    }

    public AbstractBEItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

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

    private class RoastingContraptionItemHandler extends AbstractBEItemHandler {

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
        protected boolean testInput(ItemStack stack) {
            return canInsert(stack.copyWithCount(1));
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
