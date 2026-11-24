package com.nnpg.alice.internal;

import java.util.List;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Sent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

final class CrystalBreakController {
   private static final MinecraftClient zP = MinecraftClient.getInstance();
   private static final double sY = 3.0;
   private static final long Ge = 250000000L;
   private final GrimCombatCoordinator.Participant xN;
   private final TargetTracker yg = new TargetTracker();
   private CrystalBreakController.RotationIntent HH;
   private CrystalBreakController.BreakRotationInput nT;
   private float lO;
   private float kR;
   private boolean ts;
   private int CZ = -1;
   private long qq;

   CrystalBreakController(GrimCombatCoordinator.Participant var1) {
      this.xN = var1;
   }

   GrimCombatCoordinator.Intent wj(final List<BlockPos> var1) {
      final EndCrystalEntity var2 = this.wq(var1);
      if (var2 == null) {
         this.yg.zi();
         this.HH = null;
         return null;
      } else {
         final int var3 = var2.getId();
         return new GrimCombatCoordinator.Intent() {
            @Override
            public GrimCombatCoordinator.Participant owner() {
               return CrystalBreakController.this.xN;
            }

            @Override
            public void execute() {
               CrystalBreakController.this.wo(var2, var1, var3);
            }
         };
      }
   }

   void wk() {
      CrystalBreakController.RotationIntent var1 = this.HH;
      this.HH = null;
      ClientPlayerEntity var2 = zP.player;
      if (var1 != null && var2 != null) {
         int var10000 = var2.hasVehicle() ? '\ue929' : '\ue928';

         while (true) {
            int var3 = var10000;
            if (var3 == 59688) {
               if (var2.input != null) {
                  this.ww();
                  this.nT = new CrystalBreakController.BreakRotationInput(var2.input, var2, var1);
                  var2.input = this.nT;
                  return;
               }
               break;
            }

            if (var3 != 59689) {
               break;
            }

            var10000 = 59687;
         }
      }
   }

   void wl() {
      this.ww();
      this.yg.zf();
   }

   void wm(Sent var1) {
      if (var1.packet instanceof PlayerMoveC2SPacket var2 && var2.changesLook()) {
         this.lO = var2.getYaw(this.lO);
         this.kR = var2.getPitch(this.kR);
         this.ts = true;
         if (this.nT != null && this.nT.H) {
            GrimCombatCoordinator.wN(this.xN, this.lO, this.kR);
         }
      }
   }

   void wn() {
      this.ww();
      this.HH = null;
      this.yg.zi();
      this.CZ = -1;
      this.qq = 0L;
   }

   private void wo(EndCrystalEntity var1, List<BlockPos> var2, int var3) {
      if (!this.wr(var1, var2)) {
         this.yg.zi();
      } else if (this.CZ != var3 || System.nanoTime() >= this.qq) {
         Vec3d var4 = var1.getBoundingBox().getCenter();
         double[] var5 = this.wv(var4);
         if (this.wp(var1, var5[0], var5[1])) {
            if (GrimCombatCoordinator.wO(this.xN)) {
               zP.interactionManager.attackEntity(zP.player, var1);
               zP.player.swingHand(Hand.MAIN_HAND);
               this.CZ = var3;
               this.qq = System.nanoTime() + 250000000L;
               this.yg.zi();
            }
         }
      }
   }

   private boolean wp(EndCrystalEntity var1, double var2, double var4) {
      if (this.wu(var1)) {
         return true;
      } else {
         this.HH = new CrystalBreakController.RotationIntent((float)var2, (float)var4);
         if (!this.yg.zh(var1.getId())) {
            return false;
         } else {
            boolean var6 = this.wu(var1);
            if (!var6) {
               this.yg.zi();
            }

            return var6;
         }
      }
   }

   private EndCrystalEntity wq(List<BlockPos> var1) {
      if (zP.player != null && zP.world != null && var1 != null && !var1.isEmpty()) {
         EndCrystalEntity var2 = null;
         double var3 = Double.MAX_VALUE;

         for (Entity var6 : zP.world.getEntities()) {
            if (var6 instanceof EndCrystalEntity var7 && var7.isAlive() && !var7.isRemoved() && this.wt(var7.getBoundingBox()) && ws(var7, var1)) {
               double var8 = zP.player.getEyePos().squaredDistanceTo(var7.getBoundingBox().getCenter());
               if (var8 < var3) {
                  var2 = var7;
                  var3 = var8;
               }
            }
         }

         return var2;
      } else {
         return null;
      }
   }

   private boolean wr(EndCrystalEntity var1, List<BlockPos> var2) {
      return var1 != null && var1.isAlive() && !var1.isRemoved() && this.wt(var1.getBoundingBox()) && ws(var1, var2);
   }

   private static boolean ws(EndCrystalEntity var0, List<BlockPos> var1) {
      Box var2 = var0.getBoundingBox();

      for (BlockPos var4 : var1) {
         if (new Box(var4).intersects(var2)) {
            return true;
         }
      }

      return false;
   }

   private boolean wt(Box var1) {
      Vec3d var2 = zP.player.getEyePos();
      double var3 = wx(var2.x, var1.minX, var1.maxX);
      double var5 = wx(var2.y, var1.minY, var1.maxY);
      double var7 = wx(var2.z, var1.minZ, var1.maxZ);
      return var3 * var3 + var5 * var5 + var7 * var7 <= 9.0;
   }

   private boolean wu(EndCrystalEntity var1) {
      if (this.ts && zP.player != null && var1.isAlive()) {
         Vec3d var2 = zP.player.getEyePos();
         Vec3d var3 = var2.add(Vec3d.fromPolar(this.kR, this.lO).multiply(3.0));
         Box var4 = var1.getBoundingBox();
         return var4.contains(var2) || var4.raycast(var2, var3).isPresent();
      } else {
         return false;
      }
   }

   private double[] wv(Vec3d var1) {
      Vec3d var2 = var1.subtract(zP.player.getEyePos());
      double var3 = Math.sqrt(var2.x * var2.x + var2.z * var2.z);
      double var5 = Math.toDegrees(Math.atan2(var2.z, var2.x)) - 90.0;
      double var7 = -Math.toDegrees(Math.atan2(var2.y, var3));
      return new double[]{var5, MathHelper.clamp(var7, -90.0, 90.0)};
   }

   private void ww() {
      CrystalBreakController.BreakRotationInput var1 = this.nT;
      if (var1 != null) {
         var1.wg();
         DelegatingInput.qM(zP.player, var1);
         this.nT = null;
      }
   }

   private static double wx(double var0, double var2, double var4) {
      if (!(var0 < var2)) {
         return var0 > var4 ? var0 - var4 : 0.0;
      } else {
         return var2 - var0;
      }
   }

   private static Vec2f wy(PlayerInput var0) {
      float var1 = (var0.left() ? 1.0F : 0.0F) - (var0.right() ? 1.0F : 0.0F);
      float var2 = (var0.forward() ? 1.0F : 0.0F) - (!var0.backward() ? 0.0F : 1.0F);
      return new Vec2f(var1, var2).normalize();
   }

   private final class BreakRotationInput extends DelegatingInput {
      private final ClientPlayerEntity pl;
      private final CrystalBreakController.RotationIntent AV;
      private boolean H;
      private float ek;
      private float ls;

      private BreakRotationInput(Input var2, ClientPlayerEntity var3, CrystalBreakController.RotationIntent var4) {
         super(var2);
         this.pl = var3;
         this.AV = var4;
      }

      @Override
      protected void qL() {
         if (CrystalBreakController.zP.player == this.pl && CrystalBreakController.zP.player.input == this) {
            PlayerInput var1 = this.qI().playerInput == null ? PlayerInput.DEFAULT : this.qI().playerInput;
            Vec2f var2 = this.qI().getMovementInput();
            this.ek = this.pl.getYaw();
            this.ls = this.pl.getPitch();
            float var3 = GrimCombatCoordinator.wS() ? GrimCombatCoordinator.wQ() : this.ek;
            float var4 = CrystalPlaceController.AA(this.AV.yaw, var3);
            PlayerInput var5 = CrystalAuraPlusPlus.oR(var1, var2, this.ek, var4);
            this.setState(var5, CrystalBreakController.wy(var5));
            this.pl.setYaw(var4);
            this.pl.setPitch(this.AV.pitch);
            this.H = true;
         }
      }

      private void wg() {
         if (this.H) {
            int var10000 = CrystalBreakController.zP.player != this.pl ? 16343 : 16344;

            while (true) {
               int var1 = var10000;
               if (var1 == 16343) {
                  break;
               }

               if (var1 != 16344) {
                  this.pl.setYaw(this.ek);
                  this.pl.setPitch(this.ls);
                  this.H = false;
                  return;
               }

               var10000 = 16342;
            }
         }
      }
   }

   private record RotationIntent(float yaw, float pitch) {
   }
}
