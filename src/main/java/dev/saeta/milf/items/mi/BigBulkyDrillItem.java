package dev.saeta.milf.items.mi;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.util.GeometryHelper;
import aztech.modern_industrialization.util.TextHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class BigBulkyDrillItem extends MILFAbstractSteamDrillItem {

    public BigBulkyDrillItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

        appendCommonHoverText(stack, context, tooltip, flag);

        // 5x3 state
        tooltip.add(MIText.MiningArea
                .text((this.isActivated(stack) ? Component.literal("5x3")
                        : MIText.MiningArea1x1.text())
                        .setStyle(TextHelper.NUMBER_TEXT))
                .setStyle(TextHelper.GRAY_TEXT.withItalic(false)));

    }

    private boolean should5by3(ItemStack stack, Player player) {
        return this.isActivated(stack) && !player.isShiftKeyDown();
    }

    @Override
    public @Nullable Area getArea(BlockGetter level, Player player, ItemStack stack, boolean rayTraceOnly) {

        if(!should5by3(stack, player)){
            return null;
        }

        Vec3 lookVec = player.getViewVector(0);

        if(!rayTraceOnly){
            ClickedBlock clickedBlock = lastClickedBlock.get(player);
            if (clickedBlock != null) {
                Area area = getArea(clickedBlock.pos(), clickedBlock.face(), lookVec);
                if(checkIfMineableInArea(level, area)) return area;
            }
        }

        HitResult rayTraceResult = rayTraceSimple(level, player, 0);
        if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockResult = (BlockHitResult) rayTraceResult;
            Direction facing = blockResult.getDirection();

            Area area = getArea(blockResult.getBlockPos(), facing, lookVec);
            if(checkIfMineableInArea(level, area)) return area;
        }

        return null;
    }

    private boolean checkIfMineableInArea(BlockGetter level, Area area){
        if (area == null) return true;

        BlockState centerBlockState = level.getBlockState(area.center());

        if (!isBlockMineable(level, centerBlockState, area.center())) return false;

        return BlockPos.betweenClosedStream(area.corner1(), area.corner2())
                .filter(blockPos -> !area.center().equals(blockPos))
                .allMatch(blockPos -> isBlockMineable(level, level.getBlockState(blockPos), blockPos));

    }

    private boolean isBlockMineable(BlockGetter level, BlockState state, BlockPos pos){
        return state.isAir() ||
                state.is(BlockTags.MINEABLE_WITH_PICKAXE) &&
                ( state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.BASE_STONE_NETHER) ) &&
                state.getDestroySpeed(level, pos) > 0;
    }

    private static Area getArea(BlockPos pos, Direction hitFace, Vec3 lookVec) {
        int faceIndex = hitFace.ordinal();
        Vec3 right = GeometryHelper.FACE_RIGHT[faceIndex];
        Vec3 up = GeometryHelper.FACE_UP[faceIndex];
        boolean isSideFace = hitFace.step().y() == 0;
        if(isSideFace){
            right = right.scale(2);
            int rx = (int) right.x();
            int ry = (int) right.y();
            int rz = (int) right.z();

            int ux = (int) up.x();
            int uy = (int) up.y();
            int uz = (int) up.z();

            return new Area(
                    pos,
                    pos.offset(rx + ux, ry + uy, rz + uz),
                    pos.offset(-rx - ux, -ry - uy, -rz - uz)
            );
        }

        var dotRight = lookVec.x * right.x() + lookVec.y * right.y() + lookVec.z * right.z();
        var dotUp = lookVec.x * up.x() + lookVec.y * up.y() + lookVec.z * up.z();

        if (Math.abs(dotRight) <= Math.abs(dotUp)) {
            right = right.scale(2);
        } else {
            up = up.scale(2);
        }

        int rSign = dotRight > 0 ? 1 : -1;
        int uSign = dotUp > 0 ? 1 : -1;
        int dirX = (int) (rSign * right.x() + uSign * up.x());
        int dirY = (int) (rSign * right.y() + uSign * up.y());
        int dirZ = (int) (rSign * right.z() + uSign * up.z());



        return new Area(
                pos,
                pos.offset(dirX, dirY, dirZ),
                pos.offset(-dirX, -dirY, -dirZ)
        );
    }

}
