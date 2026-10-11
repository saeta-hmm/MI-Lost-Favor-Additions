package dev.saeta.milf.client.render_stuff;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.Map;

public class AlphaMultiBufferSource implements MultiBufferSource {

    private final MultiBufferSource multiBufferSource;
    private final float alpha;

    public AlphaMultiBufferSource(MultiBufferSource multiBufferSource, float alpha) {
        this.multiBufferSource = multiBufferSource;
        this.alpha = alpha;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        return new AlphaVertexConsumer(multiBufferSource.getBuffer(renderType), alpha);
    }
}
