package dev.saeta.milf.recipes.world;

import dev.saeta.milf.recipes.SingleInputSingleOutputRecipe;
import dev.saeta.milf.recipes.fire_pit.PitFiringRecipe;
import dev.saeta.milf.registries.MILFRecipeSerializers;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public record ChiselHitRecipe(
        SizedIngredient input,
        ItemStack output
) implements SingleInputSingleOutputRecipe {

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MILFRecipeSerializers.CHISEL_HIT;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.CHISEL_HIT;
    }

    public static class Serializer extends SingleInputSingleOutputRecipe.Serializer<ChiselHitRecipe>{
        public Serializer(){
            super(ChiselHitRecipe::new);
        }
    }

}
