package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting.Builder;
import meteordevelopment.meteorclient.systems.friends.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

public final class TriggerBot extends AliceModule {
   private final SettingGroup eL = this.settings.getDefaultGroup();
   private final Setting<Boolean> up = this.eL
      .add(((Builder)((Builder)((Builder)new Builder().name("players-only")).description("chi danh thuc the nguoi choi")).defaultValue(true)).build());
   private final Setting<Boolean> Gu = this.eL
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("ignore-friends")).description("bo qua nguoi choi trong danh sach ban be cua Meteor"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> fk = this.eL
      .add(((Builder)((Builder)((Builder)new Builder().name("swing-hand")).description("hien thi hieu ung vung tay sau khi danh")).defaultValue(true)).build());

   public TriggerBot() {
      super(AliceAddon.PVP_CATEGORY, "TriggerBot", "Tu dong tan cong muc tieu hop le khi tam ngam nam truc tiep tren muc tieu.");
   }

   @AliceEventHandler
   private void KT(Post var1) {
      if (this.mc.player != null
         && this.mc.world != null
         && this.mc.interactionManager != null
         && this.mc.currentScreen == null
         && !this.mc.player.isSpectator()
         && this.mc.crosshairTarget instanceof EntityHitResult var2) {
         if (!this.KU()) {
            Entity var6 = var2.getEntity();
            int var10000 = var6 != this.mc.player ? 4462 : 4463;

            while (true) {
               int var5 = var10000;
               if (var5 == 4462) {
                  if (var6.isAlive() && !var6.isRemoved()) {
                     if (!(var6 instanceof PlayerEntity var4)) {
                        if ((Boolean)this.up.get()) {
                           return;
                        }
                     } else {
                        if (var4.isSpectator() || var4.isCreative()) {
                           return;
                        }

                        var10000 = !this.Gu.get() ? 6846 : 6845;

                        label62:
                        while (true) {
                           switch (var10000) {
                              case 6845:
                                 if (!Friends.get().shouldAttack(var4)) {
                                    return;
                                 }
                                 break label62;
                              case 6846:
                                 var10000 = 6844;
                                 break;
                              default:
                                 break label62;
                           }
                        }
                     }

                     if (this.mc.player.getAttackCooldownProgress(0.5F) + 1.0E-4F < 1.0F) {
                        return;
                     }

                     this.mc.interactionManager.attackEntity(this.mc.player, var6);
                     if ((Boolean)this.fk.get()) {
                        this.mc.player.swingHand(Hand.MAIN_HAND);
                     }

                     return;
                  }
                  break;
               }

               if (var5 != 4463) {
                  break;
               }

               var10000 = 4461;
            }
         }
      }
   }

   private boolean KU() {
      int var1 = GrimCombatCoordinator.wT();
      if (var1 >= 0) {
         if (var1 != this.mc.player.getInventory().getSelectedSlot()) {
            return true;
         }
      } else {
         char var2 = '\uef5b';
      }

      return false;
   }
}
