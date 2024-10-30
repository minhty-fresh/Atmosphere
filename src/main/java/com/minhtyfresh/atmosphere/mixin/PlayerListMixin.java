package com.minhtyfresh.atmosphere.mixin;

import com.minhtyfresh.atmosphere.WeatherData;
import com.minhtyfresh.atmosphere.event.WeatherEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject( method = "sendLevelInfo", at = @At(value = "TAIL"))
    public void sendLevelInfo(ServerPlayer player, ServerLevel level, CallbackInfo ci) {
        FriendlyByteBuf data = PacketByteBufs.create();
        data.writeFloat(WeatherData.get(level).getFogLevel());
        ServerPlayNetworking.send(player, WeatherEvents.FOG_LEVEL_CHANGE_PACKET_ID, data);
    }
}
