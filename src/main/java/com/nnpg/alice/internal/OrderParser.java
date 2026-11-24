package com.nnpg.alice.internal;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class OrderParser {
   private static final Pattern jF = Pattern.compile("(\\d+)\\s*/\\s*(\\d+)");
   private static final Pattern GT = Pattern.compile("(?i)\\$\\s*([0-9]+(?:\\.[0-9]+)?)\\s*([KMB]?)");
   private static final Pattern kp = Pattern.compile("(?i)ban da giao\\s+(\\d+)\\b.*?nhan duoc\\s*:?\\s*\\+\\s*\\$\\s*([0-9]+(?:\\.[0-9]+)?)\\s*([KMB]?)");

   private OrderParser() {
   }

   static String gB(String var0) {
      if (var0 == null) {
         return "";
      } else {
         int var1 = var0.indexOf(58);
         String var2 = var1 >= 0 ? var0.substring(var1 + 1) : var0;
         return var2.replace('_', ' ').trim();
      }
   }

   static int gC(List<String> var0) {
      if (var0 == null) {
         return -1;
      } else {
         for (String var2 : var0) {
            Matcher var3 = jF.matcher(var2);
            if (var3.find()) {
               try {
                  int var4 = Integer.parseInt(var3.group(1));
                  int var5 = Integer.parseInt(var3.group(2));
                  return Math.max(0, var5 - var4);
               } catch (NumberFormatException var6) {
                  return -1;
               }
            }
         }

         return -1;
      }
   }

   static double gD(List<String> var0) {
      if (var0 == null) {
         return 0.0;
      } else {
         for (String var2 : var0) {
            Matcher var3 = GT.matcher(var2);
            if (var3.find()) {
               return gG(var3.group(1), var3.group(2));
            }
         }

         return 0.0;
      }
   }

   static int gE(List<OrderParser.Candidate> var0) {
      int var1 = -1;
      double var2 = -1.0;

      for (OrderParser.Candidate var5 : var0) {
         if (var5.remaining() >= 64 && !(var5.pricePerItem() <= var2)) {
            var1 = var5.slot();
            var2 = var5.pricePerItem();
         }
      }

      return var1;
   }

   static Optional<OrderParser.Delivery> gF(String var0) {
      int var10000 = var0 != null ? 23409 : 23410;

      while (true) {
         int var6 = var10000;
         if (var6 == 23409) {
            if (!var0.isBlank()) {
               String var1 = Normalizer.normalize(var0, Form.NFD).replaceAll("\\p{M}+", "").replace('Đ', 'D').replace('đ', 'd').toLowerCase(Locale.ROOT);
               Matcher var2 = kp.matcher(var1);
               if (!var2.find()) {
                  return Optional.empty();
               }

               int var3 = Integer.parseInt(var2.group(1));
               double var4 = gG(var2.group(2), var2.group(3));
               return Optional.of(new OrderParser.Delivery(var3, var4));
            }
            break;
         }

         if (var6 != 23410) {
            break;
         }

         var10000 = 23408;
      }

      return Optional.empty();
   }

   static double gG(String var0, String var1) {
      double var2;
      byte var5;
      var2 = Double.parseDouble(var0);
      String var4 = var1 == null ? "" : var1.toUpperCase(Locale.ROOT);
      var5 = -1;
      label41:
      switch (var4.hashCode() ^ -1604395628) {
         case -1604395562:
            int var10000 = var4.equals("B") ? 30061 : 30062;

            while (true) {
               int var6 = var10000;
               if (var6 == 30061) {
                  var5 = 2;
                  break label41;
               }

               if (var6 != 30062) {
                  break label41;
               }

               var10000 = 30060;
            }
         case -1604395559:
            if (var4.equals("M")) {
               var5 = 1;
            }
            break;
         case -1604395553:
            if (var4.equals("K")) {
               var5 = 0;
            }
      }
      return switch (var5) {
         case 0 -> var2 * 1000.0;
         case 1 -> var2 * 1000000.0;
         case 2 -> var2 * 1.0E9;
         default -> var2;
      };
   }

   record Candidate(int slot, int remaining, double pricePerItem) {
   }

   static final class ConfirmedTransfer {
      private final Map<Integer, Integer> ga;
      private final Map<Integer, Integer> ez = new HashMap<>();
      private final Set<Integer> za = new HashSet<>();

      ConfirmedTransfer(Map<Integer, Integer> var1) {
         this.ga = new HashMap<>(var1);
      }

      int go(int var1, int var2) {
         Integer var3 = this.ga.get(var1);
         if (var3 == null) {
            return 0;
         } else {
            this.za.add(var1);
            int var4 = Math.max(0, Math.min(var3, var3 - Math.max(0, var2)));
            int var5 = this.ez.getOrDefault(var1, 0);
            if (var4 <= var5) {
               return 0;
            } else {
               this.ez.put(var1, var4);
               return var4 - var5;
            }
         }
      }

      int gp(Map<Integer, Integer> var1) {
         int var2 = 0;

         for (int var4 : this.ga.keySet()) {
            var2 += this.go(var4, var1.getOrDefault(var4, 0));
         }

         return var2;
      }

      boolean gq(int var1) {
         return this.ga.containsKey(var1);
      }

      Set<Integer> gr() {
         return Set.copyOf(this.ga.keySet());
      }

      boolean gs() {
         return this.za.containsAll(this.ga.keySet());
      }
   }

   record Delivery(int items, double money) {
   }

   static final class MinuteSchedule {
      private long xB;

      void gv(long var1, int var3) {
         this.xB = var1 + Math.max(1, var3) * 60000L;
      }

      boolean gw(long var1, int var3) {
         if (this.xB <= 0L) {
            this.gv(var1, var3);
         }

         if (var1 < this.xB) {
            return false;
         } else {
            this.gv(var1, var3);
            return true;
         }
      }
   }

   static final class StackCounter {
      private long sn;
      private int af;

      void gx(int var1) {
         if (var1 > 0) {
            long var2 = (long)this.af + var1;
            this.sn += var2 / 64L;
            this.af = (int)(var2 % 64L);
         }
      }

      long gy() {
         return this.sn;
      }

      int gz() {
         return this.af;
      }

      void gA() {
         this.sn = 0L;
         this.af = 0;
      }
   }
}
