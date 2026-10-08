package dev.saeta.milf.compat.emi.widgets;

public enum EmiSlot {
    CRUCIBLE(0,0, 18, 18),
    CRUCIBLE_WIDE(0, 18, 36, 18),
    KILN(0,36, 18, 18),
    WOOD(18,36, 18, 18),
    CLAY(36,36, 18, 18),
    STONE(36,18, 18, 18),
    BRONZE(36,0, 18, 18),
    BELLOWS(18,0, 18, 18),
    FIRE_PIT(0,54, 18, 18),

    ;

    private final int u;
    private final int v;

    private final int width;
    private final int height;

    EmiSlot(int u, int v, int width, int height){
        this.u = u;
        this.v = v;

        this.width = width;
        this.height = height;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
