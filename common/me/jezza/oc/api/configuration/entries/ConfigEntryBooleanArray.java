package me.jezza.oc.api.configuration.entries;

import me.jezza.oc.api.configuration.Config.ConfigBooleanArray;
import me.jezza.oc.api.configuration.ConfigEntry;
import net.minecraftforge.common.config.Configuration;

public class ConfigEntryBooleanArray extends ConfigEntry<ConfigBooleanArray, boolean[]> {
    @Override
    public Object processAnnotation(Configuration config, String fieldName, ConfigBooleanArray annotation, boolean[] defaultValue) {
        return config.get(annotation.category(), fieldName, defaultValue, annotation.comment(), annotation.isListLengthFixed(), annotation.maxListLength()).getBooleanList();
    }
}
