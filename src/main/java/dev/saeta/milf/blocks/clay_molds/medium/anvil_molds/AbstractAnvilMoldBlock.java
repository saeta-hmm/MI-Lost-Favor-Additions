package dev.saeta.milf.blocks.clay_molds.medium.anvil_molds;

import dev.saeta.milf.blocks.DirectionalItemHandlerBlock;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public abstract class AbstractAnvilMoldBlock extends DirectionalItemHandlerBlock {
    public AbstractAnvilMoldBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(SIDE, false));
    }

    private static final HashMap<Direction, VoxelShape[]> SHAPES = new HashMap<>();

    public final static BooleanProperty SIDE = BooleanProperty.create("side");

    static {

        SHAPES.put(Direction.NORTH, new VoxelShape[]{
                Block.box(1,0,4,15,10,12),
                Block.box(1,1,3,15,9,13),
                }
        );
        SHAPES.put(Direction.SOUTH, new VoxelShape[]{
                Block.box(1,0,4,15,10,12),
                Block.box(1,1,3,15,9,13),
                }
        );

        SHAPES.put(Direction.WEST, new VoxelShape[]{
                Block.box(4,0,1,12,10,15),
                Block.box(3,1,1,13,9,15),
                }
        );
        SHAPES.put(Direction.EAST, new VoxelShape[]{
                Block.box(4,0,1,12,10,15),
                Block.box(3,1,1,13,9,15),
                }
        );

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SIDE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING))[state.getValue(SIDE) ? 1 : 0];
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        if( context.getLevel().getBlockState(context.getClickedPos().below()).is(MILFBlocks.FIRE_PIT)) {
            //MILostFavor.LOGGER.info("WHAT");
            state = state.setValue(SIDE, true);
        }
        return state;
    }
}
