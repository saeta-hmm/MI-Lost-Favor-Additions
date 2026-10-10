package dev.saeta.milf.compat.emi.recipes;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.compat.emi.MILFEmiRecipeCategories;
import dev.saeta.milf.compat.emi.widgets.*;
import dev.saeta.milf.recipes.kiln.KilnSmeltingRecipe;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KilnSmeltingEmiRecipe implements EmiRecipe {

    public final SizedIngredient input;
    public final ItemStack output;
    public final ResourceLocation id;
    private final int time = 200;

    public KilnSmeltingEmiRecipe(RecipeHolder<KilnSmeltingRecipe> holder) {
        KilnSmeltingRecipe kilnSmeltingRecipe = holder.value();
        this.input = kilnSmeltingRecipe.input();

        this.output = kilnSmeltingRecipe.output();

        this.id = holder.id();
    }

    public KilnSmeltingEmiRecipe(RecipeHolder<SmeltingRecipe> holder, SizedIngredient input, ItemStack output) {
        this.input = input;

        this.output = output;

        this.id = holder.id();
    }

    public static void parseFurnaceRecipes(EmiRegistry registry){

        outer:
        for (RecipeHolder<SmeltingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)) {

            SmeltingRecipe recipe = holder.value();

            for (var ingredient : recipe.getIngredients()){

                for(var stack : ingredient.getItems()){


                    Block block = Block.byItem(stack.getItem());

                    BlockState state = block.defaultBlockState();

                    if(state.isAir()) continue outer;

                    if(block instanceof Fallable) continue outer;

                    if(!state.canOcclude()) continue outer;

                }

            }

            ItemStack output = recipe.getResultItem(null);

            Block block = Block.byItem(output.getItem());

            BlockState state = block.defaultBlockState();

            if(state.isAir()) continue outer;

            if(block instanceof Fallable) continue outer;

            if(!state.canOcclude()) continue outer;

            ResourceLocation id = MILostFavor.locate("/kiln_smelting/furnace_parsed/" + holder.id().getNamespace() + "/" + holder.id().getPath());

            registry.addRecipe(new KilnSmeltingEmiRecipe(
                    new RecipeHolder<>(id, recipe),
                    new SizedIngredient(recipe.getIngredients().getFirst(), 1),
                    output
            ));
        }

    }


    @Override
    public EmiRecipeCategory getCategory() {
        return MILFEmiRecipeCategories.KILN_SMELTING;
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
    public List<EmiStack> getOutputs() {
        List<EmiStack> outputs = new ArrayList<>();
        outputs.add(EmiStack.of(output));
        outputs.add(EmiStack.of(Items.CHARCOAL));
        return outputs;
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
    public int getDisplayWidth() {
        return 144;
    }

    @Override
    public int getDisplayHeight() {
        return 90 ;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        //left

        widgets.add(new MILFEmiTextureWidget(EmiTexture.FIRE_PIT, 0, 53)
                .addEmiStackTooltip(EmiStack.of(MILFBlocks.FIRE_PIT))
                .addEmiStackRemainderTooltip(getCatalysts().get(0))
        );

        widgets.add(new MILFEmiTextureWidget(EmiTexture.KILN, 0, 0));

        widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 23,21, EmiSlot.KILN));
        widgets.add(new MILFEmiSlotWidget(getInputs().get(1), 23,67, EmiSlot.FIRE_PIT));


        //right

        widgets.add(new MILFEmiTextureWidget(EmiTexture.FIRE_PIT, 80, 53)
                .addEmiStackTooltip(EmiStack.of(MILFBlocks.FIRE_PIT))
        );

        widgets.add(new MILFEmiTextureWidget(EmiTexture.KILN, 80, 0));

        widgets.add(new MILFEmiSlotWidget(getOutputs().get(0), 103,21, EmiSlot.KILN).recipeContext(this));
        widgets.add(new MILFEmiSlotWidget(getOutputs().get(1), 103,67, EmiSlot.FIRE_PIT).recipeContext(this));

        //center

        widgets.add(new MILFEmiArrowWidget(EmiProgressArrow.KILN, 61, 22, time));

    }
}
