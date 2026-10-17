package dev.saeta.milf.blocks.kiln;

import dev.saeta.milf.blocks.DirectionalItemHandlerBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;

public abstract class AbstractKilnBlock extends DirectionalItemHandlerBlock {

    private static final VoxelShape LAYER_1 = Shapes.or(
            Block.box(1,0,0,15,3,16),
            Block.box(0,0,1,16,3,15)
    );
    private static final VoxelShape LAYER_2 = Block.box(1,3,1,15,7,15);
    private static final VoxelShape LAYER_3 = Block.box(2,7,2,14,16,14);

    private static final VoxelShape INSIDES = Shapes.or(
            Block.box(3,0,3,13,16,13),
            Block.box(1,0,1,15,3,15),
            Block.box(2,3,2,14,7,14)
    );

    protected static final HashMap<Direction, VoxelShape> SHAPES = new HashMap<>();

    static {
        SHAPES.put(Direction.NORTH, Shapes.join(
                Shapes.or(
                        LAYER_1,
                        LAYER_2,
                        LAYER_3,
                        Block.box(1,7,3,15,11,13)
                ),
                Shapes.or(
                        INSIDES,
                        Block.box(4,0,0,12,3,8),
                        Block.box(5,3,0,11,6,8),
                        Block.box(2,7,3,14,11,13)
                ),
                BooleanOp.ONLY_FIRST
        ));

        SHAPES.put(Direction.EAST, Shapes.join(
                Shapes.or(
                        LAYER_1,
                        LAYER_2,
                        LAYER_3,
                        Block.box(3,7,1,13,11,15)
                ),
                Shapes.or(
                        INSIDES,
                        Block.box(8,0,4,16,3,12),
                        Block.box(8,3,5,16,6,11),
                        Block.box(3,7,2,13,11,14)
                ),
                BooleanOp.ONLY_FIRST
        ));

        SHAPES.put(Direction.SOUTH, Shapes.join(
                Shapes.or(
                        LAYER_1,
                        LAYER_2,
                        LAYER_3,
                        Block.box(1,7,3,15,11,13)
                ),
                Shapes.or(
                        INSIDES,
                        Block.box(4,0,8,12,3,16),
                        Block.box(5,3,8,11,6,16),
                        Block.box(2,7,3,14,11,13)
                ),
                BooleanOp.ONLY_FIRST
        ));

        SHAPES.put(Direction.WEST, Shapes.join(
                Shapes.or(
                        LAYER_1,
                        LAYER_2,
                        LAYER_3,
                        Block.box(3,7,1,13,11,15)
                ),
                Shapes.or(
                        INSIDES,
                        Block.box(0,0,4,8,3,12),
                        Block.box(0,3,5,8,6,11),
                        Block.box(3,7,2,13,11,14)
                ),
                BooleanOp.ONLY_FIRST
        ));
    }

    public AbstractKilnBlock(Properties properties) {
        super(properties);
    }

}
