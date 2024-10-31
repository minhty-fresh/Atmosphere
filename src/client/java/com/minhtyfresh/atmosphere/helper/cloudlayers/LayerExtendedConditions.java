package com.minhtyfresh.atmosphere.helper.cloudlayers;

public interface LayerExtendedConditions {

    public void atmosphere$setFogOnly(boolean fogOnly);
    public void atmosphere$setConditions(String dimension, boolean rainingOnly, boolean fogOnly);

    public void atmosphere$updateTransitionFactor(double transitionAmount);

    public double atmosphere$getTransitionFactor();
}
