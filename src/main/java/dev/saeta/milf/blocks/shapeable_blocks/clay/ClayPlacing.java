package dev.saeta.milf.blocks.shapeable_blocks.clay;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.recipes.MultiRecipeInput;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItemTags;
import dev.saeta.milf.registries.MILFRecipeTypes;
import dev.saeta.milf.util.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

@EventBusSubscriber(modid = MILostFavor.MOD_ID)
public class ClayPlacing {

    @SubscribeEvent
    public static void onClayRightClick(PlayerInteractEvent.RightClickBlock event) {

        ItemStack itemStack = event.getItemStack();

        if (itemStack.is(MILFItemTags.PLACEABLE_CLAY_BALLS)) {
            Level level = event.getLevel();

            if (!level.isClientSide()) {

                BlockPos pos = event.getPos();
                Direction face = event.getFace();

                if(face == null) return;

                BlockPos targetPos = pos.relative(face);

                if(!level.getBlockState(targetPos).canBeReplaced()) return;

                BlockState blockState = level.getBlockState(pos);

                var is = itemStack.copyWithCount(1);

                var recipeInput = new MultiRecipeInput(List.of(is,is,is,is));

                var recipe = RecipeUtil.getValidRecipe(level, MILFRecipeTypes.WORLD_PLACEMENT,recipeInput);

                if(recipe.isPresent()){

                    ItemStack outputStack = recipe.get().value().assemble(recipeInput, level.registryAccess());

                    if(!(outputStack.getItem() instanceof BlockItem outputBlockItem)) return;

                    if(blockState.isCollisionShapeFullBlock(level, pos)) {
                        level.setBlock(targetPos, MILFBlocks.MOLDED_BLOCK.get().defaultBlockState().setValue(MoldedBlock.TOP_LAYERS_CHIPPED, 12), Block.UPDATE_ALL);
                        if(level.getBlockEntity(targetPos) instanceof MoldedBlockEntity moldedBlockEntity) moldedBlockEntity.setCurrentInputBlockItem(outputStack);
                        level.playSound(null, pos, SoundEvents.MUD_STEP, SoundSource.BLOCKS, 1,1);
                    } else if (blockState.is(MILFBlocks.MOLDED_BLOCK)
                            && blockState.getValue(MoldedBlock.TOP_LAYERS_CHIPPED) >= 4
                            && level.getBlockEntity(pos) instanceof MoldedBlockEntity moldedBlockEntity
                            && moldedBlockEntity.getCurrentInputBlockState().is(outputBlockItem.getBlock())
                    ){
                        int topLayersChipped = blockState.getValue(MoldedBlock.TOP_LAYERS_CHIPPED);
                        if(topLayersChipped ==4 && blockState.getValue(MoldedBlock.SIDE_LAYERS_CHIPPED) == 0){
                            level.setBlock(pos, outputBlockItem.getBlock().defaultBlockState(), Block.UPDATE_ALL);
                        } else {
                            level.setBlock(pos, blockState.setValue(MoldedBlock.TOP_LAYERS_CHIPPED,  topLayersChipped - 4), Block.UPDATE_ALL);
                            moldedBlockEntity.updateOutput();
                        }
                        level.playSound(null, pos, SoundEvents.MUD_STEP, SoundSource.BLOCKS, 1,1);

                    } else {
                        return;
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
