package me.primaryuan.carpet.mixins.rule.sleepingDuringTheDay;

import me.primaryuan.carpet.CarpetPrimaryuanSettings;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * MixinPlayer - 允许白天开始睡觉 (生产版本 - 无日志)
 */
@Mixin(net.minecraft.server.level.ServerPlayer.class)
public abstract class MixinPlayer {

    @org.spongepowered.asm.mixin.injection.Redirect(
            method = "startSleepInBed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/attribute/BedRule;canSleep(Lnet/minecraft/world/level/Level;)Z"
            )
    )
    private boolean redirectCanSleep(BedRule bedRule, Level level) {
        boolean originalResult = bedRule.canSleep(level);

        if (!originalResult && CarpetPrimaryuanSettings.sleepingDuringTheDay) {
            return true;
        }

        return originalResult;
    }
}
