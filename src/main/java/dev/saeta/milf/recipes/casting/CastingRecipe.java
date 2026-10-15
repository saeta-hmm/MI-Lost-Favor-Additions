package dev.saeta.milf.recipes.casting;

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
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public record CastingRecipe(
        Ingredient mold,
        SizedFluidIngredient fluid,
        ItemStack output,
        ItemStack moldRemains,
        int time
) implements Recipe<CastingRecipeInput> {

    @Override
    public boolean matches(CastingRecipeInput recipeInput, Level level) {
        return mold.test(recipeInput.mold()) && fluid.test(recipeInput.fluid());
    }

    @Override
    public ItemStack assemble(CastingRecipeInput input, HolderLookup.Provider registries) {
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
        return MILFRecipeSerializers.CASTING;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.CASTING;
    }

    public static class Serializer implements RecipeSerializer<CastingRecipe> {

        private final static MapCodec<CastingRecipe> CODEC = RecordCodecBuilder.mapCodec(castingRecipeInstance -> castingRecipeInstance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("mold").forGetter(CastingRecipe::mold),
                SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(CastingRecipe::fluid),
                ItemStack.CODEC.fieldOf("output").forGetter(CastingRecipe::output),
                ItemStack.CODEC.fieldOf("mold_remains").forGetter(CastingRecipe::moldRemains),
                Codec.INT.optionalFieldOf("time", 109).forGetter(CastingRecipe::time)
        ).apply(castingRecipeInstance, CastingRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, CastingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, CastingRecipe::mold,
                SizedFluidIngredient.STREAM_CODEC, CastingRecipe::fluid,
                ItemStack.STREAM_CODEC, CastingRecipe::output,
                ItemStack.STREAM_CODEC, CastingRecipe::moldRemains,
                ByteBufCodecs.VAR_INT, CastingRecipe::time,
                CastingRecipe::new
        );

        @Override
        public MapCodec<CastingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CastingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

