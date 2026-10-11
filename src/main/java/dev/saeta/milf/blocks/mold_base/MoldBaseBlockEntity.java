package dev.saeta.milf.blocks.mold_base;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.ItemHandlerBlockEntity;
import dev.saeta.milf.recipes.DoubleRecipeInput;
import dev.saeta.milf.recipes.molding.ImpressionMoldingRecipe;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFRecipeTypes;
import dev.saeta.milf.util.RecipeUtil;
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

public class MoldBaseBlockEntity extends ItemHandlerBlockEntity {
    public MoldBaseBlockEntity(BlockPos pos, BlockState blockState) {
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
}
