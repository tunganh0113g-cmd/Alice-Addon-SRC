package com.nnpg.alice.internal;

import com.mojang.datafixers.util.Pair;
import com.nnpg.alice.AliceAddon;
import java.util.List;
import meteordevelopment.meteorclient.events.entity.player.PlayerTickMovementEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent.Post;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;

public class AutoTotem extends AliceModule implements GrimCombatCoordinator.Participant {
   private static final int qf = 40;
   private static final int Ei = 45;
   private static final int HS = 40;
   private static final int qu = 4;
   private static final int wR = 6;
   private static final int kk = 20;
   private static AutoTotem JB;
   private final SettingGroup FL = this.settings.getDefaultGroup();
   private final Setting<Integer> nG = this.FL
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("delay")).description("so tick cho giua moi lan swap sang offhand")).defaultValue(0))
            .min(0)
            .sliderMax(20)
            .build()
      );
   private final Setting<Boolean> fI = this.FL
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("plus"))
                  .description("giu totem mainhand phia server trong luc refill offhand; khong doi slot hien thi"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Integer> aU = this.FL
      .add(
         ((Builder)((Builder)((Builder)((Builder)new Builder().name("plus-slot")).description("hotbar slot (1-9) du tru totem cho mainhand bridge"))
                  .defaultValue(9))
               .min(1)
               .max(9)
               .sliderMax(9)
               .visible(this.fI::get))
            .build()
      );
   private final Setting<Boolean> DW = this.FL
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("sync"))
                     .description("giu totem mainhand phia server den khi offhand duoc server xac nhan"))
                  .defaultValue(false))
               .visible(this.fI::get))
            .build()
      );
   private final Setting<Boolean> rb = this.FL
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("fill-mainhand"))
                     .description("tu dong refill totem cho plus-slot tu inventory"))
                  .defaultValue(true))
               .visible(this.fI::get))
            .build()
      );
   private final InventoryRefillHelper re = new InventoryRefillHelper(this);
   private int er;
   private AutoTotem.SwapPhase ad = AutoTotem.SwapPhase.Idle;
   private ClientPlayerEntity xX;
   private AutoTotem.TotemInput DA;
   private boolean qg;
   private boolean hK;
   private boolean ig;
   private boolean Jz;
   private boolean iU;
   private int lI;
   private long aV = 1L;
   private long nF;
   private int Cf = -1;
   private int gb;
   private int w;

   public AutoTotem() {
      super(AliceAddon.PVP_CATEGORY, "auto-totem++", "Tu dong bao ve bang cau noi tay chinh va bo sung Totem vao tay phu an toan.");
   }

   static void iG() {
      AutoTotem var0 = JB;
      if (var0 != null) {
         if (!var0.iV()) {
            var0.iQ();
         }
      }
   }

   @Override
   public void onActivate() {
      JB = this;
      this.er = 0;
      this.ad = AutoTotem.SwapPhase.Idle;
      this.xX = null;
      this.qg = false;
      this.hK = false;
      this.ig = false;
      this.Jz = false;
      this.iU = false;
      this.lI = 0;
      this.aV = 1L;
      this.nF = 0L;
      this.Cf = -1;
      this.gb = 0;
      this.w = 0;
      this.jo();
      this.re.yQ();
      GrimCombatCoordinator.wG(this);
      super.onActivate();
   }

   @Override
   public void onDeactivate() {
      GrimCombatCoordinator.wH(this);
      if (JB == this) {
         JB = null;
      }

      this.er = 0;
      this.ad = AutoTotem.SwapPhase.Idle;
      this.xX = null;
      this.jm();
      this.jo();
      this.re.yQ();
      super.onDeactivate();
   }

   @AliceEventHandler(priority = -10000)
   private void iH(PlayerTickMovementEvent var1) {
      if (this.ad == AutoTotem.SwapPhase.NeutralArmed) {
         this.jn();
      }

      this.re.yO();
   }

   @AliceEventHandler(priority = 10000)
   private void iI(Post var1) {
      if (this.ad == AutoTotem.SwapPhase.NeutralApplied) {
         boolean var2 = this.jp() && this.mc.player == this.xX && this.mc.player != null && this.mc.player.input == this.DA;
         this.jo();
         this.xX = null;
         this.ad = var2 ? AutoTotem.SwapPhase.NeutralReady : AutoTotem.SwapPhase.Idle;
      }

      this.re.yP();
   }

   @AliceEventHandler
   private void iJ(Receive var1) {
      if (var1.packet instanceof EntityStatusS2CPacket var9) {
         if (var9.getStatus() == 35) {
            this.mc.execute(() -> this.iL(var9));
         }
      } else if (var1.packet instanceof SetPlayerInventoryS2CPacket var8) {
         if (var8.slot() == 40) {
            ItemStack var17 = var8.contents();
            this.mc.execute(() -> this.iM(var17));
         }
      } else if (var1.packet instanceof ScreenHandlerSlotUpdateS2CPacket var7) {
         int var16 = var7.getSyncId();
         int var20 = var7.getSlot();
         ItemStack var22 = var7.getStack();
         this.mc.execute(() -> {
            if (this.iK()) {
               if (this.jd(var16, var20)) {
                  this.iW(var22);
               }
            }
         });
      } else if (var1.packet instanceof InventoryS2CPacket var6) {
         int var15 = var6.syncId();
         List var19 = List.copyOf(var6.contents());
         this.mc.execute(() -> {
            if (this.iK()) {
               int var3 = this.je(var15);
               if (var3 >= 0 && var19.size() > var3) {
                  this.iW((ItemStack)var19.get(var3));
               }
            }
         });
      } else {
         if (var1.packet instanceof EntityEquipmentUpdateS2CPacket var2) {
            ItemStack var14 = null;

            for (Pair var5 : var2.getEquipmentList()) {
               if (var5.getFirst() == EquipmentSlot.OFFHAND) {
                  var14 = (ItemStack)var5.getSecond();
                  break;
               }
            }

            if (var14 == null) {
               return;
            }

            int var18 = var2.getEntityId();
            ItemStack var21 = var14;
            this.mc.execute(() -> {
               if (this.iK()) {
                  if (var18 == this.mc.player.getId()) {
                     this.iW(var21);
                  }
               }
            });
         }
      }
   }

   private boolean iK() {
      return this.isActive() && this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null;
   }

   private void iL(EntityStatusS2CPacket var1) {
      int var10000 = this.iK() ? 13051 : 13050;

      while (true) {
         int var2 = var10000;
         if (var2 == 13050) {
            return;
         }

         if (var2 != 13051) {
            if (var1.getEntity(this.mc.world) != this.mc.player) {
               return;
            }

            if ((Boolean)this.fI.get()) {
               this.iR();
            }

            return;
         }

         var10000 = 13049;
      }
   }

   private void iM(ItemStack var1) {
      if (this.iK()) {
         this.iW(var1);
      }
   }

   @AliceEventHandler
   private void iN(Pre var1) {
      if (this.mc.player != null
         && this.mc.world != null
         && this.mc.interactionManager != null
         && !this.mc.player.isCreative()
         && !this.mc.player.isSpectator()) {
         int var2 = this.jk();
         boolean var3 = this.mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);
         if (this.qg) {
            this.w++;
            if (this.w > 20) {
               this.jm();
            }
         }

         if (this.ig && !GrimCombatCoordinator.xm(this.nF)) {
            this.ig = false;
            this.nF = 0L;
            this.Cf = -1;
         }

         if (this.qg
            && this.ig
            && !this.iU
            && this.lI == 0
            && (this.Cf < 0 || this.Cf > 8 || !this.mc.player.getInventory().getStack(this.Cf).isOf(Items.TOTEM_OF_UNDYING))) {
            GrimCombatCoordinator.xl(this.nF);
            this.ig = false;
            this.nF = 0L;
            this.Cf = -1;
            this.hK = true;
         }

         if (this.qg && this.iU) {
            this.gb++;
            if (this.gb >= 6) {
               this.iY();
            }
         } else if (this.qg && this.hK && this.jh() >= 0) {
            this.iV();
         } else if (!var3) {
            if (this.qg && this.lI >= 4) {
               this.jm();
            }

            if (this.jj() < 0) {
               this.ad = AutoTotem.SwapPhase.Idle;
               this.jo();
               this.xX = null;
               if (this.qg) {
                  this.jm();
               }
            } else if (this.er > 0 && !this.qg) {
               this.er--;
            } else if (this.ad != AutoTotem.SwapPhase.NeutralArmed && this.ad != AutoTotem.SwapPhase.NeutralApplied) {
               if (this.ad == AutoTotem.SwapPhase.NeutralReady) {
                  this.iP();
               } else {
                  this.iO();
               }
            }
         } else {
            this.er = 0;
            if (!this.qg || this.Jz || !this.hK && !this.ig) {
               if (this.qg) {
                  this.jm();
               }

               if ((Boolean)this.fI.get()) {
                  int var10000 = !this.rb.get() ? 'ꦣ' : 'ꦢ';

                  while (true) {
                     int var5 = var10000;
                     if (var5 == 43426) {
                        if (!this.mc.player.getInventory().getStack(var2).isOf(Items.TOTEM_OF_UNDYING)) {
                           this.jl(var2);
                           return;
                        }
                        break;
                     }

                     if (var5 != 43427) {
                        break;
                     }

                     var10000 = 43425;
                  }
               }

               int var4 = this.re.yR();
               if (var4 < 0) {
                  this.re.yQ();
               }
            }
         }
      } else {
         this.er = 0;
         this.ad = AutoTotem.SwapPhase.Idle;
         this.jm();
         this.jo();
         this.xX = null;
         this.re.yQ();
      }
   }

   private void iO() {
      if (this.ad == AutoTotem.SwapPhase.Idle) {
         this.ad = AutoTotem.SwapPhase.NeutralArmed;
      }
   }

   private boolean iP() {
      int var1 = this.jj();
      if (var1 >= 0 && this.ja()) {
         char var10;
         while (var10 == '闕') {
            var10 = '闓';
         }

         boolean var2 = this.qg && this.ig && this.nF > 0L && GrimCombatCoordinator.xm(this.nF);
         boolean var3 = !var2 ? (!this.qg ? GrimCombatCoordinator.wY() : GrimCombatCoordinator.wZ()) : GrimCombatCoordinator.xh(this.nF);
         if (!var3) {
            return false;
         } else {
            boolean var4 = false;
            var4 = this.jb(var1);
            if (var4 && var2) {
               var4 = GrimCombatCoordinator.xi(this.nF, true);
               if (var4) {
                  this.hK = false;
                  this.Jz = true;
                  this.iU = (Boolean)this.DW.get();
                  this.gb = 0;
                  this.ig = GrimCombatCoordinator.xm(this.nF);
                  if (!this.ig) {
                     this.nF = 0L;
                     this.Cf = -1;
                  }
               }
            }

            if (var4) {
               this.ad = AutoTotem.SwapPhase.Idle;
               this.er = (Integer)this.nG.get();
            }

            if (!var4) {
               if (var2) {
                  GrimCombatCoordinator.xl(this.nF);
               } else {
                  GrimCombatCoordinator.xa();
               }
            }

            return var4;
         }
      } else {
         this.ad = AutoTotem.SwapPhase.Idle;
         return false;
      }
   }

   private void iQ() {
      if (this.isActive() && this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null && !this.iU) {
         if (this.ad == AutoTotem.SwapPhase.NeutralReady && this.ja()) {
            this.iP();
         } else {
            if (this.mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) && (Boolean)this.fI.get() && (Boolean)this.rb.get()) {
               int var1 = this.jk();
               if (!this.mc.player.getInventory().getStack(var1).isOf(Items.TOTEM_OF_UNDYING)) {
                  this.jl(var1);
               }
            }
         }
      }
   }

   private void iR() {
      int var2;
      label56: {
         if (this.ig) {
            var2 = this.nF <= 0L ? 784 : 783;

            label52:
            while (true) {
               switch (var2) {
                  case 783:
                     if (this.iU() && GrimCombatCoordinator.xk(this.nF)) {
                        var2 = 1;
                        break label56;
                     }
                     break label52;
                  case 784:
                     var2 = 782;
                     break;
                  default:
                     break label52;
               }
            }
         }

         var2 = 0;
      }

      boolean var1 = (boolean)var2;
      if (!var1 && this.ig && this.nF > 0L) {
         if (this.iU) {
            GrimCombatCoordinator.xj(this.nF);
         } else {
            GrimCombatCoordinator.xl(this.nF);
         }
      }

      this.ad = AutoTotem.SwapPhase.Idle;
      this.jo();
      this.xX = null;
      if (var1) {
         this.iT();
      } else {
         this.iS();
      }

      this.re.yQ();
      if (!var1 && !this.iV() && this.hK && this.jh() >= 0) {
         GrimCombatCoordinator.xd();
      }
   }

   private void iS() {
      this.qg = true;
      this.hK = true;
      this.ig = false;
      this.Jz = false;
      this.iU = false;
      this.lI = 0;
      this.nF = 0L;
      this.Cf = -1;
      this.gb = 0;
      this.w = 0;
   }

   private void iT() {
      this.qg = true;
      this.hK = false;
      this.ig = true;
      this.Jz = false;
      this.iU = false;
      this.lI = 0;
      this.gb = 0;
      this.w = 0;
   }

   private boolean iU() {
      return this.mc.player != null && this.Cf >= 0 && this.Cf <= 8 && this.mc.player.getInventory().getStack(this.Cf).isOf(Items.TOTEM_OF_UNDYING);
   }

   private boolean iV() {
      if (!this.hK || this.ig || this.lI >= 4) {
         return false;
      } else if (this.mc.player == null || this.mc.world == null) {
         return false;
      } else if (!this.mc.player.isCreative() && !this.mc.player.isSpectator()) {
         int var1 = this.jh();
         if (var1 >= 0) {
            long var2 = this.ji();
            if (GrimCombatCoordinator.xg(var1, var2)) {
               this.hK = false;
               this.ig = true;
               this.Jz = false;
               this.nF = var2;
               this.Cf = var1;
               this.gb = 0;
               this.ad = AutoTotem.SwapPhase.Idle;
               this.jo();
               this.xX = null;
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void iW(ItemStack var1) {
      int var10000 = !this.qg ? '섧' : '섦';

      while (true) {
         switch (var10000) {
            case 49446:
               this.iX(var1);
               return;
            case 49447:
               var10000 = 49445;
               break;
            default:
               if (!var1.isOf(Items.TOTEM_OF_UNDYING) && this.jg() >= 0) {
                  this.iO();
               }

               return;
         }
      }
   }

   private void iX(ItemStack var1) {
      if (!var1.isOf(Items.TOTEM_OF_UNDYING)) {
         if (!this.iU) {
            if (this.hK) {
               if (!this.iV()) {
                  if (this.jh() < 0 && this.jj() >= 0) {
                     this.hK = false;
                     this.iO();
                  }
               }
            } else {
               this.re.yQ();
               if (this.lI >= 4) {
                  this.jm();
               } else if (this.mc.player != null && !this.mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) && this.jj() >= 0) {
                  this.iO();
               }
            }
         }
      } else {
         if (this.nF > 0L) {
            GrimCombatCoordinator.xj(this.nF);
         }

         this.iU = false;
         this.gb = 0;
         this.hK = false;
      }
   }

   private void iY() {
      if (this.nF > 0L) {
         GrimCombatCoordinator.xl(this.nF);
      }

      this.iU = false;
      this.ig = false;
      this.Jz = false;
      this.nF = 0L;
      this.Cf = -1;
      this.gb = 0;
      if (this.mc.player != null && !this.mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) && this.jj() >= 0) {
         this.iO();
      }
   }

   private boolean iZ() {
      return this.qg && (this.hK || this.ig || this.iU || this.lI > 0);
   }

   boolean ja() {
      if (this.mc.player == null) {
         return false;
      } else {
         return this.mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) ? false : this.isActive();
      }
   }

   private boolean jb(int var1) {
      if (this.mc.player != null
         && this.mc.interactionManager != null
         && var1 >= 0
         && var1 < 36
         && this.mc.player.getInventory().getStack(var1).isOf(Items.TOTEM_OF_UNDYING)) {
         ScreenHandler var2 = this.mc.player.currentScreenHandler;
         if (var2 == null) {
            return false;
         } else {
            int var3 = this.jc(var1);
            if (var3 >= 0 && var3 < var2.slots.size()) {
               Slot var4 = var2.getSlot(var3);
               if (var4.inventory == this.mc.player.getInventory() && var4.getIndex() == var1 && var4.getStack().isOf(Items.TOTEM_OF_UNDYING)) {
                  if (this.qg) {
                     this.lI++;
                  }

                  this.mc.interactionManager.clickSlot(var2.syncId, var3, 40, SlotActionType.SWAP, this.mc.player);
                  return true;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private int jc(int var1) {
      if (this.mc.player == null) {
         return -1;
      } else {
         ScreenHandler var2 = this.mc.player.currentScreenHandler;
         if (var2 == null) {
            return -1;
         } else {
            label37:
            for (int var3 = 0; var3 < var2.slots.size(); var3++) {
               Slot var4 = var2.getSlot(var3);
               if (var4.inventory instanceof PlayerInventory) {
                  int var10000 = var4.getIndex() != var1 ? '삣' : '삢';

                  while (true) {
                     switch (var10000) {
                        case 49314:
                           return var3;
                        case 49315:
                           var10000 = 49313;
                           break;
                        default:
                           continue label37;
                     }
                  }
               }
            }

            return -1;
         }
      }
   }

   private boolean jd(int var1, int var2) {
      if (var2 < 0) {
         return false;
      } else {
         ScreenHandler var3 = this.jf(var1);
         if (var3 != null && var2 < var3.slots.size()) {
            Slot var4 = var3.getSlot(var2);
            return var4.inventory == this.mc.player.getInventory() && var4.getIndex() == 40;
         } else {
            return false;
         }
      }
   }

   private int je(int var1) {
      ScreenHandler var2 = this.jf(var1);
      if (var2 == null) {
         return -1;
      } else {
         if (var2 == this.mc.player.playerScreenHandler && 45 < var2.slots.size()) {
            Slot var3 = var2.getSlot(45);
            if (var3.inventory == this.mc.player.getInventory() && var3.getIndex() == 40) {
               return 45;
            }
         }

         for (int var5 = 0; var5 < var2.slots.size(); var5++) {
            Slot var4 = var2.getSlot(var5);
            if (var4.inventory == this.mc.player.getInventory() && var4.getIndex() == 40) {
               return var5;
            }
         }

         return -1;
      }
   }

   private ScreenHandler jf(int var1) {
      if (this.mc.player == null) {
         return null;
      } else if (this.mc.player.playerScreenHandler.syncId == var1) {
         return this.mc.player.playerScreenHandler;
      } else {
         ScreenHandler var2 = this.mc.player.currentScreenHandler;
         return var2 != null && var2.syncId == var1 ? var2 : null;
      }
   }

   private int jg() {
      if (this.mc.player == null) {
         return -1;
      } else {
         int var1 = this.jk();
         int var10000 = !this.mc.player.getInventory().getStack(var1).isOf(Items.TOTEM_OF_UNDYING) ? 30672 : 30671;

         while (true) {
            switch (var10000) {
               case 30671:
                  return var1;
               case 30672:
                  var10000 = 30670;
                  break;
               default:
                  for (int var2 = 0; var2 < 9; var2++) {
                     if (var2 != var1 && this.mc.player.getInventory().getStack(var2).isOf(Items.TOTEM_OF_UNDYING)) {
                        return var2;
                     }
                  }

                  for (int var3 = 9; var3 < 36; var3++) {
                     if (this.mc.player.getInventory().getStack(var3).isOf(Items.TOTEM_OF_UNDYING)) {
                        return var3;
                     }
                  }

                  return -1;
            }
         }
      }
   }

   private int jh() {
      if (this.mc.player == null) {
         return -1;
      } else {
         int var1 = this.jk();
         if (this.mc.player.getInventory().getStack(var1).isOf(Items.TOTEM_OF_UNDYING)) {
            return var1;
         } else {
            int var2 = 0;

            label35:
            while (true) {
               int var10000 = var2 >= 9 ? 29917 : 29916;

               while (true) {
                  switch (var10000) {
                     case 29916:
                        if (var2 != var1 && this.mc.player.getInventory().getStack(var2).isOf(Items.TOTEM_OF_UNDYING)) {
                           return var2;
                        }

                        var2++;
                        continue label35;
                     case 29917:
                        var10000 = 29915;
                        break;
                     default:
                        return -1;
                  }
               }
            }
         }
      }
   }

   private long ji() {
      long var1 = this.aV++;
      if (var1 <= 0L) {
         this.aV = 2L;
         return 1L;
      } else {
         return var1;
      }
   }

   private int jj() {
      if (this.qg && this.mc.player != null) {
         int var1 = this.jk();

         for (int var2 = 9; var2 < 36; var2++) {
            if (this.mc.player.getInventory().getStack(var2).isOf(Items.TOTEM_OF_UNDYING)) {
               return var2;
            }
         }

         int var4 = 0;

         while (true) {
            int var10000 = var4 >= 9 ? 2014 : 2013;

            while (true) {
               int var3 = var10000;
               if (var3 == 2013) {
                  if (var4 != var1 && this.mc.player.getInventory().getStack(var4).isOf(Items.TOTEM_OF_UNDYING)) {
                     return var4;
                  }

                  var4++;
                  break;
               }

               if (var3 != 2014) {
                  return this.mc.player.getInventory().getStack(var1).isOf(Items.TOTEM_OF_UNDYING) ? var1 : -1;
               }

               var10000 = 2012;
            }
         }
      } else {
         return this.jg();
      }
   }

   private int jk() {
      return Math.max(0, Math.min(8, (Integer)this.aU.get() - 1));
   }

   private RefillOutcome jl(int var1) {
      return this.mc.player == null ? RefillOutcome.hz : this.re.yM(Items.TOTEM_OF_UNDYING, var1);
   }

   private void jm() {
      this.qg = false;
      this.hK = false;
      this.lI = 0;
      if (this.ig && this.nF > 0L) {
         GrimCombatCoordinator.xl(this.nF);
      }

      this.ig = false;
      this.Jz = false;
      this.iU = false;
      this.nF = 0L;
      this.Cf = -1;
      this.gb = 0;
      this.w = 0;
   }

   private void jn() {
      ClientPlayerEntity var1 = this.mc.player;
      if (var1 != null && !var1.hasVehicle()) {
         if (this.DA == null || var1.input != this.DA) {
            this.DA = new AutoTotem.TotemInput(var1.input);
            var1.input = this.DA;
         }
      }
   }

   private void jo() {
      if (this.DA != null) {
         DelegatingInput.qM(this.mc.player, this.DA);
      }

      this.DA = null;
   }

   private boolean jp() {
      ClientPlayerEntity var1 = this.mc.player;
      if (var1 != null && !var1.isSprinting()) {
         Input var2 = var1.input;
         if (var2 == null) {
            return true;
         } else {
            Vec2f var3 = var2.getMovementInput();
            if (var3 != null && var3.lengthSquared() > 1.0E-5F) {
               return false;
            } else {
               PlayerInput var4 = var2.playerInput;
               return var4 == null || !var4.forward() && !var4.backward() && !var4.left() && !var4.right();
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public GrimCombatCoordinator.Intent prepareCombatIntent() {
      if (!this.isActive()) {
         return null;
      } else if (!this.iZ()) {
         return null;
      } else if (this.mc.player != null && !this.mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
         int var10000 = !this.hK ? '뀍' : '뀌';

         while (true) {
            int var2 = var10000;
            if (var2 == 45068) {
               int var1 = this.jh();
               return var1 >= 0 ? new GrimCombatCoordinator.Intent() {
                  @Override
                  public GrimCombatCoordinator.Participant owner() {
                     return AutoTotem.this;
                  }

                  @Override
                  public void execute() {
                     AutoTotem.this.iV();
                  }
               } : null;
            }

            if (var2 != 45069) {
               return null;
            }

            var10000 = 45067;
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean canYieldEpoch() {
      return false;
   }

   @Override
   public boolean reservesLowerPriorityEpochs() {
      return this.iZ();
   }

   @Override
   public int combatPriority() {
      return Integer.MAX_VALUE;
   }

   @Override
   public boolean canFollowEmergencyOffhandSwap() {
      return true;
   }

   @Override
   public void suspendForItemUse() {
      if (this.ad == AutoTotem.SwapPhase.NeutralArmed || this.ad == AutoTotem.SwapPhase.NeutralApplied) {
         this.ad = AutoTotem.SwapPhase.Idle;
         this.jo();
         this.xX = null;
      }

      this.re.yQ();
   }

   private static enum SwapPhase {
      Idle,
      NeutralArmed,
      NeutralApplied,
      NeutralReady;
   }

   private final class TotemInput extends DelegatingInput {
      private TotemInput(Input var2) {
         super(var2);
      }

      @Override
      protected void qL() {
         if (AutoTotem.this.ad == AutoTotem.SwapPhase.NeutralArmed && AutoTotem.this.mc.player != null && AutoTotem.this.mc.player.input == this) {
            PlayerInput var1 = this.qI().playerInput == null ? PlayerInput.DEFAULT : this.qI().playerInput;
            PlayerInput var2 = new PlayerInput(false, false, false, false, false, var1.sneak(), false);
            this.setState(var2, Vec2f.ZERO);
            AutoTotem.this.mc.player.setSprinting(false);
            AutoTotem.this.xX = AutoTotem.this.mc.player;
            AutoTotem.this.ad = AutoTotem.SwapPhase.NeutralApplied;
         }
      }
   }
}
