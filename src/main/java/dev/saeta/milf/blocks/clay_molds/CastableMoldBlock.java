package dev.saeta.milf.blocks.clay_molds;

import com.google.common.base.Preconditions;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.fired.AnvilMoldBlockEntity;
import dev.saeta.milf.registries.MILFItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Objects;

public interface CastableMoldBlock {

    default boolean interactWithFluidHandler(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult){
        if(level.isClientSide) return true;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(!(blockEntity instanceof CastableMoldBE castableMoldBlockEntity)) return true;

        if(castableMoldBlockEntity.canCastInto()){

            if(interactWIthFluidHandlerReverse(player, hand, castableMoldBlockEntity.getFluidHandler())){

                return true;
            }
        }

        return false;
    }

    default boolean tryExtract(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult){
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(!(blockEntity instanceof CastableMoldBE castableMoldBlockEntity)) return true;

        if(stack.is(MILFItemTags.HAMMERS)) {
            return castableMoldBlockEntity.tryExtract();
        }

        return false;
    }


    // same as FluidUtil.interactWithFluidHandler() but with tryEmptyContainerAndStow() first
    private boolean interactWIthFluidHandlerReverse(Player player, InteractionHand hand, IFluidHandler handler){
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(hand);
        Preconditions.checkNotNull(handler);

        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.isEmpty()) {
            return false;
        }

        var playerInventory = player.getCapability(Capabilities.ItemHandler.ENTITY);
        Objects.requireNonNull(playerInventory, "Player item handler is null");

        FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
        if (!fluidActionResult.isSuccess()) {
            fluidActionResult = FluidUtil.tryFillContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
        }

        if (fluidActionResult.isSuccess()) {
            player.setItemInHand(hand, fluidActionResult.getResult());
            return true;
        } else {
            return false;
        }
    }
}
