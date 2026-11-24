package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import com.nnpg.alice.bridge.NoPushBridge;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.StringConcatFactory;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AutoReconnect;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

public class AutoSell extends AliceModule {
   private static final long yk = 5000L;
   private static final int bT = 3;
   private static final long x = 4000L;
   private static final long cQ = 500L;
   private static final int R = 80;
   private static final int DF = 80;
   private static final Pattern ft = Pattern.compile("(?i)(?:\\+\\s*)?\\$[ \\t]*([0-9]+(?:\\.[0-9]+)?)\\s*([KMB]?)");
   private static final Color Hu = new Color(255, 255, 255, 255);
   private static final Color em = new Color(120, 255, 120, 255);
   private static final Color Hy = new Color(120, 220, 255, 255);
   private final SettingGroup av = this.settings.getDefaultGroup();
   private final SettingGroup dt = this.settings.createGroup("Selling");
   private final SettingGroup Fe = this.settings.createGroup("Auto Return");
   private final SettingGroup rI = this.settings.createGroup("Player Detection");
   private final SettingGroup aw = this.settings.createGroup("Discord Webhook");
   private final Setting<Boolean> nh = this.av
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("show-hud")).description("hien thi thoi gian on, tien, tong so da ban, trung binh va trang thai"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> pX = this.av
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("notifications")).description("hien thi tin nhan trang thai cua auto sell tren chat"))
               .defaultValue(true))
            .build()
      );
   private final Setting<AutoSell.FilterMode> aO = this.dt
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                     .name("filter-mode"))
                  .description("whitelist chi ban cac mon da chon, blacklist khong ban cac mon da chon"))
               .defaultValue(AutoSell.FilterMode.Blacklist))
            .build()
      );
   private final Setting<List<Item>> uM = this.dt
      .add(
         ((meteordevelopment.meteorclient.settings.ItemListSetting.Builder)((meteordevelopment.meteorclient.settings.ItemListSetting.Builder)new meteordevelopment.meteorclient.settings.ItemListSetting.Builder()
                  .name("items-to-sell"))
               .description("cac item duoc bao gom hoac loai tru theo Filter Mode"))
            .defaultValue(new Item[]{Items.ELYTRA})
            .build()
      );
   private final Setting<Integer> vF = this.dt
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("move-delay"))
                  .description("tick giua cac dot chuyen item binh thuong"))
               .defaultValue(1))
            .min(0)
            .max(100)
            .sliderMax(20)
            .build()
      );
   private final Setting<Integer> pz = this.dt
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("sell-delay"))
                  .description("tick cho sau khi bam slot xac nhan ban"))
               .defaultValue(4))
            .min(0)
            .max(100)
            .sliderMax(20)
            .build()
      );
   private final Setting<Boolean> hh = this.Fe
      .add(
         ((Builder)((Builder)((Builder)((Builder)new Builder().name("auto-return")).description("quay ve vi tri kich hoat khi gap dieu kien da chon"))
                  .defaultValue(false))
               .onChanged(var1 -> this.hy()))
            .build()
      );
   private final Setting<Boolean> eo = this.Fe
      .add(
         ((Builder)((Builder)((Builder)((Builder)new Builder().name("return-on-push")).description("quay ve sau khi bi day khoi vi tri kich hoat"))
                  .defaultValue(true))
               .visible(this.hh::get))
            .build()
      );
   private final Setting<Boolean> dN = this.Fe
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("no-push")).description("chi chan luc day cua nuoc/dong chay khi duoc bat")).defaultValue(false))
            .build()
      );
   private final Setting<Boolean> uh = this.Fe
      .add(
         ((Builder)((Builder)((Builder)((Builder)((Builder)new Builder().name("return-on-disconnect")).description("quay ve sau khi ket noi lai server"))
                     .defaultValue(true))
                  .visible(this.hh::get))
               .onChanged(var1 -> this.hy()))
            .build()
      );
   private final Setting<Boolean> wz = this.rI
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("player-detection"))
                  .description("phat hien bat ky nguoi choi nao duoc load ma khong co trong whitelist"))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> fr = this.rI
      .add(
         ((Builder)((Builder)((Builder)((Builder)new Builder().name("disconnect-on-player")).description("tu dong thoat khi phat hien nguoi choi o gan"))
                  .defaultValue(true))
               .visible(this.wz::get))
            .build()
      );
   private final Setting<List<String>> Jt = this.rI
      .add(
         ((meteordevelopment.meteorclient.settings.StringListSetting.Builder)((meteordevelopment.meteorclient.settings.StringListSetting.Builder)((meteordevelopment.meteorclient.settings.StringListSetting.Builder)((meteordevelopment.meteorclient.settings.StringListSetting.Builder)new meteordevelopment.meteorclient.settings.StringListSetting.Builder()
                        .name("whitelisted-players"))
                     .description("ten nguoi choi duoc bo qua"))
                  .defaultValue(new ArrayList()))
               .visible(this.wz::get))
            .build()
      );
   private final Setting<Boolean> he = this.aw
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("discord-tracking")).description("gui thong bao tinh trang va canh bao qua discord"))
               .defaultValue(false))
            .build()
      );
   private final Setting<String> Dd = this.aw
      .add(
         ((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)new meteordevelopment.meteorclient.settings.StringSetting.Builder()
                        .name("webhook-url"))
                     .description("dia chi webhook discord"))
                  .defaultValue(""))
               .visible(this.he::get))
            .build()
      );
   private final Setting<Integer> vH = this.aw
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("status-interval-seconds"))
                     .description("so giay giua cac lan gui webhook"))
                  .defaultValue(60))
               .min(10)
               .max(3600)
               .sliderMax(600)
               .visible(this.he::get))
            .build()
      );
   private final Setting<Boolean> ec = this.aw
      .add(
         ((Builder)((Builder)((Builder)((Builder)new Builder().name("notify-on-player")).description("gui canh bao webhook khi phat hien nguoi choi"))
                  .defaultValue(true))
               .visible(this.he::get))
            .build()
      );
   private AutoSell.State GM = AutoSell.State.IDLE;
   private long lo;
   private long kT;
   private int mT = -1;
   private int pi;
   private int jL;
   private int Ef;
   private double AB;
   private long oT;
   private Vec3d fW;
   private RegistryKey<World> ff;
   private String IN = "";
   private String sG = "";
   private double Fb;
   private boolean hN;
   private boolean jz;
   private int aR;
   private final Set<UUID> k = new HashSet<>();
   private boolean pW;
   private boolean X;
   private boolean gq;
   private int AF;
   private boolean GV;
   private boolean hg;
   private boolean gs;
   private boolean iR;
   private long IA;
   private long mG;
   private long wA;
   private boolean jm;
   private boolean sq;
   private boolean Ix;
   private boolean xd;
   private boolean qp;
   private long nL;
   private final HttpClient kr = HttpClient.newHttpClient();
   private final AutoSell.BaritoneAdapter fD = new AutoSell.BaritoneAdapter();
   private static Method Ep;
   private static boolean fz;

   public static Lookup getLookup() {
      return MethodHandles.lookup();
   }

   public AutoSell() {
      super(AliceAddon.CATEGORY, "auto-sell", "Tu dong dien va xac nhan giao dien /sell cua may chu, kem tuy chon kiem tra an toan.");
   }

   @Override
   public void onActivate() {
      super.onActivate();
      this.hN();
      this.hO();
      this.pW = false;
      this.X = false;
      this.gq = false;
      this.AF = 0;
      this.GV = false;
      this.hg = false;
      this.gs = false;
      this.iR = false;
      this.IA = 0L;
      this.mG = 0L;
      this.jm = false;
      this.sq = false;
      this.Ix = false;
      this.xd = false;
      this.hu();
      AutoSell.State var10001;
      if (this.mc.currentScreen instanceof GenericContainerScreen) {
         var10001 = AutoSell.State.MOVING_ITEMS;
      } else {
         short var1 = 8990;
         var10001 = AutoSell.State.SENDING_COMMAND;
      }

      this.GM = var10001;
      this.qp = false;
      NoPushBridge.install(this, () -> this.isActive() && (Boolean)this.dN.get() && !this.qp);
      this.hy();
      this.hP("Auto Sell enabled.");
   }

   @Override
   public void onDeactivate() {
      this.qp = false;
      NoPushBridge.uninstall(this);
      this.hs();
      this.GM = AutoSell.State.IDLE;
      this.fW = null;
      this.ff = null;
      this.IN = "";
      this.jL = 0;
      this.hM();
      this.hP("Auto Sell disabled.");
      super.onDeactivate();
   }

   public String getInfoString() {
      return this.hT();
   }

   @AliceEventHandler
   public void gX(Post var1) {
      if (this.isActive() && this.mc.player != null && this.mc.world != null && this.mc.getNetworkHandler() != null) {
         this.hp();
         this.hy();
         this.hA();
         if (!this.mc.player.isDead()) {
            AliceAutoReconnect var2;
            boolean var10000;
            label93: {
               var2 = this.hB();
               if (var2 != null) {
                  if (var2.ab()) {
                     var10000 = true;
                     break label93;
                  }
               } else {
                  char var5 = '\uddd1';
               }

               var10000 = false;
            }

            boolean var3 = var10000;
            if (this.X && var3) {
               this.gq = true;
            }

            boolean var4 = this.hm(var2);
            if (this.mc.getNetworkHandler() != null) {
               if (var3 && !this.gs) {
                  this.gs = true;
                  this.hr();
               } else if (!var3 && this.gs) {
                  this.gs = false;
                  this.lo = System.currentTimeMillis() + 250L;
               }

               this.hE();
               if (!var4 && !var3 && !this.hq()) {
                  if (!this.ht(var2)) {
                     if (!this.hh()) {
                        if (System.currentTimeMillis() >= this.lo) {
                           int var6 = this.GM.ordinal();
                           if (var6 != 0) {
                              if (var6 == 1) {
                                 this.hc();
                              } else if (var6 != 2) {
                                 if (var6 != 3) {
                                    if (var6 != 4) {
                                       if (var6 == 5) {
                                          this.hg();
                                       } else if (var6 == 6) {
                                          this.GM = AutoSell.State.SENDING_COMMAND;
                                       }
                                    } else {
                                       this.hf();
                                    }
                                 } else {
                                    this.he();
                                 }
                              } else {
                                 this.hd();
                              }
                           } else {
                              this.GM = AutoSell.State.SENDING_COMMAND;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @AliceEventHandler
   public void gY(ReceiveMessageEvent var1) {
      if (this.isActive()) {
         this.hH(var1.getMessage().getString());
      }
   }

   @AliceEventHandler
   public void gZ(GameLeftEvent var1) {
      if (this.isActive()) {
         this.hs();
         this.jm = false;
         this.qp = false;
         if (this.hg) {
            this.pW = false;
         } else {
            this.pW = this.hx() && this.fW != null;
         }

         this.hg = false;
         this.X = false;
         this.GM = AutoSell.State.IDLE;
         this.hO();
      }
   }

   @AliceEventHandler
   public void ha(GameJoinedEvent var1) {
      if (this.isActive()) {
         boolean var2 = this.GV;
         this.GV = false;
         this.hg = false;
         this.hy();
         this.hO();
         this.jL = 0;
         this.pi = 0;
         this.lo = System.currentTimeMillis() + 1000L;
         this.X = this.hx() && (this.fW == null || this.pW || var2);
         this.gq = false;
         this.AF = 0;
         this.pW = false;
         this.GM = !this.X ? AutoSell.State.SENDING_COMMAND : AutoSell.State.IDLE;
      }
   }

   @AliceEventHandler
   public void hb(Render2DEvent var1) {
      if (this.isActive() && (Boolean)this.nh.get() && this.mc.player != null) {
         TextRenderer var2 = TextRenderer.get();
         if (var2 != null) {
            ArrayList var3 = new ArrayList();
            ArrayList var4 = new ArrayList();
            var3.add(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Uptime: ">(hW(this.hU())));
            var4.add(Hu);
            var3.add(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Money: ">(hX(this.AB)));
            var4.add(em);
            var3.add(this.Ef + "");
            var4.add(Hu);
            var3.add(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","Average: ","/h">(hX(this.hV())));
            var4.add(em);
            var3.add(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","State: ">(this.hT()));
            var4.add(Hy);
            var3.add(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","AutoReconnect: ">(this.hC()));
            var4.add(Hu);
            if ((Boolean)this.wz.get()) {
               var3.add(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Player Detection: ">(this.hD()));
               var4.add(Hy);
            }

            double var5 = 0.0;

            for (String var8 : var3) {
               var5 = Math.max(var5, var2.getWidth(var8, false));
            }

            double var24 = var2.getHeight(false);
            double var9 = 4.0;
            double var11 = 2.0;
            double var13 = var5 + var9 * 2.0;
            double var15 = var3.size() * var24 + (var3.size() - 1) * var11 + var9 * 2.0;
            double var17 = this.mc.getWindow().getScaledWidth() / 2.0 - var13 - 10.0;
            double var19 = this.mc.getWindow().getScaledHeight() / 2.0 - var15 - 10.0;
            Renderer2D.COLOR.begin();
            Renderer2D.COLOR.quad(var17, var19, var13, var15, new Color(20, 20, 20, 180));
            Renderer2D.COLOR.quad(var17, var19, var13, 2.0, new Color(85, 255, 170, 255));
            this.ia(var1);
            var2.begin(1.0, false, true);
            double var21 = var19 + var9 + 3.0;

            for (int var23 = 0; var23 < var3.size(); var23++) {
               var2.render((String)var3.get(var23), var17 + var9, var21, (Color)var4.get(var23), true);
               var21 += var24 + var11;
            }

            var2.end();
         }
      }
   }

   private void hc() {
      GenericContainerScreen var1 = this.hI();
      this.mT = var1 == null ? -1 : ((GenericContainerScreenHandler)var1.getScreenHandler()).syncId;
      this.mc.player.networkHandler.sendChatCommand("sell");
      this.kT = System.currentTimeMillis() + 5000L;
      this.GM = AutoSell.State.WAITING_GUI;
   }

   private void hd() {
      GenericContainerScreen var1 = this.hI();
      if (var1 != null && ((GenericContainerScreenHandler)var1.getScreenHandler()).syncId != this.mT) {
         this.pi = 0;
         this.hR(0);
         this.GM = AutoSell.State.MOVING_ITEMS;
      } else if (System.currentTimeMillis() >= this.kT) {
         this.pi++;
         if (this.pi < 3) {
            this.GM = AutoSell.State.SENDING_COMMAND;
         } else {
            this.pi = 0;
            this.GM = AutoSell.State.COOLDOWN;
            this.lo = System.currentTimeMillis() + 10000L;
            this.hP("Failed to open the sell GUI after 3 retries.");
         }
      }
   }

   private void he() {
      GenericContainerScreen var1 = this.hI();
      if (var1 == null) {
         this.GM = AutoSell.State.SENDING_COMMAND;
      } else if (this.mc.interactionManager != null) {
         ScreenHandler var2 = var1.getScreenHandler();
         if (var2.slots.size() < 36) {
            this.GM = AutoSell.State.SENDING_COMMAND;
         } else {
            int var10000 = this.hJ(var2) != 0 ? '쥻' : '쥺';

            while (true) {
               int var9 = var10000;
               if (var9 == 51578) {
                  this.GM = AutoSell.State.CLICKING_SELL;
                  return;
               }

               if (var9 != 51579) {
                  int var3 = var2.slots.size() - 36;
                  int var4 = 0;
                  int var5 = this.hJ(var2);

                  for (int var6 = var3; var6 < var2.slots.size() && var4 < var5; var6++) {
                     ItemStack var7 = var2.getSlot(var6).getStack();
                     if (!var7.isEmpty() && !this.hL(var7)) {
                        int var8 = var7.getCount();
                        this.mc.interactionManager.clickSlot(var2.syncId, var6, 0, SlotActionType.QUICK_MOVE, this.mc.player);
                        this.jL += var8;
                        var4++;
                     }
                  }

                  if (var4 > 0) {
                     this.hR((Integer)this.vF.get());
                     return;
                  }

                  if (this.jL > 0) {
                     this.GM = AutoSell.State.CLICKING_SELL;
                  } else {
                     this.hM();
                     this.GM = AutoSell.State.COOLDOWN;
                     this.lo = System.currentTimeMillis() + 1000L;
                  }

                  return;
               }

               var10000 = 51577;
            }
         }
      }
   }

   private void hf() {
      GenericContainerScreen var1 = this.hI();
      if (var1 == null) {
         this.GM = AutoSell.State.SENDING_COMMAND;
      } else if (this.mc.interactionManager != null) {
         ScreenHandler var2 = var1.getScreenHandler();
         if (this.hJ(var2) > 0 && this.hK(var2)) {
            this.GM = AutoSell.State.MOVING_ITEMS;
         } else if (var2.slots.size() > 53 && this.jL > 0) {
            int var3 = this.jL;
            this.mc.interactionManager.clickSlot(var2.syncId, 53, 0, SlotActionType.PICKUP, this.mc.player);
            this.Ef += var3;
            this.jL = 0;
            this.hR((Integer)this.pz.get());
            this.GM = AutoSell.State.WAITING_AFTER_SELL;
         } else {
            this.hM();
            this.GM = AutoSell.State.COOLDOWN;
            this.lo = System.currentTimeMillis() + 1000L;
         }
      }
   }

   private void hg() {
      GenericContainerScreen var1 = this.hI();
      if (var1 != null && this.mc.interactionManager != null) {
         this.mc.interactionManager.clickSlot(((GenericContainerScreenHandler)var1.getScreenHandler()).syncId, -999, 0, SlotActionType.PICKUP, this.mc.player);
         this.GM = AutoSell.State.MOVING_ITEMS;
      } else {
         this.GM = AutoSell.State.SENDING_COMMAND;
      }
   }

   private boolean hh() {
      if (this.fW == null || this.mc.player == null) {
         return false;
      } else if ((Boolean)this.hh.get()) {
         int var7;
         label91: {
            if (this.GM != AutoSell.State.RETURNING) {
               var7 = !this.jm ? '\ue44c' : '\ue44d';

               while (true) {
                  int var4 = var7;
                  if (var4 == 58444) {
                     if (!(Boolean)this.eo.get()) {
                        var7 = 0;
                        break label91;
                     }
                     break;
                  }

                  if (var4 != 58445) {
                     break;
                  }

                  var7 = 58443;
               }
            }

            var7 = 1;
         }

         boolean var1 = (boolean)var7;
         if (var1) {
            if (!this.hv()) {
               if (!this.xd) {
                  this.xd = true;
                  this.hr();
                  this.hP("Waiting for the saved server and dimension before returning.");
               }

               return true;
            }

            short var9;
            while (var9 == 5947) {
               var9 = 5945;
            }
         }

         this.xd = false;
         double var2 = this.mc.player.getEntityPos().distanceTo(this.fW);
         if (!(var2 <= 0.5)) {
            if (this.GM == AutoSell.State.RETURNING || this.jm || (Boolean)this.eo.get() && var2 > 1.0) {
               if (this.GM != AutoSell.State.RETURNING) {
                  this.hi("Returning to the saved position.");
               }

               this.hj(var2);
               return true;
            } else {
               return false;
            }
         } else {
            if (this.jm || this.GM == AutoSell.State.RETURNING) {
               this.hl(true);
            }

            return false;
         }
      } else {
         if (this.GM == AutoSell.State.RETURNING || this.jm) {
            this.hl(false);
         }

         this.xd = false;
         return false;
      }
   }

   private void hi(String var1) {
      this.qp = true;
      this.hM();
      this.jL = 0;
      this.GM = AutoSell.State.RETURNING;
      this.wA = 0L;
      if (!this.jm) {
         this.jm = true;
         this.Ix = false;
         this.hP(var1);
      }

      this.hk();
   }

   private void hj(double var1) {
      if ((Boolean)this.hh.get() && this.fW != null && this.mc.player != null) {
         int var10000 = !(var1 <= 0.5) ? 2957 : 2956;

         while (true) {
            int var14 = var10000;
            if (var14 == 2956) {
               this.hl(true);
               return;
            }

            if (var14 != 2957) {
               if (this.sq) {
                  if (this.fD.gL()) {
                     return;
                  }

                  this.sq = this.fD.gJ(BlockPos.ofFloored(this.fW));
                  if (this.sq) {
                     return;
                  }
               }

               if (!this.Ix) {
                  this.Ix = true;
                  this.hQ("Baritone is unavailable. Please install a compatible Baritone build; using fallback movement.");
               }

               long var3 = System.currentTimeMillis();
               if (var3 < this.wA) {
                  return;
               }

               this.wA = var3 + 200L;
               Vec3d var5 = this.mc.player.getEntityPos();
               double var6 = this.fW.x - var5.x;
               double var8 = this.fW.z - var5.z;
               double var10 = Math.hypot(var6, var8);
               if (var10 < 0.001) {
                  return;
               }

               double var12 = 0.2873;
               this.mc.player.setVelocity(var6 / var10 * var12, this.mc.player.getVelocity().y, var8 / var10 * var12);
               this.mc.player.setYaw((float)Math.toDegrees(Math.atan2(-var6, var8)));
               return;
            }

            var10000 = 2955;
         }
      } else {
         this.hl(false);
      }
   }

   private void hk() {
      if (this.fW != null && !this.sq) {
         this.sq = this.fD.gJ(BlockPos.ofFloored(this.fW));
      }
   }

   private void hl(boolean var1) {
      this.hs();
      boolean var2 = this.jm || this.GM == AutoSell.State.RETURNING;
      this.jm = false;
      this.qp = false;
      this.Ix = false;
      this.wA = 0L;
      this.GM = AutoSell.State.SENDING_COMMAND;
      this.lo = System.currentTimeMillis() + 250L;
      if (var1 && var2) {
         this.hP("Returned to the saved position.");
      }
   }

   private boolean hm(AliceAutoReconnect var1) {
      if (this.mc.player == null || this.mc.world == null) {
         return false;
      } else if (!(Boolean)this.wz.get()) {
         this.ho();
         this.k.clear();
         return false;
      } else {
         HashSet var2 = new HashSet();
         ArrayList var3 = new ArrayList();

         for (PlayerEntity var5 : this.mc.world.getPlayers()) {
            if (var5 != this.mc.player && !var5.getUuid().equals(this.mc.player.getUuid()) && !var5.isDead()) {
               var2.add(var5.getUuid());
               String var6 = var5.getName().getString();
               if (!var6.isBlank() && !hS((List<String>)this.Jt.get(), var6) && !this.k.contains(var5.getUuid())) {
                  var3.add(var5);
               }
            }
         }

         this.k.retainAll(var2);
         if (var3.isEmpty()) {
            if (this.hN || this.jz) {
               this.hQ("Detected player left the loaded world. Auto Sell resumed.");
               this.ho();
            }

            return false;
         } else {
            PlayerEntity var7 = (PlayerEntity)var3.getFirst();
            if (!this.hN && !this.jz) {
               this.hN = true;
               this.aR = 0;
               this.sG = var7.getName().getString();
               this.Fb = this.mc.player.getEntityPos().distanceTo(var7.getEntityPos());
               this.hr();
               this.hQ(String.format(Locale.ROOT, "Player detected: %s (%.1f blocks). Confirming for 80 ticks.", this.sG, this.Fb));
               if ((Boolean)this.he.get() && (Boolean)this.ec.get()) {
                  this.hF(this.sG, this.Fb);
               }
            }

            this.hr();
            if (this.jz) {
               return true;
            } else {
               this.aR++;
               if (this.aR < 80) {
                  return true;
               } else if (var1 != null && !var1.isIdle()) {
                  for (PlayerEntity var9 : var3) {
                     this.k.add(var9.getUuid());
                  }

                  this.hQ("Player alert marked as a reconnect/hub false positive.");
                  this.ho();
                  return true;
               } else {
                  this.hN = false;
                  this.jz = true;
                  if (!(Boolean)this.fr.get()) {
                     this.hQ("Player confirmed, but Disconnect On Player is disabled.");
                  } else {
                     this.hn(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Confirmed player: ">(this.sG));
                  }

                  return true;
               }
            }
         }
      }
   }

   private void hn(String var1) {
      if (!this.hg && this.mc.getNetworkHandler() != null) {
         this.hr();
         this.hz();
         this.GV = true;
         this.hg = true;
         this.hQ(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001\u0002",". Reconnect modules disabled; disconnecting now.">(var1));
         this.mc.getNetworkHandler().getConnection().disconnect(Text.literal(var1));
      }
   }

   private void ho() {
      this.hN = false;
      this.jz = false;
      this.aR = 0;
      this.sG = "";
      this.Fb = 0.0;
   }

   private void hp() {
      boolean var1 = this.mc.getWindow() != null && GLFW.glfwGetKey(this.mc.getWindow().getHandle(), 256) == 1;
      if (var1 && !this.iR) {
         this.IA = System.currentTimeMillis() + 4000L;
         this.hr();
         this.hQ("Paused for 4 seconds because ESC was pressed.");
      }

      this.iR = var1;
   }

   private boolean hq() {
      return System.currentTimeMillis() < this.IA;
   }

   private void hr() {
      this.hM();
      this.jL = 0;
      this.hs();
      if (this.GM != AutoSell.State.IDLE) {
         this.GM = AutoSell.State.SENDING_COMMAND;
      }
   }

   private void hs() {
      if (this.sq) {
         this.fD.gK();
      }

      this.sq = false;
   }

   private boolean ht(AliceAutoReconnect var1) {
      if (this.X) {
         if (var1 != null && !var1.isIdle()) {
            this.gq = true;
            return true;
         } else {
            this.AF++;
            if (!this.gq && this.AF < 80) {
               return true;
            } else {
               this.X = false;
               this.gq = false;
               this.AF = 0;
               if (this.fW == null) {
                  this.hu();
                  this.GM = AutoSell.State.SENDING_COMMAND;
               } else {
                  this.hi("Reconnect flow finished. Returning to the saved position.");
               }

               return false;
            }
         }
      } else {
         return false;
      }
   }

   private void hu() {
      if (this.mc.player != null && this.mc.world != null) {
         this.fW = this.mc.player.getEntityPos();
         this.ff = this.mc.world.getRegistryKey();
         this.IN = this.hw();
      } else {
         this.fW = null;
         this.ff = null;
         this.IN = "";
      }
   }

   private boolean hv() {
      return this.mc.world != null && this.ff != null && this.ff.equals(this.mc.world.getRegistryKey()) && this.IN.equalsIgnoreCase(this.hw());
   }

   private String hw() {
      return this.mc.getCurrentServerEntry() == null ? "singleplayer" : this.mc.getCurrentServerEntry().address;
   }

   private boolean hx() {
      return (Boolean)this.hh.get() && (Boolean)this.uh.get();
   }

   private void hy() {
      if (this.isActive() && this.hx() && !this.GV) {
         Module var1 = Modules.get().get(AutoReconnect.class);
         if (var1 != null && !var1.isActive()) {
            var1.toggle();
         }

         AliceAutoReconnect var2 = this.hB();
         if (var2 != null && !var2.isActive()) {
            var2.toggle();
         }
      }
   }

   private void hz() {
      Module var1 = Modules.get().get(AutoReconnect.class);
      if (var1 != null && var1.isActive()) {
         var1.toggle();
      }

      AliceAutoReconnect var2 = this.hB();
      if (var2 != null && var2.isActive()) {
         var2.toggle();
      }
   }

   private void hA() {
      if (this.hx() && !this.GV && this.mc.player != null) {
         AliceAutoReconnect var1 = this.hB();
         if (var1 != null && !var1.hasLoginPassword()) {
            long var2 = System.currentTimeMillis();
            if (var2 >= this.mG) {
               this.mG = var2 + 500L;
               this.hQ("Please enter the login password in Auto Login & Antibot & Auto Join.");
            }
         } else {
            this.mG = 0L;
         }
      }
   }

   private AliceAutoReconnect hB() {
      return (AliceAutoReconnect)Modules.get().get(AliceAutoReconnect.class);
   }

   private String hC() {
      Module var1 = Modules.get().get(AutoReconnect.class);
      String var2 = var1 != null && var1.isActive() ? "ON" : "OFF";
      AliceAutoReconnect var3 = this.hB();
      String var4 = var3 == null ? "MISSING" : var3.getStateLabel();
      return var2 + var4;
   }

   private String hD() {
      if (!this.jz) {
         if (this.hN) {
            String var1 = this.sG.isBlank() ? "" : StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001"," ">(this.sG);
            return this.aR + var1;
         } else {
            return !this.k.isEmpty() ? this.k.size() + "" : "Monitoring";
         }
      } else {
         return this.sG.isBlank() ? "Confirmed" : StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Confirmed ">(this.sG);
      }
   }

   private void hE() {
      if ((Boolean)this.he.get() && !((String)this.Dd.get()).isBlank() && this.mc.player != null) {
         long var1 = Instant.now().getEpochSecond();
         if (var1 - this.nL >= ((Integer)this.vH.get()).intValue()) {
            this.nL = var1;
            String var3 = hY("Uptime", hW(this.hU()))
               + hY("Money", hX(this.AB))
               + hY("Total Sell", Integer.toString(this.Ef))
               + hY("Average", StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001\u0002","/h">(hX(this.hV())));
            this.hG(var3);
         }
      }
   }

   private void hF(String var1, double var2) {
      String var4 = hY("Player", var1) + hY("Distance", String.format(Locale.ROOT, "%.1f blocks", var2));
      this.hG(var4);
   }

   private void hG(String var1) {
      String var2 = ((String)this.Dd.get()).trim();
      if ((Boolean)this.he.get() && !var2.isBlank()) {
         Thread.ofVirtual()
            .name("session-status-webhook")
            .start(
               () -> {
                  HttpRequest var3 = HttpRequest.newBuilder()
                     .uri(URI.create(var2))
                     .header("Content-Type", "application/json")
                     .POST(BodyPublishers.ofString(var1))
                     .build();
                  this.kr.send(var3, BodyHandlers.discarding());
               }
            );
      }
   }

   private void hH(String var1) {
      if (var1 != null && !var1.isBlank()) {
         Matcher var2 = ft.matcher(var1);
         if (var2.find()) {
            double var3 = Double.parseDouble(var2.group(1));
            String var5 = var2.group(2).toUpperCase(Locale.ROOT);
            byte var6 = -1;
            switch (var5.hashCode()) {
               case 66:
                  if (var5.equals("B")) {
                     var6 = 2;
                  }
                  break;
               case 75:
                  if (var5.equals("K")) {
                     var6 = 0;
                  }
                  break;
               case 77:
                  if (var5.equals("M")) {
                     var6 = 1;
                  }
            }

            switch (var6 ^ -2136237045) {
               case -2136237047:
                  var3 *= 1.0E9;
                  break;
               case -2136237046:
                  var3 *= 1000000.0;
                  break;
               case -2136237045:
                  var3 *= 1000.0;
            }

            this.AB += var3;
         }
      }
   }

   private GenericContainerScreen hI() {
      return this.mc.currentScreen instanceof GenericContainerScreen var1 ? var1 : null;
   }

   private int hJ(ScreenHandler var1) {
      int var2 = Math.min(45, var1.slots.size());
      int var3 = 0;

      for (int var4 = 0; var4 < var2; var4++) {
         if (var1.getSlot(var4).getStack().isEmpty()) {
            var3++;
         }
      }

      return var3;
   }

   private boolean hK(ScreenHandler var1) {
      int var2 = Math.max(0, var1.slots.size() - 36);

      for (int var3 = var2; var3 < var1.slots.size(); var3++) {
         ItemStack var4 = var1.getSlot(var3).getStack();
         if (!var4.isEmpty() && !this.hL(var4)) {
            return true;
         }
      }

      return false;
   }

   private boolean hL(ItemStack var1) {
      boolean var2 = ((List)this.uM.get()).contains(var1.getItem());
      return !AutoSellFilterHelper.iC((AutoSell.FilterMode)this.aO.get(), var2);
   }

   private void hM() {
      if (this.mc.player != null && this.mc.currentScreen instanceof GenericContainerScreen) {
         this.mc.player.closeHandledScreen();
      }
   }

   private void hN() {
      this.Ef = 0;
      this.AB = 0.0;
      this.oT = System.currentTimeMillis();
      this.nL = Instant.now().getEpochSecond();
      this.jL = 0;
      this.pi = 0;
      this.lo = 0L;
   }

   private void hO() {
      this.ho();
      this.k.clear();
   }

   private void hP(String var1) {
      if ((Boolean)this.pX.get() && this.mc.player != null) {
         this.mc
            .player
            .sendMessage(Text.literal(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","[ALICE Auto Sell] ">(var1)), false);
      }
   }

   private void hQ(String var1) {
      if (this.mc.player != null) {
         this.mc
            .player
            .sendMessage(Text.literal(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","[ALICE Auto Sell] ">(var1)), false);
      }
   }

   private void hR(int var1) {
      this.lo = System.currentTimeMillis() + var1 * 50L;
   }

   private static boolean hS(List<String> var0, String var1) {
      if (var0 != null && var1 != null) {
         for (String var3 : var0) {
            if (var3 != null && var3.trim().equalsIgnoreCase(var1)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private String hT() {
      if (this.jz) {
         return "Player Confirmed";
      } else if (this.hN) {
         return this.aR + "";
      } else if (!this.gs) {
         if (this.X) {
            return "Waiting Reconnect";
         } else if (this.hq()) {
            double var1 = Math.max(0L, this.IA - System.currentTimeMillis()) / 1000.0;
            return String.format(Locale.ROOT, "ESC Paused %.1fs", var1);
         } else if (!this.xd) {
            if (this.GM == AutoSell.State.RETURNING) {
               return !this.sq ? "Returning (Fallback)" : "Returning (Baritone)";
            } else {
               int var3 = this.GM.ordinal();
               String var10000;
               if (var3 == 0) {
                  var10000 = "Idle";
               } else if (var3 == 1) {
                  var10000 = "Sending Command";
               } else if (var3 == 2) {
                  var10000 = "Waiting GUI";
               } else if (var3 != 3) {
                  if (var3 == 4) {
                     var10000 = "Selling";
                  } else if (var3 == 5) {
                     var10000 = "Waiting";
                  } else if (var3 != 6) {
                     if (var3 != 7) {
                        throw new MatchException(null, null);
                     }

                     var10000 = "Returning";
                  } else {
                     var10000 = "Cooldown";
                  }
               } else {
                  var10000 = "Moving Items";
               }

               return var10000;
            }
         } else {
            return "Waiting Anchor World";
         }
      } else {
         return "AutoReconnect";
      }
   }

   private long hU() {
      return this.oT == 0L ? 0L : System.currentTimeMillis() - this.oT;
   }

   private double hV() {
      double var1 = this.hU() / 3600000.0;
      return var1 < 0.001 ? 0.0 : this.AB / var1;
   }

   private static String hW(long var0) {
      long var2 = var0 / 1000L;
      return String.format(Locale.ROOT, "%02d:%02d:%02d", var2 / 3600L, var2 % 3600L / 60L, var2 % 60L);
   }

   private static String hX(double var0) {
      if (var0 >= 1.0E9) {
         return String.format(Locale.ROOT, "$%.2fB", var0 / 1.0E9);
      } else if (!(var0 >= 1000000.0)) {
         return !(var0 >= 1000.0) ? String.format(Locale.ROOT, "$%.0f", var0) : String.format(Locale.ROOT, "$%.2fK", var0 / 1000.0);
      } else {
         return String.format(Locale.ROOT, "$%.2fM", var0 / 1000000.0);
      }
   }

   private static String hY(String var0, String var1) {
      return hZ(var0) + hZ(var1);
   }

   private static String hZ(String var0) {
      return var0.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
   }

   private void ia(Render2DEvent var1) {
      if (!fz) {
         fz = true;

         for (Method var5 : Renderer2D.COLOR.getClass().getMethods()) {
            if (var5.getName().equals("render") && (var5.getParameterCount() == 0 || var5.getParameterCount() == 1)) {
               Ep = var5;
               break;
            }
         }
      }

      if (Ep != null) {
         if (Ep.getParameterCount() == 0) {
            Ep.invoke(Renderer2D.COLOR);
         } else {
            Class var6 = Ep.getParameterTypes()[0];
            Ep.invoke(Renderer2D.COLOR, var6.getName().contains("DrawContext") ? var1.drawContext : var1.drawContext.getMatrices());
         }
      }
   }

   private static final class BaritoneAdapter {
      private boolean eK;
      private boolean yI;
      private Object nM;
      private Object Dz;
      private Constructor<?> bn;
      private boolean zK;
      private Method Ak;
      private Method Ht;
      private Method mg;

      boolean gJ(BlockPos var1) {
         int var10000 = !this.initialize() ? '\ue1a1' : '\ue1a0';

         while (true) {
            switch (var10000) {
               case 57760:
                  if (var1 != null) {
                     char var3 = '슥';
                     Object var2 = this.zK ? this.bn.newInstance(var1) : this.bn.newInstance(var1.getX(), var1.getY(), var1.getZ());
                     this.Ak.invoke(this.nM, var2);
                     return true;
                  }

                  return false;
               case 57761:
                  var10000 = 57759;
                  break;
               default:
                  return false;
            }
         }
      }

      void gK() {
         if (this.eK && this.yI && this.Ht != null && this.Dz != null) {
            this.Ht.invoke(this.Dz);
         }
      }

      boolean gL() {
         if (this.eK && this.yI && this.mg != null && this.Dz != null) {
            try {
               return Boolean.TRUE.equals(this.mg.invoke(this.Dz));
            } catch (RuntimeException | ReflectiveOperationException var2) {
               return false;
            }
         } else {
            return false;
         }
      }

      private boolean initialize() {
         if (this.eK) {
            return this.yI;
         } else {
            this.eK = true;
            ClassLoader var1 = AutoSell.class.getClassLoader();
            Class var2 = Class.forName("baritone.api.BaritoneAPI", false, var1);
            Object var3 = var2.getMethod("getProvider").invoke(null);
            Object var4 = gM(var3.getClass(), "getPrimaryBaritone", 0).invoke(var3);
            if (var4 == null) {
               return false;
            } else {
               this.nM = gM(var4.getClass(), "getCustomGoalProcess", 0).invoke(var4);
               this.Dz = gM(var4.getClass(), "getPathingBehavior", 0).invoke(var4);
               int var10000 = this.nM != null ? 'ﺔ' : 'ﺕ';

               while (true) {
                  int var11 = var10000;
                  if (var11 == 65172) {
                     if (this.Dz != null) {
                        Class var5 = Class.forName("baritone.api.pathing.goals.GoalBlock", false, var1);

                        for (Constructor var9 : var5.getConstructors()) {
                           Class[] var10 = var9.getParameterTypes();
                           if (var10.length == 1 && var10[0].isAssignableFrom(BlockPos.class)) {
                              this.bn = var9;
                              this.zK = true;
                              break;
                           }

                           if (var10.length == 3 && var10[0] == int.class && var10[1] == int.class && var10[2] == int.class) {
                              this.bn = var9;
                              this.zK = false;
                           }
                        }

                        if (this.bn == null) {
                           return false;
                        }

                        this.Ak = gM(this.nM.getClass(), "setGoalAndPath", 1);
                        this.Ht = gN(this.Dz.getClass(), "cancelEverything", 0);
                        this.mg = gN(this.Dz.getClass(), "isPathing", 0);
                        this.yI = this.Ak != null;
                        return this.yI;
                     }
                     break;
                  }

                  if (var11 != 65173) {
                     break;
                  }

                  var10000 = 65171;
               }

               return false;
            }
         }
      }

      private static Method gM(Class<?> var0, String var1, int var2) throws NoSuchMethodException {
         Method var3 = gN(var0, var1, var2);
         if (var3 == null) {
            throw new NoSuchMethodException(var0.getName() + var1);
         } else {
            return var3;
         }
      }

      private static Method gN(Class<?> var0, String var1, int var2) {
         for (Method var6 : var0.getMethods()) {
            if (var6.getName().equals(var1) && var6.getParameterCount() == var2) {
               return var6;
            }
         }

         return null;
      }
   }

   public static enum FilterMode {
      Whitelist,
      Blacklist;
   }

   private static enum State {
      IDLE,
      SENDING_COMMAND,
      WAITING_GUI,
      MOVING_ITEMS,
      CLICKING_SELL,
      WAITING_AFTER_SELL,
      COOLDOWN,
      RETURNING;
   }
}
