package dev.saeta.milf.capabilities.fluid;

import dev.saeta.milf.networking.data.MultiFluidContents;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.ArrayList;
import java.util.List;

public class MultiFluidItemHandler implements IFluidHandlerItem {

    private final ItemStack container;
    private final int tankCount;
    private final int tankCapacity;

    public MultiFluidItemHandler(ItemStack container, int tankCount, int tankCapacity) {
        this.container = container;
        this.tankCount = tankCount;
        this.tankCapacity = tankCapacity;
    }

    private List<FluidStack> getFluids(){
        MultiFluidContents fluids = container.getOrDefault(MILFDataComponents.FLUIDS, MultiFluidContents.EMPTY);
        List<FluidStack> fluidStacks = fluids.fluids();

        List<FluidStack> toReturn = new ArrayList<>(tankCount);

        for (int i = 0; i < tankCount; i++) {
            if (i < fluidStacks.size()) {
                toReturn.add(fluidStacks.get(i).copy());
            } else {
                toReturn.add(FluidStack.EMPTY);
            }
        }

        return toReturn;
    }

    @Override
    public ItemStack getContainer() {
        return container;
    }

    @Override
    public int getTanks() {
        return tankCount;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank < 0 || tank >= tankCount) {
            return FluidStack.EMPTY;
        }
        return getFluids().get(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return tankCapacity;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;

        List<FluidStack> fluids = getFluids();

        for (int i = 0; i < tankCount; i++) {
            FluidStack current = fluids.get(i);
            if (current.isEmpty() || FluidStack.isSameFluidSameComponents(current, resource)) {
                int currentAmount = current.getAmount();
                int space = tankCapacity - currentAmount;
                if (space <= 0) continue;

                int toAdd = Math.min(resource.getAmount(), space);
                if (action.execute()) {
                    fluids.set(i, resource.copyWithAmount(currentAmount + toAdd));
                    container.set(MILFDataComponents.FLUIDS, new MultiFluidContents(fluids));
                }
                return toAdd;
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;

        List<FluidStack> fluids = getFluids();
        for (int i = 0; i < tankCount; i++) {
            FluidStack current = fluids.get(i);
            if (FluidStack.isSameFluidSameComponents(current, resource)) {
                return drainTank(i, resource.getAmount(), action, fluids);
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0) return FluidStack.EMPTY;

        List<FluidStack> fluids = getFluids();
        for (int i = 0; i < tankCount; i++) {
            FluidStack current = fluids.get(i);
            if (!current.isEmpty()) {
                return drainTank(i, maxDrain, action, fluids);
            }
        }
        return FluidStack.EMPTY;
    }

    private FluidStack drainTank(int tank, int maxDrain, FluidAction action, List<FluidStack> fluids) {
        FluidStack current = fluids.get(tank);
        int toDrain = Math.min(current.getAmount(), maxDrain);
        FluidStack drained = current.copyWithAmount(toDrain);

        if (action.execute()) {
            if (toDrain >= current.getAmount()) {
                fluids.set(tank, FluidStack.EMPTY);
            } else {
                fluids.set(tank, current.copyWithAmount(current.getAmount() - toDrain));
            }
            container.set(MILFDataComponents.FLUIDS, new MultiFluidContents(fluids));
        }
        return drained;
    }
}
