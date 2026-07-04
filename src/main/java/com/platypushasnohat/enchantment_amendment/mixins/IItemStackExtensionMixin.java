package com.platypushasnohat.enchantment_amendment.mixins;

import com.platypushasnohat.enchantment_amendment.EnchantmentAmendmentConfig;
import com.platypushasnohat.enchantment_amendment.utils.EnchantmentLimitUtils;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IItemStackExtension.class)
public interface IItemStackExtensionMixin {

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "supportsEnchantment", at = @At("HEAD"), cancellable = true)
    private void enchantmentAmendment$supportsEnchantment(Holder<Enchantment> enchantment, CallbackInfoReturnable<Boolean> cir) {
        if (EnchantmentAmendmentConfig.LIMITED_ENCHANTMENTS.getAsBoolean()) {
            ItemStack stack = (ItemStack) (Object) this;
            ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
            int limit = EnchantmentLimitUtils.getLimitCount(stack);
            if (!enchantments.keySet().contains(enchantment) && enchantments.keySet().size() >= limit) {
                cir.setReturnValue(false);
            }
        }
    }
}