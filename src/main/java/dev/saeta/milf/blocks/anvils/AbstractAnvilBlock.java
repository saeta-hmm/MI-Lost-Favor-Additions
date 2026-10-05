package dev.saeta.milf.blocks.anvils;

import dev.saeta.milf.blocks.BaseDirectionalEntityBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.registries.MILFItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashMap;

public abstract class AbstractAnvilBlock extends BaseDirectionalEntityBlock implements CapabilityProvider {

    private static final HashMap<Direction, VoxelShape> SHAPES = new HashMap<>();

    static {
        SHAPES.put(Direction.NORTH, Shapes.or(
                Block.box(5, 0, 5, 11, 1, 11),
                Block.box(6, 1, 6, 10, 2, 10),
                Block.box(7, 2, 6, 9, 3, 10),
                Block.box(7, 3, 5, 9, 4, 11),
                Block.box(6, 4, 4, 10, 5, 12),
                Block.box(6, 5, 2, 10, 6, 13),
                Block.box(5, 6, 3, 11, 7, 14),
                Block.box(6, 6, 1, 10, 7, 3),
                Block.box(6, 6, 14, 10, 7, 15),
                Block.box(7, 6, 0, 9, 7, 1)
        ));

        SHAPES.put(Direction.EAST, Shapes.or(
                Block.box(5, 0, 5, 11, 1, 11),
                Block.box(6, 1, 6, 10, 2, 10),
                Block.box(6, 2, 7, 10, 3, 9),
                Block.box(5, 3, 7, 11, 4, 9),
                Block.box(4, 4, 6, 12, 5, 10),
                Block.box(3, 5, 6, 14, 6, 10),
                Block.box(2, 6, 5, 13, 7, 11),
                Block.box(13, 6, 6, 15, 7, 10),
                Block.box(1, 6, 6, 2, 7, 10),
                Block.box(15, 6, 7, 16, 7, 9)
        ));

        SHAPES.put(Direction.SOUTH, Shapes.or(
                Block.box(5, 0, 5, 11, 1, 11),
                Block.box(6, 1, 6, 10, 2, 10),
                Block.box(7, 2, 6, 9, 3, 10),
                Block.box(7, 3, 5, 9, 4, 11),
                Block.box(6, 4, 4, 10, 5, 12),
                Block.box(6, 5, 3, 10, 6, 14),
                Block.box(5, 6, 2, 11, 7, 13),
                Block.box(6, 6, 13, 10, 7, 15),
                Block.box(6, 6, 1, 10, 7, 2),
                Block.box(7, 6, 15, 9, 7, 16)
        ));

        SHAPES.put(Direction.WEST, Shapes.or(
                Block.box(5, 0, 5, 11, 1, 11),
                Block.box(6, 1, 6, 10, 2, 10),
                Block.box(6, 2, 7, 10, 3, 9),
                Block.box(5, 3, 7, 11, 4, 9),
                Block.box(4, 4, 6, 12, 5, 10),
                Block.box(2, 5, 6, 13, 6, 10),
                Block.box(3, 6, 5, 14, 7, 11),
                Block.box(1, 6, 6, 3, 7, 10),
                Block.box(14, 6, 6, 15, 7, 10),
                Block.box(0, 6, 7, 1, 7, 9)
        ));
    }

    public AbstractAnvilBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

        if(!state.is(newState.getBlock())){

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof AbstractAnvilBlockEntity anvilBlockEntity){
                ItemStackHandler itemHandler = anvilBlockEntity.getItemHandler();

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
        if(level.isClientSide) return ItemInteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(!(blockEntity instanceof AbstractAnvilBlockEntity anvilBlockEntity)) return ItemInteractionResult.FAIL;

        AbstractAnvilBlockEntity.AnvilItemHandler itemHandler = anvilBlockEntity.getItemHandler();

        if(!stack.isEmpty()){

            if(stack.is(MILFItemTags.HAMMERS)) {

                return ItemInteractionResult.SUCCESS;
            }

            ItemStack toInsert = stack.copyWithCount(1);
            toInsert.set(MILFDataComponents.RANDOM_SEED, level.random.nextInt(109, 109109));
            ItemStack remainingStack = anvilBlockEntity.insertAnywhere(toInsert);

            if(remainingStack.isEmpty()){
                stack.shrink(1);
                player.setItemInHand(hand, stack);

                level.playSound(null, pos, SoundEvents.ANVIL_HIT, SoundSource.BLOCKS, 1,1);

                return ItemInteractionResult.SUCCESS;
            } else {
                level.playSound(null, pos, SoundEvents.IRON_GOLEM_STEP, SoundSource.BLOCKS, 1,1);
            }
        } else {
            if(player.isShiftKeyDown()){


                for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
                    ItemStack outputStack = itemHandler.extractItem(i,64, false);

                    if(!outputStack.isEmpty()){
                        Block.popResource(level, pos, outputStack);
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }

        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

}
