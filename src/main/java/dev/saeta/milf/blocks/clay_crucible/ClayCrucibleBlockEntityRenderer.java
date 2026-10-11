package dev.saeta.milf.blocks.clay_crucible;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Matrix4f;

public class ClayCrucibleBlockEntityRenderer implements BlockEntityRenderer<ClayCrucibleBlockEntity> {

    private static BlockRenderDispatcher blockRenderDispatcher;
    private static ItemRenderer itemRenderer;

    private final static float MIN_X = (float) 4 /16;
    private final static float MAX_X = (float) 12 /16;
    private final static float MIN_Z = (float) 4 /16;
    private final static float MAX_Z = (float) 12 /16;
    private final static float MIN_Y = (float) 2 /16;
    private final static float MAX_Y = (float) 8 /16;

    private final static float KILN_PART_Y_OFFSET = ClayCrucibleBlock.KILN_PART_Y_OFFSET;
    private final static ResourceLocation BURNING_COAL_TEXTURE = MILostFavor.locate("block/burning_coal_block");
    private final static ResourceLocation COAL_TEXTURE = ResourceLocation.withDefaultNamespace("block/coal_block");

    public ClayCrucibleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ClayCrucibleBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        FluidTank fluidTank = blockEntity.getFluidTank();

        if(!fluidTank.isEmpty()){

            applyKilnOffset(poseStack, blockEntity.getBlockState());
            FluidStack fluidStack = fluidTank.getFluid().copy();

            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture());



            float percent = (float) fluidStack.getAmount() / 1000;
            float maxY = MIN_Y + (percent * ((float) 6 / 16));

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

        ItemStackHandler itemHandler = blockEntity.getItemHandler();

        if(blockEntity.isFull()){

            applyKilnOffset(poseStack, blockEntity.getBlockState());

            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(blockEntity.isLit() ? BURNING_COAL_TEXTURE : COAL_TEXTURE);

            VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());
            Matrix4f matrix = poseStack.last().pose();

            RenderUtil.renderUpFaceWithSprite(
                    consumer,
                    matrix,
                    sprite,
                    true,
                    MIN_X, MAX_X,
                    MAX_Y,
                    MIN_Z, MAX_Z,
                    packedLight, packedOverlay
            );

            return;
        }

        BlockPos pos = blockEntity.getBlockPos();

        int seed = pos.hashCode();
        int itemNumber = 0;

        ItemStack stack0 = itemHandler.getStackInSlot(0);

        if(!stack0.isEmpty()){
            poseStack.pushPose();
            applyKilnOffset(poseStack, blockEntity.getBlockState());
            for (int i = 0; i < stack0.getCount(); i++) {
                renderSingleItem(stack0, i, seed, poseStack, packedLight, packedOverlay, bufferSource, blockEntity.getLevel());

                itemNumber++;
            }
            poseStack.popPose();
        }

        ItemStack stack1 = itemHandler.getStackInSlot(1);

        if(!stack1.isEmpty()){
            poseStack.pushPose();
            applyKilnOffset(poseStack, blockEntity.getBlockState());
            for (int i = itemNumber; i < stack1.getCount() + itemNumber; i++) {
                renderSingleItem(stack1, i, seed, poseStack, packedLight, packedOverlay, bufferSource, blockEntity.getLevel());

            }
            poseStack.popPose();
        }

    }

    private void applyKilnOffset(PoseStack poseStack, BlockState state){
        if(state.getValue(ClayCrucibleBlock.KILN_PART)) poseStack.translate(0, KILN_PART_Y_OFFSET, 0);

    }

    private void renderSingleItem(ItemStack stack, int index, int seed, PoseStack poseStack, int packedLight, int packedOverlay, MultiBufferSource bufferSource, Level level) {

        poseStack.pushPose();
        poseStack.translate(0.5, (double) 1 / 16 + 1/64f, 0.5);
        poseStack.scale(0.8F, 0.8F, 0.8F);
        poseStack.translate(0, 1.5 / 16 * index * 0.5, 0);
        poseStack.mulPose(Axis.XN.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (Math.abs(Math.sin(seed + index * 109109) % 1) * 360)));

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
