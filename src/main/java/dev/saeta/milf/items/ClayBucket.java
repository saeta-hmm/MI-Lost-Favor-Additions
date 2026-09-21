package dev.saeta.milf.items;

import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.*;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

import java.util.List;
import java.util.Optional;

public class ClayBucket extends Item implements ItemCapabilityProvider {
    public ClayBucket(Properties properties) {
        super(properties);
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        BlockPos pos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos offsetPos = pos.relative(direction);

        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(offsetPos, direction, stack)) {
            return InteractionResultHolder.fail(stack);
        }

        FluidStack fluidStack = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);

        if (fluidStack.isEmpty()) {

            FluidActionResult pickupResult = FluidUtil.tryPickUpFluid(stack, player, level, pos, direction);
            if (pickupResult.isSuccess()) {
                ItemStack filledStack = pickupResult.getResult();

                FluidStack pickedUpFluid = FluidUtil.getFluidContained(filledStack).orElse(FluidStack.EMPTY);
                stack.set(MILFDataComponents.FLUID, SimpleFluidContent.copyOf(pickedUpFluid));

                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }

        } else if (fluidStack.getAmount() >= FluidType.BUCKET_VOLUME) {
            BlockState targetState = level.getBlockState(pos);
            BlockPos placementPos = (targetState.getBlock() instanceof LiquidBlockContainer) ? pos : offsetPos;

            FluidActionResult placeResult = FluidUtil.tryPlaceFluid(player, level, hand, placementPos, stack, fluidStack);
            if (placeResult.isSuccess()) {
                ItemStack emptiedStack = placeResult.getResult();

                FluidStack remainingFluid = FluidUtil.getFluidContained(emptiedStack).orElse(FluidStack.EMPTY);
                stack.set(MILFDataComponents.FLUID, SimpleFluidContent.copyOf(remainingFluid));

                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {

        if (FluidUtil.getFluidContained(itemStack).isEmpty()) return itemStack;

        ItemStack returnStack = itemStack.copy();

        FluidHandlerItemStack handler = (FluidHandlerItemStack) FluidUtil.getFluidHandler(returnStack).orElseThrow();

        FluidStack drainStack = handler.getFluid().copy();

        drainStack.setAmount(FluidType.BUCKET_VOLUME);

        handler.drain(drainStack, IFluidHandler.FluidAction.EXECUTE);

        return returnStack;
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.FluidHandler.ITEM,
                (stack, something) -> new FluidHandlerItemStack(MILFDataComponents.FLUID, stack, FluidType.BUCKET_VOLUME),
                this
        );

    }
}
