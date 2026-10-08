package dev.saeta.milf.blocks.anvils.bronze_anvil;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.BaseDirectionalEntityBlock;
import dev.saeta.milf.blocks.anvils.AbstractAnvilBlock;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class BronzeAnvilBlock extends AbstractAnvilBlock {

    public static final MapCodec<BronzeAnvilBlock> CODEC = simpleCodec(BronzeAnvilBlock::new);

    public BronzeAnvilBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseDirectionalEntityBlock> codec() {
        return CODEC;
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BronzeAnvilBlockEntity(pos, state);
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.BRONZE_ANVIL.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );
    }
}
