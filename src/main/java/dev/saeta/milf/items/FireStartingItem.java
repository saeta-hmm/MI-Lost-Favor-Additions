package dev.saeta.milf.items;

import dev.saeta.milf.blocks.FlammableBlockEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public interface FireStartingItem {

    static boolean tryIgnite(ServerLevel level, BlockPos blockPos){

        BlockEntity blockEntity = level.getBlockEntity(blockPos);

        if(blockEntity instanceof FlammableBlockEntity flammableBlockEntity){

            if(!flammableBlockEntity.canBeIgnited()) return false;

            flammableBlockEntity.ignite();

            return true;
        }

        return false;
    }

    static BlockPos getBlockPos(BlockHitResult hitResult){


        if(hitResult.getType() == HitResult.Type.BLOCK){
            return hitResult.getBlockPos();
        }

        return null;
    }

    static BlockHitResult getHitResult(ServerLevel level, Player player){
        double reachDistance = player.blockInteractionRange();

        return level.clip(new ClipContext(
                player.getEyePosition(),
                player.getEyePosition().add(player.getViewVector(1).scale(reachDistance)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));
    }

    //totally not FlintAndSteelItem.useOn() (‾◡◝)
    static ItemStack trySetOnFire(ItemStack stack, BlockHitResult hitResult, Player player, ServerLevel level){
        UseOnContext context = new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult);

        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        BlockState blockstate2 = blockstate.getToolModifiedState(context, net.neoforged.neoforge.common.ItemAbilities.FIRESTARTER_LIGHT, false);
        if (blockstate2 == null) {
            BlockPos blockpos1 = blockpos.relative(context.getClickedFace());
            if (BaseFireBlock.canBePlacedAt(level, blockpos1, context.getHorizontalDirection())) {
                level.playSound(null, blockpos1, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1, level.getRandom().nextFloat() * 0.4f + 0.8f);
                BlockState blockstate1 = BaseFireBlock.getState(level, blockpos1);
                level.setBlock(blockpos1, blockstate1, 11);
                level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
                ItemStack itemstack = context.getItemInHand();
                if (player instanceof ServerPlayer) {
                    CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockpos1, itemstack);
                    itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
                }

                return itemstack;
            } else {
                return stack;
            }
        } else {
            level.playSound(null, blockpos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1, level.getRandom().nextFloat() * 0.4f + 0.8f);
            level.setBlock(blockpos, blockstate2, 11);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockpos);
            context.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));

            return stack;
        }
    }

}
