package dev.saeta.milf.client.render_stuff;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.saeta.milf.MILostFavor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.function.Supplier;

public class GenericNumberTextWidget {

    protected final static ResourceLocation NUMBERS_TEXTURE = MILostFavor.locate("textures/gui/numbers.png");

    protected final int color;

    protected boolean stripIntegerPart = false;
    protected boolean onlyPercentages = false;
    protected boolean isCentered = false;

    public GenericNumberTextWidget(int color) {
        this.color = color;

    }

    public GenericNumberTextWidget stripIntegerPart(){
        stripIntegerPart = true;
        return this;
    }

    public GenericNumberTextWidget onlyPercentages(){
        onlyPercentages = true;
        return this;
    }

    public GenericNumberTextWidget centered(){
        isCentered = true;
        return this;
    }

    private int getWidth(Number number){

        String numberString = format(number);

        if(stripIntegerPart){
            numberString = numberString.substring(numberString.indexOf('.'));
        } else if(onlyPercentages){
            numberString = numberString.substring(numberString.indexOf('.') + 1);
        }

        return numberString.length() * 4;
    }

    private String format(Number number){

        double value = number.doubleValue();

        String numberString;
        if (value == Math.rint(value)) {
            numberString = String.format(Locale.ROOT, "%.0f", value);
        } else {
            numberString = String.format(Locale.ROOT, "%.2f", value);
        }

        if(stripIntegerPart){
            numberString = numberString.substring(numberString.indexOf('.'));
        } else if(onlyPercentages){
            numberString = numberString.substring(numberString.indexOf('.') + 1);
        }

        return numberString;


    }

    public void render(int x, int y, GuiGraphics guiGraphics, float delta, Number number) {

        String numberString = format(number);

        float drawX = isCentered ? x - (float) getWidth(number) / 2 : x;

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        guiGraphics.setColor(r,g,b,a);

        PoseStack poseStack = guiGraphics.pose();


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
            guiGraphics.blit(NUMBERS_TEXTURE, 0, 0, 3, 7, u, 1, 3, 7, 45, 7);
            drawX += (c == '.') ? 3 : 4;

            poseStack.popPose();
        }

        guiGraphics.setColor(1,1,1,1);
    }

}
