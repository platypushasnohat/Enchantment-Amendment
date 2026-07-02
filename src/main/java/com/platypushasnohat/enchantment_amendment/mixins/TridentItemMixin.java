package com.platypushasnohat.enchantment_amendment.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TridentItem.class)
public class TridentItemMixin {

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isInWaterOrRain()Z"))
    private boolean enchantmentAmendment$tridentUse(Player player) {
        return this.enchantmentAmendment$canPlayerRiptide(player);
    }

    @Redirect(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isInWaterOrRain()Z"))
    private boolean enchantmentAmendment$tridentReleaseUsing(Player player) {
        return this.enchantmentAmendment$canPlayerRiptide(player);
    }

    @Unique
    private boolean enchantmentAmendment$canPlayerRiptide(Player player) {
        return player.isInWater() || (this.enchantmentAmendment$isPlayerInRain(player) && player.onGround());
    }

    @Unique
    private boolean enchantmentAmendment$isPlayerInRain(Player player) {
        BlockPos blockpos = player.blockPosition();
        return player.level().isRainingAt(blockpos) || player.level().isRainingAt(BlockPos.containing(blockpos.getX(), player.getBoundingBox().maxY, blockpos.getZ()));
    }
}
