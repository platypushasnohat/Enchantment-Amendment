package com.platypushasnohat.enchantment_amendment.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.platypushasnohat.enchantment_amendment.utils.EnchantmentLimitUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// todo:
//  repair cost = 2 always, item fully repairs
//  renaming = free
//  everything else vanilla cost except no prior work penalty applied
//  no level limit
//  no book-book merging since books hold only one enchantment
//  second slot should only consume 1 item at a time
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Shadow
    @Final
    private DataSlot cost;

    @Shadow
    @Nullable
    private String itemName;

    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @WrapOperation(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;broadcastChanges()V"))
    private void enchantmentAmendment$limitEnchantmentsAnvil(AnvilMenu instance, Operation<Void> original) {
        ItemStack outputStack = resultSlots.getItem(0);
        int limitCount = EnchantmentLimitUtils.getLimitCount(outputStack);
        ItemEnchantments enchantments = outputStack.get(EnchantmentHelper.getComponentType(outputStack));
        if (enchantments != null && enchantments.keySet().size() > limitCount) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
        }
        original.call(instance);
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    private void enchantmentAmendment$overrideFinalAnvil(CallbackInfo ci) {
        ItemStack input = inputSlots.getItem(0);
        ItemStack extra = inputSlots.getItem(1);
        ItemStack result = resultSlots.getItem(0);
        if (input.isEmpty() || result.isEmpty()) return;
        if (input.isDamageableItem() && !extra.isEmpty()) {
            if (input.getItem().isValidRepairItem(input, extra)) {
                result.setDamageValue(0);
            }
        }
        if (itemName != null && !itemName.isBlank()) {
            result.set(DataComponents.CUSTOM_NAME, Component.literal(itemName));
        } else {
            result.remove(DataComponents.CUSTOM_NAME);
        }
        result.remove(DataComponents.REPAIR_COST);
        this.cost.set(2);
    }
}
