package dev.saeta.milf.blocks.kiln.fired;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class KilnBlockEntityRenderer implements BlockEntityRenderer<KilnBlockEntity> {

    private static BlockRenderDispatcher blockRenderDispatcher;
    private final RandomSource randomSource = RandomSource.create();

    public KilnBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();

    }

    @Override
    public void render(KilnBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ItemStackHandler itemHandler = blockEntity.getItemHandler();

        ItemStack stack = itemHandler.getStackInSlot(0);

        if(!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem){
            Block block = blockItem.getBlock();

            BlockState blockToRender = block.defaultBlockState();

            poseStack.pushPose();

            poseStack.translate(0.1875, 0.625, 0.1875);
            poseStack.scale(0.625f, 0.625f, 0.625F);

            List<RenderType> renderTypes = blockRenderDispatcher.getBlockModel(blockToRender).getRenderTypes(blockToRender, randomSource, ModelData.EMPTY).asList();


            RenderType renderType = renderTypes.isEmpty() ? RenderType.solid() : renderTypes.getFirst();

            Level level = blockEntity.getLevel();
            BlockPos pos = blockEntity.getBlockPos();

            if(level != null){
                blockRenderDispatcher.getModelRenderer().tesselateBlock(
                        level,
                        blockRenderDispatcher.getBlockModel(blockToRender),
                        blockToRender,
                        pos,
                        poseStack,
                        bufferSource.getBuffer(renderType),
                        false,
                        randomSource,
                        109109,
                        packedOverlay,
                        ModelData.EMPTY,
                        renderType
                );
            }


            poseStack.popPose();

        }
    }
}
