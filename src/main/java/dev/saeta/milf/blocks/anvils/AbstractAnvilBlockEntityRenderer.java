package dev.saeta.milf.blocks.anvils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.saeta.milf.blocks.anvils.bronze_anvil.BronzeAnvilBlockEntity;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class AbstractAnvilBlockEntityRenderer<T extends  AbstractAnvilBlockEntity> implements BlockEntityRenderer<T> {

    private static ItemRenderer itemRenderer;

    public AbstractAnvilBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }


    @Override
    public void render(AbstractAnvilBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStackHandler itemHandler = blockEntity.getItemHandler();

        BlockPos pos = blockEntity.getBlockPos();

        int seed = pos.hashCode();

        ItemStack stack0 = itemHandler.getStackInSlot(0);

        if(!stack0.isEmpty()){
            for (int i = 0; i < stack0.getCount(); i++) {

                renderSingleItem(stack0, i, seed, poseStack, packedLight, packedOverlay, bufferSource, blockEntity.getLevel());

            }
        }
    }

    private void renderSingleItem(ItemStack stack, int index, int seed, PoseStack poseStack, int packedLight, int packedOverlay, MultiBufferSource bufferSource, Level level){
        poseStack.pushPose();
        poseStack.translate(0.5, (double) 7 / 16 + 1/64f, 0.5);
        poseStack.scale(0.8F, 0.8F, 0.8F);
        poseStack.translate(0, 1.5 / 16 * index * 0.5, 0);

        poseStack.mulPose(Axis.XN.rotationDegrees(90));

        Integer stackSeedComponent = stack.get(MILFDataComponents.RANDOM_SEED);

        int stackSeed = 0;
        if(stackSeedComponent != null) stackSeed = stackSeedComponent;

        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (Math.abs(Math.sin(seed + stackSeed + index * 109109) % 1) * 360)));

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
