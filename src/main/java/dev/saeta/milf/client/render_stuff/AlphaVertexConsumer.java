package dev.saeta.milf.client.render_stuff;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.saeta.milf.MILostFavor;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;

public class AlphaVertexConsumer extends VertexConsumerWrapper {

    private final float alpha;

    public AlphaVertexConsumer(VertexConsumer parent, float alpha) {
        super(parent);

        this.alpha = alpha;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {

        parent.setColor(r, g, b, (int) (a * alpha));
        return this;
    }

}
