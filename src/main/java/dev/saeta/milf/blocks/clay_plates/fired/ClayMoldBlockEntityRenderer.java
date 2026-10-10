package dev.saeta.milf.blocks.clay_plates.fired;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

public class ClayMoldBlockEntityRenderer implements BlockEntityRenderer<ClayPlateBlockEntity> {

    private final static float MIN_X = (float) 4 /16;
    private final static float MAX_X = (float) 12 /16;
    private final static float MIN_Z = (float) 4 /16;
    private final static float MAX_Z = (float) 12 /16;
    private final static float MIN_Y = (float) 1 /16;
    private final static float MAX_Y = (float) 2.01 /16;

    public ClayMoldBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ClayPlateBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidTank fluidTank = blockEntity.getFluidTank();

        if(!fluidTank.isEmpty()){

            poseStack.pushPose();
            FluidStack fluidStack = fluidTank.getFluid().copy();

            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture());



            int color = extensions.getTintColor(fluidStack);

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
    }
}
