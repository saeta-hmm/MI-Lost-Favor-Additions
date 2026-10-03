package dev.saeta.milf.blocks.kiln;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.BaseDirectionalEntityBlock;
import dev.saeta.milf.blocks.roasting_contraption.RoastingContraptionBlockEntity;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class KilnBlock extends BaseDirectionalEntityBlock implements Equipable, CapabilityProvider {

    public static final MapCodec<KilnBlock> CODEC = simpleCodec(KilnBlock::new);

    public static final BooleanProperty CONTAINS_BLOCK = BooleanProperty.create("contains_block");

    private static final VoxelShape LAYER_1 = Shapes.or(
            Block.box(1,0,0,15,3,16),
            Block.box(0,0,1,16,3,15)
    );
    private static final VoxelShape LAYER_2 = Block.box(1,3,1,15,7,15);
    private static final VoxelShape LAYER_3 = Block.box(2,7,2,14,16,14);

    private static final VoxelShape INSIDES = Shapes.or(
            Block.box(3,0,3,13,16,13),
            Block.box(1,0,1,15,3,15),
            Block.box(2,3,2,14,7,14)
    );

    private static final HashMap<Direction, VoxelShape> SHAPES = new HashMap<>();

    static {
        SHAPES.put(Direction.NORTH, Shapes.join(
                Shapes.or(
                        LAYER_1,
                        LAYER_2,
                        LAYER_3,
                        Block.box(1,7,3,15,11,13)
                ),
                Shapes.or(
                        INSIDES,
                        Block.box(4,0,0,12,3,8),
                        Block.box(5,3,0,11,6,8),
                        Block.box(2,7,3,14,11,13)
                ),
                BooleanOp.ONLY_FIRST
        ));

        SHAPES.put(Direction.EAST, Shapes.join(
                Shapes.or(
                        LAYER_1,
                        LAYER_2,
                        LAYER_3,
                        Block.box(3,7,1,13,11,15)
                ),
                Shapes.or(
                        INSIDES,
                        Block.box(8,0,4,16,3,12),
                        Block.box(8,3,5,16,6,11),
                        Block.box(3,7,2,13,11,14)
                ),
                BooleanOp.ONLY_FIRST
        ));

        SHAPES.put(Direction.SOUTH, Shapes.join(
                Shapes.or(
                        LAYER_1,
                        LAYER_2,
                        LAYER_3,
                        Block.box(1,7,3,15,11,13)
                ),
                Shapes.or(
                        INSIDES,
                        Block.box(4,0,8,12,3,16),
                        Block.box(5,3,8,11,6,16),
                        Block.box(2,7,3,14,11,13)
                ),
                BooleanOp.ONLY_FIRST
        ));

        SHAPES.put(Direction.WEST, Shapes.join(
                Shapes.or(
                        LAYER_1,
                        LAYER_2,
                        LAYER_3,
                        Block.box(3,7,1,13,11,15)
                ),
                Shapes.or(
                        INSIDES,
                        Block.box(0,0,4,8,3,12),
                        Block.box(0,3,5,8,6,11),
                        Block.box(3,7,2,13,11,14)
                ),
                BooleanOp.ONLY_FIRST
        ));
    }

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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if(!(blockEntity instanceof KilnBlockEntity kilnBlockEntity)) return ItemInteractionResult.FAIL;

        ItemStackHandler itemStackHandler = kilnBlockEntity.getItemHandler();

        if(player.isShiftKeyDown() && stack.isEmpty() ){
            if(!level.isClientSide){
                for (int i = itemStackHandler.getSlots() - 1; i >= 0; i--) {
                    ItemStack outputStack = itemStackHandler.extractItem(i,1, false);
                    if(!outputStack.isEmpty()){
                        Block.popResourceFromFace(level, pos, Direction.UP, outputStack);
                        return ItemInteractionResult.SUCCESS;
                    }
                }
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);

        } else if (!stack.isEmpty() && !level.getBlockState(pos.above()).is(MILFBlocks.CLAY_CRUCIBLE))  {

            ItemStack remainingStack = kilnBlockEntity.insertAnywhere(stack.copyWithCount(1), true);

            if (remainingStack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {

                if(!level.isClientSide) {

                    kilnBlockEntity.insertAnywhere(stack.copyWithCount(1), false);
                    stack.shrink(1);
                    player.setItemInHand(hand, stack);

                    SoundType soundType = blockItem.getBlock().defaultBlockState().getSoundType(level, pos, player);

                    level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS, 1, 1);

                }

                return ItemInteractionResult.SUCCESS;
            }

        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

        if(!state.is(newState.getBlock())){
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if(blockEntity instanceof KilnBlockEntity kilnBlockEntity){
                ItemStackHandler itemHandler = kilnBlockEntity.getItemHandler();

                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    ItemStack stack = itemHandler.getStackInSlot(i);
                    if(!stack.isEmpty()){
                        Block.popResource(level, pos, stack);
                    }
                }


            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(CONTAINS_BLOCK, false);
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
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.KILN.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );
    }
}
