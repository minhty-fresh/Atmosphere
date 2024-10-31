package com.minhtyfresh.atmosphere.mixin.client.cloudlayers;

import com.minhtyfresh.atmosphere.helper.cloudlayers.ConditionsExtended;
import mod.lwhrvw.cloud_layers.config.CloudLayersConfig;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = CloudLayersConfig.class, remap = false)
public abstract class CloudLayersConfigMixin {

    @Shadow public List<CloudLayersConfig.ConfigurableLayer> layers;

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lmod/lwhrvw/cloud_layers/config/CloudLayersConfig;layers:Ljava/util/List;", opcode = Opcodes.PUTFIELD))
    public void overrideLayers_constructor(CloudLayersConfig instance, List<CloudLayersConfig.ConfigurableLayer> layersList) {
//        layersList.add(
//        new CloudLayersConfig.ConfigurableLayer(
//                "CLOUD ONLY TEST",
//                13L,
//                128,
//                new CloudLayersConfig.Appearance(20, 24, 16777215, 80),
//                new CloudLayersConfig.Movement(100, CloudLayersConfig.Direction.WEST, false),
//                new ConditionsExtended("minecraft:overworld", false, true)));
        instance.layers = new ArrayList<>();
    }
}
