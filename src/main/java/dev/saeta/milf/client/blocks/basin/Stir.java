package dev.saeta.milf.client.blocks.basin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.basin.StoneBasinBlockEntity;
import dev.saeta.milf.networking.payloads.BasinStirPayload;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class Stir {

    private final static int MAX_USE_TICKS = 10;
    private final static int MAX_STIR_TICKS = 10;

    private static boolean isRenderingArms = false;
    private static boolean isPrimeToStir = false;
    private static boolean isStirring = false;

    private static float leftHandRotationProgress = 0;
    private static float prevLeftHandRotationProgress = 0;


    private static int stirTicks = 0;
    private static int prevStirTicks = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event){
        prevLeftHandRotationProgress = leftHandRotationProgress;
        prevStirTicks = stirTicks;
        if(isPrimeToStir) {

            leftHandRotationProgress = Mth.approach(leftHandRotationProgress, 1, 0.2f);

        }


        if(isStirring){
            stirTicks++;

            if(stirTicks >= MAX_STIR_TICKS){
                prevStirTicks=0;
                stirTicks = 0;
                stir();
                if(!canStir()) isStirring = false;
            }

        }
    }

    @SubscribeEvent
    public static void handleClick(InputEvent.MouseButton.Pre event){

        if(event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT ){

            if(event.getAction() == InputConstants.PRESS){
                BlockPos basinBlockPos = getBasinBlockPos();

                if(basinBlockPos!= null ) {

                    Minecraft minecraft = Minecraft.getInstance();

                    assert minecraft.player != null;
                    if (minecraft.player.getMainHandItem().isEmpty() && minecraft.player.getOffhandItem().isEmpty()) isPrimeToStir = true;

                }
            } else if (event.getAction() == InputConstants.RELEASE){
                if(isPrimeToStir) {
                    isPrimeToStir = false;
                    isStirring = false;
                    stirTicks= 0;
                    leftHandRotationProgress = 0;
                }
            }


        }

        if(isPrimeToStir && event.getButton() == InputConstants.MOUSE_BUTTON_LEFT){

            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player == null) return;

            if(!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) {
                isPrimeToStir = false;
                leftHandRotationProgress = 0;
                return;
            }

            if(event.getAction() == InputConstants.RELEASE) {
                isStirring = false;
                stirTicks = 0;
                //prevStirTicks = 0;
                event.setCanceled(true);
            } else {
                isStirring = true;
                event.setCanceled(true);
            }


        }

    }

    private static boolean canStir(){
        BlockPos basinPos = getBasinBlockPos();
        if(basinPos != null && Minecraft.getInstance().level.getBlockEntity(basinPos) instanceof StoneBasinBlockEntity stoneBasinBlockEntity) {
            return stoneBasinBlockEntity.canStir();
        }
        return false;
    }

    private static void stir(){
        Minecraft minecraft = Minecraft.getInstance();
        HitResult hitResult = minecraft.hitResult;

        if(hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() != HitResult.Type.MISS){
            Level level= minecraft.level;
            BlockPos pos = blockHitResult.getBlockPos();

            if (level.getBlockEntity(pos) instanceof StoneBasinBlockEntity stoneBasinBlockEntity) {
                stoneBasinBlockEntity.stir();
                PacketDistributor.sendToServer(new BasinStirPayload(pos));
            }
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderArmEvent event) {

        if(isRenderingArms) return;

        if(isPrimeToStir) {

            event.setCanceled(true);

            isRenderingArms = true;

            try{
                renderHands(
                        event.getPoseStack(),
                        event.getMultiBufferSource(),
                        event.getPackedLight()
                );
            } finally {
                isRenderingArms = false;
            }



        }

    }

    private static void renderHands(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight){

        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if(player == null) return;

        EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();

        if(!(dispatcher.getRenderer(player) instanceof PlayerRenderer playerRenderer)) return;

        poseStack.pushPose();

        float prevStirProgress = (float) prevStirTicks / MAX_STIR_TICKS;
        float stirProgress = (float) stirTicks / MAX_STIR_TICKS;
        float interpolatedStirProgress = Mth.lerp(minecraft.getTimer().getGameTimeDeltaPartialTick(true), prevStirProgress, stirProgress);
        float stirProgress010 = RenderUtil.mapProgressTo010(interpolatedStirProgress);
        float easedStirProgress = RenderUtil.EasingFunctions.easeInOutExpo(stirProgress010);

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(-20 * easedStirProgress));

        poseStack.mulPose(Axis.ZP.rotationDegrees(-15 * easedStirProgress));
        poseStack.mulPose(Axis.XP.rotationDegrees(-5 * easedStirProgress));
        poseStack.translate(0, 0.13 * easedStirProgress, 0);

        playerRenderer.renderRightHand(
                poseStack,
                multiBufferSource,
                packedLight,
                player
        );

        poseStack.popPose();


        float interpolatedLHandProgress = Mth.lerp(minecraft.getTimer().getGameTimeDeltaPartialTick(true), prevLeftHandRotationProgress, leftHandRotationProgress);
        float easedLHandProgress = RenderUtil.EasingFunctions.easeInQuadOutExpo(interpolatedLHandProgress);

        poseStack.translate(0.07, -0.4, -0.7);

        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90 * easedLHandProgress));
        poseStack.mulPose(Axis.YP.rotationDegrees(80 * easedLHandProgress));

        poseStack.mulPose(Axis.ZP.rotationDegrees(-5));
        poseStack.mulPose(Axis.YP.rotationDegrees(-15));
        poseStack.mulPose(Axis.XP.rotationDegrees(-15));

        //use

        float leftHandStirProgress = 1 - easedStirProgress;


        poseStack.mulPose(Axis.ZP.rotationDegrees(15 * leftHandStirProgress));
        poseStack.mulPose(Axis.XP.rotationDegrees(5 * leftHandStirProgress));
        poseStack.translate(0, 0.16 * leftHandStirProgress, -0.11 * leftHandStirProgress);

        poseStack.mulPose(Axis.YP.rotationDegrees(-22 * leftHandStirProgress));

        playerRenderer.renderLeftHand(
                poseStack,
                multiBufferSource,
                packedLight,
                player
        );



        poseStack.popPose();

    }

    private static BlockPos getBasinBlockPos(){
        Minecraft minecraft = Minecraft.getInstance();

        if(minecraft.level == null || minecraft.player == null || minecraft.screen != null ) return null;

        HitResult hitResult = minecraft.hitResult;

        if(hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() != HitResult.Type.MISS){
            Level level= minecraft.level;
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState blockState = level.getBlockState(pos);

            if (blockState.is(MILFBlocks.STONE_BASIN)) {
                return pos;
            }
        }

        return null;
    }
}
