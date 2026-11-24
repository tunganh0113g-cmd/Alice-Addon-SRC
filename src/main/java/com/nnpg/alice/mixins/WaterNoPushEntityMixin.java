package com.nnpg.alice.mixins;

import com.nnpg.alice.bridge.NoPushBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class WaterNoPushEntityMixin {
   @Redirect(method = "method_5692", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1297;method_18799(Lnet/minecraft/class_243;)V"))
   private void alice$ignoreFluidPush(Entity var1, Vec3d var2, TagKey<Fluid> var3, double var4) {
      if (!NoPushBridge.isEnabled() || var1 != MinecraftClient.getInstance().player || var3 != FluidTags.WATER) {
         var1.setVelocity(var2);
      }
   }
}
