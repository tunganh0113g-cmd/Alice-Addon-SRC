package com.nnpg.alice.mixins;

import com.nnpg.alice.bridge.NameProtectionBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(targets = "meteordevelopment/meteorclient/renderer/text/CustomTextRenderer", remap = false)
public abstract class NameProtectMeteorTextRendererMixin {
   @ModifyArgs(method = "getWidth", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/renderer/text/Font;getWidth(Ljava/lang/String;I)D"))
   private void alice$protectWidthText(Args var1) {
      if (NameProtectionBridge.cJ()) {
         String var2 = (String)var1.get(0);
         int var3 = (Integer)var1.get(1);
         int var4 = Math.max(0, Math.min(var3, var2.length()));
         String var5 = NameProtectionBridge.protect(var2.substring(0, var4));
         var1.set(0, var5);
         var1.set(1, var5.length());
      }
   }

   @ModifyArg(
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lmeteordevelopment/meteorclient/renderer/text/Font;render(Lmeteordevelopment/meteorclient/renderer/MeshBuilder;Ljava/lang/String;DDLmeteordevelopment/meteorclient/utils/render/color/Color;D)D"
      ),
      index = 1
   )
   private String alice$protectRenderedText(String var1) {
      return NameProtectionBridge.protect(var1);
   }
}
