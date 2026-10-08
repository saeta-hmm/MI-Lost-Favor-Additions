package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.EmiRegistry;
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
import dev.saeta.milf.recipes.fire_pit.FirePitCookingRecipe;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirePitCookingEmiRecipe implements EmiRecipe {

    public final SizedIngredient input;

    public final ItemStack output;

    public final ResourceLocation id;

    private final int time = 200;


    public FirePitCookingEmiRecipe(RecipeHolder<FirePitCookingRecipe> holder) {
        FirePitCookingRecipe firePitCookingRecipe = holder.value();
        this.input = firePitCookingRecipe.input();

        this.output = firePitCookingRecipe.output();

        this.id = holder.id();
    }

    public FirePitCookingEmiRecipe(RecipeHolder<CampfireCookingRecipe> holder, SizedIngredient input, ItemStack output) {
        this.input = input;

        this.output = output;

        this.id = holder.id();
    }

    public static void parseCampfireRecipes(EmiRegistry registry){

        for (RecipeHolder<CampfireCookingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(RecipeType.CAMPFIRE_COOKING)) {

            CampfireCookingRecipe recipe = holder.value();

            ResourceLocation id = MILostFavor.locate("/fire_pit_cooking/campfire_parsed/" + holder.id().getNamespace() + "/" + holder.id().getPath());

            registry.addRecipe(new FirePitCookingEmiRecipe(
                    new RecipeHolder<>(id, recipe),
                    new SizedIngredient(recipe.getIngredients().getFirst(), 1),
                    recipe.getResultItem(null)
            ));
        }

    }


    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.FIRE_PIT_COOKING;
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
        catalysts.add(EmiStack.of(MILFItems.FIRESTARTER));
        catalysts.add(EmiStack.of(firestarter));
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
        return 70;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        //left

        widgets.addTexture(MILostFavor.locate("textures/gui/fire_pit_cooking_emi.png"), 0,0,64,64,0,0, 64, 64, 64, 64);

        widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 23,0, EmiSlot.WOOD));
        widgets.add(new MILFEmiSlotWidget(getInputs().get(1), 23,46, EmiSlot.FIRE_PIT));

        widgets.add(new MILFEmiSlotWidget(getCatalysts().get(0), 23,23, EmiSlot.WOOD).withPointer(
                new EmiSlotPointer(EmiSlotPointer.Type.WOOD, EmiSlotPointer.Corner.BOTTOM_LEFT, EmiSlotPointer.Corner.BOTTOM_RIGHT)
        ));


        //right

        widgets.addTexture(MILostFavor.locate("textures/gui/fire_pit_cooking_emi.png"), 82,0,64,64,0,0, 64, 64, 64, 64);

        widgets.add(new MILFEmiSlotWidget(getOutputs().get(0), 105,0, EmiSlot.WOOD).recipeContext(this));
        widgets.add(new MILFEmiSlotWidget(getOutputs().get(1), 105,46, EmiSlot.FIRE_PIT).recipeContext(this));

        widgets.add(new MILFEmiSlotWidget(getCatalysts().get(1), 105,23, EmiSlot.WOOD).withPointer(
                new EmiSlotPointer(EmiSlotPointer.Type.WOOD, EmiSlotPointer.Corner.BOTTOM_LEFT, EmiSlotPointer.Corner.BOTTOM_RIGHT)
        ));

        //center

        widgets.add(new SingleTextureProgressWidget(
                        MILostFavor.locate("textures/gui/fire_pit_emi_progress_arrow.png"),
                        62, 22, 22, 22, () -> {
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

    }
}
