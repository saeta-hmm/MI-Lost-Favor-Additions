package dev.saeta.milf.blocks.clay_plates;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

public class AbstractClayPlateBlockEntityRenderer<T extends AbstractClayPlateBlockEntity> implements BlockEntityRenderer<T> {

    private static ItemRenderer itemRenderer;

    public AbstractClayPlateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStackHandler itemHandler = blockEntity.getItemHandler();

        ItemStack stack0 = itemHandler.getStackInSlot(0);

        if(!stack0.isEmpty()){
            for (int i = 0; i < stack0.getCount(); i++) {

                renderSingleItem(stack0, i, poseStack, packedLight, packedOverlay, bufferSource, blockEntity.getLevel());

            }
        }
    }

    private void renderSingleItem(ItemStack stack, int index, PoseStack poseStack, int packedLight, int packedOverlay, MultiBufferSource bufferSource, Level level){
        poseStack.pushPose();

        poseStack.translate(0.5, (double) 1.8 / 16 + 1/64f, 1 - 0.5*0.8);

//        poseStack.translate(0.5, (double) 1.8 / 16 + 1/64f, 1 - 0.5*0.8);
//        poseStack.scale(0.8F, 0.8F, 0.8F);

        poseStack.mulPose(Axis.XN.rotationDegrees(90));

        itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                level,
                0
        );

        poseStack.popPose();
    }
}
