package dev.saeta.milf.blocks.anvils.bronze_anvil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.saeta.milf.blocks.anvils.AbstractAnvilBlockEntity;
import dev.saeta.milf.blocks.anvils.AbstractAnvilBlockEntityRenderer;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BronzeAnvilBlockEntityRenderer extends AbstractAnvilBlockEntityRenderer<BronzeAnvilBlockEntity> {

    public BronzeAnvilBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
}
