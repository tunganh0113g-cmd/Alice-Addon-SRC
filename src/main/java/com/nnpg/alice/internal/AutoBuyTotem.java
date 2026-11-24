package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.EnumSetting.Builder;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class AutoBuyTotem extends AliceModule {
   private static final int iZ = 36;
   private static final int ot = 3;
   private static final int aX = 2;
   private static final int nR = 23;
   private static final int jH = 20;
   private static final String ho = "shop";
   private final SettingGroup wC = this.settings.getDefaultGroup();
   private final Setting<AutoBuyTotem.BuyMode> jG = this.wC
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("buy-mode"))
                  .description("MinToBuy:\udaa6tu mu\uda80 khi\uda49totem xuong duoi nguong. BindToBuy: chi mua khi nhan phim."))
               .defaultValue(AutoBuyTotem.BuyMode.MinToBuy))
            .build()
      );
   private final Setting<Integer> dU = this.wC
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("min-totems"))
                     .description("Con it hon bao nhieu totem thi bat dau mua."))
                  .defaultValue(8))
               .min(1)
               .max(576)
               .sliderMax(64)
               .visible(() -> this.jG.get() == AutoBuyTotem.BuyMode.MinToBuy))
            .build()
      );
   private final Setting<Integer> Gl = this.wC
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("target-totems"))
                     .description("Mua cho den \udae1h\udb51 co du bao nhieu totem."))
                  .defaultValue(32))
               .min(1)
               .max(576)
               .sliderMax(64)
               .visible(() -> this.jG.get() == AutoBuyTotem.BuyMode.MinToBuy))
            .build()
      );
   private final Setting<Keybind> DH = this.wC
      .add(
         ((meteordevelopment.meteorclient.settings.KeybindSetting.Builder)((meteordevelopment.meteorclient.settings.KeybindSetting.Builder)((meteordevelopment.meteorclient.settings.KeybindSetting.Builder)((meteordevelopment.meteorclient.settings.KeybindSetting.Builder)new meteordevelopment.meteorclient.settings.KeybindSetting.Builder()
                        .name("buy-key"))
                     .description("Phim mua totem o che do BindToBuy (bam duoc ca khi dang \udec4o GUI)."))
                  .defaultValue(Keybind.none()))
               .action(() -> this.kH = true)
               .visible(() -> this.jG.get() == AutoBuyTotem.BuyMode.BindToBuy))
            .build()
      );
   private final Setting<Integer> ia = this.wC
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("bind\udca3amount"))
                     .description("So totem m\udf15a moi lan nhan \udbc1him."))
                  .defaultValue(16))
               .min(1)
               .max(576)
               .sliderMax(64)
               .visible(() -> this.jG.get() == AutoBuyTotem.BuyMode.BindToBuy))
            .build()
      );
   private final Setting<Boolean> BO = this.wC
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("n\udfe4tify"))
                  .description("Bao t\ude3aong chat khi bat dau va\ud822khi mua xong."))
               .defaultValue(true))
            .build()
      );
   private AutoBuyTotem.State sr = AutoBuyTotem.State.Idle;
   private int eS;
   private int hd;
   private int vj;
   private int wS;
   private int hr;
   private int jT;
   private int vc;
   private boolean kH;
   private boolean wb;
   private volatile boolean oU;
   private volatile boolean ne;
   private volatile int iN = -1;
   private volatile int Fk;

   public AutoBuyTotem() {
      super(
         AliceAddon.PVP_CATEGORY, "autobuytotem", "Tu don\udde1 mua totem tu shop server, click ngay khi server phan hoi, \udadchong dung delay co dinh\udee0"
      );
   }

   @Override
   public void onActivate() {
      super.onActivate();
      this.dd();
      this.wb = false;
      this.kH = false;
   }

   @Override
   public void onDeactivate() {
      this.dd();
      super.onDeactivate();
   }

   @AliceEventHandler
   private void cR(Receive var1) {
      if (this.oU) {
         if (var1.packet instanceof OpenScreenS2CPacket) {
            this.oU = false;
         } else if (var1.packet instanceof InventoryS2CPacket var4) {
            if (this.dg(var4.syncId(), var4.revision())) {
               this.oU = false;
            }
         } else {
            if (var1.packet instanceof ScreenHandlerSlotUpdateS2CPacket var2 && this.dg(var2.getSyncId(), var2.getRevision())) {
               this.oU = false;
            }
         }
      }
   }

   @AliceEventHandler
   private void cS(Pre var1) {
      this.eS++;
      if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
         if (this.sr == AutoBuyTotem.State.Idle) {
            if (!this.cV()) {
               return;
            }

            this.cW();
            if (this.sr == AutoBuyTotem.State.Idle) {
               return;
            }
         }

         if (this.oU) {
            if (this.eS - this.hd < 20) {
               return;
            }

            this.oU = false;
         }

         switch (this.sr) {
            case OpenShop:
               this.cX();
               break;
            case Navigate:
               this.cY();
               break;
            case Buying:
               this.cZ();
         }
      }
   }

   @AliceEventHandler
   private void cT(ReceiveMessageEvent var1) {
      if (this.isActive() && !this.wb) {
         if (OrderBuyHelper.kO(var1.getMessage().getString())) {
            this.wb = true;
            if (this.sr != AutoBuyTotem.State.Idle) {
               this.db("Khong du tien.");
            } else if ((Boolean)this.BO.get()) {
               this.info("Khong du tien de\ud987mua totem.", new Object[0]);
            }
         }
      }
   }

   @AliceEventHandler
   private void cU(GameLeftEvent var1) {
      this.dd();
   }

   public String getInfoString() {
      return this.sr != AutoBuyTotem.State.Idle && this.mc.player != null ? "" + this.dk() + this.wS : null;
   }

   private boolean cV() {
      if (this.jG.get() == AutoBuyTotem.BuyMode.BindToBuy) {
         if (!this.kH) {
            return false;
         } else {
            this.kH = false;
            this.wb = false;
            return true;
         }
      } else {
         this.kH = false;
         if (this.wb) {
            return false;
         } else {
            short var1 = 11355;
            return this.dk() < (Integer)this.dU.get();
         }
      }
   }

   private void cW() {
      this.vj = this.dk();
      this.wS = this.jG.get() == AutoBuyTotem.BuyMode.BindToBuy ? this.vj + (Integer)this.ia.get() : Math.max((Integer)this.Gl.get(), (Integer)this.dU.get());
      if (this.wS > this.vj) {
         this.hr = 0;
         this.jT = this.vj;
         this.vc = 0;
         this.oU = false;
         this.sr = AutoBuyTotem.State.OpenShop;
         if ((Boolean)this.BO.get()) {
            this.info("" + this.vj + this.wS, new Object[0]);
         }
      } else {
         this.sr = AutoBuyTotem.State.Idle;
      }
   }

   private void cX() {
      if (this.di() != null) {
         this.sr = AutoBuyTotem.State.Navigate;
         this.cY();
      } else {
         this.de();
         this.mc.player.networkHandler.sendChatCommand("shop");
      }
   }

   private void cY() {
      GenericContainerScreenHandler var1 = this.di();
      if (var1 == null) {
         this.sr = AutoBuyTotem.State.OpenShop;
         this.cX();
      } else {
         int var2 = this.dj(var1, Items.TOTEM_OF_UNDYING);
         if (var2 >= 0) {
            this.df(var1);
            this.dh(var1, var2);
            this.hr++;
            if (this.hr >= 2) {
               this.sr = AutoBuyTotem.State.Buying;
            }
         } else {
            this.dc("\udb68hong thay totem trong GUI shop.");
         }
      }
   }

   private void cZ() {
      if (this.dk() < this.wS) {
         if (this.dl()) {
            this.db("Tui do da day.");
         } else {
            GenericContainerScreenHandler var1 = this.di();
            if (var1 == null) {
               this.da();
            } else if (var1.slots.size() > 23) {
               this.df(var1);
               this.dh(var1, 23);
            } else {
               this.dc("GUI mua totem khong co slot 23.");
            }
         }
      } else {
         this.db("Da m\udaf1a du totem.");
      }
   }

   private void da() {
      int var1 = this.dk();
      if (var1 <= this.jT) {
         this.vc++;
         if (this.vc > 3) {
            this.dc("Luong mua t\ud8b0ong shop khong on dinh, da dung lai\ud9c0");
            return;
         }
      } else {
         this.jT = var1;
         this.vc = 0;
      }

      this.hr = 0;
      this.oU = false;
      this.sr = AutoBuyTotem.State.OpenShop;
   }

   private void db(String var1) {
      int var2 = Math.max(0, this.dk() - this.vj);
      boolean var3 = this.di() != null;
      this.dd();
      if (var3) {
         this.mc.player.closeHandledScreen();
      }

      if ((Boolean)this.BO.get()) {
         this.info(var1 + var2 + this.dk(), new Object[0]);
      }
   }

   private void dc(String var1) {
      this.dd();
      this.error(var1, new Object[0]);
      if (this.isActive()) {
         this.toggle();
      }
   }

   private void dd() {
      this.sr = AutoBuyTotem.State.Idle;
      this.oU = false;
      this.ne = false;
      this.iN = -1;
      this.Fk = 0;
      this.hr = 0;
      this.jT = 0;
      this.vc = 0;
   }

   private void de() {
      this.ne = true;
      this.iN = -1;
      this.Fk = 0;
      this.hd = this.eS;
      this.oU = true;
   }

   private void df(GenericContainerScreenHandler var1) {
      this.ne = false;
      this.iN = var1.syncId;
      this.Fk = var1.getRevision();
      this.hd = this.eS;
      this.oU = true;
   }

   private boolean dg(int var1, int var2) {
      if (this.ne || this.iN < 0) {
         return true;
      } else {
         return var1 == 0 ? true : var1 == this.iN && var2 >= this.Fk;
      }
   }

   private void dh(GenericContainerScreenHandler var1, int var2) {
      this.mc.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, this.mc.player);
   }

   private GenericContainerScreenHandler di() {
      return this.mc.player != null && this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var1 ? var1 : null;
   }

   private int dj(GenericContainerScreenHandler var1, Item var2) {
      int var3 = Math.min(var1.getRows() * 9, var1.slots.size());

      for (int var4 = 0; var4 < var3; var4++) {
         int var10000 = !var1.getSlot(var4).getStack().isOf(var2) ? 12510 : 12509;

         while (true) {
            int var5 = var10000;
            if (var5 == 12509) {
               return var4;
            }

            if (var5 != 12510) {
               break;
            }

            var10000 = 12508;
         }
      }

      return -1;
   }

   private int dk() {
      if (this.mc.player == null) {
         return 0;
      } else {
         int var1 = 0;

         for (int var2 = 0; var2 < 36; var2++) {
            ItemStack var3 = this.mc.player.getInventory().getStack(var2);
            if (var3.isOf(Items.TOTEM_OF_UNDYING)) {
               var1 += var3.getCount();
            }
         }

         ItemStack var4 = this.mc.player.getOffHandStack();
         if (var4.isOf(Items.TOTEM_OF_UNDYING)) {
            var1 += var4.getCount();
         }

         return var1;
      }
   }

   private boolean dl() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (this.mc.player.getInventory().getStack(var1).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public static enum BuyMode {
      MinToBuy,
      BindToBuy;
   }

   private static enum State {
      Idle,
      OpenShop,
      Navigate,
      Buying;
   }
}
