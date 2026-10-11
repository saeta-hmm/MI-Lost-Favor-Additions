package dev.saeta.milf.networking.data;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record StoredItem(ItemStack stack) {

    public static final Codec<StoredItem> CODEC = ItemStack.OPTIONAL_CODEC.xmap(StoredItem::new, StoredItem::stack);

    public static final StreamCodec<RegistryFriendlyByteBuf, StoredItem> STREAM_CODEC = StreamCodec.composite(ItemStack.OPTIONAL_STREAM_CODEC, StoredItem::stack, StoredItem::new);

    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) return false;
        StoredItem other = (StoredItem) o;
        return ItemStack.matches(this.stack, other.stack);

    }

}
