package com.nnpg.alice;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AliceAddon extends MeteorAddon {
   public static final Category CATEGORY = new Category("ALICE", new ItemStack(Items.CAKE));
   public static final Category ESP_CATEGORY = new Category("ALICE ESP", new ItemStack(Items.VINE));
   public static final Category PVP_CATEGORY = new Category("ALICE PVP", new ItemStack(Items.DIAMOND_SWORD));

   public void onInitialize() {
      System.out.println("[Alice-Patched] Initializing patched premium addon without key checks (Java 21 Target)...");

      try {
         AlicePayloadEntrypoint.init();
      } catch (Throwable var2) {
         System.err.println("[Alice-Patched] Failed to initialize premium payload:");
         var2.printStackTrace();
      }
   }

   public void onRegisterCategories() {
      Modules.registerCategory(CATEGORY);
      Modules.registerCategory(ESP_CATEGORY);
      Modules.registerCategory(PVP_CATEGORY);
   }

   public String getPackage() {
      return "com.nnpg.alice";
   }
}
