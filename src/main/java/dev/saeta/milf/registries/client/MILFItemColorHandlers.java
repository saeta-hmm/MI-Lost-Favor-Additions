package dev.saeta.milf.registries.client;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.neoforge.registries.DeferredBlock;

@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class MILFItemColorHandlers {

    @SubscribeEvent
    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event){
        registerMold(MILFBlocks.CLAY_CRUCIBLE, event);

        registerMold(MILFBlocks.CLAY_MOLD_HAMMER, event);
        registerMold(MILFBlocks.CLAY_MOLD_HOE, event);
        registerMold(MILFBlocks.CLAY_MOLD_SWORD, event);
        registerMold(MILFBlocks.CLAY_MOLD_PICKAXE, event);
        registerMold(MILFBlocks.CLAY_MOLD_SHOVEL, event);
        registerMold(MILFBlocks.CLAY_MOLD_AXE, event);

    }

    private static void registerMold(DeferredBlock<? extends Block> block, RegisterColorHandlersEvent.Item event){
        event.register(new DynamicFluidContainerModel.Colors(), block.get().asItem());
    }

}
