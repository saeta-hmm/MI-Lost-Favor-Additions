package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MILFBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MILostFavor.MOD_ID);

    public static final DeferredBlock<ClayCrucibleBlock> CLAY_CRUCIBLE = registerBlock("clay_crucible", () -> new ClayCrucibleBlock(BlockBehaviour.Properties.of()
            .strength(2,1)
            .sound(SoundType.DECORATED_POT)
    ));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> blockSupplier){
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        MILFItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

}
