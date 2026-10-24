package dev.saeta.milf.client.items.fire_striker;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlockEntity;
import dev.saeta.milf.networking.data.ShapingType;
import dev.saeta.milf.networking.payloads.FireStrikerUsePayload;
import dev.saeta.milf.networking.payloads.ShapeableHitPayload;
import dev.saeta.milf.networking.payloads.ShapingStartPayload;
import dev.saeta.milf.registries.MILFItemTags;
import dev.saeta.milf.registries.MILFItems;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static dev.saeta.milf.util.RenderUtil.applyItemArmAttackTransform;
import static dev.saeta.milf.util.RenderUtil.applyItemArmTransform;


@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class SetTheWorldOnFire {

    private final static int MAX_USE_TICKS = 50;
    private final static int SOUND_DELAY = 4;

    private static float prepareAnimationProgress = 0;
    private static float prevPrepareAnimationProgress = 0;
    private static boolean isPrimed = false;
    private static boolean isStriking = false;

    private static int useTicsk = 0;
    private static int prevUseTicks = 0;

    @SubscribeEvent
    public static void handleClick(InputEvent.MouseButton.Pre event){

        if(event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT ){

            if(event.getAction() == InputConstants.PRESS){

                if(canUseStriker()) isPrimed = true;


            } else if (event.getAction() == InputConstants.RELEASE){
                if(isPrimed) {
                    isPrimed = false;
                    prepareAnimationProgress = 0;
                }
            }

        }else if(event.getButton() == InputConstants.MOUSE_BUTTON_LEFT){

            if(isPrimed && event.getAction() == InputConstants.PRESS) {
                useTicsk = 0;
                prevUseTicks = 0;
                isStriking = true;


            } else {
                useTicsk = 0;
                prevUseTicks = 0;
                isStriking = false;
            }
        }

    }

    private static boolean canUseStriker(){
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player == null) return false;
        if(!player.getMainHandItem().is(MILFItems.FIRE_STRIKER)) return false;
        return player.getOffhandItem().is(Items.FLINT);
    }


    @SubscribeEvent
    public static void handleInteractions(InputEvent.MouseButton.InteractionKeyMappingTriggered event){
        if(isPrimed) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event){
        prevPrepareAnimationProgress = prepareAnimationProgress;
        prevUseTicks = useTicsk;
        if(isPrimed){

            prepareAnimationProgress = Mth.approach(prepareAnimationProgress, 1, 0.1f);

        }
        if(isPrimed && isStriking) {

            useTicsk++;

            if(useTicsk == MAX_USE_TICKS / 3 - SOUND_DELAY ){
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.FLINTANDSTEEL_USE, 0.64f, 0.8f));
                spawnParticles();
            } else if (useTicsk == MAX_USE_TICKS * 2 / 3- SOUND_DELAY ) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.FLINTANDSTEEL_USE, 0.84f, 0.95f));
                spawnParticles();
            }else if (useTicsk == MAX_USE_TICKS- SOUND_DELAY){
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.FLINTANDSTEEL_USE, 1f, 1.22f));
                spawnParticles();
            }

            if(useTicsk == MAX_USE_TICKS){
                isStriking = false;
                Minecraft minecraft = Minecraft.getInstance();
                Player player = minecraft.player;
                if (player == null) return;

                HitResult hitResult = minecraft.hitResult;

                if(hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() != HitResult.Type.MISS){

                    BlockPos pos = blockHitResult.getBlockPos();
                    PacketDistributor.sendToServer(new FireStrikerUsePayload(pos));

                }
            }
        }
    }

    private static void spawnParticles(){
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        HitResult hitResult = minecraft.hitResult;

        if(player == null || level == null || hitResult == null) return;

        Vec3 lookVector = player.getLookAngle();
        Vec3 eyePosition = player.getEyePosition();

        Vec3 flintPosition = eyePosition.add(lookVector.scale(0.39));
        level.addParticle(
                ParticleTypes.SMOKE,
                flintPosition.x, flintPosition.y - 0.23, flintPosition.z,
                0,-0.109*1.66,0
        );

        if(hitResult.getType() == HitResult.Type.MISS) return;

        Vec3 hitVector = hitResult.getLocation();

        level.addParticle(
                ParticleTypes.WHITE_SMOKE,
                hitVector.x, hitVector.y, hitVector.z,
                0,0,0
        );

    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (!isPrimed) return;

        ItemStack stack = event.getItemStack();

        if(!canUseStriker()) {
            isPrimed = false;
            prepareAnimationProgress = 0;
            return;
        }

        event.setCanceled(true);

        renderHands(
                event.getPoseStack(),
                event.getMultiBufferSource(),
                stack,
                event.getPackedLight(),
                event.getPartialTick(),
                event.getEquipProgress(),
                event.getSwingProgress()
        );

    }

    private static void renderHands(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            ItemStack itemToRender,
            int packedLight,
            float partialTick,
            float equipProgress,
            float swingProgress
    ){
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if(player == null) return;

        EntityRenderDispatcher entityRenderDispatcher = minecraft.getEntityRenderDispatcher();

        float interpolatedPrepareAnimationProgress = Mth.lerp(partialTick, prevPrepareAnimationProgress, prepareAnimationProgress);
        float easedPrepareAnimationProgress = RenderUtil.EasingFunctions.easeInQuadOutExpo(interpolatedPrepareAnimationProgress);

        float prevUseProgress = (float) prevUseTicks / MAX_USE_TICKS;
        float useProgress = (float) useTicsk / MAX_USE_TICKS;

        float interpolatedProgress = Mth.lerp(partialTick, prevUseProgress, useProgress);

        float subProgress = (interpolatedProgress * 3f) % 1f;
        subProgress = RenderUtil.mapProgressTo010(subProgress);

        subProgress = RenderUtil.EasingFunctions.easeInExpo(subProgress);

        poseStack.pushPose();

        ItemDisplayContext context;
        if(itemToRender.is(MILFItems.FIRE_STRIKER)) {

            context = ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;

            applyItemArmAttackTransform(poseStack, HumanoidArm.RIGHT, swingProgress);
            applyItemArmTransform(poseStack, HumanoidArm.RIGHT, equipProgress);

            poseStack.translate(-0.42*easedPrepareAnimationProgress, easedPrepareAnimationProgress * 0.47 - 0.28*subProgress, easedPrepareAnimationProgress * -0.16 - 0.13*subProgress );

            poseStack.mulPose(Axis.XN.rotationDegrees(easedPrepareAnimationProgress * 35));
            poseStack.mulPose(Axis.ZN.rotationDegrees(easedPrepareAnimationProgress * 15));
            poseStack.mulPose(Axis.YN.rotationDegrees(easedPrepareAnimationProgress * -65));

            poseStack.mulPose(Axis.XN.rotationDegrees(easedPrepareAnimationProgress * 25*subProgress));


            poseStack.mulPose(Axis.XN.rotationDegrees(easedPrepareAnimationProgress * 75));

        } else {

            context = ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

            applyItemArmAttackTransform(poseStack, HumanoidArm.LEFT, swingProgress);
            applyItemArmTransform(poseStack, HumanoidArm.LEFT, equipProgress);

            //l/r  u/d f/b
            poseStack.translate(easedPrepareAnimationProgress* 0.44, easedPrepareAnimationProgress*0.04, easedPrepareAnimationProgress*-0.36);

            poseStack.translate(0, 0, -0.11 * subProgress);
            poseStack.mulPose(Axis.XN.rotationDegrees(12 * subProgress));

            poseStack.mulPose(Axis.ZN.rotationDegrees(easedPrepareAnimationProgress*15));
            poseStack.mulPose(Axis.ZN.rotationDegrees(easedPrepareAnimationProgress*-33));
            poseStack.mulPose(Axis.XN.rotationDegrees(easedPrepareAnimationProgress*15));
        }


        entityRenderDispatcher.getItemInHandRenderer().renderItem(
                player,
                itemToRender,
                context,
                context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                poseStack,
                bufferSource,
                packedLight
        );

        poseStack.popPose();
    }
}
