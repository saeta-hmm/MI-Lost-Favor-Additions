package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.*;
import dev.saeta.milf.recipes.fire_pit.PitFiringRecipe;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PitFiringEmiRecipe implements EmiRecipe {

    public final SizedIngredient input;

    public final ItemStack output;

    public final ResourceLocation id;

    private final int time = 200;


    public PitFiringEmiRecipe(RecipeHolder<PitFiringRecipe> holder) {
        PitFiringRecipe pitFiringRecipe = holder.value();
        this.input = pitFiringRecipe.input();

        this.output = pitFiringRecipe.output();

        this.id = holder.id();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.PIT_FIRING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(EmiIngredient.of(input.ingredient()).setAmount(input.count()));
        inputs.add(EmiIngredient.of(ItemTags.LOGS).setAmount(1));
        return inputs;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        List<EmiIngredient> catalysts = new ArrayList<>();
        ItemStack firestarter = new ItemStack(MILFItems.FIRESTARTER.get());
        firestarter.setDamageValue(1);
        catalysts.add(EmiStack.of(MILFItems.FIRESTARTER).setRemainder(EmiStack.of(firestarter)));
        return catalysts;
    }

    @Override
    public List<EmiStack> getOutputs() {
        List<EmiStack> outputs = new ArrayList<>();
        outputs.add(EmiStack.of(output));
        outputs.add(EmiStack.of(Items.CHARCOAL));
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 146;
    }

    @Override
    public int getDisplayHeight() {
        return 48;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        //left

        widgets.add(new MILFEmiTextureWidget(EmiTexture.FIRE_PIT, 0, 10)
                .addEmiStackTooltip(EmiStack.of(MILFBlocks.FIRE_PIT))
                .addEmiStackRemainderTooltip(getCatalysts().get(0))
        );

        widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 23,5, EmiSlot.CLAY));
        widgets.add(new MILFEmiSlotWidget(getInputs().get(1), 23,24, EmiSlot.FIRE_PIT));

        //right

        widgets.add(new MILFEmiTextureWidget(EmiTexture.FIRE_PIT, 82, 10)
                .addEmiStackTooltip(EmiStack.of(MILFBlocks.FIRE_PIT))
        );

        widgets.add(new MILFEmiSlotWidget(getOutputs().get(0), 105,5, EmiSlot.CRUCIBLE).recipeContext(this));
        widgets.add(new MILFEmiSlotWidget(getOutputs().get(1), 105,24, EmiSlot.FIRE_PIT).recipeContext(this));

        //center

        widgets.add(new MILFEmiArrowWidget(EmiProgressArrow.WOOD, 62, 0, time));

    }
}
