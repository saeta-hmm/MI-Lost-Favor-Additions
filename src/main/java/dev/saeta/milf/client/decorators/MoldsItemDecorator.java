package dev.saeta.milf.client.decorators;

import dev.saeta.milf.networking.data.StoredItem;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

public class MoldsItemDecorator implements IItemDecorator {
    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {

        ItemStack castResultItem = stack.getOrDefault(MILFDataComponents.CAST_RESULT,new StoredItem(ItemStack.EMPTY.copy()) ).stack();

        if(!castResultItem.isEmpty()){
            guiGraphics.renderFakeItem(castResultItem, xOffset, yOffset);


        }
        return false;
    }
}
