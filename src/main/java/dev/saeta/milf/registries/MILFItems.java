package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.items.ClayBucketItem;
import dev.saeta.milf.items.FirestarterItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MILFItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MILostFavor.MOD_ID);

    public static final DeferredItem<Item> CLAY_BUCKET = ITEMS.register("clay_bucket", () -> new ClayBucketItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY)));
    public static final DeferredItem<Item> FIRESTARTER = ITEMS.register("firestarter", () -> new FirestarterItem(new Item.Properties().stacksTo(1).durability(8)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
