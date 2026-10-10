package dev.saeta.milf.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class ItemHandlerBlock extends BaseEntityBlock {

    protected ItemHandlerBlock(Properties properties) {
        super(properties);
    }

    protected SoundEvent getInsertSound(boolean isFailed, UseItemOnContext context){
        if(isFailed) return SoundEvents.DECORATED_POT_INSERT_FAIL;
        return SoundEvents.DECORATED_POT_INSERT;
    }

    protected SoundEvent getExtractSound(UseItemOnContext context){
        return SoundEvents.DECORATED_POT_HIT;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if(!(blockEntity instanceof ItemHandlerBlockEntity itemHandlerBlockEntity )) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        AbstractBEItemHandler itemHandler = itemHandlerBlockEntity.getItemHandler();
        if(itemHandler.testInput(stack)){

            if(level.isClientSide()) return ItemInteractionResult.CONSUME;

            ItemStack remainingStack = itemHandlerBlockEntity.insertAnywhere(stack.copyWithCount(1));

            if(remainingStack.isEmpty()){


                level.playSound(null, pos, getInsertSound(false, new UseItemOnContext(stack, state, level, pos)), SoundSource.BLOCKS, 1,1);

                stack.shrink(1);
                player.setItemInHand(hand, stack);
                return ItemInteractionResult.SUCCESS;
            } else {
                level.playSound(null, pos, getInsertSound(true, new UseItemOnContext(stack, state, level, pos)), SoundSource.BLOCKS, 1,1);
            }

            return ItemInteractionResult.CONSUME;

        } else if(stack.isEmpty() && player.isShiftKeyDown() && itemHandlerBlockEntity.canExtract()){
            if(level.isClientSide()) return ItemInteractionResult.CONSUME;

            for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
                ItemStack outputStack = itemHandler.extractItem(i,1, false);

                if(!outputStack.isEmpty()){

                    BlockPos outputPos = itemHandlerBlockEntity.getOutputPos();
                    Block.popResource(level, outputPos, outputStack);
                    level.playSound(null, outputPos, getExtractSound(new UseItemOnContext(outputStack, state, level, pos)), SoundSource.BLOCKS, 1,1);
                    return ItemInteractionResult.SUCCESS;

                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

        if(!state.is(newState.getBlock())){

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ItemHandlerBlockEntity itemHandlerBlockEntity){
                itemHandlerBlockEntity.dropContent(pos, level);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public record UseItemOnContext(ItemStack stack, BlockState state, Level level, BlockPos pos){}

}
