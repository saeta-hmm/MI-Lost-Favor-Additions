package dev.saeta.milf.networking.payloads;

import dev.saeta.milf.MILostFavor;
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

public record ShapeableHitPayload(BlockPos pos, Direction face) implements CustomPacketPayload {

    public final static Type<ShapeableHitPayload> TYPE = new Type<>(MILostFavor.locate("shapeable_hit"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapeableHitPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ShapeableHitPayload::pos,
            Direction.STREAM_CODEC, ShapeableHitPayload::face,
            ShapeableHitPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static IPayloadHandler<ShapeableHitPayload> getPayloadHandler() {
        return (payload, context) -> {

            ServerPlayer player = (ServerPlayer) context.player();

            ServerLevel level = (ServerLevel) player.level();

            BlockEntity blockEntity = level.getBlockEntity(payload.pos);


            if(!(blockEntity instanceof AbstractShapeableBlockEntity abstractShapeableBlockEntity)) return;

            abstractShapeableBlockEntity.handleHit(payload.face, player);

            switch (level.getBlockEntity(payload.pos)){
                case ChiseledBlockEntity chiseledBlockEntity -> {
                    var hammer = player.getOffhandItem();

                    if (hammer.is(MILFItemTags.HAMMERS)) {
                        hammer.hurtAndBreak(1, level, player, item -> {
                            player.onEquippedItemBroken(item, player.getEquipmentSlotForItem(hammer));
                        });
                    }

                    var chisel = player.getMainHandItem();

                    if (chisel.is(MILFItemTags.CHISELS)) {
                        chisel.hurtAndBreak(1, level, player, item -> {
                            player.onEquippedItemBroken(item, player.getEquipmentSlotForItem(chisel));
                        });
                    }
                }

                case MoldedBlockEntity moldedBlockEntity -> {
                }


                case null -> {}
                default -> {}
            }

        };
    }
}

