package dev.saeta.milf.registries.client;

import dev.saeta.milf.blocks.bloomery.BloomeryBaseBlockEntityRenderer;
import dev.saeta.milf.blocks.bronze_anvil.BronzeAnvilBlockEntityRenderer;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntityRenderer;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntityRenderer;
import dev.saeta.milf.blocks.kiln.KilnBlockEntityRenderer;
import dev.saeta.milf.blocks.pot_bellows.PotBellowsBlockEntityRenderer;
import dev.saeta.milf.blocks.roasting_contraption.RoastingContraptionBlockEntityRenderer;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class MILFBlockEntityRenderers {

    public static void register(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(MILFBlockEntities.CLAY_CRUCIBLE.get(), ClayCrucibleBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.POT_BELLOWS.get(), PotBellowsBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.ROASTING_CONTRAPTION.get(), RoastingContraptionBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.KILN.get(), KilnBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.FIRE_PIT.get(), FirePitBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.BRONZE_ANVIL.get(), BronzeAnvilBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.BLOOMERY_BASE.get(), BloomeryBaseBlockEntityRenderer::new);

    }

}
