package com.nnpg.alice.internal;

import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;

public abstract class DelegatingInput extends Input {
   private Input ik;
   private boolean EW;

   protected DelegatingInput(Input var1) {
      this.ik = var1;
      this.qN();
   }

   public final Input qI() {
      return this.ik;
   }

   final void setDelegate(Input var1) {
      this.ik = var1;
   }

   public final void qJ() {
      this.EW = true;
   }

   public final boolean qK() {
      return this.EW;
   }

   public void tick() {
      this.ik.tick();
      this.qN();
      if (!this.EW) {
         this.qL();
      }
   }

   protected void qL() {
   }

   public static boolean qM(ClientPlayerEntity var0, DelegatingInput var1) {
      if (var1 == null) {
         return false;
      } else {
         var1.qJ();
         if (var0 == null) {
            return false;
         } else if (var0.input != var1) {
            Input var2 = var0.input;

            while (var2 instanceof DelegatingInput) {
               DelegatingInput var3 = (DelegatingInput)var2;
               if (var3.qI() == var1) {
                  var3.setDelegate(var1.qI());
                  return true;
               }

               var2 = var3.qI();
            }

            return false;
         } else {
            var0.input = var1.qI();
            return true;
         }
      }
   }

   protected final void qN() {
      this.playerInput = this.ik.playerInput == null ? PlayerInput.DEFAULT : this.ik.playerInput;
      this.movementVector = this.ik.getMovementInput() == null ? Vec2f.ZERO : this.ik.getMovementInput();
   }

   protected final void setState(PlayerInput var1, Vec2f var2) {
      this.playerInput = var1 == null ? PlayerInput.DEFAULT : var1;
      this.movementVector = var2 == null ? Vec2f.ZERO : var2;
   }
}
