package dev.saeta.milf.blocks.clay_plates;

import dev.saeta.milf.blocks.ItemHandlerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractClayPlateBlockEntity extends ItemHandlerBlockEntity {
    public AbstractClayPlateBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState blockState
    ) {
        super(type, pos, blockState);
    }
}
