package dev.saeta.milf.util;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.stream.Stream;

public class RecipeUtil {

    public static <I extends RecipeInput, T extends Recipe<I>> Stream<RecipeHolder<T>> getAllRecipes(Level level, RecipeType<T> recipeType){
        if(level == null) return Stream.empty();
        return level.getRecipeManager().getAllRecipesFor(recipeType).stream();
    }

    public static <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getValidRecipe(Level level, RecipeType<T> recipeType, I recipeInput){
        if (level == null) return Optional.empty();

        return getAllRecipes(level, recipeType).filter(holder -> holder.value().matches(recipeInput, level)).findFirst();
    }

}
