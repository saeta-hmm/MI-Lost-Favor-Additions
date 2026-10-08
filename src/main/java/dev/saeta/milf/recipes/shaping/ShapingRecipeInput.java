package dev.saeta.milf.recipes.shaping;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ShapingRecipeInput(
        ItemStack input,
        int topHits,
        int sideHits
) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return input;
    }

    @Override
    public int size() {
        return 1;
    }
}
