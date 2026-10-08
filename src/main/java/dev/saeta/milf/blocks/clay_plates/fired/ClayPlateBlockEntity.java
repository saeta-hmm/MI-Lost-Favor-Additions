package dev.saeta.milf.blocks.clay_plates.fired;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.clay_plates.AbstractClayPlateBlockEntity;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ClayPlateBlockEntity extends AbstractClayPlateBlockEntity {

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

    public ClayPlateBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.CLAY_PLATE.get(), pos, blockState);
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
}
