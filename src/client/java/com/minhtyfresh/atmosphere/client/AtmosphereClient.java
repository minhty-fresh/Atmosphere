package com.minhtyfresh.atmosphere.client;

import com.minhtyfresh.atmosphere.event.WeatherEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class AtmosphereClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(WeatherEvents.FOG_START_PACKET_ID, ((client, handler, buf, responseSender) -> {

            AtmosphereClientDataManager.getInstance().isFoggy = true;
            client.execute(() -> {
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(WeatherEvents.FOG_END_PACKET_ID, ((client, handler, buf, responseSender) -> {

            AtmosphereClientDataManager.getInstance().isFoggy = false;
            client.execute(() -> {
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(WeatherEvents.FOG_LEVEL_CHANGE_PACKET_ID, ((client, handler, buf, responseSender) -> {
            AtmosphereClientDataManager.getInstance().fogLevel = buf.readFloat();
            client.execute(() -> {
            });
        }));
    }
}
