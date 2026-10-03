package dev.saeta.milf.blocks.bronze_anvil;

import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.recipes.bronze_anvil.BronzeAnvilRecipe;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.registries.MILFItems;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BronzeAnvilBlockEntity extends BlockEntity {

    private final static int ITEM_SLOT = 0;

    private final BronzeAnvilItemHandler itemHandler = new BronzeAnvilItemHandler(1);

    public BronzeAnvilBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.BRONZE_ANVIL.get(), pos, blockState);
    }

    public BronzeAnvilItemHandler getItemHandler() {
        return itemHandler;
    }

    public ItemStack insertAnywhere(ItemStack stack) {
        ItemStack remaining = stack;
        for (int i = 0; i < itemHandler.getSlots() && !remaining.isEmpty(); i++) {
            remaining = itemHandler.insertItem(i, remaining, false);
        }
        return remaining;
    }

    public boolean hasItem(){
        return !itemHandler.getStackInSlot(ITEM_SLOT).isEmpty();
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

    public void handleHit(float accuracy, float progress, float volume){

        if(level == null) return;

        level.playSound(null, worldPosition, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.BLOCKS, 0.65f * volume,accuracy + (float) level.random.nextInt(1, 5) / 10);
        itemHandler.regenerateSeed();

        if(progress >= 1){
            itemHandler.extractAndDropResult(ITEM_SLOT);
        }

    }

    private ItemStack getResult(ItemStack stack){
        if (level == null) return stack;

        RecipeManager recipeManager = level.getRecipeManager();

        SingleRecipeInput recipeInput = new SingleRecipeInput(stack);

        var recipe = recipeManager.getRecipeFor(
                MILFRecipeTypes.BRONZE_ANVIL,
                recipeInput,
                level
        );

        return recipe.map(bronzeAnvilRecipeRecipeHolder -> bronzeAnvilRecipeRecipeHolder.value().assemble(recipeInput, level.registryAccess())).orElse(stack);

    }

    public class BronzeAnvilItemHandler extends ItemStackHandler{
        public BronzeAnvilItemHandler(int size) {
            super(size);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (level == null) return false;

            RecipeManager recipeManager = level.getRecipeManager();

            List<BronzeAnvilRecipe> allRecipes = recipeManager.getAllRecipesFor(MILFRecipeTypes.BRONZE_ANVIL).stream().map(RecipeHolder::value).toList();
            return allRecipes.stream()
                    .anyMatch(bronzeAnvilRecipe -> {
                        return bronzeAnvilRecipe.input().test(stack);
                    });

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

            for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
                ItemStack stack = itemHandler.getStackInSlot(i);

                if(!stack.isEmpty()) stack.set(MILFDataComponents.RANDOM_SEED, level.random.nextInt(109, 109109));

            }

            setChanged();
        }
    }
}
