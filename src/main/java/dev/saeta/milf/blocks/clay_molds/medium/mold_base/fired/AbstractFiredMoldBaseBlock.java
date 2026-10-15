package dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired;

import dev.saeta.milf.blocks.clay_molds.AbstractClayMoldBlockItem;
import dev.saeta.milf.blocks.clay_molds.medium.mold_base.AbstractMoldBaseBlock;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

public abstract class AbstractFiredMoldBaseBlock extends AbstractMoldBaseBlock {
    public AbstractFiredMoldBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(interactWithFluidHandler(stack, state, level, pos, player, hand, hitResult)) return ItemInteractionResult.SUCCESS;
        if(tryExtract(stack, state, level, pos, player, hand, hitResult)) return ItemInteractionResult.SUCCESS;
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    public static class BaseMoldBlockItem extends AbstractClayMoldBlockItem {

        public BaseMoldBlockItem(Block block, Properties properties) {
            super(block, properties, 1000);
        }

        @Override
        public void registerCapabilities(RegisterCapabilitiesEvent event) {
            event.registerItem(
                    Capabilities.FluidHandler.ITEM,
                    (stack, something) -> new FluidHandlerItemStack(MILFDataComponents.FLUID, stack, capacity),
                    this
            );

        }
    }
}
