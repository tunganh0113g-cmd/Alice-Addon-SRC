package com.nnpg.alice.internal;

import com.nnpg.alice.PremiumModulePersistence;
import meteordevelopment.meteorclient.systems.modules.Modules;

public final class AliceModuleBootstrap {
   private static String FB = "begin";

   private AliceModuleBootstrap() {
   }

   public static void NH() {
      NI();
   }

   private static void NI() {
      FB = "runtime verification";
      RuntimeVerification.NM();
      FB = "module SpawnerOrder";
      SpawnerOrder var0 = new SpawnerOrder();
      FB = "module AutoOrder";
      AutoOrder var1 = new AutoOrder();
      FB = "module AliceAutoReconnect";
      AliceAutoReconnect var2 = new AliceAutoReconnect();
      FB = "module SpawnerProtect";
      SpawnerProtect var3 = new SpawnerProtect();
      FB = "module EasyPlace";
      EasyPlace var4 = new EasyPlace();
      FB = "module AutoTotem";
      AutoTotem var5 = new AutoTotem();
      FB = "module AutoSell";
      AutoSell var6 = new AutoSell();
      FB = "module BlazeShopDroper";
      BlazeShopDroper var7 = new BlazeShopDroper();
      FB = "module SpawnerDropper";
      SpawnerDropper var8 = new SpawnerDropper();
      FB = "module OrderDropper";
      OrderDropper var9 = new OrderDropper();
      FB = "module HopperSetup";
      HopperSetup var10 = new HopperSetup();
      FB = "module CrafterSetup";
      CrafterSetup var11 = new CrafterSetup();
      FB = "module AnchorMacro";
      AnchorMacro var12 = new AnchorMacro();
      FB = "module CrystalAuraPlusPlus";
      CrystalAuraPlusPlus var13 = new CrystalAuraPlusPlus();
      FB = "module CrystalLegit";
      CrystalLegit var14 = new CrystalLegit();
      FB = "module ShieldDisable";
      ShieldDisable var15 = new ShieldDisable();
      FB = "module TriggerBot";
      TriggerBot var16 = new TriggerBot();
      FB = "module KeyPearl";
      KeyPearl var17 = new KeyPearl();
      FB = "module FreezePlayer";
      FreezePlayer var18 = new FreezePlayer();
      FB = "module VelocityPlusPlus";
      VelocityPlusPlus var19 = new VelocityPlusPlus();
      FB = "module KillAuraPlusPlus";
      KillAuraPlusPlus var20 = new KillAuraPlusPlus();
      FB = "module SpeedMinePlusPlus";
      SpeedMinePlusPlus var21 = new SpeedMinePlusPlus();
      FB = "module AutoMinePlusPlus";
      AutoMinePlusPlus var22 = new AutoMinePlusPlus();
      FB = "module AntiPhasePlusPlus";
      AntiPhasePlusPlus var23 = new AntiPhasePlusPlus();
      FB = "module AutoBuyTotem";
      AutoBuyTotem var24 = new AutoBuyTotem();
      FB = "module AutoExpPlusPlus";
      AutoExpPlusPlus var25 = new AutoExpPlusPlus();
      Modules var26 = Modules.get();
      FB = "GrimCombatCoordinator.install";
      GrimCombatCoordinator.install();
      FB = "module registration";
      var26.add(var0);
      var26.add(var1);
      var26.add(var2);
      var26.add(var3);
      var26.add(var4);
      var26.add(var5);
      var26.add(var6);
      var26.add(var7);
      var26.add(var8);
      var26.add(var9);
      var26.add(var10);
      var26.add(var11);
      var26.add(var12);
      var26.add(var13);
      var26.add(var14);
      var26.add(var15);
      var26.add(var16);
      var26.add(var17);
      var26.add(var18);
      var26.add(var19);
      var26.add(var20);
      var26.add(var21);
      var26.add(var22);
      var26.add(var23);
      var26.add(var24);
      var26.add(var25);
      FB = "PremiumModulePersistence.restore";
      PremiumModulePersistence.d(
         var26,
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var12,
         var13,
         var14,
         var15,
         var16,
         var17,
         var18,
         var19,
         var20,
         var21,
         var22,
         var23,
         var24,
         var25
      );
      FB = "complete";
   }

   private static RuntimeException NJ(String var0, Throwable var1) {
      if (var1 instanceof Error var3) {
         throw var3;
      } else {
         return var1 instanceof RuntimeException var2 ? var2 : new RuntimeException(var1);
      }
   }
}
