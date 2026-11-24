package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Send;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.DoubleSetting.Builder;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.OperatorBlock;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

public class SpeedMinePlusPlus extends AliceModule {
   private static final MethodHandle EH = Ky();
   private static final long dT = System.nanoTime();
   private static final double lh = 0.7;
   private static final double yc = 1.0;
   private static final double xa = 1.0E-4;
   private static final long sD = 300000000L;
   private static final int hE = 2;
   private static final int po = 1420;
   private static final double Fq = 0.5;
   private static final int tn = 2;
   private static final int xG = 10;
   private static final int io = 3;
   private static final int EQ = 8;
   private static final int xK = 6;
   private final SettingGroup GF;
   private final SettingGroup uR;
   private final Setting<Double> my;
   private final Setting<Boolean> qB;
   public final Setting<Boolean> HA;
   public final Setting<Boolean> fV;
   private final Setting<Boolean> Ig;
   private final Setting<Boolean> CO;
   private final Setting<Boolean> Ax;
   private final Setting<ShapeMode> dF;
   private final Setting<SettingColor> fc;
   private final Setting<SettingColor> Gg;
   private final Setting<SettingColor> kQ;
   private final Setting<SettingColor> ka;
   private final Setting<SettingColor> tV;
   private final Setting<SettingColor> ex;
   private final Setting<Boolean> qC;
   private final Setting<Boolean> Bs;
   private final Setting<Boolean> bb;
   private int jl;
   private final ArrayDeque<SpeedMinePlusPlus.RenderPulse> zV = new ArrayDeque<>();
   private final Vector3d Ap = new Vector3d();
   private SpeedMinePlusPlus.SpeedMinePlusPlusBlock Ac;
   private SpeedMinePlusPlus.SpeedMinePlusPlusBlock sk;
   private double s;
   private boolean vB;
   private boolean bP;
   private int hC;
   private int hs;
   private boolean dX;
   private boolean iq;
   private long bm;
   private final List<SpeedMinePlusPlus.PendingMineRequest> zR;
   private SpeedMinePlusPlus.FastPairPhase Ai;
   private BlockPos Hm;
   private int DV;
   private int zv;
   private int ue;
   private boolean mz;
   private int ng;
   private int g;
   private long gY;

   public SpeedMinePlusPlus() {
      super(AliceAddon.PVP_CATEGORY, "spe\udd28d-mine++", "Allows you to mine blocks without holding a pickaxe.");
      this.GF = this.settings.getDefaultGroup();
      this.uR = this.settings.createGroup("Render");
      this.my = this.GF
         .add(((Builder)((Builder)new Builder().name("range")).description("Range to ac\udc84ivate use at.")).defaultValue(5.4).min(0.0).sliderMax(7.0).build());
      this.qB = this.GF
         .add(
            ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("auto-switch"))
                     .description("Automatic\uda32lly switches to the best tool."))
                  .defaultValue(true))
               .build()
         );
      this.HA = this.GF
         .add(
            ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("break-syn\ud8b6"))
                     .description("Attempts\udb63to p\uddf2e\udf60ent you from r\ud821bb\udb1erbanding \udb54xtra hard. May result in kicks\udef5"))
                  .defaultValue(true))
               .build()
         );
      this.fV = this.GF
         .add(
            ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("f\udc5cstbreak"))
                     .description("Pre-switches to your pickaxe w\udc41en the singlebreak\uddc6block is almost done, for more responsive breaking."))
                  .defaultValue(true))
               .build()
         );
      this.Ig = this.GF
         .add(
            ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("predict-rebre\uddb2k-air"))
                     .description("Sets rebroken blocks\udcaato air client-side immedi\udac1tely after se\ude42din\udaf7 the break packet."))
                  .defaultValue(true))
               .build()
         );
      this.CO = this.uR
         .add(
            ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("do-render"))
                     .description("Renders the blocks in queue to be broken."))
                  .defaultValue(true))
               .build()
         );
      this.Ax = this.uR
         .add(
            ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("render-block"))
                     .description("Whether to render\ude08the block being broken."))
                  .defaultValue(true))
               .build()
         );
      this.dF = this.uR
         .add(
            ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                           .name("shape-mode"))
                        .description("How the sh\udc95pes are render\udc82d."))
                     .defaultValue(ShapeMode.Both))
                  .visible(this.Ax::get))
               .build()
         );
      this.fc = this.uR
         .add(
            ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                        .name("side-color"))
                     .description("The sid\ud9f0 color of the render\udf42ng."))
                  .defaultValue(new SettingColor(189, 153, 255, 50))
                  .visible(() -> (Boolean)this.Ax.get() && ((ShapeMode)this.dF.get()).sides()))
               .build()
         );
      this.Gg = this.uR
         .add(
            ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                        .name("\ud884ine-col\udb37r"))
                     .description("The line color of the ren\udb26erin\ude21."))
                  .defaultValue(new SettingColor(189, 153, 255, 190))
                  .visible(() -> (Boolean)this.Ax.get() && ((ShapeMode)this.dF.get()).lines()))
               .build()
         );
      this.kQ = this.uR
         .add(
            ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                        .name("que\ud93ced-side-color"))
                     .description("The side c\udaa5lor of queued mine render\udc12."))
                  .defaultValue(new SettingColor(255, 64, 64, 20))
                  .visible(() -> (Boolean)this.Ax.get() && ((ShapeMode)this.dF.get()).sides()))
               .build()
         );
      this.ka = this.uR
         .add(
            ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                        .name("queued-line-color"))
                     .description("The line color of queued mine renders."))
                  .defaultValue(new SettingColor(255, 64, 64, 90))
                  .visible(() -> (Boolean)this.Ax.get() && ((ShapeMode)this.dF.get()).lines()))
               .build()
         );
      this.tV = this.uR
         .add(
            ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                        .name("progress-co\uda20or"))
                     .description("Fil\udb58 color o\uddb1 the\uda8efloor-up break-progress bar."))
                  .defaultValue(new SettingColor(255, 170, 60, 130))
                  .visible(this.Ax::get))
               .build()
         );
      this.ex = this.uR
         .add(
            ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                        .name("rebreak-color"))
                     .description("Outline color \udce1f the armed rebrea\ude94 lane."))
                  .defaultValue(new SettingColor(80, 255, 140, 255))
                  .visible(this.Ax::get))
               .build()
         );
      this.qC = this.uR
         .add(
            ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("pulse"))
                     .description("Emit a fading pulse where a bloc\udadc finishes breaking."))
                  .defaultValue(true))
               .build()
         );
      this.Bs = this.uR
         .add(
            ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("progress-text"))
                     .description("Draw the break percentage above the mined block."))
                  .defaultValue(true))
               .build()
         );
      this.bb = this.uR
         .add(
            ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("debug-render-primary"))
                     .description("Render the primary block differently for debuggin\udc8e."))
                  .defaultValue(false))
               .build()
         );
      this.s = 0.0;
      this.vB = false;
      this.bP = false;
      this.hC = -1;
      this.hs = -1;
      this.dX = true;
      this.zR = new ArrayList<>(2);
      this.Ai = SpeedMinePlusPlus.FastPairPhase.NONE;
      this.DV = -1;
      this.zv = -1;
      this.ue = -1;
      this.ng = -1;
      this.g = -1;
      this.s = getCurrentGameTickCalculated();
   }

   @AliceEventHandler
   private void IT(Pre var1) {
      this.s = getCurrentGameTickCalculated();
      this.jl++;
      this.Ks();
      if (this.Jb() && (this.mc.world.getBlockState(this.sk.bu).isAir() || !BlockUtils.canBreak(this.sk.bu))) {
         this.Kr(this.sk.bu);
         MeteorClient.EVENT_BUS.post(new AliceTickEvent.Post(this.sk.bu, false));
         this.Jd(false);
      }

      label129:
      if (this.Ac != null) {
         int var10000 = this.mc.world.getBlockState(this.Ac.bu).isAir() ? '钘' : '钗';

         label126:
         while (true) {
            switch (var10000) {
               case 38039:
                  if (BlockUtils.canBreak(this.Ac.bu)) {
                     break label129;
                  }
                  break label126;
               case 38040:
                  var10000 = 38038;
                  break;
               default:
                  break label126;
            }
         }

         this.Ac.CQ = true;
      }

      if (this.Ac != null && this.Ac.CQ && !this.dX) {
         this.Ac = null;
      }

      if (this.Jc() && this.Ac.FK > 10) {
         int var6 = this.Je() ? 15133 : 15132;

         label114:
         while (true) {
            switch (var6) {
               case 15132:
                  this.Ac.IQ();
                  this.Ac = null;
                  break label114;
               case 15133:
                  var6 = 15131;
                  break;
               default:
                  break label114;
            }
         }
      }

      if (this.JM(this.mc.player.isUsingItem())) {
         if (this.Ja()) {
            this.JG();
         }
      } else {
         BlockState var3;
         if (this.Jb() && this.sk.oO <= 15 && !(var3 = this.mc.world.getBlockState(this.sk.bu)).isAir()) {
            FindItemResult var2 = InvUtils.findFastestTool(var3);
            if (this.sk.IK(false) && !this.mc.player.isUsingItem()) {
               this.JE(var2, false);
               MeteorClient.EVENT_BUS.post(new AliceTickEvent.Pre(this.sk.bu, false));
            }

            if (this.sk.IK(false) && (!var2.found() || this.JF(var2.slot()))) {
               this.sk.oO++;
            }
         }

         if (this.Ac != null && !(var3 = this.mc.world.getBlockState(this.Ac.bu)).isAir()) {
            FindItemResult var4 = InvUtils.findFastestTool(var3);
            if (this.Ac.IK(true) && !this.mc.player.isUsingItem()) {
               if (this.Jf(this.Ac.bu)) {
                  this.JE(var4, true);
                  MeteorClient.EVENT_BUS.post(new AliceTickEvent.Pre(this.Ac.bu, true));
                  this.Ac.IM();
                  if ((Boolean)this.Ig.get() && this.Ac != null && this.Ac.CQ) {
                     this.Kk(this.Ac.bu);
                  }
               } else {
                  this.Ac.IQ();
                  this.Ac = null;
               }
            }
         }

         if (this.Jb() && this.sk.oO > 15) {
            if (this.Jf(this.sk.bu)) {
               if (!this.sk.Hk) {
                  this.sk.IL(true);
               } else {
                  this.sk.IM();
               }
            } else {
               this.sk.IQ();
               this.sk = null;
            }
         }

         this.Jv();
         if (this.Ja()) {
            this.JG();
         }
      }
   }

   @AliceEventHandler
   private void IU(Receive var1) {
      BlockState var3;
      if (var1.packet instanceof BlockUpdateS2CPacket var4 && this.Je() && var4.getPos().equals(this.Ac.bu) && !(var3 = var4.getState()).isAir()) {
         FindItemResult var5 = InvUtils.findFastestTool(var3);
         this.Ac.IR();
         boolean var6 = this.JE(var5, true);
         MeteorClient.EVENT_BUS.post(new AliceTickEvent.Pre(this.Ac.bu, true));
         this.Ac.IM();
         if ((Boolean)this.Ig.get() && this.Ac != null && this.Ac.CQ) {
            this.Kk(this.Ac.bu);
         }

         if (var6) {
            this.JG();
         }
      }
   }

   public void IV(BlockPos var1, double var2) {
      this.IY(var1, Direction.UP, var2, false, SpeedMinePlusPlus.MineRequestSource.Module);
   }

   public void IW(BlockPos var1, double var2) {
      this.IY(var1, Direction.UP, var2, true, SpeedMinePlusPlus.MineRequestSource.Module);
   }

   public void IX(BlockPos var1, Direction var2, double var3) {
      this.IY(var1, var2, var3, false, SpeedMinePlusPlus.MineRequestSource.Module);
   }

   private boolean IY(BlockPos var1, Direction var2, double var3, boolean var5, SpeedMinePlusPlus.MineRequestSource var6) {
      if (!this.isActive()) {
         return false;
      } else if (this.mc.world == null || this.mc.player == null) {
         return false;
      } else if (var1 == null) {
         return false;
      } else {
         var1 = var1.toImmutable();
         if (var6 == SpeedMinePlusPlus.MineRequestSource.Player) {
            this.JA(var1);
         } else {
            char var12 = '\ue69f';
            if (this.Jz()) {
               return false;
            }
         }

         if (this.Jg(var1)) {
            return false;
         } else {
            Direction var7 = this.Kc(var1, var2);
            BlockState var8 = this.mc.world.getBlockState(var1);
            if (!BlockUtils.canBreak(var1, var8)) {
               return false;
            } else if (!this.Jf(var1)) {
               return false;
            } else if (this.Kh(var1, var8)) {
               return this.Ki(var1, var7, var8);
            } else if (this.Ai == SpeedMinePlusPlus.FastPairPhase.NONE) {
               if (this.JI()) {
                  return this.Ju(var1, var7, var3, var5, var6);
               } else {
                  if (!this.Jb()) {
                     boolean var9 = this.Ac != null && !this.Je();
                     if (var9 && !this.JC(this.Ac, var3, var6)) {
                        return false;
                     }

                     this.s -= 0.1;
                     this.sk = new SpeedMinePlusPlus.SpeedMinePlusPlusBlock(var1, var7, var3, var5, var6);
                     int var11 = this.JK(var1, var8, var5);
                     if (var11 >= 0) {
                        this.sk.dK = true;
                        this.sk.Fh = var11;
                        this.Ai = SpeedMinePlusPlus.FastPairPhase.iz;
                        this.Hm = var1;
                        this.DV = var11;
                        this.ue = this.Kx();
                     }

                     if (!this.sk.IL(true)) {
                        this.sk = null;
                        this.Ka(true, true);
                        return false;
                     }

                     if (var9 && !this.JH()) {
                        this.Ac.IL(false);
                     }
                  }

                  int var10000 = !this.Jg(var1) ? 3090 : 3089;

                  while (true) {
                     switch (var10000) {
                        case 3089:
                           return true;
                        case 3090:
                           var10000 = 3088;
                           break;
                        default:
                           if (this.Ac != null && this.sk != null && (this.JC(this.Ac, var3, var6) || this.Je()) && this.sk.getBreakProgress() <= 0.8) {
                              this.Ac = null;
                           }

                           if (this.Ac == null) {
                              if (this.JI()) {
                                 return this.Ju(var1, var7, var3, var5, var6);
                              }

                              this.Ac = new SpeedMinePlusPlus.SpeedMinePlusPlusBlock(var1, var7, var3, var5, var6);
                              if (!this.Ac.IL(false)) {
                                 this.Ac = null;
                                 return false;
                              }

                              return true;
                           }

                           return false;
                     }
                  }
               }
            } else {
               return this.JL(var1, var7, var3, var5, var6);
            }
         }
      }
   }

   @AliceEventHandler
   public void IZ(StartBreakingBlockEvent var1) {
      var1.cancel();
      this.IY(var1.blockPos, var1.direction, 100.0, false, SpeedMinePlusPlus.MineRequestSource.Player);
   }

   public boolean Ja() {
      boolean var1 = this.vB || this.bP;
      if (this.Jb() && this.sk.IK(false)) {
         var1 = false;
      }

      return var1;
   }

   public boolean Jb() {
      return this.sk != null;
   }

   public boolean Jc() {
      return this.Ac != null && !this.Ac.CQ;
   }

   public void Jd(boolean var1) {
      if (this.Jb()) {
         int var10000 = !var1 ? '\ue989' : '\ue988';

         label21:
         while (true) {
            switch (var10000) {
               case 59784:
                  this.sk.IQ();
                  break label21;
               case 59785:
                  var10000 = 59783;
                  break;
               default:
                  break label21;
            }
         }

         this.sk = null;
      }
   }

   public BlockPos getDelayedDestroyBlockPos() {
      return this.sk == null ? null : this.sk.bu;
   }

   public double getDelayedDestroyProgress() {
      return this.sk == null ? 0.0 : this.sk.getBreakProgress();
   }

   public BlockPos getRebreakBlockPos() {
      return this.Ac == null ? null : this.Ac.bu;
   }

   public double getRebreakBlockProgress() {
      return this.Ac == null ? 0.0 : this.Ac.getBreakProgress();
   }

   public boolean Je() {
      return this.dX && this.Ac != null ? this.Ac.CQ : false;
   }

   public void setAllowRebreakLoop(boolean var1) {
      this.dX = var1;
      if (!var1 && this.Ac != null && this.Ac.CQ) {
         this.Ac = null;
      }
   }

   public boolean Jf(BlockPos var1) {
      return new Box(var1).squaredMagnitude(this.mc.player.getEyePos()) <= (Double)this.my.get() * (Double)this.my.get();
   }

   public boolean Jg(BlockPos var1) {
      if (var1 == null) {
         return false;
      } else {
         short var8;
         if (this.Ac != null) {
            if (var1.equals(this.Ac.bu)) {
               return true;
            }
         } else {
            while (var8 == 9437) {
               var8 = 9435;
            }
         }

         return this.sk != null && var1.equals(this.sk.bu) || this.Jy(var1);
      }
   }

   public int getImmediateSlots() {
      if (!this.isActive() || this.mc.player == null || this.mc.world == null) {
         return 0;
      } else if (this.JI()) {
         return 0;
      } else if (this.Jb()) {
         return this.Ac != null && !this.Je() ? 0 : 1;
      } else {
         return this.Ac != null && !this.Je() ? 1 : 2;
      }
   }

   public boolean Jh(BlockPos var1, Direction var2, BlockPos var3, Direction var4, double var5, boolean var7) {
      if (this.isActive() && this.mc.player != null && this.mc.world != null && var1 != null && !this.Jz()) {
         BlockPos var8 = var1.toImmutable();
         BlockPos var9 = var3 != null && !var3.equals(var8) ? var3.toImmutable() : null;
         if (!this.Js(var8)) {
            return false;
         } else {
            if (var9 != null && !this.Js(var9)) {
               var9 = null;
            }

            BlockPos var10 = this.Jr();
            if (!var7 && var10 != null && !var10.equals(var8) && !var10.equals(var9)) {
               return false;
            } else {
               if (var7) {
                  this.Jt(var8, var9);
               }

               Direction var11 = this.Kc(var8, var2);
               boolean var12 = this.Jg(var8) || this.IY(var8, var11, var5, false, SpeedMinePlusPlus.MineRequestSource.Module);
               if (!var12) {
                  return false;
               } else {
                  if (var9 != null) {
                     Direction var13 = this.Kc(var9, var4);
                     this.IY(var9, var13, var5 - 0.01, false, SpeedMinePlusPlus.MineRequestSource.Module);
                  }

                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   public void Ji() {
      this.zR.removeIf(var0 -> var0 != null && var0.source() == SpeedMinePlusPlus.MineRequestSource.Module);
   }

   public BlockPos Jj() {
      BlockPos var1 = this.Jr();
      if (var1 != null) {
         return var1;
      } else if (this.sk != null && this.sk.mo == SpeedMinePlusPlus.MineRequestSource.Module) {
         return this.sk.bu;
      } else {
         return this.Ac != null && this.Ac.mo == SpeedMinePlusPlus.MineRequestSource.Module ? this.Ac.bu : null;
      }
   }

   public BlockPos Jk() {
      BlockPos var1 = this.Jj();
      if (this.sk != null && this.sk.mo == SpeedMinePlusPlus.MineRequestSource.Module && !this.sk.bu.equals(var1)) {
         return this.sk.bu;
      } else if (this.Ac != null && this.Ac.mo == SpeedMinePlusPlus.MineRequestSource.Module && !this.Ac.bu.equals(var1)) {
         return this.Ac.bu;
      } else {
         for (SpeedMinePlusPlus.PendingMineRequest var3 : this.zR) {
            if (var3 != null && var3.source() == SpeedMinePlusPlus.MineRequestSource.Module && !var3.pos().equals(var1)) {
               return var3.pos();
            }
         }

         return null;
      }
   }

   public boolean Jl() {
      return this.Jm() || this.Ai != SpeedMinePlusPlus.FastPairPhase.NONE;
   }

   public boolean Jm() {
      if (this.sk != null && this.sk.mo == SpeedMinePlusPlus.MineRequestSource.Module) {
         return true;
      } else if (this.Ac != null && this.Ac.mo == SpeedMinePlusPlus.MineRequestSource.Module) {
         return true;
      } else {
         for (SpeedMinePlusPlus.PendingMineRequest var2 : this.zR) {
            if (var2 != null) {
               if (var2.source() == SpeedMinePlusPlus.MineRequestSource.Module) {
                  return true;
               }

               short var3;
               while (var3 == 18258) {
                  var3 = 18256;
               }
            }
         }

         return false;
      }
   }

   public boolean Jn() {
      return this.Ac != null && this.Ac.mo == SpeedMinePlusPlus.MineRequestSource.Module && this.Ac.CQ;
   }

   public double Jo() {
      return (Double)this.my.get();
   }

   public boolean Jp() {
      return this.isActive();
   }

   public boolean Jq(BlockPos var1) {
      return this.Js(var1);
   }

   private BlockPos Jr() {
      if (this.mc.world == null) {
         return null;
      } else if (this.sk != null && this.sk.mo == SpeedMinePlusPlus.MineRequestSource.Module && !this.mc.world.isAir(this.sk.bu)) {
         return this.sk.bu;
      } else {
         if (this.Ac != null) {
            if (this.Ac.mo == SpeedMinePlusPlus.MineRequestSource.Module) {
               if (!this.mc.world.isAir(this.Ac.bu)) {
                  return this.Ac.bu;
               }
            } else {
               char var1 = '귮';
            }
         }

         return null;
      }
   }

   private boolean Js(BlockPos var1) {
      if (var1 != null && this.mc.world != null && this.mc.player != null) {
         BlockState var2 = this.mc.world.getBlockState(var1);
         return BlockUtils.canBreak(var1, var2) && this.Jf(var1);
      } else {
         return false;
      }
   }

   private void Jt(BlockPos var1, BlockPos var2) {
      this.zR
         .removeIf(
            var2x -> var2x != null && var2x.source() == SpeedMinePlusPlus.MineRequestSource.Module && !var2x.pos().equals(var1) && !var2x.pos().equals(var2)
         );
      if (this.sk != null && this.sk.mo == SpeedMinePlusPlus.MineRequestSource.Module && !this.sk.bu.equals(var1) && !this.sk.bu.equals(var2)) {
         this.sk.IQ();
         this.sk = null;
      }

      if (this.Ac != null) {
         if (this.Ac.mo == SpeedMinePlusPlus.MineRequestSource.Module && !this.Ac.bu.equals(var1) && !this.Ac.bu.equals(var2)) {
            this.Ac.IQ();
            this.Ac = null;
         }
      } else {
         char var3 = '챪';
      }

      if (this.Ai != SpeedMinePlusPlus.FastPairPhase.NONE && this.JB()) {
         this.Ka(true, true);
      }
   }

   private boolean Ju(BlockPos var1, Direction var2, double var3, boolean var5, SpeedMinePlusPlus.MineRequestSource var6) {
      if (var1 != null && var2 != null) {
         BlockPos var7 = var1.toImmutable();

         for (int var8 = 0; var8 < this.zR.size(); var8++) {
            SpeedMinePlusPlus.PendingMineRequest var9 = this.zR.get(var8);
            if (var9 != null && var7.equals(var9.pos())) {
               if (!this.JD(var9, var3, var6)) {
                  return false;
               }

               this.zR.set(var8, new SpeedMinePlusPlus.PendingMineRequest(var7, var2, var3, var5, var6));
               return true;
            }
         }

         if (this.zR.size() < 2) {
            this.zR.add(new SpeedMinePlusPlus.PendingMineRequest(var7, var2, var3, var5, var6));
            return true;
         } else {
            SpeedMinePlusPlus.PendingMineRequest var10 = this.zR.get(this.zR.size() - 1);
            if (!this.JD(var10, var3, var6)) {
               return false;
            } else {
               this.zR.set(this.zR.size() - 1, new SpeedMinePlusPlus.PendingMineRequest(var7, var2, var3, var5, var6));
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private void Jv() {
      if (!this.zR.isEmpty() && this.mc.player != null && this.mc.world != null) {
         this.Jw();
         if (!this.zR.isEmpty()) {
            if (!this.mc.player.isUsingItem() && !this.JI()) {
               while (!this.zR.isEmpty() && !this.JI() && this.JJ()) {
                  SpeedMinePlusPlus.PendingMineRequest var2 = this.zR.remove(0);
                  if (var2 != null
                     && !this.Jx(var2.pos())
                     && !this.IY(var2.pos(), var2.direction(), var2.priority(), var2.quietPackets(), var2.source())
                     && !this.Jx(var2.pos())) {
                     this.zR.add(0, var2);
                     return;
                  }
               }
            }
         }
      }
   }

   private void Jw() {
      if (this.mc.world == null) {
         this.zR.clear();
      } else {
         this.zR.removeIf(var1 -> {
            if (var1 != null && !this.Jx(var1.pos())) {
               BlockState var2 = this.mc.world.getBlockState(var1.pos());
               return !BlockUtils.canBreak(var1.pos(), var2) || !this.Jf(var1.pos());
            } else {
               return true;
            }
         });
      }
   }

   private boolean Jx(BlockPos var1) {
      return var1 == null ? false : this.Ac != null && var1.equals(this.Ac.bu) || this.sk != null && var1.equals(this.sk.bu);
   }

   private boolean Jy(BlockPos var1) {
      if (var1 == null) {
         return false;
      } else {
         short var4 = 27583;

         for (SpeedMinePlusPlus.PendingMineRequest var3 : this.zR) {
            if (var3 != null && var1.equals(var3.pos())) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean Jz() {
      if (this.sk != null && this.sk.mo == SpeedMinePlusPlus.MineRequestSource.Player) {
         return true;
      } else if (this.Ac != null && this.Ac.mo == SpeedMinePlusPlus.MineRequestSource.Player) {
         return true;
      } else {
         for (SpeedMinePlusPlus.PendingMineRequest var2 : this.zR) {
            if (var2 != null && var2.source() == SpeedMinePlusPlus.MineRequestSource.Player) {
               return true;
            }
         }

         return false;
      }
   }

   private void JA(BlockPos var1) {
      if (var1 != null) {
         this.zR.removeIf(var0 -> var0 != null && var0.source() == SpeedMinePlusPlus.MineRequestSource.Module);
         if (this.Ai != SpeedMinePlusPlus.FastPairPhase.NONE && this.JB()) {
            this.Ka(true, true);
         }

         if (this.sk != null && this.sk.mo == SpeedMinePlusPlus.MineRequestSource.Module) {
            if (var1.equals(this.sk.bu) && !this.sk.dK) {
               this.sk.mo = SpeedMinePlusPlus.MineRequestSource.Player;
            } else {
               this.sk.IQ();
               this.sk = null;
            }
         }

         if (this.Ac != null && this.Ac.mo == SpeedMinePlusPlus.MineRequestSource.Module) {
            if (var1.equals(this.Ac.bu) && !this.Ac.dK) {
               this.Ac.mo = SpeedMinePlusPlus.MineRequestSource.Player;
            } else {
               this.Ac.IQ();
               this.Ac = null;
            }
         }
      }
   }

   private boolean JB() {
      return this.sk != null && this.sk.dK && this.sk.mo == SpeedMinePlusPlus.MineRequestSource.Module
         || this.Ac != null && this.Ac.dK && this.Ac.mo == SpeedMinePlusPlus.MineRequestSource.Module;
   }

   private boolean JC(SpeedMinePlusPlus.SpeedMinePlusPlusBlock var1, double var2, SpeedMinePlusPlus.MineRequestSource var4) {
      if (var1 == null) {
         return true;
      } else if (var4 == SpeedMinePlusPlus.MineRequestSource.Player && var1.mo == SpeedMinePlusPlus.MineRequestSource.Module) {
         return true;
      } else {
         return var4 == SpeedMinePlusPlus.MineRequestSource.Module && var1.mo == SpeedMinePlusPlus.MineRequestSource.Player ? false : var2 >= var1.uL;
      }
   }

   private boolean JD(SpeedMinePlusPlus.PendingMineRequest var1, double var2, SpeedMinePlusPlus.MineRequestSource var4) {
      if (var1 == null) {
         return true;
      } else if (var4 == SpeedMinePlusPlus.MineRequestSource.Player && var1.source() == SpeedMinePlusPlus.MineRequestSource.Module) {
         return true;
      } else {
         int var10000 = var4 != SpeedMinePlusPlus.MineRequestSource.Module ? '覥' : '覤';

         while (true) {
            switch (var10000) {
               case 35236:
                  if (var1.source() == SpeedMinePlusPlus.MineRequestSource.Player) {
                     return false;
                  }

                  return var2 >= var1.priority();
               case 35237:
                  var10000 = 35235;
                  break;
               default:
                  return var2 >= var1.priority();
            }
         }
      }
   }

   private boolean JE(FindItemResult var1, boolean var2) {
      if (!(Boolean)this.qB.get() || var1 == null || !var1.found()) {
         return false;
      } else if (this.JF(var1.slot())) {
         return false;
      } else if (!var2 || !this.vB && !this.bP) {
         if (var1.slot() >= 0 && var1.slot() <= 8) {
            this.hC = this.Kx();
            this.hs = var1.slot();
            this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.hs));
            this.bP = true;
         } else {
            InvUtils.swap(var1.slot(), true);
            this.vB = true;
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean JF(int var1) {
      if (this.mc.player == null) {
         return false;
      } else {
         return this.Kx() == var1 ? true : this.bP && this.hs == var1;
      }
   }

   private void JG() {
      if (this.bP) {
         if (this.hC >= 0 && this.hC <= 8) {
            this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.hC));
         }

         this.bP = false;
         this.hC = -1;
         this.hs = -1;
      }

      if (this.vB) {
         InvUtils.swapBack();
         this.vB = false;
      }
   }

   private boolean JH() {
      return this.bm == 0L ? false : System.nanoTime() - this.bm < 300000000L;
   }

   private boolean JI() {
      return this.JH();
   }

   private boolean JJ() {
      return !this.Jb() ? true : this.Ac == null || this.Je();
   }

   private int JK(BlockPos var1, BlockState var2, boolean var3) {
      if (var3) {
         return -1;
      } else if (this.Ai != SpeedMinePlusPlus.FastPairPhase.NONE || this.Jb() || this.Ac != null || !this.zR.isEmpty()) {
         return -1;
      } else if (!this.vB && !this.bP && !this.mz) {
         int var4 = this.JX(var1, var2);
         if (var4 >= 0 && this.JS(var1, var2, var4)) {
            int var5 = this.JT(var1, var2, var4);
            int var10000 = var5 >= 0 ? '\ue9ac' : '\ue9ab';

            while (true) {
               switch (var10000) {
                  case 59819:
                     return -1;
                  case 59820:
                     var10000 = 59818;
                     break;
                  default:
                     var10000 = this.JV(var1, var2, var4, -1, var5) < 0 ? '\ue4e2' : '\ue4e1';

                     while (true) {
                        switch (var10000) {
                           case 58593:
                              return var4;
                           case 58594:
                              var10000 = 58592;
                              break;
                           default:
                              return -1;
                        }
                     }
               }
            }
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private boolean JL(BlockPos var1, Direction var2, double var3, boolean var5, SpeedMinePlusPlus.MineRequestSource var6) {
      if (!var5 && var1 != null && var2 != null && this.mc.world != null) {
         if (this.Ai != SpeedMinePlusPlus.FastPairPhase.iz) {
            char var16;
            if (this.Ai != SpeedMinePlusPlus.FastPairPhase.WAITING_TO_PARK) {
               if (this.Ai != SpeedMinePlusPlus.FastPairPhase.kW) {
                  return false;
               }
            } else {
               while (var16 == '\uf5ed') {
                  var16 = '\uf5eb';
               }
            }
         }

         if (this.sk != null && !this.JC(this.sk, var3, var6)) {
            return false;
         } else if (this.JN() && !var1.equals(this.Hm)) {
            BlockState var7 = this.mc.world.getBlockState(var1);
            int var8 = this.JX(var1, var7);
            if (var8 >= 0 && this.JS(var1, var7, var8)) {
               BlockState var9 = this.mc.world.getBlockState(this.Hm);
               int var10 = this.JT(this.Hm, var9, this.DV);
               if (var10 < 0) {
                  return false;
               } else {
                  int var10000 = this.JV(this.Hm, var9, this.DV, var8, Math.max((double)var10, this.s - this.sk.vZ)) >= 0 ? '\uf550' : '\uf54f';

                  while (true) {
                     int var12 = var10000;
                     if (var12 == 62799) {
                        return false;
                     }

                     if (var12 != 62800) {
                        if (!this.Ju(var1, var2, var3, false, var6)) {
                           return false;
                        }

                        if (this.Ai == SpeedMinePlusPlus.FastPairPhase.iz) {
                           this.Ai = SpeedMinePlusPlus.FastPairPhase.WAITING_TO_PARK;
                        }

                        return true;
                     }

                     var10000 = 62798;
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean JM(boolean var1) {
      if (this.Ai == SpeedMinePlusPlus.FastPairPhase.NONE) {
         return false;
      } else if (this.mc.player == null || this.mc.world == null || var1) {
         this.Ka(true, true);
         return false;
      } else if (this.Ai == SpeedMinePlusPlus.FastPairPhase.iz) {
         if (!this.JN()) {
            this.Ka(true, true);
            return false;
         } else {
            double var14 = Math.max(0.0, this.s - this.sk.vZ);
            if (var14 + 1.0E-4 < Math.min(Math.max(2, this.sk.getGrimRequiredTicks()), 10)) {
               return true;
            } else {
               this.Kb(true);
               return false;
            }
         }
      } else if (this.Ai == SpeedMinePlusPlus.FastPairPhase.WAITING_TO_PARK) {
         if (this.JN() && !this.zR.isEmpty()) {
            double var12 = Math.max(0.0, this.s - this.sk.vZ);
            int var13 = this.sk.getGrimRequiredTicks();
            if (var13 >= 0) {
               if (var12 + 1.0E-4 < var13) {
                  return true;
               } else {
                  SpeedMinePlusPlus.PendingMineRequest var5 = this.JQ();
                  if (var5 != null && this.JR(var5.pos()) && this.Jf(var5.pos())) {
                     BlockState var6 = this.mc.world.getBlockState(this.sk.bu);
                     BlockState var7 = this.mc.world.getBlockState(var5.pos());
                     int var8 = this.JX(var5.pos(), var7);
                     int var9 = this.JV(this.sk.bu, var6, this.DV, var8, var12);
                     if (var9 >= 0) {
                        return this.JP(var9);
                     } else {
                        this.Ka(true, true);
                        return false;
                     }
                  } else {
                     this.Ka(true, true);
                     return false;
                  }
               }
            } else {
               this.Ka(true, true);
               return false;
            }
         } else {
            this.Ka(true, false);
            return false;
         }
      } else if (this.Ai == SpeedMinePlusPlus.FastPairPhase.kW) {
         if (this.JO() && !this.zR.isEmpty()) {
            if (System.nanoTime() >= this.gY && !this.JH()) {
               SpeedMinePlusPlus.PendingMineRequest var11 = this.JQ();
               if (var11 != null && this.JR(var11.pos()) && this.Jf(var11.pos())) {
                  BlockState var3 = this.mc.world.getBlockState(var11.pos());
                  int var4 = this.JX(var11.pos(), var3);
                  if (var4 >= 0 && var4 != this.zv) {
                     this.zR.remove(var11);
                     this.JY(var4);
                     this.DV = var4;
                     this.Ac = new SpeedMinePlusPlus.SpeedMinePlusPlusBlock(var11.pos(), var11.direction(), var11.priority(), false, var11.source());
                     this.Ac.dK = true;
                     this.Ac.Fh = var4;
                     if (!this.Ac.IL(false)) {
                        this.Ac = null;
                        this.Ka(true, true);
                        return false;
                     } else {
                        this.Ai = SpeedMinePlusPlus.FastPairPhase.ACTIVE_SECOND;
                        return true;
                     }
                  } else {
                     this.Ka(true, true);
                     return false;
                  }
               } else {
                  this.Ka(true, true);
                  return false;
               }
            } else {
               return true;
            }
         } else {
            this.Ka(true, false);
            return false;
         }
      } else if (this.Ai == SpeedMinePlusPlus.FastPairPhase.ACTIVE_SECOND) {
         if (this.Ac != null && this.Ac.dK && !this.Ac.yf) {
            double var2 = Math.max(0.0, this.s - this.Ac.vZ);
            int var10000 = !(var2 + 1.0E-4 < 2.0) ? 4747 : 4746;

            while (true) {
               int var10 = var10000;
               if (var10 == 4746) {
                  return true;
               }

               if (var10 != 4747) {
                  this.Kb(true);
                  return false;
               }

               var10000 = 4745;
            }
         } else {
            this.Kb(true);
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean JN() {
      return this.sk != null && this.sk.dK && !this.sk.yf && this.Hm != null && this.Hm.equals(this.sk.bu) && this.JR(this.sk.bu) && this.Jf(this.sk.bu);
   }

   private boolean JO() {
      if (this.sk != null) {
         int var10000 = this.sk.dK ? '衲' : '衱';

         while (true) {
            int var1 = var10000;
            if (var1 == 34929) {
               if (this.sk.yf && this.Hm != null && this.Hm.equals(this.sk.bu) && this.JR(this.sk.bu) && this.Jf(this.sk.bu)) {
                  return true;
               }
               break;
            }

            if (var1 != 34930) {
               break;
            }

            var10000 = 34928;
         }
      }

      return false;
   }

   private boolean JP(int var1) {
      if (this.sk != null && this.mc.player != null) {
         this.JY(var1);
         this.zv = var1;
         this.sk.dK = false;
         this.sk.IM();
         if (!this.sk.yf) {
            this.Ka(true, true);
            return false;
         } else {
            this.Ai = SpeedMinePlusPlus.FastPairPhase.kW;
            this.gY = System.nanoTime() + 300000000L;
            return true;
         }
      } else {
         return false;
      }
   }

   private SpeedMinePlusPlus.PendingMineRequest JQ() {
      for (SpeedMinePlusPlus.PendingMineRequest var2 : this.zR) {
         if (var2 != null) {
            return var2;
         }
      }

      return null;
   }

   private boolean JR(BlockPos var1) {
      return var1 != null && this.mc.world != null && BlockUtils.canBreak(var1, this.mc.world.getBlockState(var1));
   }

   private boolean JS(BlockPos var1, BlockState var2, int var3) {
      if (var1 != null && var2 != null && !var2.isAir() && var3 >= 0 && var3 <= 8) {
         double var4 = this.getBreakingDeltaForSlot(var1, var2, var3);
         return Double.isFinite(var4) && var4 + 1.0E-4 >= 0.5 && var4 < 0.9999;
      } else {
         return false;
      }
   }

   private int JT(BlockPos var1, BlockState var2, int var3) {
      if (var1 != null && var2 != null && var3 >= 0 && var3 <= 8) {
         double var4 = this.getBreakingDeltaForSlot(var1, var2, var3);
         if (Double.isFinite(var4) && !(var4 <= 1.0E-4) && !(var4 >= 0.9999)) {
            int var6 = this.JU(var4);
            return var6 < 0 ? -1 : Math.max(2, var6);
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private int JU(double var1) {
      if (Double.isFinite(var1) && !(var1 <= 1.0E-4)) {
         double var3 = Math.ceil(1.0 / var1);
         return Double.isFinite(var3) && !(var3 > 2.147483647E9) ? Math.max(1, (int)var3) : -1;
      } else {
         return -1;
      }
   }

   private int JV(BlockPos var1, BlockState var2, int var3, int var4, double var5) {
      if (var1 != null && var2 != null && this.mc.player != null) {
         int var7 = this.JT(var1, var2, var3);
         if (var7 < 0) {
            return -1;
         } else {
            double var8 = Math.max((double)var7, var5) + 1.0;
            double var10 = var8 + this.JW() + 3.0;
            int var12 = this.Kx();
            int var13 = -1;
            double var14 = Double.POSITIVE_INFINITY;

            label57:
            for (int var16 = 0; var16 < 9; var16++) {
               int var10000 = var16 == var3 ? '\ue162' : '\ue161';

               while (true) {
                  switch (var10000) {
                     case 57697:
                        double var17;
                        if (var16 != var4
                           && !((var17 = this.getBreakingDeltaForSlot(var1, var2, var16)) * var8 >= 0.6999)
                           && !(var17 * var10 >= 0.9999)
                           && (var17 < var14 - 1.0E-4 || Math.abs(var17 - var14) <= 1.0E-4 && var16 == var12)) {
                           var13 = var16;
                           var14 = var17;
                        }
                        continue label57;
                     case 57698:
                        var10000 = 57696;
                        break;
                     default:
                        continue label57;
                  }
               }
            }

            return var13;
         }
      } else {
         return -1;
      }
   }

   private int JW() {
      return (int)Math.ceil(6.0);
   }

   private int JX(BlockPos var1, BlockState var2) {
      if (this.mc.player != null && var2 != null && !var2.isAir()) {
         int var3 = -1;
         double var4 = 0.0;

         for (int var6 = 0; var6 < 9; var6++) {
            double var7 = this.getBreakingDeltaForSlot(var1, var2, var6);
            if (var7 > var4) {
               var4 = var7;
               var3 = var6;
            }
         }

         return var3;
      } else {
         return -1;
      }
   }

   private double getBreakingDeltaForSlot(BlockPos var1, BlockState var2, int var3) {
      return var3 >= 0 && var3 <= 8 && var2 != null && !var2.isAir() ? this.getBreakDelta(this.getBlockBreakingSpeed(var3, var2), var2, var1) : 0.0;
   }

   private void JY(int var1) {
      if (this.mc.player != null && this.mc.getNetworkHandler() != null && var1 >= 0 && var1 <= 8) {
         if (!this.mz) {
            this.ng = this.Kx();
         }

         this.g = var1;
         this.mz = true;
         this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var1));
      }
   }

   private void JZ(boolean var1) {
      if (this.mz) {
         if (var1 && this.ng >= 0 && this.ng <= 8 && this.mc.getNetworkHandler() != null) {
            this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.ng));
         }

         this.mz = false;
         this.ng = -1;
         this.g = -1;
      }
   }

   private void Ka(boolean var1, boolean var2) {
      if (var2) {
         this.zR.clear();
      }

      this.Kb(var1);
   }

   private void Kb(boolean var1) {
      if (var1) {
         this.JZ(true);
      }

      this.Ai = SpeedMinePlusPlus.FastPairPhase.NONE;
      this.Hm = null;
      this.DV = -1;
      this.zv = -1;
      this.ue = -1;
      this.gY = 0L;
   }

   private Direction Kc(BlockPos var1, Direction var2) {
      if (this.mc.player != null && var1 != null) {
         return BlockHitHelper.DW(this.mc.player.getEyePos(), var1);
      } else if (var2 != null) {
         return var2;
      } else if (this.mc.player != null && var1 != null) {
         Vec3d var3 = Vec3d.ofCenter(var1);
         Vec3d var4 = this.mc.player.getEyePos();
         Direction var5 = Direction.getFacing(var4.x - var3.x, var4.y - var3.y, var4.z - var3.z);
         return var5 == null ? Direction.UP : var5;
      } else {
         return Direction.UP;
      }
   }

   private boolean Kd(Action var1, BlockPos var2, Direction var3) {
      return this.Ke(var1, var2, var3, true);
   }

   private boolean Ke(Action var1, BlockPos var2, Direction var3, boolean var4) {
      if (this.mc.getNetworkHandler() != null && this.mc.world != null && var2 != null) {
         int var5 = this.getSeq();
         Direction var6 = var3 == null ? Direction.DOWN : var3;
         PlayerActionC2SPacket var7 = new PlayerActionC2SPacket(var1, var2, var6, var5);
         this.iq = true;
         boolean var8 = GrimCombatCoordinator.xH(var7, () -> this.mc.getNetworkHandler().sendPacket(var7));
         this.iq = false;
         if (!var8) {
            return false;
         } else {
            if (var4 && (var1 == Action.START_DESTROY_BLOCK || var1 == Action.STOP_DESTROY_BLOCK)) {
               this.bm = System.nanoTime();
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean Kf(BlockPos var1, Direction var2) {
      if (this.mc.getNetworkHandler() != null && var1 != null) {
         PlayerActionC2SPacket var3 = new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, var1, Direction.DOWN);
         return GrimCombatCoordinator.xH(var3, () -> this.mc.getNetworkHandler().sendPacket(var3));
      } else {
         return false;
      }
   }

   private boolean Kg(BlockPos var1, Direction var2) {
      Direction var3 = this.Kc(var1, var2);
      BlockPos var4 = var1.up(1420);
      return this.Kd(Action.START_DESTROY_BLOCK, var1, var3)
         && this.Kd(Action.START_DESTROY_BLOCK, var4, Direction.DOWN)
         && this.Kd(Action.STOP_DESTROY_BLOCK, var1, var3);
   }

   private boolean Kh(BlockPos var1, BlockState var2) {
      return this.getBestBreakingDelta(var1, var2) >= 0.9999;
   }

   private double getBestBreakingDelta(BlockPos var1, BlockState var2) {
      if (this.mc.player != null && this.mc.world != null && var1 != null && var2 != null && !var2.isAir()) {
         FindItemResult var3 = InvUtils.findFastestTool(var2);
         int var4 = var3.found() ? var3.slot() : this.Kx();
         return this.getBreakDelta(this.getBlockBreakingSpeed(var4, var2), var2, var1);
      } else {
         return 0.0;
      }
   }

   private boolean Ki(BlockPos var1, Direction var2, BlockState var3) {
      if (var1 != null && var3 != null && BlockUtils.canBreak(var1, var3) && this.Jf(var1)) {
         FindItemResult var4 = InvUtils.findFastestTool(var3);
         boolean var5 = this.JE(var4, true);
         MeteorClient.EVENT_BUS.post(new AliceTickEvent.Pre(var1, true));
         boolean var6 = this.Ke(Action.START_DESTROY_BLOCK, var1, this.Kc(var1, var2), false);
         if (var6) {
            this.Kj(var1, !var4.found() ? -1 : var4.slot());
            this.Kr(var1);
            MeteorClient.EVENT_BUS.post(new AliceTickEvent.Post(var1, true));
         }

         if (var5) {
            this.JG();
         }

         return var6;
      } else {
         return false;
      }
   }

   private void Kj(BlockPos var1, int var2) {
      if (var1 != null) {
         if (!this.mc.isOnThread()) {
            BlockPos var9 = var1.toImmutable();
            this.mc.execute(() -> this.Kj(var9, var2));
         } else if (this.mc.player != null && this.mc.world != null) {
            BlockState var4 = this.mc.world.getBlockState(var1);
            if (!var4.isAir()) {
               ItemStack var3 = var2 >= 0 && var2 <= 8 ? this.mc.player.getInventory().getStack(var2) : this.mc.player.getMainHandStack();
               byte var12;
               if (var4.isToolRequired()) {
                  if (!var3.isSuitableFor(var4)) {
                     return;
                  }
               } else {
                  while (var12 == 37) {
                     var12 = 35;
                  }
               }

               Block var6 = var4.getBlock();
               if (!(var6 instanceof OperatorBlock) || this.mc.player.isCreativeLevelTwoOp()) {
                  var6.onBreak(this.mc.world, var1, var4, this.mc.player);
                  FluidState var7 = this.mc.world.getFluidState(var1);
                  if (this.mc.world.setBlockState(var1, var7.getBlockState(), 11)) {
                     var6.onBroken(this.mc.world, var1, var4);
                     this.Kl(var1, var4);
                  }
               }
            }
         }
      }
   }

   private boolean Kk(BlockPos var1) {
      if (var1 == null) {
         return false;
      } else if (!this.mc.isOnThread()) {
         BlockPos var5 = var1.toImmutable();
         this.mc.execute(() -> this.Kk(var5));
         return false;
      } else if (this.mc.world == null) {
         return false;
      } else {
         BlockState var2 = this.mc.world.getBlockState(var1);
         if (var2.isAir()) {
            return true;
         } else {
            BlockState var3 = this.mc.world.getFluidState(var1).getBlockState();
            boolean var4 = this.mc.world.setBlockState(var1, var3, 11);
            if (var4) {
               this.Kl(var1, var2);
            }

            return var4;
         }
      }
   }

   private void Kl(BlockPos var1, BlockState var2) {
      if (this.mc.world != null && var1 != null && var2 != null) {
         if (this.Km(var2) && var2.contains(Properties.FACING)) {
            if (!var2.contains(Properties.EXTENDED) || (Boolean)var2.get(Properties.EXTENDED)) {
               BlockPos var3 = var1.offset((Direction)var2.get(Properties.FACING));
               BlockState var4 = this.mc.world.getBlockState(var3);
               if (var4.isOf(Blocks.PISTON_HEAD) || var4.isOf(Blocks.MOVING_PISTON)) {
                  BlockState var5 = this.mc.world.getFluidState(var3).getBlockState();
                  this.mc.world.setBlockState(var3, var5, 11);
               }
            }
         }
      }
   }

   private boolean Km(BlockState var1) {
      return var1 != null && (var1.isOf(Blocks.PISTON) || var1.isOf(Blocks.STICKY_PISTON) || var1.isOf(Blocks.MOVING_PISTON));
   }

   @AliceEventHandler
   private void Kn(Render3DEvent var1) {
      if ((Boolean)this.CO.get()) {
         double var2 = getCurrentGameTickCalculated();
         if ((Boolean)this.Ax.get()) {
            if (this.sk != null) {
               this.sk.IS(var1, var2, false);
            }

            if (this.Ac != null) {
               this.Ac.IS(var1, var2, true);
            }

            for (SpeedMinePlusPlus.PendingMineRequest var5 : this.zR) {
               if (var5 != null && var5.pos() != null) {
                  var1.renderer.box(var5.pos(), (Color)this.kQ.get(), (Color)this.ka.get(), (ShapeMode)this.dF.get(), 0);
               }
            }
         }

         this.Ko(var1);
      }
   }

   private void Ko(Render3DEvent var1) {
      if ((Boolean)this.qC.get() && !this.zV.isEmpty()) {
         Color var2 = (Color)this.fc.get();
         Color var3 = (Color)this.Gg.get();

         for (SpeedMinePlusPlus.RenderPulse var5 : this.zV) {
            double var6 = this.Kt(var5, var1.tickDelta);
            if (!(var6 <= 0.0)) {
               double var8 = (1.0 - var6) * 0.16;
               Box var10 = new Box(var5.pos()).expand(var8);
               var1.renderer.box(var10, Kv(var2, var6), Kv(var3, var6), (ShapeMode)this.dF.get(), 0);
            }
         }
      }
   }

   @AliceEventHandler
   private void Kp(Render2DEvent var1) {
      if ((Boolean)this.CO.get() && (Boolean)this.Bs.get() && this.mc.world != null) {
         SpeedMinePlusPlus.SpeedMinePlusPlusBlock var2 = this.sk;
         if (var2 != null && var2.bu != null && !this.mc.world.getBlockState(var2.bu).isAir()) {
            this.Kq(var2);
         }
      }
   }

   private void Kq(SpeedMinePlusPlus.SpeedMinePlusPlusBlock var1) {
      Vec3d var2 = Vec3d.ofCenter(var1.bu);
      this.Ap.set(var2.x, var2.y, var2.z);
      if (NametagUtils.to2D(this.Ap, 1.0)) {
         TextRenderer var3 = TextRenderer.get();
         if (var3 != null) {
            int var4 = (int)Math.round(MathHelper.clamp(var1.getBreakProgress(), 0.0, 1.0) * 100.0);
            String var5 = var4 + "";
            Color var6 = Ku((Color)this.tV.get());
            NametagUtils.begin(this.Ap);
            var3.begin(1.0, false, true);
            double var7 = var3.getWidth(var5, true);
            double var9 = var3.getHeight(true);
            var3.render(var5, -var7 / 2.0, -var9 / 2.0, var6, true);
            var3.end();
            NametagUtils.end();
         }
      }
   }

   private void Kr(BlockPos var1) {
      int var10000 = !this.qC.get() ? 1998 : 1997;

      while (true) {
         switch (var10000) {
            case 1997:
               if (var1 != null) {
                  SpeedMinePlusPlus.RenderPulse var2 = this.zV.peekLast();
                  if (var2 != null && var2.startedTick() == this.jl && var2.pos().equals(var1)) {
                     return;
                  }

                  while (this.zV.size() >= 8) {
                     this.zV.removeFirst();
                  }

                  this.zV.addLast(new SpeedMinePlusPlus.RenderPulse(var1.toImmutable(), this.jl));
                  return;
               }

               return;
            case 1998:
               var10000 = 1996;
               break;
            default:
               return;
         }
      }
   }

   private void Ks() {
      if (!(Boolean)this.qC.get()) {
         this.zV.clear();
      } else {
         this.zV.removeIf(var1 -> this.jl - var1.startedTick() > 6);
      }
   }

   private double Kt(SpeedMinePlusPlus.RenderPulse var1, float var2) {
      double var3 = Math.max(0.0, (double)(this.jl - var1.startedTick()) + var2);
      return MathHelper.clamp(1.0 - var3 / 6.0, 0.0, 1.0);
   }

   private static Color Ku(Color var0) {
      return var0.a >= 255 ? var0 : new Color(var0.r, var0.g, var0.b, 255);
   }

   private static Color Kv(Color var0, double var1) {
      return !(var1 >= 0.999) ? new Color(var0.r, var0.g, var0.b, MathHelper.clamp((int)Math.round(var0.a * var1), 0, 255)) : var0;
   }

   @AliceEventHandler
   private void Kw(Send var1) {
      Packet var3 = var1.packet;
      PlayerActionC2SPacket var2;
      if (var3 instanceof PlayerActionC2SPacket && (var2 = (PlayerActionC2SPacket)var3).getAction() == Action.STOP_DESTROY_BLOCK) {
         int var10000 = !this.HA.get() ? 13505 : 13504;

         while (true) {
            switch (var10000) {
               case 13504:
                  if (!this.iq) {
                     if (!var2.getPos().equals(this.getRebreakBlockPos())) {
                        var10000 = !var2.getPos().equals(this.getDelayedDestroyBlockPos()) ? '쫛' : '쫚';

                        label31:
                        while (true) {
                           switch (var10000) {
                              case 51930:
                                 break label31;
                              case 51931:
                                 var10000 = 51929;
                                 break;
                              default:
                                 return;
                           }
                        }
                     }

                     this.Kf(var2.getPos(), Direction.DOWN);
                  }

                  return;
               case 13505:
                  var10000 = 13503;
                  break;
               default:
                  return;
            }
         }
      }
   }

   private static double getCurrentGameTickCalculated() {
      return (double)(System.nanoTime() - dT) / TimeUnit.MILLISECONDS.toNanos(50L);
   }

   private double getBreakDelta(double var1, BlockState var3, BlockPos var4) {
      float var5 = var3.getHardness(this.mc.world, var4);
      return var5 == -1.0F ? 0.0 : var1 / var5 / 30.0;
   }

   private double getBlockBreakingSpeed(int var1, BlockState var2) {
      double var5 = this.mc.player.getInventory().getStack(var1).getMiningSpeedMultiplier(var2);
      ItemStack var3;
      int var4;
      if (var5 > 1.0 && (var4 = Utils.getEnchantmentLevel(var3 = this.mc.player.getInventory().getStack(var1), Enchantments.EFFICIENCY)) > 0 && !var3.isEmpty()
         )
       {
         var5 += var4 * var4 + 1;
      }

      if (StatusEffectUtil.hasHaste(this.mc.player)) {
         var5 *= 1.0F + (StatusEffectUtil.getHasteAmplifier(this.mc.player) + 1) * 0.2F;
      }

      if (this.mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
         float var7 = switch (this.mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier() ^ 1222840938) {
            case 1222840936 -> 0.0027F;
            case 1222840938 -> 0.3F;
            case 1222840939 -> 0.09F;
            default -> 8.1E-4F;
         };
         var5 *= var7;
      }

      int var8 = !this.mc.player.isSubmergedIn(FluidTags.WATER) ? 19318 : 19317;

      label46:
      while (true) {
         switch (var8) {
            case 19317:
               var5 *= this.mc.player.getAttributeValue(EntityAttributes.SUBMERGED_MINING_SPEED);
               break label46;
            case 19318:
               var8 = 19316;
               break;
            default:
               break label46;
         }
      }

      if (!this.mc.player.isOnGround()) {
         var5 /= 5.0;
      }

      return var5;
   }

   private int Kx() {
      return this.mc.player.getInventory().getSelectedSlot();
   }

   private int getSeq() throws Throwable {
      if (EH != null && this.mc.world != null) {
         PendingUpdateManager var1 = (PendingUpdateManager)EH.invoke((ClientWorld)this.mc.world);
         return var1.incrementSequence().getSequence();
      } else {
         throw new IllegalStateException("Pending update manager is unavailable");
      }
   }

   private static MethodHandle Ky() {
      for (Method var3 : ClientWorld.class.getDeclaredMethods()) {
         Class[] var4 = var3.getParameterTypes();
         if (var3.getReturnType() == PendingUpdateManager.class && var4.length == 0) {
            short var6 = 17676;

            try {
               var3.setAccessible(true);
               return MethodHandles.lookup().unreflect(var3);
            } catch (RuntimeException | IllegalAccessException var7) {
            }
         }
      }

      return null;
   }

   private static enum FastPairPhase {
      NONE,
      iz,
      WAITING_TO_PARK,
      kW,
      ACTIVE_SECOND;
   }

   private static enum MineRequestSource {
      Player,
      Module;
   }

   private record PendingMineRequest(BlockPos pos, Direction direction, double priority, boolean quietPackets, SpeedMinePlusPlus.MineRequestSource source) {
   }

   private record RenderPulse(BlockPos pos, int startedTick) {
   }

   class SpeedMinePlusPlusBlock {
      public BlockPos bu;
      public Direction uO;
      public boolean bj = false;
      public int FK = 0;
      public int oO = 0;
      public boolean CQ = false;
      public boolean yf = false;
      public boolean dK = false;
      public int Fh = -1;
      public SpeedMinePlusPlus.MineRequestSource mo;
      private final boolean Hk;
      private double vZ = 0.0;
      private double ym = 0.0;
      private double uL = 0.0;

      public SpeedMinePlusPlusBlock(BlockPos var2, Direction var3, double var4) {
         this(var2, var3, var4, false, SpeedMinePlusPlus.MineRequestSource.Module);
      }

      public SpeedMinePlusPlusBlock(BlockPos var2, Direction var3, double var4, boolean var6, SpeedMinePlusPlus.MineRequestSource var7) {
         this.bu = var2;
         this.uO = var3;
         this.uL = var4;
         this.Hk = var6;
         this.mo = var7 == null ? SpeedMinePlusPlus.MineRequestSource.Module : var7;
      }

      public boolean IK(boolean var1) {
         double var3 = this.getBreakProgressSingleTick();
         boolean var2 = var3 >= 0.9999;
         if (this.dK) {
            int var10000 = this.getGrimDeadlineProgress(SpeedMinePlusPlus.this.s) >= 0.9999 ? 28671 : 28670;

            while (true) {
               int var8 = var10000;
               if (var8 != 28670) {
                  if (var8 == 28671) {
                     var10000 = 28669;
                     continue;
                  }
               } else if (this.FK <= 0) {
                  var9 = false;
                  break;
               }

               var9 = true;
               break;
            }

            return var9;
         } else {
            double var6 = var1 ? (!var2 ? 0.7 : 1.0) : 1.0 - (SpeedMinePlusPlus.this.fV.get() ? var3 / 2.0 : 0.0);
            return this.getBreakProgress() >= var6 || this.FK > 0;
         }
      }

      public boolean IL(boolean var1) {
         this.oO = 0;
         this.FK = 0;
         this.yf = false;
         this.vZ = SpeedMinePlusPlus.this.s;
         if (var1 && SpeedMinePlusPlus.this.Je()) {
            SpeedMinePlusPlus.this.Ac = null;
         }

         if (this.Hk) {
            this.bj = this.IN();
            return this.bj;
         } else {
            FindItemResult var3 = InvUtils.findFastestTool(SpeedMinePlusPlus.this.mc.world.getBlockState(this.bu));
            int var4 = this.Fh >= 0 ? this.Fh : (var3.found() ? var3.slot() : -1);
            if (this.Fh >= 0) {
               SpeedMinePlusPlus.this.JY(this.Fh);
            } else {
               SpeedMinePlusPlus.this.JE(var3, false);
            }

            this.ym = var4 >= 0 && var4 <= 8
               ? SpeedMinePlusPlus.this.getBreakingDeltaForSlot(this.bu, SpeedMinePlusPlus.this.mc.world.getBlockState(this.bu), var4)
               : SpeedMinePlusPlus.this.getBestBreakingDelta(this.bu, SpeedMinePlusPlus.this.mc.world.getBlockState(this.bu));
            boolean var2 = this.dK ? SpeedMinePlusPlus.this.Kd(Action.START_DESTROY_BLOCK, this.bu, this.uO) : SpeedMinePlusPlus.this.Kg(this.bu, this.uO);
            if (!var2) {
               this.bj = false;
               return false;
            } else {
               this.yf = !this.dK;
               this.bj = true;
               return true;
            }
         }
      }

      public void IM() {
         boolean var1 = !this.IP();
         if (!this.Hk) {
            if (SpeedMinePlusPlus.this.Ke(Action.STOP_DESTROY_BLOCK, this.bu, this.uO, var1)) {
               this.yf = true;
               this.FK++;
            }
         } else {
            if (this.IO(var1)) {
               this.yf = true;
            }

            this.FK++;
         }
      }

      private boolean IN() {
         SpeedMinePlusPlus.this.iq = true;

         boolean var3;
         try {
            boolean var1;
            this.yf = var1 = SpeedMinePlusPlus.this.Kg(this.bu, this.uO);
            var3 = var1;
         } finally {
            SpeedMinePlusPlus.this.iq = false;
         }

         return var3;
      }

      private boolean IO(boolean var1) {
         SpeedMinePlusPlus.this.iq = true;

         boolean var3;
         try {
            boolean var2 = SpeedMinePlusPlus.this.Ke(Action.STOP_DESTROY_BLOCK, this.bu, this.uO, var1);
            var3 = var2;
         } finally {
            SpeedMinePlusPlus.this.iq = false;
         }

         return var3;
      }

      private boolean IP() {
         return this.getBreakProgressSingleTick() >= 0.9999;
      }

      public void IQ() {
         SpeedMinePlusPlus.this.Kf(this.bu, this.uO);
      }

      public void IR() {
         this.yf = false;
      }

      public double getBreakProgress() {
         return this.getBreakProgress(SpeedMinePlusPlus.this.s);
      }

      public double getBreakProgress(double var1) {
         BlockState var3 = SpeedMinePlusPlus.this.mc.world.getBlockState(this.bu);
         FindItemResult var4 = InvUtils.findFastestTool(var3);
         int var5 = var4.found() ? var4.slot() : SpeedMinePlusPlus.this.Kx();
         double var6 = SpeedMinePlusPlus.this.getBlockBreakingSpeed(var5, var3);
         return Math.min(SpeedMinePlusPlus.this.getBreakDelta(var6, var3, this.bu) * (var1 - this.vZ), 1.0);
      }

      public double getBreakProgressSingleTick() {
         return this.getBreakProgress(this.vZ + 1.0);
      }

      public int getGrimRequiredTicks() {
         return SpeedMinePlusPlus.this.JU(this.ym);
      }

      public double getGrimDeadlineProgress(double var1) {
         int var3 = this.getGrimRequiredTicks();
         if (var3 < 0) {
            return 0.0;
         } else {
            double var4 = Math.max(0.0, var1 - this.vZ);
            return MathHelper.clamp(var4 / var3, 0.0, 1.0);
         }
      }

      public double getPriority() {
         return this.uL;
      }

      public void IS(Render3DEvent var1, double var2, boolean var4) {
         BlockPos var5 = this.bu;
         double var6 = MathHelper.clamp(!this.dK ? this.getBreakProgress(var2) : this.getGrimDeadlineProgress(var2), 0.0, 1.0);
         Color var8 = (Color)SpeedMinePlusPlus.this.fc.get();
         Color var9 = (Color)SpeedMinePlusPlus.this.Gg.get();
         ShapeMode var10 = (ShapeMode)SpeedMinePlusPlus.this.dF.get();
         if (var4) {
            Color var11 = SpeedMinePlusPlus.Ku((Color)SpeedMinePlusPlus.this.ex.get());
            var8 = SpeedMinePlusPlus.Kv(var11, 0.0);
            var9 = var11;
            var10 = ShapeMode.Lines;
         }

         if ((Boolean)SpeedMinePlusPlus.this.bb.get() && var4) {
            var8 = Color.ORANGE.a(40);
            var9 = Color.ORANGE;
         }

         Box var16 = !var4 ? new Box(var5) : new Box(var5).expand(0.006);
         var1.renderer.box(var16, var8, var9, var10, 0);
         double var12 = var4 ? 1.0 : var6;
         if (var12 > 0.0) {
            Color var14 = (Color)SpeedMinePlusPlus.this.tV.get();
            Box var15 = new Box(var5.getX(), var5.getY(), var5.getZ(), var5.getX() + 1.0, var5.getY() + MathHelper.clamp(var12, 0.0, 1.0), var5.getZ() + 1.0)
               .expand(-0.002);
            var1.renderer.box(var15, var14, SpeedMinePlusPlus.Ku(var14), (ShapeMode)SpeedMinePlusPlus.this.dF.get(), 0);
         }
      }
   }
}
