package com.platypushasnohat.enchantment_amendment.events;

import com.platypushasnohat.enchantment_amendment.EnchantmentAmendment;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = EnchantmentAmendment.MOD_ID)
public class EAEvents {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void handleBreakSpeedEvent(PlayerEvent.BreakSpeed event) {
        if (event.isCanceled()) {
            return;
        }
        Player player = event.getEntity();
        var enchantments = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var aquaAffinity = enchantments.get(Enchantments.AQUA_AFFINITY);
        if (aquaAffinity.isPresent() && EnchantmentHelper.getEnchantmentLevel(aquaAffinity.get(), player) > 0) {
            if (!player.onGround() && player.isInWaterOrBubble()) {
                event.setNewSpeed(event.getNewSpeed() * 5.0F);
            }
        }
    }
}
