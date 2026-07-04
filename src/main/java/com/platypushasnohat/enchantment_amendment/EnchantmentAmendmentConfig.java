package com.platypushasnohat.enchantment_amendment;

import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec;

public class EnchantmentAmendmentConfig {

    public static IConfigSpec COMMON_CONFIG;

    public static ModConfigSpec.BooleanValue ENCHANTMENT_TWEAKS;

    public static ModConfigSpec.IntValue XP_PER_LEVEL;
    public static ModConfigSpec.BooleanValue LINEAR_XP;
    public static ModConfigSpec.IntValue XP_ORB_DELAY;
    public static ModConfigSpec.BooleanValue BOTTLE_XP;

    public static ModConfigSpec.BooleanValue ANVIL_TWEAKS;
    public static ModConfigSpec.BooleanValue REPAIRABLE_ANVILS;
    public static ModConfigSpec.DoubleValue ANVIL_USE_BREAK_CHANCE;

    public static ModConfigSpec.BooleanValue LIMITED_ENCHANTMENTS;
    public static ModConfigSpec.IntValue ENCHANTMENT_CAP;
    public static ModConfigSpec.IntValue GOLDEN_ENCHANTMENT_CAP;
    public static ModConfigSpec.IntValue BOOK_ENCHANTMENT_CAP;

    public static ModConfigSpec.BooleanValue STACKABLE_ENCHANTED_BOOKS;

    public static ModConfigSpec.BooleanValue AQUA_AFFINITY_REMOVES_PENALTY;

    public static ModConfigSpec.BooleanValue RIPTIDE_CONDITIONS;
    public static ModConfigSpec.BooleanValue FLEXIBLE_TRIDENTS;

    static {
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
        ENCHANTMENT_TWEAKS = COMMON_BUILDER.comment("Whether enchantment tweaks should be enabled by default").define("enchantmentTweaks", true);
        LIMITED_ENCHANTMENTS = COMMON_BUILDER.comment("Whether items should have a limited number of enchantment slots").define("limitedEnchantments", true);
        ENCHANTMENT_CAP = COMMON_BUILDER.comment("The amount of enchantments enchantable items can have").defineInRange("enchantmentCap", 3, 1, 12);
        GOLDEN_ENCHANTMENT_CAP = COMMON_BUILDER.comment("The amount of enchantments golden enchantable items can have").defineInRange("goldenEnchantmentCap", 4, 1, 12);
        BOOK_ENCHANTMENT_CAP = COMMON_BUILDER.comment("The amount of enchantments books can have").defineInRange("bookEnchantmentCap", 1, 1, 12);

        ANVIL_TWEAKS = COMMON_BUILDER.comment("Whether anvil tweaks are enabled").define("anvilTweaks", true);
        REPAIRABLE_ANVILS = COMMON_BUILDER.comment("Whether anvils can be repaired one stage with an iron block").define("repairableAnvils", true);
        ANVIL_USE_BREAK_CHANCE = COMMON_BUILDER.comment("The chance an anvil will break when used (Vanilla is 12%)").defineInRange("anvilUseBreakChance", 0.08D, 0.0D, 1.0D);

        LINEAR_XP = COMMON_BUILDER.comment("Whether experience gain should be linear").define("linearXp", true);
        XP_PER_LEVEL = COMMON_BUILDER.comment("Experience needed per level (if linearXp is enabled)").defineInRange("xpPerLevel", 30, 0, 10000);
        XP_ORB_DELAY = COMMON_BUILDER.comment("Experience orb pickup delay (Vanilla is 2)").defineInRange("xpOrbDelay", 0, 0, 10000);
        BOTTLE_XP = COMMON_BUILDER.comment("Whether bottles o' enchanting can be obtained by using empty bottles on yourself and always give (xpPerLevel / 2) per bottle").define("bottleXp", true);

        STACKABLE_ENCHANTED_BOOKS = COMMON_BUILDER.comment("Whether enchanted books can be stacked to 64").define("stackableEnchantedBooks", true);

        AQUA_AFFINITY_REMOVES_PENALTY = COMMON_BUILDER.comment("Whether aqua affinity should remove the air mining penalty in water").define("aquaAffinityRemovesPenalty", true);

        RIPTIDE_CONDITIONS = COMMON_BUILDER.comment("Whether tridents with riptide should require being on ground and not sneaking").define("riptideConditions", true);
        FLEXIBLE_TRIDENTS = COMMON_BUILDER.comment("Whether tridents with riptide can be thrown when riptide cannot be used").define("flexibleTridents", true);

        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
