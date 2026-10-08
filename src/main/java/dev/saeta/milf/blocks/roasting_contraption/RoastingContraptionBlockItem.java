package dev.saeta.milf.blocks.roasting_contraption;

import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RoastingContraptionBlockItem extends BlockItem {
    public RoastingContraptionBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().below();
        BlockState targetState = level.getBlockState(pos);

        return targetState.is(MILFBlocks.FIRE_PIT);

    }
}
