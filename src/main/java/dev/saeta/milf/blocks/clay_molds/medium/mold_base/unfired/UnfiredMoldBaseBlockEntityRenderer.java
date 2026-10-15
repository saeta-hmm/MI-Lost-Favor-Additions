package dev.saeta.milf.blocks.clay_molds.medium.mold_base.unfired;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.ItemStackHandler;

public class UnfiredMoldBaseBlockEntityRenderer implements BlockEntityRenderer<UnfiredMoldBaseBlockEntity> {

    private static ItemRenderer itemRenderer;
    private static BlockRenderDispatcher blockRenderDispatcher;
    private final RandomSource randomSource = RandomSource.create();

    public UnfiredMoldBaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(UnfiredMoldBaseBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStackHandler itemHandler = blockEntity.getItemHandler();

        ItemStack stack = itemHandler.getStackInSlot(0);

        if(!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem){

            poseStack.pushPose();

            poseStack.translate(0, -4/16f, 0);

            //poseStack.mulPose(Axis.YN.rotationDegrees(90));

            Direction direction = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            BlockState blockToRender = blockItem.getBlock().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, direction.getCounterClockWise());

            poseStack.translate(0.5, 0.5, 0.5);

            switch (direction) {
                case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
                case SOUTH -> poseStack.mulPose(Axis.XN.rotationDegrees(90));
                case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                case WEST -> poseStack.mulPose(Axis.ZN.rotationDegrees(90));
                default -> {}
            }

            poseStack.scale(0.9f, 0.9f, 0.9f);

            poseStack.translate(-0.5, -0.5, -0.5);

            poseStack.translate(0, 3/16f, 0);

            Level level = blockEntity.getLevel();
            BlockPos pos = blockEntity.getBlockPos().above();

            if(level != null){
                blockRenderDispatcher.getModelRenderer().tesselateBlock(
                        level,
                        blockRenderDispatcher.getBlockModel(blockToRender),
                        blockToRender,
                        pos,
                        poseStack,
                        bufferSource.getBuffer(RenderType.solid()),
                        false,
                        randomSource,
                        109109,
                        packedOverlay,
                        ModelData.EMPTY,
                        RenderType.solid()
                );
            }


            poseStack.popPose();

        }
    }
}
