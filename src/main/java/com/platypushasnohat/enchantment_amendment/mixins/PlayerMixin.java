package com.platypushasnohat.enchantment_amendment.mixins;

import com.platypushasnohat.enchantment_amendment.EnchantmentAmendmentConfig;
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
        if (EnchantmentAmendmentConfig.LINEAR_XP.getAsBoolean()) {
            cir.setReturnValue(EnchantmentAmendmentConfig.XP_PER_LEVEL.getAsInt());
        }
    }
}