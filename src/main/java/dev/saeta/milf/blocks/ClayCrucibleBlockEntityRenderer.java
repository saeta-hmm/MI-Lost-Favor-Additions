package dev.saeta.milf.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.antlr.v4.runtime.atn.BlockStartState;
import org.joml.Matrix4f;

import java.util.function.BiConsumer;

public class ClayCrucibleBlockEntityRenderer implements BlockEntityRenderer<ClayCrucibleBlockEntity> {

    private static BlockRenderDispatcher blockRenderDispatcher;
    private static ItemRenderer itemRenderer;

    private final static float MIN_X = (float) 4 /16;
    private final static float MAX_X = (float) 12 /16;
    private final static float MIN_Z = (float) 4 /16;
    private final static float MAX_Z = (float) 12 /16;
    private final static float MIN_Y = (float) 2 /16;

    public ClayCrucibleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ClayCrucibleBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidTank fluidTank = blockEntity.getFluidTank();

        if(!fluidTank.isEmpty()){
            FluidStack fluidStack = fluidTank.getFluid().copy();

            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture());

            float percent = (float) fluidStack.getAmount() / 1000;
            float maxY = MIN_Y + (percent * ((float) 6 / 16));

            int color = extensions.getTintColor(fluidStack);

            VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());
            Matrix4f matrix = poseStack.last().pose();

            float u0 = sprite.getU0();
            float u1 = sprite.getU1();
            float v0 = sprite.getV0();
            float v1 = sprite.getV1();

            consumer.addVertex(matrix, MIN_X, maxY, MIN_Z).setColor(color).setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 1, 0);
            consumer.addVertex(matrix, MIN_X, maxY, MAX_Z).setColor(color).setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 1, 0);
            consumer.addVertex(matrix, MAX_X, maxY, MAX_Z).setColor(color).setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 1, 0);
            consumer.addVertex(matrix, MAX_X, maxY, MIN_Z).setColor(color).setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 1, 0);
        }

        ItemStackHandler itemHandler = blockEntity.getItemHandler();

        if(blockEntity.isFull()){
            BlockState blockToRender = blockEntity.isLit() ? Blocks.MAGMA_BLOCK.defaultBlockState() : Blocks.COAL_BLOCK.defaultBlockState();

            poseStack.pushPose();
            poseStack.translate(0.25, 0, 0.25);
            poseStack.scale(0.5F, 0.5F, 0.5F);

            blockRenderDispatcher.renderSingleBlock(
                    blockToRender,
                    poseStack,
                    bufferSource,
                    packedLight,
                    packedOverlay,
                    ModelData.EMPTY,
                    RenderType.solid()
            );

            poseStack.popPose();
            return;
        }

        BlockPos pos = blockEntity.getBlockPos();

        int seed = pos.hashCode();
        int itemNumber = 0;

        BiConsumer<ItemStack, Integer> renderSingleItem = (stack, index) -> {
            poseStack.pushPose();
            poseStack.translate(0.5, (double) 1 / 16, 0.5);
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
                    blockEntity.getLevel(),
                    0
            );

            poseStack.popPose();
        };

        ItemStack stack0 = itemHandler.getStackInSlot(0);

        if(!stack0.isEmpty()){
            for (int i = 0; i < stack0.getCount(); i++) {
                renderSingleItem.accept(stack0, i);
                itemNumber++;
            }
        }

        ItemStack stack1 = itemHandler.getStackInSlot(1);

        if(!stack1.isEmpty()){
            for (int i = itemNumber; i < stack1.getCount() + itemNumber; i++) {
                renderSingleItem.accept(stack1, i);
            }
        }
    }

}
