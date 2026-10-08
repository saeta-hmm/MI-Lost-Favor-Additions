package dev.saeta.milf.util;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

public class RenderUtil {

    public static void renderCuboidFromBlockItemStack(
            VertexConsumer consumer,
            PoseStack poseStack,
            ItemStack itemStack,
            boolean snapTextureToGrid,
            float minX, float maxX,
            float minY, float maxY,
            float minZ, float maxZ,
            int packedLight, int packedOverlay
    ) {

        BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, 0);

        TextureAtlasSprite sprite = itemModel.getParticleIcon(ModelData.EMPTY);


        renderCuboidWithSprite(
                consumer,
                poseStack,
                sprite,
                snapTextureToGrid,
                minX,maxX,
                minY,maxY,
                minZ, maxZ,
                packedLight, packedOverlay

        );
    }

    public static void renderCuboidWithSprite(
            VertexConsumer consumer,
            PoseStack poseStack,
            TextureAtlasSprite sprite,
            boolean snapTextureToGrid,
            float minX, float maxX,
            float minY, float maxY,
            float minZ, float maxZ,
            int packedLight, int packedOverlay
    ) {
        Matrix4f matrix = poseStack.last().pose();

        renderDownFaceWithSprite(consumer, matrix, sprite, snapTextureToGrid, minX, maxX, minY, minZ, maxZ, packedLight, packedOverlay);
        renderUpFaceWithSprite(consumer, matrix, sprite, snapTextureToGrid, minX, maxX, maxY, minZ, maxZ, packedLight, packedOverlay);
        renderNorthFaceWithSprite(consumer, matrix, sprite, snapTextureToGrid, minX, maxX, minY, maxY, minZ, packedLight, packedOverlay);
        renderSouthFaceWithSprite(consumer, matrix, sprite, snapTextureToGrid, minX, maxX, minY, maxY, maxZ, packedLight, packedOverlay);
        renderWestFaceWithSprite(consumer, matrix, sprite, snapTextureToGrid, minY, maxY, minZ, maxZ, minX, packedLight, packedOverlay);
        renderEastFaceWithSprite(consumer, matrix, sprite, snapTextureToGrid, minY, maxY, minZ, maxZ, maxX, packedLight, packedOverlay);
    }

    private static final int UP_COLOR = 0xFFFFFFFF;
    private static final int DOWN_COLOR = 0xFF808080;
    private static final int NS_COLOR = 0xFFCCCCCC;
    private static final int WE_COLOR = 0xFF999999;

    public static void renderUpFaceWithSprite(
            VertexConsumer consumer,
            Matrix4f matrix,
            TextureAtlasSprite sprite,
            boolean snapTextureToGrid,
            float minX, float maxX,
            float maxY,
            float minZ, float maxZ,
            int packedLight, int packedOverlay
    ) {

        float u0, u1, v0, v1;

        if (snapTextureToGrid) {
            u0 = sprite.getU(minX);
            u1 = sprite.getU(maxX);
            v0 = sprite.getV(minZ);
            v1 = sprite.getV(maxZ);
        } else {
            u0 = sprite.getU0();
            u1 = sprite.getU1();
            v0 = sprite.getV0();
            v1 = sprite.getV1();
        }

        consumer.addVertex(matrix, minX, maxY, minZ).setColor(UP_COLOR).setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 1, 0);
        consumer.addVertex(matrix, minX, maxY, maxZ).setColor(UP_COLOR).setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 1, 0);
        consumer.addVertex(matrix, maxX, maxY, maxZ).setColor(UP_COLOR).setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 1, 0);
        consumer.addVertex(matrix, maxX, maxY, minZ).setColor(UP_COLOR).setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 1, 0);

    }

    private static void renderDownFaceWithSprite(
            VertexConsumer consumer,
            Matrix4f matrix,
            TextureAtlasSprite sprite,
            boolean snapTextureToGrid,
            float minX, float maxX,
            float minY,
            float minZ, float maxZ,
            int packedLight, int packedOverlay
    ) {

        float u0, u1, v0, v1;

        if (snapTextureToGrid) {
            u0 = sprite.getU(minX);
            u1 = sprite.getU(maxX);
            v0 = sprite.getV(minZ);
            v1 = sprite.getV(maxZ);
        } else {
            u0 = sprite.getU0();
            u1 = sprite.getU1();
            v0 = sprite.getV0();
            v1 = sprite.getV1();
        }

        consumer.addVertex(matrix, minX, minY, maxZ).setColor(DOWN_COLOR).setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, -1, 0);
        consumer.addVertex(matrix, minX, minY, minZ).setColor(DOWN_COLOR).setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, -1, 0);
        consumer.addVertex(matrix, maxX, minY, minZ).setColor(DOWN_COLOR).setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, -1, 0);
        consumer.addVertex(matrix, maxX, minY, maxZ).setColor(DOWN_COLOR).setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, -1, 0);

    }

    private static void renderNorthFaceWithSprite(
            VertexConsumer consumer,
            Matrix4f matrix,
            TextureAtlasSprite sprite,
            boolean snapTextureToGrid,
            float minX, float maxX,
            float minY, float maxY,
            float minZ,
            int packedLight, int packedOverlay
    ) {

        float u0, u1, v0, v1;

        if (snapTextureToGrid) {
            u1 = sprite.getU(1 - minX);
            u0 = sprite.getU(1 - maxX);
            v0 = sprite.getV(1 - maxY);
            v1 = sprite.getV(1 - minY);
        } else {
            u0 = sprite.getU0();
            u1 = sprite.getU1();
            v0 = sprite.getV0();
            v1 = sprite.getV1();
        }

        consumer.addVertex(matrix, minX, maxY, minZ).setColor(NS_COLOR).setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, -1);
        consumer.addVertex(matrix, maxX, maxY, minZ).setColor(NS_COLOR).setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, -1);
        consumer.addVertex(matrix, maxX, minY, minZ).setColor(NS_COLOR).setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, -1);
        consumer.addVertex(matrix, minX, minY, minZ).setColor(NS_COLOR).setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, -1);

    }

    private static void renderSouthFaceWithSprite(
            VertexConsumer consumer,
            Matrix4f matrix,
            TextureAtlasSprite sprite,
            boolean snapTextureToGrid,
            float minX, float maxX,
            float minY, float maxY,
            float maxZ,
            int packedLight, int packedOverlay
    ) {

        float u0, u1, v0, v1;

        if (snapTextureToGrid) {
            u0 = sprite.getU(minX);
            u1 = sprite.getU(maxX);
            v0 = sprite.getV(1 - maxY);
            v1 = sprite.getV(1 - minY);
        } else {
            u0 = sprite.getU0();
            u1 = sprite.getU1();
            v0 = sprite.getV0();
            v1 = sprite.getV1();
        }

        consumer.addVertex(matrix, minX, minY, maxZ).setColor(NS_COLOR).setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        consumer.addVertex(matrix, maxX, minY, maxZ).setColor(NS_COLOR).setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        consumer.addVertex(matrix, maxX, maxY, maxZ).setColor(NS_COLOR).setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        consumer.addVertex(matrix, minX, maxY, maxZ).setColor(NS_COLOR).setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);

    }

    private static void renderWestFaceWithSprite(
            VertexConsumer consumer,
            Matrix4f matrix,
            TextureAtlasSprite sprite,
            boolean snapTextureToGrid,
            float minY, float maxY,
            float minZ, float maxZ,
            float minX,
            int packedLight, int packedOverlay
    ) {

        float u0, u1, v0, v1;

        if (snapTextureToGrid) {
            u0 = sprite.getU(maxZ);
            u1 = sprite.getU(minZ);
            v0 = sprite.getV(1 - maxY);
            v1 = sprite.getV(1 - minY);
        } else {
            u0 = sprite.getU0();
            u1 = sprite.getU1();
            v0 = sprite.getV0();
            v1 = sprite.getV1();
        }

        consumer.addVertex(matrix, minX, maxY, maxZ).setColor(WE_COLOR).setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(-1, 0, 0);
        consumer.addVertex(matrix, minX, maxY, minZ).setColor(WE_COLOR).setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(-1, 0, 0);
        consumer.addVertex(matrix, minX, minY, minZ).setColor(WE_COLOR).setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(-1, 0, 0);
        consumer.addVertex(matrix, minX, minY, maxZ).setColor(WE_COLOR).setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(-1, 0, 0);

    }

    private static void renderEastFaceWithSprite(
            VertexConsumer consumer,
            Matrix4f matrix,
            TextureAtlasSprite sprite,
            boolean snapTextureToGrid,
            float minY, float maxY,
            float minZ, float maxZ,
            float maxX,
            int packedLight, int packedOverlay
    ) {

        float u0, u1, v0, v1;

        if (snapTextureToGrid) {
            u0 = sprite.getU(1 - maxZ);
            u1 = sprite.getU(1 - minZ);
            v0 = sprite.getV(1 - maxY);
            v1 = sprite.getV(1 - minY);
        } else {
            u0 = sprite.getU0();
            u1 = sprite.getU1();
            v0 = sprite.getV0();
            v1 = sprite.getV1();
        }

        consumer.addVertex(matrix, maxX, minY, maxZ).setColor(WE_COLOR).setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(1, 0, 0);
        consumer.addVertex(matrix, maxX, minY, minZ).setColor(WE_COLOR).setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(1, 0, 0);
        consumer.addVertex(matrix, maxX, maxY, minZ).setColor(WE_COLOR).setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(1, 0, 0);
        consumer.addVertex(matrix, maxX, maxY, maxZ).setColor(WE_COLOR).setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(1, 0, 0);

    }

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

        public static float easeInOutExpo(float progress) {
            if (progress == 0) return 0;
            if (progress == 1) return 1;
            if (progress < 0.5) {
                return (float) (Math.pow(2, 20 * progress - 10) / 2);
            } else {
                return (float) ((2 - Math.pow(2, -20 * progress + 10)) / 2);
            }
        }

        public static float easeInBack(float progress) {
            float c1 = 1f;
            float c3 = c1 + 1;

            return (c3 * progress * progress * progress - c1 * progress * progress);
        }


    }

}
