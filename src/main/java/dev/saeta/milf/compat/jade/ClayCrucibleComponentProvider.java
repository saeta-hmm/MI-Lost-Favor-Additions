package dev.saeta.milf.compat.jade;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.clay_crucible.ClayCrucibleBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.*;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class ClayCrucibleComponentProvider <S, C>
        implements IServerExtensionProvider<S>, IClientExtensionProvider<S, C> {

    @Override
    public ResourceLocation getUid() {
        return MILostFavor.locate("crucible");
    }

    public static final class Progress extends ClayCrucibleComponentProvider<CompoundTag, ProgressView> {
        @Override
        public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {

            if (accessor.getTarget() instanceof ClayCrucibleBlockEntity clayCrucibleBlockEntity) {
                float progress = clayCrucibleBlockEntity.getCurrentProgress();

                if (progress > 0f) {
                    var progressData = new ViewGroup<CompoundTag>(new ArrayList<>());

                    progressData.views.add(ProgressView.create(progress));
                    return List.of(progressData);
                }
            }
            return List.of();
        }

        @Override
        public List<ClientViewGroup<ProgressView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> list) {
            return ClientViewGroup.map(list, ProgressView::read, null);
        }
    }
}
