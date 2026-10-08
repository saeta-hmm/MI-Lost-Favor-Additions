package dev.saeta.milf.networking.payloads;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.shapeable_blocks.chisel.ChiseledBlockEntity;
import dev.saeta.milf.blocks.shapeable_blocks.clay.MoldedBlockEntity;
import dev.saeta.milf.registries.MILFBlocks;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record ShapingStartPayload(BlockPos pos, Direction face) implements CustomPacketPayload {

    public final static Type<ShapingStartPayload> TYPE = new Type<>(MILostFavor.locate("shaping_start"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapingStartPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ShapingStartPayload::pos,
            Direction.STREAM_CODEC, ShapingStartPayload::face,
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

            boolean isValidPotteryInput = recipeManager.getAllRecipesFor(MILFRecipeTypes.POTTERY).stream()
                    .map(holder -> holder.value().input())
                    .anyMatch(ingredient -> ingredient.test(blockItemStack));

            if(isValidPotteryInput){
                level.setBlock(pos, MILFBlocks.MOLDED_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);

                if(level.getBlockEntity(pos) instanceof MoldedBlockEntity moldedBlockEntity){

                    moldedBlockEntity.handleHit(payload.face, player);

                }
            }

        };
    }
}
