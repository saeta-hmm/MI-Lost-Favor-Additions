package dev.saeta.milf.mixins;

import com.hollingsworth.arsnouveau.client.emi.EmiLecternRecipeHandler;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.registry.EmiRecipeFiller;
import dev.saeta.milf.networking.payloads.LecternCraftPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(EmiLecternRecipeHandler.class)
public abstract class ArsEmiLecternRecipeHandlerMixin {
    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    private void milf$craftWithNativeSelection(EmiRecipe recipe, EmiCraftContext<?> context, CallbackInfoReturnable<Boolean> cir) {
        int destination = switch (context.getDestination()) {
            case CURSOR -> LecternCraftPayload.CURSOR;
            case INVENTORY -> LecternCraftPayload.INVENTORY;
            case NONE -> LecternCraftPayload.FILL;
        };
        if (destination != LecternCraftPayload.FILL && !context.getScreenHandler().getCarried().isEmpty()) {
            cir.setReturnValue(false);
            return;
        }
        @SuppressWarnings({"rawtypes", "unchecked"})
        List<ItemStack> selected = EmiRecipeFiller.getStacks((StandardRecipeHandler) (Object) this, recipe, context.getScreen(), context.getAmount());
        if (selected == null) {
            cir.setReturnValue(false);
            return;
        }
        List<List<ItemStack>> inputs = new ArrayList<>(selected.size());
        for (ItemStack stack : selected) {
            inputs.add(stack.isEmpty() ? List.of() : List.of(stack));
        }
        PacketDistributor.sendToServer(new LecternCraftPayload(
                context.getScreenHandler().containerId,
                destination,
                context.getAmount() == Integer.MAX_VALUE,
                inputs));
        cir.setReturnValue(true);
    }
}
