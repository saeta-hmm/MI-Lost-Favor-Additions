package dev.saeta.milf.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(targets = "com.rekindled.embers.blockentity.PipeNetworkUtil$FluidTarget", remap = false)
public interface FluidTargetPathAccessor {
    @Invoker(value = "path", remap = false)
    List<?> milf$path();
}
