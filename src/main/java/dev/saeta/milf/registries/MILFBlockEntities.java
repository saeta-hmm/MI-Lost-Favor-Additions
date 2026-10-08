package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.anvils.stone_anvil.StoneAnvilBlockEntity;
import dev.saeta.milf.blocks.bloomery.BloomeryBaseBlockEntity;
import dev.saeta.milf.blocks.anvils.bronze_anvil.BronzeAnvilBlockEntity;
import dev.saeta.milf.blocks.clay_plates.fired.ClayPlateBlockEntity;
import dev.saeta.milf.blocks.clay_plates.unfired.UnfiredClayPlateBlockEntity;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlockEntity;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntity;
import dev.saeta.milf.blocks.kiln.KilnBlockEntity;
import dev.saeta.milf.blocks.pot_bellows.PotBellowsBlockEntity;
import dev.saeta.milf.blocks.roasting_contraption.RoastingContraptionBlockEntity;
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

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ChiseledBlockEntity>> CHISELED_BLOCK = BLOCK_ENTITIES.register(
            "chiseled_block", () -> BlockEntityType.Builder.of(ChiseledBlockEntity::new, MILFBlocks.CHISELED_BLOCK.get()).build(null)
    );

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<MoldedBlockEntity>> MOLDED_BLOCK = BLOCK_ENTITIES.register(
            "molded_block", () -> BlockEntityType.Builder.of(MoldedBlockEntity::new, MILFBlocks.MOLDED_BLOCK.get()).build(null)
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
                    MILFBlocks.CLAY_PLATE.get(),
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
