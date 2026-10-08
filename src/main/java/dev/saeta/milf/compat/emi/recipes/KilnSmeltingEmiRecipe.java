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
import dev.saeta.milf.recipes.kiln.KilnSmeltingRecipe;
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
        return 90 + 20;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        //left

        widgets.addTexture(MILostFavor.locate("textures/gui/kiln_emi.png"), 0,0,64,80,0,0, 64, 80, 64, 80);

        widgets.addTexture(
                MILostFavor.locate("textures/gui/fire_pit_emi.png"),
                0,67,64,18,0,0, 64, 18, 64, 18
        ).tooltip(firePitTooltip);

        widgets.add(new MILFEmiSlotWidget(getInputs().get(0), 23,21, EmiSlot.KILN));
        widgets.add(new MILFEmiSlotWidget(getInputs().get(1), 23,67, EmiSlot.FIRE_PIT));


        //right

        widgets.addTexture(MILostFavor.locate("textures/gui/kiln_emi.png"), 80,0,64,80,0,0, 64, 80, 64, 80);
        widgets.addTexture(
                MILostFavor.locate("textures/gui/fire_pit_emi.png"),
                80,67,64,18,0,0, 64, 18, 64, 18
        ).tooltip(firePitTooltip);

        widgets.add(new MILFEmiSlotWidget(getOutputs().get(0), 103,21, EmiSlot.KILN).recipeContext(this));
        widgets.add(new MILFEmiSlotWidget(getOutputs().get(1), 103,67, EmiSlot.FIRE_PIT).recipeContext(this));

        //center

        widgets.add(new MILFEmiSlotWidget(getCatalysts().get(0), 53,89, EmiSlot.WOOD).withPointer(
                new EmiSlotPointer(EmiSlotPointer.Type.WOOD, EmiSlotPointer.Corner.TOP_LEFT)
        ));

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

    }
}
