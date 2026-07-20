package me.primaryuan.carpet.mixins.rule.sleepingDuringTheDay;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.primaryuan.carpet.CarpetPrimaryuanSettings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * MixinPlayerBase - v20 (醒来时修正时间) - 生产版本（无日志）
 *
 * 核心逻辑：
 *   - sleepTimer=100 + 白天(dayTime < 13000) → 允许唤醒 + 设置时间为夜晚(13000)
 *   - sleepTimer<100  + 白天(dayTime < 13000) → 阻止唤醒（继续睡觉）
 *   - 规则关闭 → 完全放行原版逻辑
 */
@Mixin(net.minecraft.world.entity.player.Player.class)
public abstract class MixinPlayerBase {

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;stopSleepInBed(ZZ)V"
            )
    )
    private void onStopSleepInBed(net.minecraft.world.entity.player.Player player, boolean wakeImmediately, boolean updateLevel, Operation<Void> original) {
        // 规则关闭：完全放行
        if (!CarpetPrimaryuanSettings.sleepingDuringTheDay) {
            original.call(player, wakeImmediately, updateLevel);
            return;
        }

        // 获取当前时间和睡眠计时器
        long dayTime = 0;
        if (player.level() instanceof Level level) {
            dayTime = level.getDayTime() % 24000L;
        }
        int sleepTimer = player.getSleepTimer();

        // 判断是否是自然醒来（sleepTimer=100）
        boolean isNaturalWakeUp = (sleepTimer >= 100);
        
        // 判断是否是白天
        boolean isDaytime = (dayTime >= 0L && dayTime < 13000L);

        if (isNaturalWakeUp && isDaytime) {
            // ✅ 白天自然醒来 → 设置时间为夜晚，然后允许唤醒
            setTimeToNight(player);
            original.call(player, wakeImmediately, updateLevel);
            
        } else if (!isNaturalWakeUp && isDaytime) {
            // ⛔ 白天非自然唤醒 → 阻止（保持睡觉状态）
            // 不调用 original，阻止唤醒
            
        } else {
            // ℹ️ 夜间或其他情况 → 放行原版逻辑
            original.call(player, wakeImmediately, updateLevel);
        }
    }

    /**
     * 设置时间为夜晚（使用 /time set 命令）
     */
    private void setTimeToNight(net.minecraft.world.entity.player.Player player) {
        try {
            if (player.level() instanceof ServerLevel serverLevel) {
                long currentTime = serverLevel.getServer().overworld().getDayTime();
                long currentDayTime = currentTime % 24000L;
                long targetTime = currentTime + (13000L - currentDayTime);

                String timeCmd = "/time set " + targetTime;
                serverLevel.getServer().getCommands().performPrefixedCommand(
                    serverLevel.getServer().createCommandSourceStack(), 
                    timeCmd
                );
            }
        } catch (Exception e) {
            // 静默失败，不影响游戏体验
        }
    }
}
