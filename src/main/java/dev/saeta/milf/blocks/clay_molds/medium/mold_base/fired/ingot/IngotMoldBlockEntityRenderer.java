package dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.ingot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.AbstractMultislotMoldBaseBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class IngotMoldBlockEntityRenderer extends AbstractMultislotMoldBaseBlockEntityRenderer<IngotMoldBlockEntity> {
    public IngotMoldBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected float getItemYOffset() {
        return 1.109f / 16;
    }

    @Override
    protected void applyItemTransforms(Direction direction, PoseStack poseStack, int slots, int index){

        float yOffset = getItemYOffset();

        switch (direction) {
            case NORTH -> {

                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Axis.YN.rotationDegrees(90));
                poseStack.mulPose(Axis.ZN.rotationDegrees(22.5f));
                poseStack.translate(-0.5, -0.5, -0.5);

                poseStack.translate(10.5 / 16f, yOffset, MIN_X + (MAX_X - MIN_X) * ((float) (index) / Math.max(1, slots - 1)));
                poseStack.scale(1, 1,3);

            }
            case SOUTH -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.mulPose(Axis.ZN.rotationDegrees(22.5f));
                poseStack.translate(-0.5, -0.5, -0.5);

                poseStack.translate(10.5 / 16f, yOffset, MIN_X + (MAX_X - MIN_X) * ((float) index / Math.max(1, slots - 1)));
                poseStack.scale(1, 1, 3);
            }
            case WEST -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Axis.ZN.rotationDegrees(22.5f));
                poseStack.translate(-0.5, -0.5, -0.5);

                poseStack.translate(10.5 / 16f, yOffset, MIN_Z + (MAX_Z - MIN_Z) * ((float) index / Math.max(1, slots - 1)));
                poseStack.scale(1, 1, 3);
            }
            case EAST -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.mulPose(Axis.ZN.rotationDegrees(22.5f));
                poseStack.translate(-0.5, -0.5, -0.5);

                poseStack.translate(10.5 / 16f, yOffset, MIN_Z + (MAX_Z - MIN_Z) * ((float) index / Math.max(1, slots - 1)));
                poseStack.scale(1, 1, 3);
            }
            default -> {

            }
        }
    }

    @Override
    protected float getFluidMinOffset() {
        return 3f/16;
    }

    @Override
    protected float getFluidMaxOffset() {
        return 13f/16;
    }

}
