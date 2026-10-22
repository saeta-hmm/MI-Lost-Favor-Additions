package dev.saeta.milf.recipes.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.saeta.milf.recipes.SingleInputSingleOutputRecipe;
import dev.saeta.milf.recipes.SingleRecipeInput;
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

public record ChiselHitRecipe(
        SizedIngredient input,
        ItemStack output,
        ItemStack outputBlock
) implements Recipe<SingleRecipeInput> {

    public boolean matches(SingleRecipeInput input, Level level) {
        return input().test(input.input());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
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
        return MILFRecipeSerializers.CHISEL_HIT;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.CHISEL_HIT;
    }

    public static class Serializer implements RecipeSerializer<ChiselHitRecipe>{

        private final static MapCodec<ChiselHitRecipe> CODEC = RecordCodecBuilder.mapCodec(ChiselHitRecipeInstance -> ChiselHitRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(ChiselHitRecipe::input),
                ItemStack.CODEC.fieldOf("output").forGetter(ChiselHitRecipe::output),
                ItemStack.CODEC.optionalFieldOf("output_block", ItemStack.EMPTY).forGetter(ChiselHitRecipe::outputBlock)
        ).apply(ChiselHitRecipeInstance, ChiselHitRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, ChiselHitRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    SizedIngredient.STREAM_CODEC.encode(buf, recipe.input);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, recipe.outputBlock);
                },
                buf -> new ChiselHitRecipe(
                        SizedIngredient.STREAM_CODEC.decode(buf),
                        ItemStack.STREAM_CODEC.decode(buf),
                        ItemStack.OPTIONAL_STREAM_CODEC.decode(buf)
                )
        );

        @Override
        public MapCodec<ChiselHitRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ChiselHitRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
