package dev.saeta.milf.compat.emi.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetTooltipHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TwoTexturesProgressWidget extends Widget implements WidgetTooltipHolder<TwoTexturesProgressWidget> {

    protected final ResourceLocation texture1;
    protected final ResourceLocation texture2;
    protected final int x, y;
    protected final int textureWidth, textureHeight;
    protected final Supplier<Float> progressSupplier;

    private BiFunction<Integer, Integer, List<ClientTooltipComponent>> tooltipSupplier = (mouseX, mouseY) -> List.of();


    public TwoTexturesProgressWidget(
            ResourceLocation texture1,ResourceLocation texture2, int x, int y, int textureWidth, int textureHeight, Supplier<Float> progressSupplier
    ) {
        super();

        this.texture1 = texture1;
        this.texture2 = texture2;

        this.x = x;
        this.y = y;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        this.progressSupplier = progressSupplier;

    }

    @Override
    public Bounds getBounds() {
        return new Bounds(x, y, textureWidth, textureHeight);
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        draw.blit(texture1, x, y, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        float progress = Mth.clamp(progressSupplier.get(), 0f, 1f);
        int filledWidth = Math.round(progress * textureWidth);

        if (filledWidth > 0) {
            draw.blit(texture2, x, y, 0, 0, filledWidth, textureHeight, textureWidth, textureHeight);
        }
    }

    @Override
    public TwoTexturesProgressWidget tooltip(BiFunction<Integer, Integer, List<ClientTooltipComponent>> tooltipSupplier) {
        this.tooltipSupplier = tooltipSupplier;
        return this;
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        return tooltipSupplier.apply(mouseX, mouseY);
    }
}
