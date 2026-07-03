package com.platypushasnohat.enchantment_amendment.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.platypushasnohat.enchantment_amendment.utils.EnchantmentLimitUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "enchant", at = @At("HEAD"), cancellable = true)
    private void enchantmentAmendment$limitEnchantments(Holder<Enchantment> enchantment, int level, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        int limit = EnchantmentLimitUtils.getLimitCount(stack);
        if (!enchantments.keySet().contains(enchantment) && enchantments.keySet().size() >= limit) {
            ci.cancel();
        }
    }

    // add enchantment slots unless its a book
    @WrapOperation(method = "getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addToTooltip(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void enchantmentAmendment$enchantmentLimitTooltip(ItemStack stack, DataComponentType<?> componentType, Item.TooltipContext context, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag, Operation<Void> original) {
        if (componentType == DataComponents.ENCHANTMENTS) {
            ItemEnchantments existingEnchantments = stack.getTagEnchantments();
            int enchantmentCount = existingEnchantments.keySet().size();
            if (!(stack.getItem() instanceof EnchantedBookItem) && enchantmentCount > 0) {
                int maxEnchantments = EnchantmentLimitUtils.getLimitCount(stack);
                StringBuilder slots = new StringBuilder();
                for (int i = 0; i < maxEnchantments; i++) {
                    slots.append(i < enchantmentCount ? '◆' : '◇');
                }
                componentConsumer.accept(Component.literal(slots.toString()).withStyle(ChatFormatting.BLUE));
            }
        }
        original.call(stack, componentType, context, componentConsumer, tooltipFlag);
    }
}
