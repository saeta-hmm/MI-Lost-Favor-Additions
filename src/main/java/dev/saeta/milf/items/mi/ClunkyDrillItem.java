package dev.saeta.milf.items.mi;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.util.GeometryHelper;
import aztech.modern_industrialization.util.TextHelper;
import dev.saeta.milf.registries.MILFDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ClunkyDrillItem extends MILFAbstractSteamDrillItem {

    public ClunkyDrillItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

        appendCommonHoverText(stack, context, tooltip, flag);

        // 1x3 state
        tooltip.add(MIText.MiningArea
                .text((this.isActivated(stack) ? Component.literal("1x3")
                        .append((isHorizontal(stack) ?
                                Component.translatable("milf.clunky_drill.horizontal") :
                                Component.translatable("milf.clunky_drill.vertical")))
                        : MIText.MiningArea1x1.text())

                        .setStyle(TextHelper.NUMBER_TEXT))
                        .setStyle(TextHelper.GRAY_TEXT.withItalic(false)));

    }

    private boolean should3by1(ItemStack stack, Player player) {
        return this.isActivated(stack) && !player.isShiftKeyDown();
    }

    @Override
    public @Nullable Area getArea(BlockGetter level, Player player, ItemStack stack, boolean rayTraceOnly) {

        if(!should3by1(stack, player)){
            return null;
        }

        Vec3 lookVec = player.getViewVector(0);

        if(!rayTraceOnly){
            ClickedBlock clickedBlock = lastClickedBlock.get(player);
            if (clickedBlock != null) {
                return getArea(clickedBlock.pos(), clickedBlock.face(), lookVec, stack);
            }
        }

        HitResult rayTraceResult = rayTraceSimple(level, player, 0);
        if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockResult = (BlockHitResult) rayTraceResult;
            Direction facing = blockResult.getDirection();

            return getArea(blockResult.getBlockPos(), facing, lookVec, stack);
        }
        return null;
    }

    private static Area getArea(BlockPos pos, Direction hitFace, Vec3 lookVec, ItemStack stack) {
        int faceIndex = hitFace.ordinal();
        Vec3 right = GeometryHelper.FACE_RIGHT[faceIndex];
        Vec3 up = GeometryHelper.FACE_UP[faceIndex];
        boolean isSideFace = hitFace.step().y() == 0;
        if(isSideFace){
            Vec3 side = isHorizontal(stack) ? right : up;
            int rx = (int) side.x();
            int ry = (int) side.y();
            int rz = (int) side.z();
            return new Area(pos, pos.offset(rx, ry, rz), pos.offset(-rx, -ry, -rz));
        }

        var dotRight = lookVec.x * right.x() + lookVec.y * right.y() + lookVec.z * right.z();
        var dotUp = lookVec.x * up.x() + lookVec.y * up.y() + lookVec.z * up.z();

        int dirX, dirY, dirZ;

        if (isHorizontal(stack)) {
            if (Math.abs(dotRight) <= Math.abs(dotUp)) {
                int sign = dotRight > 0 ? 1 : -1;
                dirX = (int) (sign * right.x());
                dirY = (int) (sign * right.y());
                dirZ = (int) (sign * right.z());
            } else {
                int sign = dotUp > 0 ? 1 : -1;
                dirX = (int) (sign * up.x());
                dirY = (int) (sign * up.y());
                dirZ = (int) (sign * up.z());
            }
        } else {
            if (Math.abs(dotRight) >= Math.abs(dotUp)) {
                int sign = dotRight > 0 ? 1 : -1;
                dirX = (int) (sign * right.x());
                dirY = (int) (sign * right.y());
                dirZ = (int) (sign * right.z());
            } else {
                int sign = dotUp > 0 ? 1 : -1;
                dirX = (int) (sign * up.x());
                dirY = (int) (sign * up.y());
                dirZ = (int) (sign * up.z());
            }
        }

        return new Area(
                pos,
                pos.offset(dirX, dirY, dirZ),
                pos.offset(-dirX, -dirY, -dirZ)
        );
    }

    private static boolean isHorizontal(ItemStack stack){
        Boolean isHorizontal = stack.get(MILFDataComponents.IS_HORIZONTAL);

        if(isHorizontal == null) return false;

        return isHorizontal;

    }

}
