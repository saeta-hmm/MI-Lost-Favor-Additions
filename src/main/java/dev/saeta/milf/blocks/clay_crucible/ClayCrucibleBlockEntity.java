package dev.saeta.milf.blocks.clay_crucible;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.FlammableBlockEntity;
import dev.saeta.milf.blocks.ItemHandlerBlockEntity;
import dev.saeta.milf.blocks.ProgressProvider;
import dev.saeta.milf.recipes.clay_crucible.*;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ClayCrucibleBlockEntity extends ItemHandlerBlockEntity implements FlammableBlockEntity, ProgressProvider {

    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;

    private int currentRecipeTime = 109;
    private boolean isFull;
    private boolean isLit;
    private int progress;


    private final AbstractBEItemHandler itemHandler = new AbstractBEItemHandler(2) {

        @Override
        public int getExtractAmount() {
            return 64;
        }

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

            List<CrucibleRecipe> relevantRecipes = List.of();

            if(isKilnPart()){
                relevantRecipes = recipeManager.getAllRecipesFor(MILFRecipeTypes.CLAY_CRUCIBLE_KILN).stream().map(holder -> (CrucibleRecipe) holder.value()).toList();
            } else if (isDirt()){
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

        @Override
        protected boolean testInput(ItemStack stack) {
            return !stack.isEmpty() && !isSealed() && fluidTank.isEmpty();
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack itemStack = super.extractItem(slot, amount, simulate);
            checkAndSetIfFull();
            return itemStack;
        }
    };

    private final FluidTank fluidTank = new FluidTank(1000) {

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return itemHandler.getStackInSlot(INPUT_SLOT_1).isEmpty() && itemHandler.getStackInSlot(INPUT_SLOT_2).isEmpty();
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    public ClayCrucibleBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.CLAY_CRUCIBLE.get(), pos, blockState);
    }

    @Override
    public AbstractBEItemHandler getItemHandler(){
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
        return getCurrentRecipe() != null && !isLit && !isSealed() && !isKilnPart();
    }

    @Override
    public void ignite() {
        if (checkAndSetIfFull()) isLit = true;

    }

    @Override
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

    public boolean isDirt(){
        return getBlockState().getValue(ClayCrucibleBlock.DIRT);
    }

    @Override
    public float getCurrentProgress(){
        return (float) progress / currentRecipeTime;
    }

    public boolean isFull() {
        return isFull;
    }

    public boolean checkAndSetIfFull(){
        CrucibleRecipe recipe = getCurrentRecipe();
        if(recipe != null){

            if(isKilnPart()){
                if(isSealed()){
                    setFull(true);
                    setLit(true);
                    currentRecipeTime = recipe.time();
                    return true;
                } else {
                    setFull(false);
                    setLit(false);
                    return false;
                }
            } else {
                setFull(true);
                currentRecipeTime = recipe.time();
                return true;
            }


        }
        setFull(false);
        return false;
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

    @Override
    public boolean canExtract() {
        return !isSealed() && !isInProgress();
    }

    @Override
    public boolean shouldDropContent() {
        return true;
    }

    @Override
    public @NotNull BlockPos getOutputPos() {
        return worldPosition;
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

                fluidTank.setFluid(fluidStack);

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

                fluidTank.setFluid(fluidStack);

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

        tag.put("fluid", fluidTank.writeToNBT(registries, new CompoundTag()));

        tag.putBoolean("isFull", isFull);

        tag.putBoolean("isLit", isLit);

        tag.putInt("progress", progress);

        tag.putInt("currentRecipeTime", currentRecipeTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

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

}
