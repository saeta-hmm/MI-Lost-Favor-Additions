package dev.saeta.milf.compat.emi.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetTooltipHolder;
import dev.saeta.milf.MILostFavor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class FloatTextWidget extends Widget implements WidgetTooltipHolder<FloatTextWidget> {

    protected final static ResourceLocation NUMBERS_TEXTURE = MILostFavor.locate("textures/gui/numbers.png");

    protected final int x, y, color;
    protected final Supplier<Float> floatSupplier;

    protected boolean stripIntegerPart = false;
    protected boolean onlyPercentages = false;

    private BiFunction<Integer, Integer, List<ClientTooltipComponent>> tooltipSupplier = (mouseX, mouseY) -> List.of();


    public FloatTextWidget(int x, int y, int color, Supplier<Float> floatSupplier) {

        this.x = x;
        this.y = y;
        this.color = color;
        this.floatSupplier = floatSupplier;

    }

    public FloatTextWidget stripIntegerPart(){
        stripIntegerPart = true;
        return this;
    }

    public FloatTextWidget onlyPercentages(){
        onlyPercentages = true;
        return this;
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(x-1, y-1, getWidth()+2, 7);
    }

    private int getWidth(){
        float number = floatSupplier.get();

        String numberString = String.valueOf(number);

        if(stripIntegerPart){
            numberString = numberString.substring(numberString.indexOf('.'));
        } else if(onlyPercentages){
            numberString = numberString.substring(numberString.indexOf('.') + 1);
        }

        return numberString.length() * 4;
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        float number = floatSupplier.get();

        String numberString = String.valueOf(number);

        //MILostFavor.LOGGER.info(numberString);

        if(stripIntegerPart){
            numberString = numberString.substring(numberString.indexOf('.'));
        } else if(onlyPercentages){
            numberString = numberString.substring(numberString.indexOf('.') + 1);
        }

        //MILostFavor.LOGGER.info(string);

        int drawX = x;

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        RenderSystem.setShaderColor(r,g,b,a);

        for (int i = 0; i < numberString.length(); i++) {
            char c = numberString.charAt(i);

            int index;
            if (c == '.') {
                index = 10;
            } else if (c >= '0' && c <= '9') {
                index = c - '0' - 1;
            } else {
                continue;
            }

            int u = 1 + index * 4;
            draw.blit(NUMBERS_TEXTURE, drawX, y, u, 1, 3, 5, 45, 7);
            drawX += (c == '.') ? 3 : 4;
        }

        RenderSystem.setShaderColor(1,1,1,1);
    }

    @Override
    public FloatTextWidget tooltip(BiFunction<Integer, Integer, List<ClientTooltipComponent>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        return tooltipSupplier.apply(mouseX, mouseY);
    }
}
