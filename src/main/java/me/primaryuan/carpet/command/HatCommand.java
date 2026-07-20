package me.primaryuan.carpet.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.primaryuan.carpet.CarpetPrimaryuanSettings;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class HatCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("hat")
                    .requires(source -> {
                        if (!source.isPlayer()) return true;
                        if (isAdmin(source)) return true;
                        return CarpetPrimaryuanSettings.playerhat;
                    })
                    .executes(HatCommand::execute));
        });
    }

    private static boolean isAdmin(CommandSourceStack source) {
        if (!source.isPlayer()) return true;
        try {
            net.minecraft.server.level.ServerPlayer player = source.getPlayerOrException();
            java.io.File opsFile = new java.io.File("ops.json");
            if (opsFile.exists() && java.nio.file.Files.isReadable(opsFile.toPath())) {
                String content = java.nio.file.Files.readString(opsFile.toPath());
                return content.contains(player.getUUID().toString()) || content.contains(player.getName().getString());
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        if (!CarpetPrimaryuanSettings.playerhat) {
            player.sendSystemMessage(Component.literal("§c帽子功能未启用，请联系管理员使用 /carpet playerhat true 开启"));
            return 0;
        }

        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack headSlotItem = player.getItemBySlot(EquipmentSlot.HEAD);

        if (mainHandItem.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c请手持要戴的物品"));
            return 0;
        }

        player.setItemSlot(EquipmentSlot.HEAD, mainHandItem);
        player.setItemInHand(InteractionHand.MAIN_HAND, headSlotItem);

        String itemName = mainHandItem.getDisplayName().getString();
        player.sendSystemMessage(Component.literal("§a已将 " + itemName + " 戴在头上"));

        return 1;
    }
}