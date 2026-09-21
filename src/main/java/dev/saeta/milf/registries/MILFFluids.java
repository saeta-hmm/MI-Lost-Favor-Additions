package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MILFFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MILostFavor.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, MILostFavor.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> DUMMY_FLUID_TYPE = FLUID_TYPES.register("dummy_fluid_type",
            () -> new FluidType(FluidType.Properties.create()));

    public static final DeferredHolder<Fluid, FlowingFluid> DUMMY_FLUID = FLUIDS.register("dummy_fluid",
            () -> new BaseFlowingFluid.Source(MILFFluids.DUMMY_FLUID_PROPERTIES));

    public static final DeferredHolder<Fluid, FlowingFluid> DUMMY_FLUID_FLOWING = FLUIDS.register("dummy_fluid_flowing",
            () -> new BaseFlowingFluid.Flowing(MILFFluids.DUMMY_FLUID_PROPERTIES));

    public static final BaseFlowingFluid.Properties DUMMY_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            DUMMY_FLUID_TYPE, DUMMY_FLUID, DUMMY_FLUID_FLOWING
    );

    public static void register(IEventBus eventBus){
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }

    public static void registerClient(RegisterClientExtensionsEvent event) {
        setupFluid(event, DUMMY_FLUID_TYPE.get(), "dummy_fluid");

        ItemBlockRenderTypes.setRenderLayer(DUMMY_FLUID.get(), RenderType.translucent());
    }

    public static void setupFluid(RegisterClientExtensionsEvent event, FluidType fluidType, String name){

        ResourceLocation still = MILostFavor.locate("block/fluid/" + name + "_still");
        ResourceLocation flowing = MILostFavor.locate("block/fluid/" + name + "_flow");

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return still;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowing;
            }

        }, fluidType);
    }
}
