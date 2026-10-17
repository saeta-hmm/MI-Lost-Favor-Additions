package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.EmiSlot;
import dev.saeta.milf.compat.emi.widgets.EmiTexture;
import dev.saeta.milf.compat.emi.widgets.MILFEmiSlotWidget;
import dev.saeta.milf.compat.emi.widgets.MILFEmiTextureWidget;
import dev.saeta.milf.recipes.world.WorldPlacementRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WorldPlacementEmiRecipe implements EmiRecipe {

    public final List<Ingredient> ingredients;
    public final ItemStack output;
    public final ResourceLocation id;

    public WorldPlacementEmiRecipe(RecipeHolder<WorldPlacementRecipe> holder){
        WorldPlacementRecipe worldPlacementRecipe = holder.value();

        this.ingredients = worldPlacementRecipe.inputs();
        this.output = worldPlacementRecipe.output();

        this.id = holder.id();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.WORLD_PLACEMENT;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }



    @Override
    public List<EmiIngredient> getInputs() {

        return new ArrayList<>(new ArrayList<>(ingredients.stream().map(EmiIngredient::of).toList()));
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

        widgets.add(new MILFEmiTextureWidget(EmiTexture.WORLD_PLACEMENT, 0, 0));

        int size = getInputs().size();

        if(size >= 4){
            widgets.add(new MILFEmiSlotWidget(getInputs().get(3), 46,46, EmiSlot.GENERIC));
        }
        if(size >= 3){
            widgets.add(new MILFEmiSlotWidget(getInputs().get(2), 46,0, EmiSlot.GENERIC));
        }
        if(size >= 2){
            widgets.add(new MILFEmiSlotWidget(getInputs().get(1), 0,46, EmiSlot.GENERIC));
        }
        if(size >= 1){
            widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 0,0, EmiSlot.GENERIC));
        }

        widgets.add(new MILFEmiSlotWidget(getOutputs().get(0), 19,19, EmiSlot.GENERIC).customSize(26,26).recipeContext(this));

        //widgets.add(new MILFEmiArrowWidget(EmiProgressArrow.WOOD, 45, 29, stirs));

    }

}
