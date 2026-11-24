package com.nnpg.alice.mixins;

import com.nnpg.alice.bridge.VelocityBridge;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
   @Inject(method = "method_30673", at = @At("HEAD"), cancellable = true)
   private void alice$cancelPushOutOfBlocks(double var1, double var3, CallbackInfo var5) {
      if (VelocityBridge.cO()) {
         var5.cancel();
      }
   }
}
