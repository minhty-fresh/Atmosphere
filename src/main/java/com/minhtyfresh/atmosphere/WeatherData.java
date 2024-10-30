package com.minhtyfresh.atmosphere;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

public class WeatherData extends SavedData {

    public static String NAME = "atmosphere_weather_data";
    private boolean isFoggy;
    private int fogTime;

    private float oldFogLevel;
    private float fogLevel;

    private static String KEY_IS_FOGGY = "KEY_IS_FOGGY";
    private static String KEY_FOG_TIME = "KEY_FOG_TIME";
    private static String KEY_OLD_FOG_LEVEL = "KEY_OLD_FOG_LEVEL";
    private static String KEY_FOG_LEVEL = "KEY_FOG_LEVEL";


    private static WeatherData clientCache = new WeatherData();
    private static Level clientLevelCache = null;

    @Override
    public @NotNull CompoundTag save(CompoundTag nbt) {
        nbt.putBoolean(KEY_IS_FOGGY, this.isFoggy);
        nbt.putInt(KEY_FOG_TIME, this.fogTime);
        nbt.putFloat(KEY_OLD_FOG_LEVEL, this.oldFogLevel);
        nbt.putFloat(KEY_FOG_LEVEL, this.fogLevel);
        return nbt;
    }

    public static WeatherData get(Level world) {
        if (world instanceof ServerLevel) {
            DimensionDataStorage dataStorage = ((ServerLevel)world).getDataStorage();
            WeatherData data = dataStorage.computeIfAbsent(WeatherData::load, WeatherData::new, NAME);
            data.setDirty();
            return data;
        } else {
            if (clientLevelCache != world) {
                clientLevelCache = world;
                clientCache = new WeatherData();
            }
            return clientCache;
        }
    }

    public static WeatherData load(CompoundTag nbt) {
        WeatherData data = new WeatherData();
        data.setIsFoggy(nbt.getBoolean(KEY_IS_FOGGY));
        data.setFogTime(nbt.getInt(KEY_FOG_TIME));
        data.setOldFogLevel(nbt.getInt(KEY_OLD_FOG_LEVEL));
        data.setFogLevel(nbt.getInt(KEY_FOG_LEVEL));
        Atmosphere.LOGGER.info("Weather data loaded successfully. isFoggy={}, fogTime={}, oldFogLevel={}, fogLevel={}",
                data.isFoggy, data.fogTime, data.oldFogLevel, data.fogLevel);
        return data;
    }

    public void setIsFoggy(boolean isFoggy) {
        this.isFoggy = isFoggy;
    }

    public void setFogTime(int fogTime) {
        this.fogTime = fogTime;
    }

    public void setFogLevel(float fogLevel) {
        fogLevel = Mth.clamp(fogLevel, 0.0F, 1.0F);
        this.fogLevel = fogLevel;
    }

    public void setOldFogLevel(float oldFogLevel) {
        oldFogLevel = Mth.clamp(oldFogLevel, 0.0F, 1.0F);
        this.oldFogLevel = oldFogLevel;
    }

    public boolean getIsFoggy() {
        return this.isFoggy;
    }

    public int getFogTime() {
        return this.fogTime;
    }

    public float getFogLevel() {
        return this.fogLevel;
    }

    public float getOldFogLevel() {
        return this.oldFogLevel;
    }
}
