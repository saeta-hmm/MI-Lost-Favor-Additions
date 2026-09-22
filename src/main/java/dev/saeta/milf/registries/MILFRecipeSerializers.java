package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.recipes.clay_crucible.ClayCrucibleRecipe;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MILFRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MILostFavor.MOD_ID);

    public static final RecipeSerializer<ClayCrucibleRecipe> CLAY_CRUCIBLE_RECIPE_SERIALIZER = register("clay_crucible", new ClayCrucibleRecipe.Serializer());

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        RECIPE_SERIALIZERS.register(id, () -> serializer);
        return serializer;
    }

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }


}
