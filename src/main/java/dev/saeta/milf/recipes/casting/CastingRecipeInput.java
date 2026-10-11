package dev.saeta.milf.recipes.casting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public record CastingRecipeInput(ItemStack mold, FluidStack fluid) implements RecipeInput {


    @Override
    public ItemStack getItem(int index) {
        return mold;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return mold.isEmpty() && fluid.isEmpty();
    }
}
