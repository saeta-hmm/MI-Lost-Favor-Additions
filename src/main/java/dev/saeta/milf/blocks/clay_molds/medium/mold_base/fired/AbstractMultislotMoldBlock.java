package dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired;

public abstract class AbstractMultislotMoldBlock extends AbstractFiredMoldBaseBlock {

    protected final int slots = getSlots();

    public AbstractMultislotMoldBlock(Properties properties) {
        super(properties);
    }

    protected abstract int getSlots();

}
