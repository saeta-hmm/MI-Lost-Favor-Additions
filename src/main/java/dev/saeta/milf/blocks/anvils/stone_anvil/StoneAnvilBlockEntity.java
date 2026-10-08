package dev.saeta.milf.blocks.anvils.stone_anvil;

import dev.saeta.milf.blocks.anvils.AbstractAnvilBlockEntity;
import dev.saeta.milf.recipes.anvil.AnvilTier;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

public class StoneAnvilBlockEntity extends AbstractAnvilBlockEntity {
    public StoneAnvilBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.STONE_ANVIL.get(), pos, blockState, holder -> holder.value().tier() == AnvilTier.STONE);
    }

    @Override
    public void handleHit(float accuracy, float progress, float volume) {

        if(level == null) return;

        level.playSound(null, worldPosition, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.95f * volume,accuracy + (float) level.random.nextInt(1, 5) / 10);
        itemHandler.regenerateSeed();

        if(progress >= 1){
            itemHandler.extractAndDropResult(ITEM_SLOT);
        }

    }
}
