package dev.saeta.milf.recipes.anvil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.saeta.milf.recipes.SingleInputSingleOutputRecipe;
import dev.saeta.milf.recipes.SingleRecipeInput;
import dev.saeta.milf.recipes.clay_crucible.ClayCrucibleKilnRecipe;
import dev.saeta.milf.recipes.clay_crucible.CrucibleRecipe;
import dev.saeta.milf.registries.MILFRecipeSerializers;
import dev.saeta.milf.registries.MILFRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record AnvilRecipe(
        SizedIngredient input,
        ItemStack output,
        AnvilTier tier,
        float maxHit
) implements Recipe<SingleRecipeInput> {


    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return input().test(input.input());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return output().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MILFRecipeSerializers.ANVIL_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return MILFRecipeTypes.ANVIL;
    }

    public static class Serializer implements RecipeSerializer<AnvilRecipe>{

        private static final StreamCodec<RegistryFriendlyByteBuf, AnvilTier> ENUM_STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(AnvilTier.class);

        private final static MapCodec<AnvilRecipe> CODEC = RecordCodecBuilder.mapCodec(anvilRecipeInstance -> anvilRecipeInstance.group(
                SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(AnvilRecipe::input),
                ItemStack.CODEC.fieldOf("output").forGetter(AnvilRecipe::output),
                StringRepresentable.fromEnum(AnvilTier::values).fieldOf("tier").forGetter(AnvilRecipe::tier),
                Codec.FLOAT.optionalFieldOf("maxHit", 0.35f).forGetter(AnvilRecipe::maxHit)
                ).apply(anvilRecipeInstance, AnvilRecipe::new));

        private final static StreamCodec<RegistryFriendlyByteBuf, AnvilRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    SizedIngredient.STREAM_CODEC.encode(buf, recipe.input);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                    ENUM_STREAM_CODEC.encode(buf, recipe.tier);
                    ByteBufCodecs.FLOAT.encode(buf, recipe.maxHit());
                },
                buf -> new AnvilRecipe(
                        SizedIngredient.STREAM_CODEC.decode(buf),
                        ItemStack.STREAM_CODEC.decode(buf),
                        ENUM_STREAM_CODEC.decode(buf),
                        ByteBufCodecs.FLOAT.decode(buf)
                )
        );

        @Override
        public MapCodec<AnvilRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AnvilRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
