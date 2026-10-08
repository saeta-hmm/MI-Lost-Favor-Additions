package dev.saeta.milf.blocks.shapeable_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractShapeableBlock extends BaseEntityBlock {

    public static final IntegerProperty TOP_LAYERS_CHIPPED = IntegerProperty.create("top_layers_chipped", 0, 15);
    public static final IntegerProperty SIDE_LAYERS_CHIPPED = IntegerProperty.create("side_layers_chipped", 0, 7);

    protected AbstractShapeableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(
                state.getValue(SIDE_LAYERS_CHIPPED),
                0,
                state.getValue(SIDE_LAYERS_CHIPPED),
                16 - state.getValue(SIDE_LAYERS_CHIPPED),
                16 - state.getValue(TOP_LAYERS_CHIPPED),
                16 - state.getValue(SIDE_LAYERS_CHIPPED)
        );
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {

        if(level.getBlockEntity(pos) instanceof AbstractShapeableBlockEntity abstractShapeableBlockEntity){
            super.spawnDestroyParticles(level, player, pos, abstractShapeableBlockEntity.getCurrentInputBlockState());
            return;
        }

        super.spawnDestroyParticles(level, player, pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP_LAYERS_CHIPPED).add(SIDE_LAYERS_CHIPPED);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof AbstractShapeableBlockEntity abstractShapeableBlockEntity){
            ItemStack itemStack = abstractShapeableBlockEntity.getCurrentInputBlockItem().copy();

            Component name = itemStack.getHoverName();

            itemStack.set(DataComponents.ITEM_NAME, Component.translatable(getNameKey(), name));

            return itemStack;

        }

        return new ItemStack(Blocks.CLAY);
    }

    public abstract String getNameKey();

}
