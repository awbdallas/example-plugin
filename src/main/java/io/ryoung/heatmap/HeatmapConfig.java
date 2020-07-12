package io.ryoung.heatmap;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("heatmap")
public interface HeatmapConfig extends Config {
    enum DisplayModes {
        NONE,
        HA_VALUE,
        GE_VALUE
    }

    @ConfigItem(
            keyName = "displayOption",
            name = "Display Options",
            description = "Which mode would you like to display",
            position = 1
    )
    default DisplayModes displayModes() {
        return DisplayModes.NONE;
    }

    @ConfigItem(
            keyName = "useLogValues",
            name = "Use Log Normalization",
            description = "Use Log value normailization",
            position = 2
    )
    default boolean isUsingLogNormalization() {
        return false;
    }
}
