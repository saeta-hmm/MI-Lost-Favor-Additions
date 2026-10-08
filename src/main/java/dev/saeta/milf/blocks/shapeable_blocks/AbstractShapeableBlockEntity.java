package dev.saeta.milf.blocks.shapeable_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractShapeableBlockEntity extends BlockEntity {

    public AbstractShapeableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public abstract void handleHit(Direction face, Player player);

    public abstract BlockState getCurrentInputBlockState();

    public abstract ItemStack getCurrentInputBlockItem();

    public abstract void updateOutput();
}
