package dev.saeta.milf.client.decorators;

import dev.saeta.milf.networking.data.StoredItem;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;

public class MoldsItemDecorator implements IItemDecorator {

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {

        ItemStack castResultItem = stack.getOrDefault(MILFDataComponents.CAST_RESULT,new StoredItem(ItemStack.EMPTY.copy()) ).stack();

        if(!castResultItem.isEmpty()){
            //random bs GO!!!

//            float animationProgress = RenderUtil.getInterpolatedProgress(109 * 4);
//            float halfProgress = RenderUtil.EasingFunctions.easeInOutQuad( (animationProgress * 2) % 1  );
//            float progress = (animationProgress < 0.5f) ? (halfProgress * 0.5f) : (0.5f + halfProgress * 0.5f);
//            float progress010 = RenderUtil.mapProgressTo010(progress);
//
//            float angle = (float) (progress * Math.PI * 2 + Math.PI / 2);
//
//            float denominator = (float) (1 + Math.pow(Math.sin(angle), 2));
//
//            float offsetX = (float) ((2 * Math.cos(angle)) / denominator);
//            float offsetY = (float) ((2 * Math.cos(angle) * Math.sin(angle)) / denominator);
//
//            poseStack.translate(offsetX,offsetY,20);
//
//            poseStack.translate(xOffset + 8,yOffset + 8,150);
//
//            poseStack.mulPose(Axis.YP.rotationDegrees(RenderUtil.expandProgressToNegativeValues(progress010) * 360));
//            //poseStack.mulPose(Axis.XP.rotationDegrees(RenderUtil.expandProgressToNegativeValues(1 - progress010) * 360));
//
//            poseStack.translate(-xOffset - 8,-yOffset - 8, - 150);



            if(castResultItem.getItem() instanceof BlockItem blockItem){
                return false;
            }


            guiGraphics.renderFakeItem(castResultItem, xOffset, yOffset);
        }
        return false;
    }
}
