package me.primaryuan.carpet;

import me.primaryuan.carpet.settings.Rule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static carpet.api.settings.RuleCategory.*;

public class CarpetPrimaryuanSettings {
    public static final String PRIMARYUAN = "PRIMARYUAN";

    @Rule(
            options = {"false", "true"},
            categories = {PRIMARYUAN, CREATIVE, SURVIVAL}
    )
    public static boolean TppFakePlayer = false;

    @Rule(
            options = {"Steve,Alex", "Pry,hsds", "Pry,hsds,Firework,Food", ""},
            strict = false,
            categories = {PRIMARYUAN, CREATIVE, SURVIVAL}
    )
    public static String fakePlayerNameSuggestions = "Steve,Alex";

    @Rule(
            options = {"default", "summon", "same_skin"},
            strict = false,
            categories = {PRIMARYUAN, CREATIVE, SURVIVAL}
    )
    public static String fakePlayerSkinMode = "default";

    @Rule(
            options = {"Brokeyuan", "hsds", ""},
            strict = false,
            categories = {PRIMARYUAN, CREATIVE, SURVIVAL}
    )
    public static String fakePlayerSkinSet = "Brokeyuan";

    @Rule(
            categories = {PRIMARYUAN, FEATURE}
    )
    public static boolean xaerolibFix = false;

    @Rule(
            categories = {PRIMARYUAN, FEATURE}
    )
    public static boolean bluemapFix = false;

    @Rule(
            categories = {PRIMARYUAN, SURVIVAL, FEATURE}
    )
    public static boolean invisibleInTallGrass = false;

    @Rule(
            categories = {PRIMARYUAN, SURVIVAL, FEATURE}
    )
    public static boolean playerhat = false;

    @Rule(
            categories = {PRIMARYUAN, SURVIVAL, FEATURE}
    )
    public static boolean ridingPlayers = false;

    @Rule(
            categories = {PRIMARYUAN, SURVIVAL, FEATURE}
    )
    public static boolean pickupPlayers = false;

    @Rule(
            categories = {PRIMARYUAN, SURVIVAL, FEATURE}
    )
    public static boolean betterSnowBall = false;

    @Rule(
            options = {"16", "32", ""},
            strict = false,
            categories = {PRIMARYUAN, SURVIVAL, FEATURE}
    )
    public static int ridingPlayersPickUpLimit = 16;

    @Rule(
            categories = {PRIMARYUAN, SURVIVAL, FEATURE}
    )
    public static boolean ridingPlayersDismountOnGameModeChange = false;

    @Rule(
            categories = {PRIMARYUAN, SURVIVAL, FEATURE, CLIENT}
    )
    public static boolean ridingPlayersClientAllowInteractions = true;

    @Rule(
            categories = {PRIMARYUAN, CREATIVE, SURVIVAL}
    )
    public static boolean sleepingDuringTheDay = false;

    @Rule(
            categories = {PRIMARYUAN, FEATURE}
    )
    public static boolean unicodeArgumentsSupport = false;

    public static String fakePlayerTpStations = "";

    public static Map<String, String> tppPlayerAliases = new ConcurrentHashMap<>();
}