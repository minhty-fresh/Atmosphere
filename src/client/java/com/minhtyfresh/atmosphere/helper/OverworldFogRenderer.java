package com.minhtyfresh.atmosphere.helper;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class OverworldFogRenderer {
    public static float getFarPlaneRenderDistance() {
        return Minecraft.getInstance().options.getEffectiveRenderDistance() * 16;
    }

    public static boolean overrideFog(
            Consumer<Float> fogStartSetter,
            Consumer<Float> fogEndSetter)
    {

        // todo figure out how to disable the effect when underground - hook into Fog mod?
        // todo set up fog weather which will trigger the fog effect
        // todo make fog transition smooth
        // different intensities of fog weather, or a duration, and have the fog start far and then push in and then push out again

        fogStartSetter.accept(0f);
        fogEndSetter.accept(0.1f * getFarPlaneRenderDistance());

        return true;
    }

    public static boolean setupFog(
            Camera camera,
            FogRenderer.FogMode fogMode,
            Supplier<Float> fogStart,
            Supplier<Float> fogEnd,
            Consumer<FogShape> fogShapeSetter,
            Consumer<Float> fogStartSetter,
            Consumer<Float> fogEndSetter)
    {
        return overrideFog(fogStartSetter, fogEndSetter);
    }
}
