package com.nnpg.alice;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.listeners.IListener;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;

public final class PremiumModulePersistence {
   private static final int Hh = 20;
   private static PremiumModulePersistence.PremiumAutosave ya;

   private PremiumModulePersistence() {
   }

   public static void d(Modules var0, Module... var1) {
      File var2 = var0.getFile();
      if (var2 != null && var2.isFile()) {
         NbtCompound var3 = NbtIo.read(var2.toPath());
         if (var3 != null) {
            e(var3, var1);
         }
      }

      var0.save();
      installAutosave(var0, var1);
   }

   static int e(NbtCompound var0, Module... var1) {
      HashMap var2 = new HashMap();

      for (Module var6 : var1) {
         var2.put(var6.name.toLowerCase(Locale.ROOT), var6);
      }

      int var7 = 0;

      for (Module var9 : var2.values()) {
         NbtCompound var10 = f(var0, var9.name);
         if (var10 != null) {
            var9.fromTag(var10);
            var7++;
         }
      }

      return var7;
   }

   static NbtCompound f(NbtCompound var0, String var1) {
      for (NbtElement var3 : var0.getListOrEmpty("modules")) {
         if (var3 instanceof NbtCompound var4) {
            String var5 = var4.getString("name", "");
            if (var5.equalsIgnoreCase(var1)) {
               return var4;
            }
         }
      }

      return null;
   }

   private static void installAutosave(Modules var0, Module... var1) {
      if (ya != null) {
         MeteorClient.EVENT_BUS.unsubscribe(ya);
      }

      ya = new PremiumModulePersistence.PremiumAutosave(var0, var1);
      MeteorClient.EVENT_BUS.subscribe(ya);
   }

   private static String g(Module... var0) {
      StringBuilder var1 = new StringBuilder(var0.length * 256);

      for (Module var5 : var0) {
         String var6 = var5.toTag().toString();
         var1.append(var6.length()).append(':').append(var6);
      }

      return var1.toString();
   }

   static final class AutosaveState {
      private String FN;
      private int IB;

      AutosaveState(String var1) {
         this.FN = var1;
      }

      boolean a(String var1) {
         if (!this.b()) {
            return false;
         } else if (this.FN.equals(var1)) {
            return false;
         } else {
            this.FN = var1;
            return true;
         }
      }

      boolean b() {
         if (++this.IB >= 20) {
            this.IB = 0;
            return true;
         } else {
            return false;
         }
      }

      boolean c(String var1) {
         if (this.FN.equals(var1)) {
            return false;
         } else {
            this.FN = var1;
            return true;
         }
      }
   }

   private static final class PremiumAutosave implements IListener {
      private final Modules rA;
      private final Module[] Ab;
      private final PremiumModulePersistence.AutosaveState bz;

      private PremiumAutosave(Modules var1, Module[] var2) {
         this.rA = var1;
         this.Ab = (Module[])var2.clone();
         this.bz = new PremiumModulePersistence.AutosaveState(PremiumModulePersistence.g(this.Ab));
      }

      public void call(Object var1) {
         if (this.bz.b()) {
            if (this.bz.c(PremiumModulePersistence.g(this.Ab))) {
               this.rA.save();
            }
         }
      }

      public Class<?> getTarget() {
         return Post.class;
      }

      public int getPriority() {
         return 0;
      }

      public boolean isStatic() {
         return false;
      }
   }
}
