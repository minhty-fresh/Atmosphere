package com.minhtyfresh.atmosphere.mixin.client.cloudlayers;

import com.llamalad7.mixinextras.sugar.Local;
import com.minhtyfresh.atmosphere.helper.cloudlayers.ConditionsExtended;
import com.minhtyfresh.atmosphere.helper.cloudlayers.LayerExtendedConditions;
import mod.lwhrvw.cloud_layers.Layer;
import mod.lwhrvw.cloud_layers.config.CloudLayersConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CloudLayersConfig.ConfigurableLayer.class, remap = false)
public abstract class ConfigurableLayerMixin {
    @Shadow public CloudLayersConfig.Conditions conditions;

    @Redirect(method = "genLayer", at = @At(value = "INVOKE", target = "Lmod/lwhrvw/cloud_layers/Layer;setConditions(Ljava/lang/String;Z)V"))
    public void overrideSetConditions_genLayer(Layer instance, String dimension, boolean rainingOnly, @Local Layer layer) {
        boolean fogOnly = (conditions instanceof ConditionsExtended) ? ((ConditionsExtended)this.conditions).fogOnly : false;
        ((LayerExtendedConditions)layer).atmosphere$setConditions(dimension, rainingOnly, fogOnly);
    }
}
