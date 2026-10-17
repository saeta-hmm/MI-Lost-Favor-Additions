package dev.saeta.milf.blocks.clay_molds.small.fired;

import dev.saeta.milf.blocks.clay_molds.AbstractCastableMoldBlockEntity;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ClayMoldBlockEntity extends AbstractCastableMoldBlockEntity {

    public ClayMoldBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.CLAY_MOLD.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ClayMoldBlockEntity clayMoldBlockEntity){
        clayMoldBlockEntity.serverTick();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ClayMoldBlockEntity clayMoldBlockEntity){
        clayMoldBlockEntity.clientTick();
    }

    @Override
    protected int getInitialCapacity() {
        return 1000;
    }
}
