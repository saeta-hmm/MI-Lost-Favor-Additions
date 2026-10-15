package dev.saeta.milf.blocks.clay_molds.medium.mold_base.unfired;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.BaseDirectionalEntityBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.AbstractMoldBaseBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class UnfiredMoldBaseBlock extends AbstractMoldBaseBlock implements CapabilityProvider {

    public static final MapCodec<UnfiredMoldBaseBlock> CODEC = simpleCodec(UnfiredMoldBaseBlock::new);

    public UnfiredMoldBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    public SoundEvent getInsertSound(boolean isFailed, UseItemOnContext context) {
        return SoundEvents.MUDDY_MANGROVE_ROOTS_HIT;
    }

    @Override
    public SoundEvent getExtractSound(UseItemOnContext context) {
        return SoundEvents.MUDDY_MANGROVE_ROOTS_HIT;
    }

    @Override
    protected MapCodec<? extends BaseDirectionalEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UnfiredMoldBaseBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(newState.is(MILFBlockTags.UNFIRED_MOLDS)) return;
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.UNFIRED_MOLD_BASE.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );
    }
}
