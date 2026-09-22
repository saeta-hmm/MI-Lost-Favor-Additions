package dev.saeta.milf.items.mi;

import aztech.modern_industrialization.MIComponents;
import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.items.SteamDrillFuel;
import aztech.modern_industrialization.items.SteamDrillItem;
import aztech.modern_industrialization.util.TextHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;

//The original class has a gazillion private methods required for this thing to properly work, so this class basically copies
//the ones I need ( I SURE HOPE this will not lead to some weird interactions with the MI drill itself (‾◡◝) )
//https://github.com/AztechMC/Modern-Industrialization/blob/1.21.x/src/main/java/aztech/modern_industrialization/items/SteamDrillItem.java

public abstract class MILFAbstractSteamDrillItem extends SteamDrillItem {

    protected static final int FULL_WATER = 18000;

    public MILFAbstractSteamDrillItem(Properties settings) {
        super(settings);
    }

    protected static HitResult rayTraceSimple(BlockGetter world, Player living, float partialTicks) {
        double blockReachDistance = living.blockInteractionRange();
        Vec3 vec3d = living.getEyePosition(partialTicks);
        Vec3 vec3d1 = living.getViewVector(partialTicks);
        Vec3 vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return world.clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, living));
    }

    protected void appendCommonHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        var data = (MILFSteamDrillTooltipData) getTooltipImage(stack).get();

        // Water %
        tooltip.add(MIText.WaterPercent.text(data.waterLevel()).setStyle(TextHelper.WATER_TEXT));
        int barWater = (int) Math.ceil(data.waterLevel() / 5d);
        int barVoid = 20 - barWater;
        // Water bar
        tooltip.add(Component.literal("|".repeat(barWater)).setStyle(TextHelper.WATER_TEXT)
                .append(Component.literal("|".repeat(barVoid)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6b6b6b)))));
        // Fuel left
        if (data.burnTicks() > 0) {
            tooltip.add(MIText.SecondsLeft.text(data.burnTicks() / 100).setStyle(TextHelper.GRAY_TEXT));
        }
        // Silk touch
        tooltip.add(MIText.SilkTouchState
                .text((isNotSilkTouch(stack) ? MIText.Deactivated.text().setStyle(TextHelper.RED)
                        : MIText.Activated.text().setStyle(TextHelper.GREEN)))
                .setStyle(TextHelper.GRAY_TEXT.withItalic(false)));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        var fuel = stack.getOrDefault(MIComponents.STEAM_DRILL_FUEL, SteamDrillFuel.EMPTY);
        return Optional.of(new MILFSteamDrillTooltipData(
                stack.getOrDefault(MIComponents.WATER, 0) * 100 / FULL_WATER,
                fuel.burnTicks(),
                Math.max(1, fuel.maxBurnTicks()),
                getResource(stack),
                getAmount(stack)));
    }

    protected static final WeakHashMap<Player, ClickedBlock> lastClickedBlock = new WeakHashMap<>();

    static {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, MILFAbstractSteamDrillItem::mergeDrops);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, MILFAbstractSteamDrillItem::trackClickedFace);
    }

    protected record ClickedBlock(BlockPos pos, Direction face) {}

    @Nullable
    private static List<ItemStack> totalDrops = null;

    private static void mergeDrops(BlockDropsEvent event) {
        if (totalDrops == null) {
            return;
        }

        outer:
        for (var entity : event.getDrops()) {
            if (entity.getItem().isEmpty()) {
                continue;
            }
            for (ItemStack drop : totalDrops) {
                if (ItemStack.isSameItemSameComponents(entity.getItem(), drop)) {
                    drop.grow(entity.getItem().getCount());
                    continue outer;
                }
            }
            totalDrops.add(entity.getItem());
        }
        event.getDrops().clear();
    }

    private static void trackClickedFace(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            PlayerInteractEvent.LeftClickBlock.Action action = event.getAction();
            if (action == PlayerInteractEvent.LeftClickBlock.Action.START ||
                    action == PlayerInteractEvent.LeftClickBlock.Action.STOP) {
                lastClickedBlock.put(player, new ClickedBlock(event.getPos().immutable(), event.getFace()));
            } else if (action == PlayerInteractEvent.LeftClickBlock.Action.ABORT) {
                lastClickedBlock.remove(player);
            }
        }
    }

    protected static boolean isNotSilkTouch(ItemStack stack) {
        return !stack.getOrDefault(MIComponents.SILK_TOUCH, true);
    }

}
