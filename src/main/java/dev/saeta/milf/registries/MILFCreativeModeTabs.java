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

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MILF_TAB = CREATIVE_MODE_TAB.register("stcm_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(MILFItems.CLAY_BUCKET.get()))
                    .title(Component.translatable("itemGroup.milf"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(MILFItems.CLAY_BUCKET);
                        output.accept(MILFItems.FIRESTARTER);
                        output.accept(MILFBlocks.CLAY_CRUCIBLE);
                    })).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
