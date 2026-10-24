package dev.saeta.milf.networking.payloads;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.shapeable_blocks.AbstractShapeableBlockEntity;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlockEntity;
import dev.saeta.milf.blocks.shapeable_blocks.clay.MoldedBlockEntity;
import dev.saeta.milf.items.FireStartingItem;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record FireStrikerUsePayload(BlockPos pos) implements CustomPacketPayload {

    public final static Type<FireStrikerUsePayload> TYPE = new Type<>(MILostFavor.locate("fire_striker_use"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FireStrikerUsePayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, FireStrikerUsePayload::pos,
            FireStrikerUsePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static IPayloadHandler<FireStrikerUsePayload> getPayloadHandler() {
        return (payload, context) -> {

            ServerPlayer player = (ServerPlayer) context.player();

            ServerLevel level = (ServerLevel) player.level();

            ItemStack stack = player.getMainHandItem();

            if(stack.is(MILFItems.FIRE_STRIKER)){

                if(FireStartingItem.tryIgnite(level, payload.pos)){
                    stack.hurtAndBreak(1, level, player, item -> {
                        player.onEquippedItemBroken(item, player.getEquipmentSlotForItem(stack));
                    });
                    level.playSound(null, payload.pos.above(), SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1,1);

                    player.setItemInHand(InteractionHand.MAIN_HAND, stack);
                }

            }


        };
    }
}
