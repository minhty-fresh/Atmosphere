package com.minhtyfresh.atmosphere;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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

    @Override
    public @NotNull CompoundTag save(CompoundTag nbt) {
        nbt.putBoolean("isfoggy", this.isFoggy);
        nbt.putInt("fogtime", this.fogTime);
        return nbt;
    }

    public static WeatherData get(Level world) {
        if (world instanceof ServerLevel) {
            DimensionDataStorage dataStorage = world.getServer().getLevel(Level.OVERWORLD).getDataStorage();
            return dataStorage.computeIfAbsent(WeatherData::load, WeatherData::new, NAME);
        } else {
            throw new RuntimeException("Game attempted to load server-side weather data from a client-side world.");
        }
    }

    public static WeatherData load(CompoundTag nbt) {
        WeatherData data = new WeatherData();
        data.setIsFoggy(nbt.getBoolean("isfoggy"));
        data.setFogTime(nbt.getInt("fogtime"));
        Atmosphere.LOGGER.info("Weather data loaded successfully. isFoggy={}, fogTime={}", data.isFoggy, data.fogTime);
        return data;
    }

    public void setIsFoggy(boolean isFoggy) {
        this.isFoggy = isFoggy;
    }

    public void setFogTime(int fogTime) {
        this.fogTime = fogTime;
    }

    public void setFogLevel(float FogLevel) {
        this.fogLevel = fogLevel;
    }

    public void setOldFogLevel(float oldFogLevel) {
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
