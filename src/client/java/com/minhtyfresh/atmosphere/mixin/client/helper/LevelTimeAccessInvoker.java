package com.minhtyfresh.atmosphere.mixin.client.helper;

import net.minecraft.world.level.LevelTimeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelTimeAccess.class)
public interface LevelTimeAccessInvoker {
    @Invoker("getTimeOfDay")
    abstract public float invokeGetTimeOfDay(float f);

    @Invoker("getMoonPhase")
    abstract public int invokeGetMoonPhase();
}