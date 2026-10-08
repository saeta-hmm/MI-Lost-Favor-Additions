package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.NumberTextWidget;
import dev.saeta.milf.recipes.shaping.ChiselRecipe;
import dev.saeta.milf.registries.MILFItemTags;
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

public class ChiselEmiRecipe implements EmiRecipe {

    public final SizedIngredient input;
    public final ItemStack output;
    public final ResourceLocation id;
    public final int topHits;
    public final int sideHits;

    public ChiselEmiRecipe(RecipeHolder<ChiselRecipe> holder){
        ChiselRecipe chiselRecipe = holder.value();

        this.input = chiselRecipe.input();
        this.output = chiselRecipe.output();
        this.topHits = chiselRecipe.topHits();
        this.sideHits = chiselRecipe.sideHits();

        this.id = holder.id();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.CHISEL;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        List<EmiIngredient> catalysts = new ArrayList<>();

        catalysts.add(EmiIngredient.of(MILFItemTags.HAMMERS));
        catalysts.add(EmiIngredient.of(MILFItemTags.CHISELS));

        return catalysts;
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

        widgets.addTexture(MILostFavor.locate("textures/gui/chisel_emi.png"), 0,0,96,64,0,0, 96, 64, 96, 64);

        widgets.addSlot(getInputs().get(0), 27,27)
                .customBackground(MILostFavor.locate("textures/gui/emi_slots.png"), 36,18,36, 36)
                .drawBack(false);

        widgets.addSlot(getCatalysts().get(0), 1,1)
                .catalyst(true)
                .drawBack(false);

        widgets.addSlot(getCatalysts().get(1), 8,8)
                .catalyst(true)
                .drawBack(false);

        widgets.addSlot(getOutputs().get(0), 67,5)
                .customBackground(MILostFavor.locate("textures/gui/emi_slots.png"), 36,18,18, 18)
                .recipeContext(this);


        widgets.add(new NumberTextWidget(45.5f,19.5f, 0xa5a5a5, () -> topHits).centered()).tooltip(((something, noIdea) -> Collections.singletonList(
                ClientTooltipComponent.create(
                        Component.translatable("emi.category.milf.chisel.top_hits", String.valueOf(topHits)).getVisualOrderText()
                )
        )));

        widgets.add(new NumberTextWidget(22.5f,42.5f, 0xa5a5a5, () -> sideHits).centered()).tooltip(((something, noIdea) -> Collections.singletonList(
                ClientTooltipComponent.create(
                        Component.translatable("emi.category.milf.chisel.side_hits", String.valueOf(sideHits)).getVisualOrderText()
                )
        )));


    }

}
