package dev.saeta.milf.datagen.data.tags;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlockTags;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MILFBlockTagsProvider extends BlockTagsProvider {
    public MILFBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MILostFavor.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(MILFBlockTags.ANVILS)
                .add(
                        MILFBlocks.STONE_ANVIL.get(),
                        MILFBlocks.BRONZE_ANVIL.get()
                );

        tag(MILFBlockTags.FIRE_PIT_CAN_PROCESS)
                .add(
                        MILFBlocks.FIRE_PIT_PLATE.get(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_FULL.get(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_BASE.get(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_INGOT.get(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_ROD.get()
                );

        tag(MILFBlockTags.POTTERY_CLAYS)
                .add(
                        MILFBlocks.POTTERY_CLAY.get(),
                        MILFBlocks.FIRE_CLAY.get()
                );

        tag(MILFBlockTags.UNFIRED_MOLDS)
                .add(
                        MILFBlocks.UNFIRED_CLAY_PLATE.get(),

                        MILFBlocks.UNFIRED_CLAY_MOLD_SHOVEL.get(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_SWORD.get(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_HOE.get(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_PICKAXE.get(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_AXE.get(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_HAMMER.get(),

                        MILFBlocks.UNFIRED_CLAY_MOLD_ROD.get(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_INGOT.get(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_BASE.get(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_ANVIL_HALF.get()

                );
    }
}
