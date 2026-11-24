package com.nnpg.alice.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class GuiCycleController {
   private GuiCycleController() {
   }

   static GuiCycleController.PageKind Ft(List<GuiCycleController.SlotView> var0, Set<?> var1) {
      for (GuiCycleController.SlotView var3 : var0) {
         if (var3.count() > 0 && !var1.contains(var3.item())) {
            return GuiCycleController.PageKind.Junk;
         }
      }

      return GuiCycleController.PageKind.SelectedOrEmpty;
   }

   static GuiCycleController.PendingAccounting Fu(List<GuiCycleController.SlotView> var0) {
      return new GuiCycleController.PendingAccounting(var0);
   }

   static float Fv(float var0, float var1) {
      float var2 = (var1 - var0) % 360.0F;
      if (var2 >= 180.0F) {
         var2 -= 360.0F;
      }

      if (var2 < -180.0F) {
         var2 += 360.0F;
      }

      return var0 + var2;
   }

   static GuiCycleController.CyclePhase Fw(GuiCycleController.CyclePhase var0) {
      return var0 == GuiCycleController.CyclePhase.DropAll ? GuiCycleController.CyclePhase.SellAll : GuiCycleController.CyclePhase.DropAll;
   }

   static GuiCycleController.GuiAction Fx(GuiCycleController.CyclePhase var0, GuiCycleController.PageKind var1) {
      if (var1 != GuiCycleController.PageKind.Junk) {
         return var0 != GuiCycleController.CyclePhase.DropAll ? GuiCycleController.GuiAction.SellAll : GuiCycleController.GuiAction.DropAll;
      } else {
         return GuiCycleController.GuiAction.StartJunkDrop;
      }
   }

   record ConfirmedDelta(long items, long stacks) {
      static final GuiCycleController.ConfirmedDelta mH = new GuiCycleController.ConfirmedDelta(0L, 0L);
   }

   static enum CyclePhase {
      DropAll,
      SellAll;
   }

   static enum GuiAction {
      DropAll,
      SellAll,
      StartJunkDrop;
   }

   static enum PageKind {
      SelectedOrEmpty,
      Junk;
   }

   static final class PendingAccounting {
      private final Map<Integer, GuiCycleController.SlotView> tg = new HashMap<>();
      private final Map<Integer, Integer> qe = new HashMap<>();
      private final Set<Integer> pC = new HashSet<>();

      private PendingAccounting(List<GuiCycleController.SlotView> var1) {
         for (GuiCycleController.SlotView var3 : var1) {
            if (var3.count() > 0) {
               this.tg.put(var3.slot(), var3);
            }
         }
      }

      GuiCycleController.ConfirmedDelta Fo(List<GuiCycleController.SlotView> var1) {
         HashMap var2 = new HashMap();

         for (GuiCycleController.SlotView var4 : var1) {
            var2.put(var4.slot(), var4);
         }

         long var13 = 0L;
         long var5 = 0L;

         for (GuiCycleController.SlotView var8 : this.tg.values()) {
            GuiCycleController.SlotView var9 = (GuiCycleController.SlotView)var2.get(var8.slot());
            int var10 = var9 != null && var9.item() == var8.item() ? Math.max(0, var9.count()) : 0;
            int var11 = Math.max(0, Math.min(var8.count(), var8.count() - var10));
            int var12 = this.qe.getOrDefault(var8.slot(), 0);
            if (var11 > var12) {
               var13 += var11 - var12;
               this.qe.put(var8.slot(), var11);
               if (this.pC.add(var8.slot())) {
                  var5++;
               }
            }
         }

         return var13 == 0L && var5 == 0L ? GuiCycleController.ConfirmedDelta.mH : new GuiCycleController.ConfirmedDelta(var13, var5);
      }

      GuiCycleController.ConfirmedDelta Fp(GuiCycleController.SlotView var1) {
         GuiCycleController.SlotView var2 = this.tg.get(var1.slot());
         if (var2 == null) {
            return GuiCycleController.ConfirmedDelta.mH;
         } else {
            int var3 = var1.item() != var2.item() ? 0 : Math.max(0, var1.count());
            int var4 = Math.max(0, Math.min(var2.count(), var2.count() - var3));
            int var5 = this.qe.getOrDefault(var2.slot(), 0);
            if (var4 <= var5) {
               return GuiCycleController.ConfirmedDelta.mH;
            } else {
               this.qe.put(var2.slot(), var4);
               long var6 = this.pC.add(var2.slot()) ? 1L : 0L;
               return new GuiCycleController.ConfirmedDelta(var4 - var5, var6);
            }
         }
      }
   }

   record SlotView(int slot, Object item, int count) {
   }
}
