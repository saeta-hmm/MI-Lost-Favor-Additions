package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.kiln.KilnBlock;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EquipableCarvedPumpkinBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class MILFBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MILostFavor.MOD_ID);

    public static final DeferredBlock<Block> BURNING_COAL = registerBlock("burning_coal_block", () -> new Block(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<ClayCrucibleBlock> CLAY_CRUCIBLE = registerBlock("clay_crucible", () -> new ClayCrucibleBlock(BlockBehaviour.Properties.of()
            .strength(1,1)
            .sound(SoundType.DECORATED_POT)
    ));

    public static final DeferredBlock<KilnBlock> KILN = registerBlock("kiln",
            () -> new KilnBlock(BlockBehaviour.Properties.of()
                .strength(1,1)
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
