package dev.saeta.milf.recipes.bloomery;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.saeta.milf.recipes.clay_crucible.ClayCrucibleKilnRecipe;
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

public record BloomeryRecipe(
        SizedIngredient input1,
        SizedIngredient input2,
        ItemStack output,
        int time,
        boolean requiresBellows
) implements Recipe<BloomeryRecipeInput> {

    @Override
    public boolean matches(BloomeryRecipeInput input, Level level) {
        return input1.test(input.input1()) && input2.test(input.input2());
    }

    @Override
    public ItemStack assemble(BloomeryRecipeInput input, HolderLookup.Provider registries) {
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
        return MILFRecipeSerializers.BLOOMERY_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.BLOOMERY;
    }

    public static class Serializer implements RecipeSerializer<BloomeryRecipe>{

        private final static MapCodec<BloomeryRecipe> CODEC = RecordCodecBuilder.mapCodec(bloomeryRecipeInstance -> bloomeryRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.fieldOf("input1").forGetter(BloomeryRecipe::input1),
                SizedIngredient.FLAT_CODEC.fieldOf("input2").forGetter(BloomeryRecipe::input2),
                ItemStack.CODEC.fieldOf("output").forGetter(BloomeryRecipe::output),
                Codec.INT.optionalFieldOf("time", 109).forGetter(BloomeryRecipe::time),
                Codec.BOOL.optionalFieldOf("requires_bellows", false).forGetter(BloomeryRecipe::requiresBellows)
        ).apply(bloomeryRecipeInstance, BloomeryRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, BloomeryRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    SizedIngredient.STREAM_CODEC.encode(buf, recipe.input1());
                    SizedIngredient.STREAM_CODEC.encode(buf, recipe.input2());
                    ItemStack.STREAM_CODEC.encode(buf, recipe.output());
                    ByteBufCodecs.VAR_INT.encode(buf, recipe.time());
                    ByteBufCodecs.BOOL.encode(buf, recipe.requiresBellows());
                },
                buf -> new BloomeryRecipe(
                        SizedIngredient.STREAM_CODEC.decode(buf),
                        SizedIngredient.STREAM_CODEC.decode(buf),
                        ItemStack.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.VAR_INT.decode(buf),
                        ByteBufCodecs.BOOL.decode(buf)
                )
        );

        @Override
        public MapCodec<BloomeryRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BloomeryRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
