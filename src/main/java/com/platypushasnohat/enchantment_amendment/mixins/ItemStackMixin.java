package com.platypushasnohat.enchantment_amendment.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @WrapOperation(method = "getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addToTooltip(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void enchantmentAmendment$enchantmentLimitTooltip(ItemStack instance, DataComponentType<?> componentType, Item.TooltipContext context, Consumer<Component> textConsumer, TooltipFlag type, Operation<Void> original) {
        original.call(instance, componentType, context, textConsumer, type);
        if (componentType == DataComponents.ENCHANTMENTS) {
            ItemEnchantments existingEnchantments = instance.getTagEnchantments();
            int enchantmentCount = existingEnchantments.keySet().size();
            if (enchantmentCount > 0) {
                textConsumer.accept(Component.translatable("item.enchantment_amendment.enchantment_limit", enchantmentCount, 3).withStyle(ChatFormatting.BLUE));
            }
        }
    }
}
