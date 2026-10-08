package dev.saeta.milf.recipes.molding;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.saeta.milf.recipes.DoubleRecipeInput;
import dev.saeta.milf.registries.MILFRecipeSerializers;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public record ImpressionMoldingRecipe(
        SizedIngredient input1,
        SizedIngredient input2,
        ItemStack output
) implements Recipe<DoubleRecipeInput> {

    @Override
    public boolean matches(DoubleRecipeInput recipeInput, Level level) {
        return input1.test(recipeInput.input1()) && input2.test(recipeInput.input2());
    }

    @Override
    public ItemStack assemble(DoubleRecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MILFRecipeSerializers.IMPRESSION_MOLDING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.IMPRESSION_MOLDING;
    }

    public static class Serializer implements RecipeSerializer<ImpressionMoldingRecipe> {

        private final static MapCodec<ImpressionMoldingRecipe> CODEC = RecordCodecBuilder.mapCodec(impressionMoldingRecipeInstance -> impressionMoldingRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(ImpressionMoldingRecipe::input1),
                SizedIngredient.FLAT_CODEC.fieldOf("block").forGetter(ImpressionMoldingRecipe::input2),
                ItemStack.CODEC.fieldOf("output").forGetter(ImpressionMoldingRecipe::output)
        ).apply(impressionMoldingRecipeInstance, ImpressionMoldingRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, ImpressionMoldingRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC, ImpressionMoldingRecipe::input1,
                SizedIngredient.STREAM_CODEC, ImpressionMoldingRecipe::input2,
                ItemStack.STREAM_CODEC, ImpressionMoldingRecipe::output,
                ImpressionMoldingRecipe::new
        );

        @Override
        public MapCodec<ImpressionMoldingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ImpressionMoldingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
