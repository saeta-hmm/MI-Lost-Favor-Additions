package dev.saeta.milf.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.function.BiFunction;

public interface SingleInputSingleOutputRecipe extends Recipe<SingleRecipeInput> {

    SizedIngredient input();
    ItemStack output();

    default boolean matches(SingleRecipeInput input, Level level) {
        return input().test(input.input());
    }

    default ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return output().copy();
    }

    default boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    default ItemStack getResultItem(HolderLookup.Provider registries) {
        return output();
    }

    class Serializer<T extends SingleInputSingleOutputRecipe> implements RecipeSerializer<T>{

        private final MapCodec<T> CODEC;
        private final StreamCodec<RegistryFriendlyByteBuf, T> STREAM_CODEC;

        protected Serializer(BiFunction<SizedIngredient, ItemStack, T> factory) {
            this.CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(SingleInputSingleOutputRecipe::input),
                    ItemStack.CODEC.fieldOf("output").forGetter(SingleInputSingleOutputRecipe::output)
            ).apply(instance, factory));

            this.STREAM_CODEC = StreamCodec.of(
                    (buf, recipe) -> {
                        SizedIngredient.STREAM_CODEC.encode(buf, recipe.input());
                        ItemStack.STREAM_CODEC.encode(buf, recipe.output());
                    },
                    buf -> factory.apply(
                            SizedIngredient.STREAM_CODEC.decode(buf),
                            ItemStack.STREAM_CODEC.decode(buf)
                    )
            );
        }


        @Override
        public MapCodec<T> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
