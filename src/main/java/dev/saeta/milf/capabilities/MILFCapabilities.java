package dev.saeta.milf.capabilities;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = MILostFavor.MOD_ID)
public class MILFCapabilities {
    @SubscribeEvent
    public static void registerItemCapabilities(RegisterCapabilitiesEvent event){

        ((CapabilityProvider) MILFItems.CLAY_BUCKET.get()).registerCapabilities(event);

        ((CapabilityProvider) MILFItems.CLAY_MOLD_AXE.get()).registerCapabilities(event);
        ((CapabilityProvider) MILFItems.CLAY_MOLD_HAMMER.get()).registerCapabilities(event);
        ((CapabilityProvider) MILFItems.CLAY_MOLD_HOE.get()).registerCapabilities(event);
        ((CapabilityProvider) MILFItems.CLAY_MOLD_INGOT.get()).registerCapabilities(event);
        ((CapabilityProvider) MILFItems.CLAY_MOLD_PICKAXE.get()).registerCapabilities(event);
        ((CapabilityProvider) MILFItems.CLAY_MOLD_SWORD.get()).registerCapabilities(event);
        ((CapabilityProvider) MILFItems.CLAY_MOLD_SHOVEL.get()).registerCapabilities(event);

        MILFBlocks.CLAY_CRUCIBLE.get().registerCapabilities(event);
        MILFBlocks.FIRE_PIT.get().registerCapabilities(event);

    }

}
