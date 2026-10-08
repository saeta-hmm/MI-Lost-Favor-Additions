package dev.saeta.milf.blocks.pot_bellows;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.BaseDirectionalEntityBlock;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PotBellowsBlock extends BaseDirectionalEntityBlock {

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
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
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
    protected MapCodec<? extends BaseDirectionalEntityBlock> codec() {
        return CODEC;
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
