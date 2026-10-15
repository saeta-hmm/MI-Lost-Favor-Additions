package dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.clay_molds.AbstractMultislotCastableMoldBlockEntity;
import dev.saeta.milf.client.render_stuff.AlphaMultiBufferSource;
import dev.saeta.milf.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

public abstract class AbstractMultislotMoldBaseBlockEntityRenderer<T extends AbstractMultislotCastableMoldBlockEntity> implements BlockEntityRenderer<T > {

    protected final static float MIN_X = (float) 5 /16;
    protected final static float MAX_X = (float) 11 /16;
    protected final static float MIN_Z = (float) 5 /16;
    protected final static float MAX_Z = (float) 11 /16;
    protected final static float MIN_Y = (float) 1 /16;
    protected final static float MAX_Y = (float) 4.015 /16;

    private final static ResourceLocation CASTING_TEXTURE = MILostFavor.locate("block/overlay/bronze_casting");

    private static ItemRenderer itemRenderer;

    public AbstractMultislotMoldBaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        IFluidHandler fluidHandler = blockEntity.getFluidHandler();

        for (int i = 0; i < fluidHandler.getTanks(); i++) {
            if(!fluidHandler.getFluidInTank(i).isEmpty()) renderFluid(poseStack, i, partialTick ,blockEntity, bufferSource, packedLight, packedOverlay);
        }

        int slot = 0;

        for (int i = 0; i < blockEntity.getSlots(); i++) {
            ItemStack stack = blockEntity.isSolidifying(i) ? blockEntity.getCurrentOutput(i) : blockEntity.getItemHandler().getStackInSlot(i);

            if(!stack.isEmpty()){
                float progress = blockEntity.isSolidifying(i) ? blockEntity.getInterpolatedClientProgress(partialTick, i) : 1f;
                AlphaMultiBufferSource wrappedBufferSource = new AlphaMultiBufferSource(bufferSource,progress);
                renderSingleItem(stack, progress,i,poseStack, packedLight, packedOverlay, wrappedBufferSource, blockEntity);
            }
        }



    }

    protected void renderFluid(PoseStack poseStack, int index, float partialTick, T blockEntity, MultiBufferSource bufferSource, int packedLight, int packedOverlay){
        poseStack.pushPose();

        FluidTank fluidTank = blockEntity.getFluidTank(index);
        FluidStack fluidStack = fluidTank.getFluid().copy();

        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture());

        Direction direction = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);

        int slots = blockEntity.getSlots();
        float slotLength = (getFluidMaxOffset() - getFluidMinOffset()) / slots;

        float minX, maxX, minZ, maxZ;

        switch (direction) {
            case NORTH -> {
                maxX = getFluidMaxOffset() - slotLength * index;
                minX = maxX - slotLength;
                minZ = 4 / 16f;
                maxZ = 12 /16f;
            }
            case SOUTH -> {
                minX = getFluidMinOffset() + index * slotLength;
                maxX = minX + slotLength;
                minZ = 4 / 16f;
                maxZ = 12 /16f;
            }
            case WEST -> {
                minX = 4 /16f;
                maxX = 12 /16f;
                minZ = getFluidMinOffset() + index * slotLength;
                maxZ = minZ + slotLength;
            }
            case EAST -> {
                minX = 4 /16f;
                maxX = 12 /16f;
                maxZ = getFluidMaxOffset() - index * slotLength;
                minZ = maxZ - slotLength;

            }
            default -> {
                minX = 4 /16f;
                maxX = 12 /16f;
                minZ = 4 /16f;
                maxZ = 12 /16f;
            }
        }

        float progress = blockEntity.isSolidifying(index) ? blockEntity.getInterpolatedClientProgress(partialTick, index) : 1f;
        int color = extensions.getTintColor(fluidStack);

        if(blockEntity.isSolidifying(index)) color = RenderUtil.modifyAlpha(color, 1f - progress);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        RenderUtil.renderUpFaceWithSprite(
                consumer,
                matrix,
                sprite,
                true,
                minX, maxX,
                MAX_Y,
                minZ, maxZ,
                packedLight, packedOverlay,
                color
        );

        poseStack.popPose();
    }

    protected void renderSingleItem(ItemStack stack, float progress,int index, PoseStack poseStack, int packedLight, int packedOverlay, MultiBufferSource bufferSource, T blockEntity){


        poseStack.pushPose();
        //poseStack.mulPose(Axis.XN.rotationDegrees(90));
        int slots = blockEntity.getSlots();


        Direction direction = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        applyItemTransforms(direction, poseStack, slots, index);

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
    }

    protected abstract float getItemYOffset();
    protected abstract void applyItemTransforms(Direction direction, PoseStack poseStack, int slots, int index);

    protected abstract float getFluidMinOffset();
    protected abstract float getFluidMaxOffset();

}
