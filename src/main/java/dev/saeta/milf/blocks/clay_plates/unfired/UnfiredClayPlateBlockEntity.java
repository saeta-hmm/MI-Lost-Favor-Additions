package dev.saeta.milf.blocks.clay_plates.unfired;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.clay_plates.AbstractClayPlateBlockEntity;
import dev.saeta.milf.recipes.DoubleRecipeInput;
import dev.saeta.milf.recipes.molding.ImpressionMoldingRecipe;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public class UnfiredClayPlateBlockEntity extends AbstractClayPlateBlockEntity {

    private final AbstractBEItemHandler itemHandler = new AbstractBEItemHandler(1) {

        @Override
        protected boolean testInput(ItemStack stack) {
            return isItemValid(0, stack);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty() || level == null) return false;

            var recipe = getValidRecipe(new DoubleRecipeInput(stack, new ItemStack(getBlockState().getBlock()) ));

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

            var recipe = getValidRecipe( recipeInput);
            if(recipe.isPresent()){

                ItemStack outputStack = recipe.get().value().assemble(recipeInput, level.registryAccess());

                if(!(outputStack.getItem() instanceof BlockItem blockItem)) return;

                level.setBlock(worldPosition, blockItem.getBlock().defaultBlockState(), Block.UPDATE_NONE);
            }
            setChanged();
        }

    };

    public UnfiredClayPlateBlockEntity(BlockPos pos, BlockState blockState) {
        super(
                MILFBlockEntities.UNFIRED_CLAY_PLATE.get(),
                pos,
                blockState
        );
    }

    private Stream<RecipeHolder<ImpressionMoldingRecipe>> getAllRecipes(){
        if (level == null) return Stream.empty();

        RecipeManager recipeManager = level.getRecipeManager();

        return recipeManager.getAllRecipesFor(MILFRecipeTypes.IMPRESSION_MOLDING).stream();
    }

    private Optional<RecipeHolder<ImpressionMoldingRecipe>> getValidRecipe(DoubleRecipeInput input){
        if (level == null) return Optional.empty();

        return getAllRecipes().filter(holder -> holder.value().matches(input, level)).findFirst();
    }



    @Override
    public AbstractBEItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public @NotNull BlockPos getOutputPos() {
        return worldPosition;
    }
}
