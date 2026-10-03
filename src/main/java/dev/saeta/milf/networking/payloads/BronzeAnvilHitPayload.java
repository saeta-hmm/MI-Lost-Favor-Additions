package dev.saeta.milf.networking.payloads;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.bronze_anvil.BronzeAnvilBlockEntity;
import dev.saeta.milf.registries.MILFItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record BronzeAnvilHitPayload(BlockPos pos, float accuracy, float progress, float volume) implements CustomPacketPayload {

    public final static Type<BronzeAnvilHitPayload> TYPE = new Type<>(MILostFavor.locate("bronze_anvil_hit"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BronzeAnvilHitPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BronzeAnvilHitPayload::pos,
            ByteBufCodecs.FLOAT, BronzeAnvilHitPayload::accuracy,
            ByteBufCodecs.FLOAT, BronzeAnvilHitPayload::progress,
            ByteBufCodecs.FLOAT, BronzeAnvilHitPayload::volume,
            BronzeAnvilHitPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static IPayloadHandler<BronzeAnvilHitPayload> getPayloadHandler() {
        return (payload, context) -> {

            ServerPlayer player = (ServerPlayer) context.player();

            ServerLevel level = (ServerLevel) player.level();

            if(level.getBlockEntity(payload.pos) instanceof BronzeAnvilBlockEntity bronzeAnvilBlockEntity){

                bronzeAnvilBlockEntity.handleHit(payload.accuracy, payload.progress, payload.volume);

                var hammer = player.getMainHandItem();

                if(hammer.is(MILFItemTags.HAMMERS)){
                    hammer.hurtAndBreak(1,level, player, item -> {
                        player.onEquippedItemBroken(item, player.getEquipmentSlotForItem(hammer));
                    });
                }
            }
        };
    }


}
