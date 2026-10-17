package dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.fired;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.BaseDirectionalEntityBlock;
import dev.saeta.milf.blocks.clay_molds.AbstractClayMoldBlockItem;
import dev.saeta.milf.blocks.clay_molds.CastableMoldBlock;
import dev.saeta.milf.blocks.clay_molds.medium.anvil_molds.AbstractAnvilMoldBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;

public class AnvilMoldBlock extends AbstractAnvilMoldBlock implements CapabilityProvider, CastableMoldBlock {

    public static final MapCodec<AnvilMoldBlock> CODEC = simpleCodec(AnvilMoldBlock::new);

    public AnvilMoldBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return createTickerHelper(blockEntityType, MILFBlockEntities.CLAY_MOLD_ANVIL_FULL.get(), AnvilMoldBlockEntity::clientTick);
        return createTickerHelper(blockEntityType, MILFBlockEntities.CLAY_MOLD_ANVIL_FULL.get(), AnvilMoldBlockEntity::serverTick);
    }

    @Override
    protected MapCodec<? extends BaseDirectionalEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnvilMoldBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(interactWithFluidHandler(stack, state, level, pos, player, hand, hitResult)) return ItemInteractionResult.SUCCESS;
        if(tryExtract(stack, state, level, pos, player, hand, hitResult)) return ItemInteractionResult.SUCCESS;
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                MILFBlockEntities.CLAY_MOLD_ANVIL_FULL.get(),
                ( blockEntity,  direction) -> blockEntity.getFluidTank()
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.CLAY_MOLD_ANVIL_FULL.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );
    }

    public static class AnvilMoldBlockItem extends AbstractClayMoldBlockItem{

        public AnvilMoldBlockItem(Block block, Properties properties) {
            super(block, properties, 4000);
        }

        @Override
        public void registerCapabilities(RegisterCapabilitiesEvent event) {
            event.registerItem(
                    Capabilities.FluidHandler.ITEM,
                    (stack, something) -> new FluidHandlerItemStack(MILFDataComponents.FLUID, stack, capacity),
                    this
            );

        }
    }
}
