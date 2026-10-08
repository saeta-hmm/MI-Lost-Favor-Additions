package dev.saeta.milf.recipes.kiln;

import dev.saeta.milf.recipes.SingleInputSingleOutputRecipe;
import dev.saeta.milf.registries.MILFRecipeSerializers;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;

public record KilnSmeltingRecipe(
        SizedIngredient input,
        ItemStack output
) implements SingleInputSingleOutputRecipe {

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MILFRecipeSerializers.KILN_SMELTING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.KILN_SMELTING;
    }

    public static List<KilnSmeltingRecipe> getAllRecipes(RecipeManager recipeManager, HolderLookup.Provider registries){
        List<KilnSmeltingRecipe> recipes = new ArrayList<>(recipeManager.getAllRecipesFor(MILFRecipeTypes.KILN_SMELTING).stream().map(holder -> holder.value()).toList());
        recipes.addAll(getFurnaceRecipes(recipeManager, registries));
        return recipes;
    }

    public static List<KilnSmeltingRecipe> getFurnaceRecipes(RecipeManager recipeManager, HolderLookup.Provider registries){
        List<KilnSmeltingRecipe> recipes = new ArrayList<>();

        outer:
        for (RecipeHolder<SmeltingRecipe> holder : recipeManager.getAllRecipesFor(RecipeType.SMELTING)) {

            SmeltingRecipe recipe = holder.value();

            for (var ingredient : recipe.getIngredients()) {
                for (var stack : ingredient.getItems()) {

                    Block block = Block.byItem(stack.getItem());

                    BlockState state = block.defaultBlockState();

                    if (state.isAir()) continue outer;

                    if (block instanceof Fallable) continue outer;

                    if (!state.canOcclude()) continue outer;
                }
            }

            ItemStack output = recipe.getResultItem(registries);

            Block block = Block.byItem(output.getItem());

            BlockState state = block.defaultBlockState();

            if (state.isAir()) continue outer;

            if (block instanceof Fallable) continue outer;

            if (!state.canOcclude()) continue outer;

            recipes.add(new KilnSmeltingRecipe(
                    new SizedIngredient(recipe.getIngredients().getFirst(), 1),
                    output
            ));
        }

        return recipes;
    }

    public static class Serializer extends SingleInputSingleOutputRecipe.Serializer<KilnSmeltingRecipe>{
        public Serializer(){
            super(KilnSmeltingRecipe::new);
        }
    }

}
