package me.jezza.oc.api.configuration.entries;

import me.jezza.oc.api.configuration.Config.ConfigBooleanArray;
import me.jezza.oc.api.configuration.ConfigEntry;
import net.minecraftforge.common.config.Configuration;

public class ConfigEntryBooleanArray extends ConfigEntry<ConfigBooleanArray, boolean[]> {
    @Override
    public Object loadAnnotation(Configuration config, String fieldName, ConfigBooleanArray annotation, boolean[] currentValue, boolean[] defaultValue) {
        String comment = processComment(annotation.comment());
        return config.get(annotation.category(), fieldName, defaultValue, comment, annotation.isListLengthFixed(), annotation.maxListLength()).getBooleanList();
    }

    @Override
    public void saveAnnotation(Configuration config, String fieldName, ConfigBooleanArray annotation, boolean[] currentValue, boolean[] defaultValue) {
        String comment = processComment(annotation.comment());
        config.get(annotation.category(), fieldName, defaultValue, comment, annotation.isListLengthFixed(), annotation.maxListLength()).set(currentValue);
    }
}
