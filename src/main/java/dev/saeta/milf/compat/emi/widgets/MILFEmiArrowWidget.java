package dev.saeta.milf.compat.emi.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetTooltipHolder;
import dev.saeta.milf.MILostFavor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class MILFEmiArrowWidget extends Widget implements WidgetTooltipHolder<MILFEmiArrowWidget> {

    private final static ResourceLocation ARROWS_TEXTURE = MILostFavor.locate("textures/gui/emi_arrows.png");

    private final int x, y;
    private final int u, v;
    private final int textureWidth, textureHeight;
    private final Supplier<Float> progressSupplier;

    private final boolean isProgressArrow;

    private BiFunction<Integer, Integer, List<ClientTooltipComponent>> tooltipSupplier = (mouseX, mouseY) -> List.of();


    public MILFEmiArrowWidget(EmiProgressArrow arrow, int x, int y){
        this.x = x;
        this.y = y;

        this.u = arrow.u;
        this.v = arrow.v;

        this.textureHeight = arrow.height;
        this.textureWidth = arrow.width;

        this.isProgressArrow = false;
        this.progressSupplier = () -> 109f;
    }

    public MILFEmiArrowWidget(EmiProgressArrow arrow, int x, int y, int time){
        this.x = x;
        this.y = y;

        this.u = arrow.u;
        this.v = arrow.v;

        this.textureHeight = arrow.height;
        this.textureWidth = arrow.width;

        this.isProgressArrow = true;
        this.progressSupplier = () -> {
            long totalMs = (long) time * 50;
            if (totalMs <= 0) return 1f;

            return (System.currentTimeMillis() % totalMs) / (float) totalMs;
        };

        tooltip(((something, noIdea) -> Collections.singletonList(
                ClientTooltipComponent.create(
                        Component.translatable("emi.category.milf.clay_crucible.seconds_tooltip", time /20)
                                .getVisualOrderText()
                )
        )));

    }


    @Override
    public Bounds getBounds() {
        return new Bounds(x, y, textureWidth, textureHeight);
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        if(isProgressArrow){
            draw.blit(ARROWS_TEXTURE, x, y, u, v, textureWidth, textureHeight, 256, 256);

            float progress = Mth.clamp(progressSupplier.get(), 0f, 1f);
            int filledWidth = Math.round(progress * textureWidth);

            if (filledWidth > 0) {
                draw.blit(ARROWS_TEXTURE, x, y, u, v + textureHeight, filledWidth, textureHeight, 256, 256);
            }
        } else {
            draw.blit(ARROWS_TEXTURE, x, y, u, v, textureWidth, textureHeight, 256, 256);
        }


    }

    @Override
    public MILFEmiArrowWidget tooltip(BiFunction<Integer, Integer, List<ClientTooltipComponent>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        return tooltipSupplier.apply(mouseX, mouseY);
    }
}