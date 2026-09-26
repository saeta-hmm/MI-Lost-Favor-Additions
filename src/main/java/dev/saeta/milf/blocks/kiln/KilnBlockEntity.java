package dev.saeta.milf.blocks.kiln;

import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntity;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class KilnBlockEntity extends BlockEntity {
    public KilnBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.KILN.get(), pos, blockState);
    }

    public void passBellowsTick(){
        if(level == null) return;
        if(!(level.getBlockEntity(worldPosition.above()) instanceof ClayCrucibleBlockEntity clayCrucibleBlockEntity)){
            return;
        }

        if(!(level.getBlockEntity(worldPosition.below()) instanceof FirePitBlockEntity firePitBlockEntity)) return;

        if(firePitBlockEntity.isLit()) clayCrucibleBlockEntity.increaseProgress();


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
}
