package dev.saeta.milf.recipes.kiln;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.saeta.milf.recipes.SingleInputSingleOutputRecipe;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.recipes.shaping.ShapingRecipeInput;
import dev.saeta.milf.registries.MILFRecipeSerializers;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;

public record KilnSmeltingRecipe(
        SizedIngredient input,
        ItemStack output,
        boolean requiresBellows
) implements Recipe<SingleRecipeInput> {

    public static List<KilnSmeltingRecipe> getAllRecipes(RecipeManager recipeManager, HolderLookup.Provider registries){
        List<KilnSmeltingRecipe> recipes = new ArrayList<>(recipeManager.getAllRecipesFor(MILFRecipeTypes.KILN_SMELTING).stream().map(holder -> holder.value()).toList());
        recipes.addAll(getFurnaceRecipes(recipeManager, registries));
        return recipes;
    }

    public static List<KilnSmeltingRecipe> getFurnaceRecipes(RecipeManager recipeManager, HolderLookup.Provider registries){
        List<KilnSmeltingRecipe> recipes = new ArrayList<>();

        outer:
        for (RecipeHolder<SmeltingRecipe> holder : recipeManager.getAllRecipesFor(RecipeType.SMELTING)) {

            SmeltingRecipe recipe = holder.value();

            for (var ingredient : recipe.getIngredients()) {
                for (var stack : ingredient.getItems()) {

                    Block block = Block.byItem(stack.getItem());

                    BlockState state = block.defaultBlockState();

                    if (state.isAir()) continue outer;

                    if (block instanceof Fallable) continue outer;

                    if (!state.canOcclude()) continue outer;
                }
            }

            ItemStack output = recipe.getResultItem(registries);

            Block block = Block.byItem(output.getItem());

            BlockState state = block.defaultBlockState();

            if (state.isAir()) continue outer;

            if (block instanceof Fallable) continue outer;

            if (!state.canOcclude()) continue outer;

            recipes.add(new KilnSmeltingRecipe(
                    new SizedIngredient(recipe.getIngredients().getFirst(), 1),
                    output,
                    false
            ));
        }

        return recipes;
    }

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
        return MILFRecipeSerializers.KILN_SMELTING;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.KILN_SMELTING;
    }

    public static class Serializer implements RecipeSerializer<KilnSmeltingRecipe>{
        
        private final static MapCodec<KilnSmeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(KilnSmeltingRecipeInstance -> KilnSmeltingRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(KilnSmeltingRecipe::input),
                ItemStack.CODEC.fieldOf("output").forGetter(KilnSmeltingRecipe::output),
                Codec.BOOL.optionalFieldOf("requires_bellows", false).forGetter(KilnSmeltingRecipe::requiresBellows)
        ).apply(KilnSmeltingRecipeInstance, KilnSmeltingRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, KilnSmeltingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    SizedIngredient.STREAM_CODEC.encode(buf, recipe.input);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                    ByteBufCodecs.BOOL.encode(buf, recipe.requiresBellows);
                },
                buf -> new KilnSmeltingRecipe(
                        SizedIngredient.STREAM_CODEC.decode(buf),
                        ItemStack.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.BOOL.decode(buf)
                )
        );

        @Override
        public MapCodec<KilnSmeltingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, KilnSmeltingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }



}
