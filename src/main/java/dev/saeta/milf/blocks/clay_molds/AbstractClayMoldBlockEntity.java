package dev.saeta.milf.blocks.clay_molds;

import dev.saeta.milf.blocks.ItemHandlerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractClayMoldBlockEntity extends ItemHandlerBlockEntity {
    public AbstractClayMoldBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState blockState
    ) {
        super(type, pos, blockState);
    }

    @Override
    public boolean shouldDropContent() {
        return true;
    }
}
