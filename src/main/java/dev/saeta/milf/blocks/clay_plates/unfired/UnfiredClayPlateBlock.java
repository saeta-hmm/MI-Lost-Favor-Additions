package dev.saeta.milf.blocks.clay_plates.unfired;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.clay_plates.AbstractClayPlateBlock;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class UnfiredClayPlateBlock extends AbstractClayPlateBlock {

    public static final MapCodec<UnfiredClayPlateBlock> CODEC = simpleCodec(UnfiredClayPlateBlock::new);

    public UnfiredClayPlateBlock(Properties properties) {
        super(properties.strength(0.3f,2).sound(SoundType.MUD));
    }

    @Override
    protected SoundEvent getInsertSound(boolean isFailed) {
        return SoundEvents.MUDDY_MANGROVE_ROOTS_HIT;
    }

    @Override
    protected SoundEvent getExtractSound() {
        return SoundEvents.MUDDY_MANGROVE_ROOTS_HIT;
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.UNFIRED_CLAY_PLATE.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(newState.is(MILFBlockTags.UNFIRED_MOLDS)) return;
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UnfiredClayPlateBlockEntity(pos, state);
    }

    public static class UnfiredClayPlateItem extends BlockItem{

        public UnfiredClayPlateItem(Block block, Properties properties) {
            super(block, properties.stacksTo(8));
        }
    }
}
