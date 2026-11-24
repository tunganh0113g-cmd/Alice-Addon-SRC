package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.lang.invoke.StringConcatFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.ItemSetting.Builder;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterInit;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public final class AutoOrder extends AliceModule {
   private static final int zz = 200;
   private static final int id = 20;
   private static final int gx = -1273752556;
   private static final int uv = -11141206;
   private static final int rP = -1;
   private static final int oc = -8847496;
   private static final int cd = -8856321;
   private final SettingGroup kM = this.settings.getDefaultGroup();
   private final SettingGroup pr = this.settings.createGroup("Webhook");
   private final SettingGroup jY = this.settings.createGroup("HUD");
   private final Setting<Item> ny = this.kM
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("item")).description("item can hoan thanh. chon AIR de cho ma khong gui lenh"))
               .defaultValue(Items.AIR))
            .build()
      );
   private final Setting<Boolean> qi = this.kM
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("click-cauldron"))
                  .description("mo view tim kiem order bang nut cai vac"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> Jk = this.pr
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("webhook"))
                  .description("gui cap nhat session dat hang vao discord"))
               .defaultValue(false))
            .build()
      );
   private final Setting<String> Cw = this.pr
      .add(
         ((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)new meteordevelopment.meteorclient.settings.StringSetting.Builder()
                        .name("webhook-url"))
                     .description("dia chi webhook discord"))
                  .defaultValue(""))
               .visible(this.Jk::get))
            .build()
      );
   private final Setting<AutoOrder.WebhookMode> Co = this.pr
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                        .name("webhook-mode"))
                     .description("gui sau moi don hang hoac sau moi vai phut"))
                  .defaultValue(AutoOrder.WebhookMode.SendOnMinute))
               .visible(this.Jk::get))
            .build()
      );
   private final Setting<Integer> IL = this.pr
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("send-on-minute"))
                     .description("so phut giua cac lan cap nhat webhook"))
                  .defaultValue(5))
               .min(1)
               .max(1440)
               .sliderMax(60)
               .visible(() -> (Boolean)this.Jk.get() && this.Co.get() == AutoOrder.WebhookMode.SendOnMinute))
            .build()
      );
   private final Setting<Boolean> wo = this.jY
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("show-hud"))
                  .description("hien thi thong ke cua AutoOrder"))
               .defaultValue(true))
            .build()
      );
   private final Set<Screen> ux = Collections.newSetFromMap(new WeakHashMap<>());
   private final HttpClient rc = HttpClient.newHttpClient();
   private final OrderParser.StackCounter hn = new OrderParser.StackCounter();
   private final OrderParser.MinuteSchedule Jx = new OrderParser.MinuteSchedule();
   private AutoOrder.State sy = AutoOrder.State.SelectItem;
   private int IF;
   private int gy;
   private int rU;
   private String qF = "";
   private long mv;
   private double qI;
   private AutoOrder.PendingTransfer Ci;

   public AutoOrder() {
      super(AliceAddon.CATEGORY, "Auto-Order", "Tu dong hoan thanh lien tuc don hop le co gia cao nhat cho vat pham da chon.");
      this.fR();
   }

   @Override
   public void onActivate() {
      super.onActivate();
      this.mv = System.currentTimeMillis();
      this.qI = 0.0;
      this.hn.gA();
      this.Ci = null;
      this.rU = 0;
      this.qF = "";
      this.Jx.gv(this.mv, (Integer)this.IL.get());
      this.fJ(this.ny.get() != Items.AIR ? AutoOrder.State.SendCommand : AutoOrder.State.SelectItem, 0);
      this.fS(this.mc.currentScreen, this.mc);
   }

   @Override
   public void onDeactivate() {
      this.Ci = null;
      this.sy = AutoOrder.State.Inactive;
      super.onDeactivate();
   }

   public String getInfoString() {
      return this.isActive() ? this.fQ() : null;
   }

   @AliceEventHandler
   private void fm(Post var1) {
      if (this.isActive() && this.mc.player != null) {
         int var10000 = this.mc.world != null ? 1884 : 1885;

         while (true) {
            int var2 = var10000;
            if (var2 == 1884) {
               if (this.mc.interactionManager != null) {
                  this.fO();
                  if (this.ny.get() != Items.AIR) {
                     if (this.sy == AutoOrder.State.SelectItem) {
                        this.fJ(AutoOrder.State.SendCommand, 0);
                     }

                     if (this.IF > 0) {
                        this.IF--;
                        return;
                     }

                     this.gy++;
                     if (this.gy < 200) {
                        switch (this.sy) {
                           case SendCommand:
                              this.fq();
                              break;
                           case WaitInitialGui:
                              this.fr();
                              break;
                           case WaitSearchGui:
                              this.fs();
                              break;
                           case SearchPage:
                              this.ft();
                              break;
                           case WaitPage:
                              this.fu();
                              break;
                           case FillOrder:
                              this.fv();
                              break;
                           case WaitConfirmGui:
                              this.fw();
                              break;
                           case ConfirmOrder:
                              this.fx();
                              break;
                           case WaitOrderClose:
                              this.fy();
                              break;
                           case CloseOrder:
                              this.fz();
                        }

                        return;
                     }

                     this.fI();
                     return;
                  }

                  if (this.sy != AutoOrder.State.SelectItem) {
                     this.fN();
                     this.Ci = null;
                     this.fJ(AutoOrder.State.SelectItem, 0);
                  }

                  return;
               }
               break;
            }

            if (var2 != 1885) {
               break;
            }

            var10000 = 1883;
         }
      }
   }

   @AliceEventHandler
   private void fn(Receive var1) {
      if (this.Ci != null) {
         if (var1.packet instanceof ScreenHandlerSlotUpdateS2CPacket var5) {
            int var2 = var5.getSyncId();
            int var3 = var5.getRevision();
            if (var2 == this.Ci.ro && var3 >= this.Ci.oa) {
               int var10000 = !this.Ci.FY.gq(var5.getSlot()) ? '\uf255' : '\uf254';

               while (true) {
                  switch (var10000) {
                     case 62036:
                        int var15 = var5.getStack().isOf((Item)this.ny.get()) ? var5.getStack().getCount() : 0;
                        this.hn.gx(this.Ci.FY.go(var5.getSlot(), var15));
                        return;
                     case 62037:
                        var10000 = 62035;
                        break;
                     default:
                        return;
                  }
               }
            }
         } else {
            Packet var16 = var1.packet;
            int var18 = !(var16 instanceof InventoryS2CPacket) ? 13539 : 13538;

            while (true) {
               int var10 = var18;
               if (var10 == 13538) {
                  InventoryS2CPacket var4 = (InventoryS2CPacket)var16;
                  int var13 = var4.syncId();
                  int var14 = var4.revision();
                  if (var13 == this.Ci.ro) {
                     var18 = var14 < this.Ci.oa ? 27289 : 27288;

                     while (true) {
                        switch (var18) {
                           case 27288:
                              HashMap var17 = new HashMap();

                              for (int var8 : this.Ci.FY.gr()) {
                                 label118: {
                                    short var12;
                                    if (var8 >= 0) {
                                       if (var8 < var4.contents().size()) {
                                          var20 = (ItemStack)var4.contents().get(var8);
                                          break label118;
                                       }
                                    } else {
                                       while (var12 == 12758) {
                                          var12 = 12756;
                                       }
                                    }

                                    var20 = ItemStack.EMPTY;
                                 }

                                 ItemStack var9 = var20;
                                 var17.put(var8, var9.isOf((Item)this.ny.get()) ? var9.getCount() : 0);
                              }

                              this.hn.gx(this.Ci.FY.gp(var17));
                              return;
                           case 27289:
                              var18 = 27287;
                              break;
                           default:
                              return;
                        }
                     }
                  } else {
                     char var11 = '\udc87';
                     break;
                  }
               }

               if (var10 != 13539) {
                  return;
               }

               var18 = 13537;
            }
         }
      }
   }

   @AliceEventHandler
   private void fo(ReceiveMessageEvent var1) {
      int var10000 = this.isActive() ? '\ue9cc' : '\ue9cb';

      while (true) {
         switch (var10000) {
            case 59851:
               return;
            case 59852:
               var10000 = 59850;
               break;
            default:
               OrderParser.gF(var1.getMessage().getString()).ifPresent(var1x -> {
                  this.qI = this.qI + var1x.money();
                  if ((Boolean)this.Jk.get() && this.Co.get() == AutoOrder.WebhookMode.SendOnOrder) {
                     this.fP();
                  }

                  this.fI();
               });
               return;
         }
      }
   }

   @AliceEventHandler
   private void fp(Render2DEvent var1) {
      if (!(this.mc.currentScreen instanceof HandledScreen)) {
         this.fT(var1.drawContext);
      }
   }

   private void fq() {
      this.fN();
      String var1 = OrderParser.gB(Registries.ITEM.getId((Item)this.ny.get()).toString());
      if (!var1.isBlank() && this.ny.get() != Items.AIR) {
         this.mc.player.networkHandler.sendChatCommand(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","order ">(var1));
         this.rU = 0;
         this.fJ(AutoOrder.State.WaitInitialGui, this.fK());
      } else {
         this.fJ(AutoOrder.State.SelectItem, 0);
      }
   }

   private void fr() {
      GenericContainerScreenHandler var1 = this.fL();
      if (var1 != null) {
         if (!(Boolean)this.qi.get()) {
            this.fJ(AutoOrder.State.SearchPage, this.fK());
         } else {
            short var3 = 27061;

            for (int var2 = 0; var2 < var1.slots.size(); var2++) {
               if (var1.getSlot(var2).getStack().isOf(Items.CAULDRON)) {
                  this.fM(var1, var2, SlotActionType.PICKUP);
                  this.fN();
                  this.fJ(AutoOrder.State.WaitSearchGui, this.fK() * 3);
                  return;
               }
            }

            this.fJ(AutoOrder.State.SearchPage, this.fK());
         }
      }
   }

   private void fs() {
      if (this.fL() != null) {
         this.fJ(AutoOrder.State.SearchPage, this.fK());
      }
   }

   private void ft() {
      GenericContainerScreenHandler var1 = this.fL();
      if (var1 == null) {
         this.fI();
      } else {
         int var2 = this.fA(var1);
         if (var2 >= 0) {
            this.rU = 0;
            this.fM(var1, var2, SlotActionType.PICKUP);
            this.fJ(AutoOrder.State.FillOrder, this.fK());
         } else {
            int var10000 = var1.slots.size() <= 53 ? 25349 : 25348;

            label29:
            while (true) {
               switch (var10000) {
                  case 25348:
                     if (!var1.getSlot(53).getStack().isEmpty() && this.rU++ < 20) {
                        this.fM(var1, 53, SlotActionType.PICKUP);
                        this.qF = this.fE();
                        this.fJ(AutoOrder.State.WaitPage, this.fK());
                        return;
                     }
                     break label29;
                  case 25349:
                     var10000 = 25347;
                     break;
                  default:
                     break label29;
               }
            }

            this.fI();
         }
      }
   }

   private void fu() {
      if (!(this.mc.currentScreen instanceof GenericContainerScreen var1)) {
         this.fI();
      } else {
         String var3 = fF(var1.getTitle().getString());
         if (!var3.equals(this.qF)) {
            this.qF = var3;
            this.fJ(AutoOrder.State.SearchPage, 1);
         } else {
            this.IF = 1;
         }
      }
   }

   private void fv() {
      if (!(this.mc.currentScreen instanceof GenericContainerScreen var1)) {
         this.fI();
      } else {
         String var9 = fF(var1.getTitle().getString());
         if (!fG(var9)) {
            this.fI();
         } else {
            GenericContainerScreenHandler var3 = (GenericContainerScreenHandler)var1.getScreenHandler();
            boolean var4 = false;
            int var5 = Math.min(54, var3.slots.size());

            for (int var6 = 0; var6 < var5; var6++) {
               Slot var7 = var3.getSlot(var6);
               if (var7.inventory != this.mc.player.getInventory() && var7.getStack().isEmpty()) {
                  var4 = true;
                  break;
               }
            }

            if (!var4) {
               this.fN();
               this.fJ(AutoOrder.State.WaitConfirmGui, this.fK() * 5);
            } else {
               Map var10 = this.fC(var3);
               if (var10.isEmpty()) {
                  this.fN();
                  this.fJ(AutoOrder.State.WaitConfirmGui, this.fK() * 5);
               } else {
                  this.Ci = new AutoOrder.PendingTransfer(var3.syncId, var3.getRevision(), new OrderParser.ConfirmedTransfer(var10));

                  for (Slot var8 : var3.slots) {
                     if (var8.inventory == this.mc.player.getInventory() && var8.getStack().isOf((Item)this.ny.get())) {
                        this.mc.interactionManager.clickSlot(var3.syncId, var8.id, 0, SlotActionType.QUICK_MOVE, this.mc.player);
                     }
                  }

                  this.fJ(AutoOrder.State.FillOrder, this.fK() * 2);
               }
            }
         }
      }
   }

   private void fw() {
      if (this.fL() != null) {
         this.fJ(AutoOrder.State.ConfirmOrder, this.fK());
      }
   }

   private void fx() {
      GenericContainerScreenHandler var1 = this.fL();
      if (var1 == null) {
         this.fJ(AutoOrder.State.CloseOrder, 0);
      } else if (var1.slots.size() > 16) {
         this.fM(var1, 16, SlotActionType.PICKUP);
         this.fJ(AutoOrder.State.WaitOrderClose, this.fK());
      } else {
         this.fJ(AutoOrder.State.CloseOrder, 0);
      }
   }

   private void fy() {
      if (!(this.mc.currentScreen instanceof GenericContainerScreen var1)) {
         if (!this.fD()) {
            this.IF = this.fK();
         } else {
            this.fJ(AutoOrder.State.SendCommand, this.fK());
         }
      } else if (!this.fD()) {
         this.fN();
         this.fJ(AutoOrder.State.CloseOrder, this.fK());
      } else {
         String var3 = fF(var1.getTitle().getString());
         if (fH(var3)) {
            this.fJ(AutoOrder.State.FillOrder, this.fK());
         } else if (var3.contains("(") && var3.contains("/")) {
            this.fJ(AutoOrder.State.SearchPage, this.fK());
         } else {
            this.IF = this.fK();
         }
      }
   }

   private void fz() {
      this.fN();
      this.Ci = null;
      this.rU = 0;
      this.fJ(this.ny.get() == Items.AIR ? AutoOrder.State.SelectItem : AutoOrder.State.SendCommand, this.fK());
   }

   private int fA(GenericContainerScreenHandler var1) {
      ArrayList var2 = new ArrayList();
      int var3 = Math.min(45, var1.slots.size());

      for (int var4 = 0; var4 < var3; var4++) {
         ItemStack var5 = var1.getSlot(var4).getStack();
         if (!var5.isEmpty() && var5.isOf((Item)this.ny.get())) {
            List var6 = fB(var5);
            var2.add(new OrderParser.Candidate(var4, OrderParser.gC(var6), OrderParser.gD(var6)));
         }
      }

      return OrderParser.gE(var2);
   }

   private static List<String> fB(ItemStack var0) {
      LoreComponent var1 = (LoreComponent)var0.get(DataComponentTypes.LORE);
      if (var1 == null) {
         return Collections.emptyList();
      } else {
         ArrayList var2 = new ArrayList();

         for (Text var4 : var1.lines()) {
            var2.add(var4.getString());
         }

         return var2;
      }
   }

   private Map<Integer, Integer> fC(GenericContainerScreenHandler var1) {
      HashMap var2 = new HashMap();
      Iterator var3 = var1.slots.iterator();

      label24:
      while (true) {
         int var10000 = !var3.hasNext() ? '븨' : '븧';

         while (true) {
            switch (var10000) {
               case 48679:
                  Slot var4 = (Slot)var3.next();
                  if (var4.inventory == this.mc.player.getInventory() && var4.getStack().isOf((Item)this.ny.get())) {
                     var2.put(var4.id, var4.getStack().getCount());
                  }
                  continue label24;
               case 48680:
                  var10000 = 48678;
                  break;
               default:
                  return var2;
            }
         }
      }
   }

   private boolean fD() {
      if (this.mc.player != null && this.ny.get() != Items.AIR) {
         for (int var1 = 0; var1 < this.mc.player.getInventory().size(); var1++) {
            if (this.mc.player.getInventory().getStack(var1).isOf((Item)this.ny.get())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private String fE() {
      return this.mc.currentScreen instanceof GenericContainerScreen var1 ? fF(var1.getTitle().getString()) : "";
   }

   private static String fF(String var0) {
      return var0 == null ? "" : var0.replaceAll("§.", "");
   }

   private static boolean fG(String var0) {
      return var0 != null && var0.contains("ʙỏ ᴠậᴛ ᴘʜẩᴍ ᴠàᴏ để ɢɪᴀᴏ");
   }

   private static boolean fH(String var0) {
      return var0 != null && var0.contains("ʙỏ ᴠậᴛ ᴘʜẩᴍ");
   }

   private void fI() {
      this.Ci = null;
      this.fN();
      this.rU = 0;
      this.fJ(this.ny.get() == Items.AIR ? AutoOrder.State.SelectItem : AutoOrder.State.SendCommand, this.fK() * 3);
   }

   private void fJ(AutoOrder.State var1, int var2) {
      this.sy = var1;
      this.IF = Math.max(0, var2);
      this.gy = 0;
   }

   private int fK() {
      if (this.mc.getNetworkHandler() != null && this.mc.player != null) {
         PlayerListEntry var1 = this.mc.getNetworkHandler().getPlayerListEntry(this.mc.player.getUuid());
         int var2 = var1 == null ? 100 : var1.getLatency();
         if (var2 < 50) {
            return 2;
         } else if (var2 < 100) {
            return 3;
         } else if (var2 < 200) {
            return 5;
         } else {
            int var10000 = var2 >= 300 ? '댻' : '댺';

            while (true) {
               switch (var10000) {
                  case 45882:
                     return 8;
                  case 45883:
                     var10000 = 45881;
                     break;
                  default:
                     return 12;
               }
            }
         }
      } else {
         return 5;
      }
   }

   private GenericContainerScreenHandler fL() {
      return this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var1 ? var1 : null;
   }

   private void fM(GenericContainerScreenHandler var1, int var2, SlotActionType var3) {
      if (var2 >= 0 && var2 < var1.slots.size()) {
         this.mc.interactionManager.clickSlot(var1.syncId, var2, 0, var3, this.mc.player);
      }
   }

   private void fN() {
      if (this.mc.player != null && this.mc.currentScreen instanceof GenericContainerScreen) {
         this.mc.player.closeHandledScreen();
      }
   }

   private void fO() {
      if ((Boolean)this.Jk.get() && !((String)this.Cw.get()).isBlank() && this.Co.get() == AutoOrder.WebhookMode.SendOnMinute) {
         int var10000 = !this.Jx.gw(System.currentTimeMillis(), (Integer)this.IL.get()) ? '띞' : '띝';

         while (true) {
            switch (var10000) {
               case 46941:
                  this.fP();
                  return;
               case 46942:
                  var10000 = 46940;
                  break;
               default:
                  return;
            }
         }
      }
   }

   private void fP() {
      String var1 = ((String)this.Cw.get()).trim();
      if ((Boolean)this.Jk.get()) {
         int var10000 = !var1.isBlank() ? 'ꃤ' : 'ꃣ';

         while (true) {
            switch (var10000) {
               case 41187:
                  return;
               case 41188:
                  var10000 = 41186;
                  break;
               default:
                  String var2 = fX("Uptime", fV(this.fU())) + fX("Stacks Ordered", Long.toString(this.hn.gy())) + fX("Money Earned", fW(this.qI));
                  Thread.ofVirtual()
                     .name("order-session-webhook")
                     .start(
                        () -> {
                           HttpRequest var3 = HttpRequest.newBuilder()
                              .uri(URI.create(var1))
                              .header("Content-Type", "application/json")
                              .POST(BodyPublishers.ofString(var2))
                              .build();
                           this.rc.send(var3, BodyHandlers.discarding());
                        }
                     );
                  return;
            }
         }
      }
   }

   private String fQ() {
      int var1 = this.sy.ordinal();
      String var10000;
      if (var1 != 0) {
         if (var1 == 1) {
            var10000 = "Select Item";
         } else if (var1 == 2) {
            var10000 = "Sending /order";
         } else if (var1 == 3 || var1 == 4) {
            var10000 = "Waiting GUI";
         } else if (var1 == 5 || var1 == 6) {
            var10000 = "Searching";
         } else if (var1 == 7) {
            var10000 = "Delivering";
         } else {
            if (var1 != 8 && var1 != 9 && var1 != 10 && var1 != 11) {
               throw new MatchException(null, null);
            }

            var10000 = "Confirming";
         }
      } else {
         var10000 = "Inactive";
      }

      return var10000;
   }

   private void fR() {
      ScreenEvents.AFTER_INIT.register(new AfterInit() {
         public void afterInit(MinecraftClient var1, Screen var2, int var3, int var4) {
            AutoOrder.this.fS(var2, var1);
         }
      });
   }

   private void fS(Screen var1, final MinecraftClient var2) {
      if (var1 instanceof HandledScreen && this.ux.add(var1)) {
         ScreenEvents.afterRender(var1).register(new AfterRender() {
            public void afterRender(Screen var1, DrawContext var2x, int var3, int var4, float var5) {
               if (var2.currentScreen == var1) {
                  AutoOrder.this.fT(var2x);
               }
            }
         });
      }
   }

   private void fT(DrawContext var1) {
      if (this.isActive()) {
         int var10000 = !this.wo.get() ? 24190 : 24189;

         while (true) {
            switch (var10000) {
               case 24189:
                  if (this.mc.player != null && var1 != null) {
                     String[] var2 = new String[]{
                        this.hn.gy() + "",
                        StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Money Earned: ">(fW(this.qI)),
                        StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","State: ">(this.fQ()),
                        StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Uptime: ">(fV(this.fU()))
                     };
                     int[] var3 = new int[]{-1, -8847496, -8856321, -1};
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

                     return;
                  }

                  return;
               case 24190:
                  var10000 = 24188;
                  break;
               default:
                  return;
            }
         }
      }
   }

   private long fU() {
      return this.mv <= 0L ? 0L : Math.max(0L, System.currentTimeMillis() - this.mv);
   }

   private static String fV(long var0) {
      long var2 = Math.max(0L, var0 / 1000L);
      return String.format(Locale.ROOT, "%02d:%02d:%02d", var2 / 3600L, var2 % 3600L / 60L, var2 % 60L);
   }

   private static String fW(double var0) {
      if (var0 >= 1.0E9) {
         return String.format(Locale.ROOT, "$%.2fB", var0 / 1.0E9);
      } else if (!(var0 >= 1000000.0)) {
         return var0 >= 1000.0 ? String.format(Locale.ROOT, "$%.2fK", var0 / 1000.0) : String.format(Locale.ROOT, "$%.0f", var0);
      } else {
         return String.format(Locale.ROOT, "$%.2fM", var0 / 1000000.0);
      }
   }

   private static String fX(String var0, String var1) {
      return fY(var0) + fY(var1);
   }

   private static String fY(String var0) {
      return var0.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
   }

   private static final class PendingTransfer {
      private final int ro;
      private final int oa;
      private final OrderParser.ConfirmedTransfer FY;

      private PendingTransfer(int var1, int var2, OrderParser.ConfirmedTransfer var3) {
         this.ro = var1;
         this.oa = var2;
         this.FY = var3;
      }
   }

   private static enum State {
      Inactive,
      SelectItem,
      SendCommand,
      WaitInitialGui,
      WaitSearchGui,
      SearchPage,
      WaitPage,
      FillOrder,
      WaitConfirmGui,
      ConfirmOrder,
      WaitOrderClose,
      CloseOrder;
   }

   public static enum WebhookMode {
      SendOnOrder,
      SendOnMinute;
   }
}
