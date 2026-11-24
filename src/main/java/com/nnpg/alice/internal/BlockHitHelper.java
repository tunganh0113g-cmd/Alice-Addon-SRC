package com.nnpg.alice.internal;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

final class BlockHitHelper {
   private static final double tZ = 0.001;
   private static final double nH = 0.05;

   private BlockHitHelper() {
   }

   static Direction DW(Vec3d var0, BlockPos var1) {
      return DX(var0, var1).direction();
   }

   static BlockHitHelper.BlockHitTarget DX(Vec3d var0, BlockPos var1) {
      if (var0 != null && var1 != null) {
         double var2 = var1.getX();
         double var4 = var1.getY();
         double var6 = var1.getZ();
         return DY(var0.x, var0.y, var0.z, var2, var4, var6, var2 + 1.0, var4 + 1.0, var6 + 1.0);
      } else {
         return new BlockHitHelper.BlockHitTarget(Direction.UP, Vec3d.ZERO, Double.POSITIVE_INFINITY);
      }
   }

   private static BlockHitHelper.BlockHitTarget DY(
      double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16
   ) {
      BlockHitHelper.BlockHitTarget var18 = null;

      for (Direction var22 : Direction.values()) {
         BlockHitHelper.BlockHitTarget var23 = DZ(var0, var2, var4, var6, var8, var10, var12, var14, var16, var22);
         if (var18 == null || var23.distanceSq() < var18.distanceSq()) {
            var18 = var23;
         }
      }

      return var18 == null ? new BlockHitHelper.BlockHitTarget(Direction.UP, Vec3d.ZERO, Double.POSITIVE_INFINITY) : var18;
   }

   private static BlockHitHelper.BlockHitTarget DZ(
      double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, Direction var18
   ) {
      Direction var19 = var18 == null ? Direction.UP : var18;
      double var20;
      double var22;
      double var24;
      switch (var19) {
         case DOWN:
            var20 = Ea(var0, var6 + 0.05, var12 - 0.05);
            var22 = var8 + 0.001;
            var24 = Ea(var4, var10 + 0.05, var16 - 0.05);
            break;
         case UP:
            var20 = Ea(var0, var6 + 0.05, var12 - 0.05);
            var22 = var14 - 0.001;
            var24 = Ea(var4, var10 + 0.05, var16 - 0.05);
            break;
         case NORTH:
            var20 = Ea(var0, var6 + 0.05, var12 - 0.05);
            var22 = Ea(var2, var8 + 0.05, var14 - 0.05);
            var24 = var10 + 0.001;
            break;
         case SOUTH:
            var20 = Ea(var0, var6 + 0.05, var12 - 0.05);
            var22 = Ea(var2, var8 + 0.05, var14 - 0.05);
            var24 = var16 - 0.001;
            break;
         case WEST:
            var20 = var6 + 0.001;
            var22 = Ea(var2, var8 + 0.05, var14 - 0.05);
            var24 = Ea(var4, var10 + 0.05, var16 - 0.05);
            break;
         case EAST:
            var20 = var12 - 0.001;
            var22 = Ea(var2, var8 + 0.05, var14 - 0.05);
            var24 = Ea(var4, var10 + 0.05, var16 - 0.05);
            break;
         default:
            throw new IllegalStateException(var19 + "");
      }

      double var26 = var0 - var20;
      double var28 = var2 - var22;
      double var30 = var4 - var24;
      return new BlockHitHelper.BlockHitTarget(var19, new Vec3d(var20, var22, var24), var26 * var26 + var28 * var28 + var30 * var30);
   }

   private static double Ea(double var0, double var2, double var4) {
      return Math.max(var2, Math.min(var0, var4));
   }

   record BlockHitTarget(Direction direction, Vec3d hitPos, double distanceSq) {
   }
}
