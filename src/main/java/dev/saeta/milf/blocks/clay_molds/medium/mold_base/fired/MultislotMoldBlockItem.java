package dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired;

import dev.saeta.milf.blocks.clay_molds.AbstractClayMoldBlockItem;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.capabilities.fluid.MultiFluidItemHandler;
import dev.saeta.milf.networking.data.MultiFluidContents;
import dev.saeta.milf.networking.data.StoredItem;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

import java.util.List;
import java.util.Optional;

public class MultislotMoldBlockItem extends BlockItem implements CapabilityProvider {
    protected final int capacity;
    protected final int tankCount;

    public MultislotMoldBlockItem(Block block, Properties properties, int capacity, int tankCount) {
        super(block, properties);
        this.tankCount = tankCount;
        this.capacity = capacity;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        MultiFluidContents fluids = stack.getOrDefault(MILFDataComponents.FLUIDS, MultiFluidContents.EMPTY);

        for(FluidStack fluidStack : fluids.fluids()){
            if(!fluidStack.isEmpty()) tooltipComponents.add(Component.translatable("milf.tooltip.fluid", fluidStack.getHoverName(), fluidStack.getAmount(), capacity));
        }

        ItemContainerContents stacks = stack.getOrDefault(MILFDataComponents.STACKS, ItemContainerContents.EMPTY);

        for(ItemStack castResultItem : stacks.nonEmptyItemsCopy()){
            tooltipComponents.add(Component.translatable("milf.tooltip.mold", castResultItem.getHoverName()));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (stack, something) -> new MultiFluidItemHandler(stack, tankCount, capacity),
                this
        );

    }
}
