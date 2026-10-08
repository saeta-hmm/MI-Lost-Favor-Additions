package dev.saeta.milf.blocks.shapeable_blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlock;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlockEntity;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractShapeableBlockEntityRenderer<T extends AbstractShapeableBlockEntity> implements BlockEntityRenderer<T> {


    @Override
    public void render(AbstractShapeableBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack blockItemStack = blockEntity.getCurrentInputBlockItem();

        BlockState state = blockEntity.getBlockState();

        float sideChipped = state.getValue(ChiseledBlock.SIDE_LAYERS_CHIPPED) / 16f;
        float topChipped = state.getValue(ChiseledBlock.TOP_LAYERS_CHIPPED) / 16f;

        float minX = sideChipped;
        float minY = 0f;
        float minZ = sideChipped;
        float maxX = 1f - sideChipped;
        float maxY = 1f - topChipped;
        float maxZ = 1f - sideChipped;

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.solid());

        RenderUtil.renderCuboidFromBlockItemStack(
                consumer,
                poseStack,
                blockItemStack,
                true,
                minX,maxX,
                minY,maxY,
                minZ, maxZ,
                packedLight,
                packedOverlay
        );


    }
}
