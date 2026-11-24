package com.nnpg.alice.internal;

final class AutoSellFilterHelper {
   private AutoSellFilterHelper() {
   }

   static boolean iC(AutoSell.FilterMode var0, boolean var1) {
      return var0 != AutoSell.FilterMode.Whitelist ? !var1 : var1;
   }
}
