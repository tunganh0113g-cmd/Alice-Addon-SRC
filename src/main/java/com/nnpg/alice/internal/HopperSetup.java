package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.EnumSetting.Builder;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class HopperSetup extends AliceModule {
   private static final int AC = 5;
   private static final int gC = 36;
   private final SettingGroup lB = this.settings.getDefaultGroup();
   private final Setting<HopperSetup.TriggerMode> ly = this.lB
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("trigger-mode")).description("chon tu dong setup khi mo hoac dung phim tat de setup thu cong"))
               .defaultValue(HopperSetup.TriggerMode.Auto))
            .build()
      );
   private final Setting<Keybind> xu = this.lB
      .add(
         ((meteordevelopment.meteorclient.settings.KeybindSetting.Builder)((meteordevelopment.meteorclient.settings.KeybindSetting.Builder)((meteordevelopment.meteorclient.settings.KeybindSetting.Builder)new meteordevelopment.meteorclient.settings.KeybindSetting.Builder()
                     .name("setup-key"))
                  .description("ap dung layout hopper da cau hinh khi mo GUI"))
               .visible(() -> this.ly.get() == HopperSetup.TriggerMode.Keybind))
            .build()
      );
   private final Setting<Item> Jr = this.AQ(1);
   private final Setting<Integer> uF = this.AR(1);
   private final Setting<Item> xU = this.AQ(2);
   private final Setting<Integer> zF = this.AR(2);
   private final Setting<Item> eY = this.AQ(3);
   private final Setting<Integer> ij = this.AR(3);
   private final Setting<Item> dl = this.AQ(4);
   private final Setting<Integer> rJ = this.AR(4);
   private final Setting<Item> oL = this.AQ(5);
   private final Setting<Integer> yi = this.AR(5);
   private boolean jo;
   private boolean qZ;

   public HopperSetup() {
      super(AliceAddon.CATEGORY, "HopperSetup", "Thiet lap bo cuc pheu nam o da luu bang vat pham trong tui do.");
   }

   @Override
   public void onActivate() {
      super.onActivate();
      this.jo = false;
      this.qZ = false;
   }

   @Override
   public void onDeactivate() {
      super.onDeactivate();
      this.jo = false;
      this.qZ = false;
   }

   @AliceEventHandler
   public void AK(OpenScreenEvent var1) {
      if (this.isActive() && this.ly.get() == HopperSetup.TriggerMode.Auto) {
         if (var1.screen instanceof HopperScreen) {
            this.jo = true;
         }
      }
   }

   @AliceEventHandler
   public void AL(Pre var1) {
      if (this.isActive() && this.mc.player != null && this.mc.interactionManager != null) {
         if (!(this.mc.player.currentScreenHandler instanceof HopperScreenHandler var2)) {
            this.jo = false;
            this.qZ = false;
         } else {
            int var10000 = this.ly.get() != HopperSetup.TriggerMode.Auto ? '\uec98' : '\uec97';

            while (true) {
               int var4 = var10000;
               if (var4 == 60567) {
                  if (this.jo) {
                     this.jo = false;
                     this.AM(var2);
                     return;
                  }

                  return;
               }

               if (var4 != 60568) {
                  boolean var5 = ((Keybind)this.xu.get()).isPressed();
                  if (var5 && !this.qZ) {
                     this.AM(var2);
                  }

                  this.qZ = var5;
                  return;
               }

               var10000 = 60566;
            }
         }
      }
   }

   private void AM(HopperScreenHandler var1) {
      if (!var1.getCursorStack().isEmpty()) {
         this.info("Clear the cursor before applying HopperSetup.", new Object[0]);
      } else {
         Item[] var2 = new Item[]{(Item)this.Jr.get(), (Item)this.xU.get(), (Item)this.eY.get(), (Item)this.dl.get(), (Item)this.oL.get()};
         int[] var3 = new int[]{(Integer)this.uF.get(), (Integer)this.zF.get(), (Integer)this.ij.get(), (Integer)this.rJ.get(), (Integer)this.yi.get()};

         for (int var4 = 0; var4 < 5; var4++) {
            Item var5 = var2[var4];
            if (var5 != null && var5 != Items.AIR) {
               this.AN(var1, var4, var5, var3[var4]);
            }
         }
      }
   }

   private void AN(HopperScreenHandler var1, int var2, Item var3, int var4) {
      ItemStack var5 = var1.getSlot(var2).getStack();
      int var6 = CombatMathHelper.DB(var5.isEmpty(), var5.isOf(var3), var5.getCount(), var4, var3.getMaxCount());
      if (var6 > 0) {
         for (int var7 = 0; var7 < 36 && var6 > 0; var7++) {
            ItemStack var8 = this.mc.player.getInventory().getStack(var7);
            if (var8.isOf(var3)) {
               int var9 = this.AO(var1, var7);
               if (var9 >= 0) {
                  int var10 = var8.getCount();
                  int var11 = Math.min(var10, var6);
                  this.AP(var1, var9, 0);
                  if (var11 == var10) {
                     this.AP(var1, var2, 0);
                     if (!var1.getCursorStack().isEmpty()) {
                        this.AP(var1, var9, 0);
                     }
                  } else {
                     for (int var12 = 0; var12 < var11; var12++) {
                        this.AP(var1, var2, 1);
                     }

                     this.AP(var1, var9, 0);
                  }

                  var6 -= var11;
               }
            }
         }
      }
   }

   private int AO(HopperScreenHandler var1, int var2) {
      label30:
      for (Slot var4 : var1.slots) {
         if (var4.inventory == this.mc.player.getInventory()) {
            int var10000 = var4.getIndex() != var2 ? '諩' : '諨';

            while (true) {
               switch (var10000) {
                  case 35560:
                     return var4.id;
                  case 35561:
                     var10000 = 35559;
                     break;
                  default:
                     continue label30;
               }
            }
         }
      }

      return -1;
   }

   private void AP(HopperScreenHandler var1, int var2, int var3) {
      this.mc.interactionManager.clickSlot(var1.syncId, var2, var3, SlotActionType.PICKUP, this.mc.player);
   }

   private Setting<Item> AQ(int var1) {
      return this.lB
         .add(
            ((meteordevelopment.meteorclient.settings.ItemSetting.Builder)((meteordevelopment.meteorclient.settings.ItemSetting.Builder)((meteordevelopment.meteorclient.settings.ItemSetting.Builder)new meteordevelopment.meteorclient.settings.ItemSetting.Builder()
                        .name(var1 + ""))
                     .description(var1 + ""))
                  .defaultValue(Items.AIR))
               .build()
         );
   }

   private Setting<Integer> AR(int var1) {
      return this.lB
         .add(
            ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name(var1 + ""))
                     .description(var1 + ""))
                  .defaultValue(1))
               .min(1)
               .max(64)
               .sliderMax(64)
               .build()
         );
   }

   public static enum TriggerMode {
      Auto,
      Keybind;
   }
}
