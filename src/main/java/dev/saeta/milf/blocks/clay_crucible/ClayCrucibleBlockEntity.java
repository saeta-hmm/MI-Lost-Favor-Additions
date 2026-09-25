package dev.saeta.milf.blocks.clay_crucible;

import dev.saeta.milf.blocks.FlammableBlockEntity;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.recipes.clay_crucible.ClayCrucibleRecipe;
import dev.saeta.milf.recipes.clay_crucible.ClayCrucibleRecipeInput;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClayCrucibleBlockEntity extends BlockEntity implements FlammableBlockEntity {

    private static final int INPUT_SLOT = 0;
    private static final int FUEL_SLOT = 1;

    private int currentRecipeTime = 109;
    private boolean isFull;
    private boolean isLit;
    private int progress;

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        public int getSlotLimit(int slot) {
            return slot == INPUT_SLOT ? 8 : 4;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (!fluidTank.isEmpty() || getBlockState().getValue(ClayCrucibleBlock.SEALED)) return false;
            if (level == null) return false;

            RecipeManager recipeManager = level.getRecipeManager();

            HashSet<Ingredient> validInputs = recipeManager.getAllRecipesFor(MILFRecipeTypes.CLAY_CRUCIBLE_TYPE).stream().map(
                    clayCrucibleRecipeRecipeHolder -> clayCrucibleRecipeRecipeHolder.value().getInput().ingredient()
            ).collect(Collectors.toCollection(HashSet::new));

            HashSet<Ingredient> validFuels = recipeManager.getAllRecipesFor(MILFRecipeTypes.CLAY_CRUCIBLE_TYPE).stream().map(
                    clayCrucibleRecipeRecipeHolder -> clayCrucibleRecipeRecipeHolder.value().getFuel().ingredient()
            ).collect(Collectors.toCollection(HashSet::new));

            switch (slot){
                case INPUT_SLOT:
                    return validInputs.stream().anyMatch(ingredient -> ingredient.test(stack));
                case FUEL_SLOT:
                    return validFuels.stream().anyMatch(ingredient -> ingredient.test(stack));
            }

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

    @Override
    public boolean canBeIgnited() {
        return isFull && !isLit && !getBlockState().getValue(ClayCrucibleBlock.SEALED);
    }

    public boolean isLit(){
        return isLit;
    }


    public float getCurrentProgress(){
        return (float) progress / currentRecipeTime;
    }

    @Override
    public void ignite() {
        isLit = true;
    }

    public boolean isFull() {
        return isFull;
    }

    public void checkAndSetIfFull(){
        Optional<RecipeHolder<ClayCrucibleRecipe>> recipeHolder = getCurrentRecipe();
        if(recipeHolder.isPresent()){
            setFull(true);
            currentRecipeTime = recipeHolder.get().value().getTime();
            return;
        }
        setFull(false);
    }

    private Optional<RecipeHolder<ClayCrucibleRecipe>> getCurrentRecipe() {

        if(level == null) return Optional.empty();

        ItemStack inputStack = itemHandler.getStackInSlot(INPUT_SLOT);
        if (inputStack.isEmpty()) return Optional.empty();
        ItemStack fuelStack = itemHandler.getStackInSlot(FUEL_SLOT);
        if (fuelStack.isEmpty()) return Optional.empty();

        ClayCrucibleRecipeInput clayCrucibleRecipeInput = new ClayCrucibleRecipeInput(inputStack, fuelStack);

        return level.getRecipeManager().getRecipeFor(MILFRecipeTypes.CLAY_CRUCIBLE_TYPE, clayCrucibleRecipeInput, level);
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
        if(level == null) return;
        if(!isLit) return;

        progress++;

        if(progress >= currentRecipeTime){
            progress = 0;

            ItemStack inputStack = itemHandler.getStackInSlot(INPUT_SLOT);
            ItemStack fuelStack = itemHandler.getStackInSlot(FUEL_SLOT);

            ClayCrucibleRecipeInput clayCrucibleRecipeInput = new ClayCrucibleRecipeInput(inputStack, fuelStack);

            Optional<RecipeHolder<ClayCrucibleRecipe>> optionalClayCrucibleRecipeRecipeHolder = level.getRecipeManager().getRecipeFor(MILFRecipeTypes.CLAY_CRUCIBLE_TYPE, clayCrucibleRecipeInput, level);

            if(optionalClayCrucibleRecipeRecipeHolder.isEmpty()) {
                isLit = false;
                isFull = false;

                setChanged();
                return;
            }

            ClayCrucibleRecipe clayCrucibleRecipe = optionalClayCrucibleRecipeRecipeHolder.get().value();

            FluidStack fluidStack = clayCrucibleRecipe.getOutput();

            fluidTank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);

            itemHandler.extractItem(INPUT_SLOT,clayCrucibleRecipe.getInput().count(),false);
            itemHandler.extractItem(FUEL_SLOT,clayCrucibleRecipe.getFuel().count(),false);

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

        tag.putInt("progress", progress);

        tag.putInt("currentRecipeTime", currentRecipeTime);
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

        if (tag.contains("progress")) {
            progress = tag.getInt("progress");
        }

        if (tag.contains("currentRecipeTime")) {
            currentRecipeTime = tag.getInt("currentRecipeTime");
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
