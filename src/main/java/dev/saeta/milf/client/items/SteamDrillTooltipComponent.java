package dev.saeta.milf.client.items;

import aztech.modern_industrialization.client.machines.gui.MachineScreen;
import aztech.modern_industrialization.client.machines.guicomponents.ProgressBarClient;
import aztech.modern_industrialization.client.util.RenderHelper;
import aztech.modern_industrialization.machines.guicomponents.ProgressBar;
import dev.saeta.milf.items.mi.MILFSteamDrillTooltipData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

//https://github.com/AztechMC/Modern-Industrialization/blob/1.21.x/src/client/java/aztech/modern_industrialization/client/items/SteamDrillTooltipComponent.java (‾◡◝)

public class SteamDrillTooltipComponent implements ClientTooltipComponent {
    final MILFSteamDrillTooltipData data;

    public SteamDrillTooltipComponent(MILFSteamDrillTooltipData data) {
        this.data = data;
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getWidth(Font textRenderer) {
        return 40;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        // Slot background
        guiGraphics.blit(MachineScreen.SLOT_ATLAS, x, y, 0, 0, 18, 18, 256, 256);
        // Stack itself
        RenderHelper.renderAndDecorateItem(guiGraphics, font, data.variant().toStack((int) data.amount()), x + 1, y + 1);
        // Burning flame next to the stack
        var progressParams = new ProgressBar.Params(0, 0, "furnace", 14, 14, true);
        ProgressBarClient.renderProgress(guiGraphics, x + 20, y, progressParams, (float) data.burnTicks() / data.maxBurnTicks());
    }
}
