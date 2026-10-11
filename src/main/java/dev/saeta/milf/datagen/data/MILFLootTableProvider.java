package dev.saeta.milf.datagen.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MILFLootTableProvider extends LootTableProvider {
    public MILFLootTableProvider(PackOutput output, Set<ResourceKey<LootTable>> requiredTables, CompletableFuture<HolderLookup.Provider> registries) {
        super(
                output,
                requiredTables,
                List.of(
                        new SubProviderEntry(
                                MILFBlockLootSubProvider::new,
                                LootContextParamSets.BLOCK
                        )
                ),
                registries
        );
    }
}
