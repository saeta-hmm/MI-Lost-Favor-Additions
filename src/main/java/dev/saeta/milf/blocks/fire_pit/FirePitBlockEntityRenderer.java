package dev.saeta.milf.blocks.fire_pit;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.saeta.milf.blocks.kiln.KilnBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.joml.Matrix4f;

public class FirePitBlockEntityRenderer implements BlockEntityRenderer<FirePitBlockEntity> {

    private static BlockRenderDispatcher blockRenderDispatcher;
    private final RandomSource randomSource = RandomSource.create();

    private final static float MIN_X = (float) 0 /16;
    private final static float MAX_X = (float) 16 /16;
    private final static float MIN_Z = (float) 0 /16;
    private final static float MAX_Z = (float) 16 /16;
    private final static float MIN_Y = (float) 14 /16;

    public FirePitBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();

    }

    @Override
    public void render(FirePitBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ItemStackHandler itemHandler = blockEntity.getItemHandler();

        ItemStack coalStack = itemHandler.getStackInSlot(FirePitBlockEntity.COAL_SLOT);

        if(!coalStack.isEmpty()){

            poseStack.pushPose();

            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(ResourceLocation.fromNamespaceAndPath("minecraft", "block/coal_block"));

            float percent = (float) coalStack.getCount() / FirePitBlockEntity.COAL_CAPACITY;
            float maxY = MIN_Y + (percent * ((float) 2 / 16)) - 0.001f;

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
    }
}
