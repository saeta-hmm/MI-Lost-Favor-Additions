package dev.saeta.milf.blocks.bloomery;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.kiln.KilnBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class BloomeryBaseBlock extends BaseEntityBlock implements CapabilityProvider {

    public static final MapCodec<BloomeryBaseBlock> CODEC = simpleCodec(BloomeryBaseBlock::new);

    private final static VoxelShape DIRT = Block.box(0,0,0,16,11,16);
    private final static VoxelShape BASE = Block.box(0,11,0,16,16,16);
    private final static VoxelShape BASE_CENTER = Block.box(5,11,5,11,15,11);

    private final static VoxelShape PIT = Block.box(2,12,2,14,16,14);

    private final static VoxelShape SHAPE =Shapes.or(
            Shapes.join(
                    Shapes.or(
                            DIRT,
                            BASE
                    ),
                    PIT,
                    BooleanOp.ONLY_FIRST
            ),
            BASE_CENTER
    );


    public BloomeryBaseBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.LIT, false));

    }

    private static void serverTick(Level level, BlockPos pos, BlockState state, BloomeryBaseBlockEntity bloomeryBaseBlockEntity) {

        BloomeryBaseBlockEntity.serverTick(level, pos, state, bloomeryBaseBlockEntity);

        if (level.getGameTime() % 20 != 0) return;

        if(bloomeryBaseBlockEntity.isLit() && level instanceof ServerLevel serverLevel){

            serverLevel.sendParticles(
                    ParticleTypes.SMALL_FLAME,
                    pos.getX() + 0.5 + 0.25 * (level.random.nextBoolean() ? 1 : -1),
                    pos.getY() + 0.92,
                    pos.getZ() + 0.5 + 0.25 * (level.random.nextBoolean() ? 1 : -1),
                    2,
                    0, 0.08, 0,
                    0.003
            );

        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, MILFBlockEntities.BLOOMERY_BASE.get(), BloomeryBaseBlock::serverTick);

    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

        if(!state.is(newState.getBlock())){
            //Block.popResource(level, pos, new ItemStack(Blocks.DIRT));

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof BloomeryBaseBlockEntity bloomeryBaseBlockEntity){
                ItemStackHandler itemHandler = bloomeryBaseBlockEntity.getItemHandler();

                for (int i = 0; i < itemHandler.getSlots(); i++) {

                    if(i == BloomeryBaseBlockEntity.COAL_SLOT && bloomeryBaseBlockEntity.isLit()) continue;

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
        if(!stack.isEmpty() && stack.is(MILFItems.FIRESTARTER)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if(level.isClientSide) return ItemInteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(!(blockEntity instanceof BloomeryBaseBlockEntity bloomeryBaseBlockEntity)) return ItemInteractionResult.FAIL;

        ItemStackHandler itemHandler = bloomeryBaseBlockEntity.getItemHandler();

        if(!stack.isEmpty()){


            ItemStack toInsert = stack.copyWithCount(1);
            ItemStack remainingStack = bloomeryBaseBlockEntity.insertAnywhere(toInsert);

            if(remainingStack.isEmpty()){
                stack.shrink(1);
                player.setItemInHand(hand, stack);

                level.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 1,1);

                return ItemInteractionResult.SUCCESS;
            } else {
                level.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT_FAIL, SoundSource.BLOCKS, 1,1);
            }
        } else {
            if(player.isShiftKeyDown()){


                for (int i = 0; i < itemHandler.getSlots(); i++) {

                    if(i == BloomeryBaseBlockEntity.COAL_SLOT && bloomeryBaseBlockEntity.isLit()) continue;

                    ItemStack outputStack = itemHandler.extractItem(i,64, false);

                    if(!outputStack.isEmpty()){
                        BlockState stateAbove = level.getBlockState(pos.above());
                        if(stateAbove.is(MILFBlocks.KILN)){
                            Block.popResourceFromFace(level, pos.above(), stateAbove.getValue(KilnBlock.FACING), outputStack);
                            return ItemInteractionResult.SUCCESS;
                        }
                        Block.popResourceFromFace(level, pos, Direction.UP, outputStack);
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }

        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.LIT);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloomeryBaseBlockEntity(pos, state);
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.BLOOMERY_BASE.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );

    }
}
