package dev.saeta.milf.blocks.shapeable_blocks.clay;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.shapeable_blocks.AbstractShapeableBlock;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlock;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
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
