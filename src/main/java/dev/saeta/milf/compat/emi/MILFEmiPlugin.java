package dev.saeta.milf.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.saeta.milf.compat.emi.recipes.*;
import dev.saeta.milf.recipes.anvil.AnvilTier;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItems;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Function;
import java.util.function.Predicate;

@EmiEntrypoint
public class MILFEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        registerCategories(registry);
        registerRecipes(registry);
    }

    private void registerCategories(EmiRegistry registry){


        registry.addCategory(MILFEmiRecipeCategories.FIRE_PIT_COOKING);
        registry.addCategory(MILFEmiRecipeCategories.POTTERY);
        registry.addCategory(MILFEmiRecipeCategories.CHISEL);
        registry.addCategory(MILFEmiRecipeCategories.IMPRESSION_MOLDING);
        registry.addCategory(MILFEmiRecipeCategories.STONE_ANVIL);
        registry.addCategory(MILFEmiRecipeCategories.BRONZE_ANVIL);

        registry.addCategory(MILFEmiRecipeCategories.CLAY_CRUCIBLE);
        registry.addCategory(MILFEmiRecipeCategories.CLAY_CRUCIBLE_KILN);
        registry.addCategory(MILFEmiRecipeCategories.KILN_SMELTING);
        registry.addCategory(MILFEmiRecipeCategories.BLOOMERY);


        registry.addWorkstation(MILFEmiRecipeCategories.CLAY_CRUCIBLE, EmiStack.of(MILFBlocks.CLAY_CRUCIBLE));
        registry.addWorkstation(MILFEmiRecipeCategories.CLAY_CRUCIBLE, EmiStack.of(MILFItems.CLAY_BUCKET));

        registry.addWorkstation(MILFEmiRecipeCategories.CLAY_CRUCIBLE_KILN, EmiStack.of(MILFBlocks.KILN));
        registry.addWorkstation(MILFEmiRecipeCategories.CLAY_CRUCIBLE_KILN, EmiStack.of(MILFItems.CLAY_BUCKET));
        registry.addWorkstation(MILFEmiRecipeCategories.CLAY_CRUCIBLE_KILN, EmiStack.of(MILFBlocks.FIRE_PIT));

        registry.addWorkstation(MILFEmiRecipeCategories.FIRE_PIT_COOKING, EmiStack.of(MILFBlocks.FIRE_PIT));
        registry.addWorkstation(MILFEmiRecipeCategories.FIRE_PIT_COOKING, EmiStack.of(MILFBlocks.ROASTING_CONTRAPTION));

        registry.addWorkstation(MILFEmiRecipeCategories.KILN_SMELTING, EmiStack.of(MILFBlocks.KILN));
        registry.addWorkstation(MILFEmiRecipeCategories.KILN_SMELTING, EmiStack.of(MILFBlocks.FIRE_PIT));

        registry.addWorkstation(MILFEmiRecipeCategories.BRONZE_ANVIL, EmiStack.of(MILFBlocks.BRONZE_ANVIL));
        registry.addWorkstation(MILFEmiRecipeCategories.STONE_ANVIL, EmiStack.of(MILFBlocks.STONE_ANVIL));
        registry.addWorkstation(MILFEmiRecipeCategories.STONE_ANVIL, EmiStack.of(MILFBlocks.BRONZE_ANVIL));

        registry.addWorkstation(MILFEmiRecipeCategories.BLOOMERY, EmiStack.of(MILFBlocks.BLOOMERY_BASE));
        registry.addWorkstation(MILFEmiRecipeCategories.BLOOMERY, EmiStack.of(MILFBlocks.KILN));

    }

    private void registerRecipes(EmiRegistry registry){
        addAll(registry, MILFRecipeTypes.CLAY_CRUCIBLE, ClayCrucibleEmiRecipe::new);
        addAll(registry, MILFRecipeTypes.CLAY_CRUCIBLE_KILN, ClayCrucibleKilnEmiRecipe::new);

        addAll(registry, MILFRecipeTypes.FIRE_PIT_COOKING, FirePitCookingEmiRecipe::new);
        addAll(registry, MILFRecipeTypes.KILN_SMELTING, KilnSmeltingEmiRecipe::new);


//        addAll(registry, MILFRecipeTypes.BRONZE_ANVIL, BronzeAnvilEmiRecipe::new);
//        addAll(registry, MILFRecipeTypes.STONE_ANVIL, StoneAnvilEmiRecipe::new);

        addAll(registry, MILFRecipeTypes.BLOOMERY, BloomeryEmiRecipe::new);
        addAll(registry, MILFRecipeTypes.CHISEL, ChiselEmiRecipe::new);
        addAll(registry, MILFRecipeTypes.POTTERY, PotteryEmiRecipe::new);
        addAll(registry, MILFRecipeTypes.IMPRESSION_MOLDING, ImpressionMoldingEmiRecipe::new);

        FirePitCookingEmiRecipe.parseCampfireRecipes(registry);
        KilnSmeltingEmiRecipe.parseFurnaceRecipes(registry);

        addWithPredicate(registry, MILFRecipeTypes.ANVIL, StoneAnvilEmiRecipe::new, holder -> holder.value().tier() == AnvilTier.STONE);
        addWithPredicate(registry, MILFRecipeTypes.ANVIL, BronzeAnvilEmiRecipe::new, holder -> {

            var tier = holder.value().tier();

            return tier == AnvilTier.BRONZE;

        });

    }

    public <C extends RecipeInput, T extends Recipe<C>> void addAll(EmiRegistry registry, RecipeType<T> type, Function<RecipeHolder<T>, EmiRecipe> constructor) {
        for (RecipeHolder<T> entry : registry.getRecipeManager().getAllRecipesFor(type)) {
            registry.addRecipe(constructor.apply(entry));
        }
    }

    public <C extends RecipeInput, T extends Recipe<C>> void addWithPredicate(
            EmiRegistry registry, RecipeType<T> type, Function<RecipeHolder<T>, EmiRecipe> constructor,
            Predicate<RecipeHolder<T>> predicate) {
        for (RecipeHolder<T> entry : registry.getRecipeManager().getAllRecipesFor(type)) {
            if(predicate.test(entry)) registry.addRecipe(constructor.apply(entry));

        }
    }
}
