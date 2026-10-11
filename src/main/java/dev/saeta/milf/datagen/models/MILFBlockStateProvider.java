package dev.saeta.milf.datagen.models;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MILFBlockStateProvider extends BlockStateProvider {
    public MILFBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MILostFavor.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        horizontalBlock(MILFBlocks.UNFIRED_CLAY_MOLD_BASE.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_base")));
        horizontalBlock(MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL.get(), models().getExistingFile(MILostFavor.locate("block/unfired_clay_mold_anvil")));

    }
}
