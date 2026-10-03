package dev.saeta.milf.blocks;

import dev.saeta.milf.blocks.fire_pit.FirePitBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ReplacerBlockItem extends BlockItem {
    public ReplacerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult interactionresult = place(new ReplacerBlockPlaceContext(context));
        if (!interactionresult.consumesAction() && context.getItemInHand().has(DataComponents.FOOD)) {
            InteractionResult interactionresult1 = super.use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
            return interactionresult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : interactionresult1;
        } else {
            return interactionresult;
        }
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState targetState = level.getBlockState(pos);

        return canReplace(targetState);

    }

    protected abstract boolean canReplace(BlockState state);


    private class ReplacerBlockPlaceContext extends BlockPlaceContext{

        public ReplacerBlockPlaceContext(UseOnContext context) {
            super(context);
            BlockState state = getLevel().getBlockState(getHitResult().getBlockPos());
            replaceClicked = canReplace(state) || state.canBeReplaced(this);
        }

    }
}
