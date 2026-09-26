package dev.saeta.milf.registries.client;

import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntityRenderer;
import dev.saeta.milf.blocks.pot_bellows.PotBellowsBlockEntityRenderer;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class MILFBlockEntityRenderers {

    public static void register(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(MILFBlockEntities.CLAY_CRUCIBLE.get(), ClayCrucibleBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.POT_BELLOWS.get(), PotBellowsBlockEntityRenderer::new);
    }

}
