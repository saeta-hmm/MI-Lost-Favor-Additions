package dev.saeta.milf.blocks.roasting_contraption;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.BaseDirectionalEntityBlock;
import dev.saeta.milf.blocks.DirectionalItemHandlerBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class RoastingContraptionBlock extends DirectionalItemHandlerBlock implements CapabilityProvider {

    public static final MapCodec<RoastingContraptionBlock> CODEC = simpleCodec(RoastingContraptionBlock::new);

    private static final HashMap<Direction, VoxelShape> SHAPES = new HashMap<>();

    static {
        SHAPES.put(Direction.NORTH, Shapes.join(

                Shapes.or(
                        Block.box(0,0,7,1,9,9),
                        Block.box(0,7,6,1,11,10),
                        Block.box(15,0,7,16,9,9),
                        Block.box(15,7,6,16,11,10),

                        Block.box(-1,9,7,17,10,9)

                ),
                Block.box(-1,10,7,17,11,9),
                BooleanOp.ONLY_FIRST
        ));

        SHAPES.put(Direction.EAST, Shapes.join(
                Shapes.or(
                        Block.box(7, 0, 0, 9, 9, 1),
                        Block.box(6, 7, 0, 10, 11, 1),
                        Block.box(7, 0, 15, 9, 9, 16),
                        Block.box(6, 7, 15, 10, 11, 16),

                        Block.box(7, 9, -1, 9, 10, 17)
                ),
                Block.box(7, 10, -1, 9, 11, 17),
                BooleanOp.ONLY_FIRST
        ));

        SHAPES.put(Direction.SOUTH, Shapes.join(

                Shapes.or(
                        Block.box(0,0,7,1,9,9),
                        Block.box(0,7,6,1,11,10),
                        Block.box(15,0,7,16,9,9),
                        Block.box(15,7,6,16,11,10),

                        Block.box(-1,9,7,17,10,9)

                ),
                Block.box(-1,10,7,17,11,9),
                BooleanOp.ONLY_FIRST
        ));

        SHAPES.put(Direction.WEST, Shapes.join(
                Shapes.or(
                        Block.box(7, 0, 0, 9, 9, 1),
                        Block.box(6, 7, 0, 10, 11, 1),
                        Block.box(7, 0, 15, 9, 9, 16),
                        Block.box(6, 7, 15, 10, 11, 16),

                        Block.box(7, 9, -1, 9, 10, 17)
                ),
                Block.box(7, 10, -1, 9, 11, 17),
                BooleanOp.ONLY_FIRST
        ));
    }

    public RoastingContraptionBlock(Properties properties) {
        super(properties);
    }

    @Override
    public SoundEvent getInsertSound(boolean isFailed, UseItemOnContext context) {
        return isFailed ? SoundEvents.BUNDLE_DROP_CONTENTS : SoundEvents.BUNDLE_INSERT;
    }

    @Override
    public SoundEvent getExtractSound(UseItemOnContext context) {
        return SoundEvents.BUNDLE_REMOVE_ONE;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, MILFBlockEntities.ROASTING_CONTRAPTION.get(), RoastingContraptionBlock::serverTick);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RoastingContraptionBlockEntity roastingContraptionBlockEntity) {
        RoastingContraptionBlockEntity.serverTick(level, pos, state, roastingContraptionBlockEntity);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        return stateBelow.is(MILFBlocks.FIRE_PIT);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) return Blocks.AIR.defaultBlockState();

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected MapCodec<? extends RoastingContraptionBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RoastingContraptionBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.ROASTING_CONTRAPTION.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );
    }
}
