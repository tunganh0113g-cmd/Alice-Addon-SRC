package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.util.ArrayList;
import meteordevelopment.meteorclient.events.entity.player.PlayerTickMovementEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent.Post;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Sent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.EnumSetting.Builder;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class KillAuraPlusPlus extends AliceModule implements GrimCombatCoordinator.Participant {
   private static final double Ic = 1.0E-4;
   private static final int rj = 2;
   private final SettingGroup eu = this.settings.getDefaultGroup();
   private final SettingGroup zc = this.settings.createGroup("Targeting");
   private final SettingGroup uJ = this.settings.createGroup("Rotation");
   private final SettingGroup zS = this.settings.createGroup("Pause");
   private final Setting<KillAuraPlusPlus.WeaponSwapMode> rC = this.eu
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("weapon-swap"))
                  .description("none: danh bang do dang cam, pick: cam do ngon nhat, silent: chi doi slot tren server"))
               .defaultValue(KillAuraPlusPlus.WeaponSwapMode.None))
            .build()
      );
   private final Setting<KillAuraPlusPlus.RaycastMode> gJ = this.eu
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("raycast-mode"))
                  .description("grim chi yeu cau tia chieu vao hitbox cua thuc: the. visible con yeu cau tia khong bi chan boi the gioi"))
               .defaultValue(KillAuraPlusPlus.RaycastMode.Grim))
            .build()
      );
   private final Setting<Double> FH = this.zc
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                  .name("attack-range"))
               .description("gioi han tam danh cuc bo ap dung cung voi he thong cua minecraft 1.21.11, khong bu tru"))
            .defaultValue(3.0)
            .range(1.0, 6.0)
            .sliderRange(1.0, 6.0)
            .build()
      );
   private final Setting<KillAuraPlusPlus.TargetMode> Bo = this.zc
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("target-mode")).description("chon mot che do uu tien danh muc tieu nhat dinh"))
               .defaultValue(KillAuraPlusPlus.TargetMode.LowestHealth))
            .build()
      );
   private final Setting<Boolean> oe = this.zc
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("lock-target"))
                  .description("giu nguyen muc tieu da chon cho den khi khong con hop le"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Double> aW = this.uJ
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                  .name("yaw-step"))
               .description("goc yaw thay doi toi da tren server moi chu ky"))
            .defaultValue(180.0)
            .range(1.0, 180.0)
            .sliderRange(1.0, 180.0)
            .build()
      );
   private final Setting<Double> bl = this.uJ
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                  .name("pitch-step"))
               .description("goc pitch thay doi toi da tren server moi chu ky"))
            .defaultValue(90.0)
            .range(1.0, 90.0)
            .sliderRange(1.0, 90.0)
            .build()
      );
   private final Setting<Boolean> vu = this.zS
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("pause-on-use"))
                  .description("tam dung khi dang dung item de tranh xung dot packet"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> mU = this.zS
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("pause-on-mine"))
                  .description("tam dung khi giu nut danh de tranh xung dot"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Double> aa = this.eu
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                  .name("cooldown-threshold"))
               .description("Nguong cooldown theo attack speed cua vu khi. 0.85 mac dinh; 1.0 la day du, gia tri thap hon tang DPS nhung de bi Grim chu y hon."))
            .defaultValue(0.85)
            .range(0.5, 1.0)
            .sliderRange(0.5, 1.0)
            .build()
      );
   private final Setting<Double> gn = this.zc
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                  .name("walls-range"))
               .description("tam danh xuyen tuong toi da (gioi han truoc khi vao raycast gate)"))
            .defaultValue(3.0)
            .range(0.0, 6.0)
            .sliderRange(0.0, 6.0)
            .build()
      );
   private final TargetTracker gg = new TargetTracker();
   private PlayerEntity tp;
   private int gQ;
   private float tW;
   private float bd;
   private boolean Bi;
   private KillAuraPlusPlus.RotationIntent jQ;
   private KillAuraPlusPlus.AuraRotationInput ar;

   public KillAuraPlusPlus() {
      super(AliceAddon.PVP_CATEGORY, "kill-aura++", "Tu dong tan cong theo hoi chieu phia may khach voi tam danh, goc xoay va phoi hop Crystal Aura++.");
   }

   @Override
   public void onActivate() {
      this.BU();
      GrimCombatCoordinator.wG(this);
      super.onActivate();
   }

   @Override
   public void onDeactivate() {
      this.BS();
      GrimCombatCoordinator.wH(this);
      this.BU();
      super.onDeactivate();
   }

   @AliceEventHandler(priority = 100)
   private void Bv(PlayerTickMovementEvent var1) {
      this.BR();
   }

   @AliceEventHandler(priority = -200)
   private void Bw(Post var1) {
      this.BS();
      this.gg.zf();
   }

   @AliceEventHandler(priority = -1000)
   private void Bx(Sent var1) {
      if (var1.packet instanceof PlayerMoveC2SPacket var2 && var2.changesLook()) {
         this.tW = var2.getYaw(this.tW);
         this.bd = var2.getPitch(this.bd);
         this.Bi = true;
         if (this.ar != null && this.ar.vh) {
            GrimCombatCoordinator.wN(this, this.tW, this.bd);
         }
      }
   }

   @Override
   public GrimCombatCoordinator.Intent prepareCombatIntent() {
      this.BS();
      this.jQ = null;
      if (this.rC.get() != KillAuraPlusPlus.WeaponSwapMode.Silent && GrimCombatCoordinator.wM(this)) {
         return this.BB();
      } else if (!this.BD()) {
         this.BT();
         return !GrimCombatCoordinator.wM(this) ? null : this.BB();
      } else {
         this.tp = this.BE();
         if (this.tp == null) {
            this.gg.zi();
            return GrimCombatCoordinator.wM(this) ? this.BB() : null;
         } else {
            int var1 = this.BJ(this.tp);
            if (!this.BK(var1)) {
               if (this.BW() + 1.0E-4F < ((Double)this.aa.get()).floatValue()) {
                  this.gg.zi();
                  return null;
               } else {
                  Vec3d var2 = this.tp.getBoundingBox().getCenter();
                  double[] var3 = Ca(var2);
                  return this.BA(this.tp, var1, var3[0], var3[1]);
               }
            } else {
               return this.Bz(var1);
            }
         }
      }
   }

   @Override
   public void suspendForItemUse() {
      this.BS();
      this.jQ = null;
      this.gg.zi();
   }

   @Override
   public boolean canYieldEpoch() {
      if (this.mc.player == null) {
         return false;
      } else if (!(this.BW() + 1.0E-4F < ((Double)this.aa.get()).floatValue())) {
         if (!this.By()) {
            if (this.gQ >= 2) {
               this.gQ = 0;
               return false;
            } else {
               this.gQ++;
               return true;
            }
         } else {
            this.gQ = 0;
            return false;
         }
      } else {
         this.gQ = 0;
         return true;
      }
   }

   private boolean By() {
      if (!this.BD()) {
         return false;
      } else {
         PlayerEntity var1 = this.BE();
         if (var1 == null) {
            return false;
         } else {
            return this.BK(this.BJ(var1)) ? false : this.BP(var1);
         }
      }
   }

   @Override
   public int combatPriority() {
      return 150;
   }

   private GrimCombatCoordinator.Intent Bz(final int var1) {
      return new GrimCombatCoordinator.Intent() {
         @Override
         public GrimCombatCoordinator.Participant owner() {
            return KillAuraPlusPlus.this;
         }

         @Override
         public void execute() {
            KillAuraPlusPlus.this.BL(var1);
         }
      };
   }

   private GrimCombatCoordinator.Intent BA(final PlayerEntity var1, final int var2, final double var3, final double var5) {
      return new GrimCombatCoordinator.Intent() {
         @Override
         public GrimCombatCoordinator.Participant owner() {
            return KillAuraPlusPlus.this;
         }

         @Override
         public void execute() {
            KillAuraPlusPlus.this.BC(var1, var2, var3, var5);
         }
      };
   }

   private GrimCombatCoordinator.Intent BB() {
      return new GrimCombatCoordinator.Intent() {
         @Override
         public GrimCombatCoordinator.Participant owner() {
            return KillAuraPlusPlus.this;
         }

         @Override
         public void execute() {
            GrimCombatCoordinator.wJ(KillAuraPlusPlus.this);
         }
      };
   }

   private void BC(PlayerEntity var1, int var2, double var3, double var5) {
      if (this.tp == var1 && this.BF(var1) && !this.BK(var2) && !(this.BW() + 1.0E-4F < ((Double)this.aa.get()).floatValue())) {
         char var12;
         while (var12 == '뺼') {
            var12 = '뺺';
         }

         int var10000 = this.BM(var1, var3, var5) ? '꒦' : '꒥';

         while (true) {
            int var8 = var10000;
            if (var8 == 42149) {
               return;
            }

            if (var8 != 42150) {
               if (!GrimCombatCoordinator.wO(this)) {
                  return;
               }

               this.mc.interactionManager.attackEntity(this.mc.player, var1);
               this.mc.player.swingHand(Hand.MAIN_HAND);
               return;
            }

            var10000 = 42148;
         }
      } else {
         this.gg.zi();
      }
   }

   private boolean BD() {
      if (this.mc.player != null
         && this.mc.world != null
         && this.mc.interactionManager != null
         && this.mc.getNetworkHandler() != null
         && this.mc.player.isAlive()
         && !this.mc.player.isCreative()
         && !this.mc.player.isSpectator()
         && !this.mc.player.hasVehicle()
         && !this.mc.player.isGliding()
         && !this.mc.player.getAbilities().flying) {
         return !this.vu.get() || !this.mc.player.isUsingItem() && (this.mc.currentScreen != null || !this.mc.options.useKey.isPressed())
            ? !(Boolean)this.mU.get() || this.mc.currentScreen != null || !this.mc.options.attackKey.isPressed()
            : false;
      } else {
         return false;
      }
   }

   private PlayerEntity BE() {
      if ((Boolean)this.oe.get() && this.BF(this.tp)) {
         return this.tp;
      } else {
         ArrayList var1 = new ArrayList();

         for (PlayerEntity var3 : this.mc.world.getPlayers()) {
            if (this.BF(var3)) {
               var1.add(var3);
            }
         }

         var1.sort(this::BG);
         return !var1.isEmpty() ? (PlayerEntity)var1.getFirst() : null;
      }
   }

   private boolean BF(PlayerEntity var1) {
      if (var1 != null
         && var1 != this.mc.player
         && var1.isAlive()
         && !var1.isRemoved()
         && !var1.isCreative()
         && !var1.isSpectator()
         && Friends.get().shouldAttack(var1)) {
         if (var1 instanceof FakePlayerEntity var2 && var2.noHit) {
            return false;
         } else {
            Box var8 = var1.getBoundingBox();
            if (this.mc.player.canAttackEntityIn(var8, 0.0)) {
               double var3 = this.BV();
               double var5 = BZ(this.mc.player.getEyePos(), var8);
               if (var5 > var3 * var3) {
                  return false;
               } else {
                  if (this.gJ.get() == KillAuraPlusPlus.RaycastMode.Visible) {
                     boolean var7 = this.BQ(var8);
                     if (!var7 && var5 > (Double)this.gn.get() * (Double)this.gn.get()) {
                        return false;
                     }
                  }

                  return true;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private int BG(PlayerEntity var1, PlayerEntity var2) {
      int var4 = ((KillAuraPlusPlus.TargetMode)this.Bo.get()).ordinal();
      int var10000;
      if (var4 != 0) {
         if (var4 != 1) {
            if (var4 != 2) {
               if (var4 == 3) {
                  var10000 = Integer.compare(var1.getArmor(), var2.getArmor());
               } else {
                  if (var4 != 4) {
                     throw new MatchException(null, null);
                  }

                  var10000 = Integer.compare(var2.getArmor(), var1.getArmor());
               }
            } else {
               var10000 = Double.compare(this.BI(var1), this.BI(var2));
            }
         } else {
            var10000 = Double.compare(this.BH(var1), this.BH(var2));
         }
      } else {
         var10000 = Float.compare(Cb(var1), Cb(var2));
      }

      int var3 = var10000;
      if (var3 != 0) {
         return var3;
      } else {
         var3 = Double.compare(this.BH(var1), this.BH(var2));
         return var3 != 0 ? var3 : Integer.compare(var1.getId(), var2.getId());
      }
   }

   private double BH(PlayerEntity var1) {
      return BZ(this.mc.player.getEyePos(), var1.getBoundingBox());
   }

   private double BI(PlayerEntity var1) {
      double[] var2 = Ca(var1.getBoundingBox().getCenter());
      float var3 = this.Bi ? this.tW : (GrimCombatCoordinator.wS() ? GrimCombatCoordinator.wQ() : this.mc.player.getYaw());
      float var4 = this.Bi ? this.bd : (GrimCombatCoordinator.wS() ? GrimCombatCoordinator.wR() : this.mc.player.getPitch());
      double var5 = MathHelper.wrapDegrees(var2[0] - var3);
      double var7 = var2[1] - var4;
      return var5 * var5 + var7 * var7;
   }

   private int BJ(PlayerEntity var1) {
      if (this.rC.get() == KillAuraPlusPlus.WeaponSwapMode.None) {
         return this.mc.player.getInventory().getSelectedSlot();
      } else {
         int var2 = this.mc.player.getInventory().getSelectedSlot();
         int var3 = var2;
         float var4 = DamageUtils.getAttackDamage(this.mc.player, var1, this.mc.player.getInventory().getStack(var2));

         for (int var5 = 0; var5 < 9; var5++) {
            ItemStack var6 = this.mc.player.getInventory().getStack(var5);
            float var7 = DamageUtils.getAttackDamage(this.mc.player, var1, var6);
            if (var7 > var4 + 1.0E-4F) {
               var4 = var7;
               var3 = var5;
            }
         }

         return var3;
      }
   }

   private boolean BK(int var1) {
      if (var1 >= 0 && var1 <= 8) {
         int var2 = this.mc.player.getInventory().getSelectedSlot();
         int var3 = GrimCombatCoordinator.wT();

         return switch (((KillAuraPlusPlus.WeaponSwapMode)this.rC.get()).ordinal() ^ 1540395366) {
            case 1540395364 -> var3 != var1;
            case 1540395366 -> GrimCombatCoordinator.wM(this) ? false : var3 != var2;
            case 1540395367 -> var2 != var1 || var3 != var1;
            default -> throw new MatchException(null, null);
         };
      } else {
         return true;
      }
   }

   private void BL(int var1) {
      if (var1 >= 0 && var1 <= 8 && this.mc.player != null) {
         int var2 = this.mc.player.getInventory().getSelectedSlot();
         switch (((KillAuraPlusPlus.WeaponSwapMode)this.rC.get()).ordinal() ^ 326079042) {
            case 326079040:
               GrimCombatCoordinator.wI(this, var1);
               break;
            case 326079042:
               GrimCombatCoordinator.wI(this, var2);
               break;
            case 326079043:
               if (!GrimCombatCoordinator.wI(this, var1) && var2 != var1) {
                  InvUtils.swap(var1, false);
               }
         }

         this.gg.zi();
      }
   }

   private boolean BM(PlayerEntity var1, double var2, double var4) {
      if (this.BP(var1)) {
         return true;
      } else if (!this.BN(var2, var4)) {
         this.gg.zi();
         return false;
      } else if (!this.gg.zh(var1.getId())) {
         return false;
      } else {
         boolean var6 = this.BP(var1);
         if (!var6) {
            this.gg.zi();
         }

         return var6;
      }
   }

   private boolean BN(double var1, double var3) {
      double var5 = this.Bi ? this.tW : (!GrimCombatCoordinator.wS() ? Rotations.serverYaw : GrimCombatCoordinator.wQ());
      double var7 = this.Bi ? this.bd : (GrimCombatCoordinator.wS() ? GrimCombatCoordinator.wR() : Rotations.serverPitch);
      double var9 = MathHelper.wrapDegrees(var1 - var5);
      double var11 = var3 - var7;
      if (Math.abs(var9) < 0.5 && Math.abs(var11) < 0.5) {
         return true;
      } else {
         double var13 = CrystalAuraPlusPlus.pD(var5, var1, (Double)this.aW.get());
         double var15 = MathHelper.clamp(var7 + MathHelper.clamp(var11, -(Double)this.bl.get(), (Double)this.bl.get()), -90.0, 90.0);
         boolean var17 = Math.abs(var9) <= (Double)this.aW.get() + 1.0E-4 && Math.abs(var11) <= (Double)this.bl.get() + 1.0E-4;
         this.jQ = new KillAuraPlusPlus.RotationIntent((float)var13, (float)var15);
         return var17;
      }
   }

   private void BO() {
      this.jQ = new KillAuraPlusPlus.RotationIntent(this.tW, this.bd);
   }

   private boolean BP(PlayerEntity var1) {
      if (this.Bi && var1 != null && this.mc.player != null && this.BF(var1)) {
         Vec3d var2 = this.mc.player.getEyePos();
         double var3 = this.BV();
         Box var5 = var1.getBoundingBox();
         Vec3d var6 = var2.add(Vec3d.fromPolar(this.bd, this.tW).multiply(var3));
         Vec3d var7 = var5.contains(var2) ? var2 : (Vec3d)var5.raycast(var2, var6).orElse(null);
         if (var7 == null) {
            return false;
         } else if (this.gJ.get() == KillAuraPlusPlus.RaycastMode.Grim) {
            return true;
         } else {
            BlockHitResult var8 = this.mc.world.raycast(new RaycastContext(var2, var6, ShapeType.COLLIDER, FluidHandling.NONE, this.mc.player));
            return var8.getType() == Type.MISS || var2.squaredDistanceTo(var8.getPos()) + 1.0E-4 >= var2.squaredDistanceTo(var7);
         }
      } else {
         return false;
      }
   }

   private boolean BQ(Box var1) {
      if (this.mc.player != null && this.mc.world != null) {
         Vec3d var2 = this.mc.player.getEyePos();
         Vec3d var3 = var1.getCenter();
         BlockHitResult var4 = this.mc.world.raycast(new RaycastContext(var2, var3, ShapeType.COLLIDER, FluidHandling.NONE, this.mc.player));
         return var4.getType() == Type.MISS || var2.squaredDistanceTo(var4.getPos()) + 1.0E-4 >= var2.squaredDistanceTo(var3);
      } else {
         return false;
      }
   }

   private void BR() {
      KillAuraPlusPlus.RotationIntent var1 = this.jQ;
      this.jQ = null;
      ClientPlayerEntity var2 = this.mc.player;
      if (var1 != null && var2 != null && !var2.hasVehicle() && var2.input != null) {
         this.BS();
         this.ar = new KillAuraPlusPlus.AuraRotationInput(var2.input, var2, var1);
         var2.input = this.ar;
      }
   }

   private void BS() {
      KillAuraPlusPlus.AuraRotationInput var1 = this.ar;
      if (var1 != null) {
         var1.Bj();
         DelegatingInput.qM(this.mc.player, var1);
         this.ar = null;
      }
   }

   private void BT() {
      this.tp = null;
      this.gg.zi();
      this.jQ = null;
   }

   private void BU() {
      this.BS();
      this.BT();
      this.gQ = 0;
      this.tW = 0.0F;
      this.bd = 0.0F;
      this.Bi = false;
   }

   private double BV() {
      return (Double)this.FH.get();
   }

   private float BW() {
      return this.mc.player == null ? 0.0F : this.mc.player.getAttackCooldownProgress(0.5F);
   }

   @Deprecated
   private float BX() {
      return this.BW();
   }

   static int BY(KillAuraPlusPlus.TargetMode var0, float var1, float var2, double var3, double var5, double var7, double var9, int var11, int var12) {
      return switch (var0) {
         case LowestHealth -> Float.compare(var1, var2);
         case ClosestDistance -> Double.compare(var3, var5);
         case ClosestAngle -> Double.compare(var7, var9);
         case LowestArmor -> Integer.compare(var11, var12);
         case HighestArmor -> Integer.compare(var12, var11);
      };
   }

   static double BZ(Vec3d var0, Box var1) {
      double var2 = Math.max(var1.minX - var0.x, Math.max(0.0, var0.x - var1.maxX));
      double var4 = Math.max(var1.minY - var0.y, Math.max(0.0, var0.y - var1.maxY));
      double var6 = Math.max(var1.minZ - var0.z, Math.max(0.0, var0.z - var1.maxZ));
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   private static double[] Ca(Vec3d var0) {
      ClientPlayerEntity var1 = MinecraftClient.getInstance().player;
      Vec3d var2 = var1.getEyePos();
      double var3 = var0.x - var2.x;
      double var5 = var0.y - var2.y;
      double var7 = var0.z - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      return new double[]{Math.toDegrees(Math.atan2(var7, var3)) - 90.0, -Math.toDegrees(Math.atan2(var5, var9))};
   }

   private static float Cb(PlayerEntity var0) {
      return var0.getHealth() + var0.getAbsorptionAmount();
   }

   private static Vec2f Cc(PlayerInput var0) {
      float var10000 = var0.left() ? 1.0F : 0.0F;
      int var10001 = !var0.right() ? 22912 : 22911;

      label31:
      while (true) {
         switch (var10001) {
            case 22911:
               var3 = 1.0F;
               break label31;
            case 22912:
               var10001 = 22910;
               break;
            default:
               var3 = 0.0F;
               break label31;
         }
      }

      float var1 = var10000 - var3;
      float var2 = (var0.forward() ? 1.0F : 0.0F) - (var0.backward() ? 1.0F : 0.0F);
      return new Vec2f(var1, var2).normalize();
   }

   public String getInfoString() {
      return this.tp == null ? null : this.tp.getName().getString();
   }

   private final class AuraRotationInput extends DelegatingInput {
      private final ClientPlayerEntity uP;
      private final KillAuraPlusPlus.RotationIntent GB;
      private boolean vh;
      private float DP;
      private float kF;

      private AuraRotationInput(Input var2, ClientPlayerEntity var3, KillAuraPlusPlus.RotationIntent var4) {
         super(var2);
         this.uP = var3;
         this.GB = var4;
      }

      @Override
      protected void qL() {
         if (KillAuraPlusPlus.this.mc.player == this.uP && KillAuraPlusPlus.this.mc.player.input == this) {
            PlayerInput var1 = this.qI().playerInput == null ? PlayerInput.DEFAULT : this.qI().playerInput;
            Vec2f var2 = this.qI().getMovementInput();
            this.DP = this.uP.getYaw();
            this.kF = this.uP.getPitch();
            PlayerInput var3 = CrystalAuraPlusPlus.oR(var1, var2, this.DP, this.GB.yaw);
            this.setState(var3, KillAuraPlusPlus.Cc(var3));
            this.uP.setYaw(this.GB.yaw);
            this.uP.setPitch(this.GB.pitch);
            this.vh = true;
         }
      }

      private void Bj() {
         if (this.vh && KillAuraPlusPlus.this.mc.player == this.uP) {
            this.uP.setYaw(this.DP);
            this.uP.setPitch(this.kF);
            this.vh = false;
         }
      }
   }

   public static enum RaycastMode {
      Grim,
      Visible;
   }

   private record RotationIntent(float yaw, float pitch) {
   }

   public static enum TargetMode {
      LowestHealth,
      ClosestDistance,
      ClosestAngle,
      LowestArmor,
      HighestArmor;
   }

   public static enum WeaponSwapMode {
      None,
      Pick,
      Silent;
   }
}
