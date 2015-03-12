package me.jezza.oc.api.configuration.discovery;

import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import me.jezza.oc.api.configuration.ConfigEntry;
import me.jezza.oc.common.core.CoreProperties;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A main config container for all the config annotations.
 * This includes all sub-packages.
 * Controlled by a ConfigData instance for a ModContainer.
 */
public class ConfigContainer {

    private Map<Class<? extends Annotation>, ConfigEntry<? extends Annotation, ?>> annotationMap;

    private Collection<String> childClasses;
    private Configuration config;

    public ConfigContainer(File config) {
        this.config = new Configuration(config);
        annotationMap = new LinkedHashMap<>();
    }

    public void setChildClasses(Collection<String> childClasses) {
        this.childClasses = childClasses;
    }

    public void processAllClasses(ASMDataTable dataTable, Map<Class<? extends Annotation>, Class<? extends ConfigEntry<? extends Annotation, ?>>> staticMap) {
        for (Map.Entry<Class<? extends Annotation>, Class<? extends ConfigEntry<? extends Annotation, ?>>> entry : staticMap.entrySet()) {
            try {
                annotationMap.put(entry.getKey(), entry.getValue().newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                CoreProperties.logger.fatal("Failed to create instance of the registered ConfigEntry!", e);
            }
        }

        ArrayList<String> processedClasses = new ArrayList<>();
        for (Class<? extends Annotation> clazz : annotationMap.keySet()) {
            for (ASMData asmData : dataTable.getAll(clazz.getName())) {
                String className = asmData.getClassName();
                if (childClasses.contains(className) && !processedClasses.contains(className)) {
                    processedClasses.add(className);
                    processClass(className);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processClass(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            // Should never happen seeing as we got it from the system.
            CoreProperties.logger.fatal("Failed to find class!", e);
            return;
        }

        for (final Field field : clazz.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                Class<? extends Annotation> annotationClazz = annotation.annotationType();
                if (annotationMap.containsKey(annotationClazz)) {
                    // Sets it accessible now for later use.
                    field.setAccessible(true);
                    // Checks the saveType, if the @SaveType annotation is found, it uses that value.
                    // Gets the associated ConfigEntry from the annotationClass. It then adds the saveType, the field it was found on, and casts the found annotation to the registered one.
                    ((ConfigEntry<Annotation, Object>) annotationMap.get(annotationClazz)).add(field, annotation);
                    break;
                }
            }
        }
    }

    public void operateOnConfig(boolean saveFlag) {
        config.load();
        for (ConfigEntry<? extends Annotation, ?> configEntry : annotationMap.values())
            configEntry.processFields(config, saveFlag);
        if (config.hasChanged())
            config.save();
    }
}
