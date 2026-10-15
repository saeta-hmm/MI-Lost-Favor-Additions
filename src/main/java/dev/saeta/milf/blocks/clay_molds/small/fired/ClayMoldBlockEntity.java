package dev.saeta.milf.blocks.clay_molds.small.fired;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.ProgressProvider;
import dev.saeta.milf.blocks.clay_molds.AbstractCastableMoldBlockEntity;
import dev.saeta.milf.blocks.clay_molds.AbstractClayMoldBlockEntity;
import dev.saeta.milf.networking.data.StoredItem;
import dev.saeta.milf.recipes.casting.CastingRecipeInput;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class ClayMoldBlockEntity extends AbstractCastableMoldBlockEntity {

    public ClayMoldBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.CLAY_MOLD.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ClayMoldBlockEntity clayMoldBlockEntity){
        clayMoldBlockEntity.serverTick();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ClayMoldBlockEntity clayMoldBlockEntity){
        clayMoldBlockEntity.clientTick();
    }

    @Override
    protected int getInitialCapacity() {
        return 1000;
    }
}
