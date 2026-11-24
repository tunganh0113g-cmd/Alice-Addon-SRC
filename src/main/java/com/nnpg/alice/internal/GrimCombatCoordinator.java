package com.nnpg.alice.internal;

import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.DoAttackEvent;
import meteordevelopment.meteorclient.events.entity.player.DoItemUseEvent;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent.Post;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Send;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Sent;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.orbit.listeners.IListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PickItemFromBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PickItemFromEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class GrimCombatCoordinator {
   private static final int GX = 1000000;
   private static final int rk = -1000000;
   private static final int BS = 0;
   private static final int yT = 1;
   private static final int pa = 2;
   private static final int rh = 3;
   private static final int vx = 4;
   private static final int jc = 5;
   private static final int BI = 6;
   private static final int Jf = 7;
   private static final int GJ = 8;
   private static final int hY = 9;
   private static final int bN = 10;
   private static final GrimCombatCoordinator INSTANCE = new GrimCombatCoordinator();
   private static final ThreadLocal<GrimCombatCoordinator.MiningPacketPass> sB = new ThreadLocal<>();
   private final MinecraftClient zs = MinecraftClient.getInstance();
   private final List<GrimCombatCoordinator.Participant> IU = new ArrayList<>(2);
   private final IListener[] zY = new IListener[]{
      new GrimCombatCoordinator.CoordinatorListener(Pre.class, 1000000, 0),
      new GrimCombatCoordinator.CoordinatorListener(Pre.class, -1000000, 1),
      new GrimCombatCoordinator.CoordinatorListener(Send.class, 1000000, 2),
      new GrimCombatCoordinator.CoordinatorListener(Sent.class, -1000000, 3),
      new GrimCombatCoordinator.CoordinatorListener(Receive.class, -1000000, 4),
      new GrimCombatCoordinator.CoordinatorListener(Post.class, -1000000, 5),
      new GrimCombatCoordinator.CoordinatorListener(DoAttackEvent.class, 1000000, 6),
      new GrimCombatCoordinator.CoordinatorListener(DoItemUseEvent.class, 1000000, 7),
      new GrimCombatCoordinator.CoordinatorListener(StartBreakingBlockEvent.class, 1000000, 8),
      new GrimCombatCoordinator.CoordinatorListener(GameJoinedEvent.class, 1000000, 9),
      new GrimCombatCoordinator.CoordinatorListener(GameLeftEvent.class, 1000000, 10)
   };
   private boolean zG;
   private GrimCombatCoordinator.Participant sO;
   private GrimCombatCoordinator.Participant zd;
   private GrimCombatCoordinator.Participant fE;
   private GrimCombatCoordinator.Participant xA;
   private int qO = -1;
   private GrimCombatCoordinator.Participant su;
   private boolean sj;
   private boolean xS;
   private boolean In;
   private boolean dw;
   private boolean iM;
   private boolean vX;
   private int ca;
   private boolean to;
   private boolean hf;
   private boolean pT;
   private boolean gO;
   private boolean HJ;
   private boolean dR;
   private boolean ks;
   private volatile boolean Dv;
   private volatile long jB;
   private PlayerActionC2SPacket fT;
   private boolean aQ;
   private int Ch = -1;
   private volatile boolean jC;
   private volatile long HE;
   private int gS = -1;
   private int Db = -1;
   private int II = -1;
   private boolean yl;
   private boolean ns;
   private ClickSlotC2SPacket di;
   private boolean wJ;
   private boolean Aq;
   private boolean pd;
   private GrimCombatCoordinator.Participant xf;
   private boolean J;
   private int gl = -1;
   private int Io = -1;
   private float Ay;
   private float vi;
   private boolean xQ;

   private GrimCombatCoordinator() {
   }

   public static void install() {
      INSTANCE.installListeners();
   }

   static void wG(GrimCombatCoordinator.Participant var0) {
      INSTANCE.xq(var0);
   }

   static void wH(GrimCombatCoordinator.Participant var0) {
      INSTANCE.xr(var0);
   }

   static boolean wI(GrimCombatCoordinator.Participant var0, int var1) {
      return INSTANCE.xu(var0, var1);
   }

   static void wJ(GrimCombatCoordinator.Participant var0) {
      INSTANCE.xv(var0);
   }

   static void wK(GrimCombatCoordinator.Participant var0) {
      INSTANCE.xx(var0);
   }

   static boolean wL(GrimCombatCoordinator.Participant var0, int var1) {
      return INSTANCE.xy(var0, var1);
   }

   static boolean wM(GrimCombatCoordinator.Participant var0) {
      return INSTANCE.fE == var0 && INSTANCE.xS || INSTANCE.xA == var0;
   }

   static void wN(GrimCombatCoordinator.Participant var0, float var1, float var2) {
      INSTANCE.xC(var0, var1, var2);
   }

   static boolean wO(GrimCombatCoordinator.Participant var0) {
      return INSTANCE.sO == var0 && INSTANCE.IU.contains(var0) && !INSTANCE.vX && !INSTANCE.yo(var0);
   }

   static boolean wP(GrimCombatCoordinator.Participant var0) {
      if (var0 == null) {
         return false;
      } else {
         int var1 = var0.combatPriority();

         for (GrimCombatCoordinator.Participant var3 : INSTANCE.IU) {
            if (var3 != var0) {
               int var10000 = var3.combatPriority() >= var1 ? 3468 : 3467;

               while (true) {
                  int var4 = var10000;
                  if (var4 == 3467) {
                     return true;
                  }

                  if (var4 != 3468) {
                     break;
                  }

                  var10000 = 3466;
               }
            }
         }

         return false;
      }
   }

   static float wQ() {
      return INSTANCE.Ay;
   }

   static float wR() {
      return INSTANCE.vi;
   }

   static boolean wS() {
      return INSTANCE.xQ;
   }

   static int wT() {
      return INSTANCE.gl;
   }

   static boolean wU() {
      return INSTANCE.sj;
   }

   static boolean wV() {
      return !INSTANCE.vX && !INSTANCE.ym() && INSTANCE.sO == null;
   }

   static boolean wW() {
      return INSTANCE.hf;
   }

   static void wX() {
      if (wV()) {
         INSTANCE.to = true;
      }
   }

   static boolean wY() {
      return INSTANCE.yc();
   }

   static boolean wZ() {
      return INSTANCE.ye();
   }

   static void xa() {
      INSTANCE.to = false;
   }

   static boolean xb() {
      return INSTANCE.ye();
   }

   static boolean xc(int var0, long var1) {
      return INSTANCE.xR(var0, var1);
   }

   static boolean xd() {
      return INSTANCE.yf();
   }

   static void xe(long var0) {
      if (var0 > 0L && INSTANCE.jB == var0) {
         INSTANCE.Dv = false;
         INSTANCE.jB = 0L;
      }
   }

   static boolean xf() {
      return INSTANCE.Dv || INSTANCE.Ch >= 0 || INSTANCE.jC;
   }

   static boolean xg(int var0, long var1) {
      return INSTANCE.xS(var0, var1);
   }

   static boolean xh(long var0) {
      return INSTANCE.xT(var0);
   }

   static boolean xi(long var0, boolean var2) {
      return INSTANCE.xU(var0, var2);
   }

   static void xj(long var0) {
      INSTANCE.xV(var0);
   }

   static boolean xk(long var0) {
      return INSTANCE.xW(var0);
   }

   static void xl(long var0) {
      INSTANCE.xX(var0);
   }

   static boolean xm(long var0) {
      return var0 > 0L && INSTANCE.jC && INSTANCE.HE == var0;
   }

   static boolean xn(GrimCombatCoordinator.Participant var0) {
      return INSTANCE.xI(var0);
   }

   static boolean xo(GrimCombatCoordinator.Participant var0, Runnable var1) {
      return INSTANCE.xJ(var0, var1);
   }

   static void xp(GrimCombatCoordinator.Participant var0) {
      if (var0 != null && INSTANCE.IU.contains(var0)) {
         INSTANCE.pd = true;
         INSTANCE.yl();
         if (INSTANCE.In) {
            INSTANCE.xw();
         }
      }
   }

   private void installListeners() {
      if (!this.zG) {
         this.zG = true;
         this.initializeConnectionState();

         for (IListener var4 : this.zY) {
            MeteorClient.EVENT_BUS.subscribe(var4);
         }
      }
   }

   private void xq(GrimCombatCoordinator.Participant var1) {
      int var10000 = var1 != null ? 18286 : 18287;

      while (true) {
         int var2 = var10000;
         if (var2 == 18286) {
            if (!this.IU.contains(var1)) {
               this.IU.add(var1);
               this.initializeSlotIfUnknown();
               return;
            }
            break;
         }

         if (var2 != 18287) {
            break;
         }

         var10000 = 18285;
      }
   }

   private void xr(GrimCombatCoordinator.Participant var1) {
      if (var1 != null) {
         this.IU.remove(var1);
         var1.suspendForItemUse();
         if (var1 == this.xf) {
            this.xf = null;
         }

         if (this.xA == var1) {
            this.xB();
         }

         if (this.fE == var1 && this.xS) {
            this.In = true;
         }

         if (this.su == var1 && this.xQ && this.zs.player != null && !yt(this.Ay, this.vi, this.zs.player.getYaw(), this.zs.player.getPitch())) {
            this.dw = true;
         }

         int var10000 = this.sO != var1 ? 21974 : 21973;

         label44:
         while (true) {
            switch (var10000) {
               case 21973:
                  this.sO = null;
                  break label44;
               case 21974:
                  var10000 = 21972;
                  break;
               default:
                  break label44;
            }
         }

         this.yr();
      }
   }

   private void xs() {
      if (this.zs.player != null && this.zs.getNetworkHandler() != null) {
         this.initializeSlotIfUnknown();
         this.pd = this.yk();
         this.xf = null;
         if (this.pd) {
            this.yl();
         }

         int var10000 = this.Ch < 0 ? '\ufafd' : '\ufafc';

         label42:
         while (true) {
            switch (var10000) {
               case 64252:
                  int var1 = this.Ch;
                  if (this.gl == var1 || this.xQ(var1)) {
                     this.Ch = -1;
                  }
                  break label42;
               case 64253:
                  var10000 = 64251;
                  break;
               default:
                  break label42;
            }
         }

         this.maintainEmergencyMainhandBridge();
         if (this.In) {
            this.xw();
         }

         this.xz();
         if (this.dw && this.xQ && yt(this.Ay, this.vi, this.zs.player.getYaw(), this.zs.player.getPitch())) {
            this.dw = false;
            this.su = null;
         }

         this.yr();
      } else {
         this.yh();
      }
   }

   private void xt() {
      if (this.zs.player != null) {
         if (this.zs.world != null) {
            if (!this.IU.isEmpty()) {
               GrimCombatCoordinator.Participant var1 = this.yi();
               char var20;
               if (var1 != null) {
                  this.yj(var1);
               } else {
                  while (var20 == '츎') {
                     var20 = '츌';
                  }
               }

               boolean var2 = this.pd && var1 == null;
               if (var2 && this.xf == null) {
                  return;
               }

               boolean var3 = var1 != null ? this.jC || this.Ch >= 0 : this.yn();
               if (!var3 && !this.vX && !this.to) {
                  ArrayList var4 = new ArrayList(this.IU.size());

                  label199:
                  for (GrimCombatCoordinator.Participant var6 : List.copyOf(this.IU)) {
                     if (!var2 || var6 == this.xf) {
                        if (var1 != null) {
                           int var10000 = var6.combatPriority() >= var1.combatPriority() ? 7280 : 7279;

                           label195:
                           while (true) {
                              switch (var10000) {
                                 case 7279:
                                    continue label199;
                                 case 7280:
                                    var10000 = 7278;
                                    break;
                                 default:
                                    break label195;
                              }
                           }
                        }

                        if (!this.yo(var6) && !var6.canYieldEpoch()) {
                           GrimCombatCoordinator.Intent var7 = var6.prepareCombatIntent();
                           if (var7 != null && var7.owner() == var6) {
                              var4.add(var7);
                           }
                        }
                     }
                  }

                  if (var4.isEmpty()) {
                     return;
                  }

                  GrimCombatCoordinator.Intent var13 = (GrimCombatCoordinator.Intent)var4.getFirst();

                  label178:
                  for (GrimCombatCoordinator.Intent var15 : var4) {
                     int var17 = var15.owner().combatPriority() <= var13.owner().combatPriority() ? '脜' : '脛';

                     while (true) {
                        switch (var17) {
                           case 33051:
                              var13 = var15;
                              continue label178;
                           case 33052:
                              var17 = 33050;
                              break;
                           default:
                              continue label178;
                        }
                     }
                  }

                  this.sO = var13.owner();

                  try {
                     var13.execute();
                  } finally {
                     this.sO = null;
                  }

                  return;
               }

               return;
            }
         } else {
            char var9 = '쯎';
         }
      }
   }

   private boolean xu(GrimCombatCoordinator.Participant var1, int var2) {
      if (this.sO == var1) {
         short var7;
         if (this.IU.contains(var1)) {
            if (var2 >= 0 && var2 <= 8 && this.zs.player != null && this.zs.getNetworkHandler() != null) {
               this.initializeSlotIfUnknown();
               int var3 = this.zs.player.getInventory().getSelectedSlot();
               if (this.gl != var2 && !this.xQ(var2)) {
                  return false;
               }

               this.fE = var1;
               this.xS = var2 != var3;
               return this.gl == var2;
            }
         } else {
            while (var7 == 19776) {
               var7 = 19774;
            }
         }
      }

      return false;
   }

   private void xv(GrimCombatCoordinator.Participant var1) {
      int var10000 = this.xA != var1 ? 2960 : 2959;

      label25:
      while (true) {
         switch (var10000) {
            case 2959:
               this.xB();
               break label25;
            case 2960:
               var10000 = 2958;
               break;
            default:
               break label25;
         }
      }

      if (this.fE == var1) {
         if (!this.xS) {
            this.fE = null;
            this.xz();
         } else {
            this.xw();
         }
      }
   }

   private void xw() {
      if (this.xS && this.zs.player != null && this.zs.getNetworkHandler() != null) {
         int var1 = this.zs.player.getInventory().getSelectedSlot();
         GrimCombatCoordinator.Participant var2 = this.xA();
         int var3 = var2 != null ? this.qO : var1;
         int var10000 = this.gl == var3 ? 5403 : 5402;

         while (true) {
            int var4 = var10000;
            if (var4 != 5402) {
               if (var4 == 5403) {
                  var10000 = 5401;
                  continue;
               }
            } else if (!this.xQ(var3)) {
               break;
            }

            if (var2 != null) {
               this.fE = var2;
               this.xS = var3 != var1;
            } else {
               this.xS = false;
               this.fE = null;
            }

            this.In = false;
            this.yr();
            break;
         }
      }
   }

   private void xx(GrimCombatCoordinator.Participant var1) {
      if (var1 != null && this.fE == var1 && this.IU.contains(var1) && this.gl >= 0) {
         this.xA = var1;
         this.qO = this.gl;
      }
   }

   private boolean xy(GrimCombatCoordinator.Participant var1, int var2) {
      if (var1 == null || !this.IU.contains(var1) || var2 < 0 || var2 > 8 || this.zs.player == null || this.zs.getNetworkHandler() == null || this.sj) {
         return false;
      } else if (this.xA != null && this.xA != var1) {
         return false;
      } else if (this.fE != null && this.fE != var1 && this.xS) {
         return false;
      } else if (!this.In && this.Ch < 0 && !this.jC && !this.Dv) {
         this.initializeSlotIfUnknown();
         if (this.gl != var2 && !this.xQ(var2)) {
            return false;
         } else {
            this.xA = var1;
            this.qO = var2;
            this.fE = var1;
            this.xS = var2 != this.zs.player.getInventory().getSelectedSlot();
            int var10000 = this.gl != var2 ? 1027 : 1026;

            while (true) {
               switch (var10000) {
                  case 1026:
                     return true;
                  case 1027:
                     var10000 = 1025;
                     break;
                  default:
                     return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   private void xz() {
      if (this.xA != null) {
         if (!this.IU.contains(this.xA) || this.qO < 0 || this.qO > 8) {
            this.xB();
         } else if (this.zs.player != null && this.zs.getNetworkHandler() != null && !this.sj && !this.In && this.Ch < 0 && !this.jC && !this.Dv) {
            if (this.gl == this.qO || this.xQ(this.qO)) {
               int var1 = this.zs.player.getInventory().getSelectedSlot();
               this.fE = this.xA;
               this.xS = this.qO != var1;
               this.yr();
            }
         }
      }
   }

   private GrimCombatCoordinator.Participant xA() {
      if (this.xA == null || this.qO < 0) {
         return null;
      } else if (!this.IU.contains(this.xA)) {
         this.xB();
         return null;
      } else {
         return this.xA == this.fE ? null : this.xA;
      }
   }

   private void xB() {
      this.xA = null;
      this.qO = -1;
   }

   private void xC(GrimCombatCoordinator.Participant var1, float var2, float var3) {
      if (this.IU.contains(var1)) {
         this.Ay = var2;
         this.vi = var3;
         this.xQ = true;
         this.su = var1;
      }
   }

   private void xD(Send var1) {
      GrimCombatCoordinator.MiningPacketPass var2 = sB.get();
      if (var2 == null || var1.packet != var2.kw) {
         if (var1.packet instanceof ClientTickEndC2SPacket && this.ks && !this.HJ) {
            this.ks = false;
            if (!this.pT) {
               var1.setCancelled(true);
               return;
            }
         }

         if (var1.packet instanceof UpdateSelectedSlotC2SPacket var3 && this.jC && !this.sj) {
            this.II = var3.getSelectedSlot();
            var1.setCancelled(true);
         } else {
            boolean var9 = false;
            if (var1.packet instanceof ClickSlotC2SPacket var10 && this.jC && this.ns) {
               this.ns = false;
               this.di = var10;
               this.wJ = false;
               var9 = true;
            }

            if (this.Dv || this.Ch >= 0 || this.jC) {
               int var10000 = !yw(var1.packet) ? '雕' : '雔';

               while (true) {
                  int var6 = var10000;
                  if (var6 == 38612) {
                     if (!var9 && !this.yp()) {
                        var1.setCancelled(true);
                        return;
                     }
                     break;
                  }

                  if (var6 != 38613) {
                     break;
                  }

                  var10000 = 38611;
               }
            }

            if (this.to && var1.packet != this.fT) {
               if (yu(var1.packet)) {
                  var1.setCancelled(true);
                  return;
               }

               short var7 = 10770;
            }

            if (this.J && this.sO == null && yu(var1.packet)) {
               this.J = false;
               this.Aq = false;
            } else if (var1.packet instanceof PlayerInteractItemC2SPacket && this.iM) {
               this.iM = false;
            } else if (this.pd && this.sO == null && yu(var1.packet)) {
               this.Aq = false;
            } else {
               int var12 = var1.packet instanceof PlayerInteractItemC2SPacket ? 26285 : 26286;

               while (true) {
                  int var8 = var12;
                  if (var8 == 26285) {
                     if (this.yy()) {
                        return;
                     }
                     break;
                  }

                  if (var8 != 26286) {
                     break;
                  }

                  var12 = 26284;
               }

               if (!(var1.packet instanceof PlayerInteractBlockC2SPacket) || !this.yz()) {
                  if (yu(var1.packet)) {
                     boolean var11 = this.sO == null ? this.ym() : this.yo(this.sO);
                     if (!var11 && !this.vX) {
                        this.Aq = false;
                     } else if (!this.Aq) {
                        var1.setCancelled(true);
                     } else {
                        this.Aq = false;
                     }
                  }
               }
            }
         }
      }
   }

   private void xE(Sent var1) {
      GrimCombatCoordinator.MiningPacketPass var2 = sB.get();
      if (var2 != null && var1.packet == var2.kw) {
         var2.ms = true;
      }

      if (var1.packet == this.di) {
         this.wJ = true;
         this.hf = false;
      } else if (var1.packet == this.fT) {
         this.aQ = true;
      } else if (var1.packet instanceof UpdateSelectedSlotC2SPacket var5) {
         this.gl = var5.getSelectedSlot();
         this.Io = var5.getSelectedSlot();
         if (!this.sj) {
            this.xS = false;
            this.fE = null;
            this.In = false;
            this.yr();
         }
      } else if (var1.packet instanceof ClientTickEndC2SPacket) {
         if (this.HJ) {
            this.dR = true;
         }

         this.vX = false;
         this.zd = null;
         this.Aq = false;
         this.to = false;
         this.J = false;
         this.hf = true;
         this.pT = false;
         this.gO = false;
         AutoTotem.iG();
         CrystalAuraPlusPlus.of();
      } else {
         if (yx(var1.packet)) {
            this.hf = false;
            if (this.pT) {
               this.gO = true;
            }
         }

         if (var1.packet instanceof PlayerInteractEntityC2SPacket) {
            this.xF();
         } else {
            if (!(var1.packet instanceof PlayerInteractBlockC2SPacket)) {
               int var10000 = var1.packet instanceof PlayerInteractItemC2SPacket ? 'ꃔ' : 'ꃓ';

               label69:
               while (true) {
                  switch (var10000) {
                     case 41171:
                        if (!yv(var1.packet)) {
                           Packet var6 = var1.packet;
                           var10000 = !(var6 instanceof PlayerMoveC2SPacket) ? 2085 : 2084;

                           while (true) {
                              switch (var10000) {
                                 case 2084:
                                    PlayerMoveC2SPacket var3 = (PlayerMoveC2SPacket)var6;
                                    this.hf = false;
                                    this.pT = true;
                                    this.gO = false;
                                    if (var3.changesLook()) {
                                       this.Ay = var3.getYaw(this.Ay);
                                       this.vi = var3.getPitch(this.vi);
                                       this.xQ = true;
                                    }

                                    return;
                                 case 2085:
                                    var10000 = 2083;
                                    break;
                                 default:
                                    return;
                              }
                           }
                        }
                        break label69;
                     case 41172:
                        var10000 = 41170;
                        break;
                     default:
                        break label69;
                  }
               }
            }

            this.xF();
         }
      }
   }

   private void xF() {
      this.vX = true;
      this.ca++;
      if (this.sO != null) {
         this.zd = this.sO;
      }
   }

   static int xG() {
      return INSTANCE.ca;
   }

   static boolean xH(PlayerActionC2SPacket var0, Runnable var1) {
      if (var0 != null && var1 != null) {
         short var9;
         while (var9 == 31474) {
            var9 = 31472;
         }

         GrimCombatCoordinator.MiningPacketPass var2 = sB.get();
         GrimCombatCoordinator.MiningPacketPass var3 = new GrimCombatCoordinator.MiningPacketPass(var0);
         sB.set(var3);
         var1.run();
         boolean var4 = var3.ms;
         if (var2 == null) {
            sB.remove();
         } else {
            sB.set(var2);
         }

         return var4;
      } else {
         return false;
      }
   }

   private boolean xI(GrimCombatCoordinator.Participant var1) {
      if (var1 != null && this.IU.contains(var1)) {
         GrimCombatCoordinator.Participant var2 = this.yi();
         char var6;
         if (var2 != null) {
            if (var2 != var1 && var2.combatPriority() > var1.combatPriority()) {
               return false;
            }
         } else {
            while (var6 == '虑') {
               var6 = '虏';
            }
         }

         if (this.vX && this.zd != null && this.zd != var1 && this.zd.combatPriority() > var1.combatPriority()) {
            return false;
         } else {
            this.Aq = true;
            return true;
         }
      } else {
         return false;
      }
   }

   private boolean xJ(GrimCombatCoordinator.Participant var1, Runnable var2) {
      if (var1 != null && var2 != null && this.zs.player != null) {
         int var10000 = this.zs.world != null ? 29812 : 29813;

         while (true) {
            int var6 = var10000;
            if (var6 == 29812) {
               if (this.sO == null && this.IU.contains(var1)) {
                  if (this.pd) {
                     return false;
                  }

                  GrimCombatCoordinator.Participant var3 = this.yi();
                  if (var3 != null && var3 != var1 && var3.combatPriority() > var1.combatPriority()) {
                     return false;
                  }

                  if (this.yo(var1)) {
                     return false;
                  }

                  this.sO = var1;
                  var2.run();
                  this.sO = null;
                  return true;
               }
               break;
            }

            if (var6 != 29813) {
               break;
            }

            var10000 = 29811;
         }
      }

      return false;
   }

   private void xK(Receive var1) {
      if (var1.packet instanceof UpdateSelectedSlotS2CPacket var2) {
         final int var4 = var2.slot();
         this.zs.execute(new Runnable() {
            @Override
            public void run() {
               GrimCombatCoordinator.this.xL(var4);
            }
         });
      }
   }

   private void xL(int var1) {
      this.gl = var1;
      if (var1 == this.Ch) {
         this.Ch = -1;
      }

      this.xB();
      this.xS = false;
      this.fE = null;
      this.In = false;
      if (this.jC && this.yl && var1 == this.ya()) {
         this.yb();
      }

      this.yr();
   }

   private void xM() {
      if (this.dw && this.zs.player != null && this.xQ) {
         if (yt(this.Ay, this.vi, this.zs.player.getYaw(), this.zs.player.getPitch())) {
            this.dw = false;
            this.su = null;
            this.iM = false;
            this.yr();
         }
      }
   }

   private void xN(DoAttackEvent var1) {
      if (!this.pd && !this.yk()) {
         if (this.ym() || this.vX) {
            var1.setCancelled(true);
         }
      } else {
         this.yl();
         this.J = true;
      }
   }

   private void xO(DoItemUseEvent var1) {
      if (!this.pd) {
         int var10000 = !this.yk() ? '꺄' : '꺃';

         label91:
         while (true) {
            switch (var10000) {
               case 44675:
                  break label91;
               case 44676:
                  var10000 = 44674;
                  break;
               default:
                  if (!this.ym() && !this.vX) {
                     if (this.IU.isEmpty()) {
                        return;
                     }

                     boolean var5 = this.yq();
                     var10000 = this.xS ? 'ꣵ' : 'ꣴ';

                     label67:
                     while (true) {
                        switch (var10000) {
                           case 43252:
                              if (!var5) {
                                 return;
                              }
                              break label67;
                           case 43253:
                              var10000 = 43251;
                              break;
                           default:
                              break label67;
                        }
                     }

                     for (GrimCombatCoordinator.Participant var4 : List.copyOf(this.IU)) {
                        var4.suspendForItemUse();
                     }

                     if (!this.xS) {
                        this.dw = true;
                        this.iM = true;
                        return;
                     }

                     this.In = true;
                     if (var5) {
                        this.dw = true;
                     }

                     var1.setCancelled(true);
                     return;
                  }

                  if (this.zs.player != null) {
                     ItemStack var2 = this.zs.player.getMainHandStack();
                     if (var2.isOf(Items.EXPERIENCE_BOTTLE) || var2.isOf(Items.SPLASH_POTION) || var2.isOf(Items.LINGERING_POTION)) {
                        return;
                     }

                     ItemStack var3 = this.zs.player.getOffHandStack();
                     if (var3.isOf(Items.EXPERIENCE_BOTTLE)) {
                        return;
                     }
                  }

                  var1.setCancelled(true);
                  return;
            }
         }
      }

      this.yl();
      if (this.In) {
         this.xw();
      }

      this.iM = true;
      this.J = true;
   }

   private void xP(StartBreakingBlockEvent var1) {
      if (!this.pd && !this.yk()) {
         if (this.ym() || this.vX) {
            var1.setCancelled(true);
         }
      } else {
         this.yl();
         this.J = true;
      }
   }

   private boolean xQ(int var1) {
      if (var1 < 0 || var1 > 8 || this.zs.getNetworkHandler() == null) {
         return false;
      } else if (this.gl == var1) {
         return true;
      } else {
         if (this.Io == var1) {
            int var2 = ys(var1);
            if (!this.yg(var2) || this.gl != var2) {
               return false;
            }
         }

         return this.yg(var1) && this.gl == var1;
      }
   }

   private boolean xR(int var1, long var2) {
      if (var1 >= 0 && var1 <= 8) {
         int var10000 = var2 <= 0L ? 26163 : 26162;

         while (true) {
            switch (var10000) {
               case 26162:
                  if (this.zs.player != null && this.zs.getNetworkHandler() != null && this.hf && !this.Dv && this.Ch < 0) {
                     if (!this.to) {
                        if (wV()) {
                           this.initializeSlotIfUnknown();
                           int var4 = this.gl;
                           if (var4 >= 0 && var4 <= 8) {
                              this.to = true;
                              if (this.gl != var1) {
                                 var10000 = this.xQ(var1) ? 9714 : 9713;

                                 label145:
                                 while (true) {
                                    switch (var10000) {
                                       case 9713:
                                          this.vX = true;
                                          if (this.gl != var4 && !this.xQ(var4)) {
                                             this.Ch = var4;
                                          }

                                          return false;
                                       case 9714:
                                          var10000 = 9712;
                                          break;
                                       default:
                                          break label145;
                                    }
                                 }
                              }

                              PlayerActionC2SPacket var5 = new PlayerActionC2SPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN);
                              this.fT = var5;
                              this.aQ = false;

                              try {
                                 this.zs.getNetworkHandler().sendPacket(var5);
                              } finally {
                                 this.fT = null;
                              }

                              if (!this.aQ) {
                                 this.vX = true;
                                 if (this.gl != var4 && !this.xQ(var4)) {
                                    this.Ch = var4;
                                 }

                                 return false;
                              }

                              this.Dv = true;
                              this.jB = var2;
                              if (this.gl != var4 && !this.xQ(var4)) {
                                 this.Ch = var4;
                              } else {
                                 this.to = false;
                              }

                              return true;
                           }

                           return false;
                        }
                     } else {
                        short var7 = 26070;
                     }

                     return false;
                  }

                  return false;
               case 26163:
                  var10000 = 26161;
                  break;
               default:
                  return false;
            }
         }
      } else {
         return false;
      }
   }

   private boolean xS(int var1, long var2) {
      if (var1 >= 0
         && var1 <= 8
         && var2 > 0L
         && this.zs.player != null
         && this.zs.getNetworkHandler() != null
         && this.hf
         && !this.Dv
         && this.Ch < 0
         && !this.jC
         && !this.to
         && this.yd()) {
         this.initializeSlotIfUnknown();
         int var4 = this.gl;
         if (var4 >= 0 && var4 <= 8) {
            short var5 = 25942;
            this.to = true;
            if (this.gl != var1 && !this.xQ(var1)) {
               this.vX = true;
               if (this.gl != var4 && !this.xQ(var4)) {
                  this.Ch = var4;
               }

               this.to = false;
               return false;
            } else {
               if (this.xS || this.In) {
                  this.xS = false;
                  this.fE = null;
                  this.In = false;
               }

               this.jC = true;
               this.HE = var2;
               this.gS = var1;
               this.Db = var4;
               this.II = -1;
               this.yl = false;
               this.ns = false;
               this.di = null;
               this.wJ = false;
               this.to = false;
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean xT(long var1) {
      if (this.jC
         && this.HE == var1
         && !this.yl
         && !this.ns
         && this.di == null
         && this.gl == this.gS
         && this.hf
         && !this.to
         && !this.vX
         && this.sO == null
         && !this.Dv
         && this.Ch < 0) {
         this.to = true;
         this.ns = true;
         this.wJ = false;
         return true;
      } else {
         return false;
      }
   }

   private boolean xU(long var1, boolean var3) {
      if (this.jC && this.HE == var1) {
         boolean var4 = this.wJ;
         this.ns = false;
         this.di = null;
         this.wJ = false;
         this.to = false;
         if (!var4) {
            this.xY();
            return false;
         } else {
            if (!var3) {
               this.xY();
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void xV(long var1) {
      if (this.jC) {
         int var10000 = this.HE != var1 ? '굗' : '굘';

         while (true) {
            int var3 = var10000;
            if (var3 == 44375) {
               break;
            }

            if (var3 != 44376) {
               this.xY();
               return;
            }

            var10000 = 44374;
         }
      }
   }

   private boolean xW(long var1) {
      if (var1 > 0L && this.jC && this.HE == var1 && this.gS >= 0 && this.gS <= 8 && this.gl == this.gS) {
         this.ns = false;
         this.di = null;
         this.wJ = false;
         this.yl = false;
         this.to = false;
         return true;
      } else {
         return false;
      }
   }

   private void xX(long var1) {
      if (this.jC && this.HE == var1) {
         this.ns = false;
         this.di = null;
         this.wJ = false;
         this.to = false;
         this.xY();
      }
   }

   private void xY() {
      if (this.jC) {
         this.yl = true;
         this.xZ();
      }
   }

   private void maintainEmergencyMainhandBridge() {
      if (this.jC) {
         if (!this.yl) {
            if (this.gl != this.gS && this.hf && !this.to) {
               this.xQ(this.gS);
            }
         } else {
            this.xZ();
         }
      }
   }

   private void xZ() {
      if (this.jC && this.zs.player != null && this.zs.getNetworkHandler() != null) {
         int var1 = this.ya();
         if (var1 >= 0 && var1 <= 8) {
            if (this.gl == var1 || this.xQ(var1)) {
               this.yb();
            }
         }
      }
   }

   private int ya() {
      if (this.II >= 0 && this.II <= 8) {
         return this.II;
      } else {
         if (this.zs.player != null) {
            int var1 = this.zs.player.getInventory().getSelectedSlot();
            if (var1 >= 0) {
               int var10000 = var1 > 8 ? '\ud93a' : '\ud939';

               while (true) {
                  switch (var10000) {
                     case 55609:
                        return var1;
                     case 55610:
                        var10000 = 55608;
                        break;
                     default:
                        return this.Db;
                  }
               }
            }
         }

         return this.Db;
      }
   }

   private void yb() {
      this.jC = false;
      this.HE = 0L;
      this.gS = -1;
      this.Db = -1;
      this.II = -1;
      this.yl = false;
      this.ns = false;
      this.di = null;
      this.wJ = false;
      this.to = false;
   }

   private boolean yc() {
      if (this.hf && !this.to && wV()) {
         this.to = true;
         return true;
      } else {
         return false;
      }
   }

   private boolean yd() {
      return !this.vX && !this.to && !this.Dv && !this.jC && this.Ch < 0;
   }

   private boolean ye() {
      if (this.hf && this.yd()) {
         this.to = true;
         return true;
      } else {
         return false;
      }
   }

   private boolean yf() {
      if (!this.hf
         && this.pT
         && !this.gO
         && !this.HJ
         && !this.ks
         && !this.Dv
         && this.Ch < 0
         && !this.yn()
         && this.sO == null
         && this.zs.player != null
         && this.zs.getNetworkHandler() != null) {
         this.dR = false;
         this.HJ = true;

         try {
            this.zs.getNetworkHandler().sendPacket(ClientTickEndC2SPacket.INSTANCE);
         } finally {
            this.HJ = false;
         }

         if (!this.dR) {
            return false;
         } else {
            this.ks = true;
            return true;
         }
      } else {
         return false;
      }
   }

   private boolean yg(int var1) {
      if (var1 >= 0 && var1 <= 8 && var1 != this.Io && this.zs.getNetworkHandler() != null) {
         this.sj = true;

         try {
            this.zs.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var1));
         } finally {
            this.sj = false;
         }

         return this.gl == var1;
      } else {
         return false;
      }
   }

   private void initializeConnectionState() {
      this.yh();
      this.initializeSlotIfUnknown();
      if (this.zs.player != null) {
         this.Ay = this.zs.player.getYaw();
         this.vi = this.zs.player.getPitch();
         this.xQ = true;
      }
   }

   private void initializeSlotIfUnknown() {
      if (this.gl < 0 && this.zs.player != null) {
         this.gl = this.zs.player.getInventory().getSelectedSlot();
         this.Io = this.gl;
      }
   }

   private void yh() {
      this.sO = null;
      this.zd = null;
      this.fE = null;
      this.xA = null;
      this.qO = -1;
      this.su = null;
      this.sj = false;
      this.xS = false;
      this.In = false;
      this.dw = false;
      this.iM = false;
      this.vX = false;
      this.to = false;
      this.hf = false;
      this.pT = false;
      this.gO = false;
      this.HJ = false;
      this.dR = false;
      this.ks = false;
      this.Dv = false;
      this.jB = 0L;
      this.fT = null;
      this.aQ = false;
      this.jC = false;
      this.HE = 0L;
      this.gS = -1;
      this.Db = -1;
      this.II = -1;
      this.yl = false;
      this.ns = false;
      this.di = null;
      this.wJ = false;
      this.Aq = false;
      this.pd = false;
      this.xf = null;
      this.J = false;
      this.Ch = -1;
      this.gl = -1;
      this.Io = -1;
      this.Ay = 0.0F;
      this.vi = 0.0F;
      this.xQ = false;
   }

   private GrimCombatCoordinator.Participant yi() {
      GrimCombatCoordinator.Participant var1 = null;

      for (GrimCombatCoordinator.Participant var3 : this.IU) {
         boolean var4;
         try {
            var4 = var3.reservesLowerPriorityEpochs();
         } catch (Throwable var6) {
            continue;
         }

         if (var4 && (var1 == null || var3.combatPriority() > var1.combatPriority())) {
            var1 = var3;
         }
      }

      return var1;
   }

   private void yj(GrimCombatCoordinator.Participant var1) {
      int var2 = var1.combatPriority();
      this.Aq = false;

      for (GrimCombatCoordinator.Participant var4 : List.copyOf(this.IU)) {
         int var10000 = var4 == var1 ? 5088 : 5087;

         while (true) {
            int var6 = var10000;
            if (var6 == 5087) {
               if (var4.combatPriority() < var2) {
                  var4.yieldToHigherPriority();
               }
               break;
            }

            if (var6 != 5088) {
               break;
            }

            var10000 = 5086;
         }
      }

      if (this.xA != null && this.xA != var1 && this.xA.combatPriority() < var2) {
         this.xB();
      }

      if (this.fE != null && this.fE != var1 && this.fE.combatPriority() < var2 && this.xS) {
         this.xw();
         if (this.xS) {
            this.In = true;
         }
      }

      if (this.su != null && this.su != var1 && this.su.combatPriority() < var2) {
         if (this.yq()) {
            this.dw = true;
         } else {
            this.su = null;
            this.dw = false;
         }
      }
   }

   private boolean yk() {
      return this.zs.currentScreen == null && (this.zs.options.useKey.isPressed() || this.zs.options.attackKey.isPressed());
   }

   private void yl() {
      this.Aq = false;
      GrimCombatCoordinator.Participant var1 = null;

      for (GrimCombatCoordinator.Participant var3 : List.copyOf(this.IU)) {
         if (!var3.reservesLowerPriorityEpochs() && !var3.yieldToManualInput() && (var1 == null || var3.combatPriority() > var1.combatPriority())) {
            var1 = var3;
         }
      }

      this.xf = var1;
      if (this.fE != null && this.xS && this.fE != var1) {
         this.In = true;
      }

      if (this.su != null && this.su != var1) {
         if (this.yq()) {
            this.dw = true;
         } else {
            this.su = null;
            this.dw = false;
         }
      }
   }

   private boolean ym() {
      return this.In || this.dw || this.to || this.Dv || this.jC || this.Ch >= 0;
   }

   private boolean yn() {
      return this.In || this.dw || this.jC || this.Ch >= 0;
   }

   private boolean yo(GrimCombatCoordinator.Participant var1) {
      return this.yn() || this.Dv && (var1 == null || !var1.canFollowEmergencyOffhandSwap()) || this.jC;
   }

   private boolean yp() {
      return this.Dv && this.Ch < 0 && this.sO != null && this.sO.canFollowEmergencyOffhandSwap();
   }

   private boolean yq() {
      return this.zs.player != null && this.xQ && !yt(this.Ay, this.vi, this.zs.player.getYaw(), this.zs.player.getPitch());
   }

   private void yr() {
      if (!this.In && !this.dw) {
         if (!this.xS) {
            this.fE = null;
         }

         if (this.su != null && !this.IU.contains(this.su)) {
            this.su = null;
         }
      }
   }

   static int ys(int var0) {
      return (var0 + 1) % 9;
   }

   static boolean yt(float var0, float var1, float var2, float var3) {
      return Float.compare(var0, var2) == 0 && Float.compare(var1, var3) == 0;
   }

   private static boolean yu(Object var0) {
      return var0 instanceof PlayerInteractItemC2SPacket
         || var0 instanceof PlayerInteractBlockC2SPacket
         || var0 instanceof PlayerInteractEntityC2SPacket
         || yv(var0);
   }

   private static boolean yv(Object var0) {
      if (!(var0 instanceof PlayerActionC2SPacket var1)) {
         return false;
      } else if (var1.getAction() != Action.START_DESTROY_BLOCK
         && var1.getAction() != Action.STOP_DESTROY_BLOCK
         && var1.getAction() != Action.ABORT_DESTROY_BLOCK
         && var1.getAction() != Action.STAB) {
         int var10000 = var1.getAction() != Action.SWAP_ITEM_WITH_OFFHAND ? 10927 : 10926;

         while (true) {
            switch (var10000) {
               case 10926:
                  return true;
               case 10927:
                  var10000 = 10925;
                  break;
               default:
                  return false;
            }
         }
      } else {
         return true;
      }
   }

   private static boolean yw(Object var0) {
      if (!(var0 instanceof PlayerInteractItemC2SPacket)
         && !(var0 instanceof PlayerInteractBlockC2SPacket)
         && !(var0 instanceof PlayerInteractEntityC2SPacket)
         && !(var0 instanceof PlayerActionC2SPacket)
         && !(var0 instanceof ClickSlotC2SPacket)
         && !(var0 instanceof PickItemFromBlockC2SPacket)
         && !(var0 instanceof PickItemFromEntityC2SPacket)) {
         int var10000 = var0 instanceof HandSwingC2SPacket ? '\ue1df' : '\ue1e0';

         while (true) {
            int var1 = var10000;
            if (var1 == 57823) {
               break;
            }

            if (var1 != 57824) {
               return false;
            }

            var10000 = 57822;
         }
      }

      return true;
   }

   private static boolean yx(Object var0) {
      return var0 instanceof PlayerInteractItemC2SPacket
         || var0 instanceof PlayerInteractBlockC2SPacket
         || var0 instanceof PlayerInteractEntityC2SPacket
         || var0 instanceof PlayerActionC2SPacket
         || var0 instanceof ClickSlotC2SPacket
         || var0 instanceof PickItemFromBlockC2SPacket
         || var0 instanceof PickItemFromEntityC2SPacket
         || var0 instanceof ClientCommandC2SPacket
         || var0 instanceof ClientStatusC2SPacket;
   }

   private boolean yy() {
      if (this.zs.player == null) {
         return false;
      } else {
         try {
            ItemStack var1 = this.zs.player.getMainHandStack();
            if (var1.isOf(Items.EXPERIENCE_BOTTLE)
               || var1.isOf(Items.SPLASH_POTION)
               || var1.isOf(Items.LINGERING_POTION)
               || var1.isOf(Items.ENDER_PEARL)
               || var1.isOf(Items.FIREWORK_ROCKET)
               || var1.isOf(Items.CHORUS_FRUIT)) {
               return true;
            }

            ItemStack var2 = this.zs.player.getOffHandStack();
            if (var2.isOf(Items.EXPERIENCE_BOTTLE)) {
               return true;
            }
         } catch (Throwable var3) {
         }

         return false;
      }
   }

   private boolean yz() {
      if (this.sO != null) {
         return false;
      } else if (this.IU.isEmpty()) {
         return true;
      } else if (this.zs.player == null) {
         return false;
      } else {
         try {
            ItemStack var1 = this.zs.player.getMainHandStack();
            if (var1.getItem() instanceof BlockItem) {
               return true;
            }

            if (var1.isEmpty()) {
               return true;
            }

            ItemStack var2 = this.zs.player.getOffHandStack();
            if (var2.getItem() instanceof BlockItem) {
               return true;
            }
         } catch (Throwable var3) {
         }

         return true;
      }
   }

   private void yA(int var1, Object var2) {
      switch (var1) {
         case 0:
            this.xs();
            break;
         case 1:
            this.xt();
            break;
         case 2:
            this.xD((Send)var2);
            break;
         case 3:
            this.xE((Sent)var2);
            break;
         case 4:
            this.xK((Receive)var2);
            break;
         case 5:
            this.xM();
            break;
         case 6:
            this.xN((DoAttackEvent)var2);
            break;
         case 7:
            this.xO((DoItemUseEvent)var2);
            break;
         case 8:
            this.xP((StartBreakingBlockEvent)var2);
            break;
         case 9:
            this.initializeConnectionState();
            break;
         case 10:
            this.yh();
            break;
         default:
            throw new IllegalStateException(var1 + "");
      }
   }

   private final class CoordinatorListener implements IListener {
      private final Class<?> Cp;
      private final int Ec;
      private final int br;

      private CoordinatorListener(Class<?> var2, int var3, int var4) {
         this.Cp = var2;
         this.Ec = var3;
         this.br = var4;
      }

      public void call(Object var1) {
         GrimCombatCoordinator.this.yA(this.br, var1);
      }

      public Class<?> getTarget() {
         return this.Cp;
      }

      public int getPriority() {
         return this.Ec;
      }

      public boolean isStatic() {
         return false;
      }
   }

   interface Intent {
      GrimCombatCoordinator.Participant owner();

      void execute();
   }

   private static final class MiningPacketPass {
      private final PlayerActionC2SPacket kw;
      private boolean ms;

      private MiningPacketPass(PlayerActionC2SPacket var1) {
         this.kw = var1;
      }
   }

   interface Participant {
      GrimCombatCoordinator.Intent prepareCombatIntent();

      void suspendForItemUse();

      default boolean canFollowEmergencyOffhandSwap() {
         return false;
      }

      default boolean canYieldEpoch() {
         return false;
      }

      default boolean reservesLowerPriorityEpochs() {
         return false;
      }

      default void yieldToHigherPriority() {
         this.suspendForItemUse();
      }

      default boolean yieldToManualInput() {
         this.yieldToHigherPriority();
         return true;
      }

      default int combatPriority() {
         return 0;
      }
   }
}
