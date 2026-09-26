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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public record ClayCrucibleKilnRecipe(
        SizedIngredient input1,
        SizedIngredient input2,
        Optional<SizedIngredient> firePitFuel,
        FluidStack output,
        Optional<ItemStack> firePitOutput,
        int time,
        boolean requiresBellows
) implements Recipe<ClayCrucibleKilnRecipeInput>, CrucibleRecipe {

    @Override
    public boolean matches(ClayCrucibleKilnRecipeInput input, Level level) {
        return input1.test(input.input1()) && input2.test(input.input2());
    }

    @Override
    public ItemStack assemble(ClayCrucibleKilnRecipeInput input, HolderLookup.Provider registries) {
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
        return MILFRecipeSerializers.CLAY_CRUCIBLE_KILN_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.CLAY_CRUCIBLE_KILN_TYPE;
    }

    public static class Serializer implements RecipeSerializer<ClayCrucibleKilnRecipe>{

        private final static MapCodec<ClayCrucibleKilnRecipe> CODEC = RecordCodecBuilder.mapCodec(clayCrucibleKilnRecipeInstance -> clayCrucibleKilnRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.fieldOf("input1").forGetter(ClayCrucibleKilnRecipe::input1),
                SizedIngredient.FLAT_CODEC.fieldOf("input2").forGetter(ClayCrucibleKilnRecipe::input2),
                SizedIngredient.FLAT_CODEC.optionalFieldOf("fire_pit_fuel").forGetter(ClayCrucibleKilnRecipe::firePitFuel),
                FluidStack.CODEC.fieldOf("output").forGetter(ClayCrucibleKilnRecipe::output),
                ItemStack.CODEC.optionalFieldOf("fire_pit_output").forGetter(ClayCrucibleKilnRecipe::firePitOutput),
                Codec.INT.optionalFieldOf("time", 109).forGetter(ClayCrucibleKilnRecipe::time),
                Codec.BOOL.optionalFieldOf("requires_bellows", false).forGetter(ClayCrucibleKilnRecipe::requiresBellows)
        ).apply(clayCrucibleKilnRecipeInstance, ClayCrucibleKilnRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, ClayCrucibleKilnRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    SizedIngredient.STREAM_CODEC.encode(buf, recipe.input1());
                    SizedIngredient.STREAM_CODEC.encode(buf, recipe.input2());
                    ByteBufCodecs.optional(SizedIngredient.STREAM_CODEC).encode(buf, recipe.firePitFuel());
                    FluidStack.STREAM_CODEC.encode(buf, recipe.output());
                    ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(buf, recipe.firePitOutput());
                    ByteBufCodecs.VAR_INT.encode(buf, recipe.time());
                    ByteBufCodecs.BOOL.encode(buf, recipe.requiresBellows());
                },
                buf -> new ClayCrucibleKilnRecipe(
                        SizedIngredient.STREAM_CODEC.decode(buf),
                        SizedIngredient.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.optional(SizedIngredient.STREAM_CODEC).decode(buf),
                        FluidStack.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(buf),
                        ByteBufCodecs.VAR_INT.decode(buf),
                        ByteBufCodecs.BOOL.decode(buf)
                )
        );

        @Override
        public MapCodec<ClayCrucibleKilnRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ClayCrucibleKilnRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
