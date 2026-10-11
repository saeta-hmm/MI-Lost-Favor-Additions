package dev.saeta.milf.datagen;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.datagen.data.MILFBlockLootSubProvider;
import dev.saeta.milf.datagen.data.MILFLootTableProvider;
import dev.saeta.milf.datagen.models.MILFBlockStateProvider;
import dev.saeta.milf.datagen.models.MILFItemModelProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = MILostFavor.MOD_ID)
public class MILFDatagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(
                event.includeClient(),
                new MILFItemModelProvider(packOutput, existingFileHelper)
        );

        generator.addProvider(
                event.includeClient(),
                new MILFBlockStateProvider(packOutput, existingFileHelper)
        );

        generator.addProvider(
                event.includeServer(),
                (DataProvider.Factory<MILFLootTableProvider>) output -> new MILFLootTableProvider(
                        output,
                        Set.of(),
                        lookupProvider
                )
        );
    }
}
