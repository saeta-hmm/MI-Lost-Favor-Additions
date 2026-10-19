package dev.saeta.milf.blocks.kiln.fired;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.ItemHandlerBlockEntity;
import dev.saeta.milf.blocks.ProgressProvider;
import dev.saeta.milf.blocks.bloomery.fired.BloomeryBaseBlockEntity;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntity;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.recipes.kiln.KilnSmeltingRecipe;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFRecipeTypes;
import dev.saeta.milf.util.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KilnBlockEntity extends ItemHandlerBlockEntity implements ProgressProvider {

    private static final int INPUT_SLOT = 0;
    private static final int MAX_PROGRESS = 200;

    private int progress;
    private boolean hasItemForExtraction = false;

    private boolean requiresBellows = false;

    private final AbstractBEItemHandler itemHandler = new AbstractBEItemHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (level == null) return false;

            ItemStack singleItem = stack.copyWithCount(1);

            var furnaceRecipe = RecipeUtil.getValidRecipe(level, RecipeType.SMELTING, new net.minecraft.world.item.crafting.SingleRecipeInput(singleItem));
            if(furnaceRecipe.isPresent()) return true;

            var recipe = RecipeUtil.getValidRecipe(level, MILFRecipeTypes.KILN_SMELTING, new SingleRecipeInput(singleItem));

            return recipe.isPresent();

        }


        @Override
        protected void onContentsChanged(int slot) {

            if(level == null) return;

            ItemStack stack = getStackInSlot(slot);

            if (stack.isEmpty()) {
                progress = 0;
                hasItemForExtraction=false;
                requiresBellows = false;
                level.setBlock(worldPosition, getBlockState().setValue(KilnBlock.CONTAINS_BLOCK, false), Block.UPDATE_NONE);
                setChanged();
                return;
            }

            ItemStack singleItem = stack.copyWithCount(1);

            var recipe = RecipeUtil.getValidRecipe(level, MILFRecipeTypes.KILN_SMELTING, new SingleRecipeInput(singleItem));

            if(recipe.isPresent()){
                level.setBlock(worldPosition, getBlockState().setValue(KilnBlock.CONTAINS_BLOCK, true), Block.UPDATE_NONE);
                if(recipe.get().value().requiresBellows()) requiresBellows = true;
                setChanged();
                return;
            }

            var furnaceRecipe = RecipeUtil.getValidRecipe(level, RecipeType.SMELTING, new net.minecraft.world.item.crafting.SingleRecipeInput(singleItem));

            if(furnaceRecipe.isPresent()){
                level.setBlock(worldPosition, getBlockState().setValue(KilnBlock.CONTAINS_BLOCK, true), Block.UPDATE_NONE);
                setChanged();
                return;
            }

        }

        @Override
        protected boolean testInput(ItemStack stack) {
            if(level == null) return false;
            if(!(stack.getItem() instanceof BlockItem)) return false;
            return !level.getBlockState(worldPosition.above()).is(MILFBlocks.CLAY_CRUCIBLE);
        }


    };


    public KilnBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.KILN.get(), pos, blockState);
    }

    public AbstractBEItemHandler getItemHandler() {
        return itemHandler;
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
    public Direction getOutputDirection() {
        return Direction.UP;
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


        if(isInProgress()) {
            progress++;
            firePitBlockEntity.increaseBurnTicks(3);
            return;
        }

        if((level.getBlockEntity(worldPosition.above()) instanceof ClayCrucibleBlockEntity clayCrucibleBlockEntity)){
            clayCrucibleBlockEntity.increaseProgress();
            firePitBlockEntity.increaseBurnTicks(8);
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

        if(!requiresBellows) {
            progress++;
        }

        if(progress >= MAX_PROGRESS){

            RecipeManager recipeManager = level.getRecipeManager();

            progress=0;
            requiresBellows=false;
            ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);

            var furnaceRecipeInput =  new net.minecraft.world.item.crafting.SingleRecipeInput(input);

            var smeltingRecipeOutput = recipeManager.getRecipeFor(
                    RecipeType.SMELTING,
                    furnaceRecipeInput,
                    level
            );

            if(smeltingRecipeOutput.isPresent()){
                itemHandler.setStackInSlot(INPUT_SLOT, smeltingRecipeOutput.get().value().assemble(furnaceRecipeInput, level.registryAccess()));
                hasItemForExtraction =true;
                setChanged();
                return;
            }

            SingleRecipeInput recipeInput = new SingleRecipeInput(input);

            var ownRecipeOutput = recipeManager.getRecipeFor(
                    MILFRecipeTypes.KILN_SMELTING,
                    recipeInput,
                    level
            );

            if(ownRecipeOutput.isPresent()){
                itemHandler.setStackInSlot(INPUT_SLOT, ownRecipeOutput.get().value().assemble(recipeInput, level.registryAccess()));
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
        return progress > 0 || (!itemHandler.getStackInSlot(0).isEmpty() && requiresBellows && !hasItemForExtraction);
    }

    @Override
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

        tag.putInt("progress", progress);

        tag.putBoolean("hasItemForExtraction", hasItemForExtraction);

        tag.putBoolean("requiresBellows", requiresBellows);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("progress")) {
            progress = tag.getInt("progress");
        }

        if (tag.contains("hasItemForExtraction")) {
            hasItemForExtraction = tag.getBoolean("hasItemForExtraction");
        }

        if (tag.contains("requiresBellows")) {
            requiresBellows = tag.getBoolean("requiresBellows");
        }


    }

}
