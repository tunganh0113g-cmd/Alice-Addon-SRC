package com.nnpg.alice.bridge;

import java.util.function.BooleanSupplier;

public final class VelocityPlusPlusBridge {
   private static volatile Object og;
   private static volatile BooleanSupplier qN = () -> false;
   private static volatile BooleanSupplier as = () -> false;
   private static volatile BooleanSupplier fY = () -> false;
   private static volatile BooleanSupplier Hi = () -> false;
   private static volatile BooleanSupplier Jd = () -> false;
   private static volatile BooleanSupplier uk = () -> false;
   private static volatile BooleanSupplier qP = () -> false;

   private VelocityPlusPlusBridge() {
   }

   public static void install(
      Object var0,
      BooleanSupplier var1,
      BooleanSupplier var2,
      BooleanSupplier var3,
      BooleanSupplier var4,
      BooleanSupplier var5,
      BooleanSupplier var6,
      BooleanSupplier var7
   ) {
      if (var0 != null && var1 != null && var2 != null && var3 != null && var4 != null && var5 != null && var6 != null && var7 != null) {
         og = var0;
         qN = var1;
         as = var2;
         fY = var3;
         Hi = var4;
         Jd = var5;
         uk = var6;
         qP = var7;
      }
   }

   public static void uninstall(Object var0) {
      if (og == var0) {
         qN = () -> false;
         as = () -> false;
         fY = () -> false;
         Hi = () -> false;
         Jd = () -> false;
         uk = () -> false;
         qP = () -> false;
         og = null;
      }
   }

   public static boolean w() {
      return D(qN);
   }

   public static boolean x() {
      return D(as);
   }

   public static boolean y() {
      return D(fY);
   }

   public static boolean z() {
      return D(Hi);
   }

   public static boolean A() {
      return D(Jd);
   }

   public static boolean B() {
      return D(uk);
   }

   public static boolean C() {
      return D(qP);
   }

   private static boolean D(BooleanSupplier var0) {
      return og != null && var0.getAsBoolean();
   }
}
