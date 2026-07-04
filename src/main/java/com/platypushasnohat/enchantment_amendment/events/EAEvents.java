package com.platypushasnohat.enchantment_amendment.events;

import com.platypushasnohat.enchantment_amendment.EnchantmentAmendment;
import com.platypushasnohat.enchantment_amendment.EnchantmentAmendmentConfig;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = EnchantmentAmendment.MOD_ID)
public class EAEvents {

    @SubscribeEvent
    public static void modifyItemComponents(ModifyDefaultComponentsEvent event) {
        if (EnchantmentAmendmentConfig.STACKABLE_ENCHANTED_BOOKS.getAsBoolean()) {
            event.modify(Items.ENCHANTED_BOOK, builder -> builder.set(DataComponents.MAX_STACK_SIZE, 64));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void handleBreakSpeedEvent(PlayerEvent.BreakSpeed event) {
        if (EnchantmentAmendmentConfig.AQUA_AFFINITY_REMOVES_PENALTY.getAsBoolean() && !event.isCanceled()) {
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

    // use empty bottles to gather your own xp
    @SubscribeEvent
    public static void onClickBottle(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        Level level = event.getLevel();

        if (EnchantmentAmendmentConfig.BOTTLE_XP.getAsBoolean()) {
            if (!level.isClientSide && stack.is(Items.GLASS_BOTTLE)) {
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
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.15F, 0.9F + player.level().getRandom().nextFloat() * 0.15F);

                ItemStack xpBottle = new ItemStack(Items.EXPERIENCE_BOTTLE, used);

                if (!player.getInventory().add(xpBottle)) {
                    player.drop(xpBottle, false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void setAnvilBreakChance(AnvilRepairEvent event) {
        event.setBreakChance((float) EnchantmentAmendmentConfig.ANVIL_USE_BREAK_CHANCE.getAsDouble());
    }

    @SubscribeEvent
    public static void onAnvilClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (EnchantmentAmendmentConfig.REPAIRABLE_ANVILS.getAsBoolean()) {
            if (state.is(Blocks.CHIPPED_ANVIL) || state.is(Blocks.DAMAGED_ANVIL) && !level.isClientSide && stack.is(Tags.Items.STORAGE_BLOCKS_IRON)) {
                BlockState repaired;
                if (block.equals(Blocks.CHIPPED_ANVIL)) {
                    repaired = Blocks.ANVIL.defaultBlockState();
                    level.setBlock(pos, repaired.setValue(AnvilBlock.FACING, state.getValue(AnvilBlock.FACING)), 3);
                } else if (block.equals(Blocks.DAMAGED_ANVIL)) {
                    repaired = Blocks.CHIPPED_ANVIL.defaultBlockState();
                    level.setBlock(pos, repaired.setValue(AnvilBlock.FACING, state.getValue(AnvilBlock.FACING)), 3);
                } else {
                    event.setCancellationResult(InteractionResult.PASS);
                }

                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}
