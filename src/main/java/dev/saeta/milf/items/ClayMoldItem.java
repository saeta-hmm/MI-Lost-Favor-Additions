package dev.saeta.milf.items;

import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

import java.util.List;
import java.util.Optional;

public class ClayMoldItem extends Item implements ItemCapabilityProvider {

    private final int capacity;
    public ClayMoldItem(Properties properties, int capacity) {
        super(properties);
        this.capacity = capacity;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        Optional<FluidStack> fluidStack = FluidUtil.getFluidContained(stack);

        fluidStack.ifPresent(fs -> {
            tooltipComponents.add(Component.translatable("milf.tooltip.fluid", fs.getHoverName(), fs.getAmount(), FluidType.BUCKET_VOLUME));
        });

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.FAIL;
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
