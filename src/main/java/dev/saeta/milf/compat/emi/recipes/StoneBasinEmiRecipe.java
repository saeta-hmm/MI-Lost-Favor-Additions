package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.*;
import dev.saeta.milf.networking.data.StoredItem;
import dev.saeta.milf.recipes.basin.StoneBasinRecipe;
import dev.saeta.milf.recipes.casting.CastingRecipe;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.registries.MILFItemTags;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StoneBasinEmiRecipe implements EmiRecipe {

    public final List<Ingredient>  ingredients;
    public final ItemStack output;

    public final SizedFluidIngredient fluid;
    public final ResourceLocation id;
    public final int stirs;

    public StoneBasinEmiRecipe(RecipeHolder<StoneBasinRecipe> holder){
        StoneBasinRecipe stoneBasinRecipe = holder.value();

        this.ingredients = stoneBasinRecipe.inputs();
        this.output = stoneBasinRecipe.output();
        this.fluid = stoneBasinRecipe.fluid();
        this.stirs = stoneBasinRecipe.stirs();

        this.id = holder.id();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.STONE_BASIN;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }



    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> inputs = new ArrayList<>();
        FluidStack[] fluids = fluid.getFluids();
        inputs.add(EmiIngredient.of(Arrays.stream(fluids).map( fluidStack -> EmiStack.of(fluidStack.getFluid()).setAmount(fluidStack.getAmount())).toList()));
        inputs.addAll(new ArrayList<>(ingredients.stream().map(EmiIngredient::of).toList()));

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
        return 100;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        widgets.add(new MILFEmiTextureWidget(EmiTexture.STONE_BASIN, 0, 0));
        widgets.add(new MILFEmiTextureWidget(EmiTexture.STONE_BASIN_EMI_ARROW, 0, 50).tooltip(((something, noIdea) -> Collections.singletonList(
                ClientTooltipComponent.create(
                        Component.translatable("emi.category.milf.stone_basin.stirs_tooltip", stirs)
                                .getVisualOrderText()
                )
        ))));

        widgets.addTank(getInputs().get(0), 20, 36, 56, 14, (int) getInputs().get(0).getAmount()).drawBack(false);

        widgets.add(new MILFEmiSlotWidget(getInputs().get(1), 9,13, EmiSlot.STONE));
        widgets.add(new MILFEmiSlotWidget(getInputs().get(2), 29,15, EmiSlot.STONE));
        widgets.add(new MILFEmiSlotWidget(getInputs().get(3), 49,15, EmiSlot.STONE));
        widgets.add(new MILFEmiSlotWidget(getInputs().get(4), 69,13, EmiSlot.STONE));

        widgets.add(new MILFEmiSlotWidget(getOutputs().get(0), 39,80, EmiSlot.STONE).recipeContext(this));

        //widgets.add(new MILFEmiArrowWidget(EmiProgressArrow.WOOD, 45, 29, stirs));

    }

}
