package com.platypushasnohat.enchantment_amendment.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.platypushasnohat.enchantment_amendment.utils.EnchantmentLimitUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AnvilMenu.class, priority = 100)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Shadow
    @Final
    private DataSlot cost;

    @Shadow
    @Nullable
    private String itemName;

    @Shadow
    public int repairItemCountCost;

    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void enchantmentAmendment$createResult(CallbackInfo ci) {
        AnvilMenu anvilMenu = (AnvilMenu) (Object) this;

        ItemStack itemStack = inputSlots.getItem(0);
        this.cost.set(1);
        int cost = 0;
        long j = 0L;

        if (!itemStack.isEmpty()) {
            if (!CommonHooks.onAnvilChange(anvilMenu, itemStack, inputSlots.getItem(1), resultSlots, itemName, j, player)) {
                return;
            }
        }
        if (!itemStack.isEmpty() && EnchantmentHelper.canStoreEnchantments(itemStack)) {
            ItemStack inputItem = itemStack.copy();
            ItemStack addItem = inputSlots.getItem(1);
            ItemEnchantments.Mutable mutableEnchantments = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(inputItem));
            // always one to ensure only one item is consumed since enchanted books stack now
            this.repairItemCountCost = 1;
            boolean hasStoredEnchantments = false;
            if (!addItem.isEmpty()) {
                hasStoredEnchantments = addItem.has(DataComponents.STORED_ENCHANTMENTS);
                if (inputItem.isDamageableItem() && inputItem.getItem().isValidRepairItem(itemStack, addItem)) {
                    int l2 = Math.min(inputItem.getDamageValue(), inputItem.getMaxDamage() / 4);
                    if (l2 <= 0) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    inputItem.setDamageValue(0);

                    // repair cost increases based on (1 + (anvil cost of all enchantments combined / 4)
                    int enchantSum = EnchantmentHelper.getEnchantmentsForCrafting(inputItem).entrySet().stream().mapToInt(entry -> entry.getKey().value().getAnvilCost() * entry.getIntValue()).sum();
                    int repairCost = 1 + Mth.ceil((float) enchantSum / 4);
                    cost += repairCost;
                } else {
                    if (!hasStoredEnchantments && (!inputItem.is(addItem.getItem()) || !inputItem.isDamageableItem())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    // combine items
                    if (inputItem.isDamageableItem() && !hasStoredEnchantments) {
                        int inputItemDamage = itemStack.getMaxDamage() - itemStack.getDamageValue();
                        int addItemDamage = addItem.getMaxDamage() - addItem.getDamageValue();
                        int j1 = addItemDamage + inputItem.getMaxDamage() * 12 / 100;
                        int k1 = inputItemDamage + j1;
                        int damage = inputItem.getMaxDamage() - k1;
                        if (damage < 0) {
                            damage = 0;
                        }
                        if (damage < inputItem.getDamageValue()) {
                            inputItem.setDamageValue(damage);
                            cost += 1;
                        }
                    }

                    ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(addItem);
                    boolean flag2 = false;
                    boolean flag3 = false;

                    int limit = EnchantmentLimitUtils.getLimitCount(inputItem);
                    for (Object2IntMap.Entry<Holder<Enchantment>> enchantmentEntries : itemEnchantments.entrySet()) {

                        Holder<Enchantment> enchantmentHolder = enchantmentEntries.getKey();
                        int enchantmentLevel = mutableEnchantments.getLevel(enchantmentHolder);
                        int enchantmentValue = enchantmentEntries.getIntValue();
                        Enchantment enchantment = enchantmentHolder.value();
                        enchantmentValue = enchantmentLevel + enchantmentValue;

                        boolean supportsEnchantment = itemStack.supportsEnchantment(enchantmentHolder);
                        if (player.getAbilities().instabuild) {
                            supportsEnchantment = true;
                        }

                        for (Holder<Enchantment> holder1 : mutableEnchantments.keySet()) {
                            if (!holder1.equals(enchantmentHolder) && !Enchantment.areCompatible(enchantmentHolder, holder1)) {
                                supportsEnchantment = false;
                                cost++;
                            }
                        }

                        if (!supportsEnchantment) {
                            flag3 = true;
                        } else {
                            flag2 = true;
                            if (!mutableEnchantments.keySet().contains(enchantmentHolder) && mutableEnchantments.keySet().size() >= limit) {
                                continue;
                            }
                            if (enchantmentValue > enchantment.getMaxLevel()) {
                                enchantmentValue = enchantment.getMaxLevel();
                            }

                            mutableEnchantments.set(enchantmentHolder, enchantmentValue);
                            // cost is equal to the enchantment level * enchantment anvil cost
                            cost += enchantment.getAnvilCost() * enchantmentValue;
                            if (itemStack.getCount() > 1) {
                                cost = -1;
                                // consume multiple books if enchanting multiple items at once
                                // this.repairItemCountCost = itemStack.getCount();
                                // cost *= itemStack.getCount();
                            }
                        }
                    }

                    if (flag3 && !flag2) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }
                }
            }

            if (itemName != null && !StringUtil.isBlank(itemName)) {
                if (!itemName.equals(itemStack.getHoverName().getString())) {
                    inputItem.set(DataComponents.CUSTOM_NAME, Component.literal(itemName));
                }
            } else if (itemStack.has(DataComponents.CUSTOM_NAME)) {
                inputItem.remove(DataComponents.CUSTOM_NAME);
            }

            if (hasStoredEnchantments && !inputItem.isBookEnchantable(addItem)) {
                inputItem = ItemStack.EMPTY;
            }

            int clamped = (int) Mth.clamp(cost, 0L, 2147483647L);
            this.cost.set(clamped);

            boolean hasRename = itemName != null && !StringUtil.isBlank(itemName) && !itemName.equals(itemStack.getHoverName().getString());
            boolean hasOtherChanges = cost > 0;
            if (!hasRename && !hasOtherChanges) {
                inputItem = ItemStack.EMPTY;
            }

            if (!inputItem.isEmpty()) {
                inputItem.set(DataComponents.REPAIR_COST, 0);
                EnchantmentHelper.setEnchantments(inputItem, mutableEnchantments.toImmutable());
            }

            this.resultSlots.setItem(0, inputItem);
            this.broadcastChanges();
            ci.cancel();
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
        }
    }

    // never increase repair cost (needed for grindstone even though we pretty much override everything else related to this)
    @ModifyReturnValue(method = "calculateIncreasedRepairCost", at = @At("RETURN"))
    private static int enchantmentAmendment$noCumulativeRepairCost(int oldRepairCost) {
        return 0;
    }
}
