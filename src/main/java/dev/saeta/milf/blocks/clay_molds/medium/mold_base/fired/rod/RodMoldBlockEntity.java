package dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.rod;

import dev.saeta.milf.blocks.clay_molds.AbstractCastableMoldBlockEntity;
import dev.saeta.milf.blocks.clay_molds.AbstractMultislotCastableMoldBlockEntity;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.ingot.IngotMoldBlockEntity;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RodMoldBlockEntity extends AbstractMultislotCastableMoldBlockEntity {
    public RodMoldBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.CLAY_MOLD_ROD.get(),pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RodMoldBlockEntity moldBaseBlockEntity){
        moldBaseBlockEntity.serverTick();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, RodMoldBlockEntity moldBaseBlockEntity){
        moldBaseBlockEntity.clientTick();
    }

    @Override
    protected int getInitialCapacity() {
        return 125;
    }

    @Override
    public int getSlots() {
        return 4;
    }
}
