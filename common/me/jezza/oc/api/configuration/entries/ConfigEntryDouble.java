package me.jezza.oc.api.configuration.entries;

import me.jezza.oc.api.configuration.Config.ConfigDouble;
import me.jezza.oc.api.configuration.ConfigEntry;
import net.minecraftforge.common.config.Configuration;

public class ConfigEntryDouble extends ConfigEntry<ConfigDouble, Double> {
    @Override
    public Object processAnnotation(Configuration config, String fieldName, ConfigDouble annotation, Double defaultValue) {
        return config.get(annotation.category(), fieldName, defaultValue, annotation.comment(), annotation.minValue(), annotation.maxValue()).getDouble();
    }
}
