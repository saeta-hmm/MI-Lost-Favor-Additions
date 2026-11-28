package dev.saeta.milf.networking.payloads;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import dev.saeta.milf.MILostFavor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

import java.util.List;

public record LecternCraftPayload(int containerId, int destination, boolean all, List<List<ItemStack>> inputs) implements CustomPacketPayload {

    public static final int FILL = 0;
    public static final int CURSOR = 1;
    public static final int INVENTORY = 2;

    public static final Type<LecternCraftPayload> TYPE = new Type<>(MILostFavor.locate("lectern_craft"));

    public static final StreamCodec<RegistryFriendlyByteBuf, List<List<ItemStack>>> INPUTS_CODEC =
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list(9));

    public static final StreamCodec<RegistryFriendlyByteBuf, LecternCraftPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, LecternCraftPayload::containerId,
            ByteBufCodecs.VAR_INT, LecternCraftPayload::destination,
            ByteBufCodecs.BOOL, LecternCraftPayload::all,
            INPUTS_CODEC, LecternCraftPayload::inputs,
            LecternCraftPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static IPayloadHandler<LecternCraftPayload> getPayloadHandler() {
        return (payload, context) -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!(player.containerMenu instanceof CraftingTerminalMenu menu)
                    || menu.containerId != payload.containerId()
                    || !menu.stillValid(player)) {
                return;
            }
            boolean crafting = payload.destination() != FILL;
            if (crafting && !menu.getCarried().isEmpty()) {
                return;
            }
            menu.onTransferHandler(payload.inputs());
            if (!crafting) {
                return;
            }
            int result = menu.getResultSlotIndex();
            if (menu.getSlot(result).getItem().isEmpty()) {
                return;
            }
            if (payload.destination() == CURSOR) {
                menu.clicked(result, 0, ClickType.PICKUP, player);
            } else if (payload.all()) {
                menu.clicked(result, 0, ClickType.QUICK_MOVE, player);
            } else {
                menu.clicked(result, 0, ClickType.PICKUP, player);
                ItemStack crafted = menu.getCarried().copy();
                player.getInventory().add(crafted);
                menu.setCarried(crafted.isEmpty() ? ItemStack.EMPTY : crafted);
            }
            menu.broadcastChanges();
        };
    }
}
