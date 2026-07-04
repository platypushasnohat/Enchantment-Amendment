package com.platypushasnohat.enchantment_amendment.utils;

import com.platypushasnohat.enchantment_amendment.tags.EAItemTags;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;

public class EnchantmentLimitUtils {

    public static int getLimitCount(ItemStack stack) {
        if (stack.getItem() instanceof EnchantedBookItem || stack.getItem() instanceof BookItem) {
            return 1;
        }
        else if (stack.is(EAItemTags.GOLDEN_ITEMS)) {
            return 4;
        }
        return 3;
    }
}
