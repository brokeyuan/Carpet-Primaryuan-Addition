package me.primaryuan.carpet.mixins.rule.betterSnowBall;

import me.primaryuan.carpet.CarpetPrimaryuanSettings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = {"net.minecraft.world.entity.projectile.Snowball", "net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball"})
public abstract class SnowballMixin {
}