package me.primaryuan.carpet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TppConfigManager {

    /**
     * 站点映射：key=内部名（用于假人命名），value=显示名称/备注（null 表示无备注，显示时等同内部名）
     * 使用 LinkedHashMap 保持添加顺序
     */
    public static LinkedHashMap<String, String> stationMap = new LinkedHashMap<>();

    public static Map<String, String> aliases = new ConcurrentHashMap<>();

    /** 全局默认传送时假人右键使用珍珠的次数（默认 1），站点未单独设置时使用此值 */
    public static int useCount = 1;

    /** 站点级右键次数配置：key=站点内部名，value=右键次数 */
    public static Map<String, Integer> stationUseCount = new ConcurrentHashMap<>();

    private static final File CONFIG_FILE = new File("config/carpet-pry-tpp.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            // 创建默认空配置
            JsonObject defaultConfig = new JsonObject();
            defaultConfig.add("stations", new JsonObject());
            defaultConfig.add("aliases", new JsonObject());

            CONFIG_FILE.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(defaultConfig, writer);
            } catch (IOException e) {
                System.err.println("[TPP] Failed to create default config file: " + e.getMessage());
            }
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);

            stationMap.clear();
            JsonElement stationsElem = json.get("stations");
            if (stationsElem != null) {
                if (stationsElem.isJsonArray()) {
                    // 旧格式兼容：JsonArray → 转为 stationMap（value=null）
                    JsonArray arr = stationsElem.getAsJsonArray();
                    for (int i = 0; i < arr.size(); i++) {
                        stationMap.put(arr.get(i).getAsString(), null);
                    }
                } else if (stationsElem.isJsonObject()) {
                    // 新格式：JsonObject
                    JsonObject obj = stationsElem.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                        String value = entry.getValue().isJsonNull() ? null : entry.getValue().getAsString();
                        stationMap.put(entry.getKey(), value);
                    }
                }
            }

            aliases.clear();
            JsonObject aliasesObj = json.getAsJsonObject("aliases");
            if (aliasesObj != null) {
                for (Map.Entry<String, JsonElement> entry : aliasesObj.entrySet()) {
                    aliases.put(entry.getKey(), entry.getValue().getAsString());
                }
            }

            // 加载 useCount
            if (json.has("useCount")) {
                useCount = json.get("useCount").getAsInt();
            }

            // 加载站点级右键次数配置
            stationUseCount.clear();
            JsonObject stationUseCountObj = json.getAsJsonObject("stationUseCount");
            if (stationUseCountObj != null) {
                for (Map.Entry<String, JsonElement> entry : stationUseCountObj.entrySet()) {
                    stationUseCount.put(entry.getKey(), entry.getValue().getAsInt());
                }
            }
        } catch (Exception e) {
            System.err.println("[TPP] Failed to load config: " + e.getMessage());
        }
    }

    public static void save() {
        JsonObject config = new JsonObject();

        JsonObject stationsObj = new JsonObject();
        for (Map.Entry<String, String> entry : stationMap.entrySet()) {
            if (entry.getValue() == null) {
                stationsObj.add(entry.getKey(), null);  // JSON null
            } else {
                stationsObj.addProperty(entry.getKey(), entry.getValue());
            }
        }
        config.add("stations", stationsObj);

        JsonObject aliasesObj = new JsonObject();
        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            aliasesObj.addProperty(entry.getKey(), entry.getValue());
        }
        config.add("aliases", aliasesObj);
        config.addProperty("useCount", useCount);

        // 保存站点级右键次数配置
        JsonObject stationUseCountObj = new JsonObject();
        for (Map.Entry<String, Integer> entry : stationUseCount.entrySet()) {
            stationUseCountObj.addProperty(entry.getKey(), entry.getValue());
        }
        config.add("stationUseCount", stationUseCountObj);

        CONFIG_FILE.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
            writer.flush();
        } catch (IOException e) {
            System.err.println("[TPP] Failed to save config: " + e.getMessage());
        }
    }

    /**
     * 获取站点的显示名称。无备注时返回内部名本身。
     */
    public static String getDisplayName(String internalName) {
        String display = stationMap.get(internalName);
        return (display == null || display.isEmpty()) ? internalName : display;
    }

    /**
     * 根据玩家输入（可能是内部名或显示名）反查内部名。
     *
     * @param input 玩家输入的字符串
     * @return 内部名，未找到返回 null
     */
    public static String getInternalName(String input) {
        if (input == null || input.isEmpty()) return null;
        // 先精确匹配内部名
        if (stationMap.containsKey(input)) return input;
        // 再匹配显示名（忽略大小写）
        for (Map.Entry<String, String> entry : stationMap.entrySet()) {
            String display = entry.getValue();
            if (display != null && !display.isEmpty() && display.equalsIgnoreCase(input)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 获取所有站点的显示名称列表（用于 Tab 补全）
     */
    public static List<String> getDisplayNames() {
        List<String> result = new ArrayList<>();
        for (String key : stationMap.keySet()) {
            result.add(getDisplayName(key));
        }
        return result;
    }

    /**
     * 获取所有站点内部名的逗号分隔字符串
     */
    public static String getStationsString() {
        return String.join(",", stationMap.keySet());
    }

    /**
     * 获取指定站点的右键次数
     * 如果站点未单独设置，则返回全局默认值
     *
     * @param station 站点内部名
     * @return 该站点的右键次数
     */
    public static int getUseCount(String station) {
        return stationUseCount.getOrDefault(station, useCount);
    }
}
