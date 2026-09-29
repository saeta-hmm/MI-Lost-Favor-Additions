package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.SingleTextureProgressWidget;
import dev.saeta.milf.recipes.clay_crucible.ClayCrucibleKilnRecipe;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.client.gui.ClientTooltipComponentManager;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClayCrucibleKilnEmiRecipe implements EmiRecipe {

    public final SizedIngredient input1;
    public final SizedIngredient input2;
    public final Optional<SizedIngredient> firePitFuel;

    public final FluidStack output;
    public final Optional<ItemStack> firePitOutput;

    public final int time;
    public final boolean requiresBellows;
    public final ResourceLocation id;

    private final List<ClientTooltipComponent> firePitTooltip = new ItemStack(MILFBlocks.FIRE_PIT.get().asItem())
            .getTooltipLines(
                    Item.TooltipContext.EMPTY,
                    null,
                    TooltipFlag.NORMAL
            )
            .stream()
            .map(Component::getVisualOrderText)
            .map(ClientTooltipComponent::create)
            .toList();


    public ClayCrucibleKilnEmiRecipe(RecipeHolder<ClayCrucibleKilnRecipe> holder) {
        ClayCrucibleKilnRecipe clayCrucibleKilnRecipe = holder.value();
        this.input1 = clayCrucibleKilnRecipe.input1();
        this.input2 = clayCrucibleKilnRecipe.input2();
        this.firePitFuel = clayCrucibleKilnRecipe.firePitFuel();

        this.output = clayCrucibleKilnRecipe.output();
        this.firePitOutput = clayCrucibleKilnRecipe.firePitOutput();

        this.time = clayCrucibleKilnRecipe.time();

        this.requiresBellows = clayCrucibleKilnRecipe.requiresBellows();

        this.id = holder.id();
    }


    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.CLAY_CRUCIBLE_KILN;
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
        firePitFuel.ifPresent(firePitFuel -> inputs.add(EmiIngredient.of(firePitFuel.ingredient()).setAmount(firePitFuel.count())));
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        List<EmiStack> outputs = new ArrayList<>();
        outputs.add(EmiStack.of(output.getFluid()).setAmount(output.getAmount()));
        firePitOutput.ifPresent(firePitOutput -> outputs.add(EmiStack.of(firePitOutput)));
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 144;
    }

    @Override
    public int getDisplayHeight() {
        return 90;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        //left

        widgets.addTexture(MILostFavor.locate("textures/gui/clay_crucible_kiln_emi.png"), 0,0,64,80,0,0, 64, 80, 64, 80);

        widgets.addTexture(
                MILostFavor.locate("textures/gui/fire_pit_emi.png"),
                0,67,64,16,0,0, 64, 16, 64, 16
        ).tooltip(firePitTooltip);

        widgets.addSlot(getInputs().get(1), 23,3).customBackground(MILostFavor.locate("textures/gui/clay_crucible_emi_slot.png"), 0,0, 18, 18);
        widgets.addSlot(getInputs().get(0), 23,21).customBackground(MILostFavor.locate("textures/gui/clay_crucible_emi_slot.png"), 0,0,18, 18);

        if(firePitFuel.isPresent()){
            widgets.addSlot(getInputs().get(2), 23,48).customBackground(MILostFavor.locate("textures/gui/clay_crucible_emi_slot.png"), 0,36,18, 18);
        }

        //right

        widgets.addTexture(MILostFavor.locate("textures/gui/clay_crucible_kiln_emi.png"), 80,0,64,80,0,0, 64, 80, 64, 80);
        widgets.addTexture(
                MILostFavor.locate("textures/gui/fire_pit_emi.png"),
                80,67,64,16,0,0, 64, 16, 64, 16
        ).tooltip(firePitTooltip);

        widgets.addTank(getOutputs().get(0), 80 + 22, 3, 20, 37, 1000).recipeContext(this).drawBack(false);

        if(firePitOutput.isPresent()){
            widgets.addSlot(getOutputs().get(1), 103,48)
                    .customBackground(MILostFavor.locate("textures/gui/clay_crucible_emi_slot.png"), 0,36,18, 18)
                    .recipeContext(this);
        }

        //center

        widgets.add(new SingleTextureProgressWidget(
                MILostFavor.locate("textures/gui/clay_crucible_emi_progress_arrow.png"),
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


            widgets.addSlot(EmiStack.of(MILFBlocks.POT_BELLOWS.asItem()), 63,67)
                    .customBackground(MILostFavor.locate("textures/gui/clay_crucible_emi_slot.png"), 18,0,18, 18);
        }
    }
}
