package com.platypushasnohat.enchantment_amendment.utils;

import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;

public class EnchantmentLimitUtils {

    public static int getLimitCount(ItemStack stack) {
       return (stack.getItem() instanceof EnchantedBookItem || stack.getItem() instanceof BookItem) ? Integer.MAX_VALUE : 3;
    }
}
