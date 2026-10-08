package dev.saeta.milf.client.shaping;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlockEntity;
import dev.saeta.milf.networking.payloads.ShapeableHitPayload;
import dev.saeta.milf.networking.payloads.ShapingStartPayload;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItemTags;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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

@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class Chiseling {

    public final static ResourceLocation ID = MILostFavor.locate("chisel_overlay");
    private final static ResourceLocation GUI_TEXTURE = MILostFavor.locate("textures/gui/chisel_gui.png");

    private static final int HAMMER_MAX_TICKS = 20;

    private static int hammerHitTicks = 0;
    private static int prevHammerHitTicks = 0;

    private static boolean isChiseling(){
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return false;

        return player.getOffhandItem().is(MILFItemTags.HAMMERS) && player.getMainHandItem().is(MILFItemTags.CHISELS);
    }

    @SubscribeEvent
    public static void handleClick(InputEvent.MouseButton.Pre event){

        if(event.getButton() == InputConstants.MOUSE_BUTTON_LEFT && event.getAction() == InputConstants.PRESS){

            if(isChiseling()) {

                Minecraft minecraft = Minecraft.getInstance();
                Player player = minecraft.player;
                if (player == null || !player.isUsingItem()) return;

                HitResult hitResult = minecraft.hitResult;

                if(hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() != HitResult.Type.MISS){

                    Level level= minecraft.level;
                    BlockPos pos = blockHitResult.getBlockPos();

                    if(hammerHitTicks == 0) {
                        hammerHitTicks = HAMMER_MAX_TICKS;

                        if (level.getBlockEntity(pos) instanceof ChiseledBlockEntity chiseledBlockEntity) {
                            PacketDistributor.sendToServer(new ShapeableHitPayload(pos, blockHitResult.getDirection()));
                        } else {
                            PacketDistributor.sendToServer(new ShapingStartPayload(pos, blockHitResult.getDirection()));
                        }


                    }

                }

            }
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
        prevHammerHitTicks = hammerHitTicks;
        if(hammerHitTicks != 0) {

            hammerHitTicks--;
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        ItemStack stack = event.getItemStack();
        if(stack.is(MILFItemTags.CHISELS) || stack.is(MILFItemTags.HAMMERS)) {

            if (!isChiseling()) return;
            event.setCanceled(true);

            if(stack.is(MILFItemTags.CHISELS)) {
                renderChiselHand(
                        event.getPoseStack(),
                        event.getMultiBufferSource(),
                        stack,
                        event.getPackedLight(),
                        event.getPartialTick()
                );
            } else {
                renderHammerHand(
                        event.getPoseStack(),
                        event.getMultiBufferSource(),
                        stack,
                        event.getPackedLight(),
                        event.getPartialTick()
                );
            }

        }

    }

    private static void renderChiselHand(PoseStack poseStack, MultiBufferSource bufferSource, ItemStack itemToRender, int packedLight, float partialTick){

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        assert player != null;

        poseStack.pushPose();

        poseStack.translate(0.5, -0.55, -0.75);

        if (player.isUsingItem()) {

            float prevHammerProgress = (float) prevHammerHitTicks / HAMMER_MAX_TICKS;
            float hammerProgress = (float) hammerHitTicks / HAMMER_MAX_TICKS;

            hammerProgress = Mth.lerp( partialTick, prevHammerProgress, hammerProgress);

            hammerProgress = RenderUtil.EasingFunctions.easeInBack(hammerProgress);



            poseStack.translate(0, 0.22, -0.3 + -0.05 * hammerProgress);

            poseStack.mulPose(Axis.XN.rotationDegrees(35));


        }

        poseStack.mulPose(Axis.ZN.rotationDegrees(15));

        poseStack.mulPose(Axis.XN.rotationDegrees(75));

        minecraft.getEntityRenderDispatcher().getItemInHandRenderer().renderItem(
                player, itemToRender,
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                false,
                poseStack,
                bufferSource,
                packedLight
        );

        poseStack.popPose();
    }

    private static void renderHammerHand(PoseStack poseStack, MultiBufferSource bufferSource, ItemStack itemToRender, int packedLight, float partialTick){

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        assert player != null;

        poseStack.pushPose();



        poseStack.translate(0.3, -0.3, -0.7);


        if (player.isUsingItem()) {

            float prevHammerProgress = (float) prevHammerHitTicks / HAMMER_MAX_TICKS;
            float hammerProgress = (float) hammerHitTicks / HAMMER_MAX_TICKS;

            hammerProgress = Mth.lerp( partialTick, prevHammerProgress, hammerProgress);

            hammerProgress = RenderUtil.EasingFunctions.easeInOutExpo(hammerProgress);

            poseStack.translate(0, 0, -0.11 * hammerProgress);

            poseStack.mulPose(Axis.XN.rotationDegrees(12 * hammerProgress));

            poseStack.mulPose(Axis.ZN.rotationDegrees(15));

        }

        poseStack.mulPose(Axis.ZN.rotationDegrees(45));

        poseStack.mulPose(Axis.XN.rotationDegrees(15));


        minecraft.getEntityRenderDispatcher().getItemInHandRenderer().renderItem(
                player, itemToRender,
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                false,
                poseStack,
                bufferSource,
                packedLight
        );

        poseStack.popPose();
    }

    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker){
        if(!isChiseling()) return;
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
