package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MILFSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MILostFavor.MOD_ID);

    public static final SoundEvent TICK = register("tick");

    private static SoundEvent register(String name) {

        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(MILostFavor.locate(name));
        SOUND_EVENTS.register(name, () -> soundEvent);
        return soundEvent;
    }

    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }

}
