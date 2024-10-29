package com.minhtyfresh.atmosphere.mixin;

import com.minhtyfresh.atmosphere.WeatherData;
import com.minhtyfresh.atmosphere.event.WeatherEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Shadow public abstract List<ServerPlayer> players();

    @Inject(method = "advanceWeatherCycle", at = @At("RETURN"))
    private void advanceWeatherCycle(CallbackInfo ci) {
        ServerLevel level = (ServerLevel)(Object)this;
        if (level.dimensionType().hasSkyLight()) {
            if (level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
                WeatherData weatherData = WeatherData.get(level);
                weatherData.setIsFoggy(true);
                weatherData.setFogTime(1000);

                level.getServer().execute(() -> {
                    FriendlyByteBuf data = PacketByteBufs.create();
                    level.getServer().getPlayerList().getPlayers().forEach((player) -> {
                        ServerPlayNetworking.send(player, WeatherEvents.FOG_START_PACKET_ID, data);
                    });
                });
            }
        }
    }
}
