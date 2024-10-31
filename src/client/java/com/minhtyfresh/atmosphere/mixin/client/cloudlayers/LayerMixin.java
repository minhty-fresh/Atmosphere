package com.minhtyfresh.atmosphere.mixin.client.cloudlayers;

import com.minhtyfresh.atmosphere.WeatherData;
import com.minhtyfresh.atmosphere.helper.cloudlayers.LayerExtendedConditions;
import mod.lwhrvw.cloud_layers.CloudLayers;
import mod.lwhrvw.cloud_layers.Layer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Layer.class, remap = false)
public abstract class LayerMixin implements LayerExtendedConditions {
    @Shadow private boolean relativeHeight;
    @Shadow private float height;
    @Shadow private boolean dimensionOnly;
    @Shadow private ResourceKey<Level> dimension;
    @Shadow private boolean rainingOnly;

    @Unique
    private double atmosphere$transitionFactor;

    @Shadow public abstract void setConditions(String dimension, boolean rainingOnly);

    @Shadow private double opacity;
    @Unique
    private boolean atmosphere$fogOnly;
    @Unique
    private boolean atmosphere$firstRender = true;

    @Inject( method = "<init>(JF)V", at = @At("TAIL"))
    private void constructorTail(long seed, float height, CallbackInfo ci) {
        this.atmosphere$fogOnly = false;
    }

//    public void atmosphere$setFogOnly(boolean fogOnly) {
//        this.atmosphere$fogOnly = fogOnly;
//    }

    public void atmosphere$setConditions(String dimension, boolean rainingOnly, boolean fogOnly) {
        setConditions(dimension, rainingOnly);
        this.atmosphere$fogOnly = fogOnly;
    }

    @Inject(method = "shouldRender", at = @At(value = "HEAD"), cancellable = true)
    public void shouldRender(double cameraY, CallbackInfoReturnable<Boolean> cir) {
        boolean shouldRender;
        if (!this.relativeHeight && Math.abs((double)this.height - cameraY) > (double)(CloudLayers.getRenderDistance() * 16) * 1.1) {
            shouldRender = false;
        } else if (this.dimensionOnly && !CloudLayers.isInDimension(this.dimension)) {
            shouldRender = false;
            this.atmosphere$transitionFactor = 0; // no transitioning for different dimensions
            // TODO figure out how transitions should work for going back to the correct dimension
            // maybe don't adjust transition factor at all, have a softRender and hardRender condition returned
        } else {
            boolean rainConditionMet = !this.rainingOnly || (double)CloudLayers.getRain(this.height) > 0.0;
            WeatherData weatherData = WeatherData.get(Minecraft.getInstance().level);
            boolean fogConditionMet = !this.atmosphere$fogOnly || weatherData.getFogLevel() > 0.0f;
            shouldRender = rainConditionMet && fogConditionMet;
        }

        // initial transition factor when first rendering should be 1 if the clouds should render under current conditions
        // so that on load in, clouds are immediately fully rendered
//        if (this.atmosphere$firstRender) {
//            this.atmosphere$transitionFactor = shouldRender ? 1.0d : 0.0d;
//            this.atmosphere$firstRender = false;
//        }
        cir.setReturnValue(shouldRender);
    }

    public void atmosphere$updateTransitionFactor(double transitionAmount) {
        this.atmosphere$transitionFactor += transitionAmount;
        this.atmosphere$transitionFactor = Mth.clamp(this.atmosphere$transitionFactor, 0.0f, 1.0f);
    }

    public double atmosphere$getTransitionFactor() {
        return this.atmosphere$transitionFactor;
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lmod/lwhrvw/cloud_layers/Layer;opacity:D", opcode = Opcodes.GETFIELD))
    public double overrideGetOpacity(Layer instance) {
        return this.opacity * this.atmosphere$transitionFactor;
    }
}
