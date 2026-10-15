package dev.saeta.milf.blocks.clay_molds;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface CastableMoldBE {

    AbstractBEItemHandler getItemHandler();
    IFluidHandler getFluidHandler();

    boolean canCastInto();
    boolean tryExtract();
}
