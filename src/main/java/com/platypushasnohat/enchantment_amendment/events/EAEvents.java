package com.platypushasnohat.enchantment_amendment.events;

import com.platypushasnohat.enchantment_amendment.EnchantmentAmendment;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = EnchantmentAmendment.MOD_ID)
public class EAEvents {

    @SubscribeEvent
    public static void modifyItemComponents(ModifyDefaultComponentsEvent event) {
        event.modify(Items.ENCHANTED_BOOK, builder -> builder.set(DataComponents.MAX_STACK_SIZE, 64));
    }

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

    // use empty bottles to gather your own xp
    @SubscribeEvent
    public static void onClickBottle(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());

        if (!player.level().isClientSide && stack.is(Items.GLASS_BOTTLE)) {
            int available = player.totalExperience / 15;
            int used = player.isShiftKeyDown() ? Math.min(stack.getCount(), available) : Math.min(1, available);

            if (used <= 0) {
                return;
            }

            int xpAmount = used * 15;

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);

            if (!player.isCreative()) {
                stack.shrink(used);
            }

            player.giveExperiencePoints(-xpAmount);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.15F, 0.9F + player.level().getRandom().nextFloat() * 0.15F);

            ItemStack xpBottle = new ItemStack(Items.EXPERIENCE_BOTTLE, used);

            if (!player.getInventory().add(xpBottle)) {
                player.drop(xpBottle, false);
            }
        }
    }
}
