package dev.saeta.milf.blocks.clay_molds.medium.mold_base;

import dev.saeta.milf.blocks.DirectionalItemHandlerBlock;
import dev.saeta.milf.blocks.clay_molds.AbstractClayMoldBlockItem;
import dev.saeta.milf.blocks.clay_molds.CastableMoldBlock;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFDataComponents;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public abstract class AbstractMoldBaseBlock extends DirectionalItemHandlerBlock implements CastableMoldBlock {

    private static final HashMap<Direction, VoxelShape[]> SHAPES = new HashMap<>();

    public final static BooleanProperty FIRE_PIT_PART = BooleanProperty.create("fire_pit_part");


    static {

        SHAPES.put(Direction.NORTH, new VoxelShape[]{
                Block.box(1, 0, 3, 15, 4, 13),
                Block.box(1, 1, 3, 15, 5, 13)
        });
        SHAPES.put(Direction.SOUTH, new VoxelShape[]{
                Block.box(1, 0, 3, 15, 4, 13),
                Block.box(1, 1, 3, 15, 5, 13)
        });

        SHAPES.put(Direction.WEST, new VoxelShape[]{
                Block.box(3,0,1,13,4,15),
                Block.box(3,1,1,13,5,15)
        });
        SHAPES.put(Direction.EAST, new VoxelShape[]{
                Block.box(3,0,1,13,4,15),
                Block.box(3,1,1,13,5,15)
        });

    }

    public AbstractMoldBaseBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FIRE_PIT_PART, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FIRE_PIT_PART);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING))[state.getValue(FIRE_PIT_PART) ? 1 : 0];
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        if( context.getLevel().getBlockState(context.getClickedPos().below()).is(MILFBlocks.FIRE_PIT)) {
            state = state.setValue(FIRE_PIT_PART, true);
        }
        return state;
    }
}
