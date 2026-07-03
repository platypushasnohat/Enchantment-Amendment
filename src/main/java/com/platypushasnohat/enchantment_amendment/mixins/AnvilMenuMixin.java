package com.platypushasnohat.enchantment_amendment.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
import org.spongepowered.asm.mixin.injection.*;
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
            this.repairItemCountCost = 0;
            boolean flag = false;
            if (!addItem.isEmpty()) {
                flag = addItem.has(DataComponents.STORED_ENCHANTMENTS);
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
                    this.repairItemCountCost = 1;
                } else {
                    if (!flag && (!inputItem.is(addItem.getItem()) || !inputItem.isDamageableItem())) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.cost.set(0);
                        return;
                    }

                    // combine items
                    if (inputItem.isDamageableItem() && !flag) {
                        int l = itemStack.getMaxDamage() - itemStack.getDamageValue();
                        int i1 = addItem.getMaxDamage() - addItem.getDamageValue();
                        int j1 = i1 + inputItem.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = inputItem.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }
                        if (l1 < inputItem.getDamageValue()) {
                            inputItem.setDamageValue(l1);
                            cost += 2;
                        }
                    }

                    ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(addItem);
                    boolean flag2 = false;
                    boolean flag3 = false;

                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
                        Holder<Enchantment> holder = entry.getKey();
                        int i2 = mutableEnchantments.getLevel(holder);
                        int j2 = entry.getIntValue();
                        j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                        Enchantment enchantment = holder.value();
                        // Neo: Respect IItemExtension#supportsEnchantment - we also delegate the logic for Enchanted Books to this method.
                        // Though we still allow creative players to combine any item with any enchantment in the anvil here.
                        boolean flag1 = itemStack.supportsEnchantment(holder);
                        if (this.player.getAbilities().instabuild) {
                            flag1 = true;
                        }

                        for (Holder<Enchantment> holder1 : mutableEnchantments.keySet()) {
                            if (!holder1.equals(holder) && !Enchantment.areCompatible(holder, holder1)) {
                                flag1 = false;
                                cost++;
                            }
                        }

                        if (!flag1) {
                            flag3 = true;
                        } else {
                            flag2 = true;
                            if (j2 > enchantment.getMaxLevel()) {
                                j2 = enchantment.getMaxLevel();
                            }

                            mutableEnchantments.set(holder, j2);
                            int l3 = enchantment.getAnvilCost();
                            if (flag) {
                                l3 = Math.max(1, l3 / 2);
                            }

                            cost += l3 * j2;
                            if (itemStack.getCount() > 1) {
                                cost = 40;
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

            if (flag && !inputItem.isBookEnchantable(addItem)) inputItem = ItemStack.EMPTY;

            int k2 = (int) Mth.clamp(cost, 0L, 2147483647L);
            this.cost.set(k2);
            if (cost <= 0) {
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

        // new
//        ItemStack input = inputSlots.getItem(0);
//
//        this.cost.set(1);
//
//        int cost = 0;
//        long l = 0L;
//
//        if (!input.isEmpty() && !CommonHooks.onAnvilChange(anvilMenu, input, inputSlots.getItem(1), resultSlots, itemName, l, player)) {
//            ci.cancel();
//            return;
//        }
//
//        ItemStack result = input.copy();
//
//        if (!input.isEmpty() && EnchantmentHelper.canStoreEnchantments(input)) {
//            ItemEnchantments.Mutable mutableEnchantment = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(result));
//            ItemStack right = inputSlots.getItem(1);
//            boolean rightHasBook = !right.isEmpty() && right.has(DataComponents.STORED_ENCHANTMENTS);
//            this.repairItemCountCost = 0;
//
//            if (!right.isEmpty()) {
//                if (result.isDamageableItem() && result.getItem().isValidRepairItem(input, right) && !rightHasBook) {
//                    result.setDamageValue(0);
//                    int enchantSum = EnchantmentHelper.getEnchantmentsForCrafting(result).entrySet().stream().mapToInt(entry -> entry.getKey().value().getAnvilCost() * entry.getIntValue()).sum();
//
//                    // repair cost increases based on (1 + (anvil cost of all enchantments combined / 4)
//                    int repairCost = 1 + Mth.ceil((float) enchantSum / 4);
//                    cost += repairCost;
//                    this.repairItemCountCost = 1;
//                }
//                else {
//                    ItemEnchantments rightEnchants = EnchantmentHelper.getEnchantmentsForCrafting(right);
//                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : rightEnchants.entrySet()) {
//                        Holder<Enchantment> holder = entry.getKey();
//                        Enchantment enchantment = holder.value();
//                        int finalLevel = mutableEnchantment.getLevel(holder) + entry.getIntValue();
//                        finalLevel = Math.min(finalLevel, enchantment.getMaxLevel());
//                        boolean compatible = result.supportsEnchantment(holder) || player.getAbilities().instabuild;
//                        for (Holder<Enchantment> other : mutableEnchantment.keySet()) {
//                            if (!other.equals(holder) && !Enchantment.areCompatible(holder, other)) {
//                                compatible = false;
//                                cost++;
//                            }
//                        }
//                        if (!compatible) {
//                            continue;
//                        }
//
//                        mutableEnchantment.set(holder, finalLevel);
//                        cost += enchantment.getAnvilCost() * finalLevel;
//                    }
//                }
//            }
//
//            if (itemName != null && !StringUtil.isBlank(itemName)) {
//                if (!itemName.equals(input.getHoverName().getString())) {
//                    result.set(DataComponents.CUSTOM_NAME, Component.literal(itemName));
//                }
//            }
//
//            EnchantmentHelper.setEnchantments(result, mutableEnchantment.toImmutable());
//
//            int total = Mth.clamp(cost, 0, Integer.MAX_VALUE);
//            this.cost.set(total);
//            this.resultSlots.setItem(0, result);
//
//            this.broadcastChanges();
//            ci.cancel();
//        } else {
//            this.resultSlots.setItem(0, ItemStack.EMPTY);
//            this.cost.set(0);
//            ci.cancel();
//        }
    }

    // never increase repair cost (needed for grindstone even though we pretty much override everything else related to this)
    @ModifyReturnValue(method = "calculateIncreasedRepairCost", at = @At("RETURN"))
    private static int enchantmentAmendment$noCumulativeRepairCost(int oldRepairCost) {
        return 0;
    }
}
