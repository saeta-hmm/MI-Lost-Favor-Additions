package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.EmiSlot;
import dev.saeta.milf.compat.emi.widgets.EmiSlotPointer;
import dev.saeta.milf.compat.emi.widgets.MILFEmiSlotWidget;
import dev.saeta.milf.compat.emi.widgets.SingleTextureProgressWidget;
import dev.saeta.milf.recipes.clay_crucible.ClayCrucibleKilnRecipe;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

    public final FluidStack output;

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

        this.output = clayCrucibleKilnRecipe.output();

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
        catalysts.add(EmiStack.of(MILFBlocks.CLAY_PLATE.get()));

        ItemStack firestarter = new ItemStack(MILFItems.FIRESTARTER.get());
        firestarter.setDamageValue(1);
        catalysts.add(EmiStack.of(MILFItems.FIRESTARTER).setRemainder(EmiStack.of(firestarter)));

        return catalysts;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(EmiIngredient.of(input1.ingredient()).setAmount(input1.count()));
        inputs.add(EmiIngredient.of(input2.ingredient()).setAmount(input2.count()));
        inputs.add(EmiIngredient.of(ItemTags.LOGS).setAmount(1));
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        List<EmiStack> outputs = new ArrayList<>();
        outputs.add(EmiStack.of(output.getFluid()).setAmount(output.getAmount()));
        outputs.add(EmiStack.of(Items.CHARCOAL));
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 144;
    }

    @Override
    public int getDisplayHeight() {
        return 90 + 20;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        //left

        int clayPlateIndex = requiresBellows ? 1 : 0;

        widgets.addTexture(MILostFavor.locate("textures/gui/clay_crucible_kiln_emi.png"), 0,0,64,80,0,0, 64, 80, 64, 80);
        //widgets.addTexture(MILostFavor.locate("textures/gui/clay_crucible_kiln_top_emi.png"), 0,0,80,32,0,0, 80, 32, 80, 32);

        widgets.addTexture(
                MILostFavor.locate("textures/gui/fire_pit_emi.png"),
                0,67,64,18,0,0, 64, 18, 64, 18
        ).tooltip(firePitTooltip);

        widgets.add(new MILFEmiSlotWidget(getInputs().get(1), 23,3, EmiSlot.CRUCIBLE));
        widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 23,21, EmiSlot.CRUCIBLE));
        widgets.add(new MILFEmiSlotWidget(getInputs().get(2), 23,67, EmiSlot.FIRE_PIT));

        //right

        widgets.addTexture(MILostFavor.locate("textures/gui/clay_crucible_kiln_emi.png"), 80,0,64,80,0,0, 64, 80, 64, 80);
        widgets.addTexture(
                MILostFavor.locate("textures/gui/fire_pit_emi.png"),
                80,67,64,18,0,0, 64, 18, 64, 18
        ).tooltip(firePitTooltip);

        widgets.addTank(getOutputs().get(0), 80 + 22, 3, 20, 37, 1000).recipeContext(this).drawBack(false);

        widgets.add(new MILFEmiSlotWidget(getOutputs().get(1), 103,67, EmiSlot.FIRE_PIT).recipeContext(this));


        //center

        widgets.add(new MILFEmiSlotWidget(getCatalysts().get(clayPlateIndex), 53,6, EmiSlot.CRUCIBLE).withPointer(
                new EmiSlotPointer(EmiSlotPointer.Type.CRUCIBLE, EmiSlotPointer.Corner.TOP_LEFT).large()
            ).catalyst(true)
        );

        widgets.add(new MILFEmiSlotWidget(getCatalysts().get(clayPlateIndex+1), 53,89, EmiSlot.WOOD).withPointer(
                new EmiSlotPointer(EmiSlotPointer.Type.WOOD, EmiSlotPointer.Corner.TOP_LEFT)
        ));


//        widgets.addSlot(getCatalysts().get(clayPlateIndex), 53,6)
//                .catalyst(true)
//                .drawBack(false);

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

            widgets.add(new MILFEmiSlotWidget(EmiStack.of(MILFBlocks.POT_BELLOWS.asItem()), 63,67, EmiSlot.BELLOWS).recipeContext(this));

        }
    }
}
