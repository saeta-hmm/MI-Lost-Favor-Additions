package dev.saeta.milf.recipes.fire_pit;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record FirePitCookingRecipe(
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
        return MILFRecipeSerializers.FIRE_PIT_COOKING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.FIRE_PIT_COOKING;
    }

    public static List<FirePitCookingRecipe> getAllRecipes(RecipeManager recipeManager, HolderLookup.Provider registries){
        List<FirePitCookingRecipe> recipes = new ArrayList<>(recipeManager.getAllRecipesFor(MILFRecipeTypes.FIRE_PIT_COOKING).stream().map(holder -> holder.value()).toList());
        recipes.addAll(getCampfireRecipes(recipeManager, registries));
        return recipes;
    }

    public static List<FirePitCookingRecipe> getCampfireRecipes(RecipeManager recipeManager, HolderLookup.Provider registries){
        return recipeManager.getAllRecipesFor(RecipeType.CAMPFIRE_COOKING)
                .stream()
                .map(holder -> holder.value())
                .map(campfireCookingRecipe -> new FirePitCookingRecipe(
                        new SizedIngredient(campfireCookingRecipe.getIngredients().getFirst(), 1),
                        Optional.of(new SizedIngredient(Ingredient.of(ItemTags.LOGS), 1)),
                        campfireCookingRecipe.getResultItem(registries),
                        Optional.of(new ItemStack(Items.CHARCOAL))
                )).toList();
    }

    public static class Serializer implements RecipeSerializer<FirePitCookingRecipe>{

        private final static MapCodec<FirePitCookingRecipe> CODEC = RecordCodecBuilder.mapCodec(firePitCookingRecipeInstance -> firePitCookingRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(FirePitCookingRecipe::input),
                SizedIngredient.FLAT_CODEC.optionalFieldOf("fire_pit_fuel").forGetter(FirePitCookingRecipe::firePitFuel),
                ItemStack.CODEC.fieldOf("output").forGetter(FirePitCookingRecipe::output),
                ItemStack.CODEC.optionalFieldOf("fire_pit_output").forGetter(FirePitCookingRecipe::firePitOutput)
        ).apply(firePitCookingRecipeInstance, FirePitCookingRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, FirePitCookingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    SizedIngredient.STREAM_CODEC.encode(buf, recipe.input());
                    ByteBufCodecs.optional(SizedIngredient.STREAM_CODEC).encode(buf, recipe.firePitFuel());
                    ItemStack.STREAM_CODEC.encode(buf, recipe.output());
                    ByteBufCodecs.optional(ItemStack.STREAM_CODEC).encode(buf, recipe.firePitOutput());
                },
                buf -> new FirePitCookingRecipe(
                        SizedIngredient.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.optional(SizedIngredient.STREAM_CODEC).decode(buf),
                        ItemStack.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.optional(ItemStack.STREAM_CODEC).decode(buf)
                )
        );

        @Override
        public MapCodec<FirePitCookingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FirePitCookingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
