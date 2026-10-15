package dev.saeta.milf.blocks.clay_molds.medium.mold_base.fired;

import dev.saeta.milf.blocks.clay_molds.medium.mold_base.AbstractMoldBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AbstractMultislotMoldBlock extends AbstractFiredMoldBaseBlock {

    protected final int slots = getSlots();

    public AbstractMultislotMoldBlock(Properties properties) {
        super(properties);
    }

    protected abstract int getSlots();

}
