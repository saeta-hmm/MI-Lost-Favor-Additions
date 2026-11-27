package dev.saeta.milf.mixins;

import com.rekindled.embers.blockentity.PipeBlockEntityBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "com.rekindled.embers.blockentity.PipeNetworkUtil$PipeStep", remap = false)
public interface PipeStepAccessor {
    @Invoker(value = "pipe", remap = false)
    PipeBlockEntityBase milf$pipe();
}
