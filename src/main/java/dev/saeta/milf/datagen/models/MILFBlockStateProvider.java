package dev.saeta.milf.datagen.models;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.bloomery.AbstractBloomeryBaseBlock;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlock;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.AbstractAnvilMoldBlock;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.unfired.UnfiredAnvilMoldHalfBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.AbstractMoldBaseBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.AbstractFiredMoldBaseBlock;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.unfired.UnfiredMoldBaseBlock;
import dev.saeta.milf.blocks.fire_pit.FirePitBlock;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class MILFBlockStateProvider extends BlockStateProvider {
    public MILFBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MILostFavor.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

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


        simpleBlock(MILFBlocks.STONE_BASIN.get(), models().getExistingFile(MILostFavor.locate("block/stone_basin")));
        simpleBlock(MILFBlocks.UNFIRED_CLAY_CRUCIBLE.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_crucible")));
        simpleBlock(MILFBlocks.UNFIRED_CLAY_PLATE.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_plate")));
        simpleBlock(MILFBlocks.CLAY_PLATE.get(), models().getExistingFile(MILostFavor.locate("block/clay_plate")));

        simpleBlock(MILFBlocks.UNFIRED_CLAY_MOLD_AXE.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_axe")));
        simpleBlock(MILFBlocks.UNFIRED_CLAY_MOLD_HAMMER.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_hammer")));
        simpleBlock(MILFBlocks.UNFIRED_CLAY_MOLD_PICKAXE.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_pickaxe")));
        simpleBlock(MILFBlocks.UNFIRED_CLAY_MOLD_HOE.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_hoe")));
        simpleBlock(MILFBlocks.UNFIRED_CLAY_MOLD_SWORD.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_sword")));
        simpleBlock(MILFBlocks.UNFIRED_CLAY_MOLD_SHOVEL.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_shovel")));

        simpleBlock(MILFBlocks.CLAY_MOLD_AXE.get(), models().getExistingFile(MILostFavor.locate("block/clay_mold_axe")));
        simpleBlock(MILFBlocks.CLAY_MOLD_HAMMER.get(), models().getExistingFile(MILostFavor.locate("block/clay_mold_hammer")));
        simpleBlock(MILFBlocks.CLAY_MOLD_PICKAXE.get(), models().getExistingFile(MILostFavor.locate("block/clay_mold_pickaxe")));
        simpleBlock(MILFBlocks.CLAY_MOLD_HOE.get(), models().getExistingFile(MILostFavor.locate("block/clay_mold_hoe")));
        simpleBlock(MILFBlocks.CLAY_MOLD_SWORD.get(), models().getExistingFile(MILostFavor.locate("block/clay_mold_sword")));
        simpleBlock(MILFBlocks.CLAY_MOLD_SHOVEL.get(), models().getExistingFile(MILostFavor.locate("block/clay_mold_shovel")));

        //horizontalBlock(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_HALF.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_anvil_half")));
        horizontalBlock(MILFBlocks.BRONZE_ANVIL.get(), models().getExistingFile(MILostFavor.locate("block/bronze_anvil")));
        horizontalBlock(MILFBlocks.STONE_ANVIL.get(), models().getExistingFile(MILostFavor.locate("block/stone_anvil")));
        horizontalBlock(MILFBlocks.KILN.get(), models().getExistingFile(MILostFavor.locate("block/kiln")));
        horizontalBlock(MILFBlocks.UNFIRED_KILN.get(), models().getExistingFile(MILostFavor.locate("block/unfired_kiln")));
        horizontalBlock(MILFBlocks.POT_BELLOWS.get(), models().getExistingFile(MILostFavor.locate("block/pot_bellows")));
        horizontalBlock(MILFBlocks.ROASTING_CONTRAPTION.get(), models().getExistingFile(MILostFavor.locate("block/roasting_contraption")));

        simpleBlockWithItem(MILFBlocks.BURNING_COAL.get(),
                models().withExistingParent(MILFBlocks.BURNING_COAL.getId().getPath(), mcLoc("block/cube_all"))
                        .texture("all",MILostFavor.locate("block/burning_coal_block"))
        );

        simpleBlockWithItem(MILFBlocks.MOLDED_BLOCK.get(),
                models().withExistingParent(MILFBlocks.MOLDED_BLOCK.getId().getPath(), mcLoc("block/cube_all"))
                        .texture("all",MILostFavor.locate("block/pottery_clay"))
        );

        simpleBlockWithItem(MILFBlocks.CHISELED_BLOCK.get(),
                models().withExistingParent(MILFBlocks.CHISELED_BLOCK.getId().getPath(), mcLoc("block/cube_all"))
                        .texture("all",mcLoc("block/stone"))
        );

        simpleBlockWithItem(MILFBlocks.POTTERY_CLAY.get(), cubeAll(MILFBlocks.POTTERY_CLAY.get()));
        simpleBlockWithItem(MILFBlocks.FIRE_CLAY.get(), cubeAll(MILFBlocks.FIRE_CLAY.get()));

        simpleBlockWithItem(MILFBlocks.FIRE_PIT_PLATE.get(),
                models().withExistingParent(MILFBlocks.FIRE_PIT_PLATE.getId().getPath(), MILostFavor.locate("block/clay_plate"))
        );

        simpleBlockWithItem(MILFBlocks.STONE_POLE.get(), models().getExistingFile(MILostFavor.locate("block/stone_pole")));

        horizontalBlockWithBooleanProperty(
                MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_FULL,
                AbstractAnvilMoldBlock.SIDE,
                models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_anvil_full")),
                models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_anvil_full_side"))
        );

        horizontalBlockWithBooleanProperty(
                MILFBlocks.CLAY_MOLD_ANVIL_FULL,
                AbstractAnvilMoldBlock.SIDE,
                models().getExistingFile(MILostFavor.locate("block/clay_mold_anvil_full")),
                models().getExistingFile(MILostFavor.locate("block/clay_mold_anvil_full_side"))
        );

        blockWithBooleanProperty(
                MILFBlocks.UNFIRED_BLOOMERY_BASE,
                AbstractBloomeryBaseBlock.FIRE_PIT_PART,
                models().getExistingFile(MILostFavor.locate("block/unfired_bloomery_base")),
                models().getExistingFile(MILostFavor.locate("block/unfired_bloomery_base_fire_pit_part"))
        );

        blockWithBooleanProperty(
                MILFBlocks.BLOOMERY_BASE,
                AbstractBloomeryBaseBlock.FIRE_PIT_PART,
                models().getExistingFile(MILostFavor.locate("block/bloomery_base")),
                models().getExistingFile(MILostFavor.locate("block/bloomery_base_fire_pit_part"))
        );


        clayCrucible(MILFBlocks.CLAY_CRUCIBLE);

        firePit(MILFBlocks.FIRE_PIT);

        unfiredAnvilHalfMold(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_HALF);


    }

    private void unfiredAnvilHalfMold(DeferredBlock<? extends Block> block){
        getVariantBuilder(block.get()).forAllStates(state -> {

            ModelFile model;
            if(state.getValue(UnfiredAnvilMoldHalfBlock.VERTICAL)){
                model=models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_anvil_half_vertical"));
            } else if(state.getValue(AbstractMoldBaseBlock.FIRE_PIT_PART)) {
                model=models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_anvil_half_fire_pit_part"));
            } else {
                model =  models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_anvil_half"));
            }

            return ConfiguredModel.builder()
                .modelFile(model)
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build();
        }

        );
    }

    private void firePit(DeferredBlock<FirePitBlock> block){
        getMultipartBuilder(block.get())
                .part()
                .modelFile(models().getExistingFile(MILostFavor.locate("block/fire_pit")))
                .addModel()
                .end()

                .part()
                .modelFile(models().getExistingFile(MILostFavor.locate("block/fire_pit_logs_1")))
                .addModel()
                .condition(FirePitBlock.LOGS, 1)
                .end()

                .part()
                .modelFile(models().getExistingFile(MILostFavor.locate("block/fire_pit_logs_2")))
                .addModel()
                .condition(FirePitBlock.LOGS, 2)
                .end()

                .part()
                .modelFile(models().getExistingFile(MILostFavor.locate("block/fire_pit_logs_3")))
                .addModel()
                .condition(FirePitBlock.LOGS, 3)
                .end()

                .part()
                .modelFile(models().getExistingFile(MILostFavor.locate("block/fire_pit_logs_4")))
                .addModel()
                .condition(FirePitBlock.LOGS, 4)
                .end();
    }

    private void clayCrucible(DeferredBlock<ClayCrucibleBlock> block){
        getMultipartBuilder(block.get())

                .part()
                .modelFile(models().getExistingFile(MILostFavor.locate("block/clay_crucible")))
                .addModel()
                .condition(ClayCrucibleBlock.KILN_PART, false)
                .condition(ClayCrucibleBlock.DIRT, false)
                .end()

                .part()
                .modelFile(models().getExistingFile(MILostFavor.locate("block/clay_crucible_kiln_part")))
                .addModel()
                .condition(ClayCrucibleBlock.KILN_PART, true)
                .end()

                .part()
                .modelFile(models().getExistingFile(MILostFavor.locate("block/clay_crucible_dirt")))
                .addModel()
                .condition(ClayCrucibleBlock.DIRT, true)
                .end()

                .part()
                .modelFile(models().getExistingFile(MILostFavor.locate("block/clay_crucible_lid")))
                .addModel()
                .condition(ClayCrucibleBlock.KILN_PART, false)
                .condition(ClayCrucibleBlock.SEALED, true)
                .end()

                .part()
                .modelFile(models().getExistingFile(MILostFavor.locate("block/clay_crucible_kiln_part_lid")))
                .addModel()
                .condition(ClayCrucibleBlock.KILN_PART, true)
                .condition(ClayCrucibleBlock.SEALED, true)
                .end();
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

    private void horizontalBlockWithBooleanProperty(
            DeferredBlock<? extends Block> block,
            BooleanProperty property,
            ModelFile modelWhenFalse,
            ModelFile modelWhenTrue
    ) {
        getVariantBuilder(block.get()).forAllStates(state -> {

                    ModelFile model = state.getValue(property) ? modelWhenTrue :modelWhenFalse;

                    return ConfiguredModel.builder()
                            .modelFile(model)
                            .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                            .build();
                }

        );
    }

    private void blockWithBooleanProperty(
            DeferredBlock<? extends Block> block,
            BooleanProperty property,
            ModelFile modelWhenFalse,
            ModelFile modelWhenTrue
    ) {
        getVariantBuilder(block.get()).forAllStates(state -> {

                    ModelFile model = state.getValue(property) ? modelWhenTrue :modelWhenFalse;

                    return ConfiguredModel.builder().modelFile(model).build();
                }

        );
    }
}
