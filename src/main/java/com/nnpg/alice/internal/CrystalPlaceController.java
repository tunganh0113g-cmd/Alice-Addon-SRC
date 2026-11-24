package com.nnpg.alice.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Sent;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

final class CrystalPlaceController {
   private static final Direction[] pe = new Direction[]{Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.NORTH};
   private static final int bC = 20;
   private static final int xL = 8;
   private static final int cJ = 3;
   private static final int eP = 8;
   private static final double bv = 0.0625;
   private static final double hT = 0.45;
   private final CrystalPlaceController.Host lN;
   private final MinecraftClient lE = MinecraftClient.getInstance();
   private final Map<BlockPos, CrystalPlaceController.PendingPlacement> jn = new HashMap<>();
   private final ConcurrentLinkedQueue<CrystalPlaceController.ServerBlockObservation> fP = new ConcurrentLinkedQueue<>();
   private final ConcurrentLinkedQueue<Integer> wq = new ConcurrentLinkedQueue<>();
   private int Df;
   private int qc;
   private boolean Ea;
   private int wU;
   private float kV;
   private float Gz;
   private CrystalPlaceController.RotationIntent By;
   private CrystalPlaceController.PlaceRotationInput df;
   private CrystalPlaceController.Shot os;
   private BlockPos ws;
   private int Av;
   private CrystalPlaceController.Shot nZ;
   private Vec3d zD;
   private Box ox;
   private boolean vl;
   private boolean hP;
   private boolean nc;
   private int md = -1;
   private int Jl;
   private long qh;

   CrystalPlaceController(CrystalPlaceController.Host var1) {
      this.lN = var1;
   }

   void zC() {
      this.jn.clear();
      this.fP.clear();
      this.wq.clear();
      this.os = null;
      this.zD = null;
      this.ox = null;
      this.vl = false;
      this.ws = null;
      this.Av = 0;
      this.qc = 0;
      this.Jl = 0;
      this.qh = 0L;
      this.An();
   }

   int zD() {
      return this.Df;
   }

   BlockPos zE() {
      return this.ws;
   }

   int zF() {
      return this.Av;
   }

   boolean zG() {
      if (this.lE.player != null && this.lE.world != null) {
         this.Df++;
         this.qc = 0;
         this.nZ = null;
         this.zD = this.lE.player.getEyePos();
         this.ox = this.lE.player.getBoundingBox();
         this.Ao();
         this.Ap();
         if (!this.Ea && this.By == null) {
            return true;
         } else {
            GrimCombatCoordinator.wX();
            if (this.Df - this.wU <= 3) {
               return false;
            } else {
               this.An();
               return false;
            }
         }
      } else {
         this.zC();
         return false;
      }
   }

   void zH() {
      if (this.Ea && this.By != null && this.df == null) {
         ClientPlayerEntity var1 = this.lE.player;
         if (var1 != null && var1.input != null && !var1.hasVehicle()) {
            CrystalPlaceController.RotationIntent var2 = this.By;
            this.By = null;
            this.df = new CrystalPlaceController.PlaceRotationInput(var1.input, var1, var2, this.lN.keepCamera());
            var1.input = this.df;
         } else {
            this.An();
         }
      }
   }

   void zI() {
      CrystalPlaceController.Shot var1 = this.nZ;
      this.nZ = null;
      if (var1 != null && !this.lN.safeTiming()) {
         if (this.lE.player != null && this.lE.world != null) {
            if (this.os == var1 && this.Ea) {
               if (this.df != null && this.df.zm()) {
                  if (this.AB(var1.target())) {
                     FindItemResult var2 = this.Ak();
                     if (var2 != null) {
                        boolean var3 = GrimCombatCoordinator.wS();
                        float var4 = GrimCombatCoordinator.wQ();
                        float var5 = GrimCombatCoordinator.wR();
                        List var6 = this.lN.placementPlan();
                        this.vl = true;

                        try {
                           GrimCombatCoordinator.xo(this.lN.participant(), () -> {
                              if (this.qc == 0 && !GrimCombatCoordinator.wO(this.lN.participant())) {
                                 if (GrimCombatCoordinator.wM(this.lN.participant())) {
                                    GrimCombatCoordinator.wJ(this.lN.participant());
                                 }
                              } else {
                                 if (this.zU(var1, var2)) {
                                    this.os = null;
                                    this.zS(var1, var6, var2);
                                    if (var3 && this.qc < this.zV()) {
                                       this.zT(var4, var1.pitch(), var6, var2);
                                    }

                                    if (var3 && this.qc < this.zV()) {
                                       this.zT(var4, var5, var6, var2);
                                    }
                                 }

                                 if (GrimCombatCoordinator.wM(this.lN.participant())) {
                                    GrimCombatCoordinator.wJ(this.lN.participant());
                                 }
                              }
                           });
                        } finally {
                           this.vl = false;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   void zJ() {
      CrystalPlaceController.PlaceRotationInput var1 = this.df;
      if (var1 != null) {
         var1.zn(false);
         DelegatingInput.qM(this.lE.player, var1);
         this.df = null;
         if (this.Ea && this.Al(var1.mx.yaw(), var1.mx.pitch())) {
            this.Ea = false;
         }
      }
   }

   void zK(Sent var1) {
      if (var1.packet instanceof PlayerInteractBlockC2SPacket var5) {
         if (this.hP) {
            this.nc = true;
            this.md = var5.getSequence();
         }
      } else if (var1.packet instanceof PlayerMoveC2SPacket var2 && var2.changesLook()) {
         if (this.Ea) {
            float var7 = var2.getYaw(this.kV);
            float var4 = var2.getPitch(this.Gz);
            if (!GrimCombatCoordinator.yt(var7, var4, this.kV, this.Gz)) {
               this.By = null;
               this.os = null;
            } else if (this.df != null && this.df.zm()) {
               GrimCombatCoordinator.wN(this.lN.participant(), var7, var4);
            }

            this.Ea = false;
         }
      }
   }

   void zL(BlockPos var1, BlockState var2) {
      if (var1 != null && var2 != null) {
         this.fP.offer(new CrystalPlaceController.ServerBlockObservation(var1.toImmutable(), var2));
      }
   }

   void zM(int var1) {
      this.wq.offer(var1);
   }

   void zN() {
      this.An();
   }

   GrimCombatCoordinator.Intent zO() {
      ClientPlayerEntity var1 = this.lE.player;
      if (var1 != null && this.lE.world != null) {
         int var10000 = !var1.isAlive() ? 16163 : 16162;

         while (true) {
            int var9 = var10000;
            if (var9 == 16162) {
               if (!var1.hasVehicle()) {
                  if (!this.Ea && this.By == null && this.df == null) {
                     if (this.Df < this.Jl) {
                        return null;
                     }

                     final List var2 = this.lN.placementPlan();
                     if (var2.isEmpty()) {
                        this.os = null;
                        return null;
                     }

                     final FindItemResult var3 = this.Ak();
                     if (var3 == null) {
                        this.os = null;
                        return null;
                     }

                     final CrystalPlaceController.Shot var4;
                     label78: {
                        var4 = this.os;
                        if (var4 != null && var2.contains(var4.target()) && this.Al(var4.yaw(), var4.pitch())) {
                           var10000 = !this.Aa(var4.support()) ? '鑠' : '鑟';

                           label72:
                           while (true) {
                              switch (var10000) {
                                 case 37983:
                                    if (this.Af(var4.yaw(), var4.pitch(), var4.support())
                                       && this.Ad(var4.support())
                                       && this.Ac(var4.support(), var4.face())
                                       && this.Aj(var4.target())
                                       && this.Ai(var4.target(), var4.support(), var4.face(), var4.cursor(), var3)) {
                                       var11 = true;
                                       break label78;
                                    }
                                    break label72;
                                 case 37984:
                                    var10000 = 37982;
                                    break;
                                 default:
                                    break label72;
                              }
                           }
                        }

                        var11 = false;
                     }

                     boolean var5 = var11;
                     if (!var5) {
                        var4 = null;
                        this.os = null;
                     }

                     final CrystalPlaceController.Shot var6 = this.zQ(var2, var4, var3);
                     if (var4 == null && var6 == null) {
                        return null;
                     }

                     return new GrimCombatCoordinator.Intent() {
                        @Override
                        public GrimCombatCoordinator.Participant owner() {
                           return CrystalPlaceController.this.lN.participant();
                        }

                        @Override
                        public void execute() {
                           CrystalPlaceController.this.zP(var4, var6, var2, var3);
                        }
                     };
                  }

                  return null;
               }
               break;
            }

            if (var9 != 16163) {
               break;
            }

            var10000 = 16161;
         }

         return null;
      } else {
         return null;
      }
   }

   private void zP(CrystalPlaceController.Shot var1, CrystalPlaceController.Shot var2, List<BlockPos> var3, FindItemResult var4) {
      if (GrimCombatCoordinator.wO(this.lN.participant())) {
         if (this.lE.player != null && this.lE.world != null) {
            try {
               CrystalPlaceController.Shot var5 = var2;
               if (var1 != null && this.zU(var1, var4)) {
                  this.os = null;
                  this.zS(var1, var3, var4);
                  var5 = this.zQ(var3, null, var4);
               }

               if (var5 != null) {
                  this.Am(var5);
                  if (!this.lN.safeTiming() && this.Ea) {
                     this.nZ = var5;
                  }
               }
            } finally {
               if (GrimCombatCoordinator.wM(this.lN.participant())) {
                  GrimCombatCoordinator.wJ(this.lN.participant());
               }
            }

            short var12;
            while (var12 == 14246) {
               var12 = 14244;
            }
         }
      }
   }

   private CrystalPlaceController.Shot zQ(List<BlockPos> var1, CrystalPlaceController.Shot var2, FindItemResult var3) {
      if (this.zV() <= 1) {
         for (BlockPos var12 : var1) {
            if (var2 == null || !var12.equals(var2.target())) {
               CrystalPlaceController.Shot var13 = this.zW(var12, var3);
               if (var13 != null) {
                  return var13;
               }
            }
         }

         return null;
      } else {
         CrystalPlaceController.Shot var4 = null;
         int var5 = -1;

         label55:
         for (BlockPos var7 : var1) {
            int var10000 = var2 != null ? 8525 : 8526;

            while (true) {
               int var10 = var10000;
               if (var10 == 8525) {
                  if (var7.equals(var2.target())) {
                     continue label55;
                  }
                  break;
               }

               if (var10 != 8526) {
                  break;
               }

               var10000 = 8524;
            }

            CrystalPlaceController.Shot var8 = this.zW(var7, var3);
            if (var8 != null) {
               int var9 = this.zR(var8, var1, var3);
               if (var9 > var5) {
                  var5 = var9;
                  var4 = var8;
               }
            }
         }

         return var4;
      }
   }

   private int zR(CrystalPlaceController.Shot var1, List<BlockPos> var2, FindItemResult var3) {
      int var4 = 0;

      for (BlockPos var6 : var2) {
         if (!var6.equals(var1.target()) && this.zX(var1, var6, var3) != null) {
            var4++;
         }
      }

      return var4;
   }

   private void zS(CrystalPlaceController.Shot var1, List<BlockPos> var2, FindItemResult var3) {
      this.zT(var1.yaw(), var1.pitch(), var2, var3);
   }

   private void zT(float var1, float var2, List<BlockPos> var3, FindItemResult var4) {
      if (this.qc < this.zV()) {
         ArrayList var5 = new ArrayList(var3);

         boolean var6;
         do {
            var6 = false;
            int var7 = 0;

            while (var7 < var5.size()) {
               if (this.qc >= this.zV()) {
                  return;
               }

               BlockPos var8 = (BlockPos)var5.get(var7);
               if (var3.contains(var8) && this.Aj(var8)) {
                  CrystalPlaceController.Shot var9 = this.zY(var1, var2, var8, var4);
                  if (var9 != null) {
                     if (!this.zU(var9, var4)) {
                        return;
                     }

                     var5.remove(var7);
                     var6 = true;
                     break;
                  }

                  var7++;
               } else {
                  var5.remove(var7);
               }
            }
         } while (var6 && !var5.isEmpty());
      }
   }

   private boolean zU(CrystalPlaceController.Shot var1, FindItemResult var2) {
      if (this.qc >= this.zV()) {
         return false;
      } else if (this.vl && !this.AB(var1.target())) {
         return false;
      } else {
         long var3 = this.lN.placeDelayMs();
         long var5 = System.currentTimeMillis();
         if (this.qc == 0) {
            int var10000 = var3 <= 0L ? 6725 : 6724;

            label101:
            while (true) {
               switch (var10000) {
                  case 6724:
                     if (this.qh != 0L && var5 - this.qh < var3) {
                        return false;
                     }
                     break label101;
                  case 6725:
                     var10000 = 6723;
                     break;
                  default:
                     break label101;
               }
            }
         }

         if (this.qc > 0 && !GrimCombatCoordinator.xn(this.lN.participant())) {
            return false;
         } else if (GrimCombatCoordinator.wI(this.lN.participant(), var2.slot())) {
            int var7 = GrimCombatCoordinator.xG();
            this.hP = true;
            this.nc = false;
            this.md = -1;

            try {
               this.lE.interactionManager.interactBlock(this.lE.player, Hand.MAIN_HAND, new BlockHitResult(var1.cursor(), var1.face(), var1.support(), false));
            } finally {
               this.hP = false;
            }

            if (this.nc && GrimCombatCoordinator.xG() != var7) {
               this.lE.player.swingHand(Hand.MAIN_HAND);
               this.qc++;
               this.qh = var5;
               this.Jl = this.Df + Math.max(0, this.lN.placeDelayTicks()) + 1;
               int var8 = !this.lN.retryIfNotAck() ? 20 : 8;
               this.jn.put(var1.target(), new CrystalPlaceController.PendingPlacement(this.Df + var8, this.md));
               this.ws = var1.target();
               this.Av = this.Df;
               this.lN.onPlaced(var1.target());
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private int zV() {
      return Math.max(1, Math.min(8, this.lN.blocksPerTick()));
   }

   private CrystalPlaceController.Shot zW(BlockPos var1, FindItemResult var2) {
      if (!this.Aj(var1)) {
         return null;
      } else {
         label34:
         for (CrystalPlaceController.Support var4 : this.zZ(var1)) {
            Vec2f var5 = this.Ah(var4.aim());
            if (this.Af(var5.x, var5.y, var4.position())) {
               int var10000 = this.Ai(var1, var4.position(), var4.face(), var4.cursor(), var2) ? '鑎' : '鑍';

               while (true) {
                  switch (var10000) {
                     case 37965:
                        continue label34;
                     case 37966:
                        var10000 = 37964;
                        break;
                     default:
                        return new CrystalPlaceController.Shot(var1, var4.position(), var4.face(), var4.cursor(), var5.x, var5.y);
                  }
               }
            }
         }

         return null;
      }
   }

   private CrystalPlaceController.Shot zX(CrystalPlaceController.Shot var1, BlockPos var2, FindItemResult var3) {
      return this.zY(var1.yaw(), var1.pitch(), var2, var3);
   }

   private CrystalPlaceController.Shot zY(float var1, float var2, BlockPos var3, FindItemResult var4) {
      if (!this.Aj(var3)) {
         return null;
      } else if (this.vl && !this.AB(var3)) {
         return null;
      } else {
         label41:
         for (CrystalPlaceController.Support var6 : this.zZ(var3)) {
            if (this.Af(var1, var2, var6.position())) {
               int var10000 = this.Ai(var3, var6.position(), var6.face(), var6.cursor(), var4) ? '쏍' : '쏌';

               while (true) {
                  switch (var10000) {
                     case 50124:
                        continue label41;
                     case 50125:
                        var10000 = 50123;
                        break;
                     default:
                        return new CrystalPlaceController.Shot(var3, var6.position(), var6.face(), var6.cursor(), var1, var2);
                  }
               }
            }
         }

         char var7 = '\uf811';
         return null;
      }
   }

   private List<CrystalPlaceController.Support> zZ(BlockPos var1) {
      ArrayList var2 = new ArrayList(6);

      for (Direction var6 : pe) {
         BlockPos var7 = var1.offset(var6);
         Direction var8 = var6.getOpposite();
         if (this.Aa(var7) && this.Ac(var7, var8) && this.Ad(var7)) {
            Vec3d var9 = var7.toCenterPos();
            Vec3d var10 = Vec3d.of(var8.getVector());
            var2.add(new CrystalPlaceController.Support(var7, var8, var9.add(var10.multiply(0.5)), var9.add(var10.multiply(0.45))));
         }
      }

      return var2;
   }

   private boolean Aa(BlockPos var1) {
      BlockState var2 = this.lE.world.getBlockState(var1);
      return !var2.isAir() && !var2.isReplaceable() ? !BlockUtils.isClickable(var2.getBlock()) : false;
   }

   private Vec3d Ab() {
      Vec3d var1 = this.zD;
      return var1 != null ? var1 : this.lE.player.getEyePos();
   }

   private boolean Ac(BlockPos var1, Direction var2) {
      Vec3d var3 = this.Ab();
      double var4 = var3.x;
      double var6 = var3.y;
      double var8 = var3.z;
      int var10 = <unrepresentable>.xY[var2.ordinal()];
      boolean var10000;
      if (var10 == 1) {
         var10000 = var8 <= var1.getZ();
      } else if (var10 == 2) {
         var10000 = var8 >= var1.getZ() + 1.0;
      } else if (var10 == 3) {
         var10000 = var4 <= var1.getX();
      } else if (var10 != 4) {
         if (var10 == 5) {
            var10000 = var6 >= var1.getY() + 1.0;
         } else {
            if (var10 != 6) {
               throw new MatchException(null, null);
            }

            var10000 = var6 <= var1.getY();
         }
      } else {
         var10000 = var4 >= var1.getX() + 1.0;
      }

      return var10000;
   }

   private boolean Ad(BlockPos var1) {
      Vec3d var2 = this.Ab();
      Box var3 = new Box(var1);
      double var4 = Ae(var2.x, var3.minX, var3.maxX);
      double var6 = Ae(var2.y, var3.minY, var3.maxY);
      double var8 = Ae(var2.z, var3.minZ, var3.maxZ);
      double var10 = this.lE.player.getBlockInteractionRange() - 0.1;
      return var4 * var4 + var6 * var6 + var8 * var8 <= var10 * var10;
   }

   private static double Ae(double var0, double var2, double var4) {
      if (var0 < var2) {
         return var2 - var0;
      } else {
         return var0 > var4 ? var0 - var4 : 0.0;
      }
   }

   private boolean Af(float var1, float var2, BlockPos var3) {
      Vec3d var4 = this.Ab();
      Box var5 = new Box(var3);
      if (var5.expand(0.0625).contains(var4)) {
         return true;
      } else {
         Vec3d var6 = Ag(var1, var2);
         Vec3d var7 = var4.add(var6.multiply(this.lE.player.getBlockInteractionRange()));
         return var5.raycast(var4, var7).isPresent();
      }
   }

   private static Vec3d Ag(float var0, float var1) {
      float var2 = var0 * (float) (Math.PI / 180.0);
      float var3 = var1 * (float) (Math.PI / 180.0);
      float var4 = MathHelper.cos(var3);
      return new Vec3d(-MathHelper.sin(var2) * var4, -MathHelper.sin(var3), MathHelper.cos(var2) * var4);
   }

   private Vec2f Ah(Vec3d var1) {
      Vec3d var2 = this.Ab();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      float var11 = (float)Math.toDegrees(Math.atan2(var7, var3)) - 90.0F;
      float var12 = (float)(-Math.toDegrees(Math.atan2(var5, var9)));
      return new Vec2f(MathHelper.wrapDegrees(var11), MathHelper.clamp(var12, -90.0F, 90.0F));
   }

   private boolean Ai(BlockPos var1, BlockPos var2, Direction var3, Vec3d var4, FindItemResult var5) {
      ItemStack var6 = this.lE.player.getInventory().getStack(var5.slot());
      if (var6.getItem() instanceof BlockItem var7) {
         if (this.lN.strictPlaceCheck()) {
            BlockHitResult var11 = new BlockHitResult(var4, var3, var2, false);
            ItemPlacementContext var9 = new ItemPlacementContext(this.lE.player, Hand.MAIN_HAND, var6, var11);
            if (!var9.canPlace()) {
               return false;
            } else if (!var9.getBlockPos().equals(var1)) {
               return false;
            } else {
               BlockState var10 = var7.getBlock().getPlacementState(var9);
               if (var10 == null) {
                  return false;
               } else {
                  return !var10.canPlaceAt(this.lE.world, var1) ? false : this.lE.world.canPlace(var10, var1, ShapeContext.ofPlacement(this.lE.player));
               }
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private boolean Aj(BlockPos var1) {
      return this.lE.world.getBlockState(var1).isReplaceable();
   }

   private FindItemResult Ak() {
      for (Block var2 : this.lN.allowedBlocks()) {
         FindItemResult var3 = InvUtils.find(var1 -> var1.getItem() == var2.asItem(), 0, 8);
         if (var3.found()) {
            return var3;
         }
      }

      return null;
   }

   private boolean Al(float var1, float var2) {
      return GrimCombatCoordinator.wS() && GrimCombatCoordinator.yt(GrimCombatCoordinator.wQ(), GrimCombatCoordinator.wR(), var1, var2);
   }

   private void Am(CrystalPlaceController.Shot var1) {
      short var5;
      if (!this.Ea) {
         if (this.By == null) {
            this.kV = var1.yaw();
            this.Gz = var1.pitch();
            this.Ea = true;
            this.wU = this.Df;
            this.By = new CrystalPlaceController.RotationIntent(var1.yaw(), var1.pitch());
            this.os = var1;
            return;
         }
      } else {
         while (var5 == 5579) {
            var5 = 5577;
         }
      }
   }

   private void An() {
      this.By = null;
      this.Ea = false;
      this.os = null;
      this.nZ = null;
      CrystalPlaceController.PlaceRotationInput var1 = this.df;
      if (var1 != null) {
         var1.zn(true);
         DelegatingInput.qM(this.lE.player, var1);
         this.df = null;
      }
   }

   private void Ao() {
      CrystalPlaceController.ServerBlockObservation var1;
      label50:
      while ((var1 = this.fP.poll()) != null) {
         CrystalPlaceController.PendingPlacement var2 = this.jn.get(var1.pos());
         if (var2 != null) {
            BlockState var3 = var1.state();
            int var10000 = !var3.isAir() ? 26531 : 26532;

            while (true) {
               int var5 = var10000;
               if (var5 == 26531) {
                  if (!var3.isReplaceable()) {
                     this.jn.remove(var1.pos());
                     continue label50;
                  }
                  break;
               }

               if (var5 != 26532) {
                  break;
               }

               var10000 = 26530;
            }

            if (this.lN.retryIfNotAck()) {
               this.jn.remove(var1.pos());
            }
         }
      }

      int var6 = -1;

      Integer var7;
      while ((var7 = this.wq.poll()) != null) {
         var6 = Math.max(var6, var7);
      }

      if (this.lN.retryIfNotAck() && var6 >= 0) {
         int var4 = var6;
         this.jn.entrySet().removeIf(var1x -> var1x.getValue().sequence() >= 0 && var1x.getValue().sequence() <= var4);
      }
   }

   private void Ap() {
      this.jn.entrySet().removeIf(var1 -> var1.getValue().expiresAtTick() <= this.Df);
   }

   boolean Aq(BlockPos var1) {
      return this.jn.containsKey(var1);
   }

   static BlockPos Ar(Vec3d var0) {
      double var1 = Math.floor(var0.getY());
      double var3 = !(var0.getY() - var1 > 0.8) ? var1 : var1 + 1.0;
      return BlockPos.ofFloored(var0.getX(), var3, var0.getZ());
   }

   static List<BlockPos> As(Vec3d var0) {
      ArrayList var1 = new ArrayList(4);
      BlockPos var2 = Ar(var0);
      var1.add(var2);
      int var3 = Aw(var0.getX() - Math.floor(var0.getX()));
      int var4 = Aw(var0.getZ() - Math.floor(var0.getZ()));

      for (int var5 = 0; var5 <= Math.abs(var3); var5++) {
         for (int var6 = 0; var6 <= Math.abs(var4); var6++) {
            BlockPos var7 = var2.add(var5 * var3, 0, var6 * var4);
            if (!var1.contains(var7)) {
               var1.add(var7);
            }
         }
      }

      return var1;
   }

   static List<BlockPos> At(Vec3d var0) {
      return Au(var0, BlockPos.ofFloored(var0));
   }

   static List<BlockPos> Au(Vec3d var0, BlockPos var1) {
      ArrayList var2 = new ArrayList(12);
      double var3 = Math.abs(var0.getX()) - Math.floor(Math.abs(var0.getX()));
      double var5 = Math.abs(var0.getZ()) - Math.floor(Math.abs(var0.getZ()));
      int var7 = Ax(var3, false);
      int var8 = Ax(var3, true);
      int var9 = Ax(var5, false);
      int var10 = Ax(var5, true);

      for (int var11 = 1; var11 < var7 + 1; var11++) {
         var2.add(Ay(var1, var11, 0.0, 1 + var9));
         var2.add(Ay(var1, var11, 0.0, -(1 + var10)));
      }

      for (int var12 = 0; var12 <= var8; var12++) {
         var2.add(Ay(var1, -var12, 0.0, 1 + var9));
         var2.add(Ay(var1, -var12, 0.0, -(1 + var10)));
      }

      for (int var13 = 1; var13 < var9 + 1; var13++) {
         var2.add(Ay(var1, 1 + var7, 0.0, var13));
         var2.add(Ay(var1, -(1 + var8), 0.0, var13));
      }

      for (int var14 = 0; var14 <= var10; var14++) {
         var2.add(Ay(var1, 1 + var7, 0.0, -var14));
         var2.add(Ay(var1, -(1 + var8), 0.0, -var14));
      }

      return var2;
   }

   static void Av(List<BlockPos> var0, Vec3d var1) {
      var0.sort(
         Comparator.comparingInt(Vec3i::getY)
            .thenComparing(Comparator.<BlockPos>comparingDouble(var1x -> var1.squaredDistanceTo(var1x.toCenterPos())).reversed())
      );
   }

   static int Aw(double var0) {
      return var0 >= 0.7 ? 1 : (var0 <= 0.3 ? -1 : 0);
   }

   static int Ax(double var0, boolean var2) {
      if (!var2) {
         return var0 >= 0.7 ? 1 : 0;
      } else {
         return var0 <= 0.3 ? 1 : 0;
      }
   }

   static BlockPos Ay(BlockPos var0, double var1, double var3, double var5) {
      if (var0.getX() < 0) {
         var1 = -var1;
      }

      if (var0.getY() < 0) {
         var3 = -var3;
      }

      if (var0.getZ() < 0) {
         var5 = -var5;
      }

      return var0.add(BlockPos.ofFloored(var1, var3, var5));
   }

   static Vec2f Az(PlayerInput var0) {
      float var1 = (var0.left() ? 1.0F : 0.0F) - (var0.right() ? 1.0F : 0.0F);
      float var2 = (!var0.forward() ? 0.0F : 1.0F) - (var0.backward() ? 1.0F : 0.0F);
      return new Vec2f(var1, var2).normalize();
   }

   static float AA(float var0, float var1) {
      return var1 + MathHelper.wrapDegrees(var0 - var1);
   }

   private boolean AB(BlockPos var1) {
      if (this.lE.player != null && this.ox != null) {
         Box var2 = this.ox;
         Box var3 = this.lE.player.getBoundingBox();
         Box var4 = new Box(
               Math.min(var2.minX, var3.minX),
               Math.min(var2.minY, var3.minY),
               Math.min(var2.minZ, var3.minZ),
               Math.max(var2.maxX, var3.maxX),
               Math.max(var2.maxY, var3.maxY),
               Math.max(var2.maxZ, var3.maxZ)
            )
            .expand(0.0625);
         return !new Box(var1).intersects(var4);
      } else {
         return false;
      }
   }

   interface Host {
      GrimCombatCoordinator.Participant participant();

      List<BlockPos> placementPlan();

      List<Block> allowedBlocks();

      boolean keepCamera();

      boolean safeTiming();

      boolean strictPlaceCheck();

      int blocksPerTick();

      int placeDelayTicks();

      long placeDelayMs();

      default boolean retryIfNotAck() {
         return false;
      }

      void onPlaced(BlockPos var1);
   }

   private record PendingPlacement(int expiresAtTick, int sequence) {
   }

   private final class PlaceRotationInput extends DelegatingInput {
      private final ClientPlayerEntity Bb;
      private final CrystalPlaceController.RotationIntent mx;
      private final boolean yG;
      private boolean qG;
      private float hF;
      private float dV;

      private PlaceRotationInput(Input var2, ClientPlayerEntity var3, CrystalPlaceController.RotationIntent var4, boolean var5) {
         super(var2);
         this.Bb = var3;
         this.mx = var4;
         this.yG = var5;
      }

      private boolean zm() {
         return this.qG;
      }

      @Override
      protected void qL() {
         if (CrystalPlaceController.this.lE.player == this.Bb && CrystalPlaceController.this.lE.player.input == this) {
            PlayerInput var1 = this.qI().playerInput == null ? PlayerInput.DEFAULT : this.qI().playerInput;
            Vec2f var2 = this.qI().getMovementInput();
            this.hF = this.Bb.getYaw();
            this.dV = this.Bb.getPitch();
            float var3 = GrimCombatCoordinator.wS() ? GrimCombatCoordinator.wQ() : this.hF;
            float var4 = CrystalPlaceController.AA(this.mx.yaw(), var3);
            PlayerInput var5 = CrystalAuraPlusPlus.oR(var1, var2, this.hF, var4);
            this.setState(var5, CrystalPlaceController.Az(var5));
            this.Bb.setYaw(var4);
            this.Bb.setPitch(this.mx.pitch());
            this.qG = true;
         }
      }

      private void zn(boolean var1) {
         if (this.qG && CrystalPlaceController.this.lE.player == this.Bb) {
            if (var1 || !this.yG) {
               this.Bb.setYaw(this.hF);
               this.Bb.setPitch(this.dV);
            }

            this.qG = false;
         }
      }
   }

   private record RotationIntent(float yaw, float pitch) {
   }

   private record ServerBlockObservation(BlockPos pos, BlockState state) {
   }

   record Shot(BlockPos target, BlockPos support, Direction face, Vec3d cursor, float yaw, float pitch) {
   }

   record Support(BlockPos position, Direction face, Vec3d cursor, Vec3d aim) {
   }
}
