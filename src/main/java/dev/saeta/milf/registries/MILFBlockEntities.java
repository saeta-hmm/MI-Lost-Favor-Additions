package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.basin.StoneBasinBlockEntity;
import dev.saeta.milf.blocks.bloomery.unfired.UnfiredBloomeryBaseBlockEntity;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.fired.AnvilMoldBlockEntity;
import dev.saeta.milf.blocks.UnfiredBlockEntity;
import dev.saeta.milf.blocks.anvils.bronze_anvil.BronzeAnvilBlockEntity;
import dev.saeta.milf.blocks.anvils.stone_anvil.StoneAnvilBlockEntity;
import dev.saeta.milf.blocks.bloomery.fired.BloomeryBaseBlockEntity;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.unfired.UnfiredAnvilMoldBlock;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.unfired.UnfiredAnvilMoldBlockEntity;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.base.MoldBaseBlockEntity;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.ingot.IngotMoldBlockEntity;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.rod.RodMoldBlockEntity;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.unfired.UnfiredMoldBaseBlockEntity;
import dev.saeta.milf.blocks.clay_molds.small.fire_pit.FirePitPlateBlockEntity;
import dev.saeta.milf.blocks.clay_molds.small.fired.ClayMoldBlockEntity;
import dev.saeta.milf.blocks.clay_molds.small.fired.ClayPlateBlockEntity;
import dev.saeta.milf.blocks.clay_molds.small.unfired.UnfiredClayPlateBlockEntity;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntity;
import dev.saeta.milf.blocks.kiln.fired.KilnBlockEntity;
import dev.saeta.milf.blocks.kiln.unfired.UnfiredKilnBlockEntity;
import dev.saeta.milf.blocks.pot_bellows.PotBellowsBlockEntity;
import dev.saeta.milf.blocks.roasting_contraption.RoastingContraptionBlockEntity;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlockEntity;
import dev.saeta.milf.blocks.shapeable_blocks.clay.MoldedBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MILFBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MILostFavor.MOD_ID);

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ClayCrucibleBlockEntity>> CLAY_CRUCIBLE = BLOCK_ENTITIES.register(
            "clay_crucible", () -> BlockEntityType.Builder.of(ClayCrucibleBlockEntity::new, MILFBlocks.CLAY_CRUCIBLE.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<PotBellowsBlockEntity>> POT_BELLOWS = BLOCK_ENTITIES.register(
            "pot_bellows", () -> BlockEntityType.Builder.of(PotBellowsBlockEntity::new, MILFBlocks.POT_BELLOWS.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<KilnBlockEntity>> KILN = BLOCK_ENTITIES.register(
            "kiln", () -> BlockEntityType.Builder.of(KilnBlockEntity::new, MILFBlocks.KILN.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<FirePitBlockEntity>> FIRE_PIT = BLOCK_ENTITIES.register(
            "fire_pit", () -> BlockEntityType.Builder.of(FirePitBlockEntity::new, MILFBlocks.FIRE_PIT.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<BloomeryBaseBlockEntity>> BLOOMERY_BASE = BLOCK_ENTITIES.register(
            "bloomery_base", () -> BlockEntityType.Builder.of(BloomeryBaseBlockEntity::new, MILFBlocks.BLOOMERY_BASE.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<BronzeAnvilBlockEntity>> BRONZE_ANVIL = BLOCK_ENTITIES.register(
            "bronze_anvil", () -> BlockEntityType.Builder.of(BronzeAnvilBlockEntity::new, MILFBlocks.BRONZE_ANVIL.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<StoneAnvilBlockEntity>> STONE_ANVIL = BLOCK_ENTITIES.register(
            "stone_anvil", () -> BlockEntityType.Builder.of(StoneAnvilBlockEntity::new, MILFBlocks.STONE_ANVIL.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<StoneBasinBlockEntity>> STONE_BASIN = BLOCK_ENTITIES.register(
            "stone_basin", () -> BlockEntityType.Builder.of(StoneBasinBlockEntity::new, MILFBlocks.STONE_BASIN.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ChiseledBlockEntity>> CHISELED_BLOCK = BLOCK_ENTITIES.register(
            "chiseled_block", () -> BlockEntityType.Builder.of(ChiseledBlockEntity::new, MILFBlocks.CHISELED_BLOCK.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<MoldedBlockEntity>> MOLDED_BLOCK = BLOCK_ENTITIES.register(
            "molded_block", () -> BlockEntityType.Builder.of(MoldedBlockEntity::new, MILFBlocks.MOLDED_BLOCK.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<FirePitPlateBlockEntity>> FIRE_PIT_PLATE = BLOCK_ENTITIES.register(
            "fire_pit_plate", () -> BlockEntityType.Builder.of(FirePitPlateBlockEntity::new, MILFBlocks.FIRE_PIT_PLATE.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<UnfiredBlockEntity>> UNFIRED_BLOCK = BLOCK_ENTITIES.register(
            "unfired_block", () -> BlockEntityType.Builder.of((pos, state) -> {
                        if(state.is(MILFBlocks.UNFIRED_KILN)) {
                            return new UnfiredKilnBlockEntity(pos, state);
                        } else if (state.is(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_FULL)) {
                            return new UnfiredAnvilMoldBlockEntity(pos,state);
                        } else {
                            return new UnfiredBloomeryBaseBlockEntity(pos, state);
                        }
                    },
                    MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_FULL.get(),
                    MILFBlocks.UNFIRED_KILN.get(),
                    MILFBlocks.UNFIRED_BLOOMERY_BASE.get()

            ).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<AnvilMoldBlockEntity>> CLAY_MOLD_ANVIL_FULL = BLOCK_ENTITIES.register(
            "clay_mold_anvil_full", () -> BlockEntityType.Builder.of(AnvilMoldBlockEntity::new, MILFBlocks.CLAY_MOLD_ANVIL_FULL.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<UnfiredMoldBaseBlockEntity>> UNFIRED_MOLD_BASE = BLOCK_ENTITIES.register(
            "unfired_mold_base", () -> BlockEntityType.Builder.of(
                    UnfiredMoldBaseBlockEntity::new,
                    MILFBlocks.UNFIRED_CLAY_MOLD_BASE.get(),
                    MILFBlocks.UNFIRED_CLAY_MOLD_INGOT.get(),
                    MILFBlocks.UNFIRED_CLAY_MOLD_ROD.get(),
                    MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_HALF.get()
            ).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<MoldBaseBlockEntity>> CLAY_MOLD_BASE = BLOCK_ENTITIES.register(
            "clay_mold_base", () -> BlockEntityType.Builder.of(
                    MoldBaseBlockEntity::new,
                    MILFBlocks.CLAY_MOLD_BASE.get()
            ).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<IngotMoldBlockEntity>> CLAY_MOLD_INGOT = BLOCK_ENTITIES.register(
            "clay_mold_ingot", () -> BlockEntityType.Builder.of(IngotMoldBlockEntity::new, MILFBlocks.CLAY_MOLD_INGOT.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<RodMoldBlockEntity>> CLAY_MOLD_ROD = BLOCK_ENTITIES.register(
            "clay_mold_rod", () -> BlockEntityType.Builder.of(RodMoldBlockEntity::new, MILFBlocks.CLAY_MOLD_ROD.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<UnfiredClayPlateBlockEntity>> UNFIRED_CLAY_PLATE = BLOCK_ENTITIES.register(
            "unfired_clay_plate", () -> BlockEntityType.Builder.of(
                    UnfiredClayPlateBlockEntity::new,
                    MILFBlocks.UNFIRED_CLAY_PLATE.get(),
                    MILFBlocks.UNFIRED_CLAY_MOLD_AXE.get(),
                    MILFBlocks.UNFIRED_CLAY_MOLD_HAMMER.get(),
                    MILFBlocks.UNFIRED_CLAY_MOLD_PICKAXE.get(),
                    MILFBlocks.UNFIRED_CLAY_MOLD_SHOVEL.get(),
                    MILFBlocks.UNFIRED_CLAY_MOLD_SWORD.get(),
                    MILFBlocks.UNFIRED_CLAY_MOLD_HOE.get()
            ).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ClayPlateBlockEntity>> CLAY_PLATE = BLOCK_ENTITIES.register(
            "clay_plate", () -> BlockEntityType.Builder.of(
                    ClayPlateBlockEntity::new,
                    MILFBlocks.CLAY_PLATE.get()
            ).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ClayMoldBlockEntity>> CLAY_MOLD = BLOCK_ENTITIES.register(
            "clay_mold", () -> BlockEntityType.Builder.of(
                    ClayMoldBlockEntity::new,
                    MILFBlocks.CLAY_MOLD_AXE.get(),
                    MILFBlocks.CLAY_MOLD_HAMMER.get(),
                    MILFBlocks.CLAY_MOLD_PICKAXE.get(),
                    MILFBlocks.CLAY_MOLD_SHOVEL.get(),
                    MILFBlocks.CLAY_MOLD_SWORD.get(),
                    MILFBlocks.CLAY_MOLD_HOE.get()
            ).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<RoastingContraptionBlockEntity>> ROASTING_CONTRAPTION = BLOCK_ENTITIES.register(
            "roasting_contraption", () -> BlockEntityType.Builder.of(RoastingContraptionBlockEntity::new, MILFBlocks.ROASTING_CONTRAPTION.get()).build(null)
    );

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }

}
