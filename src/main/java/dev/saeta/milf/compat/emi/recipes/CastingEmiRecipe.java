package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.*;
import dev.saeta.milf.networking.data.StoredItem;
import dev.saeta.milf.recipes.casting.CastingRecipe;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.registries.MILFItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CastingEmiRecipe implements EmiRecipe {

    public final Ingredient mold;
    public final ItemStack output;
    public final ItemStack molfRemains;

    public final FluidIngredient fluid;
    public final ResourceLocation id;
    public final int time;

    public CastingEmiRecipe(RecipeHolder<CastingRecipe> holder){
        CastingRecipe castingRecipe = holder.value();

        this.mold = castingRecipe.mold();
        this.output = castingRecipe.output();
        this.fluid = castingRecipe.fluid();
        this.time = castingRecipe.time();
        this.molfRemains =castingRecipe.moldRemains();

        this.id = holder.id();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.CASTING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        List<EmiIngredient> catalysts = new ArrayList<>();

        catalysts.add(EmiIngredient.of(MILFItemTags.HAMMERS));

        ItemStack filledMold = new ItemStack(mold.getItems()[0].getItem());
        filledMold.set(MILFDataComponents.CAST_RESULT, new StoredItem(output.copy()) );

        catalysts.add(EmiStack.of(filledMold));

        return catalysts;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(EmiIngredient.of(mold));
        FluidStack[] fluids = fluid.getStacks();
        inputs.add(EmiIngredient.of(Arrays.stream(fluids).map( fluidStack -> EmiStack.of(fluidStack.getFluid()).setAmount(fluidStack.getAmount())).toList()));

        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        List<EmiStack> outputs = new ArrayList<>();
        outputs.add(EmiStack.of(output));
        outputs.add(EmiStack.of(molfRemains));
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 112;
    }

    @Override
    public int getDisplayHeight() {
        return 80;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        widgets.add(new MILFEmiTextureWidget(EmiTexture.CASTING, 0, 0));

        widgets.addTank(getInputs().get(1), 19, 6, 10, 14, (int) getInputs().get(0).getAmount()).drawBack(false);
        widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 15,31, EmiSlot.CRUCIBLE));

        widgets.add(new MILFEmiSlotWidget(getCatalysts().get(0), 79,4, EmiSlot.CRUCIBLE));

        widgets.add(new MILFEmiSlotWidget(getCatalysts().get(1), 79,31, EmiSlot.CRUCIBLE));

        widgets.add(new MILFEmiSlotWidget(getOutputs().get(0), 90,58, EmiSlot.CRUCIBLE).recipeContext(this));
        widgets.add(new MILFEmiSlotWidget(getOutputs().get(1), 68,58, EmiSlot.CRUCIBLE).recipeContext(this));

        widgets.add(new MILFEmiArrowWidget(EmiProgressArrow.CRUCIBLE, 45, 29, time));

    }

}