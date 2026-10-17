package dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.base;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.BaseDirectionalEntityBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.AbstractFiredMoldBaseBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class MoldBaseBlock extends AbstractFiredMoldBaseBlock implements CapabilityProvider {

    public static final MapCodec<MoldBaseBlock> CODEC = simpleCodec(MoldBaseBlock::new);

    public MoldBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseDirectionalEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                MILFBlockEntities.CLAY_MOLD_BASE.get(),
                ( blockEntity,  direction) -> blockEntity.getFluidTank()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.CLAY_MOLD_BASE.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return createTickerHelper(blockEntityType, MILFBlockEntities.CLAY_MOLD_BASE.get(), MoldBaseBlockEntity::clientTick);
        return createTickerHelper(blockEntityType, MILFBlockEntities.CLAY_MOLD_BASE.get(), MoldBaseBlockEntity::serverTick);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MoldBaseBlockEntity(pos, state);
    }




}
