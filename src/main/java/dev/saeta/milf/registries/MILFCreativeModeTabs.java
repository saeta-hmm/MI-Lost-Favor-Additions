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

                        output.accept(MILFItems.WOODEN_AXE_HEAD);
                        output.accept(MILFItems.WOODEN_HAMMER_HEAD);
                        output.accept(MILFItems.WOODEN_HOE_HEAD);
                        output.accept(MILFItems.WOODEN_PICKAXE_HEAD);
                        output.accept(MILFItems.WOODEN_SWORD_BLADE);
                        output.accept(MILFItems.WOODEN_SHOVEL_HEAD);

                        output.accept(MILFBlocks.UNFIRED_CLAY_CRUCIBLE);

                        output.accept(MILFItems.UNFIRED_CLAY_MOLD_INGOT);
                        output.accept(MILFBlocks.UNFIRED_CLAY_MOLD_AXE);
                        output.accept(MILFBlocks.UNFIRED_CLAY_MOLD_HAMMER);
                        output.accept(MILFBlocks.UNFIRED_CLAY_MOLD_HOE);
                        output.accept(MILFBlocks.UNFIRED_CLAY_MOLD_PICKAXE);
                        output.accept(MILFBlocks.UNFIRED_CLAY_MOLD_SWORD);
                        output.accept(MILFBlocks.UNFIRED_CLAY_MOLD_SHOVEL);

                        output.accept(MILFBlocks.UNFIRED_CLAY_PLATE);

                        output.accept(MILFItems.CLAY_BUCKET);

                        output.accept(MILFItems.CLAY_MOLD_INGOT);
                        output.accept(MILFBlocks.CLAY_MOLD_AXE);
                        output.accept(MILFBlocks.CLAY_MOLD_HAMMER);
                        output.accept(MILFBlocks.CLAY_MOLD_HOE);
                        output.accept(MILFBlocks.CLAY_MOLD_PICKAXE);
                        output.accept(MILFBlocks.CLAY_MOLD_SWORD);
                        output.accept(MILFBlocks.CLAY_MOLD_SHOVEL);

                        output.accept(MILFBlocks.CLAY_PLATE);



                        output.accept(MILFItems.IRON_BLOOM);

                        output.accept(MILFItems.CRUSHED_COPPER);
                        output.accept(MILFItems.CRUSHED_GOLD);
                        output.accept(MILFItems.CRUSHED_IRON);
                        output.accept(MILFItems.CRUSHED_LEAD);
                        output.accept(MILFItems.CRUSHED_TIN);


                        output.accept(MILFItems.FIRESTARTER);
                        output.accept(MILFItems.STONE_HAMMER);
                        output.accept(MILFItems.FLINT_CHISEL);


                        output.accept(MILFBlocks.KILN);
                        output.accept(MILFBlocks.FIRE_PIT);
                        output.accept(MILFBlocks.POT_BELLOWS);
                        output.accept(MILFBlocks.ROASTING_CONTRAPTION);
                        output.accept(MILFBlocks.BRONZE_ANVIL);
                        output.accept(MILFBlocks.STONE_ANVIL);

                        output.accept(MILFBlocks.BLOOMERY_BASE);

                        output.accept(MILFItems.CLUNKY_DRILL);
                        output.accept(MILFItems.BIG_BULKY_DRILL);


                    })).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
