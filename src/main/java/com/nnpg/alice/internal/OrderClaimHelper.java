package com.nnpg.alice.internal;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;

final class OrderClaimHelper {
   static final int sF = 64;
   private static final Pattern CP = Pattern.compile("\\bkhong\\s+co\\s+vat\\s+pham(?:\\s+nao)?\\s+de\\s+nhan\\b");

   private OrderClaimHelper() {
   }

   static boolean Dt(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = Normalizer.normalize(var0.toLowerCase(Locale.ROOT), Form.NFD).replaceAll("\\p{M}+", "").replace('đ', 'd');
         return CP.matcher(var1).find();
      } else {
         return false;
      }
   }

   static OrderClaimHelper.BatchTotals Du(boolean var0) {
      long var1 = var0 ? 45L : 1L;
      return new OrderClaimHelper.BatchTotals(var1, var1 * 64L);
   }

   static boolean Dv(double var0) {
      return var0 <= 4.0;
   }

   static double Dw(long var0, long var2) {
      return var2 <= 0L ? 100.0 : Math.min(100.0, Math.max(0.0, var0 * 100.0 / var2));
   }

   static int Dx(int var0, int var1, Set<Integer> var2, IntPredicate var3) {
      for (int var4 = var0; var4 <= var1; var4++) {
         if (!var2.contains(var4) && var3.test(var4)) {
            return var4;
         }
      }

      return -1;
   }

   static boolean Dy(Map<Integer, Integer> var0, Map<Integer, Integer> var1) {
      for (Entry var3 : var1.entrySet()) {
         Integer var4 = (Integer)var0.get(var3.getKey());
         if (var4 == null || (Integer)var3.getValue() > var4) {
            return true;
         }
      }

      return false;
   }

   record BatchTotals(long stacks, long items) {
   }

   static final class ConfirmationGate {
      private boolean BA;

      void Dm() {
         this.BA = true;
      }

      void Dn() {
         this.BA = false;
      }

      boolean Do(boolean var1, boolean var2) {
         if (this.BA && var1 && var2) {
            this.BA = false;
            return true;
         } else {
            return false;
         }
      }

      boolean Dp() {
         return this.BA;
      }
   }

   static final class TickDelay {
      private int GI;

      void Dq(int var1) {
         this.GI = Math.max(0, var1);
      }

      void Dr() {
         this.GI = 0;
      }

      boolean Ds() {
         if (this.GI > 0) {
            this.GI--;
            return false;
         } else {
            return true;
         }
      }
   }
}
