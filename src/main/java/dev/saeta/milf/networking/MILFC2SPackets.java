package dev.saeta.milf.networking;

import dev.saeta.milf.networking.payloads.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class MILFC2SPackets {

    public static void register(RegisterPayloadHandlersEvent event){
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(AnvilHitPayload.TYPE, AnvilHitPayload.CODEC, AnvilHitPayload.getPayloadHandler());

        registrar.playToServer(ShapeableHitPayload.TYPE, ShapeableHitPayload.CODEC, ShapeableHitPayload.getPayloadHandler());

        registrar.playToServer(ShapingStartPayload.TYPE, ShapingStartPayload.CODEC, ShapingStartPayload.getPayloadHandler());

        registrar.playToServer(BasinStirPayload.TYPE, BasinStirPayload.CODEC, BasinStirPayload.getPayloadHandler());

        registrar.playToServer(BasinStirSyncPayload.TYPE, BasinStirSyncPayload.CODEC, BasinStirSyncPayload.getPayloadHandler());

        registrar.playToServer(ThrowPayload.TYPE, ThrowPayload.CODEC, ThrowPayload.getPayloadHandler());

        registrar.playToServer(ClunkyDrillModePayload.TYPE, ClunkyDrillModePayload.CODEC, ClunkyDrillModePayload.getPayloadHandler());

        registrar.playToServer(FireStrikerUsePayload.TYPE, FireStrikerUsePayload.CODEC, FireStrikerUsePayload.getPayloadHandler());

    }

}
