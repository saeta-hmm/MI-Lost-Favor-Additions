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
