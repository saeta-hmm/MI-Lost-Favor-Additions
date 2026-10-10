package dev.saeta.milf.blocks.clay_plates.fired;

import com.mojang.serialization.MapCodec;
import dev.saeta.milf.blocks.clay_plates.AbstractClayPlateBlock;
import dev.saeta.milf.blocks.clay_plates.AbstractClayPlateBlockItem;
import dev.saeta.milf.capabilities.CapabilityProvider;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ClayPlateBlock extends AbstractClayPlateBlock {

    public static final MapCodec<ClayPlateBlock> CODEC = simpleCodec(ClayPlateBlock::new);

    public ClayPlateBlock(Properties properties) {
        super(properties.strength(0.6f,2).sound(SoundType.DECORATED_POT));
    }


    @Override
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                MILFBlockEntities.CLAY_PLATE.get(),
                ( blockEntity,  direction) -> blockEntity.getItemHandler()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                MILFBlockEntities.CLAY_PLATE.get(),
                ( blockEntity,  direction) -> blockEntity.getFluidTank()
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

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide) return ItemInteractionResult.SUCCESS;

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if(!(blockEntity instanceof ClayPlateBlockEntity clayPlateBlockEntity)) return ItemInteractionResult.FAIL;

        if(FluidUtil.interactWithFluidHandler(player, hand,clayPlateBlockEntity.getFluidTank())){
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack mold = new ItemStack(asBlock());

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof ClayPlateBlockEntity clayPlateBlockEntity){
            FluidTank fluidTank = clayPlateBlockEntity.getFluidTank();
            FluidStack fluidStack = fluidTank.getFluid();

            if(!fluidStack.isEmpty()){
                FluidHandlerItemStack handler = (FluidHandlerItemStack) FluidUtil.getFluidHandler(mold).orElseThrow();

                handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            }

        }

        return mold;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if(!level.isClientSide && stack.getItem() instanceof ClayMoldItem clayMoldItem){
            int capacity = clayMoldItem.getCapacity();
            if(level.getBlockEntity(pos) instanceof ClayPlateBlockEntity clayPlateBlockEntity){
                clayPlateBlockEntity.setCapacity(capacity);
//                FluidTank fluidTank = clayPlateBlockEntity.getFluidTank();
//                fluidTank.setCapacity(capacity);
            }
        }



    }

    public static class ClayMoldItem extends AbstractClayPlateBlockItem implements CapabilityProvider{
        private final int capacity;
        public ClayMoldItem(Block block, Properties properties, int capacity) {
            super(block, properties.stacksTo(1).component(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY));
            this.capacity = capacity;
        }

        public int getCapacity() {
            return capacity;
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

            Optional<FluidStack> fluidStack = FluidUtil.getFluidContained(stack);

            fluidStack.ifPresent(fs -> {
                tooltipComponents.add(Component.translatable("milf.tooltip.fluid", fs.getHoverName(), fs.getAmount(), capacity));
            });

            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
