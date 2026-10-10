package dev.saeta.milf.blocks.clay_plates;

import dev.saeta.milf.blocks.clay_plates.fire_pit.FirePitPlateBlockEntity;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AbstractClayPlateBlockItem extends BlockItem {
    public AbstractClayPlateBlockItem(Block block, Properties properties) {
        super(block, properties);
    }
}
