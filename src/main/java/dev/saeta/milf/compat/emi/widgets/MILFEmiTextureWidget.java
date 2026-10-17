package dev.saeta.milf.compat.emi.widgets;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.TextureWidget;
import dev.emi.emi.screen.tooltip.RemainderTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.util.ArrayList;
import java.util.List;

public class MILFEmiTextureWidget extends TextureWidget {

    private final List<ClientTooltipComponent> tooltipComponents = new ArrayList<>();
    private final List<TooltipNoRenderBounds> boundsList = new ArrayList<>();

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
        setTooltip();
        return this;
    }

    public MILFEmiTextureWidget addEmiStackRemainderTooltip(EmiIngredient emiIngredient){
        tooltipComponents.add(new RemainderTooltipComponent(emiIngredient));
        setTooltip();
        return this;
    }

    public MILFEmiTextureWidget addTooltipRenderBounds(TooltipNoRenderBounds... bounds){
        boundsList.addAll(List.of(bounds));
        return this;
    }

    private void setTooltip(){
        tooltip((mouseX, mouseY) -> {
            if(!boundsList.isEmpty()){
                for(TooltipNoRenderBounds bounds : boundsList){
                    if(isWithinBounds(mouseX, mouseY, bounds)) return List.of();
                }
            }

            return tooltipComponents;
        });
    }

    private boolean isWithinBounds(int x, int y, TooltipNoRenderBounds bounds){
        return x >= bounds.minX && x <= bounds.maxX && y >= bounds.minY && y <= bounds.maxY;
    }

    public record TooltipNoRenderBounds(int minX, int minY, int maxX, int maxY){

        public static TooltipNoRenderBounds ofSlot(MILFEmiSlotWidget slotWidget){
            return new TooltipNoRenderBounds(
                    slotWidget.getX() -1,
                    slotWidget.getY() -1,
                    slotWidget.getX() + slotWidget.getWidth(),
                    slotWidget.getY() + slotWidget.getHeight()
            );
        }

    }
}
