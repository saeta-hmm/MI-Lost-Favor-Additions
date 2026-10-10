package dev.saeta.milf.compat.emi.widgets;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.TextureWidget;
import dev.emi.emi.screen.tooltip.RemainderTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MILFEmiTextureWidget extends TextureWidget {

    private final List<ClientTooltipComponent> tooltipComponents = new ArrayList<>();

    public MILFEmiTextureWidget(EmiTexture emiTexture, int x, int y) {
        super(
                emiTexture.resourceLocation,
                x, y,
                emiTexture.width, emiTexture.height,
                emiTexture.u, emiTexture.v,
                emiTexture.regionWidth, emiTexture.regionHeight,
                emiTexture.textureWidth, emiTexture.textureHeight
        );
    }

    public MILFEmiTextureWidget addEmiStackTooltip(EmiIngredient emiIngredient){
        tooltipComponents.addAll(emiIngredient.getTooltip());
        tooltip((something1, something2) -> tooltipComponents);
        return this;
    }

    public MILFEmiTextureWidget addEmiStackRemainderTooltip(EmiIngredient emiIngredient){
        tooltipComponents.add(new RemainderTooltipComponent(emiIngredient));
        tooltip((something1, something2) -> tooltipComponents);
        return this;
    }
}
