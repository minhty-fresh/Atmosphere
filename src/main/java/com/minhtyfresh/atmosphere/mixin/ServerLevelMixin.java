package com.minhtyfresh.atmosphere.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.minhtyfresh.atmosphere.WeatherData;
import com.minhtyfresh.atmosphere.event.WeatherEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    // todo make this configurable
    @Unique
    private static final IntProvider atmosphere$FOG_DURATION = UniformInt.of(100, 400);
    @Unique
    private static final IntProvider atmosphere$FOG_DELAY = UniformInt.of(100, 300);

    @Unique
    private static final float atmosphere$FOG_LEVEL_CHANGE_SPEED = 0.1f;
    @Shadow public abstract List<ServerPlayer> players();

//    @Inject(method = "advanceWeatherCycle", at = @At("RETURN"))
//    private void advanceWeatherCycle(CallbackInfo ci) {
//        ServerLevel level = (ServerLevel)(Object)this;
//        if (level.dimensionType().hasSkyLight()) {
//            if (level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
//                WeatherData weatherData = WeatherData.get(level);
//                weatherData.setIsFoggy(true);
//                weatherData.setFogTime(1000);
//
//                level.getServer().execute(() -> {
//                    FriendlyByteBuf data = PacketByteBufs.create();
//                    level.getServer().getPlayerList().getPlayers().forEach((player) -> {
//                        ServerPlayNetworking.send(player, WeatherEvents.FOG_START_PACKET_ID, data);
//                    });
//                });
//            }
//        }
//    }


    // Advance weather cycle for Atmosphere's weather
    @Inject(method = "advanceWeatherCycle", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/ServerLevelData;getRainTime()I",
            shift = At.Shift.AFTER, by = 3))
    private void advanceAtmosphereWeather_afterGetRainTime_advanceWeatherCycle(CallbackInfo ci, @Local(ordinal=0) int clearWeatherTime) {
        ServerLevel level = (ServerLevel)(Object)this;
        WeatherData weatherData = WeatherData.get(level);

        boolean isFoggy = weatherData.getIsFoggy();
        int fogTime = weatherData.getFogTime();
        if (clearWeatherTime > 0) {
            fogTime = isFoggy ? 0 : 1;
            isFoggy = false;
        } else {
            if (fogTime > 0) {
                if (--fogTime == 0) {
                    isFoggy = !isFoggy;
                }
            } else if (isFoggy) {
                fogTime = atmosphere$FOG_DURATION.sample(level.random);
            } else {
                fogTime = atmosphere$FOG_DELAY.sample(level.random);
            }
        }

        weatherData.setIsFoggy(isFoggy);
        weatherData.setFogTime(fogTime);

        weatherData.setOldFogLevel(weatherData.getFogLevel());
        if (weatherData.getIsFoggy()) {
            weatherData.setFogLevel(weatherData.getFogLevel()+ atmosphere$FOG_LEVEL_CHANGE_SPEED);
        } else {
            weatherData.setFogLevel(weatherData.getFogLevel()- atmosphere$FOG_LEVEL_CHANGE_SPEED);
        }
    }

    // TEST clamping rain to produce light rain
    // TODO REMOVE
    @Redirect(method = "advanceWeatherCycle", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float rainClamp_advanceWeatherCycle(float value, float min, float max) {
        return Mth.clamp(value, min, 1f);
        // return value;
    }

    // Set up initial weather conditions to check for changes afterward
    @Inject(method = "advanceWeatherCycle", at = @At(value = "HEAD"))
    private void setupComparison_before_AdvanceWeatherCycle(CallbackInfo ci, @Share("wasFoggy") LocalBooleanRef wasFoggyRef) {
        ServerLevel level = (ServerLevel)(Object)this;
        WeatherData weatherData = WeatherData.get(level);

        wasFoggyRef.set(weatherData.getIsFoggy());
    }

    // Send weather events to players if necessary
    @Inject(method = "advanceWeatherCycle", at = @At(value = "TAIL"))
    private void syncToClient_after_advanceWeatherCycle(CallbackInfo ci, @Share("wasFoggy") LocalBooleanRef wasFoggyRef) {
        ServerLevel level = (ServerLevel)(Object)this;
        WeatherData weatherData = WeatherData.get(level);

        if (weatherData.getOldFogLevel() != weatherData.getFogLevel()) {
            level.getServer().execute(() -> {
                FriendlyByteBuf data = PacketByteBufs.create();
                data.writeFloat(weatherData.getFogLevel()); // TODO mqd does this need to be per player?
                players().forEach((player) -> { // TODO filter by dimension?
                    ServerPlayNetworking.send(player, WeatherEvents.FOG_LEVEL_CHANGE_PACKET_ID, data);
                });
            });
        }

        if (wasFoggyRef.get() != weatherData.getIsFoggy()) {
            if (wasFoggyRef.get()) {
                level.getServer().execute(() -> {
                    FriendlyByteBuf data = PacketByteBufs.create();
                    players().forEach((player) -> { // TODO filter by dimension?
                        ServerPlayNetworking.send(player, WeatherEvents.FOG_END_PACKET_ID, data);
                    });
                });
            } else {
                level.getServer().execute(() -> {
                    FriendlyByteBuf data = PacketByteBufs.create();
                    players().forEach((player) -> { // TODO filter by dimension?
                        ServerPlayNetworking.send(player, WeatherEvents.FOG_START_PACKET_ID, data);
                    });
                });
            }
        }
    }

    // TODO what happens if i don't prepare the weather conditions?
//    // Prepare server-side weather conditions
//    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;prepareWeather()V"))
//    public void constructorAfterPrepareWeather(MinecraftServer server, Executor dispatcher, LevelStorageSource.LevelStorageAccess levelStorageAccess, ServerLevelData serverLevelData, ResourceKey dimension, LevelStem levelStem, ChunkProgressListener progressListener, boolean isDebug, long biomeZoomSeed, List customSpawners, boolean tickTime, RandomSequences randomSequences, CallbackInfo ci) {
//        ServerLevel level = (ServerLevel)(Object)this;
//        WeatherData weatherData = WeatherData.get(level);
//
//        if (weatherData.getIsFoggy()) {
//            weatherData.setFogLevel(1.0f);
//        }
//    }
}
