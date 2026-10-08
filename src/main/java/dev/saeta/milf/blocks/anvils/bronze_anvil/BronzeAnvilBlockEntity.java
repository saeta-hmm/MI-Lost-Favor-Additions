package dev.saeta.milf.blocks.anvils.bronze_anvil;

import dev.saeta.milf.blocks.anvils.AbstractAnvilBlockEntity;
import dev.saeta.milf.recipes.anvil.AnvilTier;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

public class BronzeAnvilBlockEntity extends AbstractAnvilBlockEntity {

    public BronzeAnvilBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.BRONZE_ANVIL.get(), pos, blockState, holder -> {
            var tier = holder.value().tier();

            return tier == AnvilTier.STONE || tier == AnvilTier.BRONZE;
        });
    }


    public void handleHit(float accuracy, float progress, float volume){

        if(level == null) return;

        level.playSound(null, worldPosition, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.BLOCKS, 0.65f * volume,accuracy + (float) level.random.nextInt(1, 5) / 10);
        itemHandler.regenerateSeed();

        if(progress >= 1){
            itemHandler.extractAndDropResult(ITEM_SLOT);
        }

    }

}
