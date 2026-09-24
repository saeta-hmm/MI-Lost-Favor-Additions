package dev.saeta.milf.blocks.fire_pit;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlock;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class FirePitBlock extends BaseEntityBlock {

    public static final MapCodec<FirePitBlock> CODEC = simpleCodec(FirePitBlock::new);

    private final static VoxelShape DIRT = Block.box(0,0,0,16,14,16);
    private final static VoxelShape BASE = Block.box(0,14,0,16,16,16);
    private final static VoxelShape STONES = Block.box(1,16,1,15,17,15);

    private final static VoxelShape PIT = Shapes.or(
            Block.box(4,14,4,12,17,12),
            Block.box(3,16,5,13,17,11),
            Block.box(5,16,3,11,17,13)
    );

    private final static VoxelShape SHAPE = Shapes.join(
            Shapes.or(
                    DIRT,
                    BASE,
                    STONES
            ),
            PIT,
            BooleanOp.ONLY_FIRST
    );

    public static final IntegerProperty LOGS = IntegerProperty.create("logs", 0, 4);

    public FirePitBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(LOGS, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LOGS);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

        if(!state.is(newState.getBlock())){
            Block.popResource(level, pos, new ItemStack(Blocks.DIRT));

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof FirePitBlockEntity firePitBlockEntity){
                ItemStackHandler itemHandler = firePitBlockEntity.getItemHandler();

                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack stack = itemHandler.getStackInSlot(i);
                    if(!stack.isEmpty()){
                        Block.popResource(level, pos, stack);
                    }
                }
            }
        }


        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if(!(blockEntity instanceof FirePitBlockEntity firePitBlockEntity)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        ItemStackHandler itemHandler = firePitBlockEntity.getItemHandler();


        if(stack.is(ItemTags.LOGS)){

            if(level.isClientSide()) return ItemInteractionResult.CONSUME;

            ItemStack remainingStack = firePitBlockEntity.insertAnywhere(stack.copyWithCount(1));

            if(remainingStack.isEmpty()){
                stack.shrink(1);
                player.setItemInHand(hand, stack);

                level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1,1);

                return ItemInteractionResult.SUCCESS;
            } else {
                level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 1,1);
            }

            return ItemInteractionResult.CONSUME;

        } else if(stack.isEmpty()){
            if(level.isClientSide()) return ItemInteractionResult.CONSUME;

            for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
                ItemStack outputStack = itemHandler.extractItem(i,1, false);

                if(!outputStack.isEmpty()){
                    Block.popResource(level, pos.above(), outputStack);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FirePitBlockEntity(pos, state);
    }

    private static void serverTick(Level level, BlockPos pos, BlockState state, FirePitBlockEntity firePitBlockEntity) {
        if (level.getGameTime() % 5 != 0) return;

        if(firePitBlockEntity.isLit() && level instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(
                    ParticleTypes.FLAME,
                    pos.getX() + 0.5,
                    pos.getY() + 1,
                    pos.getZ() + 0.5,
                    2,
                    0.08, 0.08, 0.08,
                    0.005
            );

            if (level.getGameTime() % 15 == 0){
                serverLevel.sendParticles(
                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        pos.getX() + 0.5,
                        pos.getY() + 1.2,
                        pos.getZ() + 0.5,
                        0,
                        0.005, 0.09, 0.005,
                        0.25
                );
            }



            if (level.random.nextFloat() < 0.1f) {
                level.playSound(null, pos,
                        SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS,
                        1f, 0.5f + (float) level.random.nextInt(1, 7) / 10);
            }
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, MILFBlockEntities.FIRE_PIT.get(), FirePitBlock::serverTick);

    }
}
