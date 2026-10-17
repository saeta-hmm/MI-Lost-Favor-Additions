package dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.unfired;

import dev.saeta.milf.blocks.clay_molds.medium.mold_base.unfired.UnfiredMoldBaseBlock;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class UnfiredAnvilMoldHalfBlock extends UnfiredMoldBaseBlock {

    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");
    private static final HashMap<Direction, VoxelShape> VERTICAL_SHAPES = new HashMap<>();

    static {

        VERTICAL_SHAPES.put(Direction.NORTH, Block.box(1, 0, 8, 15, 10, 12));
        VERTICAL_SHAPES.put(Direction.SOUTH, Block.box(1, 0, 4, 15, 10, 8));
        VERTICAL_SHAPES.put(Direction.WEST, Block.box(8,0,1,12,10,15));
        VERTICAL_SHAPES.put(Direction.EAST, Block.box(4,0,1,8,10,15));

    }

    public UnfiredAnvilMoldHalfBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(VERTICAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VERTICAL);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(VERTICAL) ? VERTICAL_SHAPES.get(state.getValue(FACING)) : super.getShape(state, level, pos, context);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        if( context.getLevel().getBlockState(context.getClickedPos().below()).is(MILFBlocks.FIRE_PIT)) {
            state = state.setValue(FIRE_PIT_PART, true);
        } else {
            state = state.setValue(VERTICAL, true);
        }
        return state;
    }

    public static class UnfiredAnvilMoldHalfBlockItem extends BlockItem{

        public UnfiredAnvilMoldHalfBlockItem(Block block, Properties properties) {
            super(block, properties);
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {

            Level level = context.getLevel();
            BlockPlaceContext blockPlaceContext = new BlockPlaceContext(context);
            BlockPos placePos = blockPlaceContext.getClickedPos();

            if(level.getBlockState(placePos).is(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_HALF)){
                placeAnvilMold(level, placePos);
                return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
            } else if(level.getBlockState(context.getClickedPos()).is(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_HALF)){
                placeAnvilMold(level, context.getClickedPos());
                return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
            }



            return super.useOn(context);

        }

        private static void placeAnvilMold(Level level, BlockPos placePos) {

            if(!level.isClientSide){
                BlockState state = level.getBlockState(placePos);
                level.setBlock(
                        placePos,
                        MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_FULL.get().defaultBlockState().setValue(
                                BlockStateProperties.HORIZONTAL_FACING,
                                state.getValue(BlockStateProperties.HORIZONTAL_FACING
                                )
                        ),
                        Block.UPDATE_ALL
                );
                level.playSound(null, placePos, SoundEvents.MUD_PLACE, SoundSource.BLOCKS);
            }

        }


    }
}
