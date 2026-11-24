package com.nnpg.alice.internal;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;

final class OrderBuyHelper {
   static final long cK = 150L;
   private static final double sM = 1000000.0;
   private static final String Aa = "de mua vat pham nay";

   private OrderBuyHelper() {
   }

   static boolean kO(String var0) {
      if (var0 != null && !var0.isBlank()) {
         short var2 = 26814;
         String var1 = Normalizer.normalize(var0.toLowerCase(Locale.ROOT), Form.NFD).replaceAll("\\p{M}+", "").replace('đ', 'd');
         return var1.contains("de mua vat pham nay");
      } else {
         return false;
      }
   }

   static long kP(double var0) {
      if (Double.isFinite(var0) && !(var0 <= 0.0)) {
         double var2 = var0 * 1000000.0;
         return var2 >= 9.223372E18F ? Long.MAX_VALUE : Math.round(var2);
      } else {
         return 0L;
      }
   }

   static long kQ(long var0) {
      if (var0 <= 0L) {
         return 0L;
      } else {
         return var0 > 61489146912365172L ? Long.MAX_VALUE : var0 * 150L;
      }
   }

   static double kR(long var0, long var2) {
      return var2 <= 0L ? 100.0 : Math.min(100.0, Math.max(0.0, var0 * 100.0 / var2));
   }

   static OrderBuyHelper.BatchTotals kS(int... var0) {
      int var1 = 0;
      long var2 = 0L;
      if (var0 != null) {
         for (int var7 : var0) {
            if (var7 > 0) {
               var1++;
               var2 += var7;
            }
         }
      }

      return new OrderBuyHelper.BatchTotals(var1, var2, kQ(var2));
   }

   record BatchTotals(int stacks, long items, long money) {
   }

   static final class TickDelay {
      private int BP;

      void kK(int var1) {
         this.BP = Math.max(0, var1);
      }

      void kL() {
         this.BP = 0;
      }

      boolean kM() {
         if (this.BP > 0) {
            this.BP--;
            return false;
         } else {
            return true;
         }
      }

      int kN() {
         return this.BP;
      }
   }
}
