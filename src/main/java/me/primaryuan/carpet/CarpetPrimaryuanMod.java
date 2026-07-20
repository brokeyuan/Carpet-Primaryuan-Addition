package me.primaryuan.carpet;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class CarpetPrimaryuanMod implements ModInitializer {
    public static final String MOD_ID = "carpet-pry-addition";
    private static String version;

    @Override
    public void onInitialize() {
        version = FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .orElseThrow(RuntimeException::new)
                .getMetadata()
                .getVersion()
                .getFriendlyString();

        CarpetPrimaryuanServer.init();
    }

    public static String getModId() {
        return MOD_ID;
    }

    public static String getVersion() {
        return version;
    }
}
