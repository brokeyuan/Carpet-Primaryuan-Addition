package me.primaryuan.carpet.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.primaryuan.carpet.CarpetPrimaryuanSettings;
import me.primaryuan.carpet.handler.entitiesRidingPlayers.EntitiesRidingPlayersHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
        //#if MC > 12110
        //$$ return Commands.LEVEL_OWNERS.check(source.permissions());
        //#else
        return source.hasPermission(4);
        //#endif
    }

    private static int rideOn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        EntitiesRidingPlayersHandler.setRidePermission(player.getName().getString(), true);
        player.sendSystemMessage(Component.literal("§a已允许其他玩家骑乘你"));
        return 1;
    }

    private static int rideOff(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        EntitiesRidingPlayersHandler.setRidePermission(player.getName().getString(), false);
        player.sendSystemMessage(Component.literal("§a已禁止其他玩家骑乘你"));
        return 1;
    }

    private static int pickupOn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        EntitiesRidingPlayersHandler.setPickupPermission(player.getName().getString(), true);
        player.sendSystemMessage(Component.literal("§a已允许其他玩家捡起你"));
        return 1;
    }

    private static int pickupOff(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        EntitiesRidingPlayersHandler.setPickupPermission(player.getName().getString(), false);
        player.sendSystemMessage(Component.literal("§a已禁止其他玩家捡起你"));
        return 1;
    }
}