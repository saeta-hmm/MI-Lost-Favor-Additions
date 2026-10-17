package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.basin.StoneBasinBlock;
import dev.saeta.milf.blocks.bloomery.unfired.UnfiredBloomeryBaseBlock;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.fired.AnvilMoldBlock;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.unfired.UnfiredAnvilMoldBlock;
import dev.saeta.milf.blocks.anvils.bronze_anvil.BronzeAnvilBlock;
import dev.saeta.milf.blocks.anvils.stone_anvil.StoneAnvilBlock;
import dev.saeta.milf.blocks.bloomery.fired.BloomeryBaseBlock;
import dev.saeta.milf.blocks.bloomery.fired.BloomeryBaseBlockItem;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlock;
import dev.saeta.milf.blocks.clay_crucible.UnfiredClayCrucibleBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.AbstractFiredMoldBaseBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.MultislotMoldBlockItem;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.base.MoldBaseBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.ingot.IngotMoldBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.rod.RodMoldBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.unfired.UnfiredMoldBaseBlock;
import dev.saeta.milf.blocks.clay_molds.small.fire_pit.FirePitPlateBlock;
import dev.saeta.milf.blocks.clay_molds.small.fired.ClayMoldBlock;
import dev.saeta.milf.blocks.clay_molds.small.fired.ClayPlateBlock;
import dev.saeta.milf.blocks.clay_molds.small.unfired.UnfiredClayPlateBlock;
import dev.saeta.milf.blocks.fire_pit.FirePitBlock;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockItem;
import dev.saeta.milf.blocks.kiln.fired.KilnBlock;
import dev.saeta.milf.blocks.kiln.unfired.UnfiredKilnBlock;
import dev.saeta.milf.blocks.pot_bellows.PotBellowsBlock;
import dev.saeta.milf.blocks.roasting_contraption.RoastingContraptionBlock;
import dev.saeta.milf.blocks.roasting_contraption.RoastingContraptionBlockItem;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlock;
import dev.saeta.milf.blocks.shapeable_blocks.clay.MoldedBlock;
import dev.saeta.milf.blocks.stone_pole.StonePoleBlock;
import dev.saeta.milf.items.ClayBucketItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class MILFBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MILostFavor.MOD_ID);

    public static final DeferredBlock<Block> BURNING_COAL = registerBlock("burning_coal_block", () -> new Block(BlockBehaviour.Properties.of()));
    public static final DeferredBlock<Block> POTTERY_CLAY = registerBlock("pottery_clay", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD)));
    public static final DeferredBlock<Block> FIRE_CLAY = registerBlock("fire_clay", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD)));

    public static final DeferredBlock<FirePitBlock> FIRE_PIT = registerBlock("fire_pit",
            () -> new FirePitBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.ROOTED_DIRT)
                    .strength(0.6F,0.8f)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 0)),
            (block) -> new FirePitBlockItem(block.get(), new Item.Properties())
    );

    public static final DeferredBlock<BloomeryBaseBlock> BLOOMERY_BASE = registerBlock("bloomery_base",
            () -> new BloomeryBaseBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.DECORATED_POT)
                    .strength(0.6F,0.8f)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 8 : 0)),
            (block) -> new BloomeryBaseBlockItem(block.get(), new Item.Properties())
    );

    public static final DeferredBlock<UnfiredBloomeryBaseBlock> UNFIRED_BLOOMERY_BASE = registerBlock("unfired_bloomery_base",
            () -> new UnfiredBloomeryBaseBlock(BlockBehaviour.Properties.of()
                    .strength(0.5f,2)
                    .sound(SoundType.MUD)
                    .noOcclusion())

    );

    public static final DeferredBlock<RoastingContraptionBlock> ROASTING_CONTRAPTION = registerBlock("roasting_contraption",
            () -> new RoastingContraptionBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.BAMBOO_WOOD)
                    .strength(0.4F,0.8f)
                    .noOcclusion()),
            (block) -> new RoastingContraptionBlockItem(block.get(), new Item.Properties())
    );

    public static final DeferredBlock<UnfiredClayCrucibleBlock> UNFIRED_CLAY_CRUCIBLE = registerBlock("unfired_clay_crucible",
            () -> new UnfiredClayCrucibleBlock(BlockBehaviour.Properties.of()
                    .strength(0.3f,2)
                    .sound(SoundType.MUD)),
            (block) -> new UnfiredClayCrucibleBlock.UnfiredClayBucketItem(block.get(), new Item.Properties().stacksTo(1))
    );

    public static final DeferredBlock<FirePitPlateBlock> FIRE_PIT_PLATE = registerBlock("fire_pit_plate",
            () -> new FirePitPlateBlock(BlockBehaviour.Properties.of().strength(0.3f,2)
    ));

    public static final DeferredBlock<UnfiredClayPlateBlock> UNFIRED_CLAY_PLATE = registerBlock("unfired_clay_plate",
            () -> new UnfiredClayPlateBlock(BlockBehaviour.Properties.of()),
            (block) -> new UnfiredClayPlateBlock.UnfiredClayPlateItem(block.get(), new Item.Properties())
    );

    public static final DeferredBlock<UnfiredClayPlateBlock> UNFIRED_CLAY_MOLD_AXE = registerBlock("unfired_clay_mold_axe",
            () -> new UnfiredClayPlateBlock(BlockBehaviour.Properties.of()),
            (block) -> new UnfiredClayPlateBlock.UnfiredClayPlateItem(block.get(), new Item.Properties())
    );
    public static final DeferredBlock<UnfiredClayPlateBlock> UNFIRED_CLAY_MOLD_HAMMER = registerBlock("unfired_clay_mold_hammer",
            () -> new UnfiredClayPlateBlock(BlockBehaviour.Properties.of()),
            (block) -> new UnfiredClayPlateBlock.UnfiredClayPlateItem(block.get(), new Item.Properties())
    );
    public static final DeferredBlock<UnfiredClayPlateBlock> UNFIRED_CLAY_MOLD_HOE = registerBlock("unfired_clay_mold_hoe",
            () -> new UnfiredClayPlateBlock(BlockBehaviour.Properties.of()),
            (block) -> new UnfiredClayPlateBlock.UnfiredClayPlateItem(block.get(), new Item.Properties())
    );
    public static final DeferredBlock<UnfiredClayPlateBlock> UNFIRED_CLAY_MOLD_PICKAXE = registerBlock("unfired_clay_mold_pickaxe",
            () -> new UnfiredClayPlateBlock(BlockBehaviour.Properties.of()),
            (block) -> new UnfiredClayPlateBlock.UnfiredClayPlateItem(block.get(), new Item.Properties())
    );
    public static final DeferredBlock<UnfiredClayPlateBlock> UNFIRED_CLAY_MOLD_SHOVEL = registerBlock("unfired_clay_mold_shovel",
            () -> new UnfiredClayPlateBlock(BlockBehaviour.Properties.of()),
            (block) -> new UnfiredClayPlateBlock.UnfiredClayPlateItem(block.get(), new Item.Properties())
    );
    public static final DeferredBlock<UnfiredClayPlateBlock> UNFIRED_CLAY_MOLD_SWORD = registerBlock("unfired_clay_mold_sword",
            () -> new UnfiredClayPlateBlock(BlockBehaviour.Properties.of()),
            (block) -> new UnfiredClayPlateBlock.UnfiredClayPlateItem(block.get(), new Item.Properties())
    );

    public static final DeferredBlock<UnfiredMoldBaseBlock> UNFIRED_CLAY_MOLD_BASE = registerBlock("unfired_clay_mold_base",
            () -> new UnfiredMoldBaseBlock(BlockBehaviour.Properties.of()
                    .strength(0.6f,2)
                    .sound(SoundType.MUD))
    );

    public static final DeferredBlock<UnfiredMoldBaseBlock> UNFIRED_CLAY_MOLD_INGOT = registerBlock("unfired_clay_mold_ingot",
            () -> new UnfiredMoldBaseBlock(BlockBehaviour.Properties.of()
                    .strength(0.6f,2)
                    .sound(SoundType.MUD))
    );

    public static final DeferredBlock<UnfiredMoldBaseBlock> UNFIRED_CLAY_MOLD_ROD = registerBlock("unfired_clay_mold_rod",
            () -> new UnfiredMoldBaseBlock(BlockBehaviour.Properties.of()
                    .strength(0.6f,2)
                    .sound(SoundType.MUD))
    );

    public static final DeferredBlock<MoldBaseBlock> CLAY_MOLD_BASE = registerBlock("clay_mold_base",
            () -> new MoldBaseBlock(BlockBehaviour.Properties.of()
                    .strength(1f,2)
                    .sound(SoundType.DECORATED_POT)),
            (block) -> new AbstractFiredMoldBaseBlock.BaseMoldBlockItem(block.get(), new Item.Properties())
    );

    public static final DeferredBlock<IngotMoldBlock> CLAY_MOLD_INGOT = registerBlock("clay_mold_ingot",
            () -> new IngotMoldBlock(BlockBehaviour.Properties.of()
                    .strength(1f,2)
                    .sound(SoundType.DECORATED_POT)),
            (block) -> new MultislotMoldBlockItem(block.get(), new Item.Properties(), 250, 3)
    );

    public static final DeferredBlock<RodMoldBlock> CLAY_MOLD_ROD = registerBlock("clay_mold_rod",
            () -> new RodMoldBlock(BlockBehaviour.Properties.of()
                    .strength(1f,2)
                    .sound(SoundType.DECORATED_POT)),
            (block) -> new MultislotMoldBlockItem(block.get(), new Item.Properties(), 125, 4)
    );

    public static final DeferredBlock<UnfiredMoldBaseBlock> UNFIRED_CLAY_MOLD_ANVIL_HALF = registerBlock("unfired_clay_mold_anvil_half",
            () -> new UnfiredMoldBaseBlock(BlockBehaviour.Properties.of()
                    .strength(0.6f,2)
                    .sound(SoundType.MUD))
    );


    public static final DeferredBlock<UnfiredAnvilMoldBlock> UNFIRED_CLAY_MOLD_ANVIL_FULL = registerBlock("unfired_clay_mold_anvil_full",
            () -> new UnfiredAnvilMoldBlock(BlockBehaviour.Properties.of()
                    .strength(0.6f,2)
                    .sound(SoundType.MUD))
    );
    public static final DeferredBlock<AnvilMoldBlock> CLAY_MOLD_ANVIL_FULL = registerBlock("clay_mold_anvil_full",
            () -> new AnvilMoldBlock(BlockBehaviour.Properties.of()
                    .strength(1,2)
                    .sound(SoundType.DECORATED_POT)),
            (block) -> new AnvilMoldBlock.AnvilMoldBlockItem(block.get(), new Item.Properties())
    );

    public static final DeferredBlock<UnfiredKilnBlock> UNFIRED_KILN = registerBlock("unfired_kiln",
            () -> new UnfiredKilnBlock(BlockBehaviour.Properties.of()
                    .strength(0.9f,2)
                    .sound(SoundType.MUD)
                    .noOcclusion())

    );



    public static final DeferredBlock<ClayPlateBlock> CLAY_PLATE = registerBlock("clay_plate",
            () -> new ClayPlateBlock(BlockBehaviour.Properties.of()),
            (block) -> new ClayPlateBlock.ClayPlateItem(block.get(), new Item.Properties())
    );

    public static final DeferredBlock<ClayMoldBlock> CLAY_MOLD_AXE = registerBlock("clay_mold_axe",
            () -> new ClayMoldBlock(BlockBehaviour.Properties.of()),
            (block) -> new ClayMoldBlock.ClayMoldItem(block.get(), new Item.Properties(), FluidType.BUCKET_VOLUME / 4)
    );

    public static final DeferredBlock<ClayMoldBlock> CLAY_MOLD_HAMMER = registerBlock("clay_mold_hammer",
            () -> new ClayMoldBlock(BlockBehaviour.Properties.of()),
            (block) -> new ClayMoldBlock.ClayMoldItem(block.get(), new Item.Properties(), FluidType.BUCKET_VOLUME)
    );

    public static final DeferredBlock<ClayMoldBlock> CLAY_MOLD_HOE = registerBlock("clay_mold_hoe",
            () -> new ClayMoldBlock(BlockBehaviour.Properties.of()),
            (block) -> new ClayMoldBlock.ClayMoldItem(block.get(), new Item.Properties(), FluidType.BUCKET_VOLUME / 5)
    );

    public static final DeferredBlock<ClayMoldBlock> CLAY_MOLD_PICKAXE = registerBlock("clay_mold_pickaxe",
            () -> new ClayMoldBlock(BlockBehaviour.Properties.of()),
            (block) -> new ClayMoldBlock.ClayMoldItem(block.get(), new Item.Properties(),FluidType.BUCKET_VOLUME / 4)
    );

    public static final DeferredBlock<ClayMoldBlock> CLAY_MOLD_SHOVEL = registerBlock("clay_mold_shovel",
            () -> new ClayMoldBlock(BlockBehaviour.Properties.of()),
            (block) -> new ClayMoldBlock.ClayMoldItem(block.get(), new Item.Properties(),FluidType.BUCKET_VOLUME / 8)
    );

    public static final DeferredBlock<ClayMoldBlock> CLAY_MOLD_SWORD = registerBlock("clay_mold_sword",
            () -> new ClayMoldBlock(BlockBehaviour.Properties.of()),
            (block) -> new ClayMoldBlock.ClayMoldItem(block.get(), new Item.Properties(), 175)
    );

    public static final DeferredBlock<ClayCrucibleBlock> CLAY_CRUCIBLE = registerBlock("clay_crucible", () -> new ClayCrucibleBlock(BlockBehaviour.Properties.of()
            .strength(1,2)
            .sound(SoundType.DECORATED_POT)),
            (block) -> new ClayBucketItem(block.get(), new Item.Properties())
    );

    public static final DeferredBlock<PotBellowsBlock> POT_BELLOWS = registerBlock("pot_bellows", () -> new PotBellowsBlock(BlockBehaviour.Properties.of()
            .strength(1,1)
            .sound(SoundType.DECORATED_POT)
    ));

    public static final DeferredBlock<BronzeAnvilBlock> BRONZE_ANVIL = registerBlock("bronze_anvil", () -> new BronzeAnvilBlock(BlockBehaviour.Properties.of()
            .strength(1,4)
            .sound(SoundType.ANVIL)
            .noOcclusion()
            .forceSolidOn()
    ));

    public static final DeferredBlock<StoneAnvilBlock> STONE_ANVIL = registerBlock("stone_anvil", () -> new StoneAnvilBlock(BlockBehaviour.Properties.of()
            .strength(1.3f, 6)
            .sound(SoundType.STONE)
            .noOcclusion()
            .forceSolidOn()
    ));

    public static final DeferredBlock<StonePoleBlock> STONE_POLE = registerBlock("stone_pole", () -> new StonePoleBlock(BlockBehaviour.Properties.of()
            .strength(1.3f, 6)
            .sound(SoundType.STONE)
    ));

    public static final DeferredBlock<StoneBasinBlock> STONE_BASIN = registerBlock("stone_basin", () -> new StoneBasinBlock(BlockBehaviour.Properties.of()
            .strength(1.3f, 6)
            .sound(SoundType.STONE)
            .noOcclusion()
            .forceSolidOn()
    ));

    public static final DeferredBlock<ChiseledBlock> CHISELED_BLOCK = registerBlock("chiseled_block", () -> new ChiseledBlock(BlockBehaviour.Properties.of()
            .strength(1.5f, 6)
            .sound(SoundType.STONE)
            .noOcclusion()
            .forceSolidOn()
    ));

    public static final DeferredBlock<MoldedBlock> MOLDED_BLOCK = registerBlock("molded_block", () -> new MoldedBlock(BlockBehaviour.Properties.of()
            .strength(1f, 6)
            .sound(SoundType.MUD)
            .noOcclusion()
            .forceSolidOn()
    ));

    public static final DeferredBlock<KilnBlock> KILN = registerBlock("kiln",
            () -> new KilnBlock(BlockBehaviour.Properties.of()
                    .strength(1,4)
                    .sound(SoundType.DECORATED_POT)
                    .noOcclusion()),
            (block) -> new BlockItem(block.get(), new Item.Properties()
                    .stacksTo(1)
                    .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
                    .component(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder().add(
                            Attributes.ARMOR,
                            new AttributeModifier(MILostFavor.locate("helmet_armor"), 2, AttributeModifier.Operation.ADD_VALUE),
                            EquipmentSlotGroup.HEAD
                    ).build())
            )
    );

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> blockSupplier){
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        MILFItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> blockSupplier, Function<DeferredBlock<T>, ? extends Item> itemFactory){
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        MILFItems.ITEMS.register(name, () -> itemFactory.apply(block));
        return block;
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

}
