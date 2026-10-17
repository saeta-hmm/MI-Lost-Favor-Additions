package dev.saeta.milf.blocks.clay_molds.medium.mold_base.unfired;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.saeta.milf.registries.MILFItems;
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
import net.minecraft.world.item.ItemDisplayContext;
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

    protected final static float MIN_X = (float) 5 /16;
    protected final static float MAX_X = (float) 11 /16;
    protected final static float MIN_Z = (float) 5 /16;
    protected final static float MAX_Z = (float) 11 /16;
    protected final static float MIN_Y = (float) 1 /16;
    protected final static float MAX_Y = (float) 4.015 /16;

    public UnfiredMoldBaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(UnfiredMoldBaseBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStackHandler itemHandler = blockEntity.getItemHandler();

        ItemStack stack = itemHandler.getStackInSlot(0);

        if(!stack.isEmpty()){

            if(stack.getItem() instanceof BlockItem blockItem){
                renderAnvil(blockEntity, poseStack, bufferSource, packedOverlay, blockItem);

            } else {
                for (int i = 0; i < stack.getCount(); i++) {
                    renderSingleItem(stack, i, poseStack, packedLight, packedOverlay, bufferSource, blockEntity);
                }
            }


        }
    }

    private void renderAnvil(UnfiredMoldBaseBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int packedOverlay, BlockItem blockItem) {
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

    protected void renderSingleItem(ItemStack stack,int index, PoseStack poseStack, int packedLight, int packedOverlay, MultiBufferSource bufferSource, UnfiredMoldBaseBlockEntity blockEntity){


        poseStack.pushPose();

        Direction direction = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);

        if(stack.is(MILFItems.STONE_ROD)){
            applyRodTransforms(direction, poseStack, 4, index);
        } else {
            applyBrickTransforms(direction, poseStack, 3, index);
        }

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

    private void applyRodTransforms(Direction direction, PoseStack poseStack, int slots, int index) {
        float yOffset = 4.109f / 16;
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

    private void applyBrickTransforms(Direction direction, PoseStack poseStack, int slots, int index) {

        float yOffset = 1.109f / 16;

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

}
