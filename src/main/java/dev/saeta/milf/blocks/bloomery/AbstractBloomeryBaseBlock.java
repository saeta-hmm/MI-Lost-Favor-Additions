package dev.saeta.milf.blocks.bloomery;

import dev.saeta.milf.blocks.ItemHandlerBlock;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBloomeryBaseBlock extends ItemHandlerBlock {

    public final static BooleanProperty FIRE_PIT_PART = BooleanProperty.create("fire_pit_part");


    protected AbstractBloomeryBaseBlock(Properties properties) {
        super(properties);
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FIRE_PIT_PART);
    }


}
