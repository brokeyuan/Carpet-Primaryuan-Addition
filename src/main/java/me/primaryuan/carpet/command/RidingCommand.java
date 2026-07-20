package me.primaryuan.carpet.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.primaryuan.carpet.CarpetPrimaryuanSettings;
import me.primaryuan.carpet.handler.entitiesRidingPlayers.EntitiesRidingPlayersHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class RidingCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("ride")
                    .requires(source -> {
                        if (!source.isPlayer()) return true;
                        if (isAdmin(source)) return true;
                        return CarpetPrimaryuanSettings.ridingPlayers;
                    })
                    .then(Commands.literal("on")
                            .executes(RidingCommand::rideOn))
                    .then(Commands.literal("off")
                            .executes(RidingCommand::rideOff)));

            dispatcher.register(Commands.literal("pickup")
                    .requires(source -> {
                        if (!source.isPlayer()) return true;
                        if (isAdmin(source)) return true;
                        return CarpetPrimaryuanSettings.pickupPlayers;
                    })
                    .then(Commands.literal("on")
                            .executes(RidingCommand::pickupOn))
                    .then(Commands.literal("off")
                            .executes(RidingCommand::pickupOff)));
        });
    }

    private static boolean isAdmin(CommandSourceStack source) {
        if (!source.isPlayer()) return true;
        return net.minecraft.commands.Commands.LEVEL_OWNERS.check(source.permissions());
    }

    private static void broadcastToAllPlayers(MinecraftServer server, Component message) {
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(message);
        }
    }

    private static int rideOn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String playerName = player.getName().getString();

        EntitiesRidingPlayersHandler.setRidePermission(playerName, true);
        broadcastToAllPlayers(player.getServer(), Component.literal("§a" + playerName + " 玩家允许被骑乘了"));
        return 1;
    }

    private static int rideOff(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String playerName = player.getName().getString();

        EntitiesRidingPlayersHandler.setRidePermission(playerName, false);
        broadcastToAllPlayers(player.getServer(), Component.literal("§c" + playerName + " 玩家禁止被骑乘了"));
        return 1;
    }

    private static int pickupOn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String playerName = player.getName().getString();

        EntitiesRidingPlayersHandler.setPickupPermission(playerName, true);
        broadcastToAllPlayers(player.getServer(), Component.literal("§a" + playerName + " 玩家允许被捡起了"));
        return 1;
    }

    private static int pickupOff(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String playerName = player.getName().getString();

        EntitiesRidingPlayersHandler.setPickupPermission(playerName, false);
        broadcastToAllPlayers(player.getServer(), Component.literal("§c" + playerName + " 玩家禁止被捡起了"));
        return 1;
    }
}
