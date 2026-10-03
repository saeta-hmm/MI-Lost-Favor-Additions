package dev.saeta.milf.util;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

public class RenderUtil {

    public static Vec3 projectPosToScreen(Vec3 pos){
        Minecraft minecraft = Minecraft.getInstance();
        GameRenderer gameRenderer = minecraft.gameRenderer;
        Window window = minecraft.getWindow();

        Matrix4f modelViewMatrix = new Matrix4f();

        Camera camera = gameRenderer.getMainCamera();
        modelViewMatrix.set(camera.rotation().conjugate(new Quaternionf()));
        modelViewMatrix.translate(
                (float) -camera.getPosition().x,
                (float) -camera.getPosition().y,
                (float) -camera.getPosition().z
        );

        Matrix4f projectionMatrix = gameRenderer.getProjectionMatrix(
                getFov(minecraft)
        );

        Vector4f clip = new Vector4f((float) pos.x, (float) pos.y, (float) pos.z, 1f);

        clip.mul(modelViewMatrix);
        clip.mul(projectionMatrix);

        if (clip.w() <= 0) return null;

        float normalizedX = clip.x() / clip.w();
        float normalizedY = clip.y() / clip.w();

        float screenX = (float) ((normalizedX * 0.5 + 0.5) * window.getGuiScaledWidth());
        float screenY = (float) ((-normalizedY * 0.5 + 0.5) * window.getGuiScaledHeight());

        return new Vec3(screenX, screenY, 0);

    }

    private static float getFov(Minecraft minecraft){

        LocalPlayer player = minecraft.player;;

        if(player == null) return 90;

        return minecraft.options.fov().get().floatValue() * player.getFieldOfViewModifier();
    }


    public static class EasingFunctions{

        public static float easeInQuadOutExpo(float progress) {
            if (progress == 0f) {
                return 0f;
            } else if (progress == 1f) {
                return 1f;
            } else if (progress < 0.5f) {
                return (float) (Math.pow(2, 20 * (double) progress - 10) / 2);
            } else {
                return (float) ((float) 1 - Math.pow(-2 * progress + 2, 2) / 2);
            }
        }
    }

}
