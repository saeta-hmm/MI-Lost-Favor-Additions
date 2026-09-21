package dev.saeta.milf.blocks;

import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class ClayCrucibleBlockEntity extends BlockEntity {

    public static final int MAX_PROGRESS = 109;
    private static final int INPUT_SLOT = 0;
    private static final int FUEL_SLOT  = 1;

    private boolean isFull;
    private boolean isLit;
    private boolean hasFluid;
    private int progress;

    private Set<String> CRUSHED_ORES = Set.of(
            "minecraft:raw_iron"
    );

    private Set<String> COALS = Set.of(
            "minecraft:coal"
    );

    private Map<String, String> RESULT_MAP = Map.of(
            "minecraft:raw_iron", "minecraft:lava"
    );

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        public int getSlotLimit(int slot) {
            return slot == INPUT_SLOT ? 8 : 4;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (!fluidTank.isEmpty()) return false;

            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

            if (slot == INPUT_SLOT) return CRUSHED_ORES.contains(itemId);
            if (slot == FUEL_SLOT) return COALS.contains(itemId);

            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

    };

    private final FluidTank fluidTank = new FluidTank(1000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    public ClayCrucibleBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.CLAY_CRUCIBLE.get(), pos, blockState);
    }

    public ItemStackHandler getItemHandler(){
        return itemHandler;
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public void setFull(boolean full) {
        isFull = full;
        setChanged();
    }

    public void setLit(boolean lit) {
        isLit = lit;
        setChanged();
    }

    public boolean isLit(){
        return isLit;
    }

    public boolean isFull() {
        return isFull;
    }

    public void checkAndSetIfFull(){
        setFull(isInventoryFull());
    }

    private boolean isInventoryFull() {
        ItemStack stack0 = itemHandler.getStackInSlot(INPUT_SLOT);
        if (stack0.isEmpty()) return false;
        ItemStack stack1 = itemHandler.getStackInSlot(FUEL_SLOT);
        if (stack1.isEmpty()) return false;
        return stack0.getCount() + stack1.getCount() >= 12;
    }

    public ItemStack insertAnywhere(ItemStack stack) {
        ItemStack remaining = stack;
        for (int i = 0; i < 2 && !remaining.isEmpty(); i++) {
            remaining = itemHandler.insertItem(i, remaining, false);
        }
        return remaining;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ClayCrucibleBlockEntity clayCrucibleBlockEntity){
        clayCrucibleBlockEntity.tick();
    }

    private void tick(){
        if(!isLit) return;

        progress++;

        if(progress >= MAX_PROGRESS){
            progress = 0;

            ItemStack inputStack = itemHandler.getStackInSlot(0);

            String inputId = BuiltInRegistries.ITEM.getKey(inputStack.getItem()).toString();
            String fluidId = RESULT_MAP.get(inputId);

            if(fluidId == null) return;

            Fluid fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(fluidId));
            FluidStack fluidStack = new FluidStack(fluid, 1000);

            fluidTank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);

            itemHandler.extractItem(0,8,false);
            itemHandler.extractItem(1,4,false);

            isLit = false;
            isFull = false;

            setChanged();
            return;
        }

        if(progress %10 == 0){
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        ListTag itemStacks = new ListTag();

        for (int i = 0; i < 2; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            itemStacks.add(stack.isEmpty() ? new CompoundTag() : stack.save(registries));
        }

        tag.put("items", itemStacks);

        tag.put("fluid", fluidTank.writeToNBT(registries, new CompoundTag()));

        tag.putBoolean("isFull", isFull);

        tag.putBoolean("isLit", isLit);

        tag.putBoolean("hasFluid", hasFluid);

        tag.putInt("progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains("items", Tag.TAG_LIST)){
            ListTag itemStacks = tag.getList("items", Tag.TAG_COMPOUND);


            for (int i = 0; i < 2 && i < itemStacks.size(); i++) {
                CompoundTag stackCompound = itemStacks.getCompound(i);
                ItemStack itemStack = stackCompound.isEmpty() ? ItemStack.EMPTY : ItemStack.parse(registries, stackCompound).orElse(ItemStack.EMPTY);
                itemHandler.setStackInSlot(i, itemStack);
            }

        }

        if (tag.contains("fluid")) {
            fluidTank.readFromNBT(registries, tag.getCompound("fluid"));
        }

        if(tag.contains("isFull")){
            isFull = tag.getBoolean("isFull");
        }

        if (tag.contains("isLit")) {
            isLit = tag.getBoolean("isLit");
        }

        if (tag.contains("hasFluid")) {
            hasFluid = tag.getBoolean("hasFluid");
        }

        if (tag.contains("progress")) {
            progress = tag.getInt("progress");
        }

    }

    @Override
    public void setChanged() {
        super.setChanged();
        if(level != null && !level.isClientSide() && level.isLoaded(worldPosition)){
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
