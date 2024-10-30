package com.minhtyfresh.atmosphere.mixin.client;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.minhtyfresh.atmosphere.WeatherData;
import com.minhtyfresh.atmosphere.helper.OverworldFogRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {



    @Inject(
            method = "setupFog",
            at = @At("RETURN")
    )
    private static void overrideFog_setupFog(
            Camera camera,
            FogRenderer.FogMode fogMode,
            float farPlaneDistance,
            boolean levelOrSpecialFog,
            float partialTick,
            CallbackInfo ci,
            @Share("hasMobEffectFog") LocalBooleanRef hasMobEffectFogRef
    )
    {
        // todo set up config to disable
        WeatherData weatherData = WeatherData.get(Minecraft.getInstance().level);
        if (weatherData.getFogLevel() > 0.0f
                && camera.getFluidInCamera() == FogType.NONE // not in liquid
                && !levelOrSpecialFog                        // don't override nether fog and other special fog conditions
                && !hasMobEffectFogRef.get()                 // don't override mob effect fog functions like blindness
        ) {
            OverworldFogRenderer.overrideFog(RenderSystem::setShaderFogStart, RenderSystem::setShaderFogEnd);
        }
    }

    @Inject(method = "setupFog",
            at = @At(value = "HEAD"))
    private static void setComparisonMobEffectFogInitial_setupFog(
            Camera camera,
            FogRenderer.FogMode fogMode,
            float farPlaneDistance,
            boolean levelOrSpecialFog,
            float partialTick,
            CallbackInfo ci,
            @Share("hasMobEffectFog") LocalBooleanRef hasMobEffectFogRef) {
        hasMobEffectFogRef.set(false);
    }


    @Inject(method = "setupFog",
    at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/FogRenderer$MobEffectFogFunction;setupFog(Lnet/minecraft/client/renderer/FogRenderer$FogData;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/effect/MobEffectInstance;FF)V",
            shift = At.Shift.AFTER))
    private static void setMobEffectTrue_setupFog(
            Camera camera,
            FogRenderer.FogMode fogMode,
            float farPlaneDistance,
            boolean levelOrSpecialFog,
            float partialTick,
            CallbackInfo ci,
            @Share("hasMobEffectFog") LocalBooleanRef hasMobEffectFogRef) {
        hasMobEffectFogRef.set(true);
    }
}