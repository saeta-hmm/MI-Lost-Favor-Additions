package dev.saeta.milf.recipes.fire_pit;

import dev.saeta.milf.recipes.SingleInputSingleOutputRecipe;
import dev.saeta.milf.registries.MILFRecipeSerializers;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public record PitFiringRecipe(
        SizedIngredient input,
        ItemStack output
) implements SingleInputSingleOutputRecipe {

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MILFRecipeSerializers.PIT_FIRING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.PIT_FIRING;
    }

    public static class Serializer extends SingleInputSingleOutputRecipe.Serializer<PitFiringRecipe>{
        public Serializer(){
            super(PitFiringRecipe::new);
        }
    }

}
