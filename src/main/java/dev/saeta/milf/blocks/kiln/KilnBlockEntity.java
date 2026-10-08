package dev.saeta.milf.blocks.kiln;

import dev.saeta.milf.blocks.bloomery.BloomeryBaseBlockEntity;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntity;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.recipes.kiln.KilnSmeltingRecipe;
import dev.saeta.milf.registries.MILFBlockEntities;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KilnBlockEntity extends BlockEntity {

    private static final int INPUT_SLOT = 0;

    private static final int MAX_PROGRESS = 200;

    private int progress;

    private boolean hasItemForExtraction = false;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (level == null) return false;

            RecipeManager recipeManager = level.getRecipeManager();

            List<KilnSmeltingRecipe> allRecipes = KilnSmeltingRecipe.getAllRecipes(recipeManager, level.registryAccess());
            return allRecipes.stream()
                    .anyMatch(kilnSmeltingRecipe -> {
                        return kilnSmeltingRecipe.input().test(stack);
                    });

        }


        @Override
        protected void onContentsChanged(int slot) {

            if(level == null) return;

            if (getStackInSlot(slot).isEmpty()) {
                progress = 0;
                hasItemForExtraction=false;
                level.setBlock(worldPosition, getBlockState().setValue(KilnBlock.CONTAINS_BLOCK, false), Block.UPDATE_NONE);
                setChanged();
                return;
            }

            level.setBlock(worldPosition, getBlockState().setValue(KilnBlock.CONTAINS_BLOCK, true), Block.UPDATE_NONE);
            setChanged();
        }



    };


    public KilnBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.KILN.get(), pos, blockState);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ItemStack insertAnywhere(ItemStack stack, boolean simulate) {
        ItemStack remaining = stack;
        for (int i = 0; i < itemHandler.getSlots() && !remaining.isEmpty(); i++) {
            remaining = itemHandler.insertItem(i, remaining, simulate);
        }
        return remaining;
    }

    public void passBellowsTick(){
        if(level == null) return;

        if(level.getBlockEntity(worldPosition.below()) instanceof BloomeryBaseBlockEntity bloomeryBaseBlockEntity){
            if(bloomeryBaseBlockEntity.isLit()){
                bloomeryBaseBlockEntity.handleBellowsTick();
            }

            return;
        }

        if(!(level.getBlockEntity(worldPosition.below()) instanceof FirePitBlockEntity firePitBlockEntity)) return;

        if(!firePitBlockEntity.isLit()) return;

        firePitBlockEntity.increaseBurnTicks(8);

        if(isInProgress()) {
            progress++;
        }

        if((level.getBlockEntity(worldPosition.above()) instanceof ClayCrucibleBlockEntity clayCrucibleBlockEntity)){
            clayCrucibleBlockEntity.increaseProgress();
            return;
        }

    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, KilnBlockEntity kilnBlockEntity){
        kilnBlockEntity.tick();
    }

    private void tick(){
        if(level == null) return;
        if(!containsBlock()) return;
        if(hasItemForExtraction) return;
        if(!(level.getBlockEntity(worldPosition.below()) instanceof FirePitBlockEntity firePitBlockEntity)) return;

        if(!firePitBlockEntity.isLit()) return;

        progress++;

        if(progress >= MAX_PROGRESS){

            RecipeManager recipeManager = level.getRecipeManager();

            progress=0;
            ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);

            SingleRecipeInput recipeInput = new SingleRecipeInput(input);

            var smeltingRecipeOutput = recipeManager.getRecipeFor(
                    RecipeType.SMELTING,
                    new net.minecraft.world.item.crafting.SingleRecipeInput(input),
                    level
            );

            if(smeltingRecipeOutput.isPresent()){
                itemHandler.setStackInSlot(INPUT_SLOT, smeltingRecipeOutput.get().value().getResultItem(level.registryAccess()));
                hasItemForExtraction =true;
                setChanged();
                return;
            }

            var ownRecipeOutput = recipeManager.getRecipeFor(
                    MILFRecipeTypes.KILN_SMELTING,
                    recipeInput,
                    level
            );

            if(ownRecipeOutput.isPresent()){
                itemHandler.setStackInSlot(INPUT_SLOT, ownRecipeOutput.get().value().getResultItem(level.registryAccess()));
                hasItemForExtraction =true;
                setChanged();
                return;
            }
        }

        if(progress %10 == 0){
            setChanged();
        }
    }

    public boolean isInProgress(){
        return progress > 0;
    }

    public float getCurrentProgress(){
        return (float) progress / MAX_PROGRESS;
    }

    private boolean containsBlock(){
        return getBlockState().getValue(KilnBlock.CONTAINS_BLOCK);
    }

    public void spawnBellowsParticles(){

        if(!(level instanceof ServerLevel serverLevel)) return;

        if(!(serverLevel.getBlockEntity(worldPosition.below()) instanceof FirePitBlockEntity firePitBlockEntity)) return;

        if(!firePitBlockEntity.isLit()) return;

        Direction back =getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();

        double spawnOffset = 0.7;

        double posX = worldPosition.getX()+ 0.5 + back.getStepX() * spawnOffset;
        double posY = worldPosition.getY() + 0.5;
        double posZ= worldPosition.getZ()+ 0.5+ back.getStepZ() * spawnOffset;

        serverLevel.sendParticles(
                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                posX,
                posY,
                posZ,
                0,
                back.getStepX() * 0.03, 0.2, back.getStepZ() * 0.03,
                0.32
        );
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

        tag.putInt("progress", progress);

        tag.putBoolean("hasItemForExtraction", hasItemForExtraction);


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


        if (tag.contains("progress")) {
            progress = tag.getInt("progress");
        }

        if (tag.contains("hasItemForExtraction")) {
            hasItemForExtraction = tag.getBoolean("hasItemForExtraction");
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
