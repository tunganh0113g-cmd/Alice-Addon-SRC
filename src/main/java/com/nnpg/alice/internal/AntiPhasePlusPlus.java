package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import meteordevelopment.meteorclient.events.entity.player.PlayerTickMovementEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent.Post;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Sent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.EnumSetting.Builder;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

public final class AntiPhasePlusPlus extends AliceModule implements GrimCombatCoordinator.Participant {
   private static final double qr = 0.0625;
   private static final double zp = 0.1875;
   private static final double Bc = 0.0625;
   private static final double Cv = 0.6;
   private static final double GK = 4.5;
   private static final double yE = 0.08;
   private static final int GW = 2;
   private static final int cx = 4;
   private static final double qL = 0.3;
   private static final int ct = 20;
   private static final int mR = 3;
   private static final int kI = 120;
   private static final Direction[] gI = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   private static final Direction[] cG = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
   private static final EquipmentSlot[] uz = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private final SettingGroup HM = this.settings.getDefaultGroup();
   private final SettingGroup pG = this.settings.createGroup("Targeting");
   private final SettingGroup oM = this.settings.createGroup("ESP");
   private final Setting<AntiPhasePlusPlus.SealItem> Af = this.HM
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("seal-item"))
                  .description("Vat pham dung de bit o dat. Scaffold\udddbbit chac nhat, Frame khong can block dac hai ben."))
               .defaultValue(AntiPhasePlusPlus.SealItem.un))
            .build()
      );
   private final Setting<Double> nN = this.HM
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                  .name("player-range"))
               .description("Khoang cach toi da tu ban den doi thu can bit."))
            .defaultValue(5.0)
            .min(1.0)
            .sliderRange(1.0, 8.0)
            .build()
      );
   private final Setting<Boolean> kx = this.pG
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("ignore\ude79friends"))
                  .description("Khong bit o dat cua friend."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> AM = this.pG
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("\ude31rmoured-only"))
                  .description("Chi bit doi \udc92hu \ud8e7ang mac du bon manh giap."))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> lT = this.oM
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("esp"))
                  .description("Ve tam bit sap dat va tam\udd86vua dat."))
               .defaultValue(true))
            .build()
      );
   private final Setting<SettingColor> ET = this.oM
      .add(
         ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                  .name("plan-color"))
               .description("Mau o\udb1adat vua \udf11im ra, goc chua arm."))
            .defaultValue(new SettingColor(120, 220, 255, 220))
            .build()
      );
   private final Setting<SettingColor> sv = this.oM
      .add(
         ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                  .name("arm-color"))
               .description("Mau khi goc da arm va chi cho hand\udfcch\udc93ke khop de ban packet."))
            .defaultValue(new SettingColor(180, 255, 140, 230))
            .build()
      );
   private final Setting<SettingColor> kj = this.oM
      .add(
         ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                  .name("sealed-color"))
               .description("Mau tam vua gui packet dat."))
            .defaultValue(new SettingColor(255, 170, 60, 220))
            .build()
      );
   private final Map<BlockPos, Integer> iC = new HashMap<>();
   private final Map<Integer, Vec3d> rg = new HashMap<>();
   private final Map<Integer, Vec3d> xt = new HashMap<>();
   private final Map<Integer, Integer> bf = new HashMap<>();
   private final Random IT = new Random();
   private AntiPhasePlusPlus.SealPlan fl;
   private AntiPhasePlusPlus.SealPlan xF;
   private int qm;
   private int kB;
   private boolean h;
   private boolean Gx;
   private int cp;
   private float tk;
   private float qw;
   private AntiPhasePlusPlus.RotationIntent vN;
   private AntiPhasePlusPlus.SealRotationInput sl;
   private BlockPos jw;
   private BlockPos rX;
   private Direction CF;
   private float DN;
   private float ch;
   private int Gj;
   private boolean wt;
   private boolean ge;

   public AntiPhasePlusPlus() {
      super(
         AliceAddon.PVP_CATEGORY,
         "anti-phase++",
         "Bit o dat cua doi thu ban\udbe6 scaffol\udc91ing, vine,\udc02ladder hoac item frame de chan pearl phase. Grim-safe."
      );
   }

   @Override
   public void onActivate() {
      this.bz();
      GrimCombatCoordinator.wG(this);
      super.onActivate();
   }

   @Override
   public void onDeactivate() {
      GrimCombatCoordinator.wH(this);
      this.bz();
      super.onDeactivate();
   }

   private void bz() {
      this.fl = null;
      this.xF = null;
      this.qm = 0;
      this.h = false;
      this.iC.clear();
      this.rg.clear();
      this.xt.clear();
      this.bf.clear();
      this.co();
   }

   @AliceEventHandler(priority = 100)
   private void bA(Pre var1) {
      if (this.mc.player != null && this.mc.world != null) {
         this.kB++;
         this.cp();
         if (!this.Gx && this.vN == null) {
            AntiPhasePlusPlus.SealItem var2 = (AntiPhasePlusPlus.SealItem)this.Af.get();
            if (!InvUtils.findInHotbar(new Item[]{var2.bl()}).found()) {
               this.fl = null;
            } else {
               AntiPhasePlusPlus.SealPlan var3 = this.bG(var2);
               if (var3 == null) {
                  int var10000 = this.bF(var2) ? 12678 : 12677;

                  while (true) {
                     int var4 = var10000;
                     if (var4 == 12677) {
                        var3 = this.bL(var2);
                        break;
                     }

                     if (var4 != 12678) {
                        break;
                     }

                     var10000 = 12676;
                  }
               }

               this.fl = var3;
            }
         } else if (this.kB - this.cp > 3) {
            this.co();
         }
      } else {
         this.bz();
      }
   }

   @AliceEventHandler(priority = 90)
   private void bB(PlayerTickMovementEvent var1) {
      if (this.Gx && this.vN != null && this.sl == null) {
         ClientPlayerEntity var2 = this.mc.player;
         if (var2 != null && var2.input != null && !var2.hasVehicle()) {
            AntiPhasePlusPlus.RotationIntent var3 = this.vN;
            this.vN = null;
            this.sl = new AntiPhasePlusPlus.SealRotationInput(var2.input, var2, var3, false);
            var2.input = this.sl;
         } else {
            this.co();
         }
      }
   }

   @AliceEventHandler(priority = -50)
   private void bC(Post var1) {
      AntiPhasePlusPlus.SealRotationInput var2 = this.sl;
      if (var2 != null) {
         var2.by(false);
         DelegatingInput.qM(this.mc.player, var2);
         this.sl = null;
         this.Gx = false;
      }
   }

   @AliceEventHandler(priority = -1000)
   private void bD(Sent var1) {
      if (var1.packet instanceof PlayerInteractBlockC2SPacket) {
         if (this.wt) {
            this.ge = true;
         }
      } else if (var1.packet instanceof PlayerMoveC2SPacket var2 && var2.changesLook()) {
         if (this.Gx) {
            float var5 = var2.getYaw(this.tk);
            float var4 = var2.getPitch(this.qw);
            if (!GrimCombatCoordinator.yt(var5, var4, this.tk, this.qw)) {
               this.vN = null;
            }

            this.Gx = false;
         }
      }
   }

   @Override
   public GrimCombatCoordinator.Intent prepareCombatIntent() {
      if (this.mc.player != null && this.mc.world != null) {
         AntiPhasePlusPlus.SealPlan var1 = this.fl;
         if (var1 == null) {
            return null;
         } else {
            AntiPhasePlusPlus.SealItem var2 = (AntiPhasePlusPlus.SealItem)this.Af.get();
            if (!InvUtils.findInHotbar(new Item[]{var2.bl()}).found()) {
               this.fl = null;
               this.co();
               return null;
            } else if (!this.bF(var2)) {
               float[] var3 = this.ci(var1);
               if (var3 == null) {
                  return null;
               } else {
                  if (this.ch(var3)) {
                     if (this.cl(var1, var3)) {
                        return this.bE(() -> this.cc(var1, var3));
                     }

                     this.ck();
                     var3 = this.cf(var1.hit());
                     if (var3 == null) {
                        return null;
                     }
                  }

                  if (!this.Gx && this.vN == null && this.sl == null) {
                     float[] var4 = var3;
                     return this.bE(() -> this.cm(var4, var1));
                  } else {
                     return this.bE(() -> {});
                  }
               }
            } else {
               this.ck();
               return this.bE(() -> this.cc(var1, null));
            }
         }
      } else {
         return null;
      }
   }

   @Override
   public void suspendForItemUse() {
      this.co();
   }

   @Override
   public int combatPriority() {
      return 120;
   }

   private GrimCombatCoordinator.Intent bE(final Runnable var1) {
      return new GrimCombatCoordinator.Intent() {
         @Override
         public GrimCombatCoordinator.Participant owner() {
            return AntiPhasePlusPlus.this;
         }

         @Override
         public void execute() {
            var1.run();
         }
      };
   }

   private boolean bF(AntiPhasePlusPlus.SealItem var1) {
      return var1 == AntiPhasePlusPlus.SealItem.un;
   }

   private AntiPhasePlusPlus.SealPlan bG(AntiPhasePlusPlus.SealItem var1) {
      double var2 = (Double)this.nN.get();
      double var4 = var2 * var2;
      AntiPhasePlusPlus.SealPlan var6 = null;
      double var7 = Double.MAX_VALUE;
      boolean var9 = false;

      for (PlayerEntity var11 : this.mc.world.getPlayers()) {
         this.bH(var11);
         if (this.bM(var11, var4)) {
            Vec3d var12 = this.bI(var11);
            BlockPos var13 = this.bP(var11);
            AntiPhasePlusPlus.SealPlan var14 = var13 == null ? null : this.bS(var1, var11, bK(var11, null, 0), var13);
            boolean var15 = false;
            if (var14 == null) {
               var14 = this.bR(var1, var11, var12, var13);
               var15 = var14 != null;
            }

            if (var14 != null) {
               double var16 = this.jw != null && this.jw.equals(var14.seal()) ? -1.0 : this.mc.player.squaredDistanceTo(var11);
               if (var16 < var7) {
                  var7 = var16;
                  var6 = var14;
                  var9 = var15;
               }
            }
         }
      }

      this.h = var6 != null && var9;
      return var6;
   }

   private void bH(PlayerEntity var1) {
      int var2 = var1.getId();
      Vec3d var3 = bJ(var1);
      Vec3d var4 = this.rg.put(var2, var3);
      Integer var5 = this.bf.put(var2, this.kB);
      if (var4 != null && var5 != null && this.kB - var5 == 1) {
         Vec3d var6 = var3.subtract(var4);
         Vec3d var7 = this.xt.get(var2);
         this.xt.put(var2, var7 == null ? var6 : var7.multiply(0.4).add(var6.multiply(0.6)));
      } else {
         this.xt.remove(var2);
      }
   }

   private Vec3d bI(PlayerEntity var1) {
      Vec3d var2 = this.xt.get(var1.getId());
      return var2 == null ? Vec3d.ZERO : var2;
   }

   private static Vec3d bJ(PlayerEntity var0) {
      PositionInterpolator var1 = var0.getInterpolator();
      Vec3d var2 = var1 == null ? null : var1.getLerpedPos();
      return var2 == null ? var0.getEntityPos() : var2;
   }

   private static Box bK(PlayerEntity var0, Vec3d var1, int var2) {
      Vec3d var3 = bJ(var0);
      Vec3d var4 = var0.getEntityPos();
      Box var5 = var0.getBoundingBox().offset(var3.x - var4.x, var3.y - var4.y, var3.z - var4.z);
      return var2 > 0 && var1 != null ? var5.offset(var1.x * var2, var1.y * var2, var1.z * var2) : var5;
   }

   private AntiPhasePlusPlus.SealPlan bL(AntiPhasePlusPlus.SealItem var1) {
      if (this.jw != null && this.rX != null && this.CF != null) {
         if (this.kB - this.Gj > 4) {
            return null;
         } else if (this.mc.world.isOutOfHeightLimit(this.jw) || !this.mc.world.getBlockState(this.jw).isAir()) {
            return null;
         } else if (this.iC.containsKey(this.jw)) {
            return null;
         } else if (!this.bT(this.rX, this.CF)) {
            return null;
         } else {
            Vec3d var2 = bX(this.rX, this.CF);
            if (this.bY(var2)) {
               this.h = false;
               return new AntiPhasePlusPlus.SealPlan(this.jw, this.rX, this.CF, var2, bW(this.jw, this.CF.getOpposite(), var1.bo()), -1);
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private boolean bM(PlayerEntity var1, double var2) {
      if (var1 != this.mc.player) {
         char var7;
         if (var1.isAlive()) {
            if (!var1.isSpectator()) {
               if (!var1.isSwimming()) {
                  if ((Boolean)this.kx.get() && !Friends.get().shouldAttack(var1)) {
                     return false;
                  }

                  if ((Boolean)this.AM.get() && !this.bN(var1)) {
                     return false;
                  }

                  if (var1.getBoundingBox().getLengthY() <= 1.0) {
                     return false;
                  }

                  return this.mc.player.squaredDistanceTo(var1) <= var2;
               }

               return false;
            }
         } else {
            while (var7 == '腛') {
               var7 = '腙';
            }
         }
      }

      return false;
   }

   private boolean bN(PlayerEntity var1) {
      for (EquipmentSlot var5 : uz) {
         if (!var1.hasStackEquipped(var5)) {
            return false;
         }
      }

      return true;
   }

   private BlockPos bO(Box var1, double var2) {
      Box var4 = var1.expand(-0.0625, 0.0, -0.0625);
      double var5 = Math.round(var2);
      BlockPos var7 = BlockPos.ofFloored(var4.minX, var5, var4.minZ);
      if (!BlockPos.ofFloored(var4.minX, var5, var4.maxZ).equals(var7)) {
         return null;
      } else if (!BlockPos.ofFloored(var4.maxX, var5, var4.minZ).equals(var7)) {
         return null;
      } else {
         return BlockPos.ofFloored(var4.maxX, var5, var4.maxZ).equals(var7) ? var7 : null;
      }
   }

   private BlockPos bP(PlayerEntity var1) {
      return this.bO(bK(var1, null, 0), bJ(var1).y);
   }

   private BlockPos bQ(PlayerEntity var1, Vec3d var2, int var3) {
      if (var3 > 0 && var2 != null) {
         return var2.horizontalLengthSquared() < 1.0E-6 ? null : this.bO(bK(var1, var2, var3), bJ(var1).y + var2.y * var3);
      } else {
         return null;
      }
   }

   private AntiPhasePlusPlus.SealPlan bR(AntiPhasePlusPlus.SealItem var1, PlayerEntity var2, Vec3d var3, BlockPos var4) {
      for (int var5 = 1; var5 <= 2; var5++) {
         BlockPos var6 = this.bQ(var2, var3, var5);
         if (var6 != null && !var6.equals(var4)) {
            AntiPhasePlusPlus.SealPlan var7 = this.bS(var1, var2, bK(var2, var3, var5), var6);
            if (var7 != null) {
               return var7;
            }
         }
      }

      return null;
   }

   private AntiPhasePlusPlus.SealPlan bS(AntiPhasePlusPlus.SealItem var1, PlayerEntity var2, Box var3, BlockPos var4) {
      if (this.mc.world.isOutOfHeightLimit(var4)) {
         return null;
      } else {
         char var24;
         while (var24 == 'ﳱ') {
            var24 = 'ﳯ';
         }

         if (!this.mc.world.getBlockState(var4).isAir()) {
            return null;
         } else if (this.iC.containsKey(var4)) {
            return null;
         } else if (this.bV(var4)) {
            return null;
         } else {
            Box var5 = var1.bn() ? var3 : null;
            Direction[] var6 = var1.bm() ? gI : cG;
            Direction var7 = null;
            double var8 = Double.MAX_VALUE;

            for (int var10 = 0; var10 < var6.length; var10++) {
               Direction var11 = var6[var10];
               Box var12 = bW(var4, var11, var1.bo());
               if (var5 == null || !var12.intersects(var5)) {
                  BlockPos var13 = var4.offset(var11);
                  Direction var14 = var11.getOpposite();
                  if (this.bT(var13, var14)) {
                     Vec3d var15 = bX(var13, var14);
                     if (this.bY(var15) && this.cb(var14, var15) && this.ca(var4, var13, var14, var15)) {
                        double var16 = this.mc.player.getEyePos().squaredDistanceTo(var15);
                        if (var16 < var8) {
                           var8 = var16;
                           var7 = var11;
                        }
                     }
                  }
               }
            }

            if (var7 == null) {
               return null;
            } else {
               BlockPos var19 = var4.offset(var7);
               Direction var20 = var7.getOpposite();
               return new AntiPhasePlusPlus.SealPlan(var4, var19, var20, bX(var19, var20), bW(var4, var7, var1.bo()), var2.getId());
            }
         }
      }
   }

   private boolean bT(BlockPos var1, Direction var2) {
      if (this.mc.world.isOutOfHeightLimit(var1)) {
         return false;
      } else {
         BlockState var3 = this.mc.world.getBlockState(var1);
         return var3.isAir() || var3.isReplaceable() ? false : !BlockUtils.isClickable(var3.getBlock());
      }
   }

   private boolean bU(BlockPos var1) {
      BlockPos var2 = var1.down();
      if (this.mc.world.getBlockState(var2).isSideSolidFullSquare(this.mc.world, var2, Direction.UP)) {
         return true;
      } else {
         for (Direction var6 : gI) {
            if (this.mc.world.getBlockState(var1.offset(var6)).isOf(Blocks.SCAFFOLDING)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean bV(BlockPos var1) {
      return !this.mc.world.getEntitiesByClass(ItemFrameEntity.class, new Box(var1), var0 -> true).isEmpty();
   }

   private static Box bW(BlockPos var0, Direction var1, double var2) {
      double var4 = var0.getX();
      double var6 = var0.getY();
      double var8 = var0.getZ();
      double var10 = Math.min(Math.max(var2, 0.0), 1.0);

      return switch (<unrepresentable>.iy[var1.ordinal()] ^ 1303810264) {
         case 1303810265 -> new Box(var4, var6, var8, var4 + 1.0, var6 + var10, var8 + 1.0);
         case 1303810266 -> new Box(var4, var6 + 1.0 - var10, var8, var4 + 1.0, var6 + 1.0, var8 + 1.0);
         case 1303810267 -> new Box(var4, var6, var8, var4 + 1.0, var6 + 1.0, var8 + var10);
         case 1303810268 -> new Box(var4, var6, var8 + 1.0 - var10, var4 + 1.0, var6 + 1.0, var8 + 1.0);
         case 1303810269 -> new Box(var4, var6, var8, var4 + var10, var6 + 1.0, var8 + 1.0);
         case 1303810270 -> new Box(var4 + 1.0 - var10, var6, var8, var4 + 1.0, var6 + 1.0, var8 + 1.0);
         default -> throw new MatchException(null, null);
      };
   }

   private static Vec3d bX(BlockPos var0, Direction var1) {
      return var0.toCenterPos().offset(var1, 0.5);
   }

   private boolean bY(Vec3d var1) {
      double var2 = this.bZ();
      return this.mc.player.getEyePos().squaredDistanceTo(var1) <= var2 * var2;
   }

   private double bZ() {
      return Math.min(4.5, this.mc.player.getBlockInteractionRange());
   }

   private boolean ca(BlockPos var1, BlockPos var2, Direction var3, Vec3d var4) {
      Item var5 = ((AntiPhasePlusPlus.SealItem)this.Af.get()).bl();
      if (!(var5 instanceof BlockItem var6)) {
         short var9 = 21271;
         return true;
      } else {
         ItemPlacementContext var7 = new ItemPlacementContext(this.mc.player, Hand.MAIN_HAND, new ItemStack(var5), new BlockHitResult(var4, var3, var2, false));
         if (var7.canPlace() && var7.getBlockPos().equals(var1)) {
            BlockState var8 = var6.getBlock().getPlacementState(var7);
            return var8 != null && var8.canPlaceAt(this.mc.world, var1) && this.mc.world.canPlace(var8, var1, ShapeContext.ofPlacement(this.mc.player));
         } else {
            return false;
         }
      }
   }

   private boolean cb(Direction var1, Vec3d var2) {
      return var1.getDoubleVector().dotProduct(this.mc.player.getEyePos().subtract(var2)) > 0.0;
   }

   private void cc(AntiPhasePlusPlus.SealPlan var1, float[] var2) {
      int var10000 = GrimCombatCoordinator.wO(this) ? 21980 : 21979;

      while (true) {
         switch (var10000) {
            case 21979:
               return;
            case 21980:
               var10000 = 21978;
               break;
            default:
               if (this.mc.player != null && this.mc.world != null) {
                  if (!this.mc.world.getBlockState(var1.seal()).isAir()) {
                     this.fl = null;
                     return;
                  }

                  FindItemResult var3 = InvUtils.findInHotbar(new Item[]{((AntiPhasePlusPlus.SealItem)this.Af.get()).bl()});
                  BlockHitResult var4 = new BlockHitResult(var1.hit(), var1.face(), var1.support(), false);
                  if (this.cd(var3, var4, var2)) {
                     this.iC.put(var1.seal(), this.kB + 20);
                     this.ck();
                     this.xF = var1;
                     this.qm = this.kB;
                     this.fl = null;
                  }

                  return;
               }

               return;
         }
      }
   }

   private boolean cd(FindItemResult var1, BlockHitResult var2, float[] var3) {
      if (!this.ce(var1)) {
         return false;
      } else {
         this.wt = true;
         this.ge = false;

         boolean var4;
         try {
            if (var3 == null || this.ch(var3)) {
               this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, var2);
               if (this.ge) {
                  this.mc.player.swingHand(Hand.MAIN_HAND);
                  return true;
               }

               return false;
            }

            var4 = false;
         } finally {
            this.wt = false;
            GrimCombatCoordinator.wJ(this);
         }

         return var4;
      }
   }

   private boolean ce(FindItemResult var1) {
      return !var1.found() ? false : GrimCombatCoordinator.wI(this, var1.slot());
   }

   private float[] cf(Vec3d var1) {
      if (var1 != null && this.mc.player != null) {
         Vec3d var2 = this.mc.player.getEyePos();
         double var3 = var1.x - var2.x;
         double var5 = var1.y - var2.y;
         double var7 = var1.z - var2.z;
         double var9 = Math.sqrt(var3 * var3 + var7 * var7);
         double var11 = 0.08;
         return new float[]{
            (float)(Math.toDegrees(Math.atan2(var7, var3)) - 90.0 + this.cg(var11)), (float)(-Math.toDegrees(Math.atan2(var5, var9)) + this.cg(var11))
         };
      } else {
         return null;
      }
   }

   private double cg(double var1) {
      return !(var1 <= 0.0) ? (this.IT.nextDouble() * 2.0 - 1.0) * var1 : 0.0;
   }

   private boolean ch(float[] var1) {
      return var1 != null && GrimCombatCoordinator.wS() && GrimCombatCoordinator.yt(GrimCombatCoordinator.wQ(), GrimCombatCoordinator.wR(), var1[0], var1[1]);
   }

   private float[] ci(AntiPhasePlusPlus.SealPlan var1) {
      if (this.jw != null && this.CF == var1.face() && this.jw.equals(var1.seal())) {
         if (this.rX != null) {
            if (this.rX.equals(var1.support())) {
               return new float[]{this.DN, this.ch};
            }
         } else {
            short var2 = 20975;
         }
      }

      return this.cf(var1.hit());
   }

   private void cj(AntiPhasePlusPlus.SealPlan var1, float[] var2) {
      if (var1 != null && var2 != null) {
         this.jw = var1.seal();
         this.rX = var1.support();
         this.CF = var1.face();
         this.DN = var2[0];
         this.ch = var2[1];
         this.Gj = this.kB;
      }
   }

   private void ck() {
      this.jw = null;
      this.rX = null;
      this.CF = null;
   }

   private boolean cl(AntiPhasePlusPlus.SealPlan var1, float[] var2) {
      short var15;
      if (this.mc.player != null) {
         if (this.mc.world != null && var2 != null) {
            double var3 = Math.toRadians(var2[0]);
            double var5 = Math.toRadians(var2[1]);
            double var7 = Math.cos(var5);
            Vec3d var9 = new Vec3d(-Math.sin(var3) * var7, -Math.sin(var5), Math.cos(var3) * var7);
            Vec3d var10 = this.mc.player.getEyePos();
            BlockHitResult var11 = VoxelShapes.fullCube().raycast(var10, var10.add(var9.multiply(this.bZ())), var1.support());
            return var11 != null && var11.getSide() == var1.face() && var11.getBlockPos().equals(var1.support());
         }
      } else {
         while (var15 == 6878) {
            var15 = 6876;
         }
      }

      return false;
   }

   private void cm(float[] var1, AntiPhasePlusPlus.SealPlan var2) {
      if (!this.Gx && var1 != null && GrimCombatCoordinator.wO(this)) {
         this.cn(var1);
         int var10000 = this.Gx ? '迏' : '运';

         while (true) {
            int var3 = var10000;
            if (var3 == 36815) {
               this.cj(var2, var1);
               break;
            }

            if (var3 != 36816) {
               break;
            }

            var10000 = 36814;
         }
      }
   }

   private void cn(float[] var1) {
      if (!this.Gx && this.vN == null && var1 != null) {
         this.tk = var1[0];
         this.qw = var1[1];
         this.Gx = true;
         this.cp = this.kB;
         this.vN = new AntiPhasePlusPlus.RotationIntent(this.tk, this.qw);
      }
   }

   private void co() {
      this.ck();
      this.vN = null;
      this.Gx = false;
      AntiPhasePlusPlus.SealRotationInput var1 = this.sl;
      if (var1 != null) {
         var1.by(true);
         DelegatingInput.qM(this.mc.player, var1);
         this.sl = null;
      }
   }

   private void cp() {
      this.bf.entrySet().removeIf(var1 -> this.kB - var1.getValue() > 40);
      this.rg.keySet().removeIf(var1 -> !this.bf.containsKey(var1));
      this.xt.keySet().removeIf(var1 -> !this.bf.containsKey(var1));
      if (!this.iC.isEmpty()) {
         this.iC.entrySet().removeIf(var1 -> this.kB >= var1.getValue() || !this.mc.world.getBlockState(var1.getKey()).isAir());
      }
   }

   @AliceEventHandler
   private void cq(Render3DEvent var1) {
      if ((Boolean)this.lT.get() && this.mc.world != null) {
         AntiPhasePlusPlus.SealPlan var2 = this.fl;
         if (var2 != null) {
            boolean var3 = this.jw != null && this.jw.equals(var2.seal());
            Color var4 = var3 ? (Color)this.sv.get() : (Color)this.ET.get();
            this.cr(var1, var2, this.h ? cs(var4) : var4);
         } else {
            char var5 = '\ue2a2';
         }

         AntiPhasePlusPlus.SealPlan var6 = this.xF;
         if (var6 != null && this.kB - this.qm <= 20) {
            this.cr(var1, var6, (Color)this.kj.get());
         }
      }
   }

   private void cr(Render3DEvent var1, AntiPhasePlusPlus.SealPlan var2, Color var3) {
      Box var4 = var2.shape().expand(0.002);
      Direction var5 = var2.face().getOpposite();
      Vec3d[] var6 = cv(var4, var5);
      Vec3d var7 = var4.getCenter();
      double var8 = 0.3;

      for (int var10 = 0; var10 < 4; var10++) {
         Vec3d var11 = var6[var10];
         this.cu(var1, var11, ct(var11, var6[(var10 + 1) % 4], var8), var3);
         this.cu(var1, var11, ct(var11, var6[(var10 + 3) % 4], var8), var3);
      }

      Vec3d var17 = var6[1].subtract(var6[0]).multiply(var8 * 0.5);
      Vec3d var18 = var6[3].subtract(var6[0]).multiply(var8 * 0.5);
      this.cu(var1, var7.subtract(var17), var7.add(var17), var3);
      this.cu(var1, var7.subtract(var18), var7.add(var18), var3);
      Vec3d var12 = var5.getDoubleVector().multiply(0.18);

      for (Vec3d var16 : var6) {
         this.cu(var1, var16, var16.add(var12), var3);
      }
   }

   private static Color cs(Color var0) {
      return new Color(var0.r, var0.g, var0.b, Math.max(var0.a / 2, 40));
   }

   private static Vec3d ct(Vec3d var0, Vec3d var1, double var2) {
      return var0.add(var1.subtract(var0).multiply(var2));
   }

   private void cu(Render3DEvent var1, Vec3d var2, Vec3d var3, Color var4) {
      var1.renderer.line(var2.x, var2.y, var2.z, var3.x, var3.y, var3.z, var4);
   }

   private static Vec3d[] cv(Box var0, Direction var1) {
      int var2 = <unrepresentable>.iy[var1.ordinal()] ^ 643649998;
      Vec3d[] var10000;
      if (var2 == 643649992) {
         var10000 = new Vec3d[]{
            new Vec3d(var0.maxX, var0.minY, var0.minZ),
            new Vec3d(var0.maxX, var0.minY, var0.maxZ),
            new Vec3d(var0.maxX, var0.maxY, var0.maxZ),
            new Vec3d(var0.maxX, var0.maxY, var0.minZ)
         };
      } else if (var2 == 643649994) {
         var10000 = new Vec3d[]{
            new Vec3d(var0.minX, var0.minY, var0.maxZ),
            new Vec3d(var0.maxX, var0.minY, var0.maxZ),
            new Vec3d(var0.maxX, var0.maxY, var0.maxZ),
            new Vec3d(var0.minX, var0.maxY, var0.maxZ)
         };
      } else if (var2 != 643649995) {
         if (var2 != 643649996) {
            if (var2 != 643649997) {
               if (var2 != 643649999) {
                  throw new MatchException(null, null);
               }

               var10000 = new Vec3d[]{
                  new Vec3d(var0.minX, var0.minY, var0.minZ),
                  new Vec3d(var0.maxX, var0.minY, var0.minZ),
                  new Vec3d(var0.maxX, var0.minY, var0.maxZ),
                  new Vec3d(var0.minX, var0.minY, var0.maxZ)
               };
            } else {
               var10000 = new Vec3d[]{
                  new Vec3d(var0.minX, var0.minY, var0.minZ),
                  new Vec3d(var0.maxX, var0.minY, var0.minZ),
                  new Vec3d(var0.maxX, var0.maxY, var0.minZ),
                  new Vec3d(var0.minX, var0.maxY, var0.minZ)
               };
            }
         } else {
            var10000 = new Vec3d[]{
               new Vec3d(var0.minX, var0.maxY, var0.minZ),
               new Vec3d(var0.maxX, var0.maxY, var0.minZ),
               new Vec3d(var0.maxX, var0.maxY, var0.maxZ),
               new Vec3d(var0.minX, var0.maxY, var0.maxZ)
            };
         }
      } else {
         var10000 = new Vec3d[]{
            new Vec3d(var0.minX, var0.minY, var0.minZ),
            new Vec3d(var0.minX, var0.minY, var0.maxZ),
            new Vec3d(var0.minX, var0.maxY, var0.maxZ),
            new Vec3d(var0.minX, var0.maxY, var0.minZ)
         };
      }

      return var10000;
   }

   private static Vec2f cw(PlayerInput var0) {
      float var1 = (!var0.left() ? 0.0F : 1.0F) - (var0.right() ? 1.0F : 0.0F);
      float var2 = (var0.forward() ? 1.0F : 0.0F) - (!var0.backward() ? 0.0F : 1.0F);
      return new Vec2f(var1, var2).normalize();
   }

   private record RotationIntent(float yaw, float pitch) {
   }

   private static enum SealItem {
      Ladder,
      Vine,
      un,
      Frame;

      private Item bl() {
         int var1 = this.ordinal();
         Item var10000;
         if (var1 != 0) {
            if (var1 != 1) {
               if (var1 != 2) {
                  if (var1 != 3) {
                     throw new MatchException(null, null);
                  }

                  var10000 = Items.ITEM_FRAME;
               } else {
                  var10000 = Items.SCAFFOLDING;
               }
            } else {
               var10000 = Items.VINE;
            }
         } else {
            var10000 = Items.LADDER;
         }

         return var10000;
      }

      private boolean bm() {
         return this == Ladder || this == Vine;
      }

      private boolean bn() {
         return this == Ladder;
      }

      private double bo() {
         return switch (this.ordinal() ^ -1394668818) {
            case -1394668820 -> 1.0;
            case -1394668819, -1394668817 -> 0.0625;
            case -1394668818 -> 0.1875;
            default -> throw new MatchException(null, null);
         };
      }
   }

   private record SealPlan(BlockPos seal, BlockPos support, Direction face, Vec3d hit, Box shape, int victimId) {
   }

   private final class SealRotationInput extends DelegatingInput {
      private final ClientPlayerEntity iI;
      private final AntiPhasePlusPlus.RotationIntent ED;
      private final boolean fX;
      private boolean GY;
      private float Gf;
      private float sp;

      private SealRotationInput(Input var2, ClientPlayerEntity var3, AntiPhasePlusPlus.RotationIntent var4, boolean var5) {
         super(var2);
         this.iI = var3;
         this.ED = var4;
         this.fX = var5;
      }

      @Override
      protected void qL() {
         if (AntiPhasePlusPlus.this.mc.player == this.iI && AntiPhasePlusPlus.this.mc.player.input == this) {
            PlayerInput var1 = this.qI().playerInput == null ? PlayerInput.DEFAULT : this.qI().playerInput;
            Vec2f var2 = this.qI().getMovementInput();
            this.Gf = this.iI.getYaw();
            this.sp = this.iI.getPitch();
            PlayerInput var3 = CrystalAuraPlusPlus.oR(var1, var2, this.Gf, this.ED.yaw);
            this.setState(var3, AntiPhasePlusPlus.cw(var3));
            this.iI.setYaw(this.ED.yaw);
            this.iI.setPitch(this.ED.pitch);
            this.GY = true;
         }
      }

      private void by(boolean var1) {
         if (this.GY && AntiPhasePlusPlus.this.mc.player == this.iI) {
            if (var1 || !this.fX) {
               this.iI.setYaw(this.Gf);
               this.iI.setPitch(this.sp);
            }

            this.GY = false;
         }
      }
   }
}
