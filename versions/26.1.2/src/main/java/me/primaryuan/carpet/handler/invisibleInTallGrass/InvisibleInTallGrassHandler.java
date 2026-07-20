package me.primaryuan.carpet.handler.invisibleInTallGrass;

import me.primaryuan.carpet.CarpetPrimaryuanSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class InvisibleInTallGrassHandler {

    public static void checkAndApplyInvisibility(Player player) {
        if (!CarpetPrimaryuanSettings.invisibleInTallGrass) {
            return;
        }

        boolean inGrass = isInTallGrass(player);

        if (inGrass && !player.hasEffect(MobEffects.INVISIBILITY)) {
            player.addEffect(new MobEffectInstance(
                    MobEffects.INVISIBILITY,
                    Integer.MAX_VALUE,
                    0,
                    false,
                    false
            ));
        } else if (!inGrass && player.hasEffect(MobEffects.INVISIBILITY)) {
            player.removeEffect(MobEffects.INVISIBILITY);
        }
    }

    private static boolean isInTallGrass(Player player) {
        Level level = player.level();
        Vec3 eyePos = player.getEyePosition();
        BlockPos headPos = BlockPos.containing(eyePos);

        Block block = level.getBlockState(headPos).getBlock();
        return block == Blocks.TALL_GRASS;
    }
}