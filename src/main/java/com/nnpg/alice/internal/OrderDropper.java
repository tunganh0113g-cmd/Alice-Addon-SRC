package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.lang.invoke.StringConcatFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class OrderDropper extends AliceModule {
   private static final int wa = 51;
   private static final int dQ = 0;
   private static final int qx = 25;
   private static final int jR = 15;
   private static final int gK = 16;
   private static final long xT = 5000L;
   private static final int rz = 80;
   private static final int jv = -1273752556;
   private static final int fh = -11141206;
   private static final int lQ = -1;
   private static final int lb = -8847496;
   private static final int IP = -8856321;
   private final SettingGroup yF = this.settings.getDefaultGroup();
   private final SettingGroup IX = this.settings.createGroup("HUD");
   private final Setting<Integer> Bt = this.yF
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("delay")).description("thoi gian tre (tick) ap dung doc lap sau moi trang thai dieu huong"))
               .defaultValue(10))
            .min(0)
            .max(100)
            .sliderMax(40)
            .build()
      );
   private final Setting<Integer> ae = this.yF
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("drop-delay")).description("thoi gian tre toi thieu (ms) giua cac lan bam vut do tren GUI"))
               .defaultValue(1000))
            .min(0)
            .max(60000)
            .sliderMax(5000)
            .build()
      );
   private final Setting<Boolean> fZ = this.yF
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("auto-reconnect"))
                     .description("bat meteor autoreconnect va alice dang nhap, sau do tiep tuc vut do"))
                  .defaultValue(true))
               .onChanged(var1 -> this.CD()))
            .build()
      );
   private final Setting<OrderDropper.DropMode> pb = this.yF
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                     .name("drop-mode"))
                  .description("chon xem moi lan xac nhan tu server la vut 45 stack hay 1 stack"))
               .defaultValue(OrderDropper.DropMode.FortyFiveStacks))
            .build()
      );
   private final Setting<OrderDropper.CountMode> aY = this.yF
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                     .name("drop-count-mode"))
                  .description("vut cho den khi het do hoac dung lai sau mot so luong nhat dinh"))
               .defaultValue(OrderDropper.CountMode.Infinity))
            .build()
      );
   private final Setting<Integer> dD = this.yF
      .add(
         ((Builder)((Builder)((Builder)((Builder)new Builder().name("count")).description("so luong lo hang duoc xac nhan truoc khi hoan thanh"))
                  .defaultValue(10))
               .min(1)
               .max(100000)
               .sliderMax(100)
               .visible(() -> this.aY.get() == OrderDropper.CountMode.MaxAttempt))
            .build()
      );
   private final Setting<Item> O = this.yF
      .add(
         ((meteordevelopment.meteorclient.settings.ItemSetting.Builder)((meteordevelopment.meteorclient.settings.ItemSetting.Builder)((meteordevelopment.meteorclient.settings.ItemSetting.Builder)new meteordevelopment.meteorclient.settings.ItemSetting.Builder()
                     .name("select-item"))
                  .description("chon item o slot tu 0 den 25 de order"))
               .defaultValue(Items.BONE))
            .build()
      );
   private final Setting<Boolean> IS = this.IX
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("show-hud"))
                  .description("hien thi thong ke Order Dropper"))
               .defaultValue(true))
            .build()
      );
   private final OrderClaimHelper.TickDelay gf = new OrderClaimHelper.TickDelay();
   private final OrderClaimHelper.ConfirmationGate bk = new OrderClaimHelper.ConfirmationGate();
   private final Map<Integer, Integer> zr = new HashMap<>();
   private final Set<Integer> CE = new HashSet<>();
   private final Set<Screen> Dx = Collections.newSetFromMap(new WeakHashMap<>());
   private OrderDropper.State hq = OrderDropper.State.Inactive;
   private int a = -1;
   private long Eh;
   private long xp;
   private long at;
   private long oP;
   private long gZ;
   private long rq;
   private long kY;
   private OrderDropper.DropMode iA = OrderDropper.DropMode.FortyFiveStacks;
   private boolean rF;
   private int Au;

   public OrderDropper() {
      super(AliceAddon.CATEGORY, "Order Dropper", "Nhan vat pham cua don da chon qua giao dien may chu va theo doi so luong da vut duoc xac nhan.");
      this.CQ();
   }

   @Override
   public void onActivate() {
      super.onActivate();
      this.CE.clear();
      this.a = -1;
      this.xp = 0L;
      this.at = System.currentTimeMillis();
      this.oP = 0L;
      this.gZ = 0L;
      this.rq = 0L;
      this.kY = 0L;
      this.rF = false;
      this.Au = 0;
      this.CO();
      this.iA = (OrderDropper.DropMode)this.pb.get();
      this.gf.Dr();
      this.CR(this.mc.currentScreen, this.mc);
      this.CE();
      this.Cy();
   }

   @Override
   public void onDeactivate() {
      super.onDeactivate();
      this.hq = OrderDropper.State.Inactive;
      this.CO();
      this.gf.Dr();
      this.rF = false;
      this.Au = 0;
   }

   @AliceEventHandler
   public void Cr(Pre var1) {
      if (this.hq != OrderDropper.State.Completed && this.hq != OrderDropper.State.Error) {
         int var10000 = this.hq != OrderDropper.State.Inactive ? '\uf0f5' : '\uf0f4';

         while (true) {
            switch (var10000) {
               case 61684:
                  return;
               case 61685:
                  var10000 = 61683;
                  break;
               default:
                  if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
                     if (this.CC()) {
                        return;
                     }

                     if (this.hq == OrderDropper.State.Dropping && this.CM() && this.CI()) {
                        return;
                     }

                     if (this.aY.get() == OrderDropper.CountMode.MaxAttempt && this.gZ >= ((Integer)this.dD.get()).intValue()) {
                        this.CJ("Maximum confirmed attempts reached.");
                        return;
                     }

                     if (!this.gf.Ds()) {
                        return;
                     }

                     switch (this.hq.ordinal() ^ 1147394519) {
                        case 1147394515:
                           this.CB();
                           break;
                        case 1147394516:
                           this.CA();
                           break;
                        case 1147394517:
                           this.Cz();
                           break;
                        case 1147394518:
                           this.Cy();
                     }

                     return;
                  }

                  return;
            }
         }
      }
   }

   @AliceEventHandler
   public void Cs(GameLeftEvent var1) {
      if (this.isActive() && !this.CI() && (Boolean)this.fZ.get()) {
         this.hq = OrderDropper.State.WaitingReconnect;
         this.rF = false;
         this.Au = 0;
         this.CO();
         this.gf.Dr();
      }
   }

   @AliceEventHandler
   public void Ct(GameJoinedEvent var1) {
      if (this.isActive() && !this.CI()) {
         this.CE();
         this.a = -1;
         this.xp = 0L;
         this.CO();
         this.gf.Dr();
         this.rF = false;
         this.Au = 0;
         if ((Boolean)this.fZ.get()) {
            this.hq = OrderDropper.State.WaitingReconnect;
         } else {
            this.hq = OrderDropper.State.SendOrder;
            this.Eh = System.currentTimeMillis();
            this.gf.Dq((Integer)this.Bt.get());
         }
      }
   }

   @AliceEventHandler
   public void Cu(EntityAddedEvent var1) {
      if (this.isActive() && this.hq == OrderDropper.State.Dropping) {
         if (this.mc.player != null && var1.entity instanceof ItemEntity var2) {
            ItemStack var4 = var2.getStack();
            if (this.bk.Do(var4.isOf((Item)this.O.get()), OrderClaimHelper.Dv(var2.squaredDistanceTo(this.mc.player)))) {
               this.Cv();
            }
         }
      }
   }

   private void Cv() {
      this.zr.clear();
      OrderClaimHelper.BatchTotals var1 = OrderClaimHelper.Du(this.iA == OrderDropper.DropMode.FortyFiveStacks);
      this.gZ++;
      this.rq = this.rq + var1.stacks();
      this.kY = this.kY + var1.items();
      if (this.aY.get() == OrderDropper.CountMode.MaxAttempt && this.gZ >= ((Integer)this.dD.get()).intValue()) {
         this.CJ("Maximum confirmed attempts reached.");
      }
   }

   @AliceEventHandler
   public void Cw(ReceiveMessageEvent var1) {
      if (this.isActive() && !this.CI()) {
         if (OrderClaimHelper.Dt(var1.getMessage().getString())) {
            if (this.a >= 0 && this.a <= 25) {
               this.CE.add(this.a);
               this.a = -1;
               this.CO();
               this.Cy();
            }
         }
      }
   }

   @AliceEventHandler
   public void Cx(Render2DEvent var1) {
      if (!(this.mc.currentScreen instanceof HandledScreen)) {
         this.CS(var1.drawContext);
      }
   }

   public String getInfoString() {
      return this.isActive() ? this.CV() : null;
   }

   private void Cy() {
      if (this.mc.player != null && this.mc.player.networkHandler != null) {
         this.CK();
         this.a = -1;
         this.CO();
         this.mc.player.networkHandler.sendChatCommand("order");
         this.hq = OrderDropper.State.WaitOrderGui;
         this.Eh = System.currentTimeMillis();
         this.gf.Dq((Integer)this.Bt.get());
      } else {
         this.hq = OrderDropper.State.SendOrder;
         this.Eh = System.currentTimeMillis();
         this.gf.Dq((Integer)this.Bt.get());
      }
   }

   private void Cz() {
      GenericContainerScreenHandler var1 = this.CG();
      if (var1 != null && var1.slots.size() > 51) {
         this.CF(var1, 51);
         this.hq = OrderDropper.State.FindItem;
         this.Eh = System.currentTimeMillis();
         this.gf.Dq((Integer)this.Bt.get());
      } else {
         if (this.CH()) {
            this.Cy();
         }
      }
   }

   private void CA() {
      GenericContainerScreenHandler var1 = this.CG();
      if (var1 == null) {
         this.Cy();
      } else {
         int var2 = Math.min(25, var1.slots.size() - 1);
         int var3 = OrderClaimHelper.Dx(0, var2, this.CE, var2x -> var1.getSlot(var2x).getStack().isOf((Item)this.O.get()));
         if (var3 < 0) {
            this.CJ(
               StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","No unexhausted order listing remains for ",".">(
                  ((Item)this.O.get()).getName().getString()
               )
            );
         } else {
            this.a = var3;
            this.CF(var1, var3);
            this.hq = OrderDropper.State.Dropping;
            this.Eh = System.currentTimeMillis();
            this.xp = 0L;
            this.CO();
            this.gf.Dq((Integer)this.Bt.get());
         }
      }
   }

   private void CB() {
      GenericContainerScreenHandler var1 = this.CG();
      if (var1 == null) {
         this.Cy();
      } else {
         OrderDropper.DropMode var2 = (OrderDropper.DropMode)this.pb.get();
         int var3 = var2 == OrderDropper.DropMode.FortyFiveStacks ? 15 : 16;
         if (var1.slots.size() > var3) {
            long var4 = System.currentTimeMillis();
            if (this.xp <= 0L || var4 - this.xp >= ((Integer)this.ae.get()).intValue()) {
               this.iA = var2;
               this.CL();
               this.bk.Dm();
               this.CF(var1, var3);
               this.xp = var4;
               this.Eh = var4;
            }
         } else {
            if (this.CH()) {
               this.Cy();
            }
         }
      }
   }

   private boolean CC() {
      if ((Boolean)this.fZ.get()) {
         AliceAutoReconnect var1 = (AliceAutoReconnect)Modules.get().get(AliceAutoReconnect.class);
         if (var1 != null && !var1.isIdle()) {
            if (this.hq != OrderDropper.State.WaitingReconnect) {
               this.CK();
               this.CO();
               this.gf.Dr();
            }

            this.hq = OrderDropper.State.WaitingReconnect;
            this.rF = true;
            this.Au = 0;
            return true;
         } else if (this.hq == OrderDropper.State.WaitingReconnect) {
            this.Au++;
            if (!this.rF && this.Au < 80) {
               return true;
            } else {
               this.rF = false;
               this.Au = 0;
               this.Cy();
               return true;
            }
         } else {
            return false;
         }
      } else if (this.hq != OrderDropper.State.WaitingReconnect) {
         this.rF = false;
         this.Au = 0;
         return false;
      } else {
         this.rF = false;
         this.Au = 0;
         this.Cy();
         return true;
      }
   }

   private void CD() {
      if (this.isActive()) {
         if ((Boolean)this.fZ.get()) {
            this.CE();
         } else {
            if (this.hq == OrderDropper.State.WaitingReconnect) {
               this.rF = false;
               this.Au = 0;
               this.hq = OrderDropper.State.SendOrder;
               this.Eh = System.currentTimeMillis();
               this.gf.Dr();
            }
         }
      }
   }

   private void CE() {
      if (this.isActive() && (Boolean)this.fZ.get()) {
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

   private void CF(GenericContainerScreenHandler var1, int var2) {
      this.mc.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, this.mc.player);
   }

   private GenericContainerScreenHandler CG() {
      return this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var1 ? var1 : null;
   }

   private boolean CH() {
      return System.currentTimeMillis() - this.Eh >= 5000L;
   }

   private boolean CI() {
      return this.hq == OrderDropper.State.Completed || this.hq == OrderDropper.State.Error || this.hq == OrderDropper.State.Inactive;
   }

   private void CJ(String var1) {
      this.hq = OrderDropper.State.Completed;
      this.oP = System.currentTimeMillis();
      this.CO();
      this.gf.Dr();
      this.CK();
      this.info(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001\u0002"," Order Dropper completed.">(var1), new Object[0]);
   }

   private void CK() {
      if (this.mc.currentScreen != null) {
         if (this.mc.player != null && this.mc.player.currentScreenHandler != this.mc.player.playerScreenHandler) {
            this.mc.player.closeHandledScreen();
         } else {
            this.mc.setScreen(null);
         }
      }
   }

   private void CL() {
      this.zr.clear();
      this.zr.putAll(this.CN());
   }

   private boolean CM() {
      if (!this.bk.Dp()) {
         return false;
      } else {
         Map var1 = this.CN();
         if (OrderClaimHelper.Dy(this.zr, var1)) {
            if (!this.bk.Do(true, true)) {
               return false;
            } else {
               this.Cv();
               return true;
            }
         } else {
            return false;
         }
      }
   }

   private Map<Integer, Integer> CN() {
      HashMap var1 = new HashMap();
      if (this.mc.player != null && this.mc.world != null) {
         Item var2 = (Item)this.O.get();

         for (Entity var4 : this.mc.world.getEntities()) {
            if (var4 instanceof ItemEntity var5) {
               int var10000 = !var5.getStack().isOf(var2) ? 14119 : 14120;

               while (true) {
                  int var6 = var10000;
                  if (var6 == 14119) {
                     break;
                  }

                  if (var6 != 14120) {
                     if (OrderClaimHelper.Dv(var5.squaredDistanceTo(this.mc.player))) {
                        var1.put(var5.getId(), var5.getStack().getCount());
                     }
                     break;
                  }

                  var10000 = 14118;
               }
            }
         }

         return var1;
      } else {
         return var1;
      }
   }

   private void CO() {
      this.bk.Dn();
      this.zr.clear();
   }

   private long CP() {
      if (this.at > 0L) {
         long var1 = this.oP <= 0L ? System.currentTimeMillis() : this.oP;
         return Math.max(0L, var1 - this.at);
      } else {
         return 0L;
      }
   }

   private void CQ() {
      ScreenEvents.AFTER_INIT.register(new AfterInit() {
         public void afterInit(MinecraftClient var1, Screen var2, int var3, int var4) {
            OrderDropper.this.CR(var2, var1);
         }
      });
   }

   private void CR(Screen var1, final MinecraftClient var2) {
      if (var1 instanceof HandledScreen && this.Dx.add(var1)) {
         ScreenEvents.afterRender(var1).register(new AfterRender() {
            public void afterRender(Screen var1, DrawContext var2x, int var3, int var4, float var5) {
               if (var2.currentScreen == var1) {
                  OrderDropper.this.CS(var2x);
               }
            }
         });
      }
   }

   private void CS(DrawContext var1) {
      if (this.isActive() && (Boolean)this.IS.get() && this.mc.player != null && var1 != null) {
         String[] var2 = new String[]{
            StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Uptime: ">(this.CU(this.CP())),
            this.rq + "",
            this.kY + "",
            StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Progress: ">(this.CT())
         };
         int[] var3 = new int[]{-1, -8847496, -8847496, -8856321};
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

   private String CT() {
      String var1 = StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001"," - ">(this.CV());
      if (this.aY.get() == OrderDropper.CountMode.Infinity) {
         String var7 = this.hq == OrderDropper.State.Completed ? "Completed - " : "";
         return var7 + this.gZ + var1;
      } else {
         long var2 = ((Integer)this.dD.get()).intValue();
         double var4 = OrderClaimHelper.Dw(this.gZ, var2);
         String var6 = this.hq == OrderDropper.State.Completed ? "Completed - " : "";
         return String.format(Locale.ROOT, "%s%d / %d (%.1f%%)%s", var6, this.gZ, var2, var4, var1);
      }
   }

   private String CU(long var1) {
      long var3 = var1 / 1000L;
      long var5 = var3 / 3600L;
      long var7 = var3 % 3600L / 60L;
      long var9 = var3 % 60L;
      if (var5 > 0L) {
         return String.format(Locale.ROOT, "%dh %dm %ds", var5, var7, var9);
      } else {
         int var10000 = var7 <= 0L ? 10102 : 10101;

         while (true) {
            int var11 = var10000;
            if (var11 == 10101) {
               return String.format(Locale.ROOT, "%dm %ds", var7, var9);
            }

            if (var11 != 10102) {
               return var9 + "";
            }

            var10000 = 10100;
         }
      }
   }

   private String CV() {
      return switch (this.hq.ordinal() ^ -1288487787) {
         case -1288487792 -> "Auto Reconnect";
         case -1288487791 -> this.bk.Dp() ? "Waiting Confirmation" : "Dropping";
         case -1288487790 -> "Error";
         case -1288487789 -> "Completed";
         case -1288487788 -> "Sending /order";
         case -1288487787 -> "Inactive";
         case -1288487786 -> "Selecting Item";
         case -1288487785 -> "Opening Orders";
         default -> throw new MatchException(null, null);
      };
   }

   public static enum CountMode {
      Infinity("Infinity"),
      MaxAttempt("Max Attempt");

      private final String Ex;

      private CountMode(String var3) {
         this.Ex = var3;
      }

      @Override
      public String toString() {
         return this.Ex;
      }
   }

   public static enum DropMode {
      FortyFiveStacks("45 Stacks"),
      OneStack("1 Stack");

      private final String yv;

      private DropMode(String var3) {
         this.yv = var3;
      }

      @Override
      public String toString() {
         return this.yv;
      }
   }

   private static enum State {
      Inactive,
      SendOrder,
      WaitOrderGui,
      FindItem,
      Dropping,
      WaitingReconnect,
      Completed,
      Error;
   }
}
