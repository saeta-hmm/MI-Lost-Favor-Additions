package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.items.ChiselItem;
import dev.saeta.milf.items.ClayMoldItem;
import dev.saeta.milf.items.FirestarterItem;
import dev.saeta.milf.items.PotsherdItem;
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

    public static final DeferredItem<Item> FIRE_CLAY_BALL = ITEMS.register("fire_clay_ball", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> POTTERY_CLAY_BALL = ITEMS.register("pottery_clay_ball", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> UNFIRED_BRICK = ITEMS.register("unfired_brick", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> UNFIRED_FIRE_CLAY_BRICK = ITEMS.register("unfired_fire_clay_brick", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> WOODEN_AXE_HEAD = ITEMS.register("wooden_axe_head", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODEN_HAMMER_HEAD = ITEMS.register("wooden_hammer_head", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODEN_HOE_HEAD = ITEMS.register("wooden_hoe_head", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODEN_PICKAXE_HEAD = ITEMS.register("wooden_pickaxe_head", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODEN_SHOVEL_HEAD = ITEMS.register("wooden_shovel_head", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WOODEN_SWORD_BLADE = ITEMS.register("wooden_sword_blade", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> STONE_ROD = ITEMS.register("stone_rod", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> IRON_BLOOM = ITEMS.register("iron_bloom", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CRUSHED_COPPER = ITEMS.register("crushed_copper", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRUSHED_GOLD = ITEMS.register("crushed_gold", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRUSHED_IRON = ITEMS.register("crushed_iron", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRUSHED_LEAD = ITEMS.register("crushed_lead", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CRUSHED_TIN = ITEMS.register("crushed_tin", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> BRONZE_AXE_HEAD = ITEMS.register("bronze_axe_head", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> BRONZE_HAMMER_HEAD = ITEMS.register("bronze_hammer_head", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> BRONZE_HOE_HEAD = ITEMS.register("bronze_hoe_head", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> BRONZE_PICKAXE_HEAD = ITEMS.register("bronze_pickaxe_head", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> BRONZE_SHOVEL_HEAD = ITEMS.register("bronze_shovel_head", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> BRONZE_SWORD_BLADE = ITEMS.register("bronze_sword_blade", () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> LEAD_AXE_HEAD = ITEMS.register("lead_axe_head", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> LEAD_HAMMER_HEAD = ITEMS.register("lead_hammer_head", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> LEAD_HOE_HEAD = ITEMS.register("lead_hoe_head", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> LEAD_PICKAXE_HEAD = ITEMS.register("lead_pickaxe_head", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> LEAD_SHOVEL_HEAD = ITEMS.register("lead_shovel_head", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> LEAD_SWORD_BLADE = ITEMS.register("lead_sword_blade", () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> POTSHERD = ITEMS.register("potsherd", () -> new PotsherdItem(new Item.Properties().stacksTo(39)));

    public static final DeferredItem<Item> FIRESTARTER = ITEMS.register("firestarter", () -> new FirestarterItem(new Item.Properties().stacksTo(1).durability(8)));
    public static final DeferredItem<Item> STONE_HAMMER = ITEMS.register("stone_hammer", () -> new Item(new Item.Properties().stacksTo(1).durability(109)));
    public static final DeferredItem<Item> FLINT_CHISEL = ITEMS.register("flint_chisel", () -> new ChiselItem(new Item.Properties().stacksTo(1).durability(64)));


    public static final DeferredItem<Item> CLUNKY_DRILL = ITEMS.register("clunky_drill", () -> new ClunkyDrillItem(new Item.Properties().stacksTo(1).component(MILFDataComponents.IS_HORIZONTAL, true)));
    public static final DeferredItem<Item> BIG_BULKY_DRILL = ITEMS.register("big_bulky_drill", () -> new BigBulkyDrillItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
