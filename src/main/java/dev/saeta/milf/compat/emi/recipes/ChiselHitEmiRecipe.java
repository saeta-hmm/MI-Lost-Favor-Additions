package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.*;
import dev.saeta.milf.recipes.fire_pit.PitFiringRecipe;
import dev.saeta.milf.recipes.world.ChiselHitRecipe;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItemTags;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChiselHitEmiRecipe implements EmiRecipe {

    public final SizedIngredient input;
    public final ItemStack output;

    public final ResourceLocation id;


    public ChiselHitEmiRecipe(RecipeHolder<ChiselHitRecipe> holder) {
        ChiselHitRecipe pitFiringRecipe = holder.value();
        this.input = pitFiringRecipe.input();

        this.output = pitFiringRecipe.output();

        this.id = holder.id();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.CHISEL_HIT;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(EmiIngredient.of(input.ingredient()).setAmount(input.count()));
        return inputs;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        List<EmiIngredient> catalysts = new ArrayList<>();

        catalysts.add(EmiIngredient.of(MILFItemTags.HAMMERS));
        catalysts.add(EmiIngredient.of(MILFItemTags.CHISELS));

        return catalysts;
    }

    @Override
    public List<EmiStack> getOutputs() {
        List<EmiStack> outputs = new ArrayList<>();
        outputs.add(EmiStack.of(output));
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 80;
    }

    @Override
    public int getDisplayHeight() {
        return 48;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        widgets.add(new MILFEmiTextureWidget(EmiTexture.CHISEL_HIT, 0, 0));

        widgets.add(new MILFEmiSlotWidget(getCatalysts().get(0), 1,1, EmiSlot.STONE).drawBack(false).catalyst(true));
        widgets.add(new MILFEmiSlotWidget(getCatalysts().get(1), 8,8, EmiSlot.STONE).drawBack(false).catalyst(true));

        widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 28,28, EmiSlot.STONE));

        widgets.add(new MILFEmiSlotWidget(getOutputs().get(0), 52,4, EmiSlot.STONE).recipeContext(this));

    }
}
