package dev.saeta.milf.datagen.models;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class MILFItemModelProvider extends ItemModelProvider {
    public MILFItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MILostFavor.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        basicItem(MILFItems.BRONZE_AXE_HEAD.get());
        basicItem(MILFItems.BRONZE_HAMMER_HEAD.get());
        basicItem(MILFItems.BRONZE_HOE_HEAD.get());
        basicItem(MILFItems.BRONZE_PICKAXE_HEAD.get());
        basicItem(MILFItems.BRONZE_SWORD_BLADE.get());
        basicItem(MILFItems.BRONZE_SHOVEL_HEAD.get());

        basicItem(MILFItems.LEAD_AXE_HEAD.get());
        basicItem(MILFItems.LEAD_HAMMER_HEAD.get());
        basicItem(MILFItems.LEAD_HOE_HEAD.get());
        basicItem(MILFItems.LEAD_PICKAXE_HEAD.get());
        basicItem(MILFItems.LEAD_SWORD_BLADE.get());
        basicItem(MILFItems.LEAD_SHOVEL_HEAD.get());

        basicItem(MILFItems.POTTERY_CLAY_BALL.get());
        basicItem(MILFItems.FIRE_CLAY_BALL.get());
        basicItem(MILFItems.UNFIRED_BRICK.get());
        basicItem(MILFItems.UNFIRED_FIRE_CLAY_BRICK.get());

        handheldItem(MILFItems.POTSHERD.get());


        //simpleBlockItemModel(MILFBlocks.UNFIRED_CLAY_MOLD_BASE);
        //simpleBlockItemModel(MILFBlocks.CLAY_MOLD_BASE);

        simpleBlockItemModel(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_HALF);
        simpleBlockItemModel(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_FULL);
        simpleBlockItemModel(MILFBlocks.CLAY_MOLD_ANVIL_FULL);
        basicItem(MILFBlocks.STONE_BASIN.get().asItem());

    }

    private void simpleBlockItemModel(DeferredBlock<? extends Block> block){
        String blockId = block.getId().getPath();
        withExistingParent(blockId, MILostFavor.locate("block/" + blockId));
    }



}
