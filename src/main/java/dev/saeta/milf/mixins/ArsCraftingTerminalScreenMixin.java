package dev.saeta.milf.mixins;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalScreen;
import com.hollingsworth.arsnouveau.client.container.IAutoFillTerminal;
import com.hollingsworth.arsnouveau.client.container.SlotStorage;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.TerminalScrollSlider;
import dev.emi.emi.api.EmiApi;
import dev.saeta.milf.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Storage lectern layout and search fixes.
 * <p>
 * The stock screen shows 3 storage rows (7 expanded) inside a 248px panel. This adds up to three
 * more rows above the panel: the header and the extra row slices are copied from the GUI texture
 * and drawn above {@code topPos}, and the extra rows' slots sit at negative y. The number of extra
 * rows follows the screen height, so small GUI scales fall back to the stock layout instead of
 * clipping the panel. The scroll slider and the visible row counts follow the same split.
 */
@Mixin(CraftingTerminalScreen.class)
public abstract class ArsCraftingTerminalScreenMixin extends AbstractContainerScreen<CraftingTerminalMenu> {
    @Unique
    private static final int MILF_ROW_HEIGHT = 18;
    @Unique
    private static final int MILF_HEADER_HEIGHT = 21;
    @Unique
    private static final int MILF_MAX_EXTRA_ROWS = 3;
    @Unique
    private static final int MILF_PLAIN_ROW_V = 93;
    @Unique
    private static final int MILF_GROOVE_X = 183;
    @Unique
    private static final int MILF_GROOVE_WIDTH = 19;
    @Unique
    private static final int MILF_GROOVE_CAP_V = 14;
    @Unique
    private static final int MILF_GROOVE_CAP_HEIGHT = 12;
    @Unique
    private static final int MILF_GROOVE_MIDDLE_V = 26;
    @Unique
    private static final int MILF_GROOVE_BOTTOM_V = 63;
    @Unique
    private static final int MILF_GROOVE_BOTTOM_HEIGHT = 12;
    @Unique
    private static final ResourceLocation MILF_GUI = ArsNouveau.prefix("textures/gui/crafting_terminal.png");
    @Unique
    private static final ResourceLocation MILF_GUI_EXPANDED = ArsNouveau.prefix("textures/gui/crafting_terminal_expanded.png");

    @Shadow
    protected NoShadowTextField searchField;
    @Shadow
    protected boolean expanded;
    @Shadow
    protected TerminalScrollSlider scrollSlider;
    @Shadow
    protected int searchType;
    @Shadow
    private String searchLast;

    @Unique
    private int milf$extraRows;
    @Unique
    private boolean milf$pulledFromViewer;
    @Unique
    private boolean milf$storedSearchPending;

    public ArsCraftingTerminalScreenMixin(CraftingTerminalMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;init()V",
                    shift = At.Shift.AFTER
            )
    )
    private void milf$fitExtraRows(CallbackInfo ci) {
        int room = this.height - this.imageHeight - 2;
        this.milf$extraRows = Math.max(0, Math.min(MILF_MAX_EXTRA_ROWS, room / MILF_ROW_HEIGHT));
        this.topPos += this.milf$extraRows * MILF_ROW_HEIGHT / 2;
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void milf$layoutExtraRows(CallbackInfo ci) {
        this.searchField.setY(this.topPos - this.milf$extraRows * MILF_ROW_HEIGHT + 6);
        this.milf$applyGridLayout();
        this.milf$layoutSlider();
        if (Config.ARS_LECTERN_DISABLE_SEARCH_AUTOFOCUS.get()) {
            this.setFocused(null);
        }
    }

    @ModifyVariable(method = "init", at = @At("STORE"), name = "recipeButtonY")
    private int milf$shiftRecipeButtons(int recipeButtonY) {
        return recipeButtonY + this.milf$extraRows * MILF_ROW_HEIGHT / 2;
    }

    @ModifyVariable(method = "init", at = @At("STORE"), name = "collapseButtonY")
    private int milf$shiftCollapseButton(int collapseButtonY) {
        return collapseButtonY + this.milf$extraRows * MILF_ROW_HEIGHT / 2;
    }

    @Inject(method = "onExpandedChanged(Z)V", at = @At("TAIL"))
    private void milf$relayoutOnExpand(boolean expanded, CallbackInfo ci) {
        this.milf$applyGridLayout();
        this.milf$layoutSlider();
    }

    @Unique
    private void milf$layoutSlider() {
        if (this.milf$extraRows <= 0) {
            return;
        }
        int firstRowY = MILF_HEADER_HEIGHT - this.milf$extraRows * MILF_ROW_HEIGHT;
        this.scrollSlider.setY(this.topPos + firstRowY - 3);
        this.scrollSlider.setHeight(this.milf$visibleRows() * MILF_ROW_HEIGHT);
    }

    @Unique
    private void milf$renderScrollGroove(GuiGraphics graphics, ResourceLocation texture) {
        if (this.milf$extraRows <= 0) {
            return;
        }
        int firstRowY = MILF_HEADER_HEIGHT - this.milf$extraRows * MILF_ROW_HEIGHT;
        int top = this.topPos + firstRowY - 7;
        int height = this.milf$visibleRows() * MILF_ROW_HEIGHT + 7;
        graphics.blit(texture, this.leftPos + MILF_GROOVE_X, top, MILF_GROOVE_X, MILF_GROOVE_CAP_V, MILF_GROOVE_WIDTH, MILF_GROOVE_CAP_HEIGHT);
        int y = top + MILF_GROOVE_CAP_HEIGHT;
        int end = top + height - MILF_GROOVE_BOTTOM_HEIGHT;
        while (y < end) {
            int slice = Math.min(MILF_ROW_HEIGHT, end - y);
            graphics.blit(texture, this.leftPos + MILF_GROOVE_X, y, MILF_GROOVE_X, MILF_GROOVE_MIDDLE_V, MILF_GROOVE_WIDTH, slice);
            y += slice;
        }
        graphics.blit(texture, this.leftPos + MILF_GROOVE_X, top + height - MILF_GROOVE_BOTTOM_HEIGHT, MILF_GROOVE_X, MILF_GROOVE_BOTTOM_V, MILF_GROOVE_WIDTH, MILF_GROOVE_BOTTOM_HEIGHT);
    }

    @Unique
    private void milf$applyGridLayout() {
        List<SlotStorage> slots = ((CraftingTerminalMenuAccessor) this.menu).milf$storageSlotList();
        int visibleRows = this.milf$visibleRows();
        int firstRowY = MILF_HEADER_HEIGHT - this.milf$extraRows * MILF_ROW_HEIGHT;
        for (int i = 0; i < slots.size(); i++) {
            SlotStorage slot = slots.get(i);
            slot.yDisplayPosition = firstRowY + (i / 9) * MILF_ROW_HEIGHT;
            slot.show = i < visibleRows * 9;
        }
    }

    @Unique
    private int milf$visibleRows() {
        return (this.expanded ? 7 : 3) + this.milf$extraRows;
    }

    @ModifyConstant(
            method = "scrollTo(I)V",
            constant = {@Constant(intValue = 7), @Constant(intValue = 3)}
    )
    private int milf$scrollRowCount(int original) {
        return original == 7 ? 7 + this.milf$extraRows : 3 + this.milf$extraRows;
    }

    @ModifyConstant(
            method = "updateSearch",
            constant = {@Constant(intValue = 7), @Constant(intValue = 3)}
    )
    private int milf$maxScrollRows(int original) {
        return original == 7 ? 7 + this.milf$extraRows : 3 + this.milf$extraRows;
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void milf$renderExtraRows(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.milf$extraRows <= 0) {
            return;
        }
        ResourceLocation texture = this.expanded ? MILF_GUI_EXPANDED : MILF_GUI;
        int firstRowY = MILF_HEADER_HEIGHT - this.milf$extraRows * MILF_ROW_HEIGHT;
        int stripTop = this.topPos + firstRowY - MILF_HEADER_HEIGHT;
        graphics.blit(texture, this.leftPos, stripTop, 0, 0, this.imageWidth, MILF_HEADER_HEIGHT);
        for (int row = 0; row < this.milf$extraRows; row++) {
            graphics.blit(MILF_GUI_EXPANDED, this.leftPos, this.topPos + firstRowY + row * MILF_ROW_HEIGHT, 0, MILF_PLAIN_ROW_V, this.imageWidth, MILF_ROW_HEIGHT);
        }
        this.milf$renderScrollGroove(graphics, texture);
        DocClientUtils.blit(graphics, DocAssets.STORAGE_SEARCH_PAPER, this.leftPos + 102, stripTop + 3);
    }

    @Inject(method = "hasClickedOutside", at = @At("RETURN"), cancellable = true)
    private void milf$clickAbovePanel(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && this.milf$extraRows > 0) {
            int stripTop = guiTopIn - this.milf$extraRows * MILF_ROW_HEIGHT;
            cir.setReturnValue(mouseX < guiLeftIn || mouseY < stripTop || mouseX >= guiLeftIn + this.imageWidth || mouseY >= guiTopIn + this.imageHeight);
        }
    }

    @Inject(method = "containerTick", at = @At("HEAD"))
    private void milf$pullEmiSearch(CallbackInfo ci) {
        if (this.searchType != 1) {
            return;
        }
        String viewer = EmiApi.getSearchText();
        String current = this.searchField.getValue().trim();
        if (!this.milf$storedSearchPending && !this.searchLast.equals(current)) {
            return;
        }
        this.milf$storedSearchPending = false;
        if (viewer.trim().equals(current)) {
            return;
        }
        this.searchField.setValue(viewer);
        this.milf$pulledFromViewer = true;
    }

    @Redirect(
            method = "updateSearch",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/hollingsworth/arsnouveau/client/container/IAutoFillTerminal;sync(Ljava/lang/String;)V"
            )
    )
    private void milf$skipViewerEcho(String searchString) {
        if (!this.milf$pulledFromViewer) {
            IAutoFillTerminal.sync(searchString);
            return;
        }
        for (IAutoFillTerminal.ISearchHandler handler : IAutoFillTerminal.updateSearch) {
            String viewer = handler.getSearch();
            if (viewer == null || !viewer.trim().equals(searchString)) {
                handler.setSearch(searchString);
            }
        }
    }

    @Inject(method = "updateSearch", at = @At("TAIL"))
    private void milf$clearPullFlag(CallbackInfo ci) {
        this.milf$pulledFromViewer = false;
    }

    @Inject(
            method = "onPacket",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/hollingsworth/arsnouveau/client/gui/NoShadowTextField;setValue(Ljava/lang/String;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void milf$noteStoredSearch(CallbackInfo ci) {
        this.milf$storedSearchPending = true;
    }

    @ModifyArg(
            method = "onPacket",
            at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/client/gui/NoShadowTextField;setFocused(Z)V")
    )
    private boolean milf$respectAutofocusConfig(boolean focused) {
        return !Config.ARS_LECTERN_DISABLE_SEARCH_AUTOFOCUS.get() && focused;
    }

    @Inject(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/hollingsworth/arsnouveau/client/container/CraftingTerminalScreen;clearFocus()V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void milf$keepKeysOutOfSearch(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.ARS_LECTERN_DISABLE_SEARCH_AUTOFOCUS.get() || this.searchField.isFocused()) {
            return;
        }
        if (Config.ARS_LECTERN_CLOSE_ON_E.get() && keyCode == GLFW.GLFW_KEY_E) {
            this.onClose();
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "charTyped",
            at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/client/container/CraftingTerminalScreen;clearFocus()V"),
            cancellable = true
    )
    private void milf$keepTypingOutOfSearch(char codePoint, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (Config.ARS_LECTERN_DISABLE_SEARCH_AUTOFOCUS.get() && !this.searchField.isFocused()) {
            cir.setReturnValue(false);
        }
    }
}
