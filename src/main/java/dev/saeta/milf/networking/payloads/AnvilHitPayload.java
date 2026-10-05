package dev.saeta.milf.networking.payloads;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.anvils.AbstractAnvilBlockEntity;
import dev.saeta.milf.registries.MILFItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record AnvilHitPayload(BlockPos pos, float accuracy, float progress, float volume) implements CustomPacketPayload {

    public final static Type<AnvilHitPayload> TYPE = new Type<>(MILostFavor.locate("anvil_hit"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnvilHitPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, AnvilHitPayload::pos,
            ByteBufCodecs.FLOAT, AnvilHitPayload::accuracy,
            ByteBufCodecs.FLOAT, AnvilHitPayload::progress,
            ByteBufCodecs.FLOAT, AnvilHitPayload::volume,
            AnvilHitPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static IPayloadHandler<AnvilHitPayload> getPayloadHandler() {
        return (payload, context) -> {

            ServerPlayer player = (ServerPlayer) context.player();

            ServerLevel level = (ServerLevel) player.level();

            if(level.getBlockEntity(payload.pos) instanceof AbstractAnvilBlockEntity anvilBlockEntity){

                anvilBlockEntity.handleHit(payload.accuracy, payload.progress, payload.volume);

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
