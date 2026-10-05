package dev.saeta.milf.blocks.anvils;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.anvils.bronze_anvil.BronzeAnvilBlockEntity;
import dev.saeta.milf.recipes.SingleInputSingleOutputRecipe;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.recipes.anvil.AnvilRecipe;
import dev.saeta.milf.recipes.anvil.AnvilTier;
import dev.saeta.milf.registries.MILFDataComponents;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractAnvilBlockEntity extends BlockEntity {

    protected final static int ITEM_SLOT = 0;
    protected final AnvilItemHandler itemHandler = new AnvilItemHandler(1);
    private final Predicate<RecipeHolder<AnvilRecipe>> recipePredicate;

    private float currentRecipeMaxHit = 0.35f;

    public AbstractAnvilBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, Predicate<RecipeHolder<AnvilRecipe>> recipePredicate) {
        super(type, pos, blockState);
        this.recipePredicate = recipePredicate;
    }

    public AnvilItemHandler getItemHandler() {
        return itemHandler;
    }

    public boolean hasItem(){
        return !itemHandler.getStackInSlot(ITEM_SLOT).isEmpty();
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        ListTag itemStacks = new ListTag();

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            itemStacks.add(stack.isEmpty() ? new CompoundTag() : stack.save(registries));
        }

        tag.put("items", itemStacks);

        tag.putFloat("maxHit", currentRecipeMaxHit);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains("items", Tag.TAG_LIST)){
            ListTag itemStacks = tag.getList("items", Tag.TAG_COMPOUND);


            for (int i = 0; i < itemHandler.getSlots() && i < itemStacks.size(); i++) {
                CompoundTag stackCompound = itemStacks.getCompound(i);
                ItemStack itemStack = stackCompound.isEmpty() ? ItemStack.EMPTY : ItemStack.parse(registries, stackCompound).orElse(ItemStack.EMPTY);
                itemHandler.setStackInSlot(i, itemStack);
            }

        }

        if(tag.contains("maxHit")){
            currentRecipeMaxHit = tag.getFloat("maxHit");
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

    public class AnvilItemHandler extends ItemStackHandler {
        public AnvilItemHandler(int size) {
            super(size);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty() || level == null) return false;

            var recipe = getValidRecipe(new SingleRecipeInput(stack));

            if(recipe.isPresent()){
                currentRecipeMaxHit = recipe.get().value().maxHit();
                //MILostFavor.LOGGER.info(String.valueOf(currentRecipeMaxHit));
                return true;
            }

            return false;


        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack stack = super.extractItem(slot, amount, simulate);

            stack.remove(MILFDataComponents.RANDOM_SEED);

            return stack;
        }

        public void extractAndDropResult(int slot){
            if (level == null) return;

            ItemStack stack = getResult(extractItem(slot, 1, false)) ;

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
