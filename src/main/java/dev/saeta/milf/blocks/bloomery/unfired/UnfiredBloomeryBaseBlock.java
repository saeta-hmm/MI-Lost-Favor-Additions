package dev.saeta.milf.blocks.bloomery.unfired;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.bloomery.AbstractBloomeryBaseBlock;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class UnfiredBloomeryBaseBlock extends AbstractBloomeryBaseBlock {

    private final static VoxelShape BASE = Block.box(0,0,0,16,5,16);
    private final static VoxelShape BASE_CENTER = Block.box(5,0,5,11,4,11);

    private final static VoxelShape PIT = Block.box(2,1,2,14,5,14);

    protected final static VoxelShape SHAPE = Shapes.or(
            Shapes.join(
                    BASE,
                    PIT,
                    BooleanOp.ONLY_FIRST
            ),
            BASE_CENTER
    );


    protected final static VoxelShape FIRE_PIT_PART_SHAPE = Shapes.or(
            Shapes.join(
                    BASE,
                    PIT,
                    BooleanOp.ONLY_FIRST
            ),
            BASE_CENTER
    ).move(0,1/ 16f,0);

    public static final MapCodec<UnfiredBloomeryBaseBlock> CODEC = simpleCodec(UnfiredBloomeryBaseBlock::new);

    public UnfiredBloomeryBaseBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FIRE_PIT_PART, false));

    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FIRE_PIT_PART) ? FIRE_PIT_PART_SHAPE : SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        if( context.getLevel().getBlockState(context.getClickedPos().below()).is(MILFBlocks.FIRE_PIT)) {
            state = state.setValue(FIRE_PIT_PART, true);
        }
        return state;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UnfiredBloomeryBaseBlockEntity(pos, state);
    }
}
