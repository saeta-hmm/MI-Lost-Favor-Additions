package dev.saeta.milf.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.resources.ResourceLocation;

public class MILFEmiRecipeCategories {

    public static final EmiRecipeCategory CLAY_CRUCIBLE = new MILFCategory(MILostFavor.locate("clay_crucible"), EmiStack.of(MILFBlocks.CLAY_CRUCIBLE));
    public static final EmiRecipeCategory CLAY_CRUCIBLE_KILN = new MILFCategory(MILostFavor.locate("clay_crucible_kiln"), EmiStack.of(MILFBlocks.KILN));

    public static final EmiRecipeCategory FIRE_PIT_COOKING = new MILFCategory(MILostFavor.locate("fire_pit_cooking"), EmiStack.of(MILFBlocks.FIRE_PIT));

    public static final EmiRecipeCategory KILN_SMELTING = new MILFCategory(MILostFavor.locate("kiln_smelting"), EmiStack.of(MILFBlocks.KILN));
    public static final EmiRecipeCategory BRONZE_ANVIL = new MILFCategory(MILostFavor.locate("bronze_anvil"), EmiStack.of(MILFBlocks.BRONZE_ANVIL));
    public static final EmiRecipeCategory BLOOMERY = new MILFCategory(MILostFavor.locate("bloomery"), EmiStack.of(MILFBlocks.BLOOMERY_BASE));


    private static class MILFCategory extends EmiRecipeCategory {

        public MILFCategory(ResourceLocation id, EmiRenderable icon) {
            super(id, icon);
        }


    }

}



