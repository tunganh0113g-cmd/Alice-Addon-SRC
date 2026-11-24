package com.nnpg.alice.mixins;

import com.nnpg.alice.bridge.VelocityBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class VelocityEntityMixin {
   @Inject(method = "method_5697", at = @At("HEAD"), cancellable = true)
   private void alice$cancelEntityPush(Entity var1, CallbackInfo var2) {
      if (VelocityBridge.cM() && this == MinecraftClient.getInstance().player) {
         var2.cancel();
      }
   }

   @Redirect(
      method = "method_5692",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_3610;method_15758(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;)Lnet/minecraft/class_243;"
      )
   )
   private Vec3d alice$cancelLiquidVelocity(FluidState var1, BlockView var2, BlockPos var3) {
      return VelocityBridge.cN() && this == MinecraftClient.getInstance().player ? Vec3d.ZERO : var1.getVelocity(var2, var3);
   }
}
