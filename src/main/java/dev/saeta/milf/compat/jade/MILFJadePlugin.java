package dev.saeta.milf.compat.jade;

import dev.saeta.milf.blocks.basin.StoneBasinBlockEntity;
import dev.saeta.milf.blocks.clay_molds.AbstractCastableMoldBlockEntity;
import dev.saeta.milf.blocks.clay_molds.AbstractMultislotCastableMoldBlockEntity;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.unfired.UnfiredAnvilMoldBlockEntity;
import dev.saeta.milf.blocks.bloomery.BloomeryBaseBlockEntity;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.ingot.IngotMoldBlockEntity;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.rod.RodMoldBlockEntity;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.unfired.UnfiredMoldBaseBlockEntity;
import dev.saeta.milf.blocks.clay_molds.small.fired.ClayMoldBlockEntity;
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

        registration.registerProgress(new ProgressComponentProvider.Progress(), ClayCrucibleBlockEntity.class);
        registration.registerProgress(new ProgressComponentProvider.Progress(), KilnBlockEntity.class);
        registration.registerProgress(new ProgressComponentProvider.Progress(), FirePitBlockEntity.class);
        registration.registerProgress(new ProgressComponentProvider.Progress(), BloomeryBaseBlockEntity.class);
        registration.registerProgress(new ProgressComponentProvider.Progress(), AbstractCastableMoldBlockEntity.class);
        registration.registerProgress(new ProgressComponentProvider.Progress(), UnfiredAnvilMoldBlockEntity.class);
        registration.registerProgress(new ProgressComponentProvider.Progress(), UnfiredMoldBaseBlockEntity.class);
        registration.registerProgress(new ProgressComponentProvider.Progress(), StoneBasinBlockEntity.class);

        registration.registerProgress(new MultislotProgressComponentProvider.Progress(), AbstractMultislotCastableMoldBlockEntity.class);

    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerProgressClient(new ProgressComponentProvider.Progress());
        registration.registerProgressClient(new MultislotProgressComponentProvider.Progress());
    }
}
