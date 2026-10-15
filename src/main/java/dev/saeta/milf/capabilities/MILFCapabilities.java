package dev.saeta.milf.capabilities;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = MILostFavor.MOD_ID)
public class MILFCapabilities {
    @SubscribeEvent
    public static void registerItemCapabilities(RegisterCapabilitiesEvent event){

        ((CapabilityProvider) MILFBlocks.CLAY_CRUCIBLE.get().asItem()).registerCapabilities(event);

        ((CapabilityProvider) MILFBlocks.CLAY_MOLD_AXE.get().asItem()).registerCapabilities(event);
        ((CapabilityProvider) MILFBlocks.CLAY_MOLD_HAMMER.get().asItem()).registerCapabilities(event);
        ((CapabilityProvider) MILFBlocks.CLAY_MOLD_HOE.get().asItem()).registerCapabilities(event);
        ((CapabilityProvider) MILFBlocks.CLAY_MOLD_PICKAXE.get().asItem()).registerCapabilities(event);
        ((CapabilityProvider) MILFBlocks.CLAY_MOLD_SWORD.get().asItem()).registerCapabilities(event);
        ((CapabilityProvider) MILFBlocks.CLAY_MOLD_SHOVEL.get().asItem()).registerCapabilities(event);

        ((CapabilityProvider) MILFBlocks.CLAY_MOLD_ANVIL_FULL.get().asItem()).registerCapabilities(event);
        ((CapabilityProvider) MILFBlocks.CLAY_MOLD_INGOT.get().asItem()).registerCapabilities(event);
        ((CapabilityProvider) MILFBlocks.CLAY_MOLD_ROD.get().asItem()).registerCapabilities(event);
        ((CapabilityProvider) MILFBlocks.CLAY_MOLD_BASE.get().asItem()).registerCapabilities(event);


        MILFBlocks.CLAY_CRUCIBLE.get().registerCapabilities(event);
        MILFBlocks.FIRE_PIT.get().registerCapabilities(event);
        MILFBlocks.ROASTING_CONTRAPTION.get().registerCapabilities(event);
        MILFBlocks.KILN.get().registerCapabilities(event);
        MILFBlocks.BRONZE_ANVIL.get().registerCapabilities(event);
        MILFBlocks.STONE_ANVIL.get().registerCapabilities(event);
        MILFBlocks.STONE_BASIN.get().registerCapabilities(event);

        MILFBlocks.BLOOMERY_BASE.get().registerCapabilities(event);
        MILFBlocks.UNFIRED_CLAY_PLATE.get().registerCapabilities(event);
        MILFBlocks.CLAY_PLATE.get().registerCapabilities(event);
        MILFBlocks.FIRE_PIT_PLATE.get().registerCapabilities(event);

        //all the molds
        MILFBlocks.CLAY_MOLD_HAMMER.get().registerCapabilities(event);

        MILFBlocks.UNFIRED_CLAY_MOLD_BASE.get().registerCapabilities(event);
        MILFBlocks.CLAY_MOLD_ANVIL_FULL.get().registerCapabilities(event);
        MILFBlocks.CLAY_MOLD_BASE.get().registerCapabilities(event);
        MILFBlocks.CLAY_MOLD_ROD.get().registerCapabilities(event);
        MILFBlocks.CLAY_MOLD_INGOT.get().registerCapabilities(event);

    }

}
