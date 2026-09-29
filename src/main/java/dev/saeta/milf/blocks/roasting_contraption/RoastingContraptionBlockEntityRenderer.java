package dev.saeta.milf.blocks.roasting_contraption;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.function.BiConsumer;

public class RoastingContraptionBlockEntityRenderer implements BlockEntityRenderer<RoastingContraptionBlockEntity> {

    private static ItemRenderer itemRenderer;

    public RoastingContraptionBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }


    @Override
    public void render(RoastingContraptionBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {


        ItemStackHandler itemHandler = blockEntity.getItemHandler();
        BlockState state = blockEntity.getBlockState();

        Direction leftSide = state.getValue(RoastingContraptionBlock.FACING).getClockWise();

        BlockPos pos = blockEntity.getBlockPos();

        int seed = pos.hashCode();

        int stepX = leftSide.getStepX();
        int stepZ = leftSide.getStepZ();

        BiConsumer<ItemStack, Integer> renderSingleItem = (stack, index) -> {
            poseStack.pushPose();
            poseStack.translate(0.5 + stepX * (0.375 - 0.15 * index), 0.5, 0.5 + stepZ * (0.375 - 0.15 * index));

            poseStack.mulPose(Axis.YN.rotationDegrees(leftSide.toYRot()));
            //poseStack.mulPose(Axis.XP.rotationDegrees(90));

            poseStack.scale(0.8F, 0.8F, 0.8F);
            //poseStack.translate(leftSide.getStepX() * 0.1 * index,0, leftSide.getStepZ() * 0.1 * index);

            poseStack.mulPose(Axis.ZP.rotationDegrees((float) (Math.abs(Math.sin(seed + index * 109109) % 1) * 360)));

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
        };

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack inputStack = itemHandler.getStackInSlot(i);
            if(!inputStack.isEmpty()){

                renderSingleItem.accept(inputStack, i);

            }
        }

    }
}
