package dev.saeta.milf.entities.potsherd;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFEntityTypes;
import dev.saeta.milf.registries.MILFItems;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PotsherdProjectileEntityRenderer extends EntityRenderer<PotsherdProjectileEntity> {

    private final ItemRenderer itemRenderer;

    public PotsherdProjectileEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(PotsherdProjectileEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));

        float shake = (float) entity.shakeTime - partialTicks;

        if(shake > 0) {
            float shakeAngle = Mth.sign(Math.sin(shake * 5)) * RenderUtil.EasingFunctions.easeInBack(shake / 7) * shake * 5;

            //float shakeAngle = (float) (Mth.clamp( Math.sinh(shake * 3) * Math.pow(Math.sin(shake) / 2, 2)  , -2, 2)    * shake * 2);
            poseStack.mulPose(Axis.ZP.rotationDegrees(shakeAngle));
            poseStack.mulPose(Axis.YP.rotationDegrees(shakeAngle / 10.9f));
        }

        if(!entity.isInGround()) {

            float spinDegrees = ((float) entity.tickCount + partialTicks) * 40;

            poseStack.mulPose(Axis.ZP.rotationDegrees(spinDegrees));
        }

        itemRenderer.renderStatic(
                entity.getDefaultPickupItem(),
                ItemDisplayContext.GROUND,
                packedLight,
                109,
                poseStack,
                bufferSource,
                entity.level(),
                0
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

    }

    @Override
    public ResourceLocation getTextureLocation(PotsherdProjectileEntity entity) {
        return MILostFavor.locate("textures/item/potsherd.png");
    }
}
