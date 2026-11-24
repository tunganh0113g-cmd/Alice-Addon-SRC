package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.util.Random;
import meteordevelopment.meteorclient.events.entity.player.PlayerTickMovementEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent.Post;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting.Builder;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class CrystalLegit extends AliceModule {
   private final SettingGroup kz = this.settings.getDefaultGroup();
   private final Setting<Boolean> IG = this.kz
      .add(((Builder)((Builder)((Builder)new Builder().name("sync")).description("cho server xac nhan truoc khi dat/pha")).defaultValue(false)).build());
   private final Setting<Double> nk = this.kz
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                  .name("min-cps"))
               .description("toc do pha cham nhat"))
            .defaultValue(8.0)
            .min(1.0)
            .max(20.0)
            .sliderRange(1.0, 20.0)
            .build()
      );
   private final Setting<Double> uY = this.kz
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                  .name("max-cps"))
               .description("toc do pha nhanh nhat"))
            .defaultValue(12.0)
            .min(1.0)
            .max(20.0)
            .sliderRange(1.0, 20.0)
            .build()
      );
   private final Setting<Boolean> kE = this.kz
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("place-obsidian")).description("dat obsidian truoc khi ban khong nhin vao obsidian hoac bedrock"))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> ey = this.kz
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("stop-on-kill")).description("tam dung khi co nguoi chet gan do de tranh lam no do"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> CB = this.kz
      .add(((Builder)((Builder)((Builder)new Builder().name("auto-refill-crystal")).description("tu dong bom crystal vao hotbar")).defaultValue(false)).build());
   private static final double iv = 10.0;
   private static final double so = 25.0;
   private final Random mP = new Random();
   private int zQ;
   private int rO;
   private int xE;
   private boolean uy;
   private long ku;
   private final InventoryRefillHelper ul = new InventoryRefillHelper(this);
   private int Ev = -1;

   public CrystalLegit() {
      super(AliceAddon.PVP_CATEGORY, "Crystal Macro", "dat va dap crystal khi ban giu chuot phai");
   }

   @Override
   public void onActivate() {
      super.onActivate();
      this.qs();
      this.qt();
   }

   @Override
   public void onDeactivate() {
      super.onDeactivate();
      this.qs();
   }

   private void qs() {
      this.zQ = 0;
      this.rO = 0;
      this.uy = false;
      this.ku = 0L;
      this.ul.yQ();
      this.Ev = -1;
   }

   private void qt() {
      double var1 = (Double)this.nk.get();
      double var3 = (Double)this.uY.get();
      if (var1 > var3) {
         double var5 = var1;
         var1 = var3;
         var3 = var5;
      }

      double var7 = var1 + (var3 - var1) * this.mP.nextDouble();
      this.xE = Math.max(1, (int)(20.0 / var7));
   }

   @AliceEventHandler(priority = 100)
   private void qu(PlayerTickMovementEvent var1) {
      this.ul.yO();
   }

   @AliceEventHandler(priority = -200)
   private void qv(Post var1) {
      this.ul.yP();
   }

   @AliceEventHandler
   private void qw(Receive var1) {
      if ((Boolean)this.ey.get()) {
         if (var1.packet instanceof EntityStatusS2CPacket var2) {
            if (var2.getStatus() == 3) {
               int var10000 = this.mc.world != null ? '냉' : '냊';

               while (true) {
                  int var4 = var10000;
                  if (var4 == 45257) {
                     if (this.mc.player != null) {
                        Entity var5 = var2.getEntity(this.mc.world);
                        if (var5 != null && var5 != this.mc.player) {
                           if (!(var5.distanceTo(this.mc.player) > 10.0)) {
                              this.ku = System.currentTimeMillis() + 1500L;
                              return;
                           }

                           return;
                        }

                        return;
                     }
                     break;
                  }

                  if (var4 != 45258) {
                     break;
                  }

                  var10000 = 45256;
               }
            }
         }
      }
   }

   @AliceEventHandler
   private void qx(Pre var1) {
      if (this.mc.player != null && this.mc.currentScreen == null) {
         this.qz();
         if ((Boolean)this.ey.get() && System.currentTimeMillis() < this.ku) {
            this.ul.yQ();
         } else if (!this.mc.player.isUsingItem()) {
            boolean var2 = this.mc.options.useKey.isPressed();
            if (!var2) {
               this.uy = false;
               this.rO = 0;
               this.ul.yQ();
            } else {
               if (!this.uy) {
                  this.uy = true;
                  this.rO = 0;
               }

               int var3 = this.qF(Items.END_CRYSTAL);
               if (var3 >= 0) {
                  this.Ev = var3;
               }

               if (!(Boolean)this.CB.get()) {
                  this.ul.yQ();
               } else if (var3 >= 0) {
                  this.ul.yQ();
               } else if (this.Ev < 0) {
                  this.ul.yQ();
               } else {
                  RefillOutcome var5 = this.ul.yJ(Items.END_CRYSTAL, this.Ev);
                  if (var5 == RefillOutcome.hz) {
                     return;
                  }

                  if (var5 instanceof RefillOutcome.Ready var6) {
                     this.Ev = var6.hotbarSlot();
                     InvUtils.swap(var6.hotbarSlot(), false);
                  }
               }

               Hand var4 = Hand.MAIN_HAND;
               if (this.mc.player.getOffHandStack().isOf(Items.END_CRYSTAL)) {
                  var4 = Hand.OFF_HAND;
               } else {
                  if (!this.mc.player.getMainHandStack().isOf(Items.END_CRYSTAL)) {
                     return;
                  }

                  var4 = Hand.MAIN_HAND;
               }

               HitResult var9 = this.mc.crosshairTarget;
               if (var9 instanceof EntityHitResult var10) {
                  boolean var7 = this.qC(var10.getEntity());
                  if (var7 && (Boolean)this.IG.get()) {
                     return;
                  }
               } else if (!(Boolean)this.IG.get()) {
                  EndCrystalEntity var12 = this.qy();
                  if (var12 != null) {
                     this.qC(var12);
                  }
               }

               if (var9 instanceof BlockHitResult var11) {
                  this.qA(var11, var4);
               }
            }
         }
      } else {
         this.ul.yQ();
      }
   }

   private EndCrystalEntity qy() {
      if (this.mc.world != null && this.mc.player != null) {
         EndCrystalEntity var1 = null;
         double var2 = Double.MAX_VALUE;

         for (Entity var5 : this.mc.world.getEntities()) {
            if (var5 instanceof EndCrystalEntity var6 && !var6.isRemoved()) {
               double var7 = this.mc.player.squaredDistanceTo(var6);
               if (var7 <= 25.0 && var7 < var2) {
                  var1 = var6;
                  var2 = var7;
               }
            }
         }

         return var1;
      } else {
         return null;
      }
   }

   private void qz() {
      if (this.zQ > 0) {
         this.zQ--;
      }

      if (this.rO > 0) {
         this.rO--;
      }
   }

   private void qA(BlockHitResult var1, Hand var2) {
      if (var1.getType() == Type.BLOCK) {
         BlockPos var3 = var1.getBlockPos();
         BlockState var4 = this.mc.world.getBlockState(var3);
         boolean var5 = var4.isOf(Blocks.OBSIDIAN) || var4.isOf(Blocks.BEDROCK);
         if (var5) {
            if (this.qD(var3)) {
               if (!this.qE(var3.up())) {
                  this.mc.interactionManager.interactBlock(this.mc.player, var2, var1);
                  this.mc.player.swingHand(var2);
               }
            }
         } else {
            if ((Boolean)this.kE.get() && this.rO <= 0) {
               this.qB(var1, var2);
            }
         }
      }
   }

   private void qB(BlockHitResult var1, Hand var2) {
      if (this.mc.player != null && this.mc.world != null) {
         int var3 = this.qF(Items.OBSIDIAN);
         if (var3 != -1) {
            InvUtils.swap(var3, false);
            this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, var1);
            this.mc.player.swingHand(Hand.MAIN_HAND);
            if (var2 == Hand.MAIN_HAND) {
               int var4 = this.qF(Items.END_CRYSTAL);
               if (var4 != -1) {
                  InvUtils.swap(var4, false);
               }
            }

            BlockPos var6 = var1.getBlockPos().offset(var1.getSide());
            if (this.qD(var6) && !this.qE(var6.up())) {
               BlockHitResult var5 = new BlockHitResult(new Vec3d(var6.getX() + 0.5, var6.getY() + 1.0, var6.getZ() + 0.5), Direction.UP, var6, false);
               this.mc.interactionManager.interactBlock(this.mc.player, var2, var5);
               this.mc.player.swingHand(var2);
            }

            this.rO = 6;
         }
      }
   }

   private boolean qC(Entity var1) {
      if (this.zQ > 0) {
         return false;
      } else if (this.mc.player == null || this.mc.interactionManager == null) {
         return false;
      } else if (var1 == null || var1.isRemoved() || !var1.isAlive()) {
         return false;
      } else if (!(var1 instanceof EndCrystalEntity) && !(var1 instanceof SlimeEntity)) {
         return false;
      } else if (this.qE(var1.getBlockPos())) {
         return false;
      } else {
         this.mc.interactionManager.attackEntity(this.mc.player, var1);
         this.mc.player.swingHand(Hand.MAIN_HAND);
         this.zQ = this.xE;
         this.qt();
         return true;
      }
   }

   private boolean qD(BlockPos var1) {
      if (this.mc.world == null) {
         return false;
      } else {
         BlockPos var2 = var1.up();
         int var10000 = this.mc.world.isAir(var2) ? '멻' : '멺';

         while (true) {
            int var6 = var10000;
            if (var6 == 47738) {
               return false;
            }

            if (var6 != 47739) {
               if ((Boolean)this.IG.get()) {
                  int var3 = var2.getX();
                  int var4 = var2.getY();
                  int var5 = var2.getZ();
                  return this.mc.world.getOtherEntities(null, new Box(var3, var4, var5, var3 + 1.0, var4 + 2.0, var5 + 1.0)).isEmpty();
               }

               return true;
            }

            var10000 = 47737;
         }
      }
   }

   private boolean qE(BlockPos var1) {
      if (this.mc.world != null && var1 != null) {
         Box var2 = new Box(var1.getX() - 10.0, var1.getY() - 10.0, var1.getZ() - 10.0, var1.getX() + 10.0, var1.getY() + 10.0, var1.getZ() + 10.0);

         for (Entity var4 : this.mc.world.getOtherEntities(null, var2)) {
            if (var4 instanceof ItemEntity var5) {
               ItemStack var6 = var5.getStack();
               if (!var6.isEmpty()) {
                  if (var6.isOf(Items.ELYTRA)) {
                     return true;
                  }

                  short var7 = 17635;
                  if (var6.isIn(ItemTags.SWORDS)) {
                     return true;
                  }

                  if (var6.isIn(ItemTags.HEAD_ARMOR) || var6.isIn(ItemTags.CHEST_ARMOR) || var6.isIn(ItemTags.LEG_ARMOR) || var6.isIn(ItemTags.FOOT_ARMOR)) {
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private int qF(Item var1) {
      if (this.mc.player == null) {
         return -1;
      } else {
         for (int var2 = 0; var2 < 9; var2++) {
            if (this.mc.player.getInventory().getStack(var2).isOf(var1)) {
               return var2;
            }
         }

         return -1;
      }
   }
}
