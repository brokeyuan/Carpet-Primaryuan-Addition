package me.primaryuan.carpet.mixins.rule.entitiesRidingPlayers;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.commands.RideCommand.class)
public abstract class RideCommandMixin {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void carpetPrimaryuan$cancelVanillaRegister(CommandDispatcher<CommandSourceStack> dispatcher, CallbackInfo ci) {
        ci.cancel();
    }
}