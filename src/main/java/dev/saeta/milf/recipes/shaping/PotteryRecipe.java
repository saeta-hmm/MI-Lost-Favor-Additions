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

public record PotteryRecipe(
        SizedIngredient input,
        ItemStack output,
        int topHits,
        int sideHits
) implements Recipe<ShapingRecipeInput> {

    @Override
    public boolean matches(ShapingRecipeInput input, Level level) {
        return input().test(input.input()) && topHits() == input.topHits() && sideHits() == input.sideHits();
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
        return MILFRecipeSerializers.POTTERY_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.POTTERY;
    }

    public static class Serializer implements RecipeSerializer<PotteryRecipe>{


        private final static MapCodec<PotteryRecipe> CODEC = RecordCodecBuilder.mapCodec(potteryRecipeInstance -> potteryRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(PotteryRecipe::input),
                ItemStack.CODEC.fieldOf("output").forGetter(PotteryRecipe::output),
                Codec.INT.fieldOf("top_hits").forGetter(PotteryRecipe::topHits),
                Codec.INT.fieldOf("side_hits").forGetter(PotteryRecipe::sideHits)
        ).apply(potteryRecipeInstance, PotteryRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, PotteryRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    SizedIngredient.STREAM_CODEC.encode(buf, recipe.input);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                    ByteBufCodecs.INT.encode(buf, recipe.topHits);
                    ByteBufCodecs.INT.encode(buf, recipe.sideHits);
                },
                buf -> new PotteryRecipe(
                        SizedIngredient.STREAM_CODEC.decode(buf),
                        ItemStack.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.INT.decode(buf),
                        ByteBufCodecs.INT.decode(buf)
                )
        );

        @Override
        public MapCodec<PotteryRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PotteryRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
