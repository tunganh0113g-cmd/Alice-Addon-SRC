package com.nnpg.alice.bridge;

import java.util.function.UnaryOperator;

public final class NameProtectionBridge {
   private static volatile Object mn;
   private static volatile UnaryOperator<String> AK = UnaryOperator.identity();
   private static volatile boolean BT;

   private NameProtectionBridge() {
   }

   public static void install(Object var0, UnaryOperator<String> var1) {
      if (var0 != null && var1 != null) {
         mn = var0;
         AK = var1;
         BT = true;
      }
   }

   public static void uninstall(Object var0) {
      if (mn == var0) {
         BT = false;
         AK = UnaryOperator.identity();
         mn = null;
      }
   }

   public static boolean j() {
      return BT;
   }

   public static String protect(String var0) {
      return BT && var0 != null && !var0.isEmpty() ? AK.apply(var0) : var0;
   }

   public static boolean cJ() {
      return j();
   }
}
