package dev.saeta.milf.entities.potsherd;

import dev.saeta.milf.registries.MILFEntityTypes;
import dev.saeta.milf.registries.MILFItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PotsherdProjectileEntity extends AbstractArrow {

    public PotsherdProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public PotsherdProjectileEntity(LivingEntity owner, Level level, ItemStack pickupItemStack) {
        super(MILFEntityTypes.POTSHERD.get(), owner, level, pickupItemStack, null);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(MILFItems.POTSHERD.get());
    }



    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {

        return SoundEvents.SHULKER_HURT_CLOSED;
    }

    public boolean isInGround(){
        return inGround;
    }
}
