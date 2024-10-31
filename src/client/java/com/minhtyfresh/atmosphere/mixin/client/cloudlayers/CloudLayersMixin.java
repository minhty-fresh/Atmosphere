package com.minhtyfresh.atmosphere.mixin.client.cloudlayers;

import com.minhtyfresh.atmosphere.helper.cloudlayers.ConditionsExtended;
import com.minhtyfresh.atmosphere.helper.cloudlayers.LayerExtendedConditions;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.lwhrvw.cloud_layers.CloudLayers;
import mod.lwhrvw.cloud_layers.Layer;
import mod.lwhrvw.cloud_layers.config.CloudLayersConfig;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static mod.lwhrvw.cloud_layers.CloudLayers.*;

@Mixin(value = CloudLayers.class, remap = false)
public abstract class CloudLayersMixin {

    @Shadow private static ArrayList<Layer> layers;

    // Override Cloud Layers loading to use Atmosphere's defined clouds instead
    @Inject(method = "loadLayers", at = @At("HEAD"), cancellable = true)
    private static void atmosphereOverride$loadLayers(CallbackInfo ci) {
        layers.clear();
        List<CloudLayersConfig.ConfigurableLayer> configurableLayers = List.of(
                new CloudLayersConfig.ConfigurableLayer(
                        "Default Lower",
                        11L,
                        180,
                        new CloudLayersConfig.Appearance(4, 12, 16777215, 80),
                        new CloudLayersConfig.Movement(100, CloudLayersConfig.Direction.WEST, false),
                        new CloudLayersConfig.Conditions("minecraft:overworld", false)),
                new CloudLayersConfig.ConfigurableLayer(
                        "Default Upper",
                        12L,
                        256,
                        new CloudLayersConfig.Appearance(0, 12, 16777215, 80),
                        new CloudLayersConfig.Movement(80, CloudLayersConfig.Direction.NORTH, false),
                        new CloudLayersConfig.Conditions("minecraft:overworld", false)),
                new CloudLayersConfig.ConfigurableLayer(
                        "Default Upper",
                        12L,
                        132,
                        new CloudLayersConfig.Appearance(20, 24, 16777215, 80),
                        new CloudLayersConfig.Movement(80, CloudLayersConfig.Direction.NORTH, false),
                        new ConditionsExtended("minecraft:overworld", false, true)));

        for (CloudLayersConfig.ConfigurableLayer configurableLayer : configurableLayers) {
            layers.add(configurableLayer.genLayer(CONFIG.heightOffset));
        }

        ci.cancel();
    }

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    private static void atmosphereOverride$renderClouds(LevelRenderer worldRenderer, PoseStack matrixStack, Matrix4f matrix1, Matrix4f matrix2, float tickDelta, double x, double y, double z, CallbackInfo ci) {
        Vec3 cloudColor = MC.level.getCloudColor(tickDelta);
        Vec3 skyColor = MC.level.getSkyColor(new Vec3(x, y, z), tickDelta);

        layers.forEach((layer) -> {
            LayerExtendedConditions layerExtendedConditions = (LayerExtendedConditions) layer;
            layerExtendedConditions.atmosphere$updateTransitionFactor(layer.shouldRender(y) ? 0.0005d * tickDelta : -0.0005d * tickDelta);
            if (layerExtendedConditions.atmosphere$getTransitionFactor() > 0.0d) {
                layer.render(matrixStack, matrix1, matrix2, getWorldTime(), tickDelta, x, y, z, cloudColor.lerp(skyColor, 1-layerExtendedConditions.atmosphere$getTransitionFactor()));
            }
        });
        ci.cancel();
    }
}
