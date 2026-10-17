package dev.saeta.milf.datagen.data.tags;

import aztech.modern_industrialization.MIItem;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItemTags;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MILFItemTagsProvider extends ItemTagsProvider {
    public MILFItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, MILostFavor.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        tag(ItemTags.PICKAXES)
                .add(
                    MILFItems.BIG_BULKY_DRILL.get(),
                    MILFItems.CLUNKY_DRILL.get()
        );

        tag(ItemTags.SHOVELS)
                .add(
                        MILFItems.BIG_BULKY_DRILL.get(),
                        MILFItems.CLUNKY_DRILL.get()
                );

        tag(MILFItemTags.C_HAMMERS).add(
                MIItem.IRON_HAMMER.asItem(),
                MIItem.STEEL_HAMMER.asItem(),
                MIItem.DIAMOND_HAMMER.asItem(),
                MIItem.NETHERITE_HAMMER.asItem()
        );

        tag(MILFItemTags.PLACEABLE_CLAY_BALLS).add(
                MILFItems.POTTERY_CLAY_BALL.get(),
                MILFItems.FIRE_CLAY_BALL.get()
        );

        tag(MILFItemTags.HAMMERS)
                .add(MILFItems.STONE_HAMMER.get())
                .addOptionalTag(ResourceLocation.fromNamespaceAndPath("c", "hammers"));

        tag(MILFItemTags.CHISELS).add(MILFItems.FLINT_CHISEL.get());

        tag(MILFItemTags.MOLDS)
                .add(
                        MILFBlocks.CLAY_MOLD_AXE.get().asItem(),
                        MILFBlocks.CLAY_MOLD_PICKAXE.get().asItem(),
                        MILFBlocks.CLAY_MOLD_HAMMER.get().asItem(),
                        MILFBlocks.CLAY_MOLD_HOE.get().asItem(),
                        MILFBlocks.CLAY_MOLD_SWORD.get().asItem(),
                        MILFBlocks.CLAY_MOLD_SHOVEL.get().asItem()
                );

        tag(MILFItemTags.CERAMIC)
                .add(
                        MILFBlocks.CLAY_CRUCIBLE.get().asItem(),
                        MILFBlocks.CLAY_PLATE.get().asItem()
                )
                .addTag(MILFItemTags.MOLDS);

        tag(MILFItemTags.BLOOMERY_COALS)
                .addTag(ItemTags.COALS).remove(Items.CHARCOAL);

        tag(MILFItemTags.FIRE_PIT_PLATE_CAN_HOLD)
                .add(
                        MILFBlocks.CLAY_PLATE.get().asItem(),

                        MILFBlocks.CLAY_MOLD_AXE.get().asItem(),
                        MILFBlocks.CLAY_MOLD_PICKAXE.get().asItem(),
                        MILFBlocks.CLAY_MOLD_HAMMER.get().asItem(),
                        MILFBlocks.CLAY_MOLD_HOE.get().asItem(),
                        MILFBlocks.CLAY_MOLD_SWORD.get().asItem(),
                        MILFBlocks.CLAY_MOLD_SHOVEL.get().asItem(),

                        MILFBlocks.CLAY_CRUCIBLE.get().asItem(),

                        MILFBlocks.UNFIRED_CLAY_PLATE.get().asItem(),

                        MILFBlocks.UNFIRED_CLAY_MOLD_AXE.get().asItem(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_PICKAXE.get().asItem(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_HAMMER.get().asItem(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_HOE.get().asItem(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_SWORD.get().asItem(),
                        MILFBlocks.UNFIRED_CLAY_MOLD_SHOVEL.get().asItem(),

                        MILFBlocks.UNFIRED_CLAY_CRUCIBLE.get().asItem()

                );
    }
}
