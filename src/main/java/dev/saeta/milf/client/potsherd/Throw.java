package dev.saeta.milf.client.potsherd;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.networking.payloads.ThrowPayload;
import dev.saeta.milf.registries.MILFItems;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class Throw {

    private final static int MAX_USE_TICKS = 10;

    private static boolean isRenderingArms = false;
    private static boolean isPrimed = false;

    private static float prepareAnimationProgress = 0;
    private static float prevPrepareAnimationProgress = 0;

    private static int useTicks = 0;
    private static int prevUseTicks = 0;

    private static boolean isRecovering = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event){
        prevPrepareAnimationProgress = prepareAnimationProgress;
        prevUseTicks = useTicks;
        if(isPrimed) {

            prepareAnimationProgress = Mth.approach(prepareAnimationProgress, 1, 0.2f);

        }

        if(useTicks != 0){
            useTicks--;
            if(useTicks == MAX_USE_TICKS / 2){
                prepareAnimationProgress = 0;
            }
        } else if(isRecovering){
            isRecovering = false;
        }

    }

    @SubscribeEvent
    public static void handleClick(InputEvent.MouseButton.Pre event){

        if(event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT ){

            if(event.getAction() == InputConstants.PRESS){

                Minecraft minecraft = Minecraft.getInstance();

                Player player = minecraft.player;
                if (player != null && player.getMainHandItem().is(MILFItems.POTSHERD)) isPrimed = true;


            } else if (event.getAction() == InputConstants.RELEASE){
                if(isPrimed) {
                    isPrimed = false;
                    prepareAnimationProgress = 0;
                }
            }

        }

        if(isPrimed && event.getButton() == InputConstants.MOUSE_BUTTON_LEFT){

            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player == null) return;

            if(!player.getMainHandItem().is(MILFItems.POTSHERD)) {
                isPrimed = false;
                prepareAnimationProgress = 0;
                return;
            }

            if(event.getAction() == InputConstants.RELEASE) {
                event.setCanceled(true);
            } else {

                if(useTicks == 0){
                    useTicks = MAX_USE_TICKS;

                    PacketDistributor.sendToServer(new ThrowPayload());
                    isRecovering = true;

                }

                event.setCanceled(true);
            }


        }

    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {

        if(isRenderingArms) return;

        if(isPrimed) {



            event.setCanceled(true);

            isRenderingArms = true;

            try{
                renderHands(
                        event.getItemStack(),
                        event.getPoseStack(),
                        event.getMultiBufferSource(),
                        event.getPackedLight()
                );
            } finally {
                isRenderingArms = false;
            }



        }

    }

    private static void renderHands(ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight){


        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if(player == null) return;

        EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();


        if(!(dispatcher.getRenderer(player) instanceof PlayerRenderer playerRenderer)) return;
        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        poseStack.pushPose();

        float useProgress = (float) useTicks / MAX_USE_TICKS;
        float prevUseProgress = (float) prevUseTicks / MAX_USE_TICKS;

        float interpolatedUseProgress = Mth.lerp(minecraft.getTimer().getGameTimeDeltaPartialTick(true), prevUseProgress, useProgress);
        float easedUseProgress = - RenderUtil.EasingFunctions.easeInOutExpo(interpolatedUseProgress);

        float interpolatedPrepareAnimationProgress = Mth.lerp(minecraft.getTimer().getGameTimeDeltaPartialTick(true), prevPrepareAnimationProgress, prepareAnimationProgress);
        float easedPrepareAnimationProgress = RenderUtil.EasingFunctions.easeInQuadOutExpo(interpolatedPrepareAnimationProgress);

        poseStack.translate(0.74 - 0.2 * easedPrepareAnimationProgress, -0.3 + 0.3 * easedPrepareAnimationProgress, -0.18);
        poseStack.mulPose(Axis.ZP.rotationDegrees(45* easedPrepareAnimationProgress));

        poseStack.mulPose(Axis.XP.rotationDegrees(-180 + 90 * easedPrepareAnimationProgress));

        poseStack.mulPose(Axis.XP.rotationDegrees(35 * easedUseProgress));
        poseStack.mulPose(Axis.ZP.rotationDegrees(25 * easedUseProgress));

        playerRenderer.renderRightHand(
                poseStack,
                multiBufferSource,
                packedLight,
                player
        );

        if(useTicks != 0) return;

        poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        poseStack.translate(0.1, 0.6, 0.4);

        poseStack.mulPose(Axis.ZP.rotationDegrees(-35));

        itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                packedLight,
                109,
                poseStack,
                multiBufferSource,
                player.level(),
                0
        );

        poseStack.popPose();

    }

}
