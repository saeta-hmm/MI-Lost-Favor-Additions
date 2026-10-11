package dev.saeta.milf.blocks.mold_base;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.BaseDirectionalEntityBlock;
import dev.saeta.milf.blocks.DirectionalItemHandlerBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MoldBaseBlock extends DirectionalItemHandlerBlock implements CapabilityProvider {

    public static final MapCodec<MoldBaseBlock> CODEC = simpleCodec(MoldBaseBlock::new);

    private static final HashMap<Direction, VoxelShape> SHAPES = new HashMap<>();

    static {

        SHAPES.put(Direction.NORTH, Block.box(1,0,3,15,4,13));
        SHAPES.put(Direction.SOUTH, Block.box(1,0,3,15,4,13));

        SHAPES.put(Direction.WEST, Block.box(3,0,1,13,4,15));
        SHAPES.put(Direction.EAST, Block.box(3,0,1,13,4,15));

    }

    public MoldBaseBlock(Properties properties) {
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
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MoldBaseBlockEntity(pos, state);
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
