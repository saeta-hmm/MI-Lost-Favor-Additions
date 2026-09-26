package dev.saeta.milf.recipes.clay_crucible;

import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

public interface CrucibleRecipe {
    SizedIngredient input1();
    SizedIngredient input2();
    FluidStack output();
    int time();
}
