package dev.saeta.milf.blocks.basin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Matrix4f;

public class StoneBasinBlockEntityRenderer implements BlockEntityRenderer<StoneBasinBlockEntity> {
    private static ItemRenderer itemRenderer;

    private final static float MIN_X = (float) 4 /16;
    private final static float MAX_X = (float) 12 /16;
    private final static float MIN_Z = (float) 4 /16;
    private final static float MAX_Z = (float) 12 /16;
    private final static float MIN_Y = (float) 1.1 /16;
    private final static float MAX_Y = (float) 4.5 /16;

    public StoneBasinBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(StoneBasinBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidTank fluidTank = blockEntity.getFluidTank();



        ItemStackHandler itemHandler = blockEntity.getItemHandler();

        boolean hasItems = false;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);

            if(!stack.isEmpty()) {
                int seed = stack.getOrDefault(MILFDataComponents.RANDOM_SEED, 109);
                renderSingleItem(stack, i, seed, partialTick, poseStack, packedLight, packedOverlay, bufferSource, blockEntity);
                hasItems = true;
            }
        }


        //this gal has no idea what she's talking about \/, this definitely positively absolutely DOES lead to severe performance losses (┬┬﹏┬┬)
//        //surely this will not lead to severe performance losses for sure sure ( ⓛ ω ⓛ *)
//        if(hasItems && bufferSource instanceof MultiBufferSource.BufferSource bSource) {
//            bSource.endBatch();
//        }

        if(!fluidTank.isEmpty()){

            //Ugh, I'm too tired to figure this out so I don't care
            if(hasItems&& bufferSource instanceof MultiBufferSource.BufferSource bSource) {
                    bSource.endBatch();
            }

            FluidStack fluidStack = fluidTank.getFluid().copy();

            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture());


            float percent = (float) fluidStack.getAmount() / 500 * (1-blockEntity.getCurrentProgress());
            float maxY = MIN_Y + (percent * (MAX_Y - MIN_Y));

            int color = extensions.getTintColor(fluidStack);


            VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());
            Matrix4f matrix = poseStack.last().pose();

            RenderUtil.renderUpFaceWithSprite(
                    consumer,
                    matrix,
                    sprite,
                    true,
                    MIN_X, MAX_X,
                    maxY,
                    MIN_Z, MAX_Z,
                    packedLight, packedOverlay,
                    color
            );

        }

    }

    private void renderSingleItem(ItemStack stack, int index, int seed, float partialTick,  PoseStack poseStack, int packedLight, int packedOverlay, MultiBufferSource bufferSource, StoneBasinBlockEntity blockEntity) {

        poseStack.pushPose();
        poseStack.translate(0.5, (double) 1 / 16 + 1/64f, 0.5);
        poseStack.scale(0.8f, 0.8f, 0.8f);
        if(stack.getItem() instanceof BlockItem) poseStack.scale(0.8f, 0.8f, 0.8f);
        poseStack.translate(0, 1.5 / 16 * index * 0.5, 0);

        //float offset = blockEntity.getInterpolatedOffset(partialTick);
        //speed = RenderUtil.EasingFunctions.easeInOutSine(speed);

        float offsetProgress = RenderUtil.EasingFunctions.easeInOutQuad(blockEntity.getInterpolatedOffset(partialTick));

        float stackRotation = RenderUtil.getInterpolatedProgress(15 + seed % 15) * offsetProgress;
        stackRotation = RenderUtil.EasingFunctions.easeInOutSine(stackRotation);
        stackRotation =RenderUtil.mapProgressTo010(stackRotation);

        //float rotationProgress = RenderUtil.mapProgressTo010(RenderUtil.EasingFunctions.easeInQuadOutExpo(speed));

        //poseStack.translate(0, 1.5 / 16 * (3 - index) * 0.25 * offsetProgress, 0);


        poseStack.mulPose(Axis.XN.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (Math.abs(Math.sin(seed + index * 109109) % 1) * 360)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(blockEntity.getInterpolatedStirAngle(partialTick) ));

        //poseStack.translate(offsetProgress*0.11, 0, 0);
        poseStack.translate(offsetProgress*0.162, 0, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(offsetProgress * 35));



        poseStack.mulPose(Axis.YP.rotationDegrees(stackRotation * 17));
        poseStack.mulPose(Axis.XP.rotationDegrees(stackRotation * 14));





        itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                0
        );

        poseStack.popPose();

    }
}
