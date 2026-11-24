package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

public final class ShieldDisable extends AliceModule {
   private final SettingGroup dv = this.settings.getDefaultGroup();
   private final Setting<Integer> Eb = this.dv
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("hit-delay")).description("tick giua cac lan danh riu pha khien")).defaultValue(0))
            .min(0)
            .sliderMax(20)
            .build()
      );
   private final Setting<Integer> vW = this.dv
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("switch-delay")).description("tick cho sau khi chuyen sang riu")).defaultValue(0))
            .min(0)
            .sliderMax(20)
            .build()
      );
   private final Setting<Boolean> Ad = this.dv
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("switch-back"))
                  .description("quay ve slot cu khi muc tieu ngung dung khien"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> Fc = this.dv
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("ignore-friends"))
                  .description("bo qua nguoi choi trong danh sach ban be cua Meteor"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> Hb = this.dv
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("stun"))
                  .description("danh them 1 phat riu khi pha khien"))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> jj = this.dv
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("click-simulation"))
                  .description("chi chuyen sang riu va de cho click vat ly thuc hien cu danh"))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> bL = this.dv
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("hold-axe"))
                  .description("chi chay khi dang cam riu"))
               .defaultValue(false))
            .build()
      );
   private int FS = -1;
   private int rK;
   private int HZ;

   public ShieldDisable() {
      super(AliceAddon.PVP_CATEGORY, "ShieldDisable", "Tu dong chuyen sang riu va tan cong nguoi choi dang gio khien.");
   }

   @Override
   public void onActivate() {
      this.FS = -1;
      this.rK = (Integer)this.Eb.get();
      this.HZ = (Integer)this.vW.get();
      super.onActivate();
   }

   @Override
   public void onDeactivate() {
      this.DP(true);
      super.onDeactivate();
   }

   @AliceEventHandler
   private void DL(Post var1) {
      if (this.mc.player == null
         || this.mc.world == null
         || this.mc.interactionManager == null
         || this.mc.currentScreen != null
         || this.mc.player.isSpectator()) {
         this.DO();
      } else if (!this.DQ()) {
         if (!(this.mc.crosshairTarget instanceof EntityHitResult var2)) {
            this.DO();
         } else if (var2.getEntity() instanceof PlayerEntity var6 && var6.isBlocking()) {
            if (!var6.isSpectator() && !var6.isCreative()) {
               short var12;
               while (var12 == 31774) {
                  var12 = 31772;
               }

               if ((Boolean)this.Fc.get() && !Friends.get().shouldAttack(var6)) {
                  this.DO();
               } else if (this.mc.player.isUsingItem()) {
                  this.DO();
               } else if (!(Boolean)this.bL.get() || this.mc.player.getMainHandStack().getItem() instanceof AxeItem) {
                  if (this.FS == -1) {
                     this.FS = this.mc.player.getInventory().getSelectedSlot();
                  }

                  if (!(this.mc.player.getMainHandStack().getItem() instanceof AxeItem)) {
                     if (this.HZ > 0) {
                        this.HZ--;
                     } else {
                        int var7 = this.DN();
                        if (var7 != -1) {
                           InvUtils.swap(var7, false);
                           this.HZ = (Integer)this.vW.get();
                        } else {
                           this.DO();
                        }
                     }
                  } else {
                     int var10000 = this.HZ <= 0 ? 'ｿ' : 'ｾ';

                     while (true) {
                        switch (var10000) {
                           case 65406:
                              this.HZ--;
                              return;
                           case 65407:
                              var10000 = 65405;
                              break;
                           default:
                              if (this.rK > 0) {
                                 this.rK--;
                                 return;
                              }

                              if ((Boolean)this.jj.get()) {
                                 return;
                              }

                              this.DM(var6);
                              var10000 = !this.Hb.get() ? '锞' : '锝';

                              label92:
                              while (true) {
                                 switch (var10000) {
                                    case 38173:
                                       this.DM(var6);
                                       break label92;
                                    case 38174:
                                       var10000 = 38172;
                                       break;
                                    default:
                                       break label92;
                                 }
                              }

                              this.rK = (Integer)this.Eb.get();
                              this.HZ = (Integer)this.vW.get();
                              return;
                        }
                     }
                  }
               }
            } else {
               this.DO();
            }
         } else {
            this.DO();
         }
      }
   }

   private void DM(PlayerEntity var1) {
      this.mc.interactionManager.attackEntity(this.mc.player, var1);
      this.mc.player.swingHand(Hand.MAIN_HAND);
   }

   private int DN() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (this.mc.player.getInventory().getStack(var1).getItem() instanceof AxeItem) {
            return var1;
         }
      }

      return -1;
   }

   private void DO() {
      this.DP(false);
   }

   private void DP(boolean var1) {
      if (this.FS != -1 && this.mc.player != null && this.mc.interactionManager != null) {
         if (!var1 && this.DQ()) {
            return;
         }

         if ((Boolean)this.Ad.get()) {
            InvUtils.swap(this.FS, false);
         }
      }

      this.FS = -1;
      this.rK = (Integer)this.Eb.get();
      this.HZ = (Integer)this.vW.get();
   }

   private boolean DQ() {
      if (this.mc.player == null) {
         return false;
      } else {
         int var1 = GrimCombatCoordinator.wT();
         if (var1 >= 0) {
            int var10000 = var1 == this.mc.player.getInventory().getSelectedSlot() ? '褨' : '褧';

            while (true) {
               switch (var10000) {
                  case 35111:
                     return true;
                  case 35112:
                     var10000 = 35110;
                     break;
                  default:
                     return false;
               }
            }
         } else {
            return false;
         }
      }
   }
}
