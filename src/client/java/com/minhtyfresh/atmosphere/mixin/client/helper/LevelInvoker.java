package com.minhtyfresh.atmosphere.mixin.client.helper;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Level.class)
public interface LevelInvoker {
    @Invoker("getRainLevel")
    abstract public float invokeGetRainLevel(float f);

    @Invoker("getThunderLevel")
    abstract public float invokeGetThunderLevel(float f);
}
