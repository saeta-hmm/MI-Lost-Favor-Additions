package dev.saeta.milf.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface ItemHandlingBlock {

    default SoundEvent getInsertSound(boolean isFailed, UseItemOnContext context){
        if(isFailed) return SoundEvents.DECORATED_POT_INSERT_FAIL;
        return SoundEvents.DECORATED_POT_INSERT;
    }

    default SoundEvent getExtractSound(UseItemOnContext context){
        return SoundEvents.DECORATED_POT_HIT;
    }

    default ItemInteractionResult handleUseItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if(!(blockEntity instanceof ItemHandlerBlockEntity itemHandlerBlockEntity )) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        AbstractBEItemHandler itemHandler = itemHandlerBlockEntity.getItemHandler();
        if(itemHandler.testInput(stack)){

            if(level.isClientSide()) return ItemInteractionResult.CONSUME;

            ItemStack remainingStack = itemHandlerBlockEntity.insertAnywhere(stack.copyWithCount(1));

            if(remainingStack.isEmpty()){

                SoundEvent insertSound = getInsertSound(false, new UseItemOnContext(stack, state, level, pos));
                if(insertSound != null) level.playSound(null, pos, insertSound, SoundSource.BLOCKS, 1,1);

                stack.shrink(1);
                player.setItemInHand(hand, stack);
                return ItemInteractionResult.SUCCESS;
            } else {
                SoundEvent insertFailSound = getInsertSound(true, new UseItemOnContext(stack, state, level, pos));
                if(insertFailSound != null) level.playSound(null, pos, insertFailSound, SoundSource.BLOCKS, 1,1);
            }

            return ItemInteractionResult.CONSUME;

        } else if(stack.isEmpty() && player.isShiftKeyDown() && itemHandlerBlockEntity.canExtract()){
            if(level.isClientSide()) return ItemInteractionResult.CONSUME;

            if(itemHandlerBlockEntity.reverseExtractOrder()){
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    if(!itemHandlerBlockEntity.canExtractSlot(i)) continue;
                    if(extractFromSlot(i, itemHandler, itemHandlerBlockEntity, state, level, pos)) return ItemInteractionResult.SUCCESS;
                }
            } else {
                for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
                    if(!itemHandlerBlockEntity.canExtractSlot(i)) continue;
                    if(extractFromSlot(i, itemHandler, itemHandlerBlockEntity, state, level, pos)) return ItemInteractionResult.SUCCESS;
                }
            }


        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean extractFromSlot(int slot, AbstractBEItemHandler itemHandler, ItemHandlerBlockEntity itemHandlerBlockEntity, BlockState state, Level level, BlockPos pos){
        ItemStack outputStack = itemHandler.extractItem(slot,itemHandler.getExtractAmount(), false);

        if(!outputStack.isEmpty()){

            BlockPos outputPos = itemHandlerBlockEntity.getOutputPos();
            Direction outputDirection = itemHandlerBlockEntity.getOutputDirection();
            if(outputDirection != null){
                Block.popResourceFromFace(level, outputPos,outputDirection, outputStack);

            } else {
                Block.popResource(level, outputPos, outputStack);

            }
            SoundEvent extractSound = getExtractSound(new UseItemOnContext(outputStack, state, level, pos));
            if(extractSound != null){
                level.playSound(null, outputPos, extractSound, SoundSource.BLOCKS, 1,1);
            }
            return true;

        }

        return false;
    }

    default void handleOnRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

        if(!state.is(newState.getBlock())){

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ItemHandlerBlockEntity itemHandlerBlockEntity){
                itemHandlerBlockEntity.dropContent(pos, level);
            }
        }

    }

    record UseItemOnContext(ItemStack stack, BlockState state, Level level, BlockPos pos){}

}
