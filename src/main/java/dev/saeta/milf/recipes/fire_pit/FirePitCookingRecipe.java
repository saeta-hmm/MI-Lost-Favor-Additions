package dev.saeta.milf.recipes.fire_pit;

import dev.saeta.milf.recipes.SingleInputSingleOutputRecipe;
import dev.saeta.milf.registries.MILFRecipeSerializers;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;

public record FirePitCookingRecipe(
        SizedIngredient input,
        ItemStack output
) implements SingleInputSingleOutputRecipe {

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MILFRecipeSerializers.FIRE_PIT_COOKING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.FIRE_PIT_COOKING;
    }

    public static List<FirePitCookingRecipe> getAllRecipes(RecipeManager recipeManager, HolderLookup.Provider registries){
        List<FirePitCookingRecipe> recipes = new ArrayList<>(recipeManager.getAllRecipesFor(MILFRecipeTypes.FIRE_PIT_COOKING).stream().map(holder -> holder.value()).toList());
        recipes.addAll(getCampfireRecipes(recipeManager, registries));
        return recipes;
    }

    public static List<FirePitCookingRecipe> getCampfireRecipes(RecipeManager recipeManager, HolderLookup.Provider registries){
        return recipeManager.getAllRecipesFor(RecipeType.CAMPFIRE_COOKING)
                .stream()
                .map(holder -> holder.value())
                .map(campfireCookingRecipe -> new FirePitCookingRecipe(
                        new SizedIngredient(campfireCookingRecipe.getIngredients().getFirst(), 1),
                        campfireCookingRecipe.getResultItem(registries)
                )).toList();
    }

    public static class Serializer extends SingleInputSingleOutputRecipe.Serializer<FirePitCookingRecipe>{
        public Serializer(){
            super(FirePitCookingRecipe::new);
        }
    }

}
