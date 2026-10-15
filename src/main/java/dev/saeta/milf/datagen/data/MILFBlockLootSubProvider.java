package dev.saeta.milf.datagen.data;

import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlock;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public class MILFBlockLootSubProvider extends BlockLootSubProvider {
    public MILFBlockLootSubProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected void generate() {
        dropSelf(MILFBlocks.UNFIRED_CLAY_MOLD_BASE.get());
        dropSelf(MILFBlocks.UNFIRED_CLAY_MOLD_INGOT.get());
        dropSelf(MILFBlocks.UNFIRED_CLAY_MOLD_ROD.get());
        dropSelf(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_HALF.get());
        dropSelf(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_FULL.get());

        dropSelf(MILFBlocks.UNFIRED_CLAY_MOLD_HOE.get());
        dropSelf(MILFBlocks.UNFIRED_CLAY_MOLD_AXE.get());
        dropSelf(MILFBlocks.UNFIRED_CLAY_MOLD_SHOVEL.get());
        dropSelf(MILFBlocks.UNFIRED_CLAY_MOLD_SWORD.get());
        dropSelf(MILFBlocks.UNFIRED_CLAY_MOLD_PICKAXE.get());
        dropSelf(MILFBlocks.UNFIRED_CLAY_MOLD_HAMMER.get());

        dropSelf(MILFBlocks.UNFIRED_CLAY_PLATE.get());
        dropSelf(MILFBlocks.UNFIRED_CLAY_CRUCIBLE.get());


        dropMold(MILFBlocks.CLAY_MOLD_HOE.get());
        dropMold(MILFBlocks.CLAY_MOLD_AXE.get());
        dropMold(MILFBlocks.CLAY_MOLD_SHOVEL.get());
        dropMold(MILFBlocks.CLAY_MOLD_SWORD.get());
        dropMold(MILFBlocks.CLAY_MOLD_PICKAXE.get());
        dropMold(MILFBlocks.CLAY_MOLD_HAMMER.get());

        dropSelf(MILFBlocks.CLAY_MOLD_BASE.get());


        dropMultislotMold(MILFBlocks.CLAY_MOLD_ROD.get());
        dropMultislotMold(MILFBlocks.CLAY_MOLD_INGOT.get());


        dropMold(MILFBlocks.CLAY_MOLD_ANVIL_FULL.get());

        dropSelf(MILFBlocks.CLAY_PLATE.get());
        add(MILFBlocks.CLAY_CRUCIBLE.get(), crucibleDrop(MILFBlocks.CLAY_CRUCIBLE.get()));

        dropSelf(MILFBlocks.ROASTING_CONTRAPTION.get());
        dropSelf(MILFBlocks.POT_BELLOWS.get());
        dropSelf(MILFBlocks.KILN.get());
        dropSelf(MILFBlocks.BRONZE_ANVIL.get());

        dropSelf(MILFBlocks.STONE_BASIN.get());
        dropSelf(MILFBlocks.POTTERY_CLAY.get());
        dropSelf(MILFBlocks.FIRE_CLAY.get());

        dropWhenSilkTouch(MILFBlocks.STONE_ANVIL.get());

        dropOther(MILFBlocks.FIRE_PIT.get(), Blocks.DIRT);

        dropMultiple(MILFBlocks.BLOOMERY_BASE.get(), Blocks.DIRT);


        add(MILFBlocks.BURNING_COAL.get(), noDrop());
        add(MILFBlocks.FIRE_PIT_PLATE.get(), noDrop());
        add(MILFBlocks.CHISELED_BLOCK.get(), noDrop());
        add(MILFBlocks.MOLDED_BLOCK.get(), noDrop());
    }



    private void dropMold(Block block){
        add(block, moldDrop(block));
    }

    private void dropMultislotMold(Block block){
        add(block, multislotMoldDrop(block));
    }

    private LootTable.Builder multislotMoldDrop(Block block){

        return LootTable.lootTable()
                .withPool(
                        applyExplosionCondition(block, LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(block)))
                                .apply(CopyComponentsFunction
                                        .copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                        .include(MILFDataComponents.FLUIDS.get())
                                        .include(MILFDataComponents.STACKS.get())
                                        .include(MILFDataComponents.SINGLE_STACK.get())
                                )
                );

    }

    private LootTable.Builder moldDrop(Block block){

        return LootTable.lootTable()
                .withPool(
                        applyExplosionCondition(block, LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(block)))
                                .apply(CopyComponentsFunction
                                        .copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                        .include(MILFDataComponents.FLUID.get())
                                        .include(MILFDataComponents.CAST_RESULT.get())
                                        .include(MILFDataComponents.SINGLE_STACK.get())
                                )
                );

    }

    private LootTable.Builder crucibleDrop(Block block){
        return LootTable.lootTable()

                .withPool(
                        applyExplosionCondition(block, LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(block)))
                                .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(MILFDataComponents.FLUID.get()))
                )

                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .setBonusRolls(ConstantValue.exactly(0))
                        .add(LootItem.lootTableItem(MILFBlocks.CLAY_PLATE.get()))
                        .when(
                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(MILFBlocks.CLAY_CRUCIBLE.get())
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ClayCrucibleBlock.SEALED, true))
                        )
                );
    }

    private void dropMultiple(Block block, ItemLike... drops){
        LootTable.Builder builder = createSingleItemTable(block);

        for(ItemLike drop : drops){
            builder.withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .setBonusRolls(ConstantValue.exactly(0))
                    .add(LootItem.lootTableItem(drop))
            );
        }

        add(block, builder);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return MILFBlocks.BLOCKS.getEntries().stream().map(entry -> (Block) entry.value()).toList();
    }
}
