package com.platypushasnohat.enchantment_amendment.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TridentItem.class)
public class TridentItemMixin {

    @ModifyExpressionValue(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getTridentSpinAttackStrength(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)F"))
    private float enchantmentAmendment$tridentSpinAttackRelease(float original, ItemStack stack, net.minecraft.world.level.Level level, LivingEntity entity, int timeLeft) {
        if (original > 0.0F && entity instanceof Player player && !this.enchantmentAmendment$canPlayerRiptide(player)) {
            return 0.0F;
        }
        return original;
    }

    @ModifyExpressionValue(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getTridentSpinAttackStrength(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)F"))
    private float enchantmentAmendment$tridentSpinAttackUse(float original, Level level, Player player, InteractionHand hand) {
        if (original > 0.0F && !this.enchantmentAmendment$canPlayerRiptide(player)) {
            return 0.0F;
        }
        return original;
    }

    @WrapOperation(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", ordinal = 0))
    private void enchantmentAmendment$tridentThrowSound(Level level, Player sourcePlayer, Entity entity, SoundEvent sound, SoundSource source, float volume, float pitch, Operation<Void> original, @Local(argsOnly = true) LivingEntity entityLiving) {
        if (entityLiving instanceof Player player && !this.enchantmentAmendment$canPlayerRiptide(player)) {
            sound = SoundEvents.TRIDENT_THROW.value();
        }
        original.call(level, sourcePlayer, entity, sound, source, volume, pitch);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Unique
    private boolean enchantmentAmendment$canPlayerRiptide(Player player) {
        return !player.isShiftKeyDown() && (player.isInWater() || (player.isInRain() && player.onGround()));
    }
}
