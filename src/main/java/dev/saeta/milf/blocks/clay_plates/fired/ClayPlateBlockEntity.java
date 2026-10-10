package dev.saeta.milf.blocks.clay_plates.fired;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.clay_plates.AbstractClayPlateBlockEntity;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class ClayPlateBlockEntity extends AbstractClayPlateBlockEntity {

    private int capacity = 1000;

    private final AbstractBEItemHandler itemHandler = new AbstractBEItemHandler(1) {

        @Override
        protected boolean testInput(ItemStack stack) {
            return isItemValid(0, stack);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {

            setChanged();
        }

    };

    private final FluidTank fluidTank = new FluidTank(capacity) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    public ClayPlateBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.CLAY_PLATE.get(), pos, blockState);
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public void setCapacity(int capacity){
        fluidTank.setCapacity(capacity);
        this.capacity = capacity;
    }

    @Override
    public AbstractBEItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public @NotNull BlockPos getOutputPos() {
        return worldPosition;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if(!fluidTank.isEmpty()){
            components.set(MILFDataComponents.FLUID, SimpleFluidContent.copyOf(fluidTank.getFluid()));
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);

        FluidStack fluidStack = componentInput.getOrDefault(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY).copy();
        fluidTank.setFluid(fluidStack);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("fluid", fluidTank.writeToNBT(registries, new CompoundTag()));

        tag.putInt("capacity", capacity);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("fluid")) {
            fluidTank.readFromNBT(registries, tag.getCompound("fluid"));
        }

        if(tag.contains("capacity")){
            setCapacity(tag.getInt("capacity"));
        }
    }
}
