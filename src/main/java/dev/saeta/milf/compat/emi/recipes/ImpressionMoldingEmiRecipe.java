package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.EmiSlot;
import dev.saeta.milf.compat.emi.widgets.EmiTexture;
import dev.saeta.milf.compat.emi.widgets.MILFEmiSlotWidget;
import dev.saeta.milf.compat.emi.widgets.MILFEmiTextureWidget;
import dev.saeta.milf.recipes.molding.ImpressionMoldingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ImpressionMoldingEmiRecipe implements EmiRecipe {

    public final SizedIngredient input;
    public final SizedIngredient block;

    public final ItemStack output;
    public final ResourceLocation id;


    public ImpressionMoldingEmiRecipe(RecipeHolder<ImpressionMoldingRecipe> holder){
        ImpressionMoldingRecipe impressionMoldingRecipe = holder.value();

        this.input = impressionMoldingRecipe.input1();
        this.block = impressionMoldingRecipe.input2();

        this.output = impressionMoldingRecipe.output();

        this.id = holder.id();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.IMPRESSION_MOLDING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(EmiIngredient.of(input.ingredient()).setAmount(input.count()));
        inputs.add(EmiIngredient.of(block.ingredient()).setAmount(input.count()));

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

        widgets.add(new MILFEmiTextureWidget(EmiTexture.IMPRESSION_MOLDING, 0, 0));

        widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 23,0, EmiSlot.CLAY).catalyst(true));

        widgets.add(new MILFEmiSlotWidget(getInputs().get(1), 23,23, EmiSlot.CLAY));

        widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 2,44, EmiSlot.CLAY).recipeContext(this).catalyst(true));
        widgets.add(new MILFEmiSlotWidget(getOutputs().get(0), 44,44, EmiSlot.CLAY).recipeContext(this));


    }

}
