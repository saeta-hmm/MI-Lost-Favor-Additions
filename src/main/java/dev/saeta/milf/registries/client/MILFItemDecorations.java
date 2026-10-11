package dev.saeta.milf.registries.client;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.client.decorators.MoldsItemDecorator;
import dev.saeta.milf.registries.MILFBlocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class MILFItemDecorations {

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event){

        event.register(MILFBlocks.CLAY_MOLD_PICKAXE, new MoldsItemDecorator());
        event.register(MILFBlocks.CLAY_MOLD_AXE, new MoldsItemDecorator());
        event.register(MILFBlocks.CLAY_MOLD_HAMMER, new MoldsItemDecorator());
        event.register(MILFBlocks.CLAY_MOLD_HOE, new MoldsItemDecorator());
        event.register(MILFBlocks.CLAY_MOLD_SHOVEL, new MoldsItemDecorator());
        event.register(MILFBlocks.CLAY_MOLD_SWORD, new MoldsItemDecorator());


    }

}
