package com.minhtyfresh.atmosphere.mixin.client;

import com.minhtyfresh.atmosphere.helper.LightLevelHelper;
import com.minhtyfresh.atmosphere.mixin.client.helper.LevelInvoker;
import com.minhtyfresh.atmosphere.mixin.client.helper.LevelTimeAccessInvoker;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements LevelInvoker, LevelTimeAccessInvoker {
    @Inject(
            method = "getSkyDarken",
            at = @At("HEAD"),
            cancellable = true
    )
    // This affects only the internal sky level
    // https://minecraft.wiki/w/Light#Internal_light_level
    public void getSkyDarken(float g, CallbackInfoReturnable<Float> cir) {
        float skyDarkenPercentage = LightLevelHelper.getSkyDarkenPercentage(
                () -> this.invokeGetTimeOfDay(g),
                () -> this.invokeGetRainLevel(g),
                () -> this.invokeGetThunderLevel(g),
                this::invokeGetMoonPhase);

        float lightPercentage = skyDarkenPercentage * 0.8f + 0.2f; // 0.2f is minimum light percentage level at night (4 light level)
        cir.setReturnValue(lightPercentage);
    }
}
