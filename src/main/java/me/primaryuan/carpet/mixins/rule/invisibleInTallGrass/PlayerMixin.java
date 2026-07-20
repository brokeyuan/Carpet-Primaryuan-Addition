package me.primaryuan.carpet.mixins.rule.invisibleInTallGrass;

import me.primaryuan.carpet.handler.invisibleInTallGrass.InvisibleInTallGrassHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void invisibleInTallGrass$onTick(CallbackInfo ci) {
        InvisibleInTallGrassHandler.checkAndApplyInvisibility((Player) (Object) this);
    }
}