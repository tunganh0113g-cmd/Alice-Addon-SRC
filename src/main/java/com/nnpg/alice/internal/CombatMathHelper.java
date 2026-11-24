package com.nnpg.alice.internal;

final class CombatMathHelper {
   private CombatMathHelper() {
   }

   static int DB(boolean var0, boolean var1, int var2, int var3, int var4) {
      if (!var0 && !var1) {
         return 0;
      } else {
         int var5 = Math.max(1, Math.min(var3, var4));
         int var6 = !var0 ? Math.max(0, var2) : 0;
         return Math.max(0, var5 - var6);
      }
   }

   static boolean DC(boolean var0) {
      return !var0;
   }

   static int DD(boolean[] var0) {
      int var1 = 0;
      int var2 = Math.min(var0.length, 9);
      int var3 = 0;

      while (true) {
         int var10000 = var3 >= var2 ? '\uffdf' : '\uffde';

         while (true) {
            int var4 = var10000;
            if (var4 == 65502) {
               if (var0[var3]) {
                  var1 |= 1 << var3;
               }

               var3++;
               break;
            }

            if (var4 != 65503) {
               return var1;
            }

            var10000 = 65501;
         }
      }
   }

   static int DE(int var0, int var1) {
      return Math.min(Math.max(0, var0), Math.max(1, var1));
   }

   static int DF(int var0, int var1) {
      return Math.max(0, var0 - var1);
   }

   static float DG(float var0, float var1, float var2) {
      float var3 = DJ(var1 - var0);
      return var0 + DK(var3, -Math.abs(var2), Math.abs(var2));
   }

   static float DH(float var0, float var1, float var2) {
      float var3 = var1 - var0;
      return var0 + DK(var3, -Math.abs(var2), Math.abs(var2));
   }

   static float DI(float var0, float var1, float var2, float var3, float var4) {
      double var5 = Math.toDegrees(Math.atan2(var2, var1));
      double var7 = Math.toDegrees(Math.atan2(var4, var3));
      return var0 + DJ((float)(var5 - var7));
   }

   private static float DJ(float var0) {
      float var1 = var0 % 360.0F;
      if (var1 >= 180.0F) {
         var1 -= 360.0F;
      }

      if (var1 < -180.0F) {
         var1 += 360.0F;
      }

      return var1;
   }

   private static float DK(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }
}
