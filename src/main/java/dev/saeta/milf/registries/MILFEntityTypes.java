package dev.saeta.milf.registries;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.entities.potsherd.PotsherdProjectileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MILFEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MILostFavor.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<PotsherdProjectileEntity>> POTSHERD = register("potsherd", EntityType.Builder.<PotsherdProjectileEntity>of(PotsherdProjectileEntity::new, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .clientTrackingRange(4)
            .updateInterval(20)
    );


    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return ENTITY_TYPES.register(name, () -> builder.build(MILostFavor.MOD_ID + "." + name));
    }

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }


}
