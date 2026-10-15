package dev.saeta.milf.networking.payloads;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.basin.StoneBasinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record BasinStirSyncPayload(BlockPos pos) implements CustomPacketPayload {

    public final static Type<BasinStirSyncPayload> TYPE = new Type<>(MILostFavor.locate("basin_stir_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BasinStirSyncPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BasinStirSyncPayload::pos,
            BasinStirSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static IPayloadHandler<BasinStirSyncPayload> getPayloadHandler() {
        return (payload, context) -> {

            ServerPlayer player = (ServerPlayer) context.player();
            ServerLevel level = (ServerLevel) player.level();
            BlockEntity blockEntity = level.getBlockEntity(payload.pos);

            if(!(blockEntity instanceof StoneBasinBlockEntity stoneBasinBlockEntity)) return;

            stoneBasinBlockEntity.onFullStir();

        };
    }
}
