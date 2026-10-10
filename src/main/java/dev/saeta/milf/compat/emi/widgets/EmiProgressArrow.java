package dev.saeta.milf.compat.emi.widgets;

public enum EmiProgressArrow {

    CRUCIBLE(0,0,22,22),
    CRUCIBLE_FULL(0,22,22,22),
    KILN(22,0,22,22),
    KILN_FULL(22,22,22,22),
    WOOD(44,0,22,22),
    WOOD_FULL(44,22,22,22),

    ;

    public final int u;
    public final int v;

    public final int width;
    public final int height;

    EmiProgressArrow(int u, int v, int width, int height){
        this.u = u;
        this.v = v;

        this.width = width;
        this.height = height;
    }
}
