package com.platypushasnohat.enchantment_amendment;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.Locale;

@Mod(EnchantmentAmendment.MOD_ID)
public class EnchantmentAmendment {

    public static final String MOD_ID = "enchantment_amendment";

    public EnchantmentAmendment(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::addPackFinders);
    }

    public static ResourceLocation modPrefix(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name.toLowerCase(Locale.ROOT));
    }

    public void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            event.addPackFinders(modPrefix("datapacks/enchantment_amendment"), PackType.SERVER_DATA, Component.literal("Enchantment Amendments"), PackSource.BUILT_IN, true, Pack.Position.TOP);
        }
    }
}
