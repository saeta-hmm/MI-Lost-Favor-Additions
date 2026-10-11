package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class MILFBlockTags {

    public static final TagKey<Block> ANVILS = milf("anvils");
    public static final TagKey<Block> UNFIRED_MOLDS = milf("unfired_molds");

    private static TagKey<Block> milf(String id) {
        return TagKey.create(Registries.BLOCK, MILostFavor.locate(id));
    }

}
