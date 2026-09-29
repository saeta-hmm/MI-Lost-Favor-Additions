package dev.saeta.milf.blocks.roasting_contraption;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlock;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.blocks.kiln.KilnBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class RoastingContraptionBlock extends BaseEntityBlock implements CapabilityProvider {

    public static final MapCodec<RoastingContraptionBlock> CODEC = simpleCodec(RoastingContraptionBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final HashMap<Direction, VoxelShape> SHAPES = new HashMap<>();

    static {
        SHAPES.put(Direction.NORTH, Shapes.join(

                Shapes.or(
                        Block.box(0,0,7,1,9,9),
                        Block.box(0,7,6,1,11,10),
                        Block.box(15,0,7,16,9,9),
                        Block.box(15,7,6,16,11,10),

                        Block.box(-1,9,7,17,10,9)

                ),
                Block.box(-1,10,7,17,11,9),
                BooleanOp.ONLY_FIRST
        ));

        SHAPES.put(Direction.EAST, Shapes.join(
                Shapes.or(
                        Block.box(7, 0, 0, 9, 9, 1),
                        Block.box(6, 7, 0, 10, 11, 1),
                        Block.box(7, 0, 15, 9, 9, 16),
                        Block.box(6, 7, 15, 10, 11, 16),

                        Block.box(7, 9, -1, 9, 10, 17)
                ),
                Block.box(7, 10, -1, 9, 11, 17),
                BooleanOp.ONLY_FIRST
        ));

        SHAPES.put(Direction.SOUTH, Shapes.join(

                Shapes.or(
                        Block.box(0,0,7,1,9,9),
                        Block.box(0,7,6,1,11,10),
                        Block.box(15,0,7,16,9,9),
                        Block.box(15,7,6,16,11,10),

                        Block.box(-1,9,7,17,10,9)

                ),
                Block.box(-1,10,7,17,11,9),
                BooleanOp.ONLY_FIRST
        ));

        SHAPES.put(Direction.WEST, Shapes.join(
                Shapes.or(
                        Block.box(7, 0, 0, 9, 9, 1),
                        Block.box(6, 7, 0, 10, 11, 1),
                        Block.box(7, 0, 15, 9, 9, 16),
                        Block.box(6, 7, 15, 10, 11, 16),

                        Block.box(7, 9, -1, 9, 10, 17)
                ),
                Block.box(7, 10, -1, 9, 11, 17),
                BooleanOp.ONLY_FIRST
        ));
    }

    public RoastingContraptionBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, MILFBlockEntities.ROASTING_CONTRAPTION.get(), RoastingContraptionBlock::serverTick);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RoastingContraptionBlockEntity roastingContraptionBlockEntity) {
        RoastingContraptionBlockEntity.serverTick(level, pos, state, roastingContraptionBlockEntity);
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
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        return stateBelow.is(MILFBlocks.FIRE_PIT);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) return Blocks.AIR.defaultBlockState();

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide) return ItemInteractionResult.CONSUME_PARTIAL;
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if(!(blockEntity instanceof RoastingContraptionBlockEntity roastingContraptionBlockEntity)) return ItemInteractionResult.FAIL;

        ItemStackHandler itemStackHandler = roastingContraptionBlockEntity.getItemHandler();

        if(player.isShiftKeyDown() && stack.isEmpty()){
            for (int i = itemStackHandler.getSlots() - 1; i >= 0; i--) {
                ItemStack outputStack = itemStackHandler.extractItem(i,1, false);
                if(!outputStack.isEmpty()){
                    Block.popResourceFromFace(level, pos, Direction.UP, outputStack);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        } else if (!stack.isEmpty())  {
            ItemStack remainingStack = roastingContraptionBlockEntity.insertAnywhere(stack.copyWithCount(1));

            if(remainingStack.isEmpty()){
                stack.shrink(1);
                player.setItemInHand(hand, stack);

                level.playSound(null, pos, SoundEvents.BUNDLE_INSERT, SoundSource.BLOCKS, 1,1);

                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

        if(!state.is(newState.getBlock())){
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if(blockEntity instanceof RoastingContraptionBlockEntity roastingContraptionBlockEntity){
                ItemStackHandler itemHandler = roastingContraptionBlockEntity.getItemHandler();

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
    protected MapCodec<? extends RoastingContraptionBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RoastingContraptionBlockEntity(pos, state);
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
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.ROASTING_CONTRAPTION.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );
    }
}
