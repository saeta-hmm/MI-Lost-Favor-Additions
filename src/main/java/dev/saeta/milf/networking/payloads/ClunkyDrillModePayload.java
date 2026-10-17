package dev.saeta.milf.networking.payloads;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.items.mi.ClunkyDrillItem;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record ClunkyDrillModePayload() implements CustomPacketPayload {

    public final static ClunkyDrillModePayload INSTANCE = new ClunkyDrillModePayload();

    public final static Type<ClunkyDrillModePayload> TYPE = new Type<>(MILostFavor.locate("clunky_drill_mode"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClunkyDrillModePayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static IPayloadHandler<ClunkyDrillModePayload> getPayloadHandler() {
        return (payload, context) -> {

            ServerPlayer player = (ServerPlayer) context.player();
            ServerLevel level = (ServerLevel) player.level();

            ItemStack itemStack = player.getMainHandItem();

            if(itemStack.getItem() instanceof ClunkyDrillItem clunkyDrillItem){

                itemStack.set(MILFDataComponents.IS_HORIZONTAL, !itemStack.getOrDefault(MILFDataComponents.IS_HORIZONTAL, false));

            }
        };
    }


}
