package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntity;
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

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<FirePitBlockEntity>> FIRE_PIT = BLOCK_ENTITIES.register(
            "fire_pit", () -> BlockEntityType.Builder.of(FirePitBlockEntity::new, MILFBlocks.FIRE_PIT.get()).build(null)
    );

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }

}
