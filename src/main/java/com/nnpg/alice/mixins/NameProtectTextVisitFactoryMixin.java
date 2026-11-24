package com.nnpg.alice.mixins;

import com.nnpg.alice.bridge.NameProtectionBridge;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextVisitFactory.class)
public abstract class NameProtectTextVisitFactoryMixin {
   @ModifyArg(
      method = "method_27472(Ljava/lang/String;ILnet/minecraft/class_2583;Lnet/minecraft/class_5224;)Z",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_5223;method_27473(Ljava/lang/String;ILnet/minecraft/class_2583;Lnet/minecraft/class_2583;Lnet/minecraft/class_5224;)Z",
         ordinal = 0
      ),
      index = 0
   )
   private static String alice$protectVanillaText(String var0) {
      return NameProtectionBridge.protect(var0);
   }
}
