package dev.saeta.milf;

import com.mojang.logging.LogUtils;
import dev.saeta.milf.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(MILostFavor.MOD_ID)
public class MILostFavor {
    public static final String MOD_ID = "milf";
    public static final Logger LOGGER = LogUtils.getLogger();


    public MILostFavor(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        MILFBlocks.register(modEventBus);
        MILFItems.register(modEventBus);
        MILFBlockEntities.register(modEventBus);
        MILFFluids.register(modEventBus);
        MILFDataComponents.register(modEventBus);
        MILFCreativeModeTabs.register(modEventBus);
        MILFRecipeTypes.register(modEventBus);
        MILFRecipeSerializers.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation locate(String path){
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
