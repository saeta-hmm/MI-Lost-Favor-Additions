package dev.saeta.milf.client.shaping;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlockEntity;
import dev.saeta.milf.networking.data.ShapingType;
import dev.saeta.milf.networking.payloads.ShapeableHitPayload;
import dev.saeta.milf.networking.payloads.ShapingStartPayload;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItemTags;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
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
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static dev.saeta.milf.util.RenderUtil.applyItemArmAttackTransform;
import static dev.saeta.milf.util.RenderUtil.applyItemArmTransform;

@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class Chiseling {

    public final static ResourceLocation ID = MILostFavor.locate("chisel_overlay");
    private final static ResourceLocation GUI_TEXTURE = MILostFavor.locate("textures/gui/chisel_gui.png");

    private static final int HAMMER_MAX_TICKS = 20;

    private static int hammerHitTicks = 0;
    private static int prevHammerHitTicks = 0;

    private static float prepareAnimationProgress = 0;
    private static float prevPrepareAnimationProgress = 0;
    private static boolean isPrimed = false;

    @SubscribeEvent
    public static void handleClick(InputEvent.MouseButton.Pre event){

        if(event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT ){

            if(event.getAction() == InputConstants.PRESS){

                if(isChiseling()) isPrimed = true;


            } else if (event.getAction() == InputConstants.RELEASE){
                if(isPrimed) {
                    isPrimed = false;
                    prepareAnimationProgress = 0;
                }
            }

        }else if(event.getButton() == InputConstants.MOUSE_BUTTON_LEFT && event.getAction() == InputConstants.PRESS){

            if(isPrimed) {

                Minecraft minecraft = Minecraft.getInstance();
                Player player = minecraft.player;
                if (player == null) return;

                HitResult hitResult = minecraft.hitResult;

                if(hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() != HitResult.Type.MISS){

                    Level level= minecraft.level;
                    BlockPos pos = blockHitResult.getBlockPos();

                    //MILostFavor.LOGGER.info("WAHT");

                    if(hammerHitTicks == 0) {
                        hammerHitTicks = HAMMER_MAX_TICKS;

                        if (level.getBlockEntity(pos) instanceof ChiseledBlockEntity chiseledBlockEntity) {
                            PacketDistributor.sendToServer(new ShapeableHitPayload(pos, blockHitResult.getDirection()));
                        } else {
                            PacketDistributor.sendToServer(new ShapingStartPayload(pos, blockHitResult.getDirection(), ShapingType.CHISEL));
                        }


                    }

                    event.setCanceled(true);

                }

            }
        }

    }


    @SubscribeEvent
    public static void handleInteractions(InputEvent.MouseButton.InteractionKeyMappingTriggered event){
        if(isPrimed) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }

    private static BlockPos getChiseledBlockPos(){
        Minecraft minecraft = Minecraft.getInstance();

        if(minecraft.level == null || minecraft.player == null || minecraft.screen != null ) return null;

        HitResult hitResult = minecraft.hitResult;

        if(hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() != HitResult.Type.MISS){
            Level level= minecraft.level;
            BlockPos pos = blockHitResult.getBlockPos();
            BlockState blockState = level.getBlockState(pos);

            if (blockState.is(MILFBlocks.CHISELED_BLOCK)) {
                if(level.getBlockEntity(pos) instanceof ChiseledBlockEntity chiseledBlockEntity){
                    return pos;
                }
            }
        }

        return null;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event){
        prevPrepareAnimationProgress = prepareAnimationProgress;
        prevHammerHitTicks = hammerHitTicks;
        if(isPrimed){

            prepareAnimationProgress = Mth.approach(prepareAnimationProgress, 1, 0.1f);

        }
        if(hammerHitTicks != 0) {

            hammerHitTicks--;
        }
    }

    private static boolean isChiseling(){
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(player == null) return false;
        if(!player.getMainHandItem().is(MILFItemTags.CHISELS)) return false;
        return player.getOffhandItem().is(MILFItemTags.HAMMERS);
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        if (!isPrimed) return;

        ItemStack stack = event.getItemStack();
        if(stack.is(MILFItemTags.CHISELS) || stack.is(MILFItemTags.HAMMERS)) {

            if(!isChiseling()) {
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

        float prevHammerProgress = (float) prevHammerHitTicks / HAMMER_MAX_TICKS;
        float hammerProgress = (float) hammerHitTicks / HAMMER_MAX_TICKS;
        hammerProgress = Mth.lerp(partialTick, prevHammerProgress, hammerProgress);
        hammerProgress = RenderUtil.EasingFunctions.easeInBack(hammerProgress);

        poseStack.pushPose();

        ItemDisplayContext context;
        if(itemToRender.is(MILFItemTags.CHISELS)) {

            context = ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;

            applyItemArmAttackTransform(poseStack, HumanoidArm.RIGHT, swingProgress);
            applyItemArmTransform(poseStack, HumanoidArm.RIGHT, equipProgress);

            poseStack.translate(0, easedPrepareAnimationProgress * 0.22, easedPrepareAnimationProgress * -0.3 + -0.05 * hammerProgress);

            poseStack.mulPose(Axis.XN.rotationDegrees(easedPrepareAnimationProgress * 35));
            poseStack.mulPose(Axis.ZN.rotationDegrees(easedPrepareAnimationProgress * 15));
            poseStack.mulPose(Axis.XN.rotationDegrees(easedPrepareAnimationProgress * 75));

        } else {

            context = ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

            applyItemArmAttackTransform(poseStack, HumanoidArm.LEFT, swingProgress);
            applyItemArmTransform(poseStack, HumanoidArm.LEFT, equipProgress);

            //l/r  u/d f/b
            poseStack.translate(easedPrepareAnimationProgress* 0.94, easedPrepareAnimationProgress*0.04, easedPrepareAnimationProgress*0.08);

            poseStack.translate(0, 0, -0.11 * hammerProgress);
            poseStack.mulPose(Axis.XN.rotationDegrees(12 * hammerProgress));

            poseStack.mulPose(Axis.ZN.rotationDegrees(easedPrepareAnimationProgress*15));
            poseStack.mulPose(Axis.ZN.rotationDegrees(easedPrepareAnimationProgress*45));
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

    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker){
        if(!isPrimed) return;
        BlockPos pos = getChiseledBlockPos();
        if(pos != null) {

            Vec3 screenPos = RenderUtil.projectPosToScreen(Vec3.atCenterOf(pos));

            if (screenPos == null) return;

            BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);

            if(blockEntity instanceof ChiseledBlockEntity chiseledBlockEntity){

                BlockState currentOutputBlockState = chiseledBlockEntity.getCurrentOutputBlock().defaultBlockState();

                if(!currentOutputBlockState.isAir() && !Minecraft.getInstance().player.isShiftKeyDown()){
                    renderOutput(guiGraphics, deltaTracker, screenPos, currentOutputBlockState.getBlock());
                }
            }



        }
    }

    private static void renderOutput(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Vec3 screenPos, Block blockToRender){

        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();

        poseStack.translate(screenPos.x, screenPos.y, screenPos.z);

        guiGraphics.blit(GUI_TEXTURE, 0,-64,64,64,0,0, 64, 64, 64, 64);

        poseStack.translate(32, -32, 0);

        poseStack.scale(2,2,1);

        guiGraphics.renderFakeItem(new ItemStack(blockToRender),  -8, -8);

        poseStack.popPose();

    }

}
