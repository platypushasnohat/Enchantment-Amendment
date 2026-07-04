package com.platypushasnohat.enchantment_amendment.tags;

import com.platypushasnohat.enchantment_amendment.EnchantmentAmendment;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class EAItemTags {

    public static final TagKey<Item> GOLDEN_ITEMS = modItemTag("golden_items");

    private static TagKey<Item> modItemTag(String name) {
        return itemTag(EnchantmentAmendment.MOD_ID, name);
    }

    private static TagKey<Item> commonItemTag(String name) {
        return itemTag("c", name);
    }

    public static TagKey<Item> itemTag(String modId, String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(modId, name));
    }
}
