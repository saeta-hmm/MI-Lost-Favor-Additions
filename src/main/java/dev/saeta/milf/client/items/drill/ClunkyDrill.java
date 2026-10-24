package dev.saeta.milf.client.items.drill;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.client.notifications.Notification;
import dev.saeta.milf.client.notifications.NotificationManager;
import dev.saeta.milf.networking.payloads.ClunkyDrillModePayload;
import dev.saeta.milf.registries.MILFItems;
import dev.saeta.milf.registries.MILFSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class ClunkyDrill {

    private static int delayTicks;

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event){
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if(minecraft.player != null
                && player.isShiftKeyDown()
                && player.getMainHandItem().is(MILFItems.CLUNKY_DRILL )
        ){
            if(delayTicks==0){
                PacketDistributor.sendToServer(new ClunkyDrillModePayload());
                NotificationManager.push(new Notification(Component.translatable("milf.clunky_drill.mode"), 40));
                player.playSound(MILFSounds.TICK, 1, 1);
                delayTicks = 10;
            }

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event){
        if(delayTicks > 0){
            delayTicks--;
        }
    }

}
