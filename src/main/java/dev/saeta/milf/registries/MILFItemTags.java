package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class MILFItemTags {

    public static final TagKey<Item> HAMMERS = milf("hammers");
    public static final TagKey<Item> CHISELS = milf("chisels");

    public static final TagKey<Item> BLOOMERY_COALS = milf("bloomery_coals");

    public static final TagKey<Item> LIGNITE_COALS = c("gems/lignite_coal");


    private static TagKey<Item> milf(String id) {
        return TagKey.create(Registries.ITEM, MILostFavor.locate(id));
    }

    private static TagKey<Item> c(String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", id));
    }
}
