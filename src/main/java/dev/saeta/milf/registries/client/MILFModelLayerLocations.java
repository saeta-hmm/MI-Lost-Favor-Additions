package dev.saeta.milf.registries.client;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.pot_bellows.PotBellowsBlockEntityRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class MILFModelLayerLocations {

    public static final ModelLayerLocation POT_BELLOWS = new ModelLayerLocation(MILostFavor.locate("pot_bellows"), "pot_bellows");


    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event){

        event.registerLayerDefinition(MILFModelLayerLocations.POT_BELLOWS, PotBellowsBlockEntityRenderer::createLayer);

    }
}
