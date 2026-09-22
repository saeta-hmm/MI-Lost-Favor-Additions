package dev.saeta.milf.items.mi;

import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record MILFSteamDrillTooltipData(
        int waterLevel,
        int burnTicks,
        int maxBurnTicks,
        ItemVariant variant,
        long amount
) implements TooltipComponent {}
