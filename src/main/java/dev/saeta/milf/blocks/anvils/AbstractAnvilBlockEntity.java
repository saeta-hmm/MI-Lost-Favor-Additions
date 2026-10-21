package dev.saeta.milf.blocks.anvils;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.ItemHandlerBlockEntity;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.recipes.anvil.AnvilRecipe;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractAnvilBlockEntity extends ItemHandlerBlockEntity {

    protected final static int ITEM_SLOT = 0;
    protected final AnvilItemHandler itemHandler = new AnvilItemHandler(1);
    private final Predicate<RecipeHolder<AnvilRecipe>> recipePredicate;

    private float currentRecipeMaxHit = 0;
    private int currentRecipeInputCount = 64;

    public AbstractAnvilBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, Predicate<RecipeHolder<AnvilRecipe>> recipePredicate) {
        super(type, pos, blockState);
        this.recipePredicate = recipePredicate;
    }

    public AnvilItemHandler getItemHandler() {
        return itemHandler;
    }

    public boolean hasRecipe(){
        return !itemHandler.getStackInSlot(ITEM_SLOT).isEmpty() &&
                itemHandler.getStackInSlot(ITEM_SLOT).getCount() == currentRecipeInputCount;
    }

    public ItemStack insertAnywhere(ItemStack stack) {
        ItemStack remaining = stack;
        for (int i = 0; i < itemHandler.getSlots() && !remaining.isEmpty(); i++) {
            remaining = itemHandler.insertItem(i, remaining, false);
        }
        return remaining;
    }

    private ItemStack getResult(ItemStack stack){
        if (level == null) return stack;

        SingleRecipeInput recipeInput = new SingleRecipeInput(stack);

        return getValidRecipe(recipeInput).map(holder -> holder.value().assemble(recipeInput, level.registryAccess())).orElse(stack);

    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean shouldDropContent() {
        return true;
    }

    @Override
    public @NotNull BlockPos getOutputPos() {
        return worldPosition;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putFloat("maxHit", currentRecipeMaxHit);

        tag.putInt("currentRecipeInputCount", currentRecipeInputCount);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains("maxHit")){
            currentRecipeMaxHit = tag.getFloat("maxHit");
        }

        if(tag.contains("currentRecipeInputCount")){
            currentRecipeInputCount = tag.getInt("currentRecipeInputCount");
        }

    }


    public float getMaxHit(){
        return currentRecipeMaxHit;
    }

    public abstract void handleHit(float accuracy, float progress, float volume);

    private Stream<RecipeHolder<AnvilRecipe>> getAllRecipes(){
        if (level == null) return Stream.empty();

        RecipeManager recipeManager = level.getRecipeManager();

        return recipeManager.getAllRecipesFor(MILFRecipeTypes.ANVIL).stream().filter(recipePredicate);
    }

    private Optional<RecipeHolder<AnvilRecipe>> getValidRecipe(SingleRecipeInput input){
        if (level == null) return Optional.empty();

        return getAllRecipes().filter(holder -> holder.value().matches(input, level)).findFirst();
    }

    public class AnvilItemHandler extends AbstractBEItemHandler {
        public AnvilItemHandler(int size) {
            super(size);
        }

        @Override
        public int getSlotLimit(int slot) {
            return currentRecipeInputCount;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty() || level == null) return false;

            var recipe = getValidRecipe(new SingleRecipeInput(stack.copyWithCount(64)));

            if(recipe.isPresent()){
                currentRecipeMaxHit = recipe.get().value().maxHit();
                currentRecipeInputCount = recipe.get().value().input().count();
                return true;
            }

            return false;


        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if(level == null) return super.insertItem(slot, stack, simulate);
            int seed =  level.random.nextInt(109, 109109);
            if(!getStackInSlot(slot).isEmpty()) seed = getStackInSlot(slot).getOrDefault(MILFDataComponents.RANDOM_SEED, seed);
            ItemStack toInsert = stack.copy();
            toInsert.set(MILFDataComponents.RANDOM_SEED,seed);
            return super.insertItem(slot, toInsert, simulate);

        }

        @Override
        protected void onContentsChanged(int slot) {

            ItemStack stack = getStackInSlot(slot);
            var recipe = getValidRecipe(new SingleRecipeInput(stack));

            if(recipe.isPresent()){
                currentRecipeMaxHit = recipe.get().value().maxHit();
                currentRecipeInputCount = recipe.get().value().input().count();
            } else {
                currentRecipeMaxHit = 0;
                currentRecipeInputCount = 64;
            }

            setChanged();
        }

        @Override
        protected boolean testInput(ItemStack stack) {
            return !stack.isEmpty();
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack stack = super.extractItem(slot, amount, simulate);

            stack.remove(MILFDataComponents.RANDOM_SEED);

            return stack;
        }

        public void extractAndDropResult(int slot){
            if (level == null) return;

            ItemStack stack = getResult(extractItem(slot, currentRecipeInputCount, false)) ;

            Block.popResourceFromFace(level, worldPosition, Direction.UP, stack);
        }

        public void regenerateSeed(){

            if(level == null) return;

            for (int i = getSlots() - 1; i >= 0; i--) {
                ItemStack stack = getStackInSlot(i);

                if(!stack.isEmpty()) stack.set(MILFDataComponents.RANDOM_SEED, level.random.nextInt(109, 109109));

            }

            setChanged();
        }
    }

}
