package dev.saeta.milf.compat.emi.widgets;

public enum EmiSlotPointer {
    CRUCIBLE(0,242, 7, 7),
    WOOD(0,249, 7, 7),
    CLAY(0,235, 7, 7),

    ;

    private final int u;
    private final int v;

    private final int width;
    private final int height;

    public boolean topLeft;
    public boolean topRight;
    public boolean bottomLeft;
    public boolean bottomRight;

    private boolean large;


    EmiSlotPointer(int u, int v, int width, int height){
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

    public int getXOffset(){
        return large ? 4 : 2;
    }

    public int getYOffset(){
        return large ? 4 : 2;
    }

    public EmiSlotPointer corners(Corner... corners){
        for(Corner corner : corners){
            switch (corner){
                case TOP_LEFT -> {
                    topLeft = true;
                }
                case TOP_RIGHT -> {
                    topRight = true;
                }
                case BOTTOM_LEFT -> {
                    bottomLeft = true;
                }
                case BOTTOM_RIGHT -> {
                    bottomRight = true;
                }
            }
        }

        return this;
    }

    public EmiSlotPointer large(){
        this.large = true;
        return this;
    }


    public static enum Corner{
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

}
