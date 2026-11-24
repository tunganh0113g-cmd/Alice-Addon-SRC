package com.nnpg.alice.bridge;

import java.util.function.BooleanSupplier;

public final class NoPushBridge {
   private static volatile Object cc;
   private static volatile BooleanSupplier au = () -> false;

   private NoPushBridge() {
   }

   public static void install(Object var0, BooleanSupplier var1) {
      int var10000 = var0 != null ? '뒞' : '뒟';

      while (true) {
         int var2 = var10000;
         if (var2 == 46238) {
            if (var1 != null) {
               cc = var0;
               au = var1;
               return;
            }
            break;
         }

         if (var2 != 46239) {
            break;
         }

         var10000 = 46237;
      }
   }

   public static void uninstall(Object var0) {
      if (cc == var0) {
         au = () -> false;
         cc = null;
      }
   }

   public static boolean isEnabled() {
      return cc != null && au.getAsBoolean();
   }
}
