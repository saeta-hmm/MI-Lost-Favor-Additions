package dev.saeta.milf.blocks.bloomery;

import dev.saeta.milf.blocks.ReplacerBlockItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BloomeryBaseBlockItem extends ReplacerBlockItem {
    public BloomeryBaseBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canReplace(BlockState state) {
        return state.is(BlockTags.DIRT);
    }
}
