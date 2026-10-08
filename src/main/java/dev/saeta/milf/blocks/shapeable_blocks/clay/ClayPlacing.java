package dev.saeta.milf.blocks.shapeable_blocks.clay;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = MILostFavor.MOD_ID)
public class ClayPlacing {

    @SubscribeEvent
    public static void onClayRightClick(PlayerInteractEvent.RightClickBlock event) {

        ItemStack itemStack = event.getItemStack();

        if (itemStack.is(Items.CLAY_BALL)) {
            Level level = event.getLevel();

            if (!level.isClientSide()) {

                BlockPos pos = event.getPos();
                Direction face = event.getFace();

                if(face == null) return;

                BlockPos targetPos = pos.relative(face);

                if(!level.getBlockState(targetPos).canBeReplaced()) return;

                BlockState blockState = level.getBlockState(pos);

                if(blockState.isCollisionShapeFullBlock(level, pos)) {
                    level.setBlock(targetPos, MILFBlocks.MOLDED_BLOCK.get().defaultBlockState().setValue(MoldedBlock.TOP_LAYERS_CHIPPED, 12), Block.UPDATE_ALL);
                    level.playSound(null, pos, SoundEvents.MUD_STEP, SoundSource.BLOCKS, 1,1);
                } else if (blockState.is(MILFBlocks.MOLDED_BLOCK) && blockState.getValue(MoldedBlock.TOP_LAYERS_CHIPPED) >= 4){

                    if(level.getBlockEntity(pos) instanceof MoldedBlockEntity moldedBlockEntity && moldedBlockEntity.getCurrentInputBlockState().is(Blocks.CLAY)){
                        level.setBlock(pos, blockState.setValue(MoldedBlock.TOP_LAYERS_CHIPPED, blockState.getValue(MoldedBlock.TOP_LAYERS_CHIPPED) - 4), Block.UPDATE_ALL);
                        level.playSound(null, pos, SoundEvents.MUD_STEP, SoundSource.BLOCKS, 1,1);
                        moldedBlockEntity.updateOutput();
                    }


                } else {
                    return;
                }


                itemStack.shrink(1);

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}
