package dev.saeta.milf.client.overlay;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.anvils.AbstractAnvilBlockEntity;
import dev.saeta.milf.blocks.anvils.bronze_anvil.BronzeAnvilBlockEntity;
import dev.saeta.milf.blocks.anvils.stone_anvil.StoneAnvilBlockEntity;
import dev.saeta.milf.networking.payloads.AnvilHitPayload;
import dev.saeta.milf.registries.MILFBlockTags;
import dev.saeta.milf.registries.MILFItemTags;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AnvilMinigame {

    public final static ResourceLocation ID = MILostFavor.locate("anvil_minigame");

    private static ResourceLocation guiTexture = MILostFavor.locate("textures/gui/bronze_anvil_gui.png");
    private static float maxHit = 0.35f;

    private static int ticks = 0;
    private static int nextMarkerCooldown = 3;
    private static float progress = 0;
    private static float volume = 1;

    private static final ConcurrentLinkedQueue<HitMarker> HIT_MARKERS = new ConcurrentLinkedQueue<>();

    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker){
        BlockPos pos = getAnvilPos();
        if(pos != null) {

            Vec3 screenPos = RenderUtil.projectPosToScreen(Vec3.atCenterOf(pos));

            if (screenPos == null) return;

            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            if(blockEntity instanceof AbstractAnvilBlockEntity abstractAnvilBlockEntity){
                switch (blockEntity){
                    case StoneAnvilBlockEntity stoneAnvilBlockEntity -> {
                        guiTexture = MILostFavor.locate("textures/gui/stone_anvil_gui.png");
                    }

                    case BronzeAnvilBlockEntity bronzeAnvilBlockEntity -> {
                        guiTexture = MILostFavor.locate("textures/gui/bronze_anvil_gui.png");
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + blockEntity);
                }

                maxHit = abstractAnvilBlockEntity.getMaxHit();
            }

            renderProgress(guiGraphics, deltaTracker, screenPos);

            HIT_MARKERS.forEach(hitMarker -> hitMarker.render(guiGraphics, deltaTracker, screenPos));
        }
    }

    private static void renderProgress(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Vec3 screenPos){

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        poseStack.translate(screenPos.x, screenPos.y, screenPos.z);

        guiGraphics.blit(guiTexture, -16,-16,58,58,0,0, 58, 58, 128, 128);

        int progressOffset = (int) (58 * progress);

        guiGraphics.blit(guiTexture, -16 + 58 - progressOffset,-16,progressOffset,58, 58 - progressOffset,64, progressOffset, 58, 128, 128);

        poseStack.popPose();

    }

    private static BlockPos getAnvilPos(){
        Minecraft minecraft = Minecraft.getInstance();

        if(minecraft.level == null || minecraft.player == null || minecraft.screen != null ) return null;

        HitResult hitResult = minecraft.hitResult;

        if(hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() != HitResult.Type.MISS){
            Level level= minecraft.level;
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState blockState = level.getBlockState(pos);

            if (blockState.is(MILFBlockTags.ANVILS) && minecraft.player.getMainHandItem().is(MILFItemTags.HAMMERS)) {
                if(level.getBlockEntity(pos) instanceof AbstractAnvilBlockEntity anvilBlockEntity && anvilBlockEntity.hasItem()){
                    return pos;
                }
            }
        }

        return null;
    }

    public static void onClientTick(ClientTickEvent.Pre event){
        BlockPos pos = getAnvilPos();
        if(pos != null){
            ticks++;

            HIT_MARKERS.forEach(hitMarker -> hitMarker.move(0.05f));
            HIT_MARKERS.removeIf(hitMarker -> hitMarker.progress >= 1);

            if(ticks % 5 == 0){

                volume = Mth.approach(volume, 1f, 0.1f);
            }

            if(ticks % nextMarkerCooldown == 0){


                ticks = 0;

                Level level = Minecraft.getInstance().level;

                assert  level!= null;
                nextMarkerCooldown = level.random.nextInt(8, 20);
                HIT_MARKERS.add(new HitMarker(0.45f + (float) level.random.nextInt(1, 12) / 30));
            }
        } else {
            nextMarkerCooldown = 3;
            ticks = 0;
            progress = 0;
            HIT_MARKERS.clear();
        }
    }

    public static void handleClick(InputEvent.MouseButton.Pre event){

        if(event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT && event.getAction() == InputConstants.PRESS){
            BlockPos pos = getAnvilPos();
            if(pos != null) {

                if(progress >= 1f) progress = 0f;

                HitMarker lastHit = HIT_MARKERS.poll();

                float accuracy = (lastHit != null) ? lastHit.getHitAccuracy() : 0;
                progress = Mth.clamp(
                        progress +  Mth.clamp((float)(maxHit * 1.09 * Math.pow(accuracy, 3)), 0.05f , 1f),
                        0f, 1f

                );
                //lastAccuracyString = String.valueOf(accuracy);

                volume =Mth.approach(volume, 0.1f, -0.1f);

                PacketDistributor.sendToServer(new AnvilHitPayload(pos, accuracy, progress, volume));
            }
        }

    }

    private static class HitMarker {

        private final float speed;
        private float progress = 0;
        private float prevProgress = 0;

        private final static float STRAIGHT_FRACTION = 0.8f;

        private static final Vec3 START = new Vec3(53, 0, 0);
        private static final Vec3 CURVE_START = new Vec3(53, 37, 0);
        private static final Vec3 CURVE_TOP = new Vec3(45, 54, 0);
        private static final Vec3 CURVE_END = new Vec3(37, 37, 0);

        public HitMarker(float speed) {
            this.speed = speed;

        }

        public float getHitAccuracy(){
            float easedProgress = getEasedProgress(Minecraft.getInstance().getTimer());
            float peak = STRAIGHT_FRACTION + (1f - STRAIGHT_FRACTION) * 0.5f;
            float distance = easedProgress - peak;
            return (float) Math.exp(-109 * 3 * distance * distance);
        }

        public void move(float percentage){
            prevProgress = progress;
            progress+= speed * percentage;
        }

        private float getEasedProgress(DeltaTracker deltaTracker){
            float interpolatedProgress = Mth.lerp(deltaTracker.getGameTimeDeltaPartialTick(true), prevProgress, progress);

            float easedProgress = RenderUtil.EasingFunctions.easeInQuadOutExpo(interpolatedProgress);

            return easedProgress;
        }

        private Vec3 getRelativePos(DeltaTracker deltaTracker, Vec3 screenPos){

            float easedProgress = getEasedProgress(deltaTracker);

            Vec3 offset = pathOffset(Mth.clamp(easedProgress, 0, 1) );

            return new Vec3(screenPos.x + offset.x, screenPos.y + offset.y, screenPos.z);
        }

        private static Vec3 pathOffset(float progress) {
            if (progress <= STRAIGHT_FRACTION) {
                float localProgress = progress / STRAIGHT_FRACTION;
                return lerpVec3(START, CURVE_START, localProgress);
            }
            float localProgress = (progress - STRAIGHT_FRACTION) / (1f - STRAIGHT_FRACTION);
            return getQuadraticBezierPoint(CURVE_START, CURVE_TOP, CURVE_END, localProgress);
        }

        private static Vec3 lerpVec3(Vec3 startVec3, Vec3 endVec3, float progress) {
            return new Vec3(
                    Mth.clampedLerp(startVec3.x, endVec3.x, progress),
                    Mth.clampedLerp(startVec3.y, endVec3.y, progress),
                    Mth.clampedLerp(startVec3.z, endVec3.z, progress)
            );
        }

        private static Vec3 getQuadraticBezierPoint(Vec3 startPoint, Vec3 controlPoint, Vec3 endPoint, float progress) {
            float a = (float) Math.pow(1 - progress, 2);
            float b = 2 * (1 - progress) * progress;
            float c = progress * progress;
            return new Vec3(
                    a * startPoint.x + b * controlPoint.x + c * endPoint.x,
                    a * startPoint.y + b * controlPoint.y + c * endPoint.y,
                    a * startPoint.z + b * controlPoint.z + c * endPoint.z
            );
        }

        private float getScale(DeltaTracker deltaTracker){
            float easedProgress = getEasedProgress(deltaTracker);

            if(easedProgress < 0.1){
                float t = Mth.clamp(easedProgress / 0.1f, 0, 1);
                return t * t * (3f - 2f * t);
            }

            if(easedProgress > 0.9){
                float t = Mth.clamp((easedProgress - 0.9f) / 0.1f, 0, 1);
                return 1f - t * t * (3f - 2f * t);
            }

            return 1;

        }

        public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Vec3 screenPos){

            PoseStack poseStack = guiGraphics.pose();

            poseStack.pushPose();

            Vec3 relativePos = getRelativePos(deltaTracker, screenPos);
            float scale = getScale(deltaTracker);

            poseStack.translate(relativePos.x - 15, relativePos.y - 15, relativePos.z);
            poseStack.scale(scale, scale, 1);
            guiGraphics.blit(guiTexture, -2,-2,5,5,59,0, 5, 5, 128, 128);

            poseStack.popPose();

        }
    }
}
