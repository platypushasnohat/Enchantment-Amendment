package com.platypushasnohat.enchantment_amendment.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {

    // makes the game think the player is creative always at too expensive check so too expensive is skipped
    @ModifyExpressionValue(method = "renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;instabuild:Z", ordinal = 0, opcode = Opcodes.GETFIELD))
    public boolean enchantmentAmendment$removeTooExpensive(boolean original) {
        return true;
    }
}