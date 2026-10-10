package dev.saeta.milf.blocks.clay_plates.fire_pit;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.ItemHandlerBlock;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import dev.saeta.milf.blocks.shapeable_blocks.AbstractShapeableBlockEntity;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class FirePitPlateBlock extends ItemHandlerBlock implements CapabilityProvider {

    public static final BooleanProperty NE_PLATE = BooleanProperty.create("ne_plate");
    public static final BooleanProperty NW_PLATE = BooleanProperty.create("nw_plate");
    public static final BooleanProperty SE_PLATE = BooleanProperty.create("se_plate");
    public static final BooleanProperty SW_PLATE = BooleanProperty.create("sw_plate");

    private final static int PLATE_OFFSET = 6;

    private static final VoxelShape NE_PLATE_SHAPE = Shapes.or(
            Block.box(4-PLATE_OFFSET,1,4-PLATE_OFFSET,12-PLATE_OFFSET,2,12-PLATE_OFFSET),
            Block.box(3-PLATE_OFFSET,2,3-PLATE_OFFSET,13-PLATE_OFFSET,3,13-PLATE_OFFSET)
    );

    private static final VoxelShape NW_PLATE_SHAPE = Shapes.or(
            Block.box(4+PLATE_OFFSET,1,4-PLATE_OFFSET,12+PLATE_OFFSET,2,12-PLATE_OFFSET),
            Block.box(3+PLATE_OFFSET,2,3-PLATE_OFFSET,13+PLATE_OFFSET,3,13-PLATE_OFFSET)
    );

    private static final VoxelShape SE_PLATE_SHAPE = Shapes.or(
            Block.box(4-PLATE_OFFSET,1,4+PLATE_OFFSET,12-PLATE_OFFSET,2,12+PLATE_OFFSET),
            Block.box(3-PLATE_OFFSET,2,3+PLATE_OFFSET,13-PLATE_OFFSET,3,13+PLATE_OFFSET)
    );

    private static final VoxelShape SW_PLATE_SHAPE = Shapes.or(
            Block.box(4+PLATE_OFFSET,1,4+PLATE_OFFSET,12+PLATE_OFFSET,2,12+PLATE_OFFSET),
            Block.box(3+PLATE_OFFSET,2,3+PLATE_OFFSET,13+PLATE_OFFSET,3,13+PLATE_OFFSET)
    );

    public static final MapCodec<FirePitPlateBlock> CODEC = simpleCodec(FirePitPlateBlock::new);

    public FirePitPlateBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(NE_PLATE, true)
                .setValue(NW_PLATE, false)
                .setValue(SE_PLATE, false)
                .setValue(SW_PLATE, false)
        );
    }

    @Override
    protected SoundEvent getInsertSound(boolean isFailed,  UseItemOnContext context) {
        return context.stack().getItem() instanceof BlockItem blockItem ?
                blockItem.getBlock().defaultBlockState().getSoundType(context.level(), context.pos().above(), null).getPlaceSound() :
                super.getInsertSound(isFailed, context);

    }

    @Override
    protected SoundEvent getExtractSound(UseItemOnContext context) {
        return context.stack().getItem() instanceof BlockItem blockItem ?
                blockItem.getBlock().defaultBlockState().getSoundType(context.level(), context.pos().above(), null).getBreakSound() :
                super.getExtractSound(context);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {

        if(level.getBlockEntity(pos) instanceof FirePitPlateBlockEntity firePitPlateBlockEntity){

            return firePitPlateBlockEntity.getCombinedShape();

        }


        VoxelShape baseShape = NE_PLATE_SHAPE;

        if(state.getValue(NW_PLATE)){
            baseShape = Shapes.or(baseShape, NW_PLATE_SHAPE);
        }

        if(state.getValue(SE_PLATE)){
            baseShape = Shapes.or(baseShape, SE_PLATE_SHAPE);
        }

        if(state.getValue(SW_PLATE)){
            baseShape = Shapes.or(baseShape, SW_PLATE_SHAPE);
        }

        return baseShape;
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {

        if(level.getBlockEntity(pos) instanceof FirePitPlateBlockEntity firePitPlateBlockEntity){
            float f = firePitPlateBlockEntity.getCombinedHardness((Level) level, pos);
            if (f == -1.0F) {
                return 0.0F;
            } else {
                int i = net.neoforged.neoforge.event.EventHooks.doPlayerHarvestCheck(player, state, level, pos) ? 30 : 100;
                return player.getDigSpeed(state, pos) / f / (float)i;
            }
        }

        return super.getDestroyProgress(state, player, level, pos);


    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {

        if(level.getBlockEntity(pos) instanceof FirePitPlateBlockEntity firePitPlateBlockEntity){
            return super.getSoundType(firePitPlateBlockEntity.getFirstItemState(), level, pos, entity);
        }

        return super.getSoundType(state, level, pos, entity);
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {

        if(level.getBlockEntity(pos) instanceof FirePitPlateBlockEntity firePitPlateBlockEntity){

            super.spawnDestroyParticles(level, player, pos, firePitPlateBlockEntity.getFirstItemState());
            return;

        }

        super.spawnDestroyParticles(level, player, pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NE_PLATE).add(NW_PLATE).add(SE_PLATE).add(SW_PLATE);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {

        ItemStack defaultStack = new ItemStack(MILFBlocks.UNFIRED_CLAY_PLATE);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof FirePitPlateBlockEntity firePitPlateBlockEntity){

            ItemStackHandler handler = firePitPlateBlockEntity.getItemHandler();

            ItemStack stack = handler.getStackInSlot(0).copy();

            stack.set(DataComponents.ITEM_NAME, firePitPlateBlockEntity.getName());

            if(!stack.isEmpty()) return stack;

        }

        return defaultStack;
    }


    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.FIRE_PIT_PLATE.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FirePitPlateBlockEntity(pos, state);
    }
}
