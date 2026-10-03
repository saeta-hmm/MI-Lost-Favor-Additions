package dev.saeta.milf.blocks.bloomery;

import aztech.modern_industrialization.MIItem;
import aztech.modern_industrialization.materials.MIMaterials;
import aztech.modern_industrialization.materials.part.PartKey;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntity;
import dev.saeta.milf.registries.MILFItemTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Matrix4f;

public class BloomeryBaseBlockEntityRenderer implements BlockEntityRenderer<BloomeryBaseBlockEntity> {

    private final static float MIN_X = (float) 0 /16;
    private final static float MAX_X = (float) 16 /16;
    private final static float MIN_Z = (float) 0 /16;
    private final static float MAX_Z = (float) 16 /16;
    private final static float MIN_Y = (float) 13 /16;

    private static ItemRenderer itemRenderer;

    public BloomeryBaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }


    @Override
    public void render(BloomeryBaseBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStackHandler itemHandler = blockEntity.getItemHandler();

        ItemStack coalStack = itemHandler.getStackInSlot(2);

        if(!coalStack.isEmpty()){

            poseStack.pushPose();

            ResourceLocation coalTexture = blockEntity.isLit() ?
                    MILostFavor.locate("block/burning_coal_block") :
                    coalStack.is(MILFItemTags.LIGNITE_COALS) ?
                            ResourceLocation.fromNamespaceAndPath("modern_industrialization", "block/lignite_coal_block") :
                            ResourceLocation.fromNamespaceAndPath("minecraft", "block/coal_block");

            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(coalTexture);

            float percent = (float) coalStack.getCount() / FirePitBlockEntity.COAL_CAPACITY;
            float maxY = MIN_Y + (percent * ((float) 3 / 16)) - 0.001f;

            int color = 0xFFFFFFFF;

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

            poseStack.popPose();

        }

        ItemStack stack0 = itemHandler.getStackInSlot(0);

        BlockPos pos = blockEntity.getBlockPos();
        int seed = pos.hashCode();
        int itemNumber = 0;

        if(!stack0.isEmpty()){
            poseStack.pushPose();
            for (int i = 0; i < stack0.getCount(); i++) {
                renderSingleItem(stack0, i, seed, poseStack, packedLight, packedOverlay, bufferSource, blockEntity.getLevel());

                itemNumber++;
            }
            poseStack.popPose();
        }

        ItemStack stack1 = itemHandler.getStackInSlot(1);

        if(!stack1.isEmpty()){
            poseStack.pushPose();
            for (int i = itemNumber; i < stack1.getCount() + itemNumber; i++) {
                renderSingleItem(stack1, i, seed, poseStack, packedLight, packedOverlay, bufferSource, blockEntity.getLevel());

            }
            poseStack.popPose();
        }

    }

    private void renderSingleItem(ItemStack stack, int index, int seed, PoseStack poseStack, int packedLight, int packedOverlay, MultiBufferSource bufferSource, Level level) {

        poseStack.pushPose();
        poseStack.translate(0.5, (double) 15 / 16 + 1/64f, 0.5);
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
