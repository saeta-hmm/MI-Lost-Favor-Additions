package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.recipes.anvil.AnvilRecipe;
import dev.saeta.milf.recipes.bloomery.BloomeryRecipe;
import dev.saeta.milf.recipes.clay_crucible.ClayCrucibleKilnRecipe;
import dev.saeta.milf.recipes.clay_crucible.ClayCrucibleRecipe;
import dev.saeta.milf.recipes.fire_pit.FirePitCookingRecipe;
import dev.saeta.milf.recipes.fire_pit.PitFiringRecipe;
import dev.saeta.milf.recipes.kiln.KilnSmeltingRecipe;
import dev.saeta.milf.recipes.molding.ImpressionMoldingRecipe;
import dev.saeta.milf.recipes.shaping.ChiselRecipe;
import dev.saeta.milf.recipes.shaping.PotteryRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MILFRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MILostFavor.MOD_ID);

    public static final RecipeType<ClayCrucibleRecipe> CLAY_CRUCIBLE = register("clay_crucible");
    public static final RecipeType<ClayCrucibleKilnRecipe> CLAY_CRUCIBLE_KILN = register("clay_crucible_kiln");

    public static final RecipeType<FirePitCookingRecipe> FIRE_PIT_COOKING = register("fire_pit_cooking");

    public static final RecipeType<KilnSmeltingRecipe> KILN_SMELTING = register("kiln_smelting");

    public static final RecipeType<AnvilRecipe> ANVIL = register("anvil");

    public static final RecipeType<BloomeryRecipe> BLOOMERY = register("bloomery");

    public static final RecipeType<ChiselRecipe> CHISEL = register("chisel");

    public static final RecipeType<PotteryRecipe> POTTERY = register("pottery");

    public static final RecipeType<ImpressionMoldingRecipe> IMPRESSION_MOLDING = register("impression_molding");

    public static final RecipeType<PitFiringRecipe> PIT_FIRING = register("pit_firing");


    private static <T extends Recipe<?>> RecipeType<T> register(String id) {
        RecipeType<T> type = new RecipeType<>() {
            @Override
            public String toString() {
                return "milf:" + id;
            }
        };
        RECIPE_TYPES.register(id, () -> type);
        return type;
    }

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
    }
}
