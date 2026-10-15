package dev.saeta.milf.blocks.clay_molds.medium.mold_base.unfired;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.ItemHandlerBlockEntity;
import dev.saeta.milf.blocks.ProgressProvider;
import dev.saeta.milf.blocks.fire_pit.FirePitProcessable;
import dev.saeta.milf.recipes.DoubleRecipeInput;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFRecipeTypes;
import dev.saeta.milf.util.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class UnfiredMoldBaseBlockEntity extends ItemHandlerBlockEntity implements FirePitProcessable, ProgressProvider {

    private int firingTicks = 0;
    private final static int MAX_FIRING_TICKS = 200;

    public UnfiredMoldBaseBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.UNFIRED_MOLD_BASE.get(), pos, blockState);
    }

    private final AbstractBEItemHandler itemHandler = new AbstractBEItemHandler(1) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty() || level == null) return false;

            var recipe = RecipeUtil.getValidRecipe(level, MILFRecipeTypes.IMPRESSION_MOLDING, new DoubleRecipeInput(stack, new ItemStack(getBlockState().getBlock())));

            return recipe.isPresent();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.isEmpty() || level == null) {
                setChanged();
                return;
            }

            var recipeInput = new DoubleRecipeInput(stack, new ItemStack(getBlockState().getBlock()));

            var recipe = RecipeUtil.getValidRecipe(level, MILFRecipeTypes.IMPRESSION_MOLDING, new DoubleRecipeInput(stack, new ItemStack(getBlockState().getBlock())));
            if(recipe.isPresent()){

                ItemStack outputStack = recipe.get().value().assemble(recipeInput, level.registryAccess());

                if(!(outputStack.getItem() instanceof BlockItem blockItem)) return;

                level.setBlock(worldPosition, blockItem.getBlock().withPropertiesOf(getBlockState()), Block.UPDATE_NONE);
            }
            setChanged();
        }

        @Override
        protected boolean testInput(ItemStack stack) {
            return isItemValid(0, stack);
        }
    };

    @Override
    public AbstractBEItemHandler getItemHandler() {
        return itemHandler;
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
    public float getCurrentProgress() {
        return (float) firingTicks / MAX_FIRING_TICKS;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("firingTicks", firingTicks);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains("firingTicks")){
            firingTicks = tag.getInt("firingTicks");
        }

    }

    @Override
    public void firePitTick() {
        firingTicks++;
        if(firingTicks >= MAX_FIRING_TICKS){
            if(level == null) return;
            var input = new SingleRecipeInput(new ItemStack(getBlockState().getBlock()));
            var recipe = RecipeUtil.getValidRecipe(level, MILFRecipeTypes.PIT_FIRING, input);

            if(recipe.isPresent() ){
                ItemStack output = recipe.get().value().assemble(input, level.registryAccess());
                if(output.getItem() instanceof BlockItem blockItem){
                    level.setBlock(worldPosition, blockItem.getBlock().withPropertiesOf(getBlockState()), Block.UPDATE_ALL);
                }

            }
            firingTicks = 0;
        }

        if(firingTicks % 10 == 0){
            setChanged();
        }
    }
}
