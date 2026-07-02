package com.platypushasnohat.enchantment_amendment.mixins;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "isEnchantmentCompatible", at = @At("HEAD"), cancellable = true)
    private static void enchantmentAmendment$checkEnchantmentsAboveLimit(Collection<Holder<Enchantment>> currentEnchantments, Holder<Enchantment> newEnchantment, CallbackInfoReturnable<Boolean> cir) {
        if (currentEnchantments.size() >= 3 && !currentEnchantments.contains(newEnchantment)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "selectEnchantment(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILjava/util/stream/Stream;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    private static void enchantmentAmendment$limitEnchantmentSelect(RandomSource random, ItemStack stack, int level, Stream<Holder<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        List<EnchantmentInstance> selectedEnchantments = cir.getReturnValue();
        int limitCount = 3;
        if (!selectedEnchantments.isEmpty() && selectedEnchantments.size() > limitCount) {
            ArrayList<EnchantmentInstance> shuffled = new ArrayList<>(selectedEnchantments);
            Collections.shuffle(shuffled);
            int limit = Math.min(limitCount, shuffled.size());
            ArrayList<EnchantmentInstance> selected = new ArrayList<>(shuffled.subList(0, limit));
            cir.setReturnValue(selected);
        }
    }
}