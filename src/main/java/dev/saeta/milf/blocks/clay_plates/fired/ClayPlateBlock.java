package dev.saeta.milf.blocks.clay_plates.fired;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.clay_plates.AbstractClayPlateBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ClayPlateBlock extends AbstractClayPlateBlock {

    public static final MapCodec<ClayPlateBlock> CODEC = simpleCodec(ClayPlateBlock::new);

    public ClayPlateBlock(Properties properties) {
        super(properties);
    }


    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.CLAY_PLATE.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ClayPlateBlockEntity(pos, state);
    }

    public static class ClayPlateItem extends BlockItem{

        public ClayPlateItem(Block block, Properties properties) {
            super(block, properties);
        }
    }

    public static class ClayMoldItem extends BlockItem implements CapabilityProvider{
        private final int capacity;
        public ClayMoldItem(Block block, Properties properties, int capacity) {
            super(block, properties.stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY));
            this.capacity = capacity;
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

            Optional<FluidStack> fluidStack = FluidUtil.getFluidContained(stack);

            fluidStack.ifPresent(fs -> {
                tooltipComponents.add(Component.translatable("milf.tooltip.fluid", fs.getHoverName(), fs.getAmount(), capacity));
            });

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
