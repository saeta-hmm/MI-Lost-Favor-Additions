package dev.saeta.milf.blocks;

public interface FlammableBlockEntity {

    boolean canBeIgnited();

    boolean isLit();

    void ignite();
}
