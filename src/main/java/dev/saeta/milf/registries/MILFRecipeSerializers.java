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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MILFRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MILostFavor.MOD_ID);

    public static final RecipeSerializer<ClayCrucibleRecipe> CLAY_CRUCIBLE_RECIPE_SERIALIZER = register("clay_crucible", new ClayCrucibleRecipe.Serializer());
    public static final RecipeSerializer<ClayCrucibleKilnRecipe> CLAY_CRUCIBLE_KILN_RECIPE_SERIALIZER = register("clay_crucible_kiln", new ClayCrucibleKilnRecipe.Serializer());

    public static final RecipeSerializer<FirePitCookingRecipe> FIRE_PIT_COOKING_RECIPE_SERIALIZER = register("fire_pit_cooking", new FirePitCookingRecipe.Serializer());

    public static final RecipeSerializer<KilnSmeltingRecipe> KILN_SMELTING_RECIPE_SERIALIZER = register("kiln_smelting", new KilnSmeltingRecipe.Serializer());


    public static final RecipeSerializer<AnvilRecipe> ANVIL_RECIPE_SERIALIZER = register("anvil", new AnvilRecipe.Serializer());

    public static final RecipeSerializer<BloomeryRecipe> BLOOMERY_RECIPE_SERIALIZER = register("bloomery", new BloomeryRecipe.Serializer());

    public static final RecipeSerializer<ChiselRecipe> CHISEL_RECIPE_SERIALIZER = register("chisel", new ChiselRecipe.Serializer());

    public static final RecipeSerializer<PotteryRecipe> POTTERY_RECIPE_SERIALIZER = register("pottery", new PotteryRecipe.Serializer());

    public static final RecipeSerializer<ImpressionMoldingRecipe> IMPRESSION_MOLDING_RECIPE_SERIALIZER = register("impression_molding", new ImpressionMoldingRecipe.Serializer());

    public static final RecipeSerializer<PitFiringRecipe> PIT_FIRING_RECIPE_SERIALIZER = register("pit_firing", new PitFiringRecipe.Serializer());


    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        RECIPE_SERIALIZERS.register(id, () -> serializer);
        return serializer;
    }

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }


}
