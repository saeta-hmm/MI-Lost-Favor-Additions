package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.EmiSlot;
import dev.saeta.milf.compat.emi.widgets.MILFEmiSlotWidget;
import dev.saeta.milf.compat.emi.widgets.NumberTextWidget;
import dev.saeta.milf.recipes.anvil.AnvilRecipe;
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

public class StoneAnvilEmiRecipe implements EmiRecipe {

    public final SizedIngredient input;
    public final ItemStack output;
    public final ResourceLocation id;
    public final float maxHit;

    public StoneAnvilEmiRecipe(RecipeHolder<AnvilRecipe> holder){
        AnvilRecipe stoneAnvilRecipe = holder.value();

        this.input = stoneAnvilRecipe.input();
        this.output = stoneAnvilRecipe.output();
        this.maxHit = stoneAnvilRecipe.maxHit();

        this.id = holder.id();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.STONE_ANVIL;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        List<EmiIngredient> catalysts = new ArrayList<>();

        catalysts.add(EmiIngredient.of(MILFItemTags.HAMMERS));

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
        return 64;
    }

    @Override
    public int getDisplayHeight() {
        return 64;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        widgets.addTexture(MILostFavor.locate("textures/gui/stone_anvil_emi.png"), 0,0,64,64,0,0, 64, 64, 64, 64);

        widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 7,25, EmiSlot.STONE));

        widgets.add(new MILFEmiSlotWidget(getCatalysts().get(0), 23,2, EmiSlot.STONE));

        widgets.add(new MILFEmiSlotWidget(getOutputs().get(0), 39,25, EmiSlot.STONE).recipeContext(this));


        widgets.add(new NumberTextWidget(26,34, 0x48393d, () -> maxHit).onlyPercentages()).tooltip(((something, noIdea) -> Collections.singletonList(
                ClientTooltipComponent.create(
                        Component.translatable("emi.category.milf.anvil.max_hit", String.valueOf(maxHit).substring(String.valueOf(maxHit).indexOf('.') + 1)).getVisualOrderText()
                )
        )));

    }

}
