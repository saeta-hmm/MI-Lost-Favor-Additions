package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MILFBlockTags {

    public static final TagKey<Block> ANVILS = milf("anvils");

    private static TagKey<Block> milf(String id) {
        return TagKey.create(Registries.BLOCK, MILostFavor.locate(id));
    }

}
