package com.minhtyfresh.atmosphere.client;

public class AtmosphereClientDataManager {
    private static AtmosphereClientDataManager INSTANCE = new AtmosphereClientDataManager();

    public boolean isFoggy = false; // todo mqd properly initialize this?
    public float fogLevel = 0.0f;

    private AtmosphereClientDataManager() {
    }

    public static synchronized AtmosphereClientDataManager getInstance() {
        return INSTANCE;
    }
}
