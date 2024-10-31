package com.minhtyfresh.atmosphere.helper.cloudlayers;

import mod.lwhrvw.cloud_layers.config.CloudLayersConfig;

public class ConditionsExtended extends CloudLayersConfig.Conditions {
    public boolean fogOnly;
    public ConditionsExtended(String dimension, boolean rainingOnly, boolean fogOnly) {
        super(dimension, rainingOnly);
        this.fogOnly = fogOnly;
    }
}
