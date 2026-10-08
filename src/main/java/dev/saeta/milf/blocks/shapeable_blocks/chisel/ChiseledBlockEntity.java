package dev.saeta.milf.blocks.shapeable_blocks.chisel;

import dev.saeta.milf.blocks.shapeable_blocks.AbstractShapeableBlockEntity;
import dev.saeta.milf.recipes.shaping.ShapingRecipeInput;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ChiseledBlockEntity extends AbstractShapeableBlockEntity {

    protected Block currentOutputBlock = Blocks.AIR;
    protected ItemStack currentInputBlockItem = new ItemStack(Blocks.STONE);

    public ChiseledBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.CHISELED_BLOCK.get(), pos, blockState);
    }

    public void setInputBlockItem(ItemStack inputBlockItem) {
        this.currentInputBlockItem = inputBlockItem;
    }

    @Override
    public ItemStack getCurrentInputBlockItem() {
        return currentInputBlockItem;
    }

    @Override
    public BlockState getCurrentInputBlockState() {
        if(!(currentInputBlockItem.getItem() instanceof BlockItem blockItem)) return getBlockState();

        return blockItem.getBlock().defaultBlockState();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        ResourceLocation blockKey = BuiltInRegistries.BLOCK.getKey(currentOutputBlock);
        tag.putString("currentOutputBlock", blockKey.toString());

        tag.put("currentInputBlockItem",currentInputBlockItem.save(registries));

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("currentOutputBlock")) {
            ResourceLocation blockKey = ResourceLocation.parse(tag.getString("currentOutputBlock"));
            currentOutputBlock = BuiltInRegistries.BLOCK.get(blockKey);
        }

        if(tag.contains("currentInputBlockItem")){
            currentInputBlockItem = ItemStack.parse(registries, tag.getCompound("currentInputBlockItem")).orElse(new ItemStack(Blocks.STONE));
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

    public Block getCurrentOutputBlock() {
        return currentOutputBlock;
    }

    @Override
    public void updateOutput(){
        if(level == null){
            currentOutputBlock = Blocks.AIR;
            return;
        }

        int sideChippedValue = getBlockState().getValue(ChiseledBlock.SIDE_LAYERS_CHIPPED);
        int topChippedValue = getBlockState().getValue(ChiseledBlock.TOP_LAYERS_CHIPPED);

        RecipeManager recipeManager = level.getRecipeManager();

        ShapingRecipeInput shapingRecipeInput = new ShapingRecipeInput(currentInputBlockItem, topChippedValue, sideChippedValue);

        var recipe = recipeManager.getRecipeFor(MILFRecipeTypes.CHISEL, shapingRecipeInput, level);

        if(recipe.isPresent()){
            Item outputItem = recipe.get().value().getResultItem(level.registryAccess()).getItem();

            if(outputItem instanceof BlockItem outputBlockItem){
                currentOutputBlock = outputBlockItem.getBlock();
            } else {
                currentOutputBlock = Blocks.AIR;
            }
        } else {
            currentOutputBlock = Blocks.AIR;
        }

        setChanged();
    }

    public void handleHit(Direction face, Player player){

        if(!(level instanceof ServerLevel serverLevel)) return;

        BlockState inputBlockState = getCurrentInputBlockState();

        int x = worldPosition.getX();
        int y = worldPosition.getY();
        int z = worldPosition.getZ();

        if(!currentOutputBlock.defaultBlockState().isAir() && !player.isShiftKeyDown()) {
            level.setBlock(worldPosition, currentOutputBlock.defaultBlockState(), Block.UPDATE_NONE);
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, inputBlockState),
                    x + 0.5, y + 1, z + 0.5,
                    35,
                    0.2, 0.2, 0.2,
                    0.4
            );
            level.playSound(null, worldPosition, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1f,0.6f + (float) level.random.nextInt(1, 3) / 10);
            currentOutputBlock = Blocks.AIR;
            setChanged();
            return;
        }



        int sideChippedValue = getBlockState().getValue(ChiseledBlock.SIDE_LAYERS_CHIPPED);
        int topChippedValue = getBlockState().getValue(ChiseledBlock.TOP_LAYERS_CHIPPED);

        switch (face){
            case DOWN -> {
            }
            case UP -> {
                handleTopHit(topChippedValue);
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, inputBlockState),
                        x + 0.5, y + 1, z + 0.5,
                        17,
                        0.2, 0.2, 0.2,
                        0.1
                );
            }
            case EAST -> {
                handleSideHit(sideChippedValue);
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, inputBlockState),
                        x + 1, y + 0.5, z + 0.5,
                        17,
                        0.2, 0.2, 0.2,
                        0.1
                );
            }
            case WEST -> {
                handleSideHit(sideChippedValue);
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, inputBlockState),
                        x, y + 0.5, z + 0.5,
                        17,
                        0.2, 0.2, 0.2,
                        0.1
                );
            }
            case SOUTH -> {
                handleSideHit(sideChippedValue);
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, inputBlockState),
                        x + 0.5, y + 0.5, z + 1,
                        17,
                        0.2, 0.2, 0.2,
                        0.1
                );
            }
            case NORTH -> {
                handleSideHit(sideChippedValue);
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, inputBlockState),
                        x + 0.5, y + 0.5, z,
                        17,
                        0.2, 0.2, 0.2,
                        0.1
                );
            }
        }

        updateOutput();
    }

    private void handleSideHit(int chippedValue){
        if(level == null) return;

        if(chippedValue == 7){
            level.playSound(null, worldPosition, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1f,0.2f + (float) level.random.nextInt(1, 3) / 10);
            level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(),  Block.UPDATE_NONE);
            return;
        }

        level.playSound(null, worldPosition, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1f,0.2f + (float) level.random.nextInt(1, 3) / 10);
        level.setBlock(worldPosition, getBlockState().setValue(ChiseledBlock.SIDE_LAYERS_CHIPPED, chippedValue + 1), Block.UPDATE_NONE);
    }

    private void handleTopHit(int chippedValue){
        if(level == null) return;

        if(chippedValue == 15){
            level.playSound(null, worldPosition, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1f,0.2f + (float) level.random.nextInt(1, 3) / 10);
            level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(),  Block.UPDATE_NONE);
            return;
        }

        level.playSound(null, worldPosition, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1f,0.2f + (float) level.random.nextInt(1, 3) / 10);
        level.setBlock(worldPosition, getBlockState().setValue(ChiseledBlock.TOP_LAYERS_CHIPPED, chippedValue + 1), Block.UPDATE_NONE);
    }
}
