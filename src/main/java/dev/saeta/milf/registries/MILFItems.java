package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.items.*;
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

    public static final DeferredItem<Item> CLAY_MOLD_INGOT = ITEMS.register("clay_mold_ingot", () -> new ClayMoldItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY), FluidType.BUCKET_VOLUME / 8));

    public static final DeferredItem<Item> UNFIRED_CLAY_MOLD_INGOT = ITEMS.register("unfired_clay_mold_ingot", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> WOODEN_AXE_HEAD = ITEMS.register("wooden_axe_head", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODEN_HAMMER_HEAD = ITEMS.register("wooden_hammer_head", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODEN_HOE_HEAD = ITEMS.register("wooden_hoe_head", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODEN_PICKAXE_HEAD = ITEMS.register("wooden_pickaxe_head", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODEN_SHOVEL_HEAD = ITEMS.register("wooden_shovel_head", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODEN_SWORD_BLADE = ITEMS.register("wooden_sword_blade", () -> new Item(new Item.Properties()));



    public static final DeferredItem<Item> IRON_BLOOM = ITEMS.register("iron_bloom", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CRUSHED_COPPER = ITEMS.register("crushed_copper", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRUSHED_GOLD = ITEMS.register("crushed_gold", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRUSHED_IRON = ITEMS.register("crushed_iron", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRUSHED_LEAD = ITEMS.register("crushed_lead", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRUSHED_TIN = ITEMS.register("crushed_tin", () -> new Item(new Item.Properties()));



    public static final DeferredItem<Item> FIRESTARTER = ITEMS.register("firestarter", () -> new FirestarterItem(new Item.Properties().stacksTo(1).durability(8)));
    public static final DeferredItem<Item> STONE_HAMMER = ITEMS.register("stone_hammer", () -> new Item(new Item.Properties().stacksTo(1).durability(109)));
    public static final DeferredItem<Item> FLINT_CHISEL = ITEMS.register("flint_chisel", () -> new ChiselItem(new Item.Properties().stacksTo(1).durability(64)));

    public static final DeferredItem<Item> CLUNKY_DRILL = ITEMS.register("clunky_drill", () -> new ClunkyDrillItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.IS_HORIZONTAL, true)));
    public static final DeferredItem<Item> BIG_BULKY_DRILL = ITEMS.register("big_bulky_drill", () -> new BigBulkyDrillItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
