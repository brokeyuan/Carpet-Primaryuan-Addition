package me.primaryuan.carpet.mixins.rule.entitiesRidingPlayers;

import me.primaryuan.carpet.CarpetPrimaryuanSettings;
import me.primaryuan.carpet.handler.entitiesRidingPlayers.EntitiesRidingPlayersHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void ridingPlayers$onTick(CallbackInfo callbackInfo) {
        if (CarpetPrimaryuanSettings.ridingPlayers || CarpetPrimaryuanSettings.pickupPlayers) {
            EntitiesRidingPlayersHandler.onPlayerTick((Player) (Object) this);
        }
    }
}
