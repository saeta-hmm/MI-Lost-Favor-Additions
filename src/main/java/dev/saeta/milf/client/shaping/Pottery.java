package dev.saeta.milf.client.shaping;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.shapeable_blocks.AbstractShapeableBlock;
import dev.saeta.milf.blocks.shapeable_blocks.clay.MoldedBlockEntity;
import dev.saeta.milf.client.render_stuff.GenericNumberTextWidget;
import dev.saeta.milf.networking.data.ShapingType;
import dev.saeta.milf.networking.payloads.ShapeableHitPayload;
import dev.saeta.milf.networking.payloads.ShapingStartPayload;
import dev.saeta.milf.registries.MILFBlockTags;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class Pottery {
    public final static ResourceLocation ID = MILostFavor.locate("pottery_overlay");

    private final static ResourceLocation GUI_TEXTURE = MILostFavor.locate("textures/gui/pottery_gui.png");
    private final static ResourceLocation QM_TEXTURE = MILostFavor.locate("textures/gui/shaping_output_qm.png");


    private final static int TEXT_COLOR = 0xFFa0a7b8;
    private final static GenericNumberTextWidget hitsWidget = new GenericNumberTextWidget(0xFFa0a7b8).centered();

    private final static int MAX_USE_TICKS = 10;


    private static boolean isRenderingArms = false;
    private static boolean isModeling = false;

    private static float leftHandRotationProgress = 0;
    private static float prevLeftHandRotationProgress = 0;


    private static int useTicks = 0;
    private static int prevUseTicks = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event){
        prevLeftHandRotationProgress = leftHandRotationProgress;
        prevUseTicks = useTicks;
        if(isModeling) {

            leftHandRotationProgress = Mth.approach(leftHandRotationProgress, 1, 0.2f);

        }

        if(useTicks != 0){
            useTicks--;
        }
    }

    @SubscribeEvent
    public static void handleClick(InputEvent.MouseButton.Pre event){

        if(event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT ){

            if(event.getAction() == InputConstants.PRESS){
                BlockPos clayPos = getClayBlockPos();

                if(clayPos!= null ) {

                    Minecraft minecraft = Minecraft.getInstance();

                    assert minecraft.player != null;
                    if (minecraft.player.getMainHandItem().isEmpty() && minecraft.player.getOffhandItem().isEmpty()) isModeling = true;

                }
            } else if (event.getAction() == InputConstants.RELEASE){
                if(isModeling) {
                    isModeling = false;
                    leftHandRotationProgress = 0;
                }
            }


        }

        if(isModeling && event.getButton() == InputConstants.MOUSE_BUTTON_LEFT){

            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player == null) return;

            if(!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) {
                isModeling = false;
                leftHandRotationProgress = 0;
                return;
            }

            if(event.getAction() == InputConstants.RELEASE) {
                event.setCanceled(true);
            } else {

                if(useTicks == 0){
                    useTicks = MAX_USE_TICKS;

                    HitResult hitResult = minecraft.hitResult;

                    if(hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() != HitResult.Type.MISS){
                        Level level= minecraft.level;
                        BlockPos pos = blockHitResult.getBlockPos();

                        if (level.getBlockEntity(pos) instanceof MoldedBlockEntity) {
                            PacketDistributor.sendToServer(new ShapeableHitPayload(pos, blockHitResult.getDirection()));
                        } else {
                            PacketDistributor.sendToServer(new ShapingStartPayload(pos, blockHitResult.getDirection(), ShapingType.POTTERY));
                        }
                    }
                }



                event.setCanceled(true);
            }


        }

    }

    @SubscribeEvent
    public static void onRenderHand(RenderArmEvent event) {

        if(isRenderingArms) return;

        if(isModeling) {

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

        float useProgress = (float) useTicks / MAX_USE_TICKS;
        float prevUseProgress = (float) prevUseTicks / MAX_USE_TICKS;

        float interpolatedUseProgress = Mth.lerp(minecraft.getTimer().getGameTimeDeltaPartialTick(true), prevUseProgress, useProgress);
        float easedUseProgress = RenderUtil.EasingFunctions.easeInQuadOutExpo(interpolatedUseProgress);

        //use
        poseStack.mulPose(Axis.ZP.rotationDegrees(-15 * easedUseProgress));
        poseStack.mulPose(Axis.XP.rotationDegrees(-5 * easedUseProgress));

        playerRenderer.renderRightHand(
                poseStack,
                multiBufferSource,
                packedLight,
                player
        );

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
        poseStack.mulPose(Axis.ZP.rotationDegrees(15 * easedUseProgress));
        poseStack.mulPose(Axis.XP.rotationDegrees(5 * easedUseProgress));

        playerRenderer.renderLeftHand(
                poseStack,
                multiBufferSource,
                packedLight,
                player
        );

        poseStack.popPose();

    }

    private static BlockPos getClayBlockPos(){
        Minecraft minecraft = Minecraft.getInstance();

        if(minecraft.level == null || minecraft.player == null || minecraft.screen != null ) return null;

        HitResult hitResult = minecraft.hitResult;

        if(hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() != HitResult.Type.MISS){
            Level level= minecraft.level;
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState blockState = level.getBlockState(pos);

            if (blockState.is(MILFBlockTags.POTTERY_CLAYS) || blockState.is(MILFBlocks.MOLDED_BLOCK)) {
                return pos;
            }
        }

        return null;
    }

    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker){
        if(!isModeling) return;
        BlockPos pos = getClayBlockPos();
        if(pos != null) {

            Vec3 screenPos = RenderUtil.projectPosToScreen(Vec3.atCenterOf(pos));

            if (screenPos == null) return;

            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            if(blockEntity instanceof MoldedBlockEntity moldedBlockEntity){

                BlockState currentOutputBlockState = moldedBlockEntity.getCurrentOutputBlock().defaultBlockState();
                BlockState blockState = Minecraft.getInstance().level.getBlockState(pos);

                renderOutput(guiGraphics, deltaTracker, screenPos, currentOutputBlockState, blockState);


            }



        }
    }

    private static void renderOutput(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Vec3 screenPos, BlockState stateToRender, BlockState state){


        float r = ((TEXT_COLOR >> 16) & 0xFF) / 255f;
        float g = ((TEXT_COLOR >> 8) & 0xFF) / 255f;
        float b = (TEXT_COLOR & 0xFF) / 255f;
        float a = ((TEXT_COLOR >> 24) & 0xFF) / 255f;


        Font font = Minecraft.getInstance().font;
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        poseStack.translate(screenPos.x, screenPos.y, screenPos.z);

        guiGraphics.blit(GUI_TEXTURE, 0,-64,64,64,0,0, 64, 64, 64, 64);

        guiGraphics.setColor(r,g,b,a);

        poseStack.pushPose();

        poseStack.translate(32.5, font.lineHeight - 64 - 1, 0);

        hitsWidget.render(0,0, guiGraphics, 0, state.getValue(AbstractShapeableBlock.TOP_LAYERS_CHIPPED));

        poseStack.translate(0, 43, 0);

        hitsWidget.render(0,0, guiGraphics, 0, state.getValue(AbstractShapeableBlock.SIDE_LAYERS_CHIPPED));


        guiGraphics.setColor(1,1,1,1);

        poseStack.popPose();



        if(!stateToRender.isAir() && !Minecraft.getInstance().player.isShiftKeyDown()){
            poseStack.translate(32, -32, 0);
            poseStack.scale(2,2,1);
            guiGraphics.renderFakeItem(new ItemStack(stateToRender.getBlock()),  -8, -8);
        } else {
            guiGraphics.setColor(r,g,b,a);

            guiGraphics.blit(QM_TEXTURE, 0,-64,64,64,0,0, 64, 64, 64, 64);

            guiGraphics.setColor(1,1,1,1);
        }


        poseStack.popPose();

    }

}
