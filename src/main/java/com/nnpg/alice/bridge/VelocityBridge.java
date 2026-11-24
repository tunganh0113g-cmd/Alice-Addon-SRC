package com.nnpg.alice.bridge;

import java.util.function.BooleanSupplier;

public final class VelocityBridge {
   private static volatile Object DD;
   private static volatile BooleanSupplier uW = () -> false;
   private static volatile BooleanSupplier bW = () -> false;
   private static volatile BooleanSupplier sX = () -> false;

   private VelocityBridge() {
   }

   public static void install(Object var0, BooleanSupplier var1, BooleanSupplier var2, BooleanSupplier var3) {
      if (var0 != null && var1 != null && var2 != null && var3 != null) {
         DD = var0;
         uW = var1;
         bW = var2;
         sX = var3;
      }
   }

   public static void uninstall(Object var0) {
      if (DD == var0) {
         uW = () -> false;
         bW = () -> false;
         sX = () -> false;
         DD = null;
      }
   }

   public static boolean m() {
      return p(uW);
   }

   public static boolean n() {
      return p(bW);
   }

   public static boolean o() {
      return p(sX);
   }

   private static boolean p(BooleanSupplier var0) {
      return DD != null && var0.getAsBoolean();
   }

   public static boolean cM() {
      return m();
   }

   public static boolean cN() {
      return n();
   }

   public static boolean cO() {
      return o();
   }

   public static boolean cP(BooleanSupplier var0) {
      return p(var0);
   }

   public static boolean cQ() {
      return q();
   }

   public static boolean cR() {
      return r();
   }

   public static boolean cS() {
      return s();
   }

   public static boolean cT() {
      return t();
   }

   public static boolean cU() {
      return u();
   }

   public static boolean cV() {
      return v();
   }
}
