package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import com.nnpg.alice.bridge.VelocityBridge;
import com.nnpg.alice.bridge.VelocityPlusPlusBridge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.mixin.EntityVelocityUpdateS2CPacketAccessor;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.EnumSetting.Builder;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class VelocityPlusPlus extends AliceModule {
   private static final long xz = 1000L;
   private static final double dJ = 0.0625;
   private final SettingGroup la = this.settings.getDefaultGroup();
   private final SettingGroup dB = this.settings.createGroup("Jump");
   private final SettingGroup gT = this.settings.createGroup("Pause");
   private final SettingGroup CX = this.settings.createGroup("Walls");
   private final Setting<VelocityPlusPlus.Mode> zg = this.la
      .add(((Builder)((Builder)((Builder)new Builder().name("mode")).description("che do velocity")).defaultValue(VelocityPlusPlus.Mode.Cancel)).build());
   private final Setting<Double> FD = this.la
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                     .name("horizontal"))
                  .description("Horizontal velocity percentage."))
               .defaultValue(0.0)
               .range(0.0, 100.0)
               .sliderRange(0.0, 100.0)
               .visible(
                  () -> this.zg.get() == VelocityPlusPlus.Mode.Custom
                     || this.zg.get() == VelocityPlusPlus.Mode.Jump
                     || this.zg.get() == VelocityPlusPlus.Mode.Plain
               ))
            .build()
      );
   private final Setting<Double> om = this.la
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                     .name("vertical"))
                  .description("Vertical velocity percentage."))
               .defaultValue(40.0)
               .range(0.0, 100.0)
               .sliderRange(0.0, 100.0)
               .visible(() -> this.zg.get() == VelocityPlusPlus.Mode.Custom || this.zg.get() == VelocityPlusPlus.Mode.Plain))
            .build()
      );
   private final Setting<Boolean> sS = this.la
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("explosions"))
                  .description("Cancels explosion knockback."))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> dZ = this.la
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("push"))
                  .description("huy luc day thuc the ra khoi block"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> qR = this.la
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("liquids"))
                  .description("Cancels flowing-liquid velocity."))
               .defaultValue(false))
            .build()
      );
   private final Setting<VelocityPlusPlus.JumpMode> GZ = this.dB
      .add(
         ((Builder)((Builder)((Builder)((Builder)new Builder().name("jump-mode")).description("cach nhay khi bi danh"))
                  .defaultValue(VelocityPlusPlus.JumpMode.Jump))
               .visible(() -> this.zg.get() == VelocityPlusPlus.Mode.Jump))
            .build()
      );
   private final Setting<Double> mb = this.dB
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                     .name("motion"))
                  .description("luc doc cho che do Motion va Both"))
               .defaultValue(0.42)
               .range(0.1, 1.0)
               .sliderRange(0.1, 1.0)
               .visible(() -> {
                  int var10000 = this.zg.get() != VelocityPlusPlus.Mode.Jump ? '鐯' : '鐮';

                  while (true) {
                     switch (var10000) {
                        case 37934:
                           return true;
                        case 37935:
                           var10000 = 37933;
                           break;
                        default:
                           return false;
                     }
                  }
               }))
            .build()
      );
   private final Setting<Boolean> me = this.dB
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("smart-fail"))
                     .description("Randomly fails some jumps."))
                  .defaultValue(true))
               .visible(() -> this.zg.get() == VelocityPlusPlus.Mode.Jump))
            .build()
      );
   private final Setting<Double> rM = this.dB
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                     .name("fail-rate"))
                  .description("ty le nhay that bai"))
               .defaultValue(0.3)
               .range(0.0, 1.0)
               .sliderRange(0.0, 1.0)
               .visible(() -> this.zg.get() == VelocityPlusPlus.Mode.Jump && (Boolean)this.me.get()))
            .build()
      );
   private final Setting<Double> Cy = this.dB
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                     .name("fail-jump-rate"))
                  .description("ty le nhay that sau khi that bai"))
               .defaultValue(0.25)
               .range(0.0, 1.0)
               .sliderRange(0.0, 1.0)
               .visible(() -> this.zg.get() == VelocityPlusPlus.Mode.Jump && (Boolean)this.me.get()))
            .build()
      );
   private final Setting<Boolean> CS = this.gT
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("pause-on-flag"))
                  .description("tam dung velocity khi Grim flag hoac setback"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Integer> Ba = this.gT
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("pause-ticks"))
                     .description("so tick tam dung sau khi bi flag/lagback"))
                  .defaultValue(2))
               .range(1, 20)
               .sliderRange(1, 10)
               .visible(this.CS::get))
            .build()
      );
   private final Setting<Boolean> aL = this.CX
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("inverse"))
                     .description("Plain: flips horizontal knockback direction."))
                  .defaultValue(false))
               .visible(() -> this.zg.get() == VelocityPlusPlus.Mode.Plain))
            .build()
      );
   private final Setting<Boolean> hw = this.CX
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("mio-webs"))
                     .description("Walls: kich hoat ca khi bi ket trong to nhen"))
                  .defaultValue(false))
               .visible(() -> {
                  boolean var10000;
                  if (this.zg.get() == VelocityPlusPlus.Mode.Walls) {
                     var10000 = true;
                  } else {
                     short var1 = 1051;
                     var10000 = false;
                  }

                  return var10000;
               }))
            .build()
      );
   private final Setting<Integer> vw = this.CX
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("walls-delay"))
                     .description("Walls: so ms nghi sau mot loat setback"))
                  .defaultValue(500))
               .range(0, 2000)
               .sliderRange(0, 1000)
               .visible(() -> this.zg.get() == VelocityPlusPlus.Mode.Walls))
            .build()
      );
   private final Setting<Integer> EI = this.CX
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("walls-setbacks"))
                     .description("Walls: so setback moi giay chiu duoc truoc khi nghi"))
                  .defaultValue(5))
               .range(0, 20)
               .sliderRange(0, 10)
               .visible(() -> this.zg.get() == VelocityPlusPlus.Mode.Walls))
            .build()
      );
   private boolean DM;
   private boolean cF;
   private boolean wu;
   private boolean l;
   private int jV;
   private int pF;
   private boolean gE;
   private final List<Long> ud = Collections.synchronizedList(new ArrayList<>());
   private long dx;
   private boolean tu;
   private boolean qD;

   public VelocityPlusPlus() {
      super(AliceAddon.PVP_CATEGORY, "velocity++", "Dieu chinh luc day tac dong len nguoi choi theo cac thiet lap da chon.");
   }

   @Override
   public void onActivate() {
      VelocityBridge.install(this, this.dZ::get, this.qR::get, () -> true);
      VelocityPlusPlusBridge.install(this, this.dZ::get, this.qR::get, this::Le, this::Lf, () -> true, this::Le, this::Lf);
      this.gE = false;
      this.pF = 0;
      this.l = false;
      this.jV = 0;
      this.DM = false;
      this.cF = false;
      this.wu = false;
      this.Ld();
      super.onActivate();
   }

   @Override
   public void onDeactivate() {
      VelocityBridge.uninstall(this);
      VelocityPlusPlusBridge.uninstall(this);
      this.gE = false;
      this.pF = 0;
      this.l = false;
      this.Ld();
      super.onDeactivate();
   }

   private void Ld() {
      this.ud.clear();
      this.dx = 0L;
      this.tu = false;
      this.qD = false;
   }

   private boolean Le() {
      if (!this.gE && (Boolean)this.sS.get()) {
         return switch ((VelocityPlusPlus.Mode)this.zg.get()) {
            case Cancel, Grim -> true;
            default -> false;
            case Walls -> this.qD;
         };
      } else {
         return false;
      }
   }

   private boolean Lf() {
      if (this.gE) {
         return false;
      } else {
         int var1 = ((VelocityPlusPlus.Mode)this.zg.get()).ordinal();
         boolean var10000;
         if (var1 == 0) {
            var10000 = (Double)this.FD.get() != 100.0 || (Double)this.om.get() != 100.0;
         } else {
            if (var1 != 1) {
               if (var1 == 2) {
                  return (Double)this.FD.get() != 100.0;
               }

               if (var1 != 3 && var1 == 4) {
                  var10000 = !this.sS.get() ? 25140 : 25139;

                  while (true) {
                     switch (var10000) {
                        case 25139:
                           if ((Double)this.FD.get() != 100.0 || (Double)this.om.get() != 100.0 || (Boolean)this.aL.get()) {
                              return true;
                           }
                        default:
                           return false;
                        case 25140:
                           var10000 = 25138;
                     }
                  }
               }
            }

            var10000 = 0;
         }

         return (boolean)var10000;
      }
   }

   @AliceEventHandler
   private void Lg(Receive var1) {
      if (this.mc.player != null) {
         if (var1.packet instanceof PlayerPositionLookS2CPacket) {
            this.ud.add(System.currentTimeMillis());
            if (this.zg.get() == VelocityPlusPlus.Mode.Grim) {
               this.jV = 5;
            }

            if ((Boolean)this.CS.get() && this.pF <= 0) {
               this.pF = Math.max(this.pF, Math.min((Integer)this.Ba.get(), 2));
               this.gE = true;
            }
         }

         int var10000 = this.gE ? 27404 : 27405;

         while (true) {
            int var8 = var10000;
            if (var8 == 27404) {
               if (var1.packet instanceof EntityVelocityUpdateS2CPacket var2 && var2.getEntityId() == this.mc.player.getId()) {
                  return;
               }
               break;
            }

            if (var8 != 27405) {
               break;
            }

            var10000 = 27403;
         }

         if (var1.packet instanceof EntityVelocityUpdateS2CPacket var9) {
            if (var9.getEntityId() != this.mc.player.getId()) {
               return;
            }

            switch (((VelocityPlusPlus.Mode)this.zg.get()).ordinal() ^ -2068412752) {
               case -2068412752:
                  double var14 = (Double)this.FD.get();
                  double var16 = (Double)this.om.get();
                  if (var14 == 0.0 && var16 == 0.0) {
                     var1.setCancelled(true);
                     return;
                  }

                  Vec3d var7 = Lo(var9.getVelocity(), var14, var16);
                  if (var7 == Vec3d.ZERO) {
                     var1.setCancelled(true);
                     return;
                  }

                  this.mc.player.setVelocity(var7);
                  var1.setCancelled(true);
                  break;
               case -2068412751:
                  var1.setCancelled(true);
                  break;
               case -2068412750:
                  double var13 = (Double)this.FD.get();
                  if (var13 == 0.0) {
                     var1.setCancelled(true);
                     return;
                  }

                  Vec3d var15 = Lo(var9.getVelocity(), var13, 100.0);
                  if (var15 == Vec3d.ZERO) {
                     var1.setCancelled(true);
                     return;
                  }

                  this.mc.player.setVelocity(var15);
                  var1.setCancelled(true);
                  break;
               case -2068412749:
                  if (!this.gE) {
                     var1.setCancelled(true);
                     this.l = true;
                     if ((Boolean)this.CS.get()) {
                        this.gE = true;
                        this.pF = Math.min((Integer)this.Ba.get(), 2);
                     }
                  }
                  break;
               case -2068412748:
                  double var12 = (Double)this.FD.get();
                  double var5 = (Double)this.om.get();
                  if (var12 == 0.0 && var5 == 0.0) {
                     var1.setCancelled(true);
                     return;
                  }

                  ((EntityVelocityUpdateS2CPacketAccessor)var9).meteor$setVelocity(Ln(var9.getVelocity(), var12, var5, (Boolean)this.aL.get()));
                  break;
               case -2068412747:
                  var10000 = this.qD ? 16688 : 16687;

                  while (true) {
                     switch (var10000) {
                        case 16687:
                           return;
                        case 16688:
                           var10000 = 16686;
                           break;
                        default:
                           Vec3d var11 = var9.getVelocity();
                           double var4 = var11.y > 0.0 ? -var11.y : var11.y;
                           ((EntityVelocityUpdateS2CPacketAccessor)var9).meteor$setVelocity(new Vec3d(0.0, var4, 0.0));
                           return;
                     }
                  }
            }
         }
      }
   }

   @AliceEventHandler
   private void Lh(Pre var1) {
      if (this.mc.player != null) {
         if (this.pF > 0) {
            this.pF--;
            if (this.pF <= 0) {
               this.gE = false;
               this.l = false;
            }
         } else if (this.gE && !this.l && this.jV <= 0) {
            this.gE = false;
         }

         label90:
         switch ((VelocityPlusPlus.Mode)this.zg.get()) {
            case Jump:
               boolean var10000;
               label115: {
                  label129: {
                     short var11;
                     if (!this.cF) {
                        if (this.mc.player.hurtTime <= 6) {
                           break label129;
                        }
                     } else {
                        while (var11 == 16998) {
                           var11 = 16996;
                        }
                     }

                     if (this.mc.player.isOnGround()) {
                        var10000 = 1;
                        break label115;
                     }
                  }

                  var10000 = 0;
               }

               boolean var2 = (boolean)var10000;
               if (!var2) {
                  if (this.mc.player.hurtTime == 0) {
                     this.DM = false;
                     this.wu = false;
                  }
                  break;
               } else {
                  boolean var3 = this.cF;
                  this.cF = false;
                  boolean var4;
                  if (!(Boolean)this.me.get() || !(Math.random() < (Double)this.rM.get())) {
                     var4 = true;
                  } else if (!(Math.random() < (Double)this.Cy.get())) {
                     var4 = false;
                  } else {
                     var4 = true;
                     this.cF = true;
                  }

                  if (!var3 && !this.DM) {
                     this.wu = true;
                  }

                  this.DM = var4;
                  if (this.wu) {
                     this.wu = false;
                     return;
                  }

                  var10000 = this.DM ? 17398 : 17397;

                  while (true) {
                     switch (var10000) {
                        case 17397:
                           return;
                        case 17398:
                           var10000 = 17396;
                           break;
                        default:
                           switch ((VelocityPlusPlus.JumpMode)this.GZ.get()) {
                              case Motion:
                                 this.mc.player.setVelocity(this.mc.player.getVelocity().getX(), (Double)this.mb.get(), this.mc.player.getVelocity().getZ());
                                 break;
                              case Jump:
                                 this.mc.player.jump();
                                 break;
                              case Both:
                                 this.mc.player.jump();
                                 this.mc.player.setVelocity(this.mc.player.getVelocity().getX(), (Double)this.mb.get(), this.mc.player.getVelocity().getZ());
                           }

                           this.DM = false;
                           break label90;
                     }
                  }
               }
            case Grim:
               if (this.l && this.jV <= 0) {
                  this.mc
                     .getNetworkHandler()
                     .sendPacket(
                        new Full(
                           this.mc.player.getX(),
                           this.mc.player.getY(),
                           this.mc.player.getZ(),
                           this.mc.player.getYaw(),
                           this.mc.player.getPitch(),
                           this.mc.player.isOnGround(),
                           false
                        )
                     );
                  this.mc
                     .getNetworkHandler()
                     .sendPacket(
                        new PlayerActionC2SPacket(
                           Action.STOP_DESTROY_BLOCK, BlockPos.ofFloored(this.mc.player.getX(), this.mc.player.getY(), this.mc.player.getZ()), Direction.DOWN
                        )
                     );
                  this.l = false;
               } else if (this.l) {
                  this.l = false;
               }
         }

         if (this.jV > 0) {
            this.jV--;
         }
      }
   }

   @AliceEventHandler
   private void Li(Post var1) {
      VelocityPlusPlus.Mode var2 = (VelocityPlusPlus.Mode)this.zg.get();
      if (this.mc.player != null && this.mc.world != null && var2 == VelocityPlusPlus.Mode.Walls) {
         long var3 = System.currentTimeMillis();
         this.Lj(var3);
         this.tu = this.Ll();
         if (this.ud.size() > (Integer)this.EI.get()) {
            this.dx = var3;
         }

         this.qD = (Boolean)this.hw.get() && this.Lm() || this.tu && Lk(this.dx, var3, ((Integer)this.vw.get()).intValue());
      } else {
         this.qD = false;
      }
   }

   // $VF: Could not create synchronized statement, marking monitor enters and exits
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void Lj(long var1) {
      List var3 = this.ud;
      synchronized (this.ud){} // $VF: monitorenter 
      this.ud.removeIf(var2 -> var1 - var2 > 1000L);
      // $VF: monitorexit
   }

   private static boolean Lk(long var0, long var2, long var4) {
      return var2 - var0 >= var4;
   }

   private boolean Ll() {
      Box var1 = this.mc.player.getBoundingBox().shrink(0.0625, 0.0, 0.0625).withMaxY(this.mc.player.getY()).offset(0.0, 0.1, 0.0);
      return BlockPos.stream(var1).anyMatch(var1x -> !this.mc.world.getBlockState(var1x).getCollisionShape(this.mc.world, var1x).isEmpty());
   }

   private boolean Lm() {
      return this.mc.world.getStatesInBox(this.mc.player.getBoundingBox()).anyMatch(var0 -> var0.isOf(Blocks.COBWEB));
   }

   public static Vec3d Ln(Vec3d var0, double var1, double var3, boolean var5) {
      double var6 = var1 / 100.0 * (!var5 ? 1.0 : -1.0);
      double var8 = var3 / 100.0;
      return new Vec3d(var0.x * var6, var0.y * var8, var0.z * var6);
   }

   public static Vec3d Lo(Vec3d var0, double var1, double var3) {
      if (var1 == 0.0 && var3 == 0.0) {
         return Vec3d.ZERO;
      } else {
         double var5 = var1 / 100.0;
         double var7 = var3 / 100.0;
         return new Vec3d(var0.x * var5, var0.y * var7, var0.z * var5);
      }
   }

   public String getInfoString() {
      return switch (((VelocityPlusPlus.Mode)this.zg.get()).ordinal() ^ -1952324380) {
         case -1952324384 -> "" + ((Double)this.FD.get()).intValue() + ((Double)this.om.get()).intValue();
         case -1952324383 -> ((VelocityPlusPlus.Mode)this.zg.get()).name();
         default -> null;
      };
   }

   public static enum JumpMode {
      Motion,
      Jump,
      Both;
   }

   public static enum Mode {
      Custom,
      Cancel,
      Jump,
      Grim,
      Plain,
      Walls;
   }
}
