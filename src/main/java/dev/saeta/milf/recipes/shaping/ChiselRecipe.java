package dev.saeta.milf.recipes.shaping;

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

public record ChiselRecipe(
        SizedIngredient input,
        ItemStack output,
        int topHits,
        int sideHits
) implements Recipe<ShapingRecipeInput> {

    @Override
    public boolean matches(ShapingRecipeInput input, Level level) {
        return input().test(input.input()) && input.topHits() == topHits && input.sideHits() == sideHits;
    }

    @Override
    public ItemStack assemble(ShapingRecipeInput input, HolderLookup.Provider registries) {
        return output().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MILFRecipeSerializers.CHISEL_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.CHISEL;
    }

    public static class Serializer implements RecipeSerializer<ChiselRecipe>{


        private final static MapCodec<ChiselRecipe> CODEC = RecordCodecBuilder.mapCodec(chiselRecipeInstance -> chiselRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(ChiselRecipe::input),
                ItemStack.CODEC.fieldOf("output").forGetter(ChiselRecipe::output),
                Codec.INT.fieldOf("top_hits").forGetter(ChiselRecipe::topHits),
                Codec.INT.fieldOf("side_hits").forGetter(ChiselRecipe::sideHits)
                ).apply(chiselRecipeInstance, ChiselRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, ChiselRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    SizedIngredient.STREAM_CODEC.encode(buf, recipe.input);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                    ByteBufCodecs.INT.encode(buf, recipe.topHits);
                    ByteBufCodecs.INT.encode(buf, recipe.sideHits);
                },
                buf -> new ChiselRecipe(
                        SizedIngredient.STREAM_CODEC.decode(buf),
                        ItemStack.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.INT.decode(buf),
                        ByteBufCodecs.INT.decode(buf)
                )
        );

        @Override
        public MapCodec<ChiselRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ChiselRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}