package dev.saeta.milf.blocks.bloomery.unfired;

import dev.saeta.milf.blocks.UnfiredBlockEntity;
import dev.saeta.milf.blocks.kiln.fired.KilnBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class UnfiredBloomeryBaseBlockEntity extends UnfiredBlockEntity {
    public UnfiredBloomeryBaseBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    @Override
    protected void handleRecipeOutput(BlockItem blockItem) {
        level.setBlock(worldPosition, blockItem.getBlock().withPropertiesOf(getBlockState()), Block.UPDATE_ALL);

    }
}
