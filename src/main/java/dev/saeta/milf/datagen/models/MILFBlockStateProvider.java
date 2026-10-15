package dev.saeta.milf.datagen.models;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.AbstractAnvilMoldBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.AbstractMoldBaseBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.AbstractFiredMoldBaseBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.base.MoldBaseBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.unfired.UnfiredMoldBaseBlock;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class MILFBlockStateProvider extends BlockStateProvider {
    public MILFBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MILostFavor.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        //horizontalBlock(MILFBlocks.UNFIRED_CLAY_MOLD_BASE.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_base")));
        horizontalBlock(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_HALF.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_anvil_half")));

        unfiredBaseMold(MILFBlocks.UNFIRED_CLAY_MOLD_BASE, MILostFavor.locate("block/overlay/mold_base_overlay_blank"));
        unfiredBaseMold(MILFBlocks.UNFIRED_CLAY_MOLD_INGOT, MILostFavor.locate("block/overlay/unfired_mold_base_overlay_ingots"));
        unfiredBaseMold(MILFBlocks.UNFIRED_CLAY_MOLD_ROD, MILostFavor.locate("block/overlay/unfired_mold_base_overlay_rods"));

        baseMold(MILFBlocks.CLAY_MOLD_BASE,
                MILostFavor.locate("block/overlay/mold_base_overlay_blank"),
                MILostFavor.locate("block/overlay/mold_base_overlay_blank")
        );

        baseMold(MILFBlocks.CLAY_MOLD_INGOT,
                MILostFavor.locate("block/overlay/mold_base_overlay_ingots"),
                MILostFavor.locate("block/overlay/mold_base_overlay_overlay_ingots")
        );

        baseMold(MILFBlocks.CLAY_MOLD_ROD,
                MILostFavor.locate("block/overlay/mold_base_overlay_rods"),
                MILostFavor.locate("block/overlay/mold_base_overlay_overlay_rods")
        );


//        BlockModelBuilder unfiredMoldIngotsModel = models()
//                .withExistingParent("block/unfired_clay_mold_ingot", MILostFavor.locate("block/unfired_clay_mold_base"))
//                .texture("1", MILostFavor.locate("block/overlay/unfired_mold_base_overlay_ingots"));
//
//        horizontalBlock(MILFBlocks.UNFIRED_CLAY_MOLD_INGOT.get(), unfiredMoldIngotsModel);
//        simpleBlockItem(MILFBlocks.UNFIRED_CLAY_MOLD_INGOT.get(), unfiredMoldIngotsModel);
//
//        BlockModelBuilder unfiredRoldRodsModel = models()
//                .withExistingParent("block/unfired_clay_mold_rod", MILostFavor.locate("block/unfired_clay_mold_base"))
//                .texture("1", MILostFavor.locate("block/overlay/unfired_mold_base_overlay_rods"));
//
//        horizontalBlock(MILFBlocks.UNFIRED_CLAY_MOLD_ROD.get(), unfiredRoldRodsModel);
//        simpleBlockItem(MILFBlocks.UNFIRED_CLAY_MOLD_ROD.get(), unfiredRoldRodsModel);

//        horizontalBlock(MILFBlocks.CLAY_MOLD_BASE.get(), models().getExistingFile(MILostFavor.locate("block/clay_mold_base")));
//
//        BlockModelBuilder moldIngotsModel = models()
//                .withExistingParent("block/clay_mold_ingot", MILostFavor.locate("block/clay_mold_base"))
//                .texture("1", MILostFavor.locate("block/overlay/mold_base_overlay_ingots"))
//                .texture("2", MILostFavor.locate("block/overlay/mold_base_overlay_overlay_ingots"));
//
//        horizontalBlock(MILFBlocks.CLAY_MOLD_INGOT.get(), moldIngotsModel);
//        simpleBlockItem(MILFBlocks.CLAY_MOLD_INGOT.get(), moldIngotsModel);
//
//        BlockModelBuilder moldRodsModel = models()
//                .withExistingParent("block/clay_mold_rod", MILostFavor.locate("block/clay_mold_base"))
//                .texture("1", MILostFavor.locate("block/overlay/mold_base_overlay_rods"))
//                .texture("2", MILostFavor.locate("block/overlay/mold_base_overlay_overlay_rods"));
//
//        horizontalBlock(MILFBlocks.CLAY_MOLD_ROD.get(), moldRodsModel);
//        simpleBlockItem(MILFBlocks.CLAY_MOLD_ROD.get(), moldRodsModel);


        simpleBlock(MILFBlocks.STONE_BASIN.get(), models().getExistingFile(MILostFavor.locate("block/stone_basin")));

        simpleBlockWithItem(MILFBlocks.POTTERY_CLAY.get(), cubeAll(MILFBlocks.POTTERY_CLAY.get()));
        simpleBlockWithItem(MILFBlocks.FIRE_CLAY.get(), cubeAll(MILFBlocks.FIRE_CLAY.get()));

        getVariantBuilder(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_FULL.get()).forAllStates(state -> {

            ModelFile model = state.getValue(AbstractAnvilMoldBlock.SIDE) ?
                        models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_anvil_full_side")) :
                        models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_anvil_full"));

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build();
            }

        );

        getVariantBuilder(MILFBlocks.CLAY_MOLD_ANVIL_FULL.get()).forAllStates(state -> {

                    ModelFile model = state.getValue(AbstractAnvilMoldBlock.SIDE) ?
                            models().getExistingFile(MILostFavor.locate("block/clay_mold_anvil_full_side")) :
                            models().getExistingFile(MILostFavor.locate("block/clay_mold_anvil_full"));

                    return ConfiguredModel.builder()
                            .modelFile(model)
                            .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                            .build();
                }

        );

    }

    private void baseMold(
            DeferredBlock<? extends AbstractFiredMoldBaseBlock> block,
            ResourceLocation overlay,
            ResourceLocation overlay_overlay
    ){

        String blockName = block.getId().getPath();

        getVariantBuilder(block.get()).forAllStates(state -> {
            boolean isFirePitPart = state.getValue(AbstractMoldBaseBlock.FIRE_PIT_PART);
            String variantName = "block/" + blockName + (isFirePitPart ? "_fire_pit" : "_normal");

            BlockModelBuilder model = isFirePitPart ?
                    models().withExistingParent(variantName, MILostFavor.locate("block/clay_mold_base_fire_pit_part")):
                    models().withExistingParent(variantName, MILostFavor.locate("block/clay_mold_base"));

            if (!isFirePitPart){
                itemModels().withExistingParent("item/" + blockName, model.getLocation());
            }

            model.texture("1", overlay);
            model.texture("2", overlay_overlay);

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build();
        });

    }

    private void unfiredBaseMold(DeferredBlock<UnfiredMoldBaseBlock> block, ResourceLocation overlay){

        String blockName = block.getId().getPath();

        getVariantBuilder(block.get()).forAllStates(state -> {
            boolean isFirePitPart = state.getValue(AbstractMoldBaseBlock.FIRE_PIT_PART);
            String variantName = "block/" + blockName + (isFirePitPart ? "_fire_pit" : "_normal");

            BlockModelBuilder model = isFirePitPart ?
                    models().withExistingParent(variantName, MILostFavor.locate("block/unfired_clay_mold_base_fire_pit_part")):
                    models().withExistingParent(variantName, MILostFavor.locate("block/unfired_clay_mold_base"));

            if (!isFirePitPart){
                itemModels().withExistingParent("item/" + blockName, model.getLocation());
            }

            model.texture("1", overlay);

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build();
        });

    }
}
