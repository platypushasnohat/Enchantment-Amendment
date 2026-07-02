package com.platypushasnohat.enchantment_amendment.mixins;

import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ThrownExperienceBottle.class)
public class ThrownExperienceBottleMixin {

    @ModifyVariable(method = "onHit", at = @At(value = "STORE"), ordinal = 0)
    private int enchantmentAmendment$setXpAmountFromBottle(int original) {
        return 10;
    }
}
