package dev.saeta.milf.blocks.clay_crucible;

import dev.saeta.milf.blocks.FlammableBlockEntity;
import dev.saeta.milf.recipes.clay_crucible.*;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ClayCrucibleBlockEntity extends BlockEntity implements FlammableBlockEntity {

    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;

    private int currentRecipeTime = 109;
    private boolean isFull;
    private boolean isLit;
    private int progress;


    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        public int getSlotLimit(int slot) {

            int defaultLimit = 64;
            if (level == null) return defaultLimit;

            int otherSlot = (slot == INPUT_SLOT_1) ? INPUT_SLOT_2 : INPUT_SLOT_1;
            ItemStack otherStack = itemHandler.getStackInSlot(otherSlot);

            RecipeManager recipeManager = level.getRecipeManager();
            int maxCount = 0;

            List<CrucibleRecipe> relevantRecipes;

            if (isKilnPart()) {
                relevantRecipes = recipeManager.getAllRecipesFor(MILFRecipeTypes.CLAY_CRUCIBLE_KILN).stream().map(holder -> (CrucibleRecipe) holder.value()).toList();
            } else {
                relevantRecipes = recipeManager.getAllRecipesFor(MILFRecipeTypes.CLAY_CRUCIBLE).stream().map(holder -> (CrucibleRecipe) holder.value()).toList();
            }

            for(CrucibleRecipe recipe : relevantRecipes){
                SizedIngredient thisPossibleInput = (slot == INPUT_SLOT_1) ? recipe.input1() : recipe.input2();
                SizedIngredient otherPossibleInput = (slot == INPUT_SLOT_1) ? recipe.input2() : recipe.input1();

                if (otherStack.isEmpty() || otherPossibleInput.ingredient().test(otherStack)) {
                    maxCount = Math.max(maxCount, thisPossibleInput.count());
                }
            }

            return (maxCount > 0) ? maxCount : defaultLimit;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (!fluidTank.isEmpty() || getBlockState().getValue(ClayCrucibleBlock.SEALED)) return false;
            if (level == null) return false;

            RecipeManager recipeManager = level.getRecipeManager();

            int otherSlot = (slot == INPUT_SLOT_1) ? INPUT_SLOT_2 : INPUT_SLOT_1;
            ItemStack otherStack = itemHandler.getStackInSlot(otherSlot);

            List<CrucibleRecipe> relevantRecipes;

            if(isKilnPart()){
                relevantRecipes = recipeManager.getAllRecipesFor(MILFRecipeTypes.CLAY_CRUCIBLE_KILN).stream().map(holder -> (CrucibleRecipe) holder.value()).toList();
            } else {
                relevantRecipes = recipeManager.getAllRecipesFor(MILFRecipeTypes.CLAY_CRUCIBLE).stream().map(holder -> (CrucibleRecipe) holder.value()).toList();
            }

            for(CrucibleRecipe recipe : relevantRecipes){
                SizedIngredient thisPossibleInput = (slot == INPUT_SLOT_1) ? recipe.input1() : recipe.input2();
                SizedIngredient otherPossibleInput = (slot == INPUT_SLOT_1) ? recipe.input2() : recipe.input1();

                if (!thisPossibleInput.ingredient().test(stack)) continue;

                if (!otherStack.isEmpty()) {
                    if (!otherPossibleInput.ingredient().test(otherStack)) continue;
                    if (otherStack.getCount() > otherPossibleInput.count()) continue;
                }

                return true;
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

    public boolean isInProgress(){
        return progress > 0;
    }

    public boolean isKilnPart(){
        return getBlockState().getValue(ClayCrucibleBlock.KILN_PART);
    }

    public boolean isSealed(){
        return getBlockState().getValue(ClayCrucibleBlock.SEALED);
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
        CrucibleRecipe recipe = getCurrentRecipe();
        if(recipe != null){

            if(isKilnPart()){
                if(isSealed()){
                    setFull(true);
                    setLit(true);
                    currentRecipeTime = recipe.time();
                    return;
                } else {
                    setFull(false);
                    setLit(false);
                    return;
                }
            } else {
                setFull(true);
                currentRecipeTime = recipe.time();
                return;
            }


        }
        setFull(false);
    }

    private CrucibleRecipe getCurrentRecipe() {

        if(level == null) return null;

        ItemStack inputStack1 = itemHandler.getStackInSlot(INPUT_SLOT_1);
        if (inputStack1.isEmpty()) return null;
        ItemStack inputStack2 = itemHandler.getStackInSlot(INPUT_SLOT_2);
        if (inputStack2.isEmpty()) return null;

        if(isKilnPart()){
            ClayCrucibleKilnRecipeInput crucibleKilnRecipeInput = new ClayCrucibleKilnRecipeInput(inputStack1, inputStack2);

            var holder = level.getRecipeManager().getRecipeFor(MILFRecipeTypes.CLAY_CRUCIBLE_KILN, crucibleKilnRecipeInput, level);

            if(holder.isPresent()){
                return holder.get().value();
            }


        } else {
            ClayCrucibleRecipeInput clayCrucibleRecipeInput = new ClayCrucibleRecipeInput(inputStack1, inputStack2);

            var holder =  level.getRecipeManager().getRecipeFor(MILFRecipeTypes.CLAY_CRUCIBLE, clayCrucibleRecipeInput, level);

            if(holder.isPresent()){
                return holder.get().value();
            }
        }

        return null;
    }

    public void increaseProgress(){
        if(isKilnPart() && isLit) progress++;
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

        if(isKilnPart()){

            if(progress >= currentRecipeTime){
                progress = 0;

                ItemStack inputStack1 = itemHandler.getStackInSlot(INPUT_SLOT_1);
                ItemStack inputStack2 = itemHandler.getStackInSlot(INPUT_SLOT_2);

                ClayCrucibleKilnRecipeInput crucibleKilnRecipeInput = new ClayCrucibleKilnRecipeInput(inputStack1, inputStack2);

                Optional<RecipeHolder<ClayCrucibleKilnRecipe>> optionalClayCrucibleKilnRecipeRecipeHolder = level.getRecipeManager().getRecipeFor(MILFRecipeTypes.CLAY_CRUCIBLE_KILN, crucibleKilnRecipeInput, level);

                if(optionalClayCrucibleKilnRecipeRecipeHolder.isEmpty()) {
                    isLit = false;
                    isFull = false;

                    setChanged();
                    return;
                }

                ClayCrucibleKilnRecipe clayCrucibleKilnRecipe = optionalClayCrucibleKilnRecipeRecipeHolder.get().value();

                FluidStack fluidStack = clayCrucibleKilnRecipe.output();

                fluidTank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);

                itemHandler.extractItem(INPUT_SLOT_1, clayCrucibleKilnRecipe.input1().count(),false);
                itemHandler.extractItem(INPUT_SLOT_2, clayCrucibleKilnRecipe.input2().count(),false);

                isLit = false;
                isFull = false;

                setChanged();
                return;
            }

            if(progress %10 == 0){
                setChanged();
            }

        }else{
            progress++;

            if(progress >= currentRecipeTime){
                progress = 0;

                ItemStack inputStack = itemHandler.getStackInSlot(INPUT_SLOT_1);
                ItemStack fuelStack = itemHandler.getStackInSlot(INPUT_SLOT_2);

                ClayCrucibleRecipeInput clayCrucibleRecipeInput = new ClayCrucibleRecipeInput(inputStack, fuelStack);

                Optional<RecipeHolder<ClayCrucibleRecipe>> optionalClayCrucibleRecipeRecipeHolder = level.getRecipeManager().getRecipeFor(MILFRecipeTypes.CLAY_CRUCIBLE, clayCrucibleRecipeInput, level);

                if(optionalClayCrucibleRecipeRecipeHolder.isEmpty()) {
                    isLit = false;
                    isFull = false;

                    setChanged();
                    return;
                }

                ClayCrucibleRecipe clayCrucibleRecipe = optionalClayCrucibleRecipeRecipeHolder.get().value();

                FluidStack fluidStack = clayCrucibleRecipe.output();

                fluidTank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);

                itemHandler.extractItem(INPUT_SLOT_1,clayCrucibleRecipe.input1().count(),false);
                itemHandler.extractItem(INPUT_SLOT_2,clayCrucibleRecipe.input2().count(),false);

                isLit = false;
                isFull = false;

                setChanged();
                return;
            }

            if(progress %10 == 0){
                setChanged();
            }
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
