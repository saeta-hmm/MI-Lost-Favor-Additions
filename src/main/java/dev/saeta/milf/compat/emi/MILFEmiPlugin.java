package dev.saeta.milf.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.saeta.milf.compat.emi.recipes.ClayCrucibleEmiRecipe;
import dev.saeta.milf.recipes.clay_crucible.ClayCrucibleRecipe;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Function;

@EmiEntrypoint
public class MILFEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registerCategories(registry);
        registerRecipes(registry);
    }

    private void registerCategories(EmiRegistry registry){
        registry.addCategory(MILFEmiRecipeCategories.CLAY_CRUCIBLE);

        registry.addWorkstation(MILFEmiRecipeCategories.CLAY_CRUCIBLE, EmiStack.of(MILFBlocks.CLAY_CRUCIBLE));
    }

    private void registerRecipes(EmiRegistry registry){
        addAll(registry, MILFRecipeTypes.CLAY_CRUCIBLE_TYPE, ClayCrucibleEmiRecipe::new);
    }

    public <C extends RecipeInput, T extends Recipe<C>> void addAll(EmiRegistry registry, RecipeType<T> type, Function<RecipeHolder<T>, EmiRecipe> constructor) {
        for (RecipeHolder<T> entry : registry.getRecipeManager().getAllRecipesFor(type)) {
            registry.addRecipe(constructor.apply(entry));
        }
    }
}
