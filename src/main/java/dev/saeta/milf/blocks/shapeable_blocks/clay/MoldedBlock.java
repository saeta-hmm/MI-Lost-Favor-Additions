package dev.saeta.milf.blocks.shapeable_blocks.clay;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.shapeable_blocks.AbstractShapeableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MoldedBlock extends AbstractShapeableBlock {

    public static final MapCodec<MoldedBlock> CODEC = simpleCodec(MoldedBlock::new);


    public MoldedBlock(Properties properties) {
        super(properties);
    }

    @Override
    public String getNameKey() {
        return "block.milf.molded";
    }

    @Override
    protected MapCodec<? extends MoldedBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MoldedBlockEntity(pos, state);
    }


}
