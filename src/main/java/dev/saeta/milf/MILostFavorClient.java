package dev.saeta.milf;

import dev.saeta.milf.client.items.SteamDrillTooltipComponent;
import dev.saeta.milf.client.overlay.AnvilMinigame;
import dev.saeta.milf.client.shaping.Chiseling;
import dev.saeta.milf.client.shaping.Pottery;
import dev.saeta.milf.items.mi.MILFSteamDrillTooltipData;
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
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = MILostFavor.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class MILostFavorClient {
    public MILostFavorClient(IEventBus modBus, ModContainer container) {

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modBus.addListener(MILFFluids::registerClient);

        NeoForge.EVENT_BUS.addListener(Chiseling::onRenderHand);
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

    @SubscribeEvent
    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(AnvilMinigame.ID, AnvilMinigame::render);
        event.registerAboveAll(Chiseling.ID, Chiseling::render);
        event.registerAboveAll(Pottery.ID, Pottery::render);

    }

    @SubscribeEvent
    private static void registerMouseInputHandlers(InputEvent.MouseButton.Pre inputEvent){
        AnvilMinigame.handleClick(inputEvent);
    }

    @SubscribeEvent
    private static void registerClientTickHandlers(ClientTickEvent.Pre event){
        AnvilMinigame.onClientTick(event);
    }
}
