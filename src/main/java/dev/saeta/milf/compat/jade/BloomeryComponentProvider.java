package dev.saeta.milf.compat.jade;

import dev.saeta.milf.MILostFavor;
import dev.saeta.milf.blocks.bloomery.BloomeryBaseBlockEntity;
import dev.saeta.milf.blocks.fire_pit.FirePitBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.*;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class BloomeryComponentProvider <S, C>
        implements IServerExtensionProvider<S>, IClientExtensionProvider<S, C> {

    @Override
    public ResourceLocation getUid() {
        return MILostFavor.locate("bloomery");
    }

    public static final class Progress extends BloomeryComponentProvider<CompoundTag, ProgressView> {
        @Override
        public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {

            if (accessor.getTarget() instanceof BloomeryBaseBlockEntity bloomeryBaseBlockEntity) {
                float progress = bloomeryBaseBlockEntity.getCurrentProgress();

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
