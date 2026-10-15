package dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.rod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.AbstractMultislotMoldBaseBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class RoeMoldBlockEntityRenderer extends AbstractMultislotMoldBaseBlockEntityRenderer<RodMoldBlockEntity> {
    public RoeMoldBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected float getItemYOffset() {
        return 4.109f / 16;
    }

    @Override
    protected void applyItemTransforms(Direction direction, PoseStack poseStack, int slots, int index) {
        float yOffset = getItemYOffset();
        float slotMultiplier = ((float) (index) / Math.max(1, slots - 1));

        float xOffset = MAX_X - (MAX_X - MIN_X) * slotMultiplier + 0.109f * 2f / 16;
        float zOffset = 5.5f / 16 + 2.25f / 16f * slotMultiplier;
        switch (direction) {
            case NORTH -> {

                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Axis.XN.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(22.5f));
                poseStack.translate(-0.5, -0.5, -0.5);

                poseStack.translate(xOffset, zOffset, yOffset);

                poseStack.scale(0.75f, 0.75f, 0.75f);

            }
            case SOUTH -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.mulPose(Axis.XN.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(22.5f));
                poseStack.translate(-0.5, -0.5, -0.5);

                poseStack.translate(xOffset, zOffset, yOffset);

                poseStack.scale(0.75f, 0.75f, 0.75f);
            }
            case WEST -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.mulPose(Axis.XN.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(22.5f));
                poseStack.translate(-0.5, -0.5, -0.5);

                poseStack.translate(xOffset, zOffset, yOffset);

                poseStack.scale(0.75f, 0.75f, 0.75f);
            }
            case EAST -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Axis.YN.rotationDegrees(90));
                poseStack.mulPose(Axis.XN.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(22.5f));
                poseStack.translate(-0.5, -0.5, -0.5);

                poseStack.translate(xOffset, zOffset, yOffset);

                poseStack.scale(0.75f, 0.75f, 0.75f);
            }
            default -> {

            }
        }
    }

    @Override
    protected float getFluidMinOffset() {
        return 4f/16;
    }

    @Override
    protected float getFluidMaxOffset() {
        return 12f/16;
    }

}
