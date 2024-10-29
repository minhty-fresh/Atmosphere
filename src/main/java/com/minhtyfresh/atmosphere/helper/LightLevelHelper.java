package com.minhtyfresh.atmosphere.helper;

import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class LightLevelHelper {
    // TODO make this configurable
    public static float RAIN_LIGHT_DAMPEN_LEVEL = 10.0f; // vanilla is 5.0f, possible range is 0->16
    public static float THUNDER_LIGHT_DAMPEN_LEVEL = 10.0f; // vanilla is 5.0f, possible range is 0->16

    public static float getSkyDarkenPercentage(
            Supplier<Float> timeOfDaySupplier,
            Supplier<Float> rainLevelSupplier,
            Supplier<Float> thunderLevelSupplier,
            Supplier<Integer> moonPhaseSupplier
    ) {
        // TODO add config option to enable brighter nights during full moon and 3/4 moon
        float maximumSkyDarkenPercentage = switch (moonPhaseSupplier.get()) {
            case (0) -> // full moon
                    15.0f / 16.0f;
//            case (1), (7) -> // 3/4 full moon
//                    15.0f / 16.0f;
            default -> 1.0f;
        };

        float darkenPercentage = 1.0f - Mth.clamp(
                1.0f - (Mth.cos(timeOfDaySupplier.get() * ((float)Math.PI * 2)) * 2.0f + 0.2f),
                0,
                maximumSkyDarkenPercentage);
        darkenPercentage *= 1.0f - rainLevelSupplier.get() * RAIN_LIGHT_DAMPEN_LEVEL / 16.0f;
        darkenPercentage *= 1.0f - thunderLevelSupplier.get() * THUNDER_LIGHT_DAMPEN_LEVEL / 16.0f;
        return darkenPercentage;
    }
}
