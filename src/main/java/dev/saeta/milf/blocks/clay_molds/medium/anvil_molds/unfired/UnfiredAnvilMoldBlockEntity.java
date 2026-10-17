package dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.unfired;

import dev.saeta.milf.blocks.UnfiredBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class UnfiredAnvilMoldBlockEntity extends UnfiredBlockEntity {
    public UnfiredAnvilMoldBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    @Override
    protected void handleRecipeOutput(BlockItem blockItem) {
        level.setBlock(worldPosition, blockItem.getBlock().withPropertiesOf(getBlockState()), Block.UPDATE_ALL);
    }


}
