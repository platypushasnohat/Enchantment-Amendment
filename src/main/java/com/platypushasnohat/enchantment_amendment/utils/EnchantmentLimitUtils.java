package com.platypushasnohat.enchantment_amendment.utils;

import com.platypushasnohat.enchantment_amendment.EnchantmentAmendmentConfig;
import com.platypushasnohat.enchantment_amendment.tags.EAItemTags;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;

public class EnchantmentLimitUtils {

    public static int getLimitCount(ItemStack stack) {
        if (stack.getItem() instanceof EnchantedBookItem || stack.getItem() instanceof BookItem) {
            return EnchantmentAmendmentConfig.BOOK_ENCHANTMENT_CAP.getAsInt();
        }
        else if (stack.is(EAItemTags.GOLDEN_ITEMS)) {
            return EnchantmentAmendmentConfig.GOLDEN_ENCHANTMENT_CAP.getAsInt();
        }
        return EnchantmentAmendmentConfig.ENCHANTMENT_CAP.getAsInt();
    }
}
