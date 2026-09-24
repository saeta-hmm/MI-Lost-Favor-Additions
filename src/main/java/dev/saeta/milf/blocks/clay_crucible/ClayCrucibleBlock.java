package dev.saeta.milf.blocks.clay_crucible;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ClayCrucibleBlock extends BaseEntityBlock {

    public static final MapCodec<ClayCrucibleBlock> CODEC = simpleCodec(ClayCrucibleBlock::new);
    public static final BooleanProperty KILN_PART = BooleanProperty.create("kiln_part");

    public final static float KILN_PART_Y_OFFSET = (float) -6 /16;

    public ClayCrucibleBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(KILN_PART, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if(state.getValue(KILN_PART)){
            return Block.box(3, 0, 3, 13, 4, 13);
        }
        return Block.box(3, 0, 3, 13, 10, 13);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(KILN_PART);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {

        BlockState stateBelow = context.getLevel().getBlockState(context.getClickedPos().below());

        if(stateBelow.is(MILFBlocks.KILN)){
            return defaultBlockState().setValue(KILN_PART, true);
        }

        return defaultBlockState();
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

        if(!state.is(newState.getBlock())){
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if(blockEntity instanceof ClayCrucibleBlockEntity clayCrucibleBlockEntity){
                ItemStackHandler itemHandler = clayCrucibleBlockEntity.getItemHandler();

                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack stack = itemHandler.getStackInSlot(i);
                    if(!stack.isEmpty()){
                        Block.popResource(level, pos, stack);
                    }
                }

                FluidTank fluidTank = clayCrucibleBlockEntity.getFluidTank();
                FluidStack fluidStack = fluidTank.getFluid();

                ItemStack gucket = new ItemStack(MILFItems.CLAY_BUCKET.get());

                if(!fluidStack.isEmpty()){
                    FluidHandlerItemStack handler = (FluidHandlerItemStack) FluidUtil.getFluidHandler(gucket).orElseThrow();

                    handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                }

                Block.popResource(level, pos, gucket);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {

        ItemStack gucket = new ItemStack(MILFItems.CLAY_BUCKET.get());


        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof ClayCrucibleBlockEntity clayCrucibleBlockEntity){
            FluidTank fluidTank = clayCrucibleBlockEntity.getFluidTank();
            FluidStack fluidStack = fluidTank.getFluid();



            if(!fluidStack.isEmpty()){
                FluidHandlerItemStack handler = (FluidHandlerItemStack) FluidUtil.getFluidHandler(gucket).orElseThrow();

                handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            }

            return gucket;
        }

        return gucket;
    }

    private static void serverTick(Level level, BlockPos pos, BlockState state, ClayCrucibleBlockEntity clayCrucibleBlockEntity){

        ClayCrucibleBlockEntity.serverTick(level, pos, state, clayCrucibleBlockEntity);

        if (level.getGameTime() % 5 != 0) return;

        if(clayCrucibleBlockEntity.isLit() && level instanceof ServerLevel serverLevel){
            serverLevel.sendParticles(
                    ParticleTypes.FLAME,
                    pos.getX() + 0.5,
                    pos.getY() + 0.75 + (state.getValue(KILN_PART) ? KILN_PART_Y_OFFSET : 0),
                    pos.getZ() + 0.5,
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
