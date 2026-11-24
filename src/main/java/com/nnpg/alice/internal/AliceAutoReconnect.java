package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.lang.invoke.StringConcatFactory;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AutoReconnect;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class AliceAutoReconnect extends AliceModule {
   private static final long jI = 8000L;
   private static final long GP = 20000L;
   private static final long gN = 1500L;
   private static final long gH = 2000L;
   private static final long IK = 180000L;
   private final MinecraftClient FC = MinecraftClient.getInstance();
   private final SettingGroup km = this.settings.getDefaultGroup();
   private final Setting<String> Is = this.km
      .add(((Builder)((Builder)((Builder)new Builder().name("login-password")).description("mat khau cho lenh /l")).defaultValue("")).build());
   private final Setting<Integer> wm = this.km
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("click-delay-ticks"))
                  .description("thoi gian tre giua moi lenh va click"))
               .defaultValue(15))
            .min(15)
            .max(40)
            .build()
      );
   private final Setting<Boolean> Bj = this.km
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("notifications"))
                  .description("hien thi thong bao"))
               .defaultValue(true))
            .build()
      );
   private AliceAutoReconnect.State AL = AliceAutoReconnect.State.IDLE;
   private boolean ix = false;
   private boolean Dg = false;
   private boolean BN = false;
   private long xk = 0L;
   private long IH = 0L;
   private long iJ = 0L;
   private long eF = 0L;
   private long iE = 0L;
   private long wr = 0L;
   private static boolean ph = false;

   public AliceAutoReconnect() {
      super(
         AliceAddon.CATEGORY, "Auto Login & Antibot & Auto Join", "Nhan dien yeu cau dang nhap, tu dong dang nhap, xu ly kiem tra va khoi phuc SpawnerOrder."
      );
   }

   public boolean ab() {
      return this.isActive() && this.AL != AliceAutoReconnect.State.IDLE;
   }

   public boolean isIdle() {
      return !this.isActive() || this.AL == AliceAutoReconnect.State.IDLE;
   }

   public boolean hasLoginPassword() {
      return this.Is.get() != null && !((String)this.Is.get()).trim().isEmpty();
   }

   public String getStateLabel() {
      if (!this.isActive()) {
         return "OFF";
      } else if (this.AL != AliceAutoReconnect.State.WAITING_LOGIN_SUCCESS) {
         if (this.AL == AliceAutoReconnect.State.WAITING_TO_RESTORE_SPAWNER && this.Dg) {
            long var1 = Math.max(0L, this.iE - System.currentTimeMillis());
            return StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","RESTORE ">(
               String.format(Locale.US, "%.1fs", var1 / 1000.0)
            );
         } else {
            return this.AL.name().replace("_", " ");
         }
      } else {
         return "WAITING LOGIN";
      }
   }

   @Override
   public void onActivate() {
      super.onActivate();
      this.aw();
      Module var1 = Modules.get().get(AutoReconnect.class);
      if (var1 != null && !var1.isActive()) {
         var1.toggle();
      }
   }

   @Override
   public void onDeactivate() {
      super.onDeactivate();
      this.ap(true);
      this.aw();
   }

   @AliceEventHandler
   public void ac(ReceiveMessageEvent var1) {
      if (this.isActive()) {
         String var2 = this.ax(var1.getMessage().getString()).toLowerCase(Locale.ROOT);
         if (this.as(var2)) {
            this.am(var2);
         } else if (this.AL == AliceAutoReconnect.State.ANTIBOT_VERIFY && var2.contains("xac minh thanh cong")) {
            this.ix = true;
            this.AL = AliceAutoReconnect.State.WAIT_AFTER_VERIFY;
            this.xk = System.currentTimeMillis();
            this.ay("[AutoLogin] Xác minh antibot thành công.");
         } else if (this.AL == AliceAutoReconnect.State.WAITING_LOGIN_SUCCESS && var2.contains("/dn")) {
            if (this.au() && System.currentTimeMillis() - this.IH >= Math.max(1500L, this.getActionDelayMs())) {
               this.an();
               this.ay("[AutoLogin] /dn xuất hiện lại, đã gửi lại /l.");
            }
         } else if (this.AL == AliceAutoReconnect.State.IDLE) {
            if (var2.contains("dang nhap bang lenh") && var2.contains("/dn")) {
               this.ae();
            }
         }
      }
   }

   @AliceEventHandler
   public void ad(Pre var1) {
      if (this.isActive()) {
         if (this.AL != AliceAutoReconnect.State.IDLE && this.wr > 0L && System.currentTimeMillis() - this.wr >= 180000L) {
            this.az("[AutoLogin] Flow timeout (3 min)! Forcing restore and returning to IDLE.");
            if (this.FC.currentScreen != null && this.FC.player != null) {
               this.FC.player.closeHandledScreen();
            }

            this.ap(true);
            this.aw();
         } else {
            switch (this.AL) {
               case IDLE:
                  this.ap(false);
                  break;
               case WAITING_TO_SEND_LOGIN:
                  this.af();
               case WAITING_LOGIN_SUCCESS:
               default:
                  break;
               case SENDING_MENU:
                  this.ag();
                  break;
               case WAIT_FOR_MENU_GUI:
                  this.ah();
                  break;
               case ANTIBOT_VERIFY:
                  this.ai();
                  break;
               case WAIT_AFTER_VERIFY:
                  this.aj();
                  break;
               case CLICKING_FINAL_SLOT:
                  this.ak();
                  break;
               case WAITING_TO_RESTORE_SPAWNER:
                  this.al();
            }
         }
      }
   }

   private void ae() {
      this.ix = false;
      this.eF = 0L;
      this.wr = System.currentTimeMillis();
      this.ao();
      this.AL = AliceAutoReconnect.State.WAITING_TO_SEND_LOGIN;
      this.xk = System.currentTimeMillis();
      this.ay("[AutoLogin] Ðã phát hiện /dn. Ðang chờ gửi /l.");
   }

   private void af() {
      if (this.au()) {
         if (System.currentTimeMillis() - this.xk >= this.getActionDelayMs()) {
            this.an();
            this.AL = AliceAutoReconnect.State.WAITING_LOGIN_SUCCESS;
            this.xk = this.IH;
            this.ay("[AutoLogin] Ðã gửi /l sau khi vào game.");
         }
      }
   }

   private void ag() {
      if (this.au()) {
         if (System.currentTimeMillis() - this.xk >= 2500L) {
            ChatUtils.sendPlayerMsg("/menu");
            this.ix = false;
            this.iJ = System.currentTimeMillis();
            this.eF = 0L;
            this.AL = AliceAutoReconnect.State.WAIT_FOR_MENU_GUI;
            this.xk = this.iJ;
            this.ay("[AutoLogin] Ðã gửi /menu.");
         }
      }
   }

   private void ah() {
      if (!(this.FC.currentScreen instanceof GenericContainerScreen var1)) {
         if (System.currentTimeMillis() - this.iJ >= 8000L) {
            this.AL = AliceAutoReconnect.State.SENDING_MENU;
            this.xk = System.currentTimeMillis();
         }
      } else {
         if (this.aq(var1)) {
            this.AL = AliceAutoReconnect.State.ANTIBOT_VERIFY;
            this.xk = System.currentTimeMillis();
            this.eF = 0L;
            this.ay("[AutoLogin] Phát hiện antibot, bắt đầu xác minh.");
         } else {
            this.AL = AliceAutoReconnect.State.CLICKING_FINAL_SLOT;
            this.xk = System.currentTimeMillis();
            this.eF = 0L;
         }
      }
   }

   private void ai() {
      if (this.FC.currentScreen instanceof GenericContainerScreen var1) {
         if (this.ix) {
            this.AL = AliceAutoReconnect.State.WAIT_AFTER_VERIFY;
            this.xk = System.currentTimeMillis();
         } else if (System.currentTimeMillis() - this.xk >= 20000L) {
            if (this.FC.player != null) {
               this.FC.player.closeHandledScreen();
            }

            this.AL = AliceAutoReconnect.State.SENDING_MENU;
            this.xk = System.currentTimeMillis();
            this.az("[AutoLogin] Timeout antibot, thử lại /menu.");
         } else if (this.av()) {
            if (System.currentTimeMillis() - this.eF >= this.getClickDelayMs()) {
               ScreenHandler var4 = var1.getScreenHandler();
               int var3 = this.ar(var4);
               if (var3 != -1) {
                  this.FC.interactionManager.clickSlot(var4.syncId, var3, 0, SlotActionType.PICKUP, this.FC.player);
                  this.eF = System.currentTimeMillis();
               }
            }
         }
      } else {
         if (System.currentTimeMillis() - this.xk >= 8000L) {
            this.AL = AliceAutoReconnect.State.SENDING_MENU;
            this.xk = System.currentTimeMillis();
         }
      }
   }

   private void aj() {
      if (System.currentTimeMillis() - this.xk >= 1500L) {
         this.AL = AliceAutoReconnect.State.CLICKING_FINAL_SLOT;
         this.xk = System.currentTimeMillis();
         this.eF = 0L;
      }
   }

   private void ak() {
      if (!(this.FC.currentScreen instanceof GenericContainerScreen var1)) {
         if (System.currentTimeMillis() - this.xk >= 8000L) {
            this.AL = AliceAutoReconnect.State.SENDING_MENU;
            this.xk = System.currentTimeMillis();
         }
      } else if (this.av()) {
         if (System.currentTimeMillis() - this.eF >= this.getClickDelayMs()) {
            ScreenHandler var3 = var1.getScreenHandler();
            int var10000 = var3.slots.size() > 24 ? '\ud83a' : '\ud839';

            while (true) {
               switch (var10000) {
                  case 55353:
                     if (System.currentTimeMillis() - this.xk >= 8000L) {
                        this.AL = AliceAutoReconnect.State.SENDING_MENU;
                        this.xk = System.currentTimeMillis();
                     }

                     return;
                  case 55354:
                     var10000 = 55352;
                     break;
                  default:
                     this.FC.interactionManager.clickSlot(var3.syncId, 24, 0, SlotActionType.PICKUP, this.FC.player);
                     this.eF = System.currentTimeMillis();
                     this.ix = false;
                     this.AL = AliceAutoReconnect.State.WAITING_TO_RESTORE_SPAWNER;
                     this.xk = this.eF;
                     this.iE = this.eF + 2000L;
                     this.ay("[AutoLogin] Đã click slot 24.");
                     return;
               }
            }
         }
      }
   }

   private void al() {
      int var10000 = System.currentTimeMillis() >= this.iE ? '顠' : '顟';

      while (true) {
         switch (var10000) {
            case 39007:
               return;
            case 39008:
               var10000 = 39006;
               break;
            default:
               if (!(this.FC.currentScreen instanceof GenericContainerScreen)) {
                  if (!this.au()) {
                     return;
                  }

                  this.ap(false);
                  this.AL = AliceAutoReconnect.State.IDLE;
                  this.xk = System.currentTimeMillis();
                  this.ay("[AutoLogin] Hoàn tất flow đăng nhập.");
                  return;
               }

               if (this.FC.player != null && System.currentTimeMillis() - this.xk >= 8000L) {
                  this.FC.player.closeHandledScreen();
               }

               return;
         }
      }
   }

   private void am(String var1) {
      if (!this.at(this.AL)) {
         short var2 = 3182;
         this.ao();
         this.ix = false;
         this.eF = 0L;
         this.AL = AliceAutoReconnect.State.SENDING_MENU;
         this.xk = System.currentTimeMillis();
         this.ay(
            StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","[AutoLogin] Phát hiện chat trigger (","), bắt đầu mở /menu.">(
               this.getTriggerLabel(var1)
            )
         );
      }
   }

   private void an() {
      String var1 = ((String)this.Is.get()).trim();
      int var10000 = !var1.isBlank() ? 1155 : 1154;

      while (true) {
         switch (var10000) {
            case 1154:
               this.az("[AutoLogin] Chưa nhập mật khẩu! Hãy điền vào setting 'login-password'.");
               this.AL = AliceAutoReconnect.State.IDLE;
               this.xk = System.currentTimeMillis();
               return;
            case 1155:
               var10000 = 1153;
               break;
            default:
               ChatUtils.sendPlayerMsg(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","/l ">(var1));
               this.IH = System.currentTimeMillis();
               return;
         }
      }
   }

   private void ao() {
      try {
         Module var1 = Modules.get().get(SpawnerOrder.class);
         boolean var2 = var1 != null && var1.isActive();
         if (var2) {
            if (this.FC.currentScreen != null && this.FC.player != null) {
               this.FC.player.closeHandledScreen();
            }

            ((SpawnerOrder)var1).mI = true;
            this.BN = true;
            this.Dg = true;
            this.ay("[AutoLogin] Đã dừng SpawnerOrder.");
         }
      } catch (Exception var3) {
      }
   }

   private void ap(boolean var1) {
      if (this.Dg) {
         int var10000 = this.BN ? 31260 : 31259;

         while (true) {
            int var4 = var10000;
            if (var4 == 31259) {
               break;
            }

            if (var4 != 31260) {
               if (!var1) {
                  if (!this.au()) {
                     return;
                  }

                  if (this.FC.currentScreen != null) {
                     return;
                  }
               }

               Module var2 = Modules.get().get(SpawnerOrder.class);
               if (var2 != null && ((SpawnerOrder)var2).mI) {
                  ((SpawnerOrder)var2).mI = false;
                  this.ay("[AutoLogin] Đã bật lại SpawnerOrder.");
               }

               this.Dg = false;
               this.BN = false;
               this.iE = 0L;
               return;
            }

            var10000 = 31258;
         }
      }
   }

   private boolean aq(GenericContainerScreen var1) {
      ScreenHandler var2 = var1.getScreenHandler();

      for (int var3 = 0; var3 < Math.min(var2.slots.size(), 54); var3++) {
         ItemStack var4 = var2.getSlot(var3).getStack();
         if (!var4.isEmpty()) {
            String var5 = this.ax(var4.getName().getString()).toLowerCase(Locale.ROOT);
            if (var5.contains("xac minh") || var5.contains("la nguoi") || var5.contains("verify")) {
               return true;
            }
         }
      }

      return false;
   }

   private int ar(ScreenHandler var1) {
      for (int var2 = 0; var2 < Math.min(var1.slots.size(), 54); var2++) {
         ItemStack var3 = var1.getSlot(var2).getStack();
         if (!var3.isEmpty()) {
            String var4 = this.ax(var3.getName().getString()).toLowerCase(Locale.ROOT);
            if (var4.contains("xac minh") || var4.contains("la nguoi") || var4.contains("verify")) {
               return var2;
            }
         }
      }

      return -1;
   }

   private boolean as(String var1) {
      return var1.contains("dang nhap thanh cong, neu chua tao ma pin, hay vao")
         || var1.contains("server con moi nen neu co")
         || var1.contains("khong duoc phep ban cac vat pham");
   }

   private String getTriggerLabel(String var1) {
      if (var1.contains("dang nhap thanh cong, neu chua tao ma pin, hay vao")) {
         return "login-success";
      } else if (!var1.contains("server con moi nen neu co")) {
         return var1.contains("khong duoc phep ban cac vat pham") ? "khong-duoc-phep-ban" : "unknown";
      } else {
         return "server-moi";
      }
   }

   private boolean at(AliceAutoReconnect.State var1) {
      return var1 == AliceAutoReconnect.State.SENDING_MENU
         || var1 == AliceAutoReconnect.State.WAIT_FOR_MENU_GUI
         || var1 == AliceAutoReconnect.State.ANTIBOT_VERIFY
         || var1 == AliceAutoReconnect.State.WAIT_AFTER_VERIFY
         || var1 == AliceAutoReconnect.State.CLICKING_FINAL_SLOT;
   }

   private long getClickDelayMs() {
      return ((Integer)this.wm.get()).intValue() * 50L;
   }

   private long getActionDelayMs() {
      return ((Integer)this.wm.get()).intValue() * 50L;
   }

   private boolean au() {
      if (this.FC.player != null) {
         if (this.FC.world != null) {
            return true;
         }
      } else {
         short var1 = 23635;
      }

      return false;
   }

   private boolean av() {
      return this.au() && this.FC.interactionManager != null;
   }

   private void aw() {
      this.AL = AliceAutoReconnect.State.IDLE;
      this.ix = false;
      this.xk = 0L;
      this.IH = 0L;
      this.iJ = 0L;
      this.eF = 0L;
      this.Dg = false;
      this.BN = false;
      this.iE = 0L;
      this.wr = 0L;
   }

   private String ax(String var1) {
      return var1 == null ? "" : Normalizer.normalize(var1, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replace("đ", "d").replace("Đ", "D");
   }

   private void ay(String var1) {
   }

   private void az(String var1) {
   }

   public String getInfoString() {
      return this.isActive() ? this.getStateLabel() : null;
   }

   public static void init() {
      if (!ph) {
         ph = true;
      }
   }

   private static enum State {
      IDLE,
      WAITING_TO_SEND_LOGIN,
      WAITING_LOGIN_SUCCESS,
      SENDING_MENU,
      WAIT_FOR_MENU_GUI,
      ANTIBOT_VERIFY,
      WAIT_AFTER_VERIFY,
      CLICKING_FINAL_SLOT,
      WAITING_TO_RESTORE_SPAWNER;
   }
}
