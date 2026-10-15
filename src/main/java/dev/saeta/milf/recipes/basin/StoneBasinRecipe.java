package dev.saeta.milf.recipes.basin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.saeta.milf.registries.MILFRecipeSerializers;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public record StoneBasinRecipe(
        List<Ingredient> inputs,
        SizedFluidIngredient fluid,
        ItemStack output,
        int stirs
) implements Recipe<StoneBasinRecipeInput> {

    @Override
    public boolean matches(StoneBasinRecipeInput recipeInput, Level level) {
        if (recipeInput.size() < inputs.size()) return false;

        for (int i = 0; i < inputs.size(); i++) {
            if (!inputs.get(i).test(recipeInput.getItem(i))) {
                return false;
            }
        }

        return fluid.test(recipeInput.fluid());
    }

    @Override
    public ItemStack assemble(StoneBasinRecipeInput input, HolderLookup.Provider registries) {
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
        return MILFRecipeSerializers.STONE_BASIN;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.STONE_BASIN;
    }

    public static class Serializer implements RecipeSerializer<StoneBasinRecipe> {

        private final static MapCodec<StoneBasinRecipe> CODEC = RecordCodecBuilder.mapCodec(castingRecipeInstance -> castingRecipeInstance.group(
                Ingredient.LIST_CODEC.fieldOf("inputs").forGetter(StoneBasinRecipe::inputs),
                SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(StoneBasinRecipe::fluid),
                ItemStack.CODEC.fieldOf("output").forGetter(StoneBasinRecipe::output),
                Codec.INT.optionalFieldOf("stirs", 4).forGetter(StoneBasinRecipe::stirs)
        ).apply(castingRecipeInstance, StoneBasinRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, StoneBasinRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(4)), StoneBasinRecipe::inputs,
                SizedFluidIngredient.STREAM_CODEC, StoneBasinRecipe::fluid,
                ItemStack.STREAM_CODEC, StoneBasinRecipe::output,
                ByteBufCodecs.VAR_INT, StoneBasinRecipe::stirs,
                StoneBasinRecipe::new
        );

        @Override
        public MapCodec<StoneBasinRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StoneBasinRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
