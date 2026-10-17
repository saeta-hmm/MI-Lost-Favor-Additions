package dev.saeta.milf.recipes.world;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.saeta.milf.recipes.MultiRecipeInput;
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

import java.util.List;

public record WorldPlacementRecipe(
        List<Ingredient> inputs,
        ItemStack output
) implements Recipe<MultiRecipeInput> {

    @Override
    public boolean matches(MultiRecipeInput recipeInput, Level level) {
        if (recipeInput.size() < inputs.size()) return false;

        for (int i = 0; i < inputs.size(); i++) {
            if (!inputs.get(i).test(recipeInput.getItem(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(MultiRecipeInput input, HolderLookup.Provider registries) {
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
        return MILFRecipeSerializers.WORLD_PLACEMENT;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.WORLD_PLACEMENT;
    }

    public static class Serializer implements RecipeSerializer<WorldPlacementRecipe> {

        private final static MapCodec<WorldPlacementRecipe> CODEC = RecordCodecBuilder.mapCodec(castingRecipeInstance -> castingRecipeInstance.group(
                Ingredient.LIST_CODEC.fieldOf("inputs").forGetter(WorldPlacementRecipe::inputs),
                ItemStack.CODEC.fieldOf("output").forGetter(WorldPlacementRecipe::output)
        ).apply(castingRecipeInstance, WorldPlacementRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, WorldPlacementRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(4)), WorldPlacementRecipe::inputs,
                ItemStack.STREAM_CODEC, WorldPlacementRecipe::output,
                WorldPlacementRecipe::new
        );

        @Override
        public MapCodec<WorldPlacementRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WorldPlacementRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}