package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import meteordevelopment.meteorclient.events.entity.player.PlayerTickMovementEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.KeybindSetting.Builder;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;

public final class AutoExpPlusPlus extends AliceModule implements GrimCombatCoordinator.Participant {
   private static final int DY = 200;
   private static final int lz = 10;
   private static final MethodHandle En = dR();
   private final SettingGroup Cu = this.settings.getDefaultGroup();
   private final SettingGroup ei = this.settings.createGroup("Trigger");
   private final SettingGroup Bq = this.settings.createGroup("Safety");
   private final Setting<Keybind> yy = this.Cu
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("activ\ud9cate-key")).description("Phim tu kich hoat nem\uddc8exp, khong c\udacdn cho giap yeu."))
               .defaultValue(Keybind.none()))
            .build()
      );
   private final Setting<AutoExpPlusPlus.KeyMode> L = this.Cu
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                     .name("key-mode"))
                  .description("Toggle: nhan mot lan roi nem\udd41den k\ude44i du nguong. Hold: chi nem khi dang\udf41\udd48iu phim."))
               .defaultValue(AutoExpPlusPlus.KeyMode.Toggle))
            .build()
      );
   private final Setting<Integer> rL = this.Cu
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("throws-per-tick"))
                  .description("So binh nem trong mot tick. Cang cao cang n\ud8d4anh, khong co delay nao khac."))
               .defaultValue(4))
            .min(1)
            .max(10)
            .sliderRange(1, 10)
            .build()
      );
   private final Setting<Boolean> xl = this.Cu
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("look-down"))
                  .description("Gui pitch nem trong chinh goi interact de binh roi ngay duoi chan; camera khong bi xoay."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Integer> vd = this.Cu
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("throw-pitch"))
                     .description("Pitch dung cho goi nem khi lo\ud8e2k-down bat (90 = thang xuong chan)."))
                  .defaultValue(90))
               .min(0)
               .max(90)
               .sliderRange(0, 90)
               .visible(this.xl::get))
            .build()
      );
   private final Setting<Boolean> Jn = this.Cu
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("refill-hotbar"))
                  .description("Tu keo exp bott\ud8a0e tu inventory xuong hotbar khi hotba\udac2 het."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Integer> sx = this.Cu
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("refill-slot"))
                     .description("Sl\udb61t hotbar (1-9) danh cho exp bottle."))
                  .defaultValue(9))
               .min(1)
               .max(9)
               .sliderRange(1, 9)
               .visible(this.Jn::get))
            .build()
      );
   private final Setting<Integer> EJ = this.Cu
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("keep-bottles"))
                  .description("\udeb5iu lai it nhat bao nhieu binh, khong nem het."))
               .defaultValue(0))
            .min(0)
            .sliderRange(0, 64)
            .build()
      );
   private final Setting<Boolean> ve = this.Cu
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("chat-feedback"))
                  .description("Bao trong c\ud824at khi bat dau, khi dung, hoac khi het binh."))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> bG = this.ei
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("auto-trigger"))
                  .description("Tu kich hoat khi do ben gi\ud901p thap hon start-durability."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Integer> Hc = this.ei
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("start-durability"))
                     .description("Do\ud9b9ben (%) \udbb4hap nhat c\ud8c1a giap ma bat dau nem."))
                  .defaultValue(45))
               .min(1)
               .max(99)
               .sliderRange(1, 99)
               .visible(this.bG::get))
            .build()
      );
   private final Setting<Integer> eW = this.ei
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("s\udff6op-durability"))
                  .description("Dung nem khi do ben (%) t\uda80ap nhat da \ud9daat nguong nay. 100 = cho hoi day."))
               .defaultValue(100))
            .min(2)
            .max(100)
            .sliderRange(2, 100)
            .build()
      );
   private final Setting<Boolean> tA = this.ei
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("mending-only"))
                  .description("Chi tinh \ude14hung\udd80mon co Mending; mon khong co Me\ud880ding thi e\udb86p khong the va."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> uX = this.ei
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("\udcd4atch-helmet"))
                  .description("Theo\udf38doi do b\udad2n cua mu."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> qv = this.ei
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("watch-ch\ude67stplate"))
                  .description("Th\ude71o doi do ben cua ao giap."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> Bx = this.ei
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("watc\udcd2-l\uda20ggings"))
                  .description("Theo doi do ben cua q\udbc8a\udc20 giap."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> ou = this.ei
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("wa\udeb4ch-boots"))
                  .description("Theo doi do ben cua giay."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> IR = this.ei
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("watch-hands"))
                  .description("Tinh ca do ben cua item t\udaa6en hai tay."))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> AR = this.Bq
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("pause-on-screen"))
                  .description("Khong nem k\ud8b4\ud833 dang mo GUI."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> jr = this.Bq
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("pause-while-using"))
                  .description("Khong nem khi dang\udf6ca\udd03,\ud929dang gio khien hoac dang dung item khac."))
               .defaultValue(true))
            .build()
      );
   private final InventoryRefillHelper zt = new InventoryRefillHelper(this);
   private boolean zh;
   private boolean V;
   private boolean yO;
   private boolean oQ;

   public AutoExpPlusPlus() {
      super(
         AliceAddon.PVP_CATEGORY,
         "auto-exp++",
         "Tu \ud9b4ong nem binh kinh nghiem de Mending hoi giap ngay khi gia\ud880 yeu\uda79 va dung lai khi giap da du."
      );
   }

   @Override
   public void onActivate() {
      this.dQ();
      if (En == null) {
         this.error("Kho\udc5cg tim thay se\uda2euenced packet\ud840sender cho ban Minecraft \ud9c4ay.", new Object[0]);
         this.toggle();
      } else {
         GrimCombatCoordinator.wG(this);
         super.onActivate();
      }
   }

   @Override
   public void onDeactivate() {
      GrimCombatCoordinator.wH(this);
      this.dQ();
      super.onDeactivate();
   }

   @AliceEventHandler(priority = 90)
   private void dz(PlayerTickMovementEvent var1) {
      this.zt.yO();
   }

   @AliceEventHandler(priority = -50)
   private void dA(Post var1) {
      this.zt.yP();
   }

   @AliceEventHandler
   private void dB(meteordevelopment.meteorclient.events.world.TickEvent.Post var1) {
      if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
         this.dE();
         int var2 = this.dH();
         this.dF(var2);
         if (var2 >= 0 && var2 >= (Integer)this.eW.get()) {
            this.dG("giap da du nguong, dung nem");
         }

         if (!this.zh && !this.V) {
            this.zt.yQ();
         } else if (!(Boolean)this.AR.get() || this.mc.currentScreen == null) {
            if (!(Boolean)this.jr.get() || !this.mc.player.isUsingItem()) {
               if (!GrimCombatCoordinator.xf()) {
                  int var3 = this.dK();
                  if (var3 <= 0) {
                     this.dG("het binh kinh nghiem");
                     this.zt.yQ();
                  } else {
                     this.oQ = false;
                     Hand var4 = Hand.MAIN_HAND;
                     int var5 = -1;
                     if (this.mc.player.getOffHandStack().isOf(Items.EXPERIENCE_BOTTLE)) {
                        var4 = Hand.OFF_HAND;
                     } else {
                        var5 = this.dL();
                        if (var5 < 0) {
                           if ((Boolean)this.Jn.get()) {
                              this.dN();
                           }

                           return;
                        }
                     }

                     int var6 = Math.min(var3, this.dM(var4, var5));
                     if (var6 > 0) {
                        Hand var7 = var4;
                        int var8 = var5;
                        int var9 = Math.min(var6, (Integer)this.rL.get());
                        GrimCombatCoordinator.xo(this, () -> this.dC(var7, var8, var9));
                     }
                  }
               }
            }
         }
      } else {
         this.dQ();
      }
   }

   private void dC(Hand var1, int var2, int var3) {
      if (this.mc.player != null && this.mc.world != null) {
         boolean var4 = false;
         if (var1 == Hand.MAIN_HAND) {
            if (!GrimCombatCoordinator.wI(this, var2)) {
               return;
            }

            var4 = true;
         }

         for (int var5 = 0; var5 < var3; var5++) {
            if (var5 > 0 && !GrimCombatCoordinator.xn(this)) {
               if (var4) {
                  GrimCombatCoordinator.wJ(this);
               }

               return;
            }

            if (!this.dD(var1)) {
               if (var4) {
                  GrimCombatCoordinator.wJ(this);
               }

               return;
            }
         }

         if (var4) {
            GrimCombatCoordinator.wJ(this);
         }
      }
   }

   private boolean dD(Hand var1) {
      if (En != null && this.mc.player != null && this.mc.world != null) {
         float var2 = this.mc.player.getYaw();
         float var3 = this.xl.get() ? ((Integer)this.vd.get()).intValue() : this.mc.player.getPitch();
         SequencedPacketCreator var4 = var3x -> new PlayerInteractItemC2SPacket(var1, var3x, var2, var3);
         int var5 = GrimCombatCoordinator.xG();

         try {
            En.invoke((ClientPlayerInteractionManager)this.mc.interactionManager, (ClientWorld)this.mc.world, (SequencedPacketCreator)var4);
         } catch (Throwable var7) {
            return false;
         }

         return GrimCombatCoordinator.xG() > var5;
      } else {
         return false;
      }
   }

   private void dE() {
      int var2;
      label56: {
         if (this.mc.currentScreen == null) {
            var2 = !((Keybind)this.yy.get()).isPressed() ? 997 : 996;

            label44:
            while (true) {
               switch (var2) {
                  case 996:
                     var2 = 1;
                     break label56;
                  case 997:
                     var2 = 995;
                     break;
                  default:
                     break label44;
               }
            }
         }

         var2 = 0;
      }

      boolean var1 = (boolean)var2;
      if (this.L.get() == AutoExpPlusPlus.KeyMode.Hold) {
         this.zh = var1;
      } else if (var1 && !this.yO) {
         this.zh = !this.zh;
         this.dO(this.zh ? "bat bang phim" : "ta\udb05 bang phim");
      }

      this.yO = var1;
   }

   private void dF(int var1) {
      if ((Boolean)this.bG.get() && var1 >= 0) {
         if (!this.V && var1 <= (Integer)this.Hc.get() && var1 < (Integer)this.eW.get()) {
            this.V = true;
            this.dO(var1 + "");
         }
      } else {
         this.V = false;
      }
   }

   private void dG(String var1) {
      boolean var2 = this.zh || this.V;
      this.V = false;
      if (this.L.get() == AutoExpPlusPlus.KeyMode.Toggle) {
         this.zh = false;
      }

      if (var2 && !this.zh && !this.V) {
         this.dO(var1);
      }
   }

   private int dH() {
      if (this.mc.player == null) {
         return -1;
      } else {
         int var1 = -1;
         if ((Boolean)this.uX.get()) {
            var1 = this.dI(var1, EquipmentSlot.HEAD);
         }

         if ((Boolean)this.qv.get()) {
            var1 = this.dI(var1, EquipmentSlot.CHEST);
         }

         if ((Boolean)this.Bx.get()) {
            var1 = this.dI(var1, EquipmentSlot.LEGS);
         }

         if ((Boolean)this.ou.get()) {
            var1 = this.dI(var1, EquipmentSlot.FEET);
         }

         if ((Boolean)this.IR.get()) {
            var1 = this.dI(var1, EquipmentSlot.MAINHAND);
            var1 = this.dI(var1, EquipmentSlot.OFFHAND);
         }

         return var1;
      }
   }

   private int dI(int var1, EquipmentSlot var2) {
      ItemStack var3 = this.mc.player.getEquippedStack(var2);
      if (var3.isEmpty() || !var3.isDamageable() || var3.getMaxDamage() <= 0) {
         return var1;
      } else if ((Boolean)this.tA.get() && !this.dJ(var3)) {
         return var1;
      } else {
         int var4 = (var3.getMaxDamage() - var3.getDamage()) * 100 / var3.getMaxDamage();
         return var1 < 0 ? var4 : Math.min(var1, var4);
      }
   }

   private boolean dJ(ItemStack var1) {
      for (RegistryEntry var3 : EnchantmentHelper.getEnchantments(var1).getEnchantments()) {
         if (var3.matchesKey(Enchantments.MENDING)) {
            return true;
         }
      }

      return false;
   }

   private int dK() {
      if (this.mc.player == null) {
         return 0;
      } else {
         int var1 = 0;

         for (int var2 = 0; var2 < this.mc.player.getInventory().size(); var2++) {
            ItemStack var3 = this.mc.player.getInventory().getStack(var2);
            if (var3.isOf(Items.EXPERIENCE_BOTTLE)) {
               var1 += var3.getCount();
            }
         }

         return var1 - (Integer)this.EJ.get();
      }
   }

   private int dL() {
      if (this.mc.player == null) {
         return -1;
      } else {
         int var1 = (Integer)this.sx.get() - 1;
         if (this.mc.player.getInventory().getStack(var1).isOf(Items.EXPERIENCE_BOTTLE)) {
            return var1;
         } else {
            for (int var2 = 0; var2 < 9; var2++) {
               if (this.mc.player.getInventory().getStack(var2).isOf(Items.EXPERIENCE_BOTTLE)) {
                  return var2;
               }
            }

            return -1;
         }
      }
   }

   private int dM(Hand var1, int var2) {
      if (this.mc.player == null) {
         return 0;
      } else {
         ItemStack var3 = var1 == Hand.OFF_HAND ? this.mc.player.getOffHandStack() : this.mc.player.getInventory().getStack(var2);
         return !var3.isOf(Items.EXPERIENCE_BOTTLE) ? 0 : var3.getCount();
      }
   }

   private void dN() {
      RefillOutcome var1 = this.zt.yM(Items.EXPERIENCE_BOTTLE, (Integer)this.sx.get() - 1);
      if (var1 == RefillOutcome.td) {
         this.dP("khong tim thay exp bottle trong inventory");
         this.dG("khong con exp bottle");
      }
   }

   private void dO(String var1) {
      if ((Boolean)this.ve.get()) {
         this.info(var1, new Object[0]);
      }
   }

   private void dP(String var1) {
      if (!this.oQ) {
         this.oQ = true;
         this.dO(var1);
      }
   }

   private void dQ() {
      this.zh = false;
      this.V = false;
      this.yO = false;
      this.oQ = false;
      this.zt.yQ();
   }

   @Override
   public GrimCombatCoordinator.Intent prepareCombatIntent() {
      return null;
   }

   @Override
   public boolean canYieldEpoch() {
      return true;
   }

   @Override
   public boolean reservesLowerPriorityEpochs() {
      return false;
   }

   @Override
   public int combatPriority() {
      return 200;
   }

   @Override
   public void suspendForItemUse() {
      this.zt.yQ();
   }

   @Override
   public void yieldToHigherPriority() {
      this.zt.yQ();
   }

   @Override
   public boolean yieldToManualInput() {
      this.zt.yQ();
      return true;
   }

   private static MethodHandle dR() {
      for (Method var3 : ClientPlayerInteractionManager.class.getDeclaredMethods()) {
         Class[] var4 = var3.getParameterTypes();
         if (var3.getReturnType() == void.class && var4.length == 2 && var4[0] == ClientWorld.class && var4[1] == SequencedPacketCreator.class) {
            try {
               var3.setAccessible(true);
               return MethodHandles.lookup().unreflect(var3);
            } catch (RuntimeException | IllegalAccessException var6) {
            }
         }
      }

      return null;
   }

   public static enum KeyMode {
      Toggle,
      Hold;
   }
}
