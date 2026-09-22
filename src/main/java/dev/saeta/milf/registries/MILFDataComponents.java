package dev.saeta.milf.registries;

import com.mojang.serialization.Codec;
import dev.saeta.milf.MILostFavor;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MILFDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MILostFavor.MOD_ID);

    public static DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID = DATA_COMPONENTS.register(
            "fluid", () -> DataComponentType.<SimpleFluidContent>builder()
                    .persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
                    .build()
    );

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_HORIZONTAL = DATA_COMPONENTS.register(
            "is_horizontal", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );

    public static void register(IEventBus eventBus){
        DATA_COMPONENTS.register(eventBus);
    }
}
