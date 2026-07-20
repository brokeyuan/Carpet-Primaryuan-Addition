package me.primaryuan.carpet.mixins.rule.fakePlayerSkin;

import carpet.commands.PlayerCommand;
import carpet.patches.EntityPlayerMPFake;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import me.primaryuan.carpet.CarpetPrimaryuanSettings;
import net.lionarius.skinrestorer.skin.SkinService;
import net.lionarius.skinrestorer.skin.SkinVariant;
import net.lionarius.skinrestorer.skin.provider.SkinProviderContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

/**
 * PlayerCommandSkinMixin - 1.21.1 版本覆盖
 *
 * 使用 skinrestorer 2.7.0 (early) 的 SkinService API
 * setSkinAsync 参数类型为 Collection<GameProfile>（非 ServerPlayer）
 */
@Mixin(PlayerCommand.class)
public class PlayerCommandSkinMixin {

    @Inject(
            method = "spawn",
            at = @At("TAIL"),
            remap = false
    )
    private static void afterSpawn(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir) {
        String mode = CarpetPrimaryuanSettings.fakePlayerSkinMode;

        if ("default".equals(mode)) {
            return;
        }

        try {
            // 获取召唤者（执行命令的玩家）
            ServerPlayer summoner = null;
            try {
                summoner = context.getSource().getPlayerOrException();
            } catch (Exception ignored) {
                // 命令可能由非玩家执行（如命令方块）
            }

            // 从服务器玩家列表中找到刚生成的假人（EntityPlayerMPFake）
            var server = context.getSource().getServer();
            EntityPlayerMPFake fakePlayer = null;
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                if (p instanceof EntityPlayerMPFake fp) {
                    fakePlayer = fp;
                    break;
                }
            }

            if (fakePlayer == null) {
                return;
            }

            // 根据模式解析皮肤目标玩家名（summon/same_skin 仅来源不同，复用同一套逻辑）
            String skinTargetName = null;

            switch (mode) {
                case "summon" -> {
                    // 使用召唤者的皮肤
                    if (summoner == null) return;
                    skinTargetName = summoner.getGameProfile()
                            //#if MC >= 12110
                            //$$ .name()
                            //#else
                            .getName()
                            //#endif
;
                }
                case "same_skin" -> {
                    // 使用 fakePlayerSkinSet 配置的玩家皮肤
                    skinTargetName = CarpetPrimaryuanSettings.fakePlayerSkinSet;
                    if (skinTargetName == null || skinTargetName.isEmpty()) {
                        return;
                    }
                }
                default -> {
                    return;
                }
            }

            // 通过 Mojang API 获取目标玩家的皮肤并应用到假人
            applySkinToFakePlayer(server, fakePlayer.getGameProfile(), skinTargetName);

        } catch (Exception e) {
        }
    }

    /**
     * 使用 SkinService.setSkinAsync 异步设置假人皮肤（skinrestorer 2.7.0 early API）
     * 参数类型为 GameProfile（非 ServerPlayer）
     */
    private static void applySkinToFakePlayer(net.minecraft.server.MinecraftServer server,
                                               GameProfile targetProfile,
                                               String skinPlayerName) {
        try {
            SkinProviderContext context = new SkinProviderContext("mojang", skinPlayerName, SkinVariant.SLIM);
            SkinService.setSkinAsync(server, Collections.singletonList(targetProfile), context, true);
        } catch (Exception e) {
        }
    }

}
