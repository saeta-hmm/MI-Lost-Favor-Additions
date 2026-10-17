package dev.saeta.milf.blocks.basin;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.ItemHandlerBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

public class StoneBasinBlock extends ItemHandlerBlock implements CapabilityProvider {
    public static final MapCodec<StoneBasinBlock> CODEC = simpleCodec(StoneBasinBlock::new);

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(3, 2, 3, 13, 5, 13),
            Block.box(4, 0, 4, 12, 1, 12),
            Block.box(3, 1, 4, 13, 2, 12),
            Block.box(4, 1, 3, 12, 2, 13)

    );


    public StoneBasinBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {

        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StoneBasinBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof StoneBasinBlockEntity stoneBasinBlockEntity)) return ItemInteractionResult.FAIL;

        //stoneBasinBlockEntity.stir();

        if(!level.isClientSide) {

            if (FluidUtil.interactWithFluidHandler(player, hand, stoneBasinBlockEntity.getFluidTank())) {

                return ItemInteractionResult.SUCCESS;
            }

        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return createTickerHelper(blockEntityType, MILFBlockEntities.STONE_BASIN.get(), StoneBasinBlockEntity::clientTick);
        return createTickerHelper(blockEntityType, MILFBlockEntities.STONE_BASIN.get(), StoneBasinBlockEntity::serverTick);
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.STONE_BASIN.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                MILFBlockEntities.STONE_BASIN.get(),
                ( blockEntity,  direction) -> blockEntity.getFluidTank()
        );
    }
}
