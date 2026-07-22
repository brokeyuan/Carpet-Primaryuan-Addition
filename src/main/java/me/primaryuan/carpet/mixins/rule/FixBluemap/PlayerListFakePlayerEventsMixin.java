package me.primaryuan.carpet.mixins.rule.FixBluemap;

import carpet.patches.EntityPlayerMPFake;
import carpet.patches.NetHandlerPlayServerFake;
import me.primaryuan.carpet.CarpetPrimaryuanSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复假人不触发 Fabric API 的 ServerPlayConnectionEvents.JOIN/DISCONNECT 事件。
 *
 * BlueMap 等模组依赖这些事件来追踪玩家上下线，Carpet 的假人由于不走正常登录流程，
 * 不会触发这些事件，导致 BlueMap 的实时玩家标记在假人下线后仍然残留。
 *
 * 参考: https://github.com/gnembon/fabric-carpet/pull/2142
 */
@Mixin(PlayerList.class)
public class PlayerListFakePlayerEventsMixin {

    @Inject(method = "placeNewPlayer", at = @At("RETURN"))
    private void fixBluemap$onPlaceNewPlayer(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        if (!CarpetPrimaryuanSettings.FixBluemap) return;
        if (player instanceof EntityPlayerMPFake && player.connection instanceof NetHandlerPlayServerFake) {
            MinecraftServer server = ((PlayerList) (Object) this).getServer();
            ServerPlayConnectionEvents.JOIN.invoker().onPlayReady(player.connection, null, server);
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void fixBluemap$onRemovePlayer(ServerPlayer player, CallbackInfo ci) {
        if (!CarpetPrimaryuanSettings.FixBluemap) return;
        if (player instanceof EntityPlayerMPFake && player.connection instanceof NetHandlerPlayServerFake) {
            MinecraftServer server = ((PlayerList) (Object) this).getServer();
            ServerPlayConnectionEvents.DISCONNECT.invoker().onPlayDisconnect(player.connection, server);
        }
    }
}
