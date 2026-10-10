package dev.saeta.milf.compat.emi.widgets;

import dev.saeta.milf.MILostFavor;
import net.minecraft.resources.ResourceLocation;

public enum EmiTexture {
    FIRE_PIT(64, 32, MILostFavor.locate("textures/gui/fire_pit_emi.png")),
    KILN(64, 80, MILostFavor.locate("textures/gui/kiln_emi.png")),
    CLAY_CRUCIBLE(64, 64, MILostFavor.locate("textures/gui/clay_crucible_emi.png")),
    CRUCIBLE_KILN(64, 80, MILostFavor.locate("textures/gui/clay_crucible_kiln_emi.png")),
    BRONZE_ANVIL(64, 64, MILostFavor.locate("textures/gui/bronze_anvil_emi.png")),
    STONE_ANVIL(64, 64, MILostFavor.locate("textures/gui/stone_anvil_emi.png")),
    POTTERY(96, 64, MILostFavor.locate("textures/gui/pottery_emi.png")),
    CHISEL(96, 64, MILostFavor.locate("textures/gui/chisel_emi.png")),
    BLOOMERY(64, 96, MILostFavor.locate("textures/gui/bloomery_emi.png")),
    IMPRESSION_MOLDING(64, 64, MILostFavor.locate("textures/gui/impression_molding_emi.png"))


    ;

    public final int width;
    public final int height;
    public final int u;
    public final int v;
    public final int regionWidth;
    public final int regionHeight;
    public final int textureWidth;
    public final int textureHeight;
    public final ResourceLocation resourceLocation;

    EmiTexture(int width, int height, ResourceLocation resourceLocation){
        this(width, height, 0, 0, width, height, width, height, resourceLocation);
    }

    EmiTexture(int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight, ResourceLocation resourceLocation){
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.resourceLocation = resourceLocation;
    }
}
