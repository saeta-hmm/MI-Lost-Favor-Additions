package dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.fired;

import dev.saeta.milf.blocks.clay_molds.AbstractCastableMoldBlockEntity;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AnvilMoldBlockEntity extends AbstractCastableMoldBlockEntity {

    public AnvilMoldBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.CLAY_MOLD_ANVIL_FULL.get(), pos, blockState);
    }


    @Override
    protected int getInitialCapacity() {
        return 4000;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AnvilMoldBlockEntity anvilMoldBlockEntity){
        anvilMoldBlockEntity.serverTick();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, AnvilMoldBlockEntity anvilMoldBlockEntity){
        anvilMoldBlockEntity.clientTick();
    }


}
