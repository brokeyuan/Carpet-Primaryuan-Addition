package me.primaryuan.carpet.i18n;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.primaryuan.carpet.CarpetPrimaryuanMod;
import me.primaryuan.carpet.CarpetPrimaryuanServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端翻译工具类。
 *
 * 背景：本 mod 为服务端 only，客户端没有翻译文件。
 * {@link Component#translatable(String, Object...)} 的翻译查找发生在客户端，
 * 客户端找不到翻译键时会直接显示原始键字符串。
 *
 * 本类在服务端完成翻译查找与占位符替换，返回 {@link Component#literal(String)}，
 * 这样客户端无需任何翻译资源即可正确显示。
 */
public class ServerI18n {
    private static final Map<String, Map<String, String>> translations = new HashMap<>();
    private static final String DEFAULT_LANG = "en_us";
    private static final String[] SUPPORTED_LANGS = {"en_us", "zh_cn", "zh_tw"};

    private ServerI18n() {}

    /** 在 mod 初始化时调用，加载所有语言文件到内存 */
    public static void init() {
        for (String lang : SUPPORTED_LANGS) {
            loadLang(lang);
        }
        CarpetPrimaryuanServer.LOGGER.info("ServerI18n loaded {} languages: {}",
                translations.size(), translations.keySet());
    }

    private static void loadLang(String lang) {
        String path = "/assets/" + CarpetPrimaryuanMod.getModId() + "/lang/" + lang + ".json";
        try (InputStream is = ServerI18n.class.getResourceAsStream(path)) {
            if (is == null) {
                CarpetPrimaryuanServer.LOGGER.debug("Language file not found: {}", path);
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<String, com.google.gson.JsonElement> entry : json.entrySet()) {
                map.put(entry.getKey(), entry.getValue().getAsString());
            }
            translations.put(lang, map);
        } catch (Exception e) {
            CarpetPrimaryuanServer.LOGGER.error("Failed to load language file: " + lang, e);
        }
    }

    /** 获取玩家语言，回退到 en_us */
    public static String getLang(ServerPlayer player) {
        try {
            String lang = player.getLanguage();
            if (lang != null && !lang.isEmpty() && translations.containsKey(lang)) {
                return lang;
            }
        } catch (Throwable ignored) {
            // 某些版本可能没有 getLanguage() 方法，回退到默认语言
        }
        return DEFAULT_LANG;
    }

    /** 返回翻译后的 Component（给 ServerPlayer） */
    public static Component tr(ServerPlayer player, String key, Object... args) {
        return Component.literal(format(getLang(player), key, args));
    }

    /** 返回翻译后的 Component（给 CommandSourceStack，自动判断玩家/控制台） */
    public static Component tr(CommandSourceStack source, String key, Object... args) {
        String lang = source.isPlayer() ? getLang(source.getPlayerOrException()) : DEFAULT_LANG;
        return Component.literal(format(lang, key, args));
    }

    /** 返回翻译后的 Component（使用默认语言 en_us，给广播等场景） */
    public static Component tr(String key, Object... args) {
        return Component.literal(format(DEFAULT_LANG, key, args));
    }

    /** 核心格式化方法，返回翻译后的字符串 */
    public static String format(String lang, String key, Object... args) {
        String template = getTemplate(lang, key);
        if (template == null) {
            return key;
        }
        if (args == null || args.length == 0) {
            return template;
        }
        try {
            return String.format(template, args);
        } catch (Exception e) {
            return template;
        }
    }

    /** 查找翻译模板：先查指定语言 → 再查 en_us → 都找不到返回 null */
    private static String getTemplate(String lang, String key) {
        Map<String, String> langMap = translations.get(lang);
        if (langMap != null) {
            String template = langMap.get(key);
            if (template != null) {
                return template;
            }
        }
        if (!DEFAULT_LANG.equals(lang)) {
            Map<String, String> defaultMap = translations.get(DEFAULT_LANG);
            if (defaultMap != null) {
                return defaultMap.get(key);
            }
        }
        return null;
    }
}
