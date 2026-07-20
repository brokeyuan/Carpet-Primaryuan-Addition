package me.primaryuan.carpet.mixins.rule.sleepingDuringTheDay;

import org.spongepowered.asm.mixin.Mixin;

/**
 * MixinServerLevel - v20 (醒来时修正时间)
 *
 * v20 设计变更：
 *   不再在每个 tick 修正时间（v14-v19 的错误做法）
 *   时间修正逻辑已移至 MixinPlayerBase（在 sleepTimer=100 时执行）
 *
 * 此类保留为空 mixin 以备将来需要
 */
@Mixin(net.minecraft.server.level.ServerLevel.class)
public abstract class MixinServerLevel {
    // v20: 移除了所有 per-tick 时间修正逻辑
    // 时间修正现在在 MixinPlayerBase.stopSleepInBed() 中处理
}
