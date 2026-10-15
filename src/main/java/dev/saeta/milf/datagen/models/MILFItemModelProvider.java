package dev.saeta.milf.datagen.models;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFFluids;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class MILFItemModelProvider extends ItemModelProvider {
    public MILFItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MILostFavor.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        basicItem(MILFItems.WOODEN_AXE_HEAD.get());
        basicItem(MILFItems.WOODEN_HAMMER_HEAD.get());
        basicItem(MILFItems.WOODEN_HOE_HEAD.get());
        basicItem(MILFItems.WOODEN_PICKAXE_HEAD.get());
        basicItem(MILFItems.WOODEN_SHOVEL_HEAD.get());
        basicItem(MILFItems.WOODEN_SWORD_BLADE.get());

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

        basicItem(MILFItems.CRUSHED_COPPER.get());
        basicItem(MILFItems.CRUSHED_GOLD.get());
        basicItem(MILFItems.CRUSHED_IRON.get());
        basicItem(MILFItems.CRUSHED_LEAD.get());
        basicItem(MILFItems.CRUSHED_TIN.get());

        basicItem(MILFItems.IRON_BLOOM.get());

        basicItem(MILFBlocks.UNFIRED_CLAY_MOLD_AXE.get().asItem());
        basicItem(MILFBlocks.UNFIRED_CLAY_MOLD_PICKAXE.get().asItem());
        basicItem(MILFBlocks.UNFIRED_CLAY_MOLD_SWORD.get().asItem());
        basicItem(MILFBlocks.UNFIRED_CLAY_MOLD_SHOVEL.get().asItem());
        basicItem(MILFBlocks.UNFIRED_CLAY_MOLD_HOE.get().asItem());
        basicItem(MILFBlocks.UNFIRED_CLAY_MOLD_HAMMER.get().asItem());
        basicItem(MILFBlocks.UNFIRED_CLAY_PLATE.get().asItem());

        basicItem(MILFBlocks.STONE_BASIN.get().asItem());
        basicItem(MILFBlocks.FIRE_PIT.get().asItem());
        basicItem(MILFBlocks.BLOOMERY_BASE.get().asItem());
        basicItem(MILFBlocks.CLAY_PLATE.get().asItem());
        basicItem(MILFBlocks.ROASTING_CONTRAPTION.get().asItem());

        withExistingParent(MILFBlocks.UNFIRED_CLAY_CRUCIBLE.getId().toString(), mcLoc("item/generated")).texture("layer0", MILostFavor.locate("item/unfired_clay_bucket"));

        handheldItem(MILFItems.POTSHERD.get());
        handheldItem(MILFItems.BIG_BULKY_DRILL.get());
        handheldItem(MILFItems.CLUNKY_DRILL.get());
        handheldItem(MILFItems.FIRESTARTER.get());
        handheldItem(MILFItems.STONE_HAMMER.get());
        handheldItem(MILFItems.FLINT_CHISEL.get());

        simpleBlockItemModel(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_HALF);
        simpleBlockItemModel(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_FULL);
        simpleBlockItemModel(MILFBlocks.CLAY_MOLD_ANVIL_FULL);
        simpleBlockItemModel(MILFBlocks.POT_BELLOWS);
        simpleBlockItemModel(MILFBlocks.STONE_ANVIL);
        simpleBlockItemModel(MILFBlocks.BRONZE_ANVIL);
        simpleBlockItemModel(MILFBlocks.KILN);

        moldBlockItem(MILFBlocks.CLAY_MOLD_AXE);
        moldBlockItem(MILFBlocks.CLAY_MOLD_PICKAXE);
        moldBlockItem(MILFBlocks.CLAY_MOLD_SWORD);
        moldBlockItem(MILFBlocks.CLAY_MOLD_SHOVEL);
        moldBlockItem(MILFBlocks.CLAY_MOLD_HOE);
        moldBlockItem(MILFBlocks.CLAY_MOLD_HAMMER);
        moldBlockItem(MILFBlocks.CLAY_CRUCIBLE,
                MILostFavor.locate("item/clay_bucket"),
                MILostFavor.locate("item/bucket_overlay")
        );



    }

    private void simpleBlockItemModel(DeferredBlock<? extends Block> block){
        String blockId = block.getId().getPath();
        withExistingParent(blockId, MILostFavor.locate("block/" + blockId));
    }

    private void moldBlockItem(
            DeferredBlock<? extends Block> block
    ) {
        moldBlockItem(
                block,
                MILostFavor.locate("item/" + block.getId().getPath()),
                MILostFavor.locate("item/"+ block.getId().getPath()+"_overlay")
        );
    }

    private void moldBlockItem(
            DeferredBlock<? extends Block> block,
            ResourceLocation baseTexture,
            ResourceLocation fluidTexture

    ){
        withExistingParent(block.getId().getPath(), ResourceLocation.fromNamespaceAndPath("neoforge", "default"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(MILFFluids.DUMMY_FLUID.get())
                .coverIsMask(false)
                .applyTint(true)
                .flipGas(true)
                .end()
                .texture("base", baseTexture)
                .texture("fluid", fluidTexture)
        ;
    }

}
