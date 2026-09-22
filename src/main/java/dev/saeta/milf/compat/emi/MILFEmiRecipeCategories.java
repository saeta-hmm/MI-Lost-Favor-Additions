package dev.saeta.milf.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.resources.ResourceLocation;

public class MILFEmiRecipeCategories {

    public static final EmiRecipeCategory CLAY_CRUCIBLE = new MILFCategory(MILostFavor.locate("clay_crucible"), EmiStack.of(MILFBlocks.CLAY_CRUCIBLE));

    private static class MILFCategory extends EmiRecipeCategory {

        public MILFCategory(ResourceLocation id, EmiRenderable icon) {
            super(id, icon);
        }
    }

}



