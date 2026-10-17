package dev.saeta.milf.registries.client;

import dev.saeta.milf.blocks.anvils.bronze_anvil.BronzeAnvilBlockEntityRenderer;
import dev.saeta.milf.blocks.anvils.stone_anvil.StoneAnvilBlockEntityRenderer;
import dev.saeta.milf.blocks.basin.StoneBasinBlockEntityRenderer;
import dev.saeta.milf.blocks.bloomery.fired.BloomeryBaseBlockEntityRenderer;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntityRenderer;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.fired.AnvilMoldBlockEntityRenderer;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.ingot.IngotMoldBlockEntityRenderer;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired.rod.RoeMoldBlockEntityRenderer;
import dev.saeta.milf.blocks.clay_molds.small.fire_pit.FirePitPlateBlockEntityRenderer;
import dev.saeta.milf.blocks.clay_molds.small.fired.ClayMoldBlockEntityRenderer;
import dev.saeta.milf.blocks.clay_molds.small.unfired.UnfiredClayPlateBlockEntityRenderer;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntityRenderer;
import dev.saeta.milf.blocks.kiln.fired.KilnBlockEntityRenderer;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.unfired.UnfiredMoldBaseBlockEntityRenderer;
import dev.saeta.milf.blocks.pot_bellows.PotBellowsBlockEntityRenderer;
import dev.saeta.milf.blocks.roasting_contraption.RoastingContraptionBlockEntityRenderer;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlockEntityRenderer;
import dev.saeta.milf.blocks.shapeable_blocks.clay.MoldedBlockEntityRenderer;
import dev.saeta.milf.entities.potsherd.PotsherdProjectileEntityRenderer;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFEntityTypes;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class MILFEntityRenderers {

    public static void register(EntityRenderersEvent.RegisterRenderers event){

        //#region BER

        event.registerBlockEntityRenderer(MILFBlockEntities.CLAY_CRUCIBLE.get(), ClayCrucibleBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.POT_BELLOWS.get(), PotBellowsBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.ROASTING_CONTRAPTION.get(), RoastingContraptionBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.KILN.get(), KilnBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.FIRE_PIT.get(), FirePitBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.BRONZE_ANVIL.get(), BronzeAnvilBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.STONE_ANVIL.get(), StoneAnvilBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.BLOOMERY_BASE.get(), BloomeryBaseBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.CHISELED_BLOCK.get(), ChiseledBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.MOLDED_BLOCK.get(), MoldedBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.UNFIRED_CLAY_PLATE.get(), UnfiredClayPlateBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.FIRE_PIT_PLATE.get(), FirePitPlateBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.CLAY_MOLD.get(), ClayMoldBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.UNFIRED_MOLD_BASE.get(), UnfiredMoldBaseBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.CLAY_MOLD_ANVIL_FULL.get(), AnvilMoldBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.STONE_BASIN.get(), StoneBasinBlockEntityRenderer::new);

       // event.registerBlockEntityRenderer(MILFBlockEntities.CLAY_MOLD_BASE.get(), AbstractMultislotMoldBaseBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(MILFBlockEntities.CLAY_MOLD_INGOT.get(), IngotMoldBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(MILFBlockEntities.CLAY_MOLD_ROD.get(), RoeMoldBlockEntityRenderer::new);



        //#endregion

        event.registerEntityRenderer(MILFEntityTypes.POTSHERD.get(), PotsherdProjectileEntityRenderer::new);


    }

}
