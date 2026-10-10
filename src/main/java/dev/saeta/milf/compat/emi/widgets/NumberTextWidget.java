package dev.saeta.milf.compat.emi.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetTooltipHolder;
import dev.saeta.milf.MILostFavor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class NumberTextWidget extends Widget implements WidgetTooltipHolder<NumberTextWidget> {

    protected final static ResourceLocation NUMBERS_TEXTURE = MILostFavor.locate("textures/gui/numbers.png");

    protected final int color;
    protected final float x, y;
    protected final Supplier<Number> numberSupplier;

    protected boolean stripIntegerPart = false;
    protected boolean onlyPercentages = false;
    protected boolean isCentered = false;

    private BiFunction<Integer, Integer, List<ClientTooltipComponent>> tooltipSupplier = (mouseX, mouseY) -> List.of();


    public NumberTextWidget(float x, float y, int color, Supplier<Number> numberSupplier) {

        this.x = x;
        this.y = y;
        this.color = color;
        this.numberSupplier = numberSupplier;

    }

    public NumberTextWidget stripIntegerPart(){
        stripIntegerPart = true;
        return this;
    }

    public NumberTextWidget onlyPercentages(){
        onlyPercentages = true;
        return this;
    }

    public NumberTextWidget centered(){
        isCentered = true;
        return this;
    }

    @Override
    public Bounds getBounds() {
        if(isCentered){
            int width = getWidth();
            return new Bounds((int) (x- (float) width /2), (int) (y-1), width, 7);
        }
        return new Bounds((int) (x-1), (int) (y-1), getWidth()+2, 7);
    }

    private int getWidth(){
        Number number = numberSupplier.get();

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
        Number number = numberSupplier.get();

        double value = number.doubleValue();

        String numberString;
        if (value == Math.rint(value)) {
            numberString = String.valueOf(value);
        } else {
            numberString = String.format(Locale.ROOT, "%.2f", value);
        }

        if(stripIntegerPart){
            numberString = numberString.substring(numberString.indexOf('.'));
        } else if(onlyPercentages){
            numberString = numberString.substring(numberString.indexOf('.') + 1);
        }

        float drawX = isCentered ? x - (float) getWidth() / 2 : x;

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        RenderSystem.setShaderColor(r,g,b,a);

        PoseStack poseStack = draw.pose();



        for (int i = 0; i < numberString.length(); i++) {
            char c = numberString.charAt(i);

            int index;
            if (c == '.') {
                index = 10;
            } else if (c >= '1' && c <= '9') {
                index = c - '1';
            } else if (c == '0'){
                index = 9;
            } else  {
                continue;
            }

            poseStack.pushPose();

            poseStack.translate(drawX, y, 0);

            int u = 1 + index * 4;
            draw.blit(NUMBERS_TEXTURE, 0, 0, u, 1, 3, 5, 45, 7);
            drawX += (c == '.') ? 3 : 4;

            poseStack.popPose();
        }



        RenderSystem.setShaderColor(1,1,1,1);
    }

    @Override
    public NumberTextWidget tooltip(BiFunction<Integer, Integer, List<ClientTooltipComponent>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        return tooltipSupplier.apply(mouseX, mouseY);
    }
}
