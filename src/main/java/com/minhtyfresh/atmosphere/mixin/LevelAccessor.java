package com.minhtyfresh.atmosphere.mixin;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Level.class)
public interface LevelAccessor {
    @Accessor("skyDarken")
    public void setSkyDarken(int skyDarken);
}
