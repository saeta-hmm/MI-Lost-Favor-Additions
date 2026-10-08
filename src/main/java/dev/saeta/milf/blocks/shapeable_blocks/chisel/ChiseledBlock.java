package dev.saeta.milf.blocks.shapeable_blocks.chisel;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.shapeable_blocks.AbstractShapeableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
