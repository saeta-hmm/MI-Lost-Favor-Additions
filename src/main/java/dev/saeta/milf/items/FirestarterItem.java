package dev.saeta.milf.items;

import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

            if(blockEntity == null) return stack;
            if(!(blockEntity instanceof ClayCrucibleBlockEntity clayCrucibleBlockEntity)) return stack;

            if(!clayCrucibleBlockEntity.isFull()) return stack;

            stack.hurtAndBreak(1, serverLevel, player, item -> {
                player.onEquippedItemBroken(item, player.getEquipmentSlotForItem(stack));
            });

            clayCrucibleBlockEntity.setLit(true);

        }

        return  stack;
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
