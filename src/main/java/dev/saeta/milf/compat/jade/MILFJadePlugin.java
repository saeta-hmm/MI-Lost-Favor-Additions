package dev.saeta.milf.compat.jade;

import dev.saeta.milf.blocks.bloomery.BloomeryBaseBlockEntity;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntity;
import dev.saeta.milf.blocks.kiln.KilnBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MILFJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {

        registration.registerProgress(new ClayCrucibleComponentProvider.Progress(), ClayCrucibleBlockEntity.class);
        registration.registerProgress(new KilnComponentProvider.Progress(), KilnBlockEntity.class);
        registration.registerProgress(new FirePitComponentProvider.Progress(), FirePitBlockEntity.class);
        registration.registerProgress(new BloomeryComponentProvider.Progress(), BloomeryBaseBlockEntity.class);

    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerProgressClient(new ClayCrucibleComponentProvider.Progress());

        registration.registerProgressClient(new KilnComponentProvider.Progress());

        registration.registerProgressClient(new FirePitComponentProvider.Progress());

        registration.registerProgressClient(new BloomeryComponentProvider.Progress());
    }
}
