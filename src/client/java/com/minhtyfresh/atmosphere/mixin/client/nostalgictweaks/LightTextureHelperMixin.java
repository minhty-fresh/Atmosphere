package com.minhtyfresh.atmosphere.mixin.client.nostalgictweaks;

import com.minhtyfresh.atmosphere.helper.LightLevelHelper;
import mod.adrenix.nostalgic.helper.candy.light.LightTextureHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightTextureHelper.class)
public abstract class LightTextureHelperMixin {

    @Inject(
            method = "getSkylightSubtracted",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void getSkylightSubtracted(ClientLevel level, CallbackInfoReturnable<Integer> cir) {
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }
        float darkenPercentage = 1 - LightLevelHelper.getSkyDarkenPercentage(
                () -> level.getTimeOfDay(1.0f),
                () -> level.getRainLevel(1.0f),
                () -> level.getThunderLevel(1.0f),
                level::getMoonPhase
        );
        cir.setReturnValue ((int) (darkenPercentage * 9.0f));
        // TODO double check this logic
        // maybe try the original logic of x 0.8, then + 0.2, then round? does vanilla round up or down? or nearest?
        // get the result of getSkyDarken which is a float [0.2,1] which represents light percentage
        // 0.2 -> 4 light level, 1 -> 15 LL
        // 0 -> 0, 1 -> 9 (max darkness that can be subtracted)
        // just multiply by 9? the value before it gets multiplied by 0.8 then .2 is added
    }
}
