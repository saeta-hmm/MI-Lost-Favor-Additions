package dev.saeta.milf.recipes.fire_pit;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record FirePitSingleRecipeInput(
        ItemStack input,
        ItemStack firePitFuel
) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if(index == 0) return input;
        return firePitFuel;
    }

    @Override
    public int size() {
        return 2;
    }
}
