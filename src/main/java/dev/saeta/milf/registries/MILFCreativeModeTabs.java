package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MILFCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MILostFavor.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MILF_TAB = CREATIVE_MODE_TAB.register("mi_lost_favor_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(MILFItems.CLAY_BUCKET.get()))
                    .title(Component.translatable("itemGroup.milf"))
                    .displayItems(((itemDisplayParameters, output) -> {

                        output.accept(MILFItems.CLAY_BUCKET);

                        output.accept(MILFItems.CLAY_MOLD_INGOT);
                        output.accept(MILFItems.CLAY_MOLD_AXE);
                        output.accept(MILFItems.CLAY_MOLD_HAMMER);
                        output.accept(MILFItems.CLAY_MOLD_HOE);
                        output.accept(MILFItems.CLAY_MOLD_PICKAXE);
                        output.accept(MILFItems.CLAY_MOLD_SWORD);
                        output.accept(MILFItems.CLAY_MOLD_SHOVEL);
                        output.accept(MILFItems.CLAY_PLATE);

                        output.accept(MILFItems.FIRESTARTER);

                        output.accept(MILFItems.CLUNKY_DRILL);
                        output.accept(MILFItems.BIG_BULKY_DRILL);

                        output.accept(MILFBlocks.KILN);
                        output.accept(MILFBlocks.FIRE_PIT);


                    })).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
