package dev.saeta.milf.items;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = MILostFavor.MOD_ID)
public class ItemCapability {
    @SubscribeEvent
    public static void registerItemCapabilities(RegisterCapabilitiesEvent event){

        ((ItemCapabilityProvider) MILFItems.CLAY_BUCKET.get()).registerCapabilities(event);

        ((ItemCapabilityProvider) MILFItems.CLAY_MOLD_AXE.get()).registerCapabilities(event);
        ((ItemCapabilityProvider) MILFItems.CLAY_MOLD_HAMMER.get()).registerCapabilities(event);
        ((ItemCapabilityProvider) MILFItems.CLAY_MOLD_HOE.get()).registerCapabilities(event);
        ((ItemCapabilityProvider) MILFItems.CLAY_MOLD_INGOT.get()).registerCapabilities(event);
        ((ItemCapabilityProvider) MILFItems.CLAY_MOLD_PICKAXE.get()).registerCapabilities(event);
        ((ItemCapabilityProvider) MILFItems.CLAY_MOLD_SWORD.get()).registerCapabilities(event);
        ((ItemCapabilityProvider) MILFItems.CLAY_MOLD_SHOVEL.get()).registerCapabilities(event);
    }

}
