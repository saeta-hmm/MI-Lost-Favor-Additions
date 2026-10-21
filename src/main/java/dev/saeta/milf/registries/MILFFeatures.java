package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.worldgen.features.EverburningRootsHeartFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MILFFeatures {

    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, MILostFavor.MOD_ID);

    public static DeferredHolder<Feature<?>, EverburningRootsHeartFeature> EVERBURNING_ROOTS_HEART = register("everburning_roots_heart", new EverburningRootsHeartFeature(NoneFeatureConfiguration.CODEC));

    private static <C extends FeatureConfiguration, F extends Feature<C>> DeferredHolder<Feature<?>, F> register(String name, F feature) {
        return FEATURES.register(name, () -> feature);
    }

    public static void register(IEventBus modBus) {
        FEATURES.register(modBus);
    }

}
