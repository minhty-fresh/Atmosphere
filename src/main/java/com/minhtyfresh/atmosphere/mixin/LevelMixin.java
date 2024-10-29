package com.minhtyfresh.atmosphere.mixin;

import com.minhtyfresh.atmosphere.helper.LightLevelHelper;
import com.minhtyfresh.atmosphere.mixin.helper.LevelAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor {
    @Inject(method = "updateSkyBrightness", at = @At(value = "HEAD"), cancellable = true)
    public void updateSkyBrightness(CallbackInfo ci) {
        Level level = (Level)(Object)this;
        double d = 1.0 - (double)(level.getRainLevel(1.0F) * LightLevelHelper.RAIN_LIGHT_DAMPEN_LEVEL) / 16.0;
        double e = 1.0 - (double)(level.getThunderLevel(1.0F) * LightLevelHelper.THUNDER_LIGHT_DAMPEN_LEVEL) / 16.0;
        double f = 0.5 + 2.0 * Mth.clamp((double)Mth.cos(level.getTimeOfDay(1.0F) * (float) (Math.PI * 2)), -0.25, 0.25);
        this.setSkyDarken((int)((1.0 - f * d * e) * 11.0));
        ci.cancel();
    }
}
