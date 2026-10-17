package dev.saeta.milf.blocks.clay_molds.small.fired;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.clay_molds.AbstractClayMoldBlockItem;
import dev.saeta.milf.blocks.clay_molds.CastableMoldBlock;
import dev.saeta.milf.blocks.clay_molds.small.AbstractClayPlateBlock;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.networking.data.StoredItem;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class ClayMoldBlock extends AbstractClayPlateBlock implements CastableMoldBlock {

    public static final MapCodec<ClayPlateBlock> CODEC = simpleCodec(ClayPlateBlock::new);

    public ClayMoldBlock(BlockBehaviour.Properties properties) {
        super(properties.strength(0.6f,2).sound(SoundType.DECORATED_POT));
    }


    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                MILFBlockEntities.CLAY_MOLD.get(),
                ( blockEntity,  direction) -> blockEntity.getFluidTank()
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ClayMoldBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return createTickerHelper(blockEntityType, MILFBlockEntities.CLAY_MOLD.get(), ClayMoldBlockEntity::clientTick);
        return createTickerHelper(blockEntityType, MILFBlockEntities.CLAY_MOLD.get(), ClayMoldBlockEntity::serverTick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(interactWithFluidHandler(stack, state, level, pos, player, hand, hitResult)) return ItemInteractionResult.SUCCESS;
        if(tryExtract(stack, state, level, pos, player, hand, hitResult)) return ItemInteractionResult.SUCCESS;
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack mold = new ItemStack(asBlock());

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof ClayMoldBlockEntity clayMoldBlockEntity){
            FluidTank fluidTank = clayMoldBlockEntity.getFluidTank();
            FluidStack fluidStack = fluidTank.getFluid();

            if(!fluidStack.isEmpty()){
                FluidHandlerItemStack handler = (FluidHandlerItemStack) FluidUtil.getFluidHandler(mold).orElseThrow();

                handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            }

            ItemStack castResultItem = clayMoldBlockEntity.getItemHandler().getStackInSlot(0);



            if(!castResultItem.isEmpty()){
                mold.set(MILFDataComponents.CAST_RESULT, new StoredItem(castResultItem.copy()));
            }

        }

        return mold;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if(!level.isClientSide && stack.getItem() instanceof ClayMoldItem clayMoldItem){
            int capacity = clayMoldItem.getCapacity();
            if(level.getBlockEntity(pos) instanceof ClayMoldBlockEntity clayMoldBlockEntity){
                clayMoldBlockEntity.setCapacity(capacity);

            }
        }



    }

    public static class ClayMoldItem extends AbstractClayMoldBlockItem implements CapabilityProvider {
        public ClayMoldItem(Block block, Properties properties, int capacity) {
            super(block, properties, capacity);
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
