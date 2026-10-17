package dev.saeta.milf.client.notifications;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public class Notification {

    private static final int X_OFFSET = -20;
    private static final int Y_OFFSET = -20;
    private static final int SPRITE_PADDING = 4;
    private static final ResourceLocation NOTIFICATION_9_SLICE = MILostFavor.locate("notification_sprite");

    private final int lifespan;
    private final Component text;
    private int age;
    private int prevAge;

    private float inFloat = 0.25f;
    private float outFloat = 0.25f;

    private boolean didPlayOutSound = false;

    public Notification(Component text, int lifespan) {
        this.text = text;
        this.lifespan = lifespan;

    }

    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, int index){

        Minecraft minecraft = Minecraft.getInstance();

        PoseStack poseStack = guiGraphics.pose();
        Font font = minecraft.font;

        int notificationWidth = font.width(text);
        int notificationHeight = font.lineHeight;

        poseStack.pushPose();

        poseStack.translate(
                guiGraphics.guiWidth() + X_OFFSET - notificationWidth - SPRITE_PADDING,
                guiGraphics.guiHeight() + Y_OFFSET - notificationHeight - (index * 16),
                0
        );



        float progress = (float) age / lifespan;
        float prevProgress = (float) prevAge / lifespan;
        float interpolatedProgress = Mth.lerp(deltaTracker.getGameTimeDeltaPartialTick(true), prevProgress, progress);

        if(interpolatedProgress <= inFloat){
            float subProgress = interpolatedProgress * (1f/ inFloat);
            float easedProgress = RenderUtil.EasingFunctions.easeInOutQuad(subProgress);

            poseStack.translate((notificationWidth - X_OFFSET * 1.5) * (1 - easedProgress), 0, 0);

        } else if (interpolatedProgress >= 1- outFloat){

            float subProgress = (1 - interpolatedProgress) * (1f/ outFloat);
            float easedProgress = RenderUtil.EasingFunctions.easeInOutQuad(subProgress);

            poseStack.translate((notificationWidth - X_OFFSET * 1.5) * (1 - easedProgress), 0, 0);


        } else {

            //poseStack.translate(-20, 0, 0);

        }
        guiGraphics.blitSprite(NOTIFICATION_9_SLICE, -SPRITE_PADDING,0, notificationWidth + SPRITE_PADDING*2, 14);

        guiGraphics.drawString(font, text, 0,(16-notificationHeight)/2, 0xfbfbfc);

        poseStack.popPose();


    }

    public void tick(){
        prevAge = age;
        age++;
        if((float) age / lifespan >= (1-outFloat)){
            if(!didPlayOutSound) playOutSound();
        }
    }

    public boolean canRemove(){
        return age >= lifespan;
    }

    public boolean tryRemove(){
        if(canRemove()){
            onRemove();
            return true;
        }
        return false;
    }

    private void playOutSound(){
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null) player.playSound(SoundEvents.UI_TOAST_OUT);
        didPlayOutSound = true;
    }

    public void onRemove(){

    }

    public void onPush(){
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null) player.playSound(SoundEvents.UI_TOAST_IN);
    }

}
