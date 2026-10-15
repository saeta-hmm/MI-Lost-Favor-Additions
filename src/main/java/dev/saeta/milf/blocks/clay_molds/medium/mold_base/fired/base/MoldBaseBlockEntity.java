package dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.base;

import dev.saeta.milf.blocks.clay_molds.AbstractCastableMoldBlockEntity;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MoldBaseBlockEntity extends AbstractCastableMoldBlockEntity {
    public MoldBaseBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.CLAY_MOLD_BASE.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MoldBaseBlockEntity moldBaseBlockEntity){
        moldBaseBlockEntity.serverTick();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MoldBaseBlockEntity moldBaseBlockEntity){
        moldBaseBlockEntity.clientTick();
    }

    @Override
    protected int getInitialCapacity() {
        return 1000;
    }
}
