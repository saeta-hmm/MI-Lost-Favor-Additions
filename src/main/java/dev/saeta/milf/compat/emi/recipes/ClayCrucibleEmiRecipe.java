package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.recipes.clay_crucible.ClayCrucibleRecipe;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClayCrucibleEmiRecipe implements EmiRecipe {

    public final SizedIngredient input;
    public final SizedIngredient fuel;
    public final FluidStack output;
    public final int time;
    public final ResourceLocation id;

    public ClayCrucibleEmiRecipe(RecipeHolder<ClayCrucibleRecipe> holder) {
        ClayCrucibleRecipe clayCrucibleRecipe = holder.value();
        this.input = clayCrucibleRecipe.getInput();
        this.fuel = clayCrucibleRecipe.getFuel();
        this.output = clayCrucibleRecipe.getOutput();
        this.time = clayCrucibleRecipe.getTime();
        this.id = holder.id();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.CLAY_CRUCIBLE;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(EmiIngredient.of(input.ingredient()).setAmount(input.count()));
        inputs.add(EmiIngredient.of(fuel.ingredient()).setAmount(fuel.count()));
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(output.getFluid()).setAmount(output.getAmount()));
    }

    @Override
    public int getDisplayWidth() {
        return 140;
    }

    @Override
    public int getDisplayHeight() {
        return 64;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        widgets.addTexture(MILostFavor.locate("textures/gui/clay_crucible_emi.png"), 0,0,64,64,0,0, 64, 64, 64, 64);

//        widgets.addSlot(getInputs().get(0), 13,60);
//        widgets.addSlot(getInputs().get(1), 33,60);

//        widgets.addSlot(getInputs().get(0), 14,14).backgroundTexture(MILostFavor.locate("textures/gui/clay_crucible_emi_slot.png"), 0,0);
//        widgets.addSlot(getInputs().get(1), 32,14).backgroundTexture(MILostFavor.locate("textures/gui/clay_crucible_emi_slot.png"), 0,0);
//
//        widgets.addSlot(14, 32).backgroundTexture(MILostFavor.locate("textures/gui/clay_crucible_emi_slot.png"), 0,0);
//        widgets.addSlot(32, 32).backgroundTexture(MILostFavor.locate("textures/gui/clay_crucible_emi_slot.png"), 0,0);



        widgets.addSlot(getInputs().get(1), 14,14).customBackground(MILostFavor.locate("textures/gui/clay_crucible_emi_slot.png"), 0,18, 36, 18);
        widgets.addSlot(getInputs().get(0), 14,32).customBackground(MILostFavor.locate("textures/gui/clay_crucible_emi_slot.png"), 0,18,36, 18);





        widgets.addTexture(MILostFavor.locate("textures/gui/clay_crucible_emi.png"), 76,0,64,64,0,0, 64, 64, 64, 64);

        widgets.addTank(getOutputs().get(0), 76 + 14, 14, 36, 36, 1000).recipeContext(this).drawBack(false);

        widgets.addFillingArrow(58, 24, time /20 * 1000)
                .tooltip(((something, noIdea) -> Collections.singletonList(
                ClientTooltipComponent.create(
                        Component.translatable("emi.category.milf.clay_crucible.seconds_tooltip", time /20)
                                .getVisualOrderText()
                )
        )));

        ItemStack firestarter = new ItemStack(MILFItems.FIRESTARTER.get());
        firestarter.setDamageValue(1);
        widgets.addSlot(EmiStack.of(MILFItems.FIRESTARTER).setRemainder(EmiStack.of(firestarter)), 61,6).drawBack(false).catalyst(true);

    }
}
