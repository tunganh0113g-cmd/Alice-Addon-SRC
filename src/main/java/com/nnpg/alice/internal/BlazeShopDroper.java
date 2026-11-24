package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.lang.invoke.StringConcatFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.WeakHashMap;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AutoReconnect;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterInit;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class BlazeShopDroper extends AliceModule {
   private static final int FT = 36;
   private static final int FZ = 13;
   private static final int Gi = 17;
   private static final int cW = 23;
   private static final int aM = 80;
   private static final int jA = -1273752556;
   private static final int gA = -11141206;
   private static final int lx = -1;
   private static final int aJ = -8847496;
   private static final int Cx = -8856321;
   private final SettingGroup yZ = this.settings.getDefaultGroup();
   private final SettingGroup gG = this.settings.createGroup("HUD");
   private final Setting<Integer> Aw = this.yZ
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("delay")).description("thoi gian tre (tick) khi dieu huong shop va mua lien tuc o slot 23"))
               .defaultValue(10))
            .min(1)
            .max(100)
            .sliderMax(40)
            .build()
      );
   private final Setting<Integer> HR = this.yZ
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("click-buy-delay")).description("thoi gian tre (tick) sau khi click vao nut chuan bi mua o slot 17"))
               .defaultValue(4))
            .min(0)
            .max(100)
            .sliderMax(40)
            .build()
      );
   private final Setting<Integer> bg = this.yZ
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("drop-delay")).description("thoi gian tre 1 lan sau khi tui day roi moi vut het blaze rod"))
               .defaultValue(1))
            .min(0)
            .max(100)
            .sliderMax(40)
            .build()
      );
   private final Setting<Boolean> ac = this.yZ
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("auto-reconnect"))
                     .description("bat meteor autoreconnect va alice dang nhap, sau do tiep tuc mua"))
                  .defaultValue(true))
               .onChanged(var1 -> this.jU()))
            .build()
      );
   private final Setting<BlazeShopDroper.LimitMode> t = this.yZ
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                     .name("limit-mode"))
                  .description("chon gioi han so stack roi, gioi han tien, hoac mua khong gioi han"))
               .defaultValue(BlazeShopDroper.LimitMode.MaxStackAmount))
            .build()
      );
   private final Setting<Integer> Cj = this.yZ
      .add(
         ((Builder)((Builder)((Builder)((Builder)new Builder().name("max-stack-amount")).description("so stack blaze rod roi ra toi da")).defaultValue(36))
               .min(1)
               .max(3600)
               .sliderMax(360)
               .visible(() -> this.t.get() == BlazeShopDroper.LimitMode.MaxStackAmount))
            .build()
      );
   private final Setting<Double> Je = this.yZ
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                     .name("max-money"))
                  .description("so tien toi da de xai, don vi trieu. vd: 0.5 la 500k"))
               .defaultValue(1.0)
               .min(0.001)
               .max(1000.0)
               .sliderMax(100.0)
               .visible(() -> this.t.get() == BlazeShopDroper.LimitMode.MaxMoney))
            .build()
      );
   private final Setting<Boolean> tw = this.gG
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("show-hud"))
                  .description("hien thi thong ke cua BlazeShopDroper"))
               .defaultValue(true))
            .build()
      );
   private final OrderBuyHelper.TickDelay es = new OrderBuyHelper.TickDelay();
   private final OrderBuyHelper.TickDelay ta = new OrderBuyHelper.TickDelay();
   private final Set<Screen> hH = Collections.newSetFromMap(new WeakHashMap<>());
   private BlazeShopDroper.State HP = BlazeShopDroper.State.Inactive;
   private BlazeShopDroper.State DX = BlazeShopDroper.State.OpenShop;
   private long bI;
   private long vA;
   private long vK;
   private long iu;
   private boolean ak;
   private boolean nu;
   private int cT;

   public BlazeShopDroper() {
      super(AliceAddon.CATEGORY, "BlazeShopDroper", "Tu dong mua que quy lua, vut vat pham khi day tui va theo doi phien hoat dong.");
      this.kk();
   }

   @Override
   public void onActivate() {
      super.onActivate();
      this.HP = BlazeShopDroper.State.OpenShop;
      this.DX = BlazeShopDroper.State.OpenShop;
      this.bI = System.currentTimeMillis();
      this.vA = 0L;
      this.vK = 0L;
      this.iu = 0L;
      this.ak = false;
      this.nu = false;
      this.cT = 0;
      this.es.kL();
      this.ta.kL();
      this.kl(this.mc.currentScreen, this.mc);
      this.jV();
   }

   @Override
   public void onDeactivate() {
      super.onDeactivate();
      this.HP = BlazeShopDroper.State.Inactive;
      this.es.kL();
      this.ta.kL();
   }

   @AliceEventHandler
   public void jH(Pre var1) {
      if (this.HP != BlazeShopDroper.State.Completed && this.HP != BlazeShopDroper.State.Error && this.HP != BlazeShopDroper.State.Inactive) {
         if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
            if (!this.jR()) {
               this.kb();
               if (this.HP == BlazeShopDroper.State.WaitingDrop) {
                  this.jQ();
               } else if (this.HP == BlazeShopDroper.State.Dropping) {
                  this.jS();
               } else if (this.kc()) {
                  this.DX = this.HP;
                  this.HP = BlazeShopDroper.State.WaitingDrop;
                  this.ta.kK((Integer)this.bg.get());
               } else if (this.es.kM()) {
                  GenericContainerScreenHandler var2 = this.jX();
                  int var10000 = this.HP != BlazeShopDroper.State.OpenShop ? '艔' : '艕';

                  while (true) {
                     int var3 = var10000;
                     if (var3 == 33364) {
                        if (var2 == null) {
                           this.info("Shop GUI closed. BlazeShopDroper stopped.", new Object[0]);
                           this.toggle();
                           return;
                        }
                        break;
                     }

                     if (var3 != 33365) {
                        break;
                     }

                     var10000 = 33363;
                  }

                  switch (this.HP) {
                     case OpenShop:
                        this.jM(var2);
                        break;
                     case SelectBlazeRod:
                        this.jN(var2);
                        break;
                     case PrepareBuy:
                        this.jO(var2);
                        break;
                     case Buying:
                        this.jP(var2);
                  }
               }
            }
         }
      }
   }

   @AliceEventHandler
   public void jI(GameLeftEvent var1) {
      int var10000 = !this.isActive() ? 17381 : 17380;

      while (true) {
         switch (var10000) {
            case 17380:
               if (this.ke()) {
                  break;
               }

               var10000 = this.ac.get() ? '菭' : '菬';

               while (true) {
                  switch (var10000) {
                     case 33772:
                        return;
                     case 33773:
                        var10000 = 33771;
                        break;
                     default:
                        this.HP = BlazeShopDroper.State.WaitingReconnect;
                        this.nu = false;
                        this.cT = 0;
                        this.es.kL();
                        this.ta.kL();
                        return;
                  }
               }
            case 17381:
               var10000 = 17379;
               continue;
         }

         return;
      }
   }

   @AliceEventHandler
   public void jJ(GameJoinedEvent var1) {
      if (this.isActive() && !this.ke()) {
         this.jV();
         this.DX = BlazeShopDroper.State.OpenShop;
         this.es.kL();
         this.ta.kL();
         this.nu = false;
         this.cT = 0;
         this.HP = !this.ac.get() ? BlazeShopDroper.State.OpenShop : BlazeShopDroper.State.WaitingReconnect;
      }
   }

   @AliceEventHandler
   public void jK(ReceiveMessageEvent var1) {
      if (this.isActive() && !this.ak && !this.ke()) {
         if (OrderBuyHelper.kO(var1.getMessage().getString())) {
            this.ak = true;
            this.es.kL();
            this.ta.kL();
            this.HP = BlazeShopDroper.State.Dropping;
            this.info("Not enough money. Dropping the remaining Blaze Rods.", new Object[0]);
         }
      }
   }

   @AliceEventHandler
   public void jL(Render2DEvent var1) {
      if (!(this.mc.currentScreen instanceof HandledScreen)) {
         this.km(var1.drawContext);
      }
   }

   public String getInfoString() {
      return !this.isActive() ? null : this.kr();
   }

   private void jM(GenericContainerScreenHandler var1) {
      if (var1 == null) {
         this.mc.player.networkHandler.sendChatCommand("shop");
         this.es.kK((Integer)this.Aw.get());
      } else {
         int var2 = this.jY(var1, Items.NETHERRACK);
         if (var2 >= 0) {
            this.jW(var1, var2);
            this.HP = BlazeShopDroper.State.SelectBlazeRod;
            this.es.kK((Integer)this.Aw.get());
         } else {
            this.es.kK((Integer)this.Aw.get());
         }
      }
   }

   private void jN(GenericContainerScreenHandler var1) {
      int var2 = this.jY(var1, Items.BLAZE_ROD);
      if (var2 >= 0) {
         this.jW(var1, var2);
         this.HP = BlazeShopDroper.State.PrepareBuy;
         this.es.kK((Integer)this.Aw.get());
      } else {
         this.es.kK((Integer)this.Aw.get());
      }
   }

   private void jO(GenericContainerScreenHandler var1) {
      if (!this.ka(var1)) {
         this.jT();
      } else if (var1.slots.size() <= 17) {
         this.kg("Shop GUI does not contain slot 17.");
      } else {
         this.jW(var1, 17);
         this.HP = BlazeShopDroper.State.Buying;
         this.es.kK((Integer)this.HR.get());
      }
   }

   private void jP(GenericContainerScreenHandler var1) {
      if (this.ka(var1)) {
         if (var1.slots.size() <= 23) {
            this.kg("Shop GUI does not contain slot 23.");
         } else {
            this.jW(var1, 23);
            this.es.kK((Integer)this.Aw.get());
         }
      } else {
         this.jT();
      }
   }

   private void jQ() {
      int var10000 = !this.kc() ? '쓹' : '쓺';

      while (true) {
         int var1 = var10000;
         if (var1 == 50425) {
            this.HP = this.DX;
            this.ta.kL();
            return;
         }

         if (var1 != 50426) {
            if (!this.ta.kM()) {
               return;
            }

            this.HP = BlazeShopDroper.State.Dropping;
            this.jS();
            return;
         }

         var10000 = 50424;
      }
   }

   private boolean jR() {
      if (!(Boolean)this.ac.get()) {
         if (this.HP == BlazeShopDroper.State.WaitingReconnect) {
            this.HP = BlazeShopDroper.State.OpenShop;
         }

         this.nu = false;
         this.cT = 0;
         return false;
      } else {
         AliceAutoReconnect var1 = (AliceAutoReconnect)Modules.get().get(AliceAutoReconnect.class);
         if (var1 != null && !var1.isIdle()) {
            this.HP = BlazeShopDroper.State.WaitingReconnect;
            this.nu = true;
            this.cT = 0;
            this.es.kL();
            this.ta.kL();
            return true;
         } else if (this.HP != BlazeShopDroper.State.WaitingReconnect) {
            return false;
         } else {
            this.cT++;
            if (!this.nu && this.cT < 80) {
               return true;
            } else {
               this.HP = BlazeShopDroper.State.OpenShop;
               this.DX = BlazeShopDroper.State.OpenShop;
               this.nu = false;
               this.cT = 0;
               this.es.kK((Integer)this.Aw.get());
               return true;
            }
         }
      }
   }

   private void jS() {
      ScreenHandler var1 = this.mc.player.currentScreenHandler;
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();

      for (int var4 = 0; var4 < 36; var4++) {
         ItemStack var5 = this.mc.player.getInventory().getStack(var4);
         if (var5.isOf(Items.BLAZE_ROD)) {
            int var6 = this.jZ(var1, var4);
            if (var6 >= 0) {
               var2.add(var6);
               var3.add(var5.getCount());
            }
         }
      }

      if (var2.isEmpty()) {
         if (this.ak) {
            this.kf("Out of money.");
         } else {
            this.kg("Inventory is full but contains no Blaze Rods to drop.");
         }
      } else {
         int[] var9 = new int[var3.size()];
         int var10 = 0;

         while (true) {
            int var10000 = var10 >= var3.size() ? 24081 : 24080;

            while (true) {
               int var8 = var10000;
               if (var8 == 24080) {
                  var9[var10] = (Integer)var3.get(var10);
                  var10++;
                  break;
               }

               if (var8 != 24081) {
                  OrderBuyHelper.BatchTotals var11 = OrderBuyHelper.kS(var9);

                  for (int var7 : var2) {
                     this.mc.interactionManager.clickSlot(var1.syncId, var7, 1, SlotActionType.THROW, this.mc.player);
                  }

                  this.vK = this.vK + var11.stacks();
                  this.iu = this.iu + var11.items();
                  if (this.ak) {
                     this.kf("Out of money.");
                  } else if (!this.kd()) {
                     this.HP = this.DX;
                     this.es.kK((Integer)this.Aw.get());
                  } else {
                     this.kf("Buy limit reached.");
                  }

                  return;
               }

               var10000 = 24079;
            }
         }
      }
   }

   private void jT() {
      this.HP = BlazeShopDroper.State.OpenShop;
      this.es.kK((Integer)this.Aw.get());
   }

   private void jU() {
      if (this.isActive()) {
         if ((Boolean)this.ac.get()) {
            this.jV();
         } else {
            if (this.HP == BlazeShopDroper.State.WaitingReconnect) {
               this.HP = BlazeShopDroper.State.OpenShop;
            }

            this.nu = false;
            this.cT = 0;
         }
      }
   }

   private void jV() {
      if (this.isActive() && (Boolean)this.ac.get()) {
         Module var1 = Modules.get().get(AutoReconnect.class);
         if (var1 != null && !var1.isActive()) {
            var1.toggle();
         }

         AliceAutoReconnect var2 = (AliceAutoReconnect)Modules.get().get(AliceAutoReconnect.class);
         if (var2 != null && !var2.isActive()) {
            var2.toggle();
         }
      }
   }

   private void jW(GenericContainerScreenHandler var1, int var2) {
      this.mc.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, this.mc.player);
   }

   private GenericContainerScreenHandler jX() {
      return this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var1 ? var1 : null;
   }

   private int jY(GenericContainerScreenHandler var1, Item var2) {
      int var3 = Math.min(var1.getRows() * 9, var1.slots.size());

      for (int var4 = 0; var4 < var3; var4++) {
         if (var1.getSlot(var4).getStack().isOf(var2)) {
            return var4;
         }
      }

      return -1;
   }

   private int jZ(ScreenHandler var1, int var2) {
      for (Slot var4 : var1.slots) {
         if (var4.inventory == this.mc.player.getInventory() && var4.getIndex() == var2) {
            return var4.id;
         }
      }

      return -1;
   }

   private boolean ka(GenericContainerScreenHandler var1) {
      return var1.slots.size() > 13 && var1.getSlot(13).getStack().isOf(Items.BLAZE_ROD);
   }

   private void kb() {
      int var1 = this.mc.player.getInventory().getSelectedSlot();
      if (!this.mc.player.getInventory().getStack(var1).isEmpty()) {
         for (int var2 = 0; var2 < 9; var2++) {
            if (this.mc.player.getInventory().getStack(var2).isEmpty()) {
               this.mc.player.getInventory().setSelectedSlot(var2);
               return;
            }
         }
      }
   }

   private boolean kc() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (this.mc.player.getInventory().getStack(var1).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private boolean kd() {
      return switch (((BlazeShopDroper.LimitMode)this.t.get()).ordinal() ^ -181035292) {
         case -181035292 -> this.vK >= ((Integer)this.Cj.get()).intValue();
         case -181035291 -> this.kh() >= this.ki();
         case -181035290 -> false;
         default -> throw new MatchException(null, null);
      };
   }

   private boolean ke() {
      return this.HP == BlazeShopDroper.State.Completed || this.HP == BlazeShopDroper.State.Error || this.HP == BlazeShopDroper.State.Inactive;
   }

   private void kf(String var1) {
      this.HP = BlazeShopDroper.State.Completed;
      this.vA = System.currentTimeMillis();
      this.es.kL();
      this.ta.kL();
      this.info(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001\u0002"," BlazeShopDroper completed.">(var1), new Object[0]);
   }

   private void kg(String var1) {
      this.HP = BlazeShopDroper.State.Error;
      this.vA = System.currentTimeMillis();
      this.es.kL();
      this.ta.kL();
      this.error(var1, new Object[0]);
   }

   private long kh() {
      return OrderBuyHelper.kQ(this.iu);
   }

   private long ki() {
      return OrderBuyHelper.kP((Double)this.Je.get());
   }

   private long kj() {
      if (this.bI <= 0L) {
         return 0L;
      } else {
         long var1 = this.vA > 0L ? this.vA : System.currentTimeMillis();
         return Math.max(0L, var1 - this.bI);
      }
   }

   private void kk() {
      ScreenEvents.AFTER_INIT.register(new AfterInit() {
         public void afterInit(MinecraftClient var1, Screen var2, int var3, int var4) {
            BlazeShopDroper.this.kl(var2, var1);
         }
      });
   }

   private void kl(Screen var1, final MinecraftClient var2) {
      if (var1 instanceof HandledScreen && this.hH.add(var1)) {
         ScreenEvents.afterRender(var1).register(new AfterRender() {
            public void afterRender(Screen var1, DrawContext var2x, int var3, int var4, float var5) {
               if (var2.currentScreen == var1) {
                  BlazeShopDroper.this.km(var2x);
               }
            }
         });
      }
   }

   private void km(DrawContext var1) {
      if (this.isActive() && (Boolean)this.tw.get() && this.mc.player != null && var1 != null) {
         String[] var2 = new String[]{
            this.vK + "",
            this.iu + "",
            StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Money Spent: $">(this.kp(this.kh())),
            StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Uptime: ">(this.kq(this.kj())),
            StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Progress: ">(this.kn())
         };
         int[] var3 = new int[]{-1, -1, -8847496, -1, -8856321};
         byte var4 = 4;
         byte var5 = 2;
         byte var6 = 9;
         int var7 = 0;

         for (String var11 : var2) {
            var7 = Math.max(var7, this.mc.textRenderer.getWidth(var11));
         }

         var7 += var4 * 2;
         int var14 = var2.length * var6 + (var2.length - 1) * var5 + var4 * 2 + 2;
         int var15 = this.mc.getWindow().getScaledWidth() / 2 - var7 - 10;
         int var16 = this.mc.getWindow().getScaledHeight() / 2 - var14 - 10;
         var1.fill(var15, var16, var15 + var7, var16 + var14, -1273752556);
         var1.fill(var15, var16, var15 + var7, var16 + 2, -11141206);
         int var17 = var16 + var4 + 3;

         for (int var12 = 0; var12 < var2.length; var12++) {
            var1.drawTextWithShadow(this.mc.textRenderer, var2[var12], var15 + var4, var17, var3[var12]);
            var17 += var6 + var5;
         }
      }
   }

   private String kn() {
      String var1 = StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001"," - ">(this.kr());
      if (this.t.get() != BlazeShopDroper.LimitMode.Infinity) {
         long var2;
         long var4;
         String var6;
         if (this.t.get() == BlazeShopDroper.LimitMode.MaxStackAmount) {
            var2 = this.vK;
            var4 = ((Integer)this.Cj.get()).intValue();
            var6 = " stacks";
         } else {
            var2 = this.kh();
            var4 = this.ki();
            var6 = " money";
         }

         double var7 = OrderBuyHelper.kR(var2, var4);
         String var9 = this.HP != BlazeShopDroper.State.Completed ? (this.HP != BlazeShopDroper.State.Error ? "" : "Error - ") : "Completed - ";
         return String.format(Locale.ROOT, "%s%.1f%% (%s / %s%s)%s", var9, var7, this.ko(var2), this.ko(var4), var6, var1);
      } else {
         return StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","∞ (N/A%)">(var1);
      }
   }

   private String ko(long var1) {
      return this.t.get() == BlazeShopDroper.LimitMode.MaxMoney
         ? StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","$">(this.kp(var1))
         : Long.toString(var1);
   }

   private String kp(long var1) {
      if (var1 >= 1000000000L) {
         return String.format(Locale.ROOT, "%.2fB", var1 / 1.0E9);
      } else if (var1 < 1000000L) {
         return var1 >= 1000L ? String.format(Locale.ROOT, "%.2fK", var1 / 1000.0) : Long.toString(var1);
      } else {
         return String.format(Locale.ROOT, "%.2fM", var1 / 1000000.0);
      }
   }

   private String kq(long var1) {
      long var3 = var1 / 1000L;
      long var5 = var3 / 3600L;
      long var7 = var3 % 3600L / 60L;
      long var9 = var3 % 60L;
      if (var5 <= 0L) {
         return var7 > 0L ? String.format(Locale.ROOT, "%dm %ds", var7, var9) : var9 + "";
      } else {
         return String.format(Locale.ROOT, "%dh %dm %ds", var5, var7, var9);
      }
   }

   private String kr() {
      int var1 = this.HP.ordinal();
      String var10000;
      if (var1 == 0) {
         var10000 = "Inactive";
      } else if (var1 != 1) {
         if (var1 != 2) {
            if (var1 == 3) {
               var10000 = "Preparing Buy";
            } else if (var1 != 4) {
               if (var1 == 5) {
                  var10000 = "Waiting To Drop";
               } else if (var1 == 6) {
                  var10000 = "Dropping";
               } else if (var1 != 7) {
                  if (var1 != 8) {
                     if (var1 != 9) {
                        throw new MatchException(null, null);
                     }

                     var10000 = "Error";
                  } else {
                     var10000 = "Completed";
                  }
               } else {
                  var10000 = "Auto Reconnect";
               }
            } else {
               var10000 = "Buying";
            }
         } else {
            var10000 = "Selecting Blaze Rod";
         }
      } else {
         var10000 = "Opening Shop";
      }

      return var10000;
   }

   public static enum LimitMode {
      MaxStackAmount,
      MaxMoney,
      Infinity;
   }

   private static enum State {
      Inactive,
      OpenShop,
      SelectBlazeRod,
      PrepareBuy,
      Buying,
      WaitingDrop,
      Dropping,
      WaitingReconnect,
      Completed,
      Error;
   }
}
