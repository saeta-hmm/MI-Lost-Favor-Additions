package dev.saeta.milf.items;

import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class WaterskinItem extends Item implements CapabilityProvider {
    public WaterskinItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        Optional<FluidStack> fluidStack = FluidUtil.getFluidContained(stack);

        fluidStack.ifPresent(fs -> {
            tooltipComponents.add(Component.translatable("milf.tooltip.fluid", fs.getHoverName(), fs.getAmount(), 500));
        });

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (hitResult.getType() != HitResult.Type.BLOCK || player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }

        BlockPos pos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos offsetPos = pos.relative(direction);

        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(offsetPos, direction, stack)) {
            return InteractionResultHolder.fail(stack);
        }

        WaterskinFluidHandler handler = (WaterskinFluidHandler) FluidUtil.getFluidHandler(stack).orElse(null);
        if(handler == null) return InteractionResultHolder.fail(stack);

        if(level.getFluidState(pos).is(Tags.Fluids.WATER) && handler.getFluid().getAmount() != handler.getTankCapacity(0)){
            FluidStack water = new FluidStack(Fluids.WATER, 500);
            handler.fill(water, IFluidHandler.FluidAction.EXECUTE);

            level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1, 1);

            return InteractionResultHolder.sidedSuccess(handler.getContainer(), level.isClientSide);
        }

        if(!level.isClientSide){
            FluidActionResult pickupResult = FluidUtil.tryPickUpFluid(stack, player, level, pos, direction);
            if (pickupResult.isSuccess()) {
                ItemStack filledStack = pickupResult.getResult();
                return InteractionResultHolder.sidedSuccess(filledStack, level.isClientSide());
            }
        } else if(!level.getFluidState(pos).isEmpty()){
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        return InteractionResultHolder.pass(stack);
    }


    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (stack, something) -> new WaterskinFluidHandler(MILFDataComponents.FLUID, stack, 500),
                this
        );
    }

    public static class WaterskinFluidHandler extends FluidHandlerItemStack{

        public WaterskinFluidHandler(Supplier<DataComponentType<SimpleFluidContent>> componentType, ItemStack container, int capacity) {
            super(componentType, container, capacity);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return stack.is(Tags.Fluids.WATER);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return fluid.is(Tags.Fluids.WATER);
        }
    }
}
