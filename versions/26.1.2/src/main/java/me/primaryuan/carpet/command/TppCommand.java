package me.primaryuan.carpet.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.primaryuan.carpet.CarpetPrimaryuanSettings;
import me.primaryuan.carpet.TppConfigManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class TppCommand {

    private static final String STATION_ARG = "station";
    private static final String PLAYER_ARG = "player";
    private static final String ALIAS_ARG = "alias";
    private static final String DISPLAY_NAME_ARG = "displayName";
    private static final String RULE_STATION_ARG = "stationName";
    /** 假人名基础部分最大长度（不含 "_" + 地区） */
    private static final int MAX_BASE_NAME_LENGTH = 10;

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // === /tpp <station> — 玩家传送命令（可见性受 TppFakePlayer 规则控制）===
            LiteralArgumentBuilder<CommandSourceStack> tppRoot = Commands.literal("tpp")
                    .requires(source -> {
                        if (!source.isPlayer()) return true;
                        if (isAdmin(source)) return true;
                        return CarpetPrimaryuanSettings.TppFakePlayer;
                    });
            // /tpp <station> — 传送（支持中文显示名或英文内部名）
            tppRoot.then(Commands.argument(STATION_ARG, StringArgumentType.greedyString())
                    .suggests(TppCommand::suggestStations)
                    .executes(TppCommand::teleportToStation));
            dispatcher.register(tppRoot);

            // === /tppset — 站点管理与规则配置命令（根节点受 TppFakePlayer 控制）===
            LiteralArgumentBuilder<CommandSourceStack> setRoot = Commands.literal("tppset")
                    .requires(source -> {
                        if (!source.isPlayer()) return true;
                        if (isAdmin(source)) return true;
                        return CarpetPrimaryuanSettings.TppFakePlayer;
                    });

            // /tppset spawn <station> — 设置假人生成点（继承父级 TppFakePlayer 可见性）
            setRoot.then(Commands.literal("spawn")
                    .then(Commands.argument(STATION_ARG, StringArgumentType.greedyString())
                            .suggests(TppCommand::suggestStations)
                            .executes(TppCommand::setSpawnFakePlayer)));

            // /tppset set <name> [displayName] — 添加站点（管理员专属）
            setRoot.then(Commands.literal("set")
                    .requires(TppCommand::isAdmin)
                    .then(Commands.argument(STATION_ARG, StringArgumentType.word())
                            .then(Commands.argument(DISPLAY_NAME_ARG, StringArgumentType.greedyString())
                                    .executes(TppCommand::addStationWithDisplay))
                            .executes(TppCommand::addStation)));

            // /tppset rename <playerName> [set <alias>|remove]（管理员专属）
            setRoot.then(Commands.literal("rename")
                    .requires(TppCommand::isAdmin)
                    .then(Commands.argument(PLAYER_ARG, StringArgumentType.word())
                            .suggests(TppCommand::suggestOnlinePlayers)
                            .then(Commands.literal("remove")
                                    .executes(TppCommand::removePlayerAlias))
                            .then(Commands.literal("set")
                                    .then(Commands.argument(ALIAS_ARG, StringArgumentType.word())
                                            .executes(TppCommand::renamePlayer)))));

            // /tppset remove <station>（管理员专属）
            setRoot.then(Commands.literal("remove")
                    .requires(TppCommand::isAdmin)
                    .then(Commands.argument(STATION_ARG, StringArgumentType.greedyString())
                            .suggests(TppCommand::suggestStations)
                            .executes(TppCommand::removeStation)));

            // /tppset rule [use <count> [station]] — 查看/设置右键次数（管理员专属，无站点=全局，有站点=站点级）
            setRoot.then(Commands.literal("rule")
                    .requires(TppCommand::isAdmin)
                    .then(Commands.literal("use")
                            .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                    .executes(TppCommand::setUseCount)
                                    .then(Commands.argument(RULE_STATION_ARG, StringArgumentType.greedyString())
                                            .suggests(TppCommand::suggestStations)
                                            .executes(TppCommand::setUseCount))))
                    .executes(TppCommand::showRules));

            dispatcher.register(setRoot);
        });
    }

    /**
     * 构建假人名：别名优先 → 截断(>10字符) → 拼接地区
     *
     * @param playerName 玩家真实名
     * @param station    地区名
     * @return 假人名，格式: {基础名}_{地区}
     */
    private static String buildFakePlayerName(String playerName, String station) {
        String baseName;

        // 步骤1: 检查别名表
        if (TppConfigManager.aliases.containsKey(playerName)) {
            baseName = TppConfigManager.aliases.get(playerName);
        } else {
            baseName = playerName;
        }

        // 步骤2: 名字截断（超过10字符时只取前10个）
        if (baseName.length() > MAX_BASE_NAME_LENGTH) {
            baseName = baseName.substring(0, MAX_BASE_NAME_LENGTH);
        }

        // 步骤3: 拼接
        return baseName + "_" + station;
    }

    /**
     * 检查命令来源是否为管理员（控制台/RCON 始终允许，玩家需为 OP）
     */
    private static boolean isAdmin(CommandSourceStack source) {
        if (!source.isPlayer()) return true;
        return Commands.LEVEL_OWNERS.check(source.permissions());
    }

    /**
     * /tpp <station> - 玩家传送到指定站点（需要 TppFakePlayer=true）
     * 流程: rejoin → 轮询等待上线 → use → sleep(1500) → kill
     */
    private static int teleportToStation(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();

        // 检查功能是否开启
        if (!CarpetPrimaryuanSettings.TppFakePlayer) {
            player.sendSystemMessage(Component.translatable("carpetprimaryuan.command.tpp.disabled"));
            return 0;
        }

        String input = context.getArgument(STATION_ARG, String.class);

        // 解析输入（可能是内部名或显示名）为内部名
        String station = TppConfigManager.getInternalName(input);
        if (station == null) {
            player.sendSystemMessage(Component.translatable("carpetprimaryuan.command.tpp.station_not_found", input, String.join(", ", TppConfigManager.getDisplayNames())));
            return 0;
        }

        String playerName = player.getGameProfile().name();
        String fakePlayerName = buildFakePlayerName(playerName, station);

        source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.teleporting", TppConfigManager.getDisplayName(station), fakePlayerName), false);

        new Thread(() -> {
            try {
                var server = source.getServer();

                // 步骤 A: 执行 /player <fakePlayerName> rejoin（以玩家身份）
                server.execute(() -> {
                    server.getCommands().performPrefixedCommand(
                            player.createCommandSourceStack(),
                            "/player " + fakePlayerName + " rejoin"
                    );
                });

                // 步骤 B: 等待假人上线（最大 10 秒）
                boolean joined = false;
                for (int i = 0; i < 50; i++) {
                    Thread.sleep(200);
                    final CountDownLatch latch = new CountDownLatch(1);
                    final boolean[] isOnline = {false};
                    server.execute(() -> {
                        try {
                            isOnline[0] = server.getPlayerList().getPlayer(fakePlayerName) != null;
                        } finally {
                            latch.countDown();
                        }
                    });
                    latch.await();
                    if (isOnline[0]) {
                        joined = true;
                        break;
                    }
                }

                if (!joined) {
                    server.execute(() -> {
                        player.sendSystemMessage(
                                Component.translatable("carpetprimaryuan.command.tpp.teleport_failed", fakePlayerName)
                        );
                    });
                    return;
                }

                // 步骤 C: 逐次执行 use，每次间隔 1 秒，确保每次右键独立生效
                // 使用站点级右键次数配置，如果站点未设置则使用全局默认值
                int stationUseCount = TppConfigManager.getUseCount(station);
                for (int i = 0; i < stationUseCount; i++) {
                    server.execute(() -> {
                        server.getCommands().performPrefixedCommand(
                                player.createCommandSourceStack(),
                                "/player " + fakePlayerName + " use"
                        );
                    });
                    if (i < stationUseCount - 1) {
                        Thread.sleep(500);
                    }
                }

                // 步骤 D: 等待 3 秒让传送完成，再执行下线
                Thread.sleep(3000);

                // 步骤 E: 以控制台身份执行 /player <fakePlayerName> kill
                // 使用控制台身份可避免权限问题，并让 Carpet 自然处理断开流程
                server.execute(() -> {
                    server.getCommands().performPrefixedCommand(
                            server.createCommandSourceStack(),
                            "/player " + fakePlayerName + " kill"
                    );

                    player.sendSystemMessage(
                            Component.translatable("carpetprimaryuan.command.tpp.teleport_complete", TppConfigManager.getDisplayName(station))
                    );
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                var srv = source.getServer();
                srv.execute(() -> {
                    player.sendSystemMessage(
                            Component.translatable("carpetprimaryuan.command.tpp.teleport_interrupted")
                    );
                });
            }
        }).start();

        return 1;
    }

    /**
     * /tppset spawn <station> - 立即以玩家身份生成假人，1秒后自动下线
     */
    private static int setSpawnFakePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        ServerPlayer player = source.getPlayerOrException();

        // 检查功能是否开启
        if (!CarpetPrimaryuanSettings.TppFakePlayer) {
            player.sendSystemMessage(Component.translatable("carpetprimaryuan.command.tpp.disabled"));
            return 0;
        }

        String input = context.getArgument(STATION_ARG, String.class);

        // 解析输入（可能是内部名或显示名）为内部名
        String station = TppConfigManager.getInternalName(input);
        if (station == null) {
            player.sendSystemMessage(Component.translatable("carpetprimaryuan.command.tpp.station_not_found", input, String.join(", ", TppConfigManager.getDisplayNames())));
            return 0;
        }

        String playerName = player.getGameProfile().name();
        String fakePlayerName = buildFakePlayerName(playerName, station);
        var server = source.getServer();

        // 步骤 1: 立即以玩家身份生成假人
        server.getCommands().performPrefixedCommand(
                player.createCommandSourceStack(),
                "/player " + fakePlayerName + " spawn"
        );
        source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.fake_player_spawned", fakePlayerName), false);

        // 步骤 2: 3 秒后以控制台身份 kill 假人下线
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                server.execute(() -> {
                    server.getCommands().performPrefixedCommand(
                            server.createCommandSourceStack(),
                            "/player " + fakePlayerName + " kill"
                    );
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        return 1;
    }

    /**
     * /tppset set <name> - 添加传送站点（无备注）
     */
    private static int addStation(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!isAdmin(source)) {
            source.sendFailure(Component.translatable("carpetprimaryuan.command.tpp.admin_only"));
            return 0;
        }

        String name = context.getArgument(STATION_ARG, String.class);

        if (TppConfigManager.stationMap.containsKey(name)) {
            String existingDisplay = TppConfigManager.getDisplayName(name);
            source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.station_exists", name, existingDisplay), false);
            return 0;
        }

        TppConfigManager.stationMap.put(name, null);
        TppConfigManager.save();

        source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.station_added", name), false);
        return 1;
    }

    /**
     * /tppset set <name> <displayName> - 添加传送站点（带显示名称/备注）
     */
    private static int addStationWithDisplay(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!isAdmin(source)) {
            source.sendFailure(Component.translatable("carpetprimaryuan.command.tpp.admin_only"));
            return 0;
        }

        String name = context.getArgument(STATION_ARG, String.class);
        String displayName = context.getArgument(DISPLAY_NAME_ARG, String.class);

        if (TppConfigManager.stationMap.containsKey(name)) {
            String existingDisplay = TppConfigManager.getDisplayName(name);
            source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.station_exists", name, existingDisplay), false);
            return 0;
        }

        TppConfigManager.stationMap.put(name, displayName);
        TppConfigManager.save();

        source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.station_added_with_display", name, displayName), false);
        return 1;
    }

    /**
     * /tppset remove <name> - 删除传送站点（支持内部名或显示名）
     */
    private static int removeStation(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!isAdmin(source)) {
            source.sendFailure(Component.translatable("carpetprimaryuan.command.tpp.admin_only"));
            return 0;
        }

        String input = context.getArgument(STATION_ARG, String.class);
        String internalName = TppConfigManager.getInternalName(input);

        if (internalName == null) {
            source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.station_not_exists", input), false);
            return 0;
        }

        TppConfigManager.stationMap.remove(internalName);
        TppConfigManager.save();

        source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.station_removed", internalName), false);
        return 1;
    }

    /**
     * /tppset rename <playerName> set <alias> - 为玩家设置假人传送别名
     */
    private static int renamePlayer(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!isAdmin(source)) {
            source.sendFailure(Component.translatable("carpetprimaryuan.command.tpp.admin_only"));
            return 0;
        }

        String playerName = context.getArgument(PLAYER_ARG, String.class);
        String alias = context.getArgument(ALIAS_ARG, String.class);

        if (playerName.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.player_name_empty"), false);
            return 0;
        }
        if (alias.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.alias_empty"), false);
            return 0;
        }

        // 校验别名长度: 别名 + "_" + 最长可能的地区名不应超过15字符（留余量）
        // 这里简单检查别名本身不超过12字符
        if (alias.length() > 12) {
            source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.alias_too_long", alias.length()), false);
            return 0;
        }

        TppConfigManager.aliases.put(playerName, alias);
        TppConfigManager.save();

        source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.alias_set", playerName, alias, alias), false);
        return 1;
    }

    /**
     * /tppset rename <playerName> remove - 移除玩家别名
     */
    private static int removePlayerAlias(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!isAdmin(source)) {
            source.sendFailure(Component.translatable("carpetprimaryuan.command.tpp.admin_only"));
            return 0;
        }

        String playerName = context.getArgument(PLAYER_ARG, String.class);

        if (!TppConfigManager.aliases.containsKey(playerName)) {
            source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.no_alias", playerName), false);
            return 0;
        }

        TppConfigManager.aliases.remove(playerName);
        TppConfigManager.save();

        source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.alias_removed", playerName), false);
        return 1;
    }

    /**
     * /tppset rule use <count> [station] - 设置假人右键次数
     * 无 station 参数：设置全局次数
     * 有 station 参数：设置站点级次数
     */
    private static int setUseCount(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!isAdmin(source)) {
            source.sendFailure(Component.translatable("carpetprimaryuan.command.tpp.admin_only"));
            return 0;
        }

        int count = context.getArgument("count", Integer.class);

        // 检查是否指定了站点名（可选参数）
        String stationInput = null;
        try {
            stationInput = context.getArgument(RULE_STATION_ARG, String.class);
        } catch (IllegalArgumentException ignored) {
            // 未指定站点参数，使用全局设置
        }
        final String stationInputFinal = stationInput;

        if (stationInputFinal != null && !stationInputFinal.isEmpty()) {
            // 站点级设置
            String station = TppConfigManager.getInternalName(stationInputFinal);
            if (station == null) {
                source.sendFailure(Component.translatable("carpetprimaryuan.command.tpp.station_not_exists", stationInputFinal));
                return 0;
            }
            TppConfigManager.stationUseCount.put(station, count);
            TppConfigManager.save();
            String displayName = TppConfigManager.getDisplayName(station);
            source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.use_count_set_station", displayName, count), false);
        } else {
            // 全局设置
            TppConfigManager.useCount = count;
            TppConfigManager.save();
            source.sendSuccess(() -> Component.translatable("carpetprimaryuan.command.tpp.use_count_set_global", count), false);
        }
        return 1;
    }

    /**
     * /tppset rule - 查看当前 TPP 规则配置
     */
    private static int showRules(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component message = Component.empty()
                .append(Component.translatable("carpetprimaryuan.command.tpp.rules_header"))
                .append(Component.literal("\n"))
                .append(Component.translatable("carpetprimaryuan.command.tpp.rules_global_count", TppConfigManager.useCount))
                .append(Component.literal("\n"));
        if (!TppConfigManager.stationUseCount.isEmpty()) {
            message = message.append(Component.translatable("carpetprimaryuan.command.tpp.rules_station_counts")).append(Component.literal("\n"));
            for (Map.Entry<String, Integer> entry : TppConfigManager.stationUseCount.entrySet()) {
                String displayName = TppConfigManager.getDisplayName(entry.getKey());
                message = message.append(Component.translatable("carpetprimaryuan.command.tpp.rules_station_entry", displayName, entry.getValue())).append(Component.literal("\n"));
            }
        } else {
            message = message.append(Component.translatable("carpetprimaryuan.command.tpp.rules_no_station_counts")).append(Component.literal("\n"));
        }
        source.sendSuccess(() -> message, false);
        return 1;
    }

    /**
     * 自动补全：从 TppConfigManager 获取所有站点显示名称
     */
    private static CompletableFuture<Suggestions> suggestStations(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        for (String displayName : TppConfigManager.getDisplayNames()) {
            if (displayName.toLowerCase().startsWith(remaining)) {
                builder.suggest(displayName);
            }
        }

        return builder.buildFuture();
    }

    /**
     * 自动补全：从在线玩家列表中获取所有玩家名
     */
    private static CompletableFuture<Suggestions> suggestOnlinePlayers(
            CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        try {
            var players = context.getSource().getServer().getPlayerList().getPlayers();
            String remaining = builder.getRemainingLowerCase();
            for (ServerPlayer p : players) {
                String name = p.getGameProfile().name();
                if (name.toLowerCase().startsWith(remaining)) {
                    builder.suggest(name);
                }
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    }
}
