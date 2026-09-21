package dev.saeta.milf.blocks;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ClayCrucibleBlock extends BaseEntityBlock {

    public static final MapCodec<ClayCrucibleBlock> CODEC = simpleCodec(ClayCrucibleBlock::new);

    public ClayCrucibleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(3, 0, 3, 13, 10, 13);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ClayCrucibleBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, MILFBlockEntities.CLAY_CRUCIBLE.get(), ClayCrucibleBlock::serverTick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!stack.isEmpty() && stack.is(MILFItems.FIRESTARTER)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if(level.isClientSide) return ItemInteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if(!(blockEntity instanceof ClayCrucibleBlockEntity clayCrucibleBlockEntity)) return ItemInteractionResult.FAIL;
        if(FluidUtil.interactWithFluidHandler(player, hand, clayCrucibleBlockEntity.getFluidTank())){
            return ItemInteractionResult.SUCCESS;
        }

        ItemStackHandler itemHandler = clayCrucibleBlockEntity.getItemHandler();

        if(!stack.isEmpty()){
            ItemStack remainingStack = clayCrucibleBlockEntity.insertAnywhere(stack.copyWithCount(1));

            if(remainingStack.isEmpty()){
                stack.shrink(1);
                player.setItemInHand(hand, stack);

                level.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 1,1);

                return ItemInteractionResult.SUCCESS;
            } else {
                level.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT_FAIL, SoundSource.BLOCKS, 1,1);
            }
        } else {
            if(player.isShiftKeyDown() && !clayCrucibleBlockEntity.isLit()){
                for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
                    ItemStack outputStack = itemHandler.extractItem(i,64, false);

                    if(!outputStack.isEmpty()){
                        Block.popResource(level, pos, outputStack);
                        clayCrucibleBlockEntity.checkAndSetIfFull();
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }

            clayCrucibleBlockEntity.checkAndSetIfFull();
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

        if(state.is(newState.getBlock())){
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if(blockEntity instanceof ClayCrucibleBlockEntity clayCrucibleBlockEntity){
                ItemStackHandler itemHandler = clayCrucibleBlockEntity.getItemHandler();

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

    private static void serverTick(Level level, BlockPos pos, BlockState state, ClayCrucibleBlockEntity clayCrucibleBlockEntity){

        ClayCrucibleBlockEntity.serverTick(level, pos, state, clayCrucibleBlockEntity);

        if (level.getGameTime() % 5 != 0) return;

        if(clayCrucibleBlockEntity.isLit() && level instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(
                    ParticleTypes.FLAME,
                    pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5,
                    3,
                    0.16, 0.1, 0.16,
                    0.004
            );

            if (level.random.nextFloat() < 0.3f) {
                level.playSound(null, pos,
                        SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS,
                        1f, 1f);
            }
        }
    }
}
