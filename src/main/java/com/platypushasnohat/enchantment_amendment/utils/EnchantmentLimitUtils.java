package com.platypushasnohat.enchantment_amendment.utils;

import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EnchantmentLimitUtils {

    public static int getLimitCount(ItemStack stack) {
        if (stack.getItem() instanceof EnchantedBookItem || stack.getItem() instanceof BookItem) {
            return 1;
        }
        else if (stack.is(Items.GOLDEN_SWORD)) {
            return 4;
        }
        return 3;
    }
}
