package dev.saeta.milf.recipes.clay_crucible;

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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

public record ClayCrucibleRecipe(
        SizedIngredient input1,
        SizedIngredient input2,
        FluidStack output,
        int time
) implements Recipe<ClayCrucibleRecipeInput> , CrucibleRecipe {

    @Override
    public boolean matches(ClayCrucibleRecipeInput recipeInput, Level level) {
        return input1.test(recipeInput.input()) && input2.test(recipeInput.fuel());
    }

    @Override
    public ItemStack assemble(ClayCrucibleRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MILFRecipeSerializers.CLAY_CRUCIBLE_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.CLAY_CRUCIBLE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<ClayCrucibleRecipe> {

        private final static MapCodec<ClayCrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(clayCrucibleRecipeInstance -> clayCrucibleRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(ClayCrucibleRecipe::input1),
                SizedIngredient.FLAT_CODEC.fieldOf("fuel").forGetter(ClayCrucibleRecipe::input2),
                FluidStack.CODEC.fieldOf("output").forGetter(ClayCrucibleRecipe::output),
                Codec.INT.optionalFieldOf("time", 109).forGetter(ClayCrucibleRecipe::time)
        ).apply(clayCrucibleRecipeInstance, ClayCrucibleRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, ClayCrucibleRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC, ClayCrucibleRecipe::input1,
                SizedIngredient.STREAM_CODEC, ClayCrucibleRecipe::input2,
                FluidStack.STREAM_CODEC, ClayCrucibleRecipe::output,
                ByteBufCodecs.VAR_INT, ClayCrucibleRecipe::time,
                ClayCrucibleRecipe::new
        );

        @Override
        public MapCodec<ClayCrucibleRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ClayCrucibleRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
