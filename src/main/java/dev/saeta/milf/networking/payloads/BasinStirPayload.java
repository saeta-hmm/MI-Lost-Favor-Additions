package dev.saeta.milf.networking.payloads;


import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.basin.StoneBasinBlockEntity;
import dev.saeta.milf.blocks.shapeable_blocks.AbstractShapeableBlockEntity;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlockEntity;
import dev.saeta.milf.blocks.shapeable_blocks.clay.MoldedBlockEntity;
import dev.saeta.milf.registries.MILFItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record BasinStirPayload(BlockPos pos) implements CustomPacketPayload {

    public final static Type<BasinStirPayload> TYPE = new Type<>(MILostFavor.locate("basin_stir"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BasinStirPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BasinStirPayload::pos,
            BasinStirPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static IPayloadHandler<BasinStirPayload> getPayloadHandler() {
        return (payload, context) -> {

            ServerPlayer player = (ServerPlayer) context.player();
            ServerLevel level = (ServerLevel) player.level();
            BlockEntity blockEntity = level.getBlockEntity(payload.pos);

            if(!(blockEntity instanceof StoneBasinBlockEntity stoneBasinBlockEntity)) return;

            stoneBasinBlockEntity.stir();

        };
    }
}
