package dev.saeta.milf.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.rekindled.embers.blockentity.FluidPipeBlockEntityBase;
import com.rekindled.embers.blockentity.FluidTransferBlockEntity;
import com.rekindled.embers.blockentity.PipeBlockEntityBase;
import com.rekindled.embers.blockentity.PipeNetworkUtil;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = PipeNetworkUtil.class, remap = false)
public abstract class PipeNetworkUtilFluidFilterMixin {
    @ModifyExpressionValue(
            method = "routeFluid",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/rekindled/embers/blockentity/PipeNetworkUtil;findFluidTargets(Lcom/rekindled/embers/blockentity/FluidPipeBlockEntityBase;Lnet/minecraft/core/Direction;)Ljava/util/List;"
            )
    )
    private static List<?> milf$filterFluidTargets(List<?> targets, FluidPipeBlockEntityBase origin, Direction blockedFirstSide, FluidStack stack, FluidAction action) {
        List<Object> accepted = new ArrayList<>(targets.size());
        for (Object target : targets) {
            if (milf$pathAcceptsFluid(((FluidTargetPathAccessor) target).milf$path(), stack)) {
                accepted.add(target);
            }
        }
        return accepted.size() == targets.size() ? targets : accepted;
    }

    @Unique
    private static boolean milf$pathAcceptsFluid(List<?> path, FluidStack stack) {
        for (Object step : path) {
            PipeBlockEntityBase pipe = ((PipeStepAccessor) step).milf$pipe();
            if (pipe instanceof FluidTransferBlockEntity transfer && !milf$acceptsFluid(transfer, stack)) {
                return false;
            }
        }
        return true;
    }

    @Unique
    private static boolean milf$acceptsFluid(FluidTransferBlockEntity transfer, FluidStack stack) {
        FluidStack filter = transfer.filterFluid;
        if (filter.isEmpty()) {
            return true;
        }
        return filter.isComponentsPatchEmpty()
                ? FluidStack.isSameFluid(stack, filter)
                : FluidStack.isSameFluidSameComponents(stack, filter);
    }
}
