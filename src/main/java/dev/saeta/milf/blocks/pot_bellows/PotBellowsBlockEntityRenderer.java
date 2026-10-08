package dev.saeta.milf.blocks.pot_bellows;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.client.MILFModelLayerLocations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class PotBellowsBlockEntityRenderer implements BlockEntityRenderer<PotBellowsBlockEntity> {

    private static final ResourceLocation TEXTURE = MILostFavor.locate("textures/block/pot_bellows.png");

    private final ModelPart leather;
    private final ModelPart base;
    private final ModelPart tuyere;

    public PotBellowsBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

        ModelPart root = context.bakeLayer(MILFModelLayerLocations.POT_BELLOWS);

        this.leather = root.getChild("leather");
        this.base = root.getChild("base");
        this.tuyere = root.getChild("tuyere");

    }

    @Override
    public void render(PotBellowsBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        BlockState state = blockEntity.getBlockState();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(TEXTURE));

        Direction facing = state.getValue(PotBellowsBlock.FACING);


        poseStack.pushPose();

        poseStack.translate(0.5, 0, 0.5);

        poseStack.mulPose(Axis.YP.rotationDegrees(180 - facing.toYRot()));


        poseStack.pushPose();

        poseStack.scale(-1,-1,1.01f);
        poseStack.translate(-0.5, -1.5, -0.5);

        this.base.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        this.tuyere.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.scale(-1, -1f + blockEntity.getLeatherOffset(partialTick),1);
        poseStack.translate(-0.5, -1.5, -0.5);

        this.leather.render(poseStack, vertexConsumer, packedLight, packedOverlay);

        poseStack.popPose();
        poseStack.popPose();


    }

    public static LayerDefinition  createLayer(){
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        root.addOrReplaceChild(
                "base",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5, -5, -5, 10, 5, 10),
                PartPose.offset(8, 24, 8)
        );

        root.addOrReplaceChild(
                "leather",
                CubeListBuilder.create()
                        .texOffs(0, 15)
                        .addBox(-4, -3, -4, 8, 3, 8),
                PartPose.offset(8, 19, 8)
        );

        root.addOrReplaceChild(
                "tuyere",
                CubeListBuilder.create()
                        .texOffs(16, 26)
                        .addBox(-2, -3, 5, 4, 2, 3)
                        .texOffs(0, 26)
                        .addBox(-2, -5, 6, 4, 2, 4),
                PartPose.offset(8, 24, 8)
        );

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

}
