package dev.saeta.milf.blocks.clay_plates.fired;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.clay_plates.AbstractClayPlateBlock;
import dev.saeta.milf.blocks.clay_plates.AbstractClayPlateBlockItem;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class ClayPlateBlock extends AbstractClayPlateBlock {

    public static final MapCodec<ClayPlateBlock> CODEC = simpleCodec(ClayPlateBlock::new);

    public ClayPlateBlock(Properties properties) {
        super(properties.strength(0.6f,2).sound(SoundType.DECORATED_POT));
    }


    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.CLAY_MOLD.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );

    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ClayPlateBlockEntity(pos, state);
    }

    public static class ClayPlateItem extends AbstractClayPlateBlockItem{

        public ClayPlateItem(Block block, Properties properties) {
            super(block, properties.stacksTo(1));
        }
    }

}
