package dev.saeta.milf.compat.emi.widgets;

public class EmiSlotPointer {

    public final boolean topLeft;
    public final boolean topRight;
    public final boolean bottomLeft;
    public final boolean bottomRight;

    private boolean large;

    public final Type type;


    public EmiSlotPointer(Type type, Corner... corners){

        boolean bottomRightT = false;
        boolean bottomLeftT = false;
        boolean topRightT = false;
        boolean topLeftT = false;

        for(Corner corner : corners){
            switch (corner){
                case TOP_LEFT -> {
                    topLeftT = true;
                }
                case TOP_RIGHT -> {
                    topRightT = true;
                }
                case BOTTOM_LEFT -> {
                    bottomLeftT = true;
                }
                case BOTTOM_RIGHT -> {
                    bottomRightT = true;
                }
            }
        }
        bottomRight = bottomRightT;
        bottomLeft = bottomLeftT;
        topRight = topRightT;
        topLeft = topLeftT;

        this.type = type;
    }

    public int getXOffset(){
        return large ? 4 : 2;
    }

    public int getYOffset(){
        return large ? 4 : 2;
    }

    public EmiSlotPointer large(){
        this.large = true;
        return this;
    }


    public enum Corner{
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public enum Type{
        CRUCIBLE(0,242, 7, 7),
        WOOD(0,249, 7, 7),
        CLAY(0,235, 7, 7),

        ;

        public final int u;
        public final int v;

        public final int width;
        public final int height;

        Type(int u, int v, int width, int height){
            this.u = u;
            this.v = v;

            this.width = width;
            this.height = height;
        }
    }

}
