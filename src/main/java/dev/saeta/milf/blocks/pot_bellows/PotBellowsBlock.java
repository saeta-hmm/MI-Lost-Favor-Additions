package dev.saeta.milf.blocks.pot_bellows;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlock;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.blocks.kiln.KilnBlock;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class PotBellowsBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final MapCodec<PotBellowsBlock> CODEC = simpleCodec(PotBellowsBlock::new);

    private static final VoxelShape POT_SHAPE = Block.box(3,0,3,13,5,13);
    private static final VoxelShape LEATHER_SHAPE = Block.box(4,5,4,12,8,12);

    public PotBellowsBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.or(POT_SHAPE, LEATHER_SHAPE);
    }


    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, MILFBlockEntities.POT_BELLOWS.get(), PotBellowsBlock::tick);
    }

    private static void tick(Level level, BlockPos pos, BlockState state, PotBellowsBlockEntity potBellowsBlockEntity){

        PotBellowsBlockEntity.tick(level, pos, state, potBellowsBlockEntity);

    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide) return ItemInteractionResult.CONSUME;

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if(!(blockEntity instanceof PotBellowsBlockEntity potBellowsBlockEntity)) return ItemInteractionResult.FAIL;

        if(potBellowsBlockEntity.canActivate()) {
            potBellowsBlockEntity.activate();
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.CONSUME;

    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if((blockEntity instanceof PotBellowsBlockEntity potBellowsBlockEntity) && potBellowsBlockEntity.canActivate()) {
            potBellowsBlockEntity.activate();

        }

        super.fallOn(level, state, pos, entity, fallDistance);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PotBellowsBlockEntity(pos, state);
    }
}
