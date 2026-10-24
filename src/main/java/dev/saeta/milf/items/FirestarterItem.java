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

        BlockHitResult hitResult = FireStartingItem.getHitResult(serverLevel, player);
        BlockPos hitResultBlockPos = FireStartingItem.getBlockPos(hitResult);

        if(hitResultBlockPos != null && FireStartingItem.tryIgnite(serverLevel, hitResultBlockPos)){

            level.playSound(null, hitResultBlockPos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1,1);

            stack.hurtAndBreak(1, serverLevel, player, item -> {
                player.onEquippedItemBroken(item, player.getEquipmentSlotForItem(stack));
            });

            return stack;

        }

        if(!player.getMainHandItem().is(MILFItems.FIRESTARTER)) return stack;

        return FireStartingItem.trySetOnFire(stack, hitResult, player, serverLevel);

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
