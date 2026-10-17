package dev.saeta.milf.registries.client;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class MILFItemProperties {

    @SubscribeEvent
    public static void register(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            ItemProperties.register(
                    MILFItems.WATERSKIN.get(),
                    MILostFavor.locate("fluid"),
                    (stack, level, entity, seed) -> {
                        IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
                        if(fluidHandler != null && !fluidHandler.getFluidInTank(0).isEmpty()){
                            return 1f;
                        }

                        return 0f;
                    }
            );
        });

    }

}
