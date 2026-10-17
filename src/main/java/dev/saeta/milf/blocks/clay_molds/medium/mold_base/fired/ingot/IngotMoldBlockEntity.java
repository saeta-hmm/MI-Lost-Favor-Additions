package dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.ingot;

import dev.saeta.milf.blocks.clay_molds.AbstractMultislotCastableMoldBlockEntity;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class IngotMoldBlockEntity extends AbstractMultislotCastableMoldBlockEntity {
    public IngotMoldBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.CLAY_MOLD_INGOT.get(),pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, IngotMoldBlockEntity moldBaseBlockEntity){
        moldBaseBlockEntity.serverTick();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, IngotMoldBlockEntity moldBaseBlockEntity){
        moldBaseBlockEntity.clientTick();
    }

    @Override
    protected int getInitialCapacity() {
        return 250;
    }

    @Override
    public int getSlots() {
        return 3;
    }
}
