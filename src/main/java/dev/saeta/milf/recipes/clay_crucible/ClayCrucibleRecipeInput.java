package dev.saeta.milf.recipes.clay_crucible;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ClayCrucibleRecipeInput(ItemStack input, ItemStack fuel) implements RecipeInput {


    @Override
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> input;
            case 1 -> fuel;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return input.isEmpty() && fuel.isEmpty();
    }
}
