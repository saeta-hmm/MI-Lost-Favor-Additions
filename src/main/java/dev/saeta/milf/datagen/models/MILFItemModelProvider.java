package dev.saeta.milf.datagen.models;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MILFItemModelProvider extends ItemModelProvider {
    public MILFItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MILostFavor.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        basicItem(MILFItems.BRONZE_AXE_HEAD.get());
        basicItem(MILFItems.BRONZE_HAMMER_HEAD.get());
        basicItem(MILFItems.BRONZE_HOE_HEAD.get());
        basicItem(MILFItems.BRONZE_PICKAXE_HEAD.get());
        basicItem(MILFItems.BRONZE_SWORD_BLADE.get());
        basicItem(MILFItems.BRONZE_SHOVEL_HEAD.get());

        basicItem(MILFItems.LEAD_AXE_HEAD.get());
        basicItem(MILFItems.LEAD_HAMMER_HEAD.get());
        basicItem(MILFItems.LEAD_HOE_HEAD.get());
        basicItem(MILFItems.LEAD_PICKAXE_HEAD.get());
        basicItem(MILFItems.LEAD_SWORD_BLADE.get());
        basicItem(MILFItems.LEAD_SHOVEL_HEAD.get());

        handheldItem(MILFItems.POTSHERD.get());

    }



}
