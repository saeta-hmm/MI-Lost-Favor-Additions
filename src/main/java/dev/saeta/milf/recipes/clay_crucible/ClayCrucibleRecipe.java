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

public class ClayCrucibleRecipe implements Recipe<ClayCrucibleRecipeInput> {

    private final SizedIngredient input;
    private final SizedIngredient fuel;
    private final FluidStack output;
    private final int time;

    public ClayCrucibleRecipe(SizedIngredient input, SizedIngredient fuel, FluidStack output, int time) {
        this.input = input;
        this.fuel = fuel;
        this.output = output;
        this.time = time;
    }

    public SizedIngredient getInput() {
        return input;
    }

    public FluidStack getOutput() {
        return output;
    }

    public SizedIngredient getFuel() {
        return fuel;
    }

    public int getTime() {
        return time;
    }

    @Override
    public boolean matches(ClayCrucibleRecipeInput recipeInput, Level level) {
        return input.test(recipeInput.input()) && fuel.test(recipeInput.fuel());
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

    public static class Serializer implements RecipeSerializer<ClayCrucibleRecipe>{

        private final static MapCodec<ClayCrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(clayCrucibleRecipeInstance -> clayCrucibleRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(ClayCrucibleRecipe::getInput),
                SizedIngredient.FLAT_CODEC.fieldOf("fuel").forGetter(ClayCrucibleRecipe::getFuel),
                FluidStack.CODEC.fieldOf("output").forGetter(ClayCrucibleRecipe::getOutput),
                Codec.INT.optionalFieldOf("time", 109).forGetter(ClayCrucibleRecipe::getTime)
        ).apply(clayCrucibleRecipeInstance, ClayCrucibleRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, ClayCrucibleRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC, ClayCrucibleRecipe::getInput,
                SizedIngredient.STREAM_CODEC, ClayCrucibleRecipe::getFuel,
                FluidStack.STREAM_CODEC, ClayCrucibleRecipe::getOutput,
                ByteBufCodecs.VAR_INT, ClayCrucibleRecipe::getTime,
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
