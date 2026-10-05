package dev.saeta.milf.recipes.anvil;


import net.minecraft.util.StringRepresentable;

public enum AnvilTier implements StringRepresentable {
    STONE("stone"),
    BRONZE("bronze");

    private final String name;

    AnvilTier(String name){
        this.name = name;
    }



    @Override
    public String getSerializedName() {
        return name;
    }

}
