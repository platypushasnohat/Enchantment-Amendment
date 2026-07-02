package com.platypushasnohat.enchantment_amendment.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Shadow
    @Final
    private DataSlot cost;

    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @WrapOperation(method = "createResult()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;broadcastChanges()V"))
    private void enchantmentAmendment$limitEnchantmentsAnvil(AnvilMenu instance, Operation<Void> original) {
        ItemStack outputStack = resultSlots.getItem(0);
        int limitCount = 3;
        ItemEnchantments enchantments = outputStack.get(DataComponents.ENCHANTMENTS);
        if (enchantments != null && enchantments.keySet().size() > limitCount) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
        }
        original.call(instance);
    }
}
