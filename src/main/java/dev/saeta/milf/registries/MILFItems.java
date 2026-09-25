package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.items.ClayBucketItem;
import dev.saeta.milf.items.ClayMoldItem;
import dev.saeta.milf.items.FirestarterItem;
import dev.saeta.milf.items.mi.BigBulkyDrillItem;
import dev.saeta.milf.items.mi.ClunkyDrillItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MILFItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MILostFavor.MOD_ID);

    public static final DeferredItem<Item> CLAY_BUCKET = ITEMS.register("clay_bucket", () -> new ClayBucketItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY)));

    public static final DeferredItem<Item> CLAY_MOLD_AXE = ITEMS.register("clay_mold_axe", () -> new ClayMoldItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY), FluidType.BUCKET_VOLUME / 4));
    public static final DeferredItem<Item> CLAY_MOLD_HAMMER = ITEMS.register("clay_mold_hammer", () -> new ClayMoldItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY), FluidType.BUCKET_VOLUME));
    public static final DeferredItem<Item> CLAY_MOLD_HOE = ITEMS.register("clay_mold_hoe", () -> new ClayMoldItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY), FluidType.BUCKET_VOLUME / 5));
    public static final DeferredItem<Item> CLAY_MOLD_PICKAXE = ITEMS.register("clay_mold_pickaxe", () -> new ClayMoldItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY), FluidType.BUCKET_VOLUME / 4));
    public static final DeferredItem<Item> CLAY_MOLD_SHOVEL = ITEMS.register("clay_mold_shovel", () -> new ClayMoldItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY), FluidType.BUCKET_VOLUME / 8));
    public static final DeferredItem<Item> CLAY_MOLD_SWORD = ITEMS.register("clay_mold_sword", () -> new ClayMoldItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY), FluidType.BUCKET_VOLUME / 5));
    public static final DeferredItem<Item> CLAY_MOLD_INGOT = ITEMS.register("clay_mold_ingot", () -> new ClayMoldItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY), FluidType.BUCKET_VOLUME / 8));

    public static final DeferredItem<Item> CLAY_PLATE = ITEMS.register("clay_plate", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> FIRESTARTER = ITEMS.register("firestarter", () -> new FirestarterItem(new Item.Properties().stacksTo(1).durability(8)));

    public static final DeferredItem<Item> CLUNKY_DRILL = ITEMS.register("clunky_drill", () -> new ClunkyDrillItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.IS_HORIZONTAL, true)));
    public static final DeferredItem<Item> BIG_BULKY_DRILL = ITEMS.register("big_bulky_drill", () -> new BigBulkyDrillItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
