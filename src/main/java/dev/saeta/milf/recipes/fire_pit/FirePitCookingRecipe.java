package dev.saeta.milf.recipes.fire_pit;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.saeta.milf.recipes.SingleInputSingleOutputRecipe;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.recipes.kiln.KilnSmeltingRecipe;
import dev.saeta.milf.registries.MILFRecipeSerializers;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
