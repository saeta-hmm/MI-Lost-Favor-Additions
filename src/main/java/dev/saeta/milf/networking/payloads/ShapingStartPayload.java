package dev.saeta.milf.networking.payloads;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlockEntity;
import dev.saeta.milf.blocks.shapeable_blocks.clay.MoldedBlockEntity;
import dev.saeta.milf.networking.data.ShapingType;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFRecipeTypes;
import dev.saeta.milf.util.RecipeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record ShapingStartPayload(BlockPos pos, Direction face, ShapingType shapingType) implements CustomPacketPayload {

    public final static Type<ShapingStartPayload> TYPE = new Type<>(MILostFavor.locate("shaping_start"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapingStartPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ShapingStartPayload::pos,
            Direction.STREAM_CODEC, ShapingStartPayload::face,
            ByteBufCodecs.idMapper(ShapingType.BY_ID, ShapingType::getId), ShapingStartPayload::shapingType,
            ShapingStartPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static IPayloadHandler<ShapingStartPayload> getPayloadHandler() {
        return (payload, context) -> {

            ServerPlayer player = (ServerPlayer) context.player();
            ServerLevel level = (ServerLevel) player.level();
            RecipeManager recipeManager = level.getRecipeManager();
            BlockPos pos = payload.pos;

            BlockState state = level.getBlockState(pos);

            ItemStack blockItemStack = new ItemStack(state.getBlock());

            switch (payload.shapingType){
                case CHISEL -> {
                    boolean isValidChiselInput = recipeManager.getAllRecipesFor(MILFRecipeTypes.CHISEL).stream()
                            .map(holder -> holder.value().input())
                            .anyMatch(ingredient -> ingredient.test(blockItemStack));

                    if(isValidChiselInput){
                        level.setBlock(pos, MILFBlocks.CHISELED_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);

                        if(level.getBlockEntity(pos) instanceof ChiseledBlockEntity chiseledBlockEntity){

                            chiseledBlockEntity.setInputBlockItem(blockItemStack);

                            chiseledBlockEntity.handleHit(payload.face, player);
                        }

                        return;

                    }

                    var recipeInput = new SingleRecipeInput(blockItemStack);
                    var chiselHitRecipe = RecipeUtil.getValidRecipe(level, MILFRecipeTypes.CHISEL_HIT, recipeInput);

                    if(chiselHitRecipe.isPresent()){
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        level.playSound(null, pos, SoundEvents.BASALT_BREAK, SoundSource.BLOCKS);
                        Block.popResource(level, pos, chiselHitRecipe.get().value().assemble(recipeInput, level.registryAccess()));
                    }
                }
                case POTTERY -> {
                    boolean isValidPotteryInput = recipeManager.getAllRecipesFor(MILFRecipeTypes.POTTERY).stream()
                            .map(holder -> holder.value().input())
                            .anyMatch(ingredient -> ingredient.test(blockItemStack));

                    if(isValidPotteryInput){
                        level.setBlock(pos, MILFBlocks.MOLDED_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);

                        if(level.getBlockEntity(pos) instanceof MoldedBlockEntity moldedBlockEntity){

                            moldedBlockEntity.setCurrentInputBlockItem(blockItemStack);

                            moldedBlockEntity.handleHit(payload.face, player);

                        }
                    }
                }
            }






        };
    }
}
