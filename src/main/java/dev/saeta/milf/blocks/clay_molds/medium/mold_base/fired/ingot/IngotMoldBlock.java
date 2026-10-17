package dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.ingot;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.BaseDirectionalEntityBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.AbstractMultislotMoldBlock;
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

public class IngotMoldBlock extends AbstractMultislotMoldBlock implements CapabilityProvider {

    public static final MapCodec<IngotMoldBlock> CODEC = simpleCodec(IngotMoldBlock::new);

    public IngotMoldBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected int getSlots() {
        return 3;
    }

    @Override
    protected MapCodec<? extends BaseDirectionalEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IngotMoldBlockEntity(pos, state);
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                MILFBlockEntities.CLAY_MOLD_INGOT.get(),
                ( blockEntity,  direction) -> blockEntity.getFluidHandler()
        );


        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.CLAY_MOLD_INGOT.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return createTickerHelper(blockEntityType, MILFBlockEntities.CLAY_MOLD_INGOT.get(), IngotMoldBlockEntity::clientTick);
        return createTickerHelper(blockEntityType, MILFBlockEntities.CLAY_MOLD_INGOT.get(), IngotMoldBlockEntity::serverTick);
    }
}
