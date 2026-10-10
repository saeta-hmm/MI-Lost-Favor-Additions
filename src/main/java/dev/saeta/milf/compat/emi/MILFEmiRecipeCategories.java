package dev.saeta.milf.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.resources.ResourceLocation;

public class MILFEmiRecipeCategories {

    public static final EmiRecipeCategory CLAY_CRUCIBLE = new MILFCategory(MILostFavor.locate("clay_crucible"), EmiStack.of(MILFBlocks.CLAY_CRUCIBLE));
    public static final EmiRecipeCategory CLAY_CRUCIBLE_KILN = new MILFCategory(MILostFavor.locate("clay_crucible_kiln"), EmiStack.of(MILFBlocks.KILN));

    public static final EmiRecipeCategory FIRE_PIT_COOKING = new MILFCategory(MILostFavor.locate("fire_pit_cooking"), EmiStack.of(MILFBlocks.ROASTING_CONTRAPTION));

    public static final EmiRecipeCategory KILN_SMELTING = new MILFCategory(MILostFavor.locate("kiln_smelting"), EmiStack.of(MILFBlocks.KILN));
    public static final EmiRecipeCategory BRONZE_ANVIL = new MILFCategory(MILostFavor.locate("bronze_anvil"), EmiStack.of(MILFBlocks.BRONZE_ANVIL));
    public static final EmiRecipeCategory STONE_ANVIL = new MILFCategory(MILostFavor.locate("stone_anvil"), EmiStack.of(MILFBlocks.STONE_ANVIL));

    public static final EmiRecipeCategory BLOOMERY = new MILFCategory(MILostFavor.locate("bloomery"), EmiStack.of(MILFBlocks.BLOOMERY_BASE));
    public static final EmiRecipeCategory CHISEL = new MILFCategory(MILostFavor.locate("chisel"), EmiStack.of(MILFItems.FLINT_CHISEL));
    public static final EmiRecipeCategory POTTERY = new MILFCategory(MILostFavor.locate("pottery"), EmiStack.of(MILFBlocks.UNFIRED_CLAY_CRUCIBLE));
    public static final EmiRecipeCategory IMPRESSION_MOLDING = new MILFCategory(MILostFavor.locate("impression_molding"), EmiStack.of(MILFBlocks.UNFIRED_CLAY_PLATE));

    public static final EmiRecipeCategory PIT_FIRING = new MILFCategory(MILostFavor.locate("pit_firing"), EmiStack.of(MILFBlocks.FIRE_PIT));

    private static class MILFCategory extends EmiRecipeCategory {

        public MILFCategory(ResourceLocation id, EmiRenderable icon) {
            super(id, icon);
        }


    }

}



