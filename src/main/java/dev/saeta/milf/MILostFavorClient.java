package dev.saeta.milf;

import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntityRenderer;
import dev.saeta.milf.client.items.SteamDrillTooltipComponent;
import dev.saeta.milf.items.mi.MILFSteamDrillTooltipData;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFFluids;
import dev.saeta.milf.registries.client.MILFBlockEntityRenderers;
import dev.saeta.milf.registries.client.MILFModelLayerLocations;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MILostFavor.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class MILostFavorClient {
    public MILostFavorClient(IEventBus modBus, ModContainer container) {

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modBus.addListener(MILFFluids::registerClient);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event){
        MILFBlockEntityRenderers.register(event);
    }

    @SubscribeEvent
    public static void registerModelLayerLocations(EntityRenderersEvent.RegisterLayerDefinitions event){
        MILFModelLayerLocations.register(event);
    }

    @SubscribeEvent
    private static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {

        event.register(MILFSteamDrillTooltipData.class, SteamDrillTooltipComponent::new);
    }
}
