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
import dev.saeta.milf.compat.emi.widgets.SingleTextureProgressWidget;
import dev.saeta.milf.recipes.bloomery.BloomeryRecipe;
import dev.saeta.milf.registries.MILFBlocks;
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

public class BloomeryEmiRecipe implements EmiRecipe {

    public final SizedIngredient input1;
    public final SizedIngredient input2;

    public final ItemStack output;

    public final int time;
    public final boolean requiresBellows;
    public final ResourceLocation id;

    public BloomeryEmiRecipe(RecipeHolder<BloomeryRecipe> holder) {
        BloomeryRecipe bloomeryRecipe = holder.value();
        this.input1 = bloomeryRecipe.input1();
        this.input2 = bloomeryRecipe.input2();

        this.output = bloomeryRecipe.output();

        this.time = bloomeryRecipe.time();

        this.requiresBellows = bloomeryRecipe.requiresBellows();

        this.id = holder.id();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.BLOOMERY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        List<EmiIngredient> catalysts = new ArrayList<>();
        if(requiresBellows){
            catalysts.add(EmiStack.of(MILFBlocks.POT_BELLOWS.get().asItem()));
        }
        return catalysts;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(EmiIngredient.of(input1.ingredient()).setAmount(input1.count()));
        inputs.add(EmiIngredient.of(input2.ingredient()).setAmount(input2.count()));
        inputs.add(EmiIngredient.of(MILFItemTags.BLOOMERY_COALS).setAmount(4));
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
        return 144;
    }

    @Override
    public int getDisplayHeight() {
        return 106;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        //left

        widgets.addTexture(MILostFavor.locate("textures/gui/bloomery_emi.png"), 0,0,64,96,0,0, 64, 96, 64, 96);

        widgets.add(new MILFEmiSlotWidget(getInputs().get(1), 13,67, EmiSlot.KILN));
        widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 33,67, EmiSlot.KILN));
        widgets.add(new MILFEmiSlotWidget(getInputs().get(2), 23,48, EmiSlot.KILN));

        //right

        widgets.addTexture(MILostFavor.locate("textures/gui/bloomery_emi.png"), 80,0,64,96,0,0, 64, 96, 64, 96);

        widgets.add(new MILFEmiSlotWidget(getOutputs().get(0), 80 + 23, 67, EmiSlot.KILN).recipeContext(this));

        //center

        widgets.add(new SingleTextureProgressWidget(
                        MILostFavor.locate("textures/gui/kiln_emi_progress_arrow.png"),
                        61, 22, 22, 22, () -> {
                    long totalMs = (long) time * 50;
                    if (totalMs <= 0) return 1f;

                    return (System.currentTimeMillis() % totalMs) / (float) totalMs;
                }
                ).tooltip(((something, noIdea) -> Collections.singletonList(
                        ClientTooltipComponent.create(
                                Component.translatable("emi.category.milf.clay_crucible.seconds_tooltip", time /20)
                                        .getVisualOrderText()
                        )
                )))
        );

        if(requiresBellows){

            widgets.addTexture(
                    MILostFavor.locate("textures/gui/kiln_bellows_pipe_emi.png"),
                    59,35,26,32,0,0, 26, 32, 26, 32
            ).tooltip((something, noIdea) -> Collections.singletonList(
                    ClientTooltipComponent.create(
                            Component.translatable("emi.category.milf.clay_crucible_kiln.requires_bellows", time /20).getVisualOrderText()
                    )
            ));

            widgets.add(new MILFEmiSlotWidget(EmiStack.of(MILFBlocks.POT_BELLOWS.asItem()), 63,67, EmiSlot.BELLOWS).recipeContext(this));

        }
    }
}
