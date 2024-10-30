package com.minhtyfresh.atmosphere;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Atmosphere implements ModInitializer {

    /* - Identifiers */

    /**
     * This is the mod's unique identifier. This should never change. If a change is required, then it is important that
     * mod developers using our API are properly informed of the change.
     */
    public static final String MOD_ID = "atmosphere";

    /**
     * This is the mod's display name. This can change, but should not be required since it closely resembles the mod's
     * unique identifier.
     */
    public static final String MOD_NAME = "Atmosphere";

    /**
     * This is a unique logger instance. It will change the output visible in the debugging console and in a player's
     * runtime console.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
    }
}
