package com.platypushasnohat.enchantment_amendment.mixins;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    // linear xp levels
    @Inject(at = @At("HEAD"), method = "getXpNeededForNextLevel", cancellable = true)
    private void enchantmentAmendment$tweakXpCurve(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(30);
    }

}