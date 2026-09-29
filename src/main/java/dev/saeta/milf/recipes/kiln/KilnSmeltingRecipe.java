package dev.saeta.milf.recipes.kiln;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.saeta.milf.recipes.fire_pit.FirePitCookingRecipe;
import dev.saeta.milf.recipes.fire_pit.FirePitSingleRecipeInput;
import dev.saeta.milf.registries.MILFRecipeSerializers;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record KilnSmeltingRecipe(
        SizedIngredient input,
        Optional<SizedIngredient> firePitFuel,
        ItemStack output,
        Optional<ItemStack> firePitOutput
) implements Recipe<FirePitSingleRecipeInput> {
    @Override
    public boolean matches(FirePitSingleRecipeInput input, Level level) {
        return this.input.test(input.input());
    }

    @Override
    public ItemStack assemble(FirePitSingleRecipeInput input, HolderLookup.Provider registries) {
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
        return MILFRecipeSerializers.KILN_SMELTING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.KILN_SMELTING;
    }

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
                    Optional.of(new SizedIngredient(Ingredient.of(ItemTags.LOGS), 1)),
                    output,
                    Optional.of(new ItemStack(Items.CHARCOAL))
            ));
        }

        return recipes;
    }

    public static class Serializer implements RecipeSerializer<KilnSmeltingRecipe>{

        private final static MapCodec<KilnSmeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(kilnSmeltingRecipeInstance -> kilnSmeltingRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(KilnSmeltingRecipe::input),
                SizedIngredient.FLAT_CODEC.optionalFieldOf("fire_pit_fuel").forGetter(KilnSmeltingRecipe::firePitFuel),
                ItemStack.CODEC.fieldOf("output").forGetter(KilnSmeltingRecipe::output),
                ItemStack.CODEC.optionalFieldOf("fire_pit_output").forGetter(KilnSmeltingRecipe::firePitOutput)
        ).apply(kilnSmeltingRecipeInstance, KilnSmeltingRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, KilnSmeltingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    SizedIngredient.STREAM_CODEC.encode(buf, recipe.input());
                    ByteBufCodecs.optional(SizedIngredient.STREAM_CODEC).encode(buf, recipe.firePitFuel());
                    ItemStack.STREAM_CODEC.encode(buf, recipe.output());
                    ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(buf, recipe.firePitOutput());
                },
                buf -> new KilnSmeltingRecipe(
                        SizedIngredient.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.optional(SizedIngredient.STREAM_CODEC).decode(buf),
                        ItemStack.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(buf)
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
