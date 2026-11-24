package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import net.minecraft.client.network.ClientPlayerEntity;

public final class FreezePlayer extends AliceModule {
   private ClientPlayerEntity yN;
   private double m;
   private double IV;
   private double rZ;

   public FreezePlayer() {
      super(AliceAddon.PVP_CATEGORY, "Freeze", "Giu nguoi choi tai vi tri hien tai khi module duoc bat.");
   }

   @Override
   public void onActivate() {
      this.yN = null;
      super.onActivate();
   }

   @Override
   public void onDeactivate() {
      if (this.mc.player != null) {
         this.mc.player.setVelocity(0.0, 0.0, 0.0);
      }

      this.yN = null;
      super.onDeactivate();
   }

   @AliceEventHandler
   private void wd(Pre var1) {
      ClientPlayerEntity var2 = this.mc.player;
      short var6;
      if (var2 != null) {
         if (this.mc.world != null) {
            if (this.yN != var2) {
               this.yN = var2;
               this.m = var2.getX();
               this.IV = var2.getY();
               this.rZ = var2.getZ();
               return;
            }

            var2.setVelocity(0.0, 0.0, 0.0);
            var2.setPosition(this.m, this.IV, this.rZ);
            return;
         }
      } else {
         while (var6 == 28882) {
            var6 = 28880;
         }
      }

      this.yN = null;
   }
}
