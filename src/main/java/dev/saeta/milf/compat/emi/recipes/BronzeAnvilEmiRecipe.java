package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.SingleTextureProgressWidget;
import dev.saeta.milf.recipes.bronze_anvil.BronzeAnvilRecipe;
import dev.saeta.milf.recipes.kiln.KilnSmeltingRecipe;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItemTags;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BronzeAnvilEmiRecipe implements EmiRecipe {

    public final SizedIngredient input;
    public final ItemStack output;
    public final ResourceLocation id;

    public BronzeAnvilEmiRecipe(RecipeHolder<BronzeAnvilRecipe> holder){
        BronzeAnvilRecipe bronzeAnvilRecipe = holder.value();

        this.input = bronzeAnvilRecipe.input();
        this.output = bronzeAnvilRecipe.output();

        this.id = holder.id();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.BRONZE_ANVIL;
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

        widgets.addTexture(MILostFavor.locate("textures/gui/bronze_anvil_emi.png"), 0,0,64,64,0,0, 64, 64, 64, 64);

        widgets.addSlot(getInputs().get(0), 7,25).customBackground(MILostFavor.locate("textures/gui/emi_slots.png"), 36,0,18, 18);

        widgets.addSlot(getCatalysts().get(0), 23,2)
                .catalyst(true)
                .customBackground(MILostFavor.locate("textures/gui/emi_slots.png"), 36,0,18, 18);

        widgets.addSlot(getOutputs().get(0), 39,25)
                .customBackground(MILostFavor.locate("textures/gui/emi_slots.png"), 36,0,18, 18)
                .recipeContext(this);


    }

}
