package dev.saeta.milf.blocks;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class AbstractBEItemHandler extends ItemStackHandler {

    public AbstractBEItemHandler(int size) {
        super(size);
    }

    protected abstract boolean testInput(ItemStack stack);
}
