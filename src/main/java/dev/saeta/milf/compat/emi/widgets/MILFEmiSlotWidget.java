package dev.saeta.milf.compat.emi.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import dev.saeta.milf.MILostFavor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class MILFEmiSlotWidget extends SlotWidget {

    private static final ResourceLocation SLOTS_TEXTURE = MILostFavor.locate("textures/gui/emi_slots.png");

    private EmiSlotPointer pointer;
    private final EmiSlot slotType;

    public MILFEmiSlotWidget(EmiIngredient stack, int x, int y, EmiSlot slotType) {
        super(stack, x, y);
        this.customBackground(SLOTS_TEXTURE, slotType.getU(), slotType.getV(), slotType.getWidth(), slotType.getHeight());
        this.slotType = slotType;
    }

    public MILFEmiSlotWidget withPointer(EmiSlotPointer pointer){
        this.pointer = pointer;
        return this;
    }

    public MILFEmiSlotWidget customSize(int x, int y){
        this.customBackground(SLOTS_TEXTURE, slotType.getU(), slotType.getV(), x, y);
        return this;
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {

        if(pointer != null){

            PoseStack poseStack = draw.pose();

            if(pointer.topLeft){
                draw.blit(SLOTS_TEXTURE, x - pointer.getXOffset(),y - pointer.getYOffset(), pointer.type.u, pointer.type.v, pointer.type.width, pointer.type.height);
            }

            if (pointer.topRight) {
                poseStack.pushPose();
                poseStack.translate(x + slotType.getWidth(), y, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                draw.blit(SLOTS_TEXTURE, -pointer.getXOffset(), -pointer.getYOffset(), pointer.type.u, pointer.type.v, pointer.type.width, pointer.type.height);
                poseStack.popPose();
            }

            if (pointer.bottomLeft) {
                poseStack.pushPose();
                poseStack.translate(x, y + slotType.getHeight(), 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(270));
                draw.blit(SLOTS_TEXTURE, -pointer.getXOffset(), -pointer.getYOffset(), pointer.type.u, pointer.type.v, pointer.type.width, pointer.type.height);
                poseStack.popPose();
            }

            if (pointer.bottomRight) {
                poseStack.pushPose();
                poseStack.translate(x + slotType.getWidth(), y + slotType.getHeight(), 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                draw.blit(SLOTS_TEXTURE, -pointer.getXOffset(), -pointer.getYOffset(), pointer.type.u, pointer.type.v, pointer.type.width, pointer.type.height);
                poseStack.popPose();
            }
        }

        super.render(draw, mouseX, mouseY, delta);
    }
}
