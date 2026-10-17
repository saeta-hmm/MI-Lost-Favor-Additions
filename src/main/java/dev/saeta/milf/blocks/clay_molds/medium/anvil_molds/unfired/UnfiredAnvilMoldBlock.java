package dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.unfired;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.BaseDirectionalEntityBlock;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.AbstractAnvilMoldBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class UnfiredAnvilMoldBlock extends AbstractAnvilMoldBlock {

    public static final MapCodec<UnfiredAnvilMoldBlock> CODEC = simpleCodec(UnfiredAnvilMoldBlock::new);

    public UnfiredAnvilMoldBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseDirectionalEntityBlock> codec() {
        return CODEC;
    }



    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UnfiredAnvilMoldBlockEntity(pos, state);
    }
}
