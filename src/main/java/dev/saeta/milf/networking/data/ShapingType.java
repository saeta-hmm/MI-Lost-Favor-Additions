package dev.saeta.milf.networking.data;

import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum ShapingType {
    CHISEL(0),
    POTTERY(1);

    private final int id;

    ShapingType(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static final IntFunction<ShapingType> BY_ID = ByIdMap.continuous(
            ShapingType::getId,
            ShapingType.values(),
            ByIdMap.OutOfBoundsStrategy.ZERO
    );
}
