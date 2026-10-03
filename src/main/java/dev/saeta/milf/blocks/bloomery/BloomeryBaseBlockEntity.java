package dev.saeta.milf.blocks.bloomery;

import dev.saeta.milf.blocks.FlammableBlockEntity;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlock;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntity;
import dev.saeta.milf.blocks.kiln.KilnBlock;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.recipes.bloomery.BloomeryRecipe;
import dev.saeta.milf.recipes.bloomery.BloomeryRecipeInput;
import dev.saeta.milf.recipes.clay_crucible.CrucibleRecipe;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItemTags;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BloomeryBaseBlockEntity extends BlockEntity implements FlammableBlockEntity {

    protected static final int INPUT_SLOT_1 = 0;
    protected static final int INPUT_SLOT_2 = 1;
    protected static final int COAL_SLOT = 2;

    private boolean isLit;
    private boolean hasValidRecipe;

    private int progress;
    private int currentRecipeTime = 109;

    public BloomeryBaseBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.BLOOMERY_BASE.get(), pos, blockState);
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        public int getSlotLimit(int slot) {

            if(slot == COAL_SLOT) return 4;

            int defaultLimit = 64;
            if (level == null) return defaultLimit;

            int otherSlot = (slot == INPUT_SLOT_1) ? INPUT_SLOT_2 : INPUT_SLOT_1;
            ItemStack otherStack = itemHandler.getStackInSlot(otherSlot);

            RecipeManager recipeManager = level.getRecipeManager();
            int maxCount = 0;

            List<BloomeryRecipe> relevantRecipes;
            relevantRecipes = recipeManager.getAllRecipesFor(MILFRecipeTypes.BLOOMERY).stream().map(holder -> (BloomeryRecipe) holder.value()).toList();

            for(BloomeryRecipe recipe : relevantRecipes){
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
            if (level == null) return false;

            if(slot == COAL_SLOT) return stack.is(MILFItemTags.BLOOMERY_COALS);

            RecipeManager recipeManager = level.getRecipeManager();

            int otherSlot = (slot == INPUT_SLOT_1) ? INPUT_SLOT_2 : INPUT_SLOT_1;
            ItemStack otherStack = itemHandler.getStackInSlot(otherSlot);

            List<BloomeryRecipe> relevantRecipes;
            relevantRecipes = recipeManager.getAllRecipesFor(MILFRecipeTypes.BLOOMERY).stream().map(holder -> (BloomeryRecipe) holder.value()).toList();


            for(BloomeryRecipe recipe : relevantRecipes){
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
            if (level == null) return;

            var currentRecipe = level.getRecipeManager()
                    .getRecipeFor(
                            MILFRecipeTypes.BLOOMERY,
                            new BloomeryRecipeInput(getStackInSlot(INPUT_SLOT_1), getStackInSlot(INPUT_SLOT_2)),
                            level
                    );

            currentRecipe.ifPresent(bloomeryRecipeRecipeHolder -> {
                        currentRecipeTime = bloomeryRecipeRecipeHolder.value().time();
                        hasValidRecipe = true;
                    });

            setChanged();


        }

    };

    public void setLit(boolean value) {
        isLit = value;
        if (this.level == null || this.level.isClientSide) return;
        level.setBlock(worldPosition, getBlockState().setValue(BlockStateProperties.LIT, value), Block.UPDATE_NONE);
        setChanged();
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ItemStack insertAnywhere(ItemStack stack) {
        ItemStack remaining = stack;
        for (int i = 0; i < itemHandler.getSlots() && !remaining.isEmpty(); i++) {
            remaining = itemHandler.insertItem(i, remaining, false);
        }
        return remaining;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BloomeryBaseBlockEntity bloomeryBaseBlockEntity){
        bloomeryBaseBlockEntity.tick();
    }

    private void tick(){
        if(level == null) return;
        if(!isLit) return;



    }

    public void handleBellowsTick(){
        if(level == null) return;
        if(!(level instanceof ServerLevel serverLevel)) return;
        if(!isLit || !hasValidRecipe) return;
        increaseProgress();

        if(progress % 3 == 0){
            serverLevel.sendParticles(
                    ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1,
                    worldPosition.getZ() + 0.5,
                    0,
                    0, 0.12, 0,
                    0.8
            );
        }



        if(progress % (currentRecipeTime / 4) == 0){
            ItemStack coalStack = itemHandler.getStackInSlot(COAL_SLOT);
            coalStack.shrink(1);
            itemHandler.setStackInSlot(COAL_SLOT, coalStack);

            level.playSound(null, worldPosition.above(),
                    SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS,
                    1f, 0.8f + (float) level.random.nextInt(1, 2) / 10);
        }

        if(progress >= currentRecipeTime){
            progress = 0;
            setLit(false);

            ItemStack stack1 = itemHandler.getStackInSlot(INPUT_SLOT_1);
            ItemStack stack2 = itemHandler.getStackInSlot(INPUT_SLOT_2);

            RecipeManager recipeManager = level.getRecipeManager();

            var recipeInput = new BloomeryRecipeInput(stack1, stack2);

            var recipe = recipeManager.getRecipeFor(
                    MILFRecipeTypes.BLOOMERY,
                    recipeInput,
                    level
            );

            if(recipe.isPresent()){
                itemHandler.setStackInSlot(INPUT_SLOT_1, ItemStack.EMPTY);
                itemHandler.setStackInSlot(INPUT_SLOT_2, ItemStack.EMPTY);

                itemHandler.setStackInSlot(INPUT_SLOT_1, recipe.get().value().assemble(recipeInput, level.registryAccess()));


            }

        }
    }


    public void increaseProgress(){
        progress++;
    }

    public float getCurrentProgress(){
        return (float) progress / currentRecipeTime;
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

        tag.putBoolean("isLit", isLit);

        tag.putBoolean("hasValidRecipe", hasValidRecipe);

        tag.putInt("progress", progress);

        tag.putInt("currentRecipeTime", currentRecipeTime);

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

        if (tag.contains("isLit")) {
            isLit = tag.getBoolean("isLit");
        }

        if (tag.contains("hasValidRecipe")) {
            hasValidRecipe = tag.getBoolean("hasValidRecipe");
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

    @Override
    public boolean canBeIgnited() {
        return itemHandler.getStackInSlot(COAL_SLOT).getCount() == 4;
    }

    @Override
    public boolean isLit() {
        return isLit;
    }

    @Override
    public void ignite() {
        setLit(true);
    }
}
