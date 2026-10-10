package dev.saeta.milf.blocks.clay_plates.fire_pit;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.saeta.milf.MILostFavor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.ItemStackHandler;

public class FirePitPlateBlockEntityRenderer implements BlockEntityRenderer<FirePitPlateBlockEntity> {

    private static BlockRenderDispatcher blockRenderDispatcher;
    private final RandomSource randomSource = RandomSource.create();

    private final static float PLATE_OFFSET = 6 / 16f;
    private final static float Y_OFFSET = 1 / 16f;

    public FirePitPlateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(FirePitPlateBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if(level == null) return;

        BlockPos pos = blockEntity.getBlockPos();

        ItemStackHandler itemHandler = blockEntity.getItemHandler();

        ItemStack nePlateStack = itemHandler.getStackInSlot(FirePitPlateBlockEntity.NE_PLATE_SLOT);

        if(!nePlateStack.isEmpty() && nePlateStack.getItem() instanceof BlockItem blockItem){

            BlockState blockToRender = blockItem.getBlock().defaultBlockState();
            poseStack.pushPose();
            poseStack.translate(-PLATE_OFFSET,Y_OFFSET,-PLATE_OFFSET);
            renderPlate(level, pos, blockToRender, poseStack, bufferSource, packedOverlay);
            poseStack.popPose();

        }

        ItemStack nwPlateStack = itemHandler.getStackInSlot(FirePitPlateBlockEntity.NW_PLATE_SLOT);

        if(!nwPlateStack.isEmpty() && nwPlateStack.getItem() instanceof BlockItem blockItem){

            BlockState blockToRender = blockItem.getBlock().defaultBlockState();
            poseStack.pushPose();
            poseStack.translate(PLATE_OFFSET,Y_OFFSET,-PLATE_OFFSET);
            renderPlate(level, pos, blockToRender, poseStack, bufferSource, packedOverlay);
            poseStack.popPose();

        }

        ItemStack sePlateStack = itemHandler.getStackInSlot(FirePitPlateBlockEntity.SE_PLATE_SLOT);

        if(!sePlateStack.isEmpty() && sePlateStack.getItem() instanceof BlockItem blockItem){

            BlockState blockToRender = blockItem.getBlock().defaultBlockState();
            poseStack.pushPose();
            poseStack.translate(-PLATE_OFFSET,Y_OFFSET,PLATE_OFFSET);
            renderPlate(level, pos, blockToRender, poseStack, bufferSource, packedOverlay);
            poseStack.popPose();

        }

        ItemStack swPlateStack = itemHandler.getStackInSlot(FirePitPlateBlockEntity.SW_PLATE_SLOT);

        if(!swPlateStack.isEmpty() && swPlateStack.getItem() instanceof BlockItem blockItem){

            BlockState blockToRender = blockItem.getBlock().defaultBlockState();
            poseStack.pushPose();
            poseStack.translate(PLATE_OFFSET,Y_OFFSET,PLATE_OFFSET);
            renderPlate(level, pos, blockToRender, poseStack, bufferSource, packedOverlay);
            poseStack.popPose();

        }
    }

    private void renderPlate(Level level,BlockPos pos, BlockState blockToRender, PoseStack poseStack, MultiBufferSource bufferSource, int packedOverlay){

        BakedModel model = blockRenderDispatcher.getBlockModel(blockToRender);

        for(RenderType renderType : model.getRenderTypes(blockToRender, randomSource, ModelData.EMPTY)){
            blockRenderDispatcher.getModelRenderer().tesselateBlock(
                    level,
                    model,
                    blockToRender,
                    pos,
                    poseStack,
                    bufferSource.getBuffer(renderType),
                    false,
                    randomSource,
                    blockToRender.getSeed(pos),
                    packedOverlay,
                    ModelData.EMPTY,
                    renderType
            );
        }


    }
}
