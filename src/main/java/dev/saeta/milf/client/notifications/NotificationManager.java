package dev.saeta.milf.client.notifications;

import dev.saeta.milf.MILostFavor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;

@EventBusSubscriber(modid = MILostFavor.MOD_ID, value = Dist.CLIENT)
public class NotificationManager {

    public final static ResourceLocation ID = MILostFavor.locate("notifications");


    private final static ArrayList<Notification> notifications = new ArrayList<>();

    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker){

        if(!notifications.isEmpty()) {

            for (int i = 0; i < notifications.size(); i++) {
                notifications.get(i).render(guiGraphics, deltaTracker, i);
            }


        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event){
        if(!notifications.isEmpty()) {

            notifications.forEach(Notification::tick);
            notifications.removeIf(Notification::tryRemove);

        }
    }

    public static void push(Notification notification){
        notifications.add(notification);
        notification.onPush();

    }

}
