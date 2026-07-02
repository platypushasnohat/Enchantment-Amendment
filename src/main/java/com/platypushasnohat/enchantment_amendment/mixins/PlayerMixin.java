package com.platypushasnohat.enchantment_amendment.mixins;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    @Shadow
    public int experienceLevel;

    @Inject(at = @At("HEAD"), method = "getXpNeededForNextLevel()I", cancellable = true)
    private void enchantmentAmendment$tweakXpCurve(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(10 + experienceLevel * 2);
    }
}