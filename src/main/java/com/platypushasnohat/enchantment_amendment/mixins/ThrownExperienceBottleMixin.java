package com.platypushasnohat.enchantment_amendment.mixins;

import com.platypushasnohat.enchantment_amendment.EnchantmentAmendmentConfig;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ThrownExperienceBottle.class)
public class ThrownExperienceBottleMixin {

    // always give half a xp level
    @ModifyVariable(method = "onHit", at = @At(value = "STORE"), ordinal = 0)
    private int enchantmentAmendment$setXpAmountFromBottle(int original) {
        if (EnchantmentAmendmentConfig.BOTTLE_XP.getAsBoolean()) {
            return EnchantmentAmendmentConfig.XP_PER_LEVEL.getAsInt() / 2;
        }
        return original;
    }
}
