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
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    // todo make this configurable
    private static final IntProvider FOG_DURATION = UniformInt.of(10, 100);
    private static final IntProvider FOG_DELAY = UniformInt.of(10, 100);

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


    @Inject(method = "advanceWeatherCycle", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/ServerLevelData;getRainTime()I",
            shift = At.Shift.AFTER, by = 3))
    private void afterGetRainTime(CallbackInfo ci, @Local(ordinal=0) int clearWeatherTime) {
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
                fogTime = FOG_DURATION.sample(level.random);
            } else {
                fogTime = FOG_DELAY.sample(level.random);
            }
        }

        weatherData.setIsFoggy(isFoggy);
        weatherData.setFogTime(fogTime);
    }

    @Inject(method = "advanceWeatherCycle", at = @At(value = "HEAD"))
    private void beforeAdvanceWeatherCycle(CallbackInfo ci, @Share("wasFoggy") LocalBooleanRef wasFoggyRef) {
        ServerLevel level = (ServerLevel)(Object)this;
        WeatherData weatherData = WeatherData.get(level);

        wasFoggyRef.set(weatherData.getIsFoggy());
    }

    @Inject(method = "advanceWeatherCycle", at = @At(value = "TAIL"))
    private void afterAdvanceWeatherCycle(CallbackInfo ci, @Share("wasFoggy") LocalBooleanRef wasFoggyRef) {
        ServerLevel level = (ServerLevel)(Object)this;
        WeatherData weatherData = WeatherData.get(level);

        if (wasFoggyRef.get() != weatherData.getIsFoggy()) {
            if (wasFoggyRef.get()) {
                level.getServer().execute(() -> {
                    FriendlyByteBuf data = PacketByteBufs.create();
                    players().forEach((player) -> { // TODO filter by dimension
                        ServerPlayNetworking.send(player, WeatherEvents.FOG_END_PACKET_ID, data);
                    });
                });
            } else {
                level.getServer().execute(() -> {
                    FriendlyByteBuf data = PacketByteBufs.create();
                    players().forEach((player) -> { // TODO filter by dimension
                        ServerPlayNetworking.send(player, WeatherEvents.FOG_START_PACKET_ID, data);
                    });
                });
            }
        }
    }
}
