package dev.saeta.milf.blocks;

import dev.saeta.milf.blocks.fire_pit.FirePitProcessable;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFRecipeTypes;
import dev.saeta.milf.util.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class UnfiredBlockEntity extends BlockEntity implements FirePitProcessable, ProgressProvider {

    private int firingTicks = 0;
    private final static int MAX_FIRING_TICKS = 200;

    public UnfiredBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.UNFIRED_BLOCK.get(), pos, blockState);
    }

    protected abstract void handleRecipeOutput(BlockItem blockItem);

    @Override
    public void firePitTick() {
        firingTicks++;
        if(firingTicks >= MAX_FIRING_TICKS){
            if(level == null) return;
            var input = new SingleRecipeInput(new ItemStack(getBlockState().getBlock()));
            var recipe = RecipeUtil.getValidRecipe(level, MILFRecipeTypes.PIT_FIRING, input);

            if(recipe.isPresent() ){
                ItemStack output = recipe.get().value().assemble(input, level.registryAccess());
                if(output.getItem() instanceof BlockItem blockItem){
                    handleRecipeOutput(blockItem);
                    //level.setBlock(worldPosition, blockItem.getBlock().withPropertiesOf(getBlockState()), Block.UPDATE_ALL);
                }

            }
            firingTicks = 0;
        }

        if(firingTicks % 10 == 0){
            setChanged();
        }
    }

    @Override
    public float getCurrentProgress() {
        return (float) firingTicks / MAX_FIRING_TICKS;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("firingTicks", firingTicks);

    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains("firingTicks")){
            firingTicks = tag.getInt("firingTicks");
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
