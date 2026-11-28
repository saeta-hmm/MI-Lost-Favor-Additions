package dev.saeta.milf.mixins;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.SlotStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CraftingTerminalMenu.class)
public abstract class ArsCraftingTerminalMenuMixin {
    @Shadow
    protected List<SlotStorage> storageSlotList;

    @Inject(method = "init", at = @At("TAIL"))
    private void milf$enlargeStorageGrid(CallbackInfo ci) {
        storageSlotList.clear();
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                storageSlotList.add(new SlotStorage(13 + col * 18, 21 + row * 18, row < 3));
            }
        }
    }
}
