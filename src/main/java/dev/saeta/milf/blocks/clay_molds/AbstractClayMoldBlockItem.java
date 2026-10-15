package dev.saeta.milf.blocks.clay_molds;

import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.networking.data.StoredItem;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.List;
import java.util.Optional;

public abstract class AbstractClayMoldBlockItem extends BlockItem implements CapabilityProvider {
    protected final int capacity;

    public AbstractClayMoldBlockItem(Block block, Properties properties, int capacity) {
        super(block, properties.stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY));
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        Optional<FluidStack> fluidStack = FluidUtil.getFluidContained(stack);

        fluidStack.ifPresent(fs -> {
            tooltipComponents.add(Component.translatable("milf.tooltip.fluid", fs.getHoverName(), fs.getAmount(), capacity));
        });

        ItemStack castResultItem = stack.getOrDefault(MILFDataComponents.CAST_RESULT,new StoredItem(ItemStack.EMPTY.copy()) ).stack();

        if(!castResultItem.isEmpty()){
            tooltipComponents.add(Component.translatable("milf.tooltip.mold", castResultItem.getHoverName()));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
