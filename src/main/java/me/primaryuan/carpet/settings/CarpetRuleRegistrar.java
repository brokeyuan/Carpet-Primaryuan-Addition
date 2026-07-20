package me.primaryuan.carpet.settings;

import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

public class CarpetRuleRegistrar {
    private static final Logger LOGGER = LogManager.getLogger("CarpetPrimaryuan");

    public static void register(Class<?> settingsClass) {
        SettingsManager settingsManager = CarpetServer.settingsManager;
        if (settingsManager == null) {
            throw new RuntimeException("CarpetServer.settingsManager is null");
        }

        for (Field field : settingsClass.getDeclaredFields()) {
            Rule rule = field.getAnnotation(Rule.class);
            if (rule != null) {
                registerRule(settingsManager, field, rule);
            }
        }
    }

    private static void registerRule(SettingsManager settingsManager, Field field, Rule rule) {
        String ruleName = field.getName();
        
        try {
            Class<?> ruleAnnotationClass = Class.forName("carpet.settings.ParsedRule$RuleAnnotation");
            Constructor<?> ctr1 = ruleAnnotationClass.getDeclaredConstructors()[0];
            ctr1.setAccessible(true);
            Object ruleAnnotation = ctr1.newInstance(false, null, null, null, rule.categories(), rule.options(), rule.strict(), "", rule.validators());

            Class<?> parsedRuleClass = Class.forName("carpet.settings.ParsedRule");
            Constructor<?> ctr2 = Arrays.stream(parsedRuleClass.getDeclaredConstructors())
                    .filter(ctr -> {
                        Class<?>[] parameterTypes = ctr.getParameterTypes();
                        if (parameterTypes.length != 3) return false;
                        return parameterTypes[0] == Field.class 
                                && parameterTypes[1] == ruleAnnotationClass 
                                && parameterTypes[2].getName().contains("SettingsManager");
                    })
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Failed to get matched ParsedRule constructor"));
            ctr2.setAccessible(true);
            Object carpetRule = ctr2.newInstance(field, ruleAnnotation, settingsManager);

            settingsManager.addCarpetRule((carpet.api.settings.CarpetRule<?>) carpetRule);
            LOGGER.info("Registered rule: " + ruleName);
        } catch (Exception e) {
            LOGGER.error("Failed to register rule " + ruleName, e);
            throw new RuntimeException("Failed to register rule " + ruleName, e);
        }
    }
}
