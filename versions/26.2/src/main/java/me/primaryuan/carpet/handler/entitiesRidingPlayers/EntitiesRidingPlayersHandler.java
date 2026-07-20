package me.primaryuan.carpet.handler.entitiesRidingPlayers;

import me.primaryuan.carpet.CarpetPrimaryuanSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntitiesRidingPlayersHandler {
    private static final Map<String, Boolean> ridePermission = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> pickupPermission = new ConcurrentHashMap<>();

    public static InteractionResult rideEntity(Player player, Entity targetEntity, Level level, InteractionHand hand) {
        if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            if (!(targetEntity instanceof Player targetPlayer)) {
                return InteractionResult.PASS;
            }

            if (!player.getItemInHand(hand).is(Items.TOTEM_OF_UNDYING)) {
                return InteractionResult.PASS;
            }

            if (player.getItemInHand(InteractionHand.OFF_HAND).is(Items.GOLDEN_CARROT)) {
                return InteractionResult.PASS;
            }

            String targetName = targetPlayer.getName().getString();
            if (Boolean.FALSE.equals(ridePermission.get(targetName))) {
                if (player instanceof ServerPlayer serverPlayer) {
                    Component subtitle = Component.translatable("carpetprimaryuan.command.ride.disallow_ride_subtitle", targetName);
                    serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
                    serverPlayer.connection.send(new ClientboundSetTitleTextPacket(Component.empty()));
                }
                return InteractionResult.PASS;
            }

            Entity vehicle = getHighestOrSelf(targetEntity, player, CarpetPrimaryuanSettings.ridingPlayersPickUpLimit);

            if (vehicle == null) return InteractionResult.FAIL;
            player.startRiding(vehicle);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult pickUpEntity(Player player, Entity targetEntity, Level level, InteractionHand hand) {
        if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            if (!(targetEntity instanceof Player targetPlayer)) {
                return InteractionResult.PASS;
            }

            if (!player.getItemInHand(hand).is(Items.TOTEM_OF_UNDYING)) {
                return InteractionResult.PASS;
            }

            if (!player.getItemInHand(InteractionHand.OFF_HAND).is(Items.GOLDEN_CARROT)) {
                return InteractionResult.PASS;
            }

            String targetName = targetPlayer.getName().getString();
            if (Boolean.FALSE.equals(pickupPermission.get(targetName))) {
                if (player instanceof ServerPlayer serverPlayer) {
                    Component subtitle = Component.translatable("carpetprimaryuan.command.ride.disallow_pickup_subtitle", targetName);
                    serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
                    serverPlayer.connection.send(new ClientboundSetTitleTextPacket(Component.empty()));
                }
                return InteractionResult.PASS;
            }

            Entity vehicle = getHighestOrSelf(player, targetEntity, CarpetPrimaryuanSettings.ridingPlayersPickUpLimit);

            if (vehicle == null) return InteractionResult.FAIL;
            targetEntity.startRiding(vehicle);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static Entity getHighestOrSelf(Entity vehicle, Entity newPassenger, int limit) {
        int count = -1;
        while (vehicle.isVehicle()) {
            count++;
            vehicle = vehicle.getFirstPassenger();
            if (vehicle == newPassenger || count >= limit) return null;
        }
        return vehicle;
    }

    public static void setRidePermission(String playerName, boolean allow) {
        ridePermission.put(playerName, allow);
    }

    public static boolean getRidePermission(String playerName) {
        return ridePermission.getOrDefault(playerName, true);
    }

    public static void setPickupPermission(String playerName, boolean allow) {
        pickupPermission.put(playerName, allow);
    }

    public static boolean getPickupPermission(String playerName) {
        return pickupPermission.getOrDefault(playerName, true);
    }

    public static void onMount(Entity vehicle, Entity passenger) {
        if (!vehicle.level().isClientSide() && vehicle instanceof Player) {
            ((ServerPlayer) vehicle).connection.send(new ClientboundSetPassengersPacket(vehicle));
        }
    }

    public static void onDismount(Entity vehicle) {
        if (!vehicle.level().isClientSide() && vehicle instanceof Player)
            ((ServerPlayer) vehicle).connection.send(new ClientboundSetPassengersPacket(vehicle));
    }

    public static void onPlayerTick(Player player) {
        if (!player.level().isClientSide() && player.onGround() && player.isVehicle() && player.isCrouching()) {
            player.getFirstPassenger().stopRiding();
        }
    }

    public static void onLogOut(Player player) {
        if (player.isPassenger() && player.getVehicle() instanceof Player)
            player.stopRiding();
        ridePermission.remove(player.getName().getString());
        pickupPermission.remove(player.getName().getString());
    }

    public static void onGameModeChange(Player player, GameType gameMode) {
        if (player.isVehicle() && (CarpetPrimaryuanSettings.ridingPlayersDismountOnGameModeChange || gameMode == GameType.SPECTATOR))
            player.getFirstPassenger().stopRiding();
    }
}
