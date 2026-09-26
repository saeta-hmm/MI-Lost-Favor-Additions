package dev.saeta.milf.blocks.pot_bellows;

import dev.saeta.milf.blocks.kiln.KilnBlockEntity;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PotBellowsBlockEntity extends BlockEntity {

    private final static int MAX_ACTIVE_TICKS = 40;

    private int activeTicks = 0;
    private int prevActiveTicks = 0;

    public PotBellowsBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.POT_BELLOWS.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PotBellowsBlockEntity potBellowsBlockEntity){
        potBellowsBlockEntity.tick();
    }

    public float getProgress(){
        return (float) activeTicks / MAX_ACTIVE_TICKS;
    }

    public float getInterpolatedProgress(float partialTick){
        float prev = (float) prevActiveTicks / MAX_ACTIVE_TICKS;
        float curr = (float) activeTicks / MAX_ACTIVE_TICKS;
        return Mth.lerp(partialTick, prev, curr);
    }

    public float getLeatherOffset(float partialTick){
        float progress = 1 - getInterpolatedProgress(partialTick);

        final float MAX_OFFSET = 0.25f;
        final float PEAK_PROGRESS = 0.1f;

        if(progress < PEAK_PROGRESS){
            return MAX_OFFSET * (float) Math.sin((Math.PI / 2) * (progress / PEAK_PROGRESS));
        }

        return MAX_OFFSET * (float) Math.cos((Math.PI / 2) * ((progress - PEAK_PROGRESS) / (1f - PEAK_PROGRESS)));

    }

    public boolean canActivate(){
        return activeTicks == 0;
    }

    public void activate(){
        activeTicks = MAX_ACTIVE_TICKS;
        prevActiveTicks = MAX_ACTIVE_TICKS;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            level.playSound(null, worldPosition, SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.BLOCKS, 0.35f, 0.2f);


        }
    }

    private void tick(){
        if(level == null) return;
        if(activeTicks <= 0) {
            prevActiveTicks = activeTicks;
            return;
        }

        if(level.getBlockEntity(worldPosition.relative(getBlockState().getValue(PotBellowsBlock.FACING).getOpposite())) instanceof KilnBlockEntity kilnBlockEntity){
            kilnBlockEntity.passBellowsTick();

            if(activeTicks % 10 == 0){
                kilnBlockEntity.spawnBellowsParticles();
            }
        }



        prevActiveTicks = activeTicks;
        activeTicks--;

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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("activeTicks", activeTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        activeTicks = tag.getInt("activeTicks");
        prevActiveTicks = activeTicks;
    }
}
