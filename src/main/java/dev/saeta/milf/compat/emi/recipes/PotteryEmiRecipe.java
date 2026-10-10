package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.*;
import dev.saeta.milf.recipes.shaping.PotteryRecipe;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PotteryEmiRecipe implements EmiRecipe {

    public final SizedIngredient input;
    public final ItemStack output;
    public final ResourceLocation id;
    public final int topHits;
    public final int sideHits;

    public PotteryEmiRecipe(RecipeHolder<PotteryRecipe> holder){
        PotteryRecipe potteryRecipe = holder.value();

        this.input = potteryRecipe.input();
        this.output = potteryRecipe.output();
        this.topHits = potteryRecipe.topHits();
        this.sideHits = potteryRecipe.sideHits();

        this.id = holder.id();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.POTTERY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(EmiIngredient.of(input.ingredient()).setAmount(input.count()));
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        List<EmiStack> outputs = new ArrayList<>();
        outputs.add(EmiStack.of(output));
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 96;
    }

    @Override
    public int getDisplayHeight() {
        return 64;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        widgets.add(new MILFEmiTextureWidget(EmiTexture.POTTERY, 0, 0));

        widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 27,27, EmiSlot.CLAY).customSize(36,36).drawBack(false));

        widgets.add(new MILFEmiSlotWidget(getOutputs().get(0), 67,5, EmiSlot.CLAY).drawBack(false).recipeContext(this));

        widgets.add(new NumberTextWidget(45.5f,19.5f, 0xa0a7b8, () -> topHits).centered()).tooltip(((something, noIdea) -> Collections.singletonList(
                ClientTooltipComponent.create(
                        Component.translatable("emi.category.milf.chisel.top_hits", String.valueOf(topHits)).getVisualOrderText()
                )
        )));

        widgets.add(new NumberTextWidget(22.5f,42.5f, 0xa0a7b8, () -> sideHits).centered()).tooltip(((something, noIdea) -> Collections.singletonList(
                ClientTooltipComponent.create(
                        Component.translatable("emi.category.milf.chisel.side_hits", String.valueOf(sideHits)).getVisualOrderText()
                )
        )));


    }

}
