package dev.saeta.milf.networking;

import dev.saeta.milf.networking.payloads.AnvilHitPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class MILFC2SPackets {

    public static void register(RegisterPayloadHandlersEvent event){
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(AnvilHitPayload.TYPE, AnvilHitPayload.CODEC, AnvilHitPayload.getPayloadHandler());
    }

}
