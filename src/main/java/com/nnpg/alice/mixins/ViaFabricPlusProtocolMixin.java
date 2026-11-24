package com.nnpg.alice.mixins;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com/viaversion/viafabricplus/protocoltranslator/protocol/ViaFabricPlusProtocol", remap = false)
public abstract class ViaFabricPlusProtocolMixin {
   private static final String PROTOCOL_TRANSLATOR = "com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator";

   @Inject(method = "lambda$registerPackets$0", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
   private static void alice$skipNativeCustomPayloadRewrite(CallbackInfo var0) {
      if (alice$isNativeTarget()) {
         var0.cancel();
      }
   }

   private static boolean alice$isNativeTarget() {
      try {
         ClassLoader var0 = ViaFabricPlusProtocolMixin.class.getClassLoader();
         Class var1 = Class.forName("com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator", false, var0);
         Method var2 = var1.getMethod("getTargetVersion");
         Field var3 = var1.getField("NATIVE_VERSION");
         Object var4 = var2.invoke(null);
         Object var5 = var3.get(null);
         return var4 != null && Objects.equals(var4, var5);
      } catch (LinkageError | ReflectiveOperationException var6) {
         return false;
      }
   }
}
