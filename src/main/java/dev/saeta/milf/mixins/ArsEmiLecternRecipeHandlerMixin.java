package dev.saeta.milf.mixins;

import com.hollingsworth.arsnouveau.client.emi.EmiLecternRecipeHandler;
import com.hollingsworth.arsnouveau.common.network.ClientTransferHandlerPacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.registry.EmiRecipeFiller;
import net.minecraft.world.item.ItemStack;
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
        Networking.sendToServer(new ClientTransferHandlerPacket(inputs));
        cir.setReturnValue(true);
    }
}
