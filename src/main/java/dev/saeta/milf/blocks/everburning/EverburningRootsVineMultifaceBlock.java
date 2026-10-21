package dev.saeta.milf.blocks.everburning;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;

public class EverburningRootsVineMultifaceBlock extends MultifaceBlock {

    public static final MapCodec<EverburningRootsVineMultifaceBlock> CODEC = simpleCodec(EverburningRootsVineMultifaceBlock::new);

    public EverburningRootsVineMultifaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends MultifaceBlock> codec() {
        return CODEC;
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return new MultifaceSpreader(this);
    }
}
