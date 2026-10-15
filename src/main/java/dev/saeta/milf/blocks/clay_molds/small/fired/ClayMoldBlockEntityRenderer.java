package dev.saeta.milf.blocks.clay_molds.small.fired;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saeta.milf.client.render_stuff.AlphaMultiBufferSource;
import dev.saeta.milf.util.RecipeUtil;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

public class ClayMoldBlockEntityRenderer implements BlockEntityRenderer<ClayMoldBlockEntity> {

    private final static float MIN_X = (float) 4 /16;
    private final static float MAX_X = (float) 12 /16;
    private final static float MIN_Z = (float) 4 /16;
    private final static float MAX_Z = (float) 12 /16;
    private final static float MIN_Y = (float) 1 /16;
    private final static float MAX_Y = (float) 2.01 /16;

    private static ItemRenderer itemRenderer;


    public ClayMoldBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ClayMoldBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidTank fluidTank = blockEntity.getFluidTank();

        float progress = blockEntity.isSolidifying() ? blockEntity.getInterpolatedClientProgress(partialTick) : 1f;

        if(!fluidTank.isEmpty()){

            poseStack.pushPose();
            FluidStack fluidStack = fluidTank.getFluid().copy();

            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture());



            int color = extensions.getTintColor(fluidStack);
            //AlphaMultiBufferSource wrappedBufferSource = new AlphaMultiBufferSource(bufferSource,1f -  progress);

            if(blockEntity.isSolidifying()) color = RenderUtil.modifyAlpha(color, 1f - progress);

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
                    packedLight, packedOverlay,
                    color
            );

            poseStack.popPose();
        }

        ItemStack stack0 = blockEntity.isSolidifying() ? blockEntity.getCurrentOutput() : blockEntity.getItemHandler().getStackInSlot(0);

        if(!stack0.isEmpty()){
            for (int i = 0; i < stack0.getCount(); i++) {
                AlphaMultiBufferSource wrappedBufferSource = new AlphaMultiBufferSource(bufferSource,progress);


                renderSingleItem(stack0, progress, poseStack, packedLight, packedOverlay, wrappedBufferSource, blockEntity.getLevel());

            }
        }
    }

    protected void renderSingleItem(ItemStack stack, float progress, PoseStack poseStack, int packedLight, int packedOverlay, MultiBufferSource bufferSource, Level level){


        poseStack.pushPose();

        poseStack.translate(0.5, (double) 1.8 / 16 + 1/64f, 1 - 0.5*0.8);

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
