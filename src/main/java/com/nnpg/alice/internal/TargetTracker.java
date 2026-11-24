package com.nnpg.alice.internal;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

final class TargetTracker {
   private long mu;
   private TargetTracker.Target yR;
   private long hL;

   void zf() {
      this.mu++;
   }

   boolean zg(BlockHitResult var1) {
      if (var1 == null) {
         this.zi();
         return false;
      } else {
         return this.zj(new TargetTracker.BlockTarget(var1.getBlockPos().toImmutable(), var1.getSide()));
      }
   }

   boolean zh(int var1) {
      return this.zj(new TargetTracker.EntityTarget(var1));
   }

   void zi() {
      this.yR = null;
   }

   private boolean zj(TargetTracker.Target var1) {
      if (!var1.equals(this.yR)) {
         this.yR = var1;
         this.hL = this.mu;
         return false;
      } else if (this.mu <= this.hL) {
         return false;
      } else {
         this.yR = null;
         return true;
      }
   }

   private record BlockTarget(BlockPos pos, Direction side) implements TargetTracker.Target {
   }

   private record EntityTarget(int entityId) implements TargetTracker.Target {
   }

   private sealed interface Target permits TargetTracker.BlockTarget, TargetTracker.EntityTarget {
   }
}
