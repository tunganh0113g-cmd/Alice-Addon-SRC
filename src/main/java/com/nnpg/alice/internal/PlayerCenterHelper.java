package com.nnpg.alice.internal;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

final class PlayerCenterHelper {
   private static final double GC = 0.15;
   private static final double vR = 0.199;
   private static final double CV = 0.03;
   private static final double fa = 1.2;
   private static final double Iv = 0.45;
   private static final double xe = 0.2;
   private static final double wB = 0.3827;
   private static final int us = 20;
   private final MinecraftClient zo = MinecraftClient.getInstance();
   private PlayerCenterHelper.CenterInput HD;
   private boolean cw;
   private boolean jW;
   private boolean si;
   private int pI;
   private boolean oD;
   private double JA;
   private double fS;
   private double Gb;
   private double fb;
   private boolean dW;

   void wz() {
      this.wF();
      this.cw = false;
      this.jW = false;
      this.si = false;
      this.oD = false;
      this.pI = 0;
      this.dW = false;
   }

   boolean wA() {
      return this.jW;
   }

   boolean wB() {
      return this.cw && !this.jW && this.pI < 20;
   }

   void wC(boolean var1) {
      this.oD = false;
      ClientPlayerEntity var2 = this.zo.player;
      if (var1 && var2 != null && this.zo.world != null && var2.isAlive() && !var2.hasVehicle()) {
         this.cw = true;
         Vec3d var3 = var2.getEntityPos();
         double var4 = this.dW ? var3.getX() - this.Gb : 0.0;
         double var6 = !this.dW ? 0.0 : var3.getZ() - this.fb;
         this.Gb = var3.getX();
         this.fb = var3.getZ();
         this.dW = true;
         double var8 = Math.floor(var3.getX()) + 0.5 - var3.getX();
         double var10 = Math.floor(var3.getZ()) + 0.5 - var3.getZ();
         double var12 = Math.max(Math.abs(var8), Math.abs(var10));
         if (this.jW) {
            if (var12 > 0.199) {
               this.jW = false;
            }
         } else if (var12 <= 0.15) {
            this.jW = true;
         }

         if (!this.jW) {
            if (this.pI < 20) {
               this.pI++;
            }

            this.JA = var8 - var4 * 1.2;
            this.fS = var10 - var6 * 1.2;
            if (!(Math.sqrt(this.JA * this.JA + this.fS * this.fS) < 0.03)) {
               double var14 = Math.sqrt(var8 * var8 + var10 * var10);
               if (this.si) {
                  if (var14 < 0.2) {
                     this.si = false;
                  }
               } else if (var14 >= 0.45) {
                  this.si = true;
               }

               this.oD = true;
            } else {
               this.si = false;
            }
         } else {
            this.si = false;
            this.pI = 0;
         }
      } else {
         this.cw = false;
         this.jW = false;
         this.si = false;
         this.pI = 0;
         this.dW = false;
      }
   }

   void wD() {
      if (this.oD && this.HD == null) {
         ClientPlayerEntity var1 = this.zo.player;
         if (var1 != null) {
            if (var1.input != null) {
               if (!var1.hasVehicle()) {
                  this.HD = new PlayerCenterHelper.CenterInput(var1.input, var1);
                  var1.input = this.HD;
                  return;
               }
            } else {
               short var2 = 29050;
            }
         }
      }
   }

   void wE() {
      this.wF();
   }

   private void wF() {
      PlayerCenterHelper.CenterInput var1 = this.HD;
      if (var1 != null) {
         this.HD = null;
         DelegatingInput.qM(this.zo.player, var1);
      }
   }

   private final class CenterInput extends DelegatingInput {
      private final ClientPlayerEntity mi;

      private CenterInput(Input var2, ClientPlayerEntity var3) {
         super(var2);
         this.mi = var3;
      }

      @Override
      protected void qL() {
         if (PlayerCenterHelper.this.zo.player == this.mi) {
            PlayerInput var1 = this.qI().playerInput == null ? PlayerInput.DEFAULT : this.qI().playerInput;
            if (!var1.forward() && !var1.backward() && !var1.left() && !var1.right()) {
               float var2 = this.mi.getYaw() * (float) (Math.PI / 180.0);
               double var3 = MathHelper.cos(var2);
               double var5 = MathHelper.sin(var2);
               double var7 = PlayerCenterHelper.this.JA * var3 + PlayerCenterHelper.this.fS * var5;
               double var9 = PlayerCenterHelper.this.fS * var3 - PlayerCenterHelper.this.JA * var5;
               double var11 = Math.sqrt(var7 * var7 + var9 * var9);
               if (!(var11 < 1.0E-5)) {
                  double var13 = 0.3827 * var11;
                  boolean var15 = var9 >= var13;
                  boolean var16 = var9 <= -var13;
                  boolean var17 = var7 >= var13;
                  boolean var18 = var7 <= -var13;
                  boolean var19 = PlayerCenterHelper.this.si && var15 && !var1.sneak();
                  PlayerInput var20 = new PlayerInput(var15, var16, var17, var18, var1.jump(), var1.sneak(), var19);
                  this.setState(var20, CrystalPlaceController.Az(var20));
               }
            }
         }
      }
   }
}
