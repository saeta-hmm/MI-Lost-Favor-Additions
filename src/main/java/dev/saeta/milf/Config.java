package dev.saeta.milf;

import net.neoforged.neoforge.common.ModConfigSpec;


public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ARS_LECTERN_DISABLE_SEARCH_AUTOFOCUS = BUILDER
            .comment("Open the Ars Nouveau storage lectern without focusing its search box.")
            .define("arsLecternDisableSearchAutofocus", true);

    public static final ModConfigSpec.BooleanValue ARS_LECTERN_CLOSE_ON_E = BUILDER
            .comment("Close the Ars Nouveau storage lectern with E while its search box is not focused.")
            .define("arsLecternCloseOnE", true);

    static final ModConfigSpec SPEC = BUILDER.build();

}
