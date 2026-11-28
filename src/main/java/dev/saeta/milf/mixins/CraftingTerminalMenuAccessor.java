package dev.saeta.milf.mixins;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.SlotStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CraftingTerminalMenu.class)
public interface CraftingTerminalMenuAccessor {
    @Accessor("storageSlotList")
    List<SlotStorage> milf$storageSlotList();
}
