package dev.saeta.milf.compat.jade.server;

import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MILFJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {

        registration.registerProgress(new ClayCrucibleComponentProvider.Progress(), ClayCrucibleBlockEntity.class);

    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerProgressClient(new ClayCrucibleComponentProvider.Progress());
    }
}
