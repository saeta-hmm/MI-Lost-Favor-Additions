package dev.saeta.milf.recipes.bronze_anvil;

import dev.saeta.milf.recipes.SingleInputSingleOutputRecipe;
import dev.saeta.milf.recipes.kiln.KilnSmeltingRecipe;
import dev.saeta.milf.registries.MILFRecipeSerializers;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public record BronzeAnvilRecipe(
        SizedIngredient input,
        ItemStack output
) implements SingleInputSingleOutputRecipe {
    @Override
    public RecipeSerializer<?> getSerializer() {
        return MILFRecipeSerializers.BRONZE_ANVIL_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.BRONZE_ANVIL;
    }

    public static class Serializer extends SingleInputSingleOutputRecipe.Serializer<BronzeAnvilRecipe>{
        public Serializer(){
            super(BronzeAnvilRecipe::new);
        }
    }
}
