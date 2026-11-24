package com.nnpg.alice.internal;

import meteordevelopment.meteorclient.utils.player.SlotUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;

final class InventoryRefillHelper {
   private static InventoryRefillHelper EE;
   private final MinecraftClient Jq = MinecraftClient.getInstance();
   private final Object uU;
   private InventoryRefillHelper.PendingRefill eE;
   private int iY = -1;
   private boolean ji;
   private ClientPlayerEntity Hw;
   private InventoryRefillHelper.RefillInput Al;

   InventoryRefillHelper(Object var1) {
      this.uU = var1;
   }

   static void yI() {
      InventoryRefillHelper var0 = EE;
      if (var0 != null) {
         var0.yQ();
      }
   }

   RefillOutcome yJ(Item var1, int var2) {
      return this.yN(var1, var2, false, false);
   }

   RefillOutcome yK(Item var1, int var2) {
      return this.yN(var1, var2, true, false);
   }

   RefillOutcome yL(Item var1, int var2) {
      return this.yN(var1, var2, true, true);
   }

   RefillOutcome yM(Item var1, int var2) {
      return this.yN(var1, var2, true, false);
   }

   private RefillOutcome yN(Item var1, int var2, boolean var3, boolean var4) {
      if (GrimCombatCoordinator.xf()) {
         this.yQ();
         return RefillOutcome.hz;
      } else if (this.Jq.player != null && var1 != null) {
         int var5 = var3 ? this.yX(var1, var2) : this.yW(var1);
         if (var5 >= 0) {
            this.yQ();
            return new RefillOutcome.Ready(var5);
         } else {
            int var6 = this.yY(var1);
            if (var6 < 0) {
               this.yQ();
               return RefillOutcome.td;
            } else if (var2 < 0 || var2 > 8) {
               return RefillOutcome.hz;
            } else if (!this.yV()) {
               this.yQ();
               return RefillOutcome.hz;
            } else {
               if (this.eE == null || this.eE.tM != var1 || this.eE.rl != var2 || this.eE.pj != var3 || this.eE.CN != var4) {
                  this.yQ();
                  this.eE = new InventoryRefillHelper.PendingRefill(var1, var2, var3, var4);
               }

               if (!var4) {
                  EasyPlace.ExternalInventoryWindowState var7 = EasyPlace.um(this.uU);
                  if (var7 == EasyPlace.ExternalInventoryWindowState.Ready) {
                     this.yU();
                     return RefillOutcome.hz;
                  } else if (var7 == EasyPlace.ExternalInventoryWindowState.Waiting) {
                     return RefillOutcome.hz;
                  } else {
                     int var10000 = this.yS() ? 2804 : 2803;

                     while (true) {
                        int var8 = var10000;
                        if (var8 == 2803) {
                           return RefillOutcome.hz;
                        }

                        if (var8 != 2804) {
                           if (this.eE.Be != InventoryRefillHelper.Phase.NeutralArmed && this.eE.Be != InventoryRefillHelper.Phase.NeutralApplied) {
                              if (this.eE.Be == InventoryRefillHelper.Phase.NeutralReady) {
                                 if (!this.eE.rd) {
                                    this.eE.Be = InventoryRefillHelper.Phase.NeutralArmed;
                                 } else {
                                    this.yU();
                                 }

                                 return RefillOutcome.hz;
                              }

                              this.eE.Be = InventoryRefillHelper.Phase.NeutralArmed;
                              return RefillOutcome.hz;
                           }

                           return RefillOutcome.hz;
                        }

                        var10000 = 2802;
                     }
                  }
               } else if (this.yZ()) {
                  this.yU();
                  return RefillOutcome.hz;
               } else {
                  return RefillOutcome.hz;
               }
            }
         }
      } else {
         return RefillOutcome.hz;
      }
   }

   void yO() {
      if (this.eE != null && EE == this && this.eE.Be == InventoryRefillHelper.Phase.NeutralArmed) {
         this.za();
      }
   }

   void yP() {
      this.ji = this.yZ();
      if (this.eE != null && this.eE.Be == InventoryRefillHelper.Phase.NeutralApplied) {
         boolean var1 = this.ji && this.Jq.player == this.Hw && this.Jq.player != null && this.Jq.player.input == this.Al;
         this.zb();
         this.Hw = null;
         this.eE.rd = var1;
         this.eE.Be = !var1 ? InventoryRefillHelper.Phase.NeutralArmed : InventoryRefillHelper.Phase.NeutralReady;
      }
   }

   void yQ() {
      this.iY = -1;
      EasyPlace.un(this.uU);
      this.zb();
      this.Hw = null;
      this.eE = null;
      this.yT();
   }

   int yR() {
      int var1 = this.iY;
      this.iY = -1;
      return var1;
   }

   private boolean yS() {
      if (EE != null && EE != this) {
         return false;
      } else {
         EE = this;
         return true;
      }
   }

   private void yT() {
      if (EE == this) {
         EE = null;
      }
   }

   private void yU() {
      InventoryRefillHelper.PendingRefill var1 = this.eE;
      if (var1 != null && !GrimCombatCoordinator.xf() && this.yV()) {
         int var2 = var1.pj ? this.yX(var1.tM, var1.rl) : this.yW(var1.tM);
         if (var2 >= 0) {
            this.yQ();
            this.iY = var2;
         } else {
            int var3 = this.yY(var1.tM);
            int var4 = SlotUtils.indexToId(var3);
            if (var3 >= 9
               && var3 < 36
               && var4 >= 0
               && var4 < this.Jq.player.playerScreenHandler.slots.size()
               && this.Jq.player.playerScreenHandler.getSlot(var4).getStack().isOf(var1.tM)) {
               int var5 = var1.rl;
               if (GrimCombatCoordinator.wY() || GrimCombatCoordinator.xb()) {
                  boolean var6 = false;

                  try {
                     this.Jq.interactionManager.clickSlot(this.Jq.player.playerScreenHandler.syncId, var4, var5, SlotActionType.SWAP, this.Jq.player);
                     var6 = true;
                  } finally {
                     if (!var6) {
                        GrimCombatCoordinator.xa();
                     }
                  }

                  this.yQ();
                  this.iY = var5;
               }
            } else {
               this.yQ();
            }
         }
      } else {
         this.yQ();
      }
   }

   private boolean yV() {
      return this.Jq.player != null
         && this.Jq.interactionManager != null
         && !this.Jq.player.isCreative()
         && !this.Jq.player.isSpectator()
         && this.Jq.currentScreen == null
         && this.Jq.player.currentScreenHandler == this.Jq.player.playerScreenHandler;
   }

   private int yW(Item var1) {
      if (this.Jq.player == null) {
         return -1;
      } else {
         for (int var2 = 0; var2 < 9; var2++) {
            if (this.Jq.player.getInventory().getStack(var2).isOf(var1)) {
               return var2;
            }
         }

         return -1;
      }
   }

   private int yX(Item var1, int var2) {
      if (this.Jq.player != null && var2 >= 0 && var2 <= 8) {
         return this.Jq.player.getInventory().getStack(var2).isOf(var1) ? var2 : -1;
      } else {
         return -1;
      }
   }

   private int yY(Item var1) {
      if (this.Jq.player == null) {
         return -1;
      } else {
         for (int var2 = 9; var2 < 36; var2++) {
            if (this.Jq.player.getInventory().getStack(var2).isOf(var1)) {
               return var2;
            }
         }

         return -1;
      }
   }

   private boolean yZ() {
      ClientPlayerEntity var1 = this.Jq.player;
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

   private void za() {
      ClientPlayerEntity var1 = this.Jq.player;
      if (var1 != null && !var1.hasVehicle()) {
         if (this.Al == null) {
            this.Al = new InventoryRefillHelper.RefillInput(var1.input);
            var1.input = this.Al;
         }
      }
   }

   private void zb() {
      if (this.Al != null) {
         DelegatingInput.qM(this.Jq.player, this.Al);
      }

      this.Al = null;
   }

   private static final class PendingRefill {
      private final Item tM;
      private final int rl;
      private final boolean pj;
      private final boolean CN;
      private InventoryRefillHelper.Phase Be = InventoryRefillHelper.Phase.Queued;
      private boolean rd;

      private PendingRefill(Item var1, int var2, boolean var3, boolean var4) {
         this.tM = var1;
         this.rl = var2;
         this.pj = var3;
         this.CN = var4;
      }
   }

   private static enum Phase {
      Queued,
      NeutralArmed,
      NeutralApplied,
      NeutralReady;
   }

   private final class RefillInput extends DelegatingInput {
      private RefillInput(Input var2) {
         super(var2);
      }

      @Override
      protected void qL() {
         if (InventoryRefillHelper.this.eE != null
            && InventoryRefillHelper.this.eE.Be == InventoryRefillHelper.Phase.NeutralArmed
            && InventoryRefillHelper.EE == InventoryRefillHelper.this
            && InventoryRefillHelper.this.Jq.player != null
            && InventoryRefillHelper.this.Jq.player.input == this) {
            PlayerInput var1 = this.qI().playerInput == null ? PlayerInput.DEFAULT : this.qI().playerInput;
            PlayerInput var2 = new PlayerInput(false, false, false, false, false, var1.sneak(), false);
            this.setState(var2, Vec2f.ZERO);
            InventoryRefillHelper.this.Jq.player.setSprinting(false);
            InventoryRefillHelper.this.Hw = InventoryRefillHelper.this.Jq.player;
            InventoryRefillHelper.this.eE.Be = InventoryRefillHelper.Phase.NeutralApplied;
         }
      }
   }
}
