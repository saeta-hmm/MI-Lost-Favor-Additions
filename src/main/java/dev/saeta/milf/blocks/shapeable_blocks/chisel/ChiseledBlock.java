package dev.saeta.milf.blocks.shapeable_blocks.chisel;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.shapeable_blocks.AbstractShapeableBlock;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ChiseledBlock extends AbstractShapeableBlock {

    public static final MapCodec<ChiseledBlock> CODEC = simpleCodec(ChiseledBlock::new);

    public ChiseledBlock(Properties properties) {
        super(properties);
    }

    @Override
    public String getNameKey() {
        return "block.milf.chiseled";
    }

    @Override
    protected MapCodec<? extends ChiseledBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChiseledBlockEntity(pos, state);
    }
}
