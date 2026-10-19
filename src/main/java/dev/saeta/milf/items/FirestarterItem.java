package dev.saeta.milf.items;

import dev.saeta.milf.blocks.FlammableBlockEntity;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class FirestarterItem extends Item {
    public FirestarterItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!(level instanceof ServerLevel serverLevel)) return stack;

        if(!(livingEntity instanceof Player player)) return stack;

        double reachDistance = player.blockInteractionRange();

        BlockHitResult hitResult = level.clip(new ClipContext(
                player.getEyePosition(),
                player.getEyePosition().add(player.getViewVector(1).scale(reachDistance)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));

        if(hitResult.getType() == HitResult.Type.BLOCK){
            BlockPos blockPos = hitResult.getBlockPos();
            BlockEntity blockEntity = level.getBlockEntity(blockPos);

            if(blockEntity instanceof FlammableBlockEntity flammableBlockEntity){

                if(!flammableBlockEntity.canBeIgnited()) return stack;

                stack.hurtAndBreak(1, serverLevel, player, item -> {
                    player.onEquippedItemBroken(item, player.getEquipmentSlotForItem(stack));
                });

                level.playSound(null, blockPos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1,1);

                flammableBlockEntity.ignite();

                return stack;
            }


        }

        if(!player.getMainHandItem().is(MILFItems.FIRESTARTER)) return stack;

        //totally not FlintAndSteelItem.useOn() (‾◡◝)

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

    @Override
    public boolean canPerformAction(ItemStack stack, net.neoforged.neoforge.common.ItemAbility itemAbility) {
        return net.neoforged.neoforge.common.ItemAbilities.DEFAULT_FLINT_ACTIONS.contains(itemAbility);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 50;
    }
}
