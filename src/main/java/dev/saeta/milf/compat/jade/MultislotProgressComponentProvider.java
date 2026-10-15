package dev.saeta.milf.compat.jade;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.MultislotProgressProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.*;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class MultislotProgressComponentProvider<S, C>
        implements IServerExtensionProvider<S>, IClientExtensionProvider<S, C> {

    @Override
    public ResourceLocation getUid() {
        return MILostFavor.locate("progress");
    }

    public static final class Progress extends MultislotProgressComponentProvider<CompoundTag, ProgressView> {
        @Override
        public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {

            if (accessor.getTarget() instanceof MultislotProgressProvider progressProvider) {
                var progressData = new ViewGroup<CompoundTag>(new ArrayList<>());
                boolean isEmpty = true;
                for (int i = 0; i < progressProvider.getProgressSlots(); i++) {
                    float progress = progressProvider.getCurrentProgress(i);
                    if (progress > 0f) {
                        progressData.views.add(ProgressView.create(progress));
                        isEmpty = false;
                    }
                }

                return isEmpty ? List.of() : List.of(progressData);

            }

            return List.of();
        }

        @Override
        public List<ClientViewGroup<ProgressView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> list) {
            return ClientViewGroup.map(list, ProgressView::read, null);
        }
    }
}