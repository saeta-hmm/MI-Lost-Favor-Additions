package dev.saeta.milf.blocks.bloomery.fired;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.ItemHandlerBlock;
import dev.saeta.milf.blocks.bloomery.AbstractBloomeryBaseBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

public class BloomeryBaseBlock extends AbstractBloomeryBaseBlock implements CapabilityProvider {

    private final static VoxelShape DIRT = Block.box(0,0,0,16,11,16);
    private final static VoxelShape BASE = Block.box(0,11,0,16,16,16);
    private final static VoxelShape BASE_CENTER = Block.box(5,11,5,11,15,11);

    private final static VoxelShape PIT = Block.box(2,12,2,14,16,14);

    protected final static VoxelShape SHAPE = Shapes.or(
            Shapes.join(
                    Shapes.or(
                            DIRT,
                            BASE
                    ),
                    PIT,
                    BooleanOp.ONLY_FIRST
            ),
            BASE_CENTER
    );


    protected final static VoxelShape FIRE_PIT_PART_SHAPE = Shapes.or(
            Shapes.join(
                    BASE,
                    PIT,
                    BooleanOp.ONLY_FIRST
            ),
            BASE_CENTER
    ).move(0, -10/16f,0);

    public static final MapCodec<BloomeryBaseBlock> CODEC = simpleCodec(BloomeryBaseBlock::new);

    public BloomeryBaseBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.LIT, false).setValue(FIRE_PIT_PART, false));

    }

    private static void serverTick(Level level, BlockPos pos, BlockState state, BloomeryBaseBlockEntity bloomeryBaseBlockEntity) {

        BloomeryBaseBlockEntity.serverTick(level, pos, state, bloomeryBaseBlockEntity);

        if (level.getGameTime() % 20 != 0) return;

        if(bloomeryBaseBlockEntity.isLit() && level instanceof ServerLevel serverLevel){

            serverLevel.sendParticles(
                    ParticleTypes.SMALL_FLAME,
                    pos.getX() + 0.5 + 0.25 * (level.random.nextBoolean() ? 1 : -1),
                    pos.getY() + 0.92,
                    pos.getZ() + 0.5 + 0.25 * (level.random.nextBoolean() ? 1 : -1),
                    2,
                    0, 0.08, 0,
                    0.003
            );

        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, MILFBlockEntities.BLOOMERY_BASE.get(), BloomeryBaseBlock::serverTick);

    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FIRE_PIT_PART) ? FIRE_PIT_PART_SHAPE : SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.LIT);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BloomeryBaseBlockEntity(pos, state);
    }

    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.BLOOMERY_BASE.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );

    }
}
