package dev.saeta.milf.recipes.basin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record StoneBasinRecipeInput(
        List<ItemStack> inputs,
        FluidStack fluid
)implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return inputs.get(index);
    }

    @Override
    public int size() {
        return inputs.size();
    }

    @Override
    public boolean isEmpty() {
        return inputs.isEmpty() && fluid.isEmpty();
    }
}
