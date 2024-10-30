package com.minhtyfresh.atmosphere.event;

import com.minhtyfresh.atmosphere.Atmosphere;
import net.minecraft.resources.ResourceLocation;

public final class WeatherEvents {
    public static ResourceLocation FOG_START_PACKET_ID = ResourceLocation.tryBuild(Atmosphere.MOD_ID, "fog_weather_start");
    public static ResourceLocation FOG_END_PACKET_ID = ResourceLocation.tryBuild(Atmosphere.MOD_ID, "fog_weather_end");
    public static ResourceLocation FOG_LEVEL_CHANGE_PACKET_ID = ResourceLocation.tryBuild(Atmosphere.MOD_ID, "fog_level_change");
}
