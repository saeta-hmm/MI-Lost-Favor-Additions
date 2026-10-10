package dev.saeta.milf.networking.payloads;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.anvils.AbstractAnvilBlockEntity;
import dev.saeta.milf.entities.potsherd.PotsherdProjectileEntity;
import dev.saeta.milf.items.PotsherdItem;
import dev.saeta.milf.registries.MILFItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public record ThrowPayload() implements CustomPacketPayload {

    public final static ThrowPayload INSTANCE = new ThrowPayload();

    public final static Type<ThrowPayload> TYPE = new Type<>(MILostFavor.locate("throw"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ThrowPayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static IPayloadHandler<ThrowPayload> getPayloadHandler() {
        return (payload, context) -> {

            ServerPlayer player = (ServerPlayer) context.player();
            ServerLevel level = (ServerLevel) player.level();

            ItemStack itemStack = player.getMainHandItem();

            if(itemStack.getItem() instanceof PotsherdItem potsherdItem){

                PotsherdProjectileEntity projectile = new PotsherdProjectileEntity(player, level, itemStack.copyWithCount(1));

                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0,1.6f,0);

                level.addFreshEntity(projectile);

                itemStack.shrink(1);

            }
        };
    }


}