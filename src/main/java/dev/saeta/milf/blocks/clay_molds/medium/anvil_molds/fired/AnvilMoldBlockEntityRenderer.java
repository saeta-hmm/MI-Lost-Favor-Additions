package dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.fired;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.client.render_stuff.AlphaMultiBufferSource;
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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix4f;

public class AnvilMoldBlockEntityRenderer implements BlockEntityRenderer<AnvilMoldBlockEntity> {

    private static BlockRenderDispatcher blockRenderDispatcher;

    private final static float MIN_X = (float) 5 /16;
    private final static float MAX_X = (float) 11 /16;
    private final static float MIN_Z = (float) 5 /16;
    private final static float MAX_Z = (float) 11 /16;
    private final static float MIN_Y = (float) 2 /16;
    private final static float MAX_Y = (float) 10 /16;

    private final RandomSource randomSource = RandomSource.create();
    private final static ResourceLocation CASTING_TEXTURE = MILostFavor.locate("block/overlay/bronze_casting");

    public AnvilMoldBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(AnvilMoldBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidTank fluidTank = blockEntity.getFluidTank();

        float progress = blockEntity.isSolidifying() ? blockEntity.getInterpolatedClientProgress(partialTick) : 1f;

        if(!fluidTank.isEmpty()){

            FluidStack fluidStack = fluidTank.getFluid().copy();

            IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(extensions.getStillTexture());


            float percent = (float) fluidStack.getAmount() / 4000;
            float maxY = MIN_Y + (percent * (MAX_Y - MIN_Y)) - 0.01f;

            int color = extensions.getTintColor(fluidStack);
            if(blockEntity.isSolidifying()) color = RenderUtil.modifyAlpha(color, 1f - progress);

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

        ItemStack stack0 = blockEntity.isSolidifying() ? blockEntity.getCurrentOutput() : blockEntity.getItemHandler().getStackInSlot(0);

        if(!stack0.isEmpty()){
            for (int i = 0; i < stack0.getCount(); i++) {

                renderAnvil(progress, blockEntity, poseStack, packedLight, packedOverlay, bufferSource, fluidTank);

            }
        }
    }

    private void renderAnvil(float progress, AnvilMoldBlockEntity blockEntity, PoseStack poseStack, int packedLight, int packedOverlay, MultiBufferSource bufferSource, FluidTank fluidTank){



        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(CASTING_TEXTURE);
        FluidStack fluidStack = fluidTank.getFluid().copy();

        float percent = (float) fluidStack.getAmount() / 4000;
        float maxY = MAX_Y - 0.01f;

        int color = 0xFFFFFFFF;
        if(blockEntity.isSolidifying()) color = RenderUtil.modifyAlpha(color, progress);

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

    //TODO something

//    private void renderAnvil(ItemStack stack, BlockEntity blockEntity, PoseStack poseStack, int packedLight, int packedOverlay, MultiBufferSource bufferSource, Level level){
//        if(!(stack.getItem() instanceof BlockItem blockItem)) return;
//        poseStack.pushPose();
//
//        poseStack.translate(0, -5.1/16f, 0);
//
//        //poseStack.mulPose(Axis.YN.rotationDegrees(90));
//
//        Direction direction = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
//        BlockState blockToRender = blockItem.getBlock().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, direction.getCounterClockWise());
//
//        poseStack.translate(0.5, 0.5, 0.5);
//
//        switch (direction) {
//            case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
//            case SOUTH -> poseStack.mulPose(Axis.XN.rotationDegrees(180));
//            case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(180));
//            case WEST -> poseStack.mulPose(Axis.ZN.rotationDegrees(180));
//            default -> {}
//        }
//
//        poseStack.scale(0.9f, 0.9f, 0.9f);
//
//        poseStack.translate(-0.5, -0.5, -0.5);
//
//        //poseStack.translate(0, 3/16f, 0);
//
//        BlockPos pos = blockEntity.getBlockPos().above(2);
//
//        if(level != null){
//
//            blockRenderDispatcher.renderSingleBlock(
//                    blockToRender,
//                    poseStack,
//                    bufferSource,
//                    packedLight,
//                    packedOverlay,
//                    ModelData.EMPTY,
//                    RenderType.translucent()
//            );
//
////            blockRenderDispatcher.getModelRenderer().tesselateBlock(
////                    level,
////                    blockRenderDispatcher.getBlockModel(blockToRender),
////                    blockToRender,
////                    pos,
////                    poseStack,
////                    bufferSource.getBuffer(RenderType.translucent()),
////                    false,
////                    randomSource,
////                    109109,
////                    packedOverlay,
////                    ModelData.EMPTY,
////                    RenderType.translucent()
////            );
//        }
//
//
//        poseStack.popPose();
//    }
}
