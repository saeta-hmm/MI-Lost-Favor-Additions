package dev.saeta.milf.blocks.kiln.fired;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.kiln.AbstractKilnBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class KilnBlock extends AbstractKilnBlock implements Equipable, CapabilityProvider {

    public static final MapCodec<KilnBlock> CODEC = simpleCodec(KilnBlock::new);

    public static final BooleanProperty CONTAINS_BLOCK = BooleanProperty.create("contains_block");

    public KilnBlock(Properties properties) {
        super(properties);

    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if(state.getValue(CONTAINS_BLOCK)) return Shapes.or(
                SHAPES.get(state.getValue(BlockStateProperties.HORIZONTAL_FACING)),
                Block.box(3, 10, 3, 13, 20, 13)
        );
        return SHAPES.get(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    @Override
    public SoundEvent getInsertSound(boolean isFailed, UseItemOnContext context) {
        ItemStack stack = context.stack();

        if(stack.getItem() instanceof BlockItem blockItem){

            return isFailed ?
                    blockItem.getBlock().defaultBlockState().getSoundType(context.level(), context.pos(), null).getPlaceSound() :
                    blockItem.getBlock().defaultBlockState().getSoundType(context.level(), context.pos(), null).getHitSound();
        }

        return super.getInsertSound(isFailed, context);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, MILFBlockEntities.KILN.get(), KilnBlock::serverTick);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, KilnBlockEntity kilnBlockEntity) {
        KilnBlockEntity.serverTick(level, pos, state, kilnBlockEntity);
    }

    @Override
    protected MapCodec<? extends KilnBlock> codec() {
        return CODEC;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONTAINS_BLOCK);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KilnBlockEntity(pos,state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(CONTAINS_BLOCK, false);
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.KILN.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );
    }
}
