package dev.saeta.milf.networking.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record MultiFluidContents(List<FluidStack> fluids) {

    public final static MultiFluidContents EMPTY = new MultiFluidContents(List.of());

    public static final Codec<MultiFluidContents> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(FluidStack.OPTIONAL_CODEC.listOf().fieldOf("fluids").forGetter(MultiFluidContents::fluids)).apply(instance, MultiFluidContents::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MultiFluidContents> STREAM_CODEC= StreamCodec.composite(
            FluidStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), MultiFluidContents::fluids,
            MultiFluidContents::new
    );
}
