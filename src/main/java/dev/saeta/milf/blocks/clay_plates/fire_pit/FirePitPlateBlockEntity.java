package dev.saeta.milf.blocks.clay_plates.fire_pit;

import dev.saeta.milf.blocks.AbstractBEItemHandler;
import dev.saeta.milf.blocks.ItemHandlerBlockEntity;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.registries.MILFBlockEntities;
import dev.saeta.milf.registries.MILFDataComponents;
import dev.saeta.milf.registries.MILFItemTags;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class FirePitPlateBlockEntity extends ItemHandlerBlockEntity {

    public static final int NE_PLATE_SLOT = 0;
    public static final int NW_PLATE_SLOT = 1;
    public static final int SE_PLATE_SLOT = 2;
    public static final int SW_PLATE_SLOT = 3;

    private final static double PLATE_OFFSET = (double) 6 / 16;

    private final FirePitPlateItemHandler itemHandler = new FirePitPlateItemHandler(4);


    public FirePitPlateBlockEntity(BlockPos pos, BlockState blockState) {
        super(MILFBlockEntities.FIRE_PIT_PLATE.get(), pos, blockState);
    }

    public VoxelShape getCombinedShape(){
        VoxelShape baseShape = Block.box(0,0,0,0,0,0);

        if(level == null ) return baseShape;

        for (int i = 0; i < getItemHandler().getSlots(); i++) {
            ItemStack stack = getItemHandler().getStackInSlot(i);
            if(!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem){
                switch (i){
                    case NE_PLATE_SLOT -> baseShape = Shapes.or(baseShape , blockItem.getBlock().defaultBlockState().getShape(level, worldPosition).move(-PLATE_OFFSET, (double) 1 / 16, -PLATE_OFFSET));
                    case NW_PLATE_SLOT ->baseShape = Shapes.or(baseShape , blockItem.getBlock().defaultBlockState().getShape(level, worldPosition).move(PLATE_OFFSET, (double) 1 / 16, -PLATE_OFFSET));
                    case SE_PLATE_SLOT ->baseShape = Shapes.or(baseShape , blockItem.getBlock().defaultBlockState().getShape(level, worldPosition).move(-PLATE_OFFSET, (double) 1 / 16, PLATE_OFFSET));
                    case SW_PLATE_SLOT ->baseShape = Shapes.or(baseShape , blockItem.getBlock().defaultBlockState().getShape(level, worldPosition).move(PLATE_OFFSET, (double) 1 / 16, PLATE_OFFSET));
                    default -> {}
                }
            }
        }

        return baseShape;
    }

    public Component getName(){
        MutableComponent baseComponent = Component.literal("");

        if(level == null ) return baseComponent;

        for (int i = 0; i < getItemHandler().getSlots(); i++) {
            ItemStack stack = getItemHandler().getStackInSlot(i);
            if(!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem){
                switch (i){
                    case NE_PLATE_SLOT ->  baseComponent.append(blockItem.getBlock().getName());
                    case NW_PLATE_SLOT -> baseComponent.append(", ").append(blockItem.getBlock().getName());
                    case SE_PLATE_SLOT -> baseComponent.append("...");
                    case SW_PLATE_SLOT ->{}
                    default -> {}
                }
            }
        }

        return baseComponent;
    }

    public BlockState getFirstItemState(){

        return getBlockItemState(0);
    }

    public BlockState getBlockItemState(int slot){

        ItemStack stack = itemHandler.getStackInSlot(slot);

        if(!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem){
            return blockItem.getBlock().defaultBlockState();
        }

        return getBlockState();
    }

    public float getCombinedHardness(Level level, BlockPos pos){

        float hardness = 0;

        for (int i = 0; i < getItemHandler().getSlots(); i++) {
            ItemStack stack = getItemHandler().getStackInSlot(i);
            if(!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem){
                hardness += blockItem.getBlock().defaultBlockState().getDestroySpeed(level, pos);
            }
        }

        return hardness;
    }

    public void firePitTick(){
        for (int i = 0; i < getItemHandler().getSlots(); i++) {
            ItemStack stack = getItemHandler().getStackInSlot(i);
            if(!stack.isEmpty() && stack.getOrDefault(MILFDataComponents.FIRING_PROGRESS, 0 ) != -109){
                stack.set(MILFDataComponents.FIRING_PROGRESS, stack.getOrDefault(MILFDataComponents.FIRING_PROGRESS, 0 ) + 1);
                if(stack.getOrDefault(MILFDataComponents.FIRING_PROGRESS, 0 ) == 200){
                    handleFiringEnd(i);
                    stack.set(MILFDataComponents.FIRING_PROGRESS, -109);
                }
            }
        }
    }

    private void handleFiringEnd(int slot){

        if(level == null) return;

        ItemStack stack = itemHandler.getStackInSlot(slot);

        SingleRecipeInput input = new SingleRecipeInput(stack);

        var recipe = level.getRecipeManager().getRecipeFor(MILFRecipeTypes.PIT_FIRING, input, level);

        recipe.ifPresent(pitFiringRecipeRecipeHolder -> itemHandler.setStackInSlot(slot, pitFiringRecipeRecipeHolder.value().assemble(input, level.registryAccess())));

        setChanged();
    }

    @Override
    public FirePitPlateItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public @NotNull BlockPos getOutputPos() {
        return worldPosition;
    }

    public class FirePitPlateItemHandler extends AbstractBEItemHandler {

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if(level == null) return super.insertItem(slot, stack, simulate);
            switch (slot){
                case NE_PLATE_SLOT -> level.setBlock(worldPosition, getBlockState().setValue(FirePitPlateBlock.NE_PLATE, true), FirePitPlateBlock.UPDATE_NONE);
                case NW_PLATE_SLOT -> level.setBlock(worldPosition, getBlockState().setValue(FirePitPlateBlock.NW_PLATE, true), FirePitPlateBlock.UPDATE_NONE);
                case SE_PLATE_SLOT -> level.setBlock(worldPosition, getBlockState().setValue(FirePitPlateBlock.SE_PLATE, true), FirePitPlateBlock.UPDATE_NONE);
                case SW_PLATE_SLOT -> level.setBlock(worldPosition, getBlockState().setValue(FirePitPlateBlock.SW_PLATE, true), FirePitPlateBlock.UPDATE_NONE);
                default -> {}
            }
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(level == null || simulate) return super.extractItem(slot, amount, simulate);
            switch (slot){
                case NE_PLATE_SLOT -> level.setBlock(worldPosition, getBlockState().setValue(FirePitPlateBlock.NE_PLATE, false), FirePitPlateBlock.UPDATE_NONE);
                case NW_PLATE_SLOT -> level.setBlock(worldPosition, getBlockState().setValue(FirePitPlateBlock.NW_PLATE, false), FirePitPlateBlock.UPDATE_NONE);
                case SE_PLATE_SLOT -> level.setBlock(worldPosition, getBlockState().setValue(FirePitPlateBlock.SE_PLATE, false), FirePitPlateBlock.UPDATE_NONE);
                case SW_PLATE_SLOT -> level.setBlock(worldPosition, getBlockState().setValue(FirePitPlateBlock.SW_PLATE, false), FirePitPlateBlock.UPDATE_NONE);
                default -> {}
            }
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (level == null) {
                setChanged();
                return;
            }
            boolean isEmpty = true;
            for (int i = 0; i < getItemHandler().getSlots(); i++) {
                ItemStack stack = getItemHandler().getStackInSlot(i);
                if(!stack.isEmpty()){
                    isEmpty = false;
                    break;
                }
            }
            if(isEmpty) level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            setChanged();
        }

        public FirePitPlateItemHandler(int size) {
            super(size);
        }

        @Override
        protected boolean testInput(ItemStack stack) {
            return stack.is(MILFItemTags.FIRE_PIT_PLATE_CAN_HOLD) && stack.getOrDefault(MILFDataComponents.FLUID, SimpleFluidContent.EMPTY).isEmpty();
        }
    }
}
