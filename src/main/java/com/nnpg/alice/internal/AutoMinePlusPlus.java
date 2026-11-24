package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.EnumSetting.Builder;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class AutoMinePlusPlus extends AliceModule {
   private static final double AU = 0.0625;
   private static final double al = 0.05;
   private static final float cV = 600.0F;
   private static final int c = 1500;
   private static final int in = 700;
   private static final int Cb = 600;
   private static final int De = 500;
   private static final int Er = 400;
   private static final int bU = 100;
   private static final int zA = 400;
   private static final Direction[] BF = new Direction[]{Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
   private static final EquipmentSlot[] nJ = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private final SettingGroup fg = this.settings.getDefaultGroup();
   private final SettingGroup uQ = this.settings.createGroup("Rules");
   private final SettingGroup pQ = this.settings.createGroup("Self");
   private final Setting<AutoMinePlusPlus.Logic> S = this.fg
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("logic")).description("Che do chon muc tieu: Plain, Grim hoac NoVelocity."))
               .defaultValue(AutoMinePlusPlus.Logic.Plain))
            .build()
      );
   private final Setting<Boolean> nb = this.fg
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("strict-direction"))
                  .description("Chi nhan block co mat dao hop le tu vi tri mat hien tai."))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> Id = this.fg
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("raytrace"))
                  .description("Yeu cau tia nhin cham duoc block muc tieu."))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> q = this.fg
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("rotate"))
                  .description("Gui look trong movement packet truoc khi giao plan cho Speed Mine++."))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> uN = this.fg
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("ignore-naked"))
                  .description("Bo qua nguoi choi khong mac mon giap nao."))
               .defaultValue(false))
            .build()
      );
   private final Setting<Integer> qJ = this.fg
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("grim-delay"))
                  .description("So tick giu nguyen plan moi truoc khi giao cho Speed Mine++."))
               .defaultValue(1))
            .range(0, 5)
            .sliderRange(0, 5)
            .build()
      );
   private final Setting<Boolean> vg = this.uQ
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("burrow"))
                  .description("Dao block chong no ma muc tieu dang dung ben trong."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> Eq = this.uQ
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("fish"))
                  .description("Dao block ben duoi muc tieu dang dung tren cot bedrock mot o."))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> GR = this.uQ
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("head"))
                  .description("Dao nap va vong block tren dau muc tieu."))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> sI = this.uQ
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("trap-override"))
                     .description("Nang uu tien nap dau len 650 khi gap trap hop le."))
                  .defaultValue(false))
               .visible(this.GR::get))
            .build()
      );
   private final Setting<Boolean> eb = this.uQ
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("feet"))
                  .description("City vong surround quanh chan muc tieu."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> px = this.uQ
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("safe"))
                     .description("Khong city block thuoc surround cua chinh ban."))
                  .defaultValue(true))
               .visible(this.eb::get))
            .build()
      );
   private final Setting<Boolean> vk = this.pQ
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("self"))
                  .description("Tu dao block dang ghim dau, mat hoac chan cua ban."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> fv = this.pQ
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("face"))
                     .description("Uu tien block chong no tai eye position cua ban."))
                  .defaultValue(false))
               .visible(this.vk::get))
            .build()
      );
   private final Setting<Boolean> aj = this.pQ
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("only-crawl"))
                     .description("Chi tu dao khi dang crawl, tru block face hop le."))
                  .defaultValue(false))
               .visible(this.vk::get))
            .build()
      );
   private final Setting<Boolean> pw = this.pQ
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("down-priority"))
                     .description("Cho phep block duoi chan thang uu tien ma khong can hole an toan."))
                  .defaultValue(false))
               .visible(this.vk::get))
            .build()
      );
   private int tq;
   private int Gp;
   private long ww;
   private AutoMinePlusPlus.MinePlan an;
   private AutoMinePlusPlus.MinePlan oX;
   private boolean qE;

   public AutoMinePlusPlus() {
      super(AliceAddon.PVP_CATEGORY, "auto-mine++", "Bo lap ke hoach ALICE toi uu hai lane cho Speed Mine++");
   }

   @Override
   public void onActivate() {
      this.eQ();
      SpeedMinePlusPlus var1 = this.eP();
      if (var1 == null || !var1.isActive()) {
         this.warning("Speed Mine++ is off, so Auto Mine++ cannot submit targets.", new Object[0]);
      }

      super.onActivate();
   }

   @Override
   public void onDeactivate() {
      this.ww++;
      SpeedMinePlusPlus var1 = this.eP();
      if (var1 != null) {
         var1.Ji();
      }

      this.eQ();
      super.onDeactivate();
   }

   @AliceEventHandler(priority = 150)
   private void ej(Pre var1) {
      this.tq++;
      SpeedMinePlusPlus var2 = this.eP();
      if (var2 != null && var2.isActive() && this.mc.player != null && this.mc.world != null && !this.mc.player.isCreative() && !this.mc.player.isSpectator()) {
         PlayerEntity var3 = this.er(var2.Jo());
         AutoMinePlusPlus.PlanAccumulator var4 = new AutoMinePlusPlus.PlanAccumulator();
         this.em(var2, var3, var4);
         this.en(var2, var3, var4);
         this.eo(var2, var3, var4);
         this.ep(var2, var3, var4);
         this.eq(var2, var3, var4);
         AutoMinePlusPlus.MinePlan var5 = var4.ei();
         if (var5 != null && this.et(var2, var5)) {
            if (!eS(var5, this.an)) {
               this.an = var5;
               this.Gp = this.tq + (Integer)this.qJ.get();
               this.qE = false;
               this.oX = null;
               this.ww++;
            }

            if (this.tq >= this.Gp) {
               if ((Boolean)this.q.get() && !eS(var5, this.oX)) {
                  this.ek(var5);
               } else {
                  this.el(var2, var5);
               }
            }
         } else {
            this.eR();
         }
      } else {
         this.eR();
      }
   }

   private void ek(AutoMinePlusPlus.MinePlan var1) {
      if (!this.qE && this.mc.player != null) {
         this.qE = true;
         long var2 = ++this.ww;
         Vec3d var4 = this.ev(var1.primary(), var1.primaryFace());
         double var5 = Rotations.getYaw(var4);
         double var7 = Rotations.getPitch(var4);
         Rotations.rotate(var5, var7, 100, () -> {
            this.qE = false;
            if (var2 == this.ww && this.isActive() && eS(var1, this.an)) {
               SpeedMinePlusPlus var4x = this.eP();
               if (var4x != null && var4x.isActive() && this.et(var4x, var1)) {
                  this.oX = var1;
                  this.el(var4x, var1);
               }
            }
         });
      }
   }

   private boolean el(SpeedMinePlusPlus var1, AutoMinePlusPlus.MinePlan var2) {
      return var1.Jh(var2.primary(), var2.primaryFace(), var2.secondary(), var2.secondaryFace(), var2.priority(), var2.priority() >= 1500);
   }

   private void em(SpeedMinePlusPlus var1, PlayerEntity var2, AutoMinePlusPlus.PlanAccumulator var3) {
      if ((Boolean)this.vg.get() && var2 != null) {
         List var4 = BlockPos.stream(var2.getBoundingBox().expand(-0.1, 0.0, -0.1).withMaxY(var2.getY()))
            .<BlockPos>map(BlockPos::toImmutable)
            .filter(
               var2x -> this.eC(var2x)
                  && !this.mc.world.getBlockState(var2x).isOf(Blocks.COBWEB)
                  && !this.mc.world.getBlockState(var2x).isOf(Blocks.ANVIL)
                  && this.es(var1, var2x)
            )
            .sorted(this.eE(var1, var2))
            .toList();
         if (var4.size() >= 2) {
            var3.eg(700, "BURROW", (BlockPos)var4.get(0), (BlockPos)var4.get(1));
         } else if (var4.size() == 1) {
            var3.eg(500, "BURROW", (BlockPos)var4.get(0), null);
         }
      }
   }

   private void en(SpeedMinePlusPlus var1, PlayerEntity var2, AutoMinePlusPlus.PlanAccumulator var3) {
      int var10000 = !this.Eq.get() ? 'ﭹ' : 'ﭸ';

      while (true) {
         switch (var10000) {
            case 64376:
               if (var2 != null && eM(var2).size() == 1) {
                  BlockPos var4 = eL(var2);
                  if (!this.mc.world.isAir(var4) && !BlockUtils.canBreak(var4, this.mc.world.getBlockState(var4))) {
                     BlockPos var5 = var4.down();
                     if (this.es(var1, var5)) {
                        var3.eg(400, "FISH", var5, null);
                     }

                     return;
                  }

                  return;
               }

               return;
            case 64377:
               var10000 = 64375;
               break;
            default:
               return;
         }
      }
   }

   private void eo(SpeedMinePlusPlus var1, PlayerEntity var2, AutoMinePlusPlus.PlanAccumulator var3) {
      if ((Boolean)this.eb.get() && var2 != null) {
         int var10000 = !this.ex(var2) ? 'ﮂ' : 'ﮃ';

         while (true) {
            int var13 = var10000;
            if (var13 == 64386) {
               break;
            }

            if (var13 != 64387) {
               if (this.eK(var2).stream().allMatch(var1x -> this.mc.world.getBlockState(var1x).isOf(Blocks.BEDROCK))) {
                  return;
               }

               BlockPos var4 = this.eJ(var2);
               int var5 = var4.getY() - eL(var2).getY();
               boolean var6 = this.eC(var4);
               Set var7 = eO(this.mc.player);
               ArrayList var8 = new ArrayList();

               for (BlockPos var10 : eO(var2)) {
                  BlockPos var11 = var10.up(var5);
                  BlockPos var12 = !var6 ? var11 : var11.down();
                  if ((!(Boolean)this.px.get() || !var7.contains(var12))
                     && !this.mc.world.getBlockState(var12).isOf(Blocks.COBWEB)
                     && this.es(var1, var12)
                     && (this.S.get() != AutoMinePlusPlus.Logic.Plain || var6 || this.eD(var12))) {
                     var8.add(var12);
                  }
               }

               var8.sort(this.eF(var1, var2));
               if (var8.isEmpty()) {
                  return;
               }

               var3.eg(600, "FEET", (BlockPos)var8.get(0), var8.size() > 1 ? (BlockPos)var8.get(1) : null);
               return;
            }

            var10000 = 64385;
         }
      }
   }

   private void ep(SpeedMinePlusPlus var1, PlayerEntity var2, AutoMinePlusPlus.PlanAccumulator var3) {
      if ((Boolean)this.vk.get() && !this.mc.player.isGliding()) {
         BlockPos var4 = this.ez(var1);
         if (var2 != null || var4 != null) {
            boolean var5 = this.mc.player.isCrawling();
            if (!(Boolean)this.aj.get() || var5 || var4 != null) {
               int var6 = !var5 && var4 == null ? 100 : 1500;
               BlockPos var7 = null;
               BlockPos var8 = eL(this.mc.player).down();
               if (this.es(var1, var8) && (this.eG(var1, var8) || var5) && this.eB()) {
                  var7 = var8;
               }

               if (var7 == null) {
                  var7 = var4 != null ? var4 : this.eA(var1);
               }

               var3.eg(var6, "SELF", var7, null);
            }
         }
      }
   }

   private void eq(SpeedMinePlusPlus var1, PlayerEntity var2, AutoMinePlusPlus.PlanAccumulator var3) {
      if ((Boolean)this.GR.get() && var2 != null && !var2.isCrawling() && this.ex(var2)) {
         if (this.S.get() == AutoMinePlusPlus.Logic.NoVelocity || !this.eK(var2).stream().anyMatch(this::eC)) {
            ArrayList var4 = new ArrayList();
            int var5 = (int)Math.floor(var2.getBoundingBox().maxY);
            int[] var6 = new int[]{var5, var5 + 1};
            int var7 = var6.length;
            int var8 = 0;

            while (true) {
               int var10000 = var8 < var7 ? 'ꍊ' : 'ꍋ';

               while (true) {
                  int var13 = var10000;
                  if (var13 == 41802) {
                     int var17 = var6[var8];

                     for (BlockPos var11 : eN(var2.getBoundingBox(), 0.05)) {
                        BlockPos var12 = new BlockPos(var11.getX(), var17, var11.getZ());
                        if (this.ey(var1, var12)) {
                           var4.add(var12);
                        }
                     }

                     var8++;
                     break;
                  }

                  if (var13 != 41803) {
                     var4.sort(this.eE(var1, var2));
                     List var14 = eO(var2).stream().<BlockPos>map(BlockPos::up).filter(var2x -> this.ey(var1, var2x)).sorted(this.eE(var1, var2)).toList();
                     BlockPos var15 = var4.isEmpty() ? null : (BlockPos)var4.get(0);
                     BlockPos var16 = !var14.isEmpty() ? (BlockPos)var14.get(0) : null;
                     if (var15 == null && var16 != null && this.S.get() != AutoMinePlusPlus.Logic.NoVelocity) {
                        var15 = var16;
                        var16 = null;
                     }

                     if (var15 == null) {
                        return;
                     }

                     boolean var9 = this.eK(var2).stream().anyMatch(var1x -> this.mc.world.getBlockState(var1x).isOf(Blocks.BEDROCK));
                     short var10;
                     if (this.S.get() == AutoMinePlusPlus.Logic.NoVelocity) {
                        if (!var9 ? var16 == null : var16 == null) {
                        }
                     } else if (!this.eG(var1, var15) && !(Boolean)this.sI.get()) {
                        var10 = 599;
                     } else {
                        var10 = 650;
                     }

                     var3.eg(var10, "HEAD", var15, var16);
                     return;
                  }

                  var10000 = 41801;
               }
            }
         }
      }
   }

   private PlayerEntity er(double var1) {
      PlayerEntity var3 = null;
      double var4 = Double.MAX_VALUE;

      for (PlayerEntity var7 : this.mc.world.getPlayers()) {
         if (var7 != this.mc.player
            && var7.isAlive()
            && !var7.isSpectator()
            && Friends.get().shouldAttack(var7)
            && !(this.mc.player.squaredDistanceTo(var7) > var1 * var1)
            && (!(Boolean)this.uN.get() || eH(var7))) {
            double var8 = Math.abs(MathHelper.wrapDegrees((float)(Rotations.getYaw(var7) - this.mc.player.getYaw())));
            if (var8 < var4) {
               var4 = var8;
               var3 = var7;
            }
         }
      }

      return var3;
   }

   private boolean es(SpeedMinePlusPlus var1, BlockPos var2) {
      if (var2 != null && var1.Jq(var2)) {
         BlockState var3 = this.mc.world.getBlockState(var2);
         if (!var3.isAir() && BlockUtils.canBreak(var2, var3)) {
            Direction var4 = this.eu(var2);
            if ((Boolean)this.nb.get() && var4 == null) {
               return false;
            } else if ((Boolean)this.Id.get()) {
               int var10000 = !this.ew(var2) ? 30959 : 30958;

               while (true) {
                  switch (var10000) {
                     case 30958:
                        return true;
                     case 30959:
                        var10000 = 30957;
                        break;
                     default:
                        return false;
                  }
               }
            } else {
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean et(SpeedMinePlusPlus var1, AutoMinePlusPlus.MinePlan var2) {
      return var2 != null && this.es(var1, var2.primary()) ? var2.secondary() == null || this.es(var1, var2.secondary()) : false;
   }

   private Direction eu(BlockPos var1) {
      return this.nb.get() ? CrystalAuraPlusPlus.pB(var1, this.mc.player.getEyePos()) : Direction.UP;
   }

   private Vec3d ev(BlockPos var1, Direction var2) {
      Vec3d var3 = Vec3d.ofCenter(var1);
      Direction var4 = var2 == null ? Direction.UP : var2;
      return var3.add(var4.getOffsetX() * 0.5, var4.getOffsetY() * 0.5, var4.getOffsetZ() * 0.5);
   }

   private boolean ew(BlockPos var1) {
      BlockHitResult var2 = this.mc
         .world
         .raycast(new RaycastContext(this.mc.player.getEyePos(), Vec3d.ofCenter(var1), ShapeType.COLLIDER, FluidHandling.NONE, this.mc.player));
      return var2.getType() == Type.MISS || var2.getBlockPos().equals(var1);
   }

   private boolean ex(Entity var1) {
      return !this.mc.world.isSpaceEmpty(var1, var1.getBoundingBox().stretch(0.0, -0.1, 0.0));
   }

   private boolean ey(SpeedMinePlusPlus var1, BlockPos var2) {
      if (!this.es(var1, var2)) {
         return false;
      } else if (this.eG(var1, var2)) {
         return true;
      } else {
         return this.S.get() == AutoMinePlusPlus.Logic.NoVelocity
            ? true
            : this.mc.world.getBlockState(var2).isOf(Blocks.OBSIDIAN) && this.mc.world.isAir(var2.up());
      }
   }

   private BlockPos ez(SpeedMinePlusPlus var1) {
      if ((Boolean)this.fv.get() && !this.mc.player.isCrawling()) {
         BlockPos var2 = BlockPos.ofFloored(this.mc.player.getEyePos());
         return this.eC(var2) && this.es(var1, var2) ? var2 : null;
      } else {
         return null;
      }
   }

   private BlockPos eA(SpeedMinePlusPlus var1) {
      int var2 = (int)Math.floor(this.mc.player.getBoundingBox().maxY) + 1;

      for (BlockPos var4 : eN(this.mc.player.getBoundingBox(), 0.05)) {
         BlockPos var5 = new BlockPos(var4.getX(), var2, var4.getZ());
         if (this.es(var1, var5)) {
            return var5;
         }
      }

      return null;
   }

   private boolean eB() {
      if ((Boolean)this.pw.get()) {
         return true;
      } else if (this.mc.world.isAir(eL(this.mc.player).down(2))) {
         return false;
      } else {
         for (BlockPos var2 : eO(this.mc.player)) {
            if (!this.eC(var2.down())) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean eC(BlockPos var1) {
      BlockState var2 = this.mc.world.getBlockState(var1);
      return !var2.isAir() && var2.getBlock().getBlastResistance() >= 600.0F;
   }

   private boolean eD(BlockPos var1) {
      for (Direction var5 : BF) {
         BlockPos var6 = var1.offset(var5).down();
         BlockState var7 = this.mc.world.getBlockState(var6);
         if (var7.isOf(Blocks.OBSIDIAN) || var7.isOf(Blocks.BEDROCK)) {
            BlockPos var8 = var6.up();
            if (this.mc.world.isAir(var8)) {
               Box var9 = new Box(var8);
               boolean var10 = false;

               for (Entity var12 : this.mc.world.getEntities()) {
                  if (!var12.isRemoved() && var12.isAlive() && var12.getBoundingBox().intersects(var9)) {
                     var10 = true;
                     break;
                  }
               }

               if (!var10) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private Comparator<BlockPos> eE(SpeedMinePlusPlus var1, PlayerEntity var2) {
      return Comparator.<BlockPos, Boolean>comparing(var2x -> !this.eG(var1, var2x))
         .thenComparingDouble(var1x -> var2.squaredDistanceTo(Vec3d.ofCenter(var1x)));
   }

   private Comparator<BlockPos> eF(SpeedMinePlusPlus var1, PlayerEntity var2) {
      return Comparator.<BlockPos, Boolean>comparing(var2x -> !this.eG(var1, var2x))
         .thenComparing(var1x -> !this.mc.player.getBoundingBox().intersects(new Box(var1x)))
         .thenComparing(var1x -> !this.eD(var1x))
         .thenComparingDouble(var1x -> var2.squaredDistanceTo(Vec3d.ofCenter(var1x)));
   }

   private boolean eG(SpeedMinePlusPlus var1, BlockPos var2) {
      return var2 != null && (var2.equals(var1.Jj()) || var2.equals(var1.Jk()));
   }

   private static boolean eH(PlayerEntity var0) {
      for (EquipmentSlot var4 : nJ) {
         if (var0.hasStackEquipped(var4)) {
            return true;
         }
      }

      return false;
   }

   private int eI(Entity var1) {
      BlockPos var2 = eL(var1);

      for (int var3 = 0; var3 <= 2; var3++) {
         BlockPos var4 = new BlockPos(var2.getX(), var2.getY() - var3, var2.getZ());
         if (this.eC(var4)) {
            return var4.getY() + 1;
         }
      }

      return var2.getY();
   }

   private BlockPos eJ(Entity var1) {
      BlockPos var2 = eL(var1);
      return new BlockPos(var2.getX(), this.eI(var1), var2.getZ());
   }

   private Set<BlockPos> eK(Entity var1) {
      int var2 = this.eI(var1);
      LinkedHashSet var3 = new LinkedHashSet();

      for (BlockPos var5 : eN(var1.getBoundingBox(), 0.0625)) {
         var3.add(new BlockPos(var5.getX(), var2, var5.getZ()));
      }

      return var3;
   }

   private static BlockPos eL(Entity var0) {
      return BlockPos.ofFloored(var0.getX(), var0.getY(), var0.getZ());
   }

   private static Set<BlockPos> eM(Entity var0) {
      int var1 = eL(var0).getY();
      LinkedHashSet var2 = new LinkedHashSet();

      for (BlockPos var4 : eN(var0.getBoundingBox(), 0.0625)) {
         var2.add(new BlockPos(var4.getX(), var1, var4.getZ()));
      }

      return var2;
   }

   private static Set<BlockPos> eN(Box var0, double var1) {
      Box var3 = var0.expand(-var1, 0.0, -var1);
      LinkedHashSet var4 = new LinkedHashSet();
      var4.add(BlockPos.ofFloored(var3.minX, 0.0, var3.minZ));
      var4.add(BlockPos.ofFloored(var3.minX, 0.0, var3.maxZ));
      var4.add(BlockPos.ofFloored(var3.maxX, 0.0, var3.minZ));
      var4.add(BlockPos.ofFloored(var3.maxX, 0.0, var3.maxZ));
      return var4;
   }

   private static Set<BlockPos> eO(Entity var0) {
      Set var1 = eM(var0);
      LinkedHashSet var2 = new LinkedHashSet();

      for (BlockPos var4 : var1) {
         for (Direction var6 : net.minecraft.util.math.Direction.Type.HORIZONTAL) {
            BlockPos var7 = var4.offset(var6);
            if (!var1.contains(var7)) {
               var2.add(var7);
            }
         }
      }

      return var2;
   }

   private SpeedMinePlusPlus eP() {
      return (SpeedMinePlusPlus)Modules.get().get(SpeedMinePlusPlus.class);
   }

   private void eQ() {
      this.tq = 0;
      this.eR();
   }

   private void eR() {
      this.an = null;
      this.oX = null;
      this.qE = false;
      this.Gp = 0;
      this.ww++;
   }

   private static boolean eS(AutoMinePlusPlus.MinePlan var0, AutoMinePlusPlus.MinePlan var1) {
      if (var0 == var1) {
         return true;
      } else {
         return var0 != null && var1 != null
            ? var0.primary().equals(var1.primary()) && Objects.equals(var0.secondary(), var1.secondary()) && var0.priority() == var1.priority()
            : false;
      }
   }

   public static enum Logic {
      Plain,
      Grim,
      NoVelocity;
   }

   private record MinePlan(BlockPos primary, Direction primaryFace, BlockPos secondary, Direction secondaryFace, int priority, String rule) {
   }

   private final class PlanAccumulator {
      private AutoMinePlusPlus.MinePlan Js;
      private BlockPos zf;
      private int pn = Integer.MIN_VALUE;

      private void eg(int var1, String var2, BlockPos var3, BlockPos var4) {
         if (var3 != null) {
            BlockPos var5 = var3.toImmutable();
            BlockPos var10000;
            if (var4 != null && !var4.equals(var3)) {
               short var14;
               while (var14 == 21953) {
                  var14 = 21951;
               }

               var10000 = var4.toImmutable();
            } else {
               var10000 = null;
            }

            BlockPos var6 = var10000;
            Direction var7 = AutoMinePlusPlus.this.eu(var5);
            if (!(Boolean)AutoMinePlusPlus.this.nb.get() || var7 != null) {
               Direction var8 = var6 == null ? null : AutoMinePlusPlus.this.eu(var6);
               if (var6 != null && (Boolean)AutoMinePlusPlus.this.nb.get() && var8 == null) {
                  var6 = null;
                  var8 = null;
               }

               AutoMinePlusPlus.MinePlan var9 = new AutoMinePlusPlus.MinePlan(var5, var7, var6, var8, var1, var2);
               if (this.Js != null && var1 < this.Js.priority()) {
                  this.eh(var1, var5);
                  this.eh(var1, var6);
               } else {
                  AutoMinePlusPlus.MinePlan var10 = this.Js;
                  this.Js = var9;
                  if (var10 != null) {
                     this.eh(var10.priority(), var10.primary());
                     this.eh(var10.priority(), var10.secondary());
                  }
               }
            }
         }
      }

      private void eh(int var1, BlockPos var2) {
         if (var2 != null) {
            int var10000 = var1 < this.pn ? '耡' : '耠';

            while (true) {
               switch (var10000) {
                  case 32800:
                     if (this.Js != null) {
                        if (var2.equals(this.Js.primary())) {
                           return;
                        }

                        short var3 = 18650;
                     }

                     this.zf = var2.toImmutable();
                     this.pn = var1;
                     return;
                  case 32801:
                     var10000 = 32799;
                     break;
                  default:
                     return;
               }
            }
         }
      }

      private AutoMinePlusPlus.MinePlan ei() {
         if (this.Js == null) {
            return null;
         } else if (this.Js.secondary() == null && this.zf != null && this.pn >= 400 && !this.zf.equals(this.Js.primary())) {
            Direction var1 = AutoMinePlusPlus.this.eu(this.zf);
            return AutoMinePlusPlus.this.nb.get() && var1 == null
               ? this.Js
               : new AutoMinePlusPlus.MinePlan(this.Js.primary(), this.Js.primaryFace(), this.zf, var1, this.Js.priority(), this.Js.rule());
         } else {
            return this.Js;
         }
      }
   }
}
