package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.lang.invoke.StringConcatFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractItemEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerTickMovementEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent.Post;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting.Builder;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.CartographyTableBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.GrindstoneBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.LoomBlock;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SmithingTableBlock;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.Orientation;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.network.packet.c2s.play.PickItemFromBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.WorldView;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.attribute.EnvironmentAttributeAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;

public class EasyPlace extends AliceModule {
   private static final int lD = 40;
   private static final int fF = 40;
   private static final int yQ = 2;
   private static final double Bh = 30.25;
   private static final int cA = 80;
   private static final int vr = 4;
   private static final int Cc = 20;
   private static final int eZ = 4;
   private static final int nj = 100;
   private static final int zU = 40;
   private static final int mj = 60;
   private static EasyPlace uZ;
   private static final Block[] vo = new Block[]{
      Blocks.HOPPER,
      Blocks.DROPPER,
      Blocks.DISPENSER,
      Blocks.CHEST,
      Blocks.TRAPPED_CHEST,
      Blocks.ENDER_CHEST,
      Blocks.BARREL,
      Blocks.SHULKER_BOX,
      Blocks.WHITE_SHULKER_BOX,
      Blocks.ORANGE_SHULKER_BOX,
      Blocks.MAGENTA_SHULKER_BOX,
      Blocks.LIGHT_BLUE_SHULKER_BOX,
      Blocks.YELLOW_SHULKER_BOX,
      Blocks.LIME_SHULKER_BOX,
      Blocks.PINK_SHULKER_BOX,
      Blocks.GRAY_SHULKER_BOX,
      Blocks.LIGHT_GRAY_SHULKER_BOX,
      Blocks.CYAN_SHULKER_BOX,
      Blocks.PURPLE_SHULKER_BOX,
      Blocks.BLUE_SHULKER_BOX,
      Blocks.BROWN_SHULKER_BOX,
      Blocks.GREEN_SHULKER_BOX,
      Blocks.RED_SHULKER_BOX,
      Blocks.BLACK_SHULKER_BOX,
      Blocks.COMPARATOR,
      Blocks.REPEATER,
      Blocks.OBSERVER,
      Blocks.REDSTONE_WIRE,
      Blocks.PISTON,
      Blocks.STICKY_PISTON
   };
   private final SettingGroup uf = this.settings.getDefaultGroup();
   private final SettingGroup jX = this.settings.createGroup("Rotation");
   private final SettingGroup M = this.settings.createGroup("Smart Place");
   private final Setting<Boolean> nD = this.uf
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("all-blocks")).description("cho phep easy place hoat dong voi moi loai block")).defaultValue(true))
            .build()
      );
   private final Setting<List<Block>> CJ = this.uf
      .add(
         ((meteordevelopment.meteorclient.settings.BlockListSetting.Builder)((meteordevelopment.meteorclient.settings.BlockListSetting.Builder)((meteordevelopment.meteorclient.settings.BlockListSetting.Builder)new meteordevelopment.meteorclient.settings.BlockListSetting.Builder()
                     .name("blocks"))
                  .description("cac block ma easy place duoc phep dat khi tat che do All Blocks"))
               .defaultValue(vo)
               .filter(EasyPlace::vv)
               .visible(() -> !(Boolean)this.nD.get()))
            .build()
      );
   private final Setting<Boolean> AW = this.uf
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("show-status-message")).description("hien thi bat/tat Easy Place o action bar")).defaultValue(true))
            .build()
      );
   private final Setting<Boolean> vI = this.jX
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("rotation-spoof")).description("lang le gia mao huong xoay khi dat cac block co huong"))
               .defaultValue(true))
            .build()
      );
   private final Setting<EasyPlace.RotationMode> rR = this.jX
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                     .name("direction"))
                  .description("huong dat block. tu dong doc huong tu litematica. van ap dung cho Schematic Aura va hopper khi tat Rotation Spoof"))
               .defaultValue(EasyPlace.RotationMode.Auto))
            .build()
      );
   private final Setting<Boolean> CD = this.M
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("smart-placement")).description("chon block dung yeu cau cua litematica truoc khi dat"))
               .defaultValue(true))
            .build()
      );
   private final Setting<EasyPlace.InventoryPickSourceMode> fw = this.M
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                        .name("inventory-pick-source"))
                     .description("chon cac block xac nhan tren the gioi de chuyen item vao inventory"))
                  .defaultValue(EasyPlace.InventoryPickSourceMode.ServerRange))
               .visible(this.CD::get))
            .build()
      );
   private final Setting<EasyPlace.InventoryPickSyncMode> DI = this.M
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                        .name("inventory-pick-sync"))
                     .description("ServerAck se cho server xac nhan chuyen tui do. SameTick du doan ngay lap tuc nhung co the bi server grim phat hien"))
                  .defaultValue(EasyPlace.InventoryPickSyncMode.ServerAck))
               .visible(this.CD::get))
            .build()
      );
   private final Setting<EasyPlace.DependencySyncMode> gt = this.M
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                        .name("dependency-sync"))
                     .description(
                        "ServerAck cho server xac nhan tung block. Immediate cho Schematic Aura noi chuoi tren block vua du doan de nhanh hon nhung de bi server Grim phat hien hon"
                     ))
                  .defaultValue(EasyPlace.DependencySyncMode.ServerAck))
               .visible(this.CD::get))
            .build()
      );
   private final Setting<Boolean> gw = this.M
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("schematic-only")).description("ngan dat block khac vao vung litematica da chon"))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> xs = this.M
      .add(
         ((Builder)((Builder)((Builder)((Builder)new Builder().name("schematic-lock"))
                     .description(
                        "chi dat block trong o schematic Litematica dang render va khong phai air. chi ap dung ben trong vung schematic da chon, bo qua o dang bi an theo layer"
                     ))
                  .defaultValue(false))
               .visible(this.gw::get))
            .build()
      );
   private final Setting<Boolean> xZ = this.M
      .add(
         ((Builder)((Builder)((Builder)((Builder)new Builder().name("schematic-aura"))
                     .description("tu dong dat cac block litematica o gan ma khong can giu chuot phai. can bat Smart Placement hoac Schematic Only"))
                  .defaultValue(false))
               .visible(() -> (Boolean)this.CD.get() || (Boolean)this.gw.get()))
            .build()
      );
   private final Setting<Double> tB = this.M
      .add(
         ((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)((meteordevelopment.meteorclient.settings.DoubleSetting.Builder)new meteordevelopment.meteorclient.settings.DoubleSetting.Builder()
                     .name("aura-range"))
                  .description("khoang cach toi da de quet block schematic. van ap dung tam voi vanilla"))
               .defaultValue(4.0)
               .min(1.0)
               .max(6.0)
               .sliderMax(5.0)
               .visible(() -> (Boolean)this.xZ.get() && ((Boolean)this.CD.get() || (Boolean)this.gw.get())))
            .build()
      );
   private final Setting<Integer> Dc = this.M
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("aura-delay"))
                     .description("so tick toi thieu giua hai lan tu dong dat schematic"))
                  .defaultValue(4))
               .range(1, 20)
               .sliderRange(1, 10)
               .visible(() -> {
                  int var10000 = this.xZ.get() ? 6618 : 6619;

                  while (true) {
                     int var1 = var10000;
                     if (var1 == 6618) {
                        if ((Boolean)this.CD.get() || (Boolean)this.gw.get()) {
                           var2 = true;
                           break;
                        }
                     } else if (var1 == 6619) {
                        var10000 = 6617;
                        continue;
                     }

                     var2 = false;
                     break;
                  }

                  return var2;
               }))
            .build()
      );
   private final Setting<Boolean> yH = this.M
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("hopper-force-facing"))
                  .description("ep hopper huong dung theo schematic Litematica thay vi theo huong dang nhin"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> HB = this.M
      .add(
         ((Builder)((Builder)((Builder)((Builder)new Builder().name("hopper-force-when-hidden"))
                     .description("van bam vao block ho tro du bi chan tam nhin. dat duoc nhieu goc nhung de bi flag"))
                  .defaultValue(false))
               .visible(this.yH::get))
            .build()
      );
   private boolean HY;
   private boolean xJ;
   private int gp;
   private EasyPlace.PendingPlacement aD;
   private EasyPlace.PendingDependencyPlacement if;
   private EasyPlace.PostPlacementRotation BU;
   private final Map<BlockPos, EasyPlace.PredictedPlacement> gU = new ConcurrentHashMap<>();
   private final Map<BlockPos, EasyPlace.PostPlacementAdjustment> yV = new ConcurrentHashMap<>();
   private final ConcurrentLinkedQueue<BlockPos> iT = new ConcurrentLinkedQueue<>();
   private int Ds;
   private boolean sN;
   private int eh;
   private int xc;
   private int ja;
   private final Map<BlockPos, Integer> zx = new ConcurrentHashMap<>();
   private int IJ;
   private String zj;
   private boolean Ga;
   private EasyPlace.PendingInventoryTransfer Cg;
   private EasyPlace.PendingExternalInventoryWindow gL;
   private boolean Ia;
   private Object pN;
   private Method Fz;
   private Method jD;
   private boolean lt;
   private boolean Jp;
   private Input xI;
   private EasyPlace.PlacementInput Hs;

   public EasyPlace() {
      super(AliceAddon.CATEGORY, "easy-place", "Dat redstone va khoi len cac khoi co tuong tac ma khong can cui, kem ho tro dat thong minh cho Litematica.");
   }

   @Override
   public void onActivate() {
      this.vx();
      this.pN = this.mc.world;
      this.tn();
      if (this.sD()) {
         if (!FabricLoader.getInstance().isModLoaded("litematica")) {
            this.error("Easy Place requires Litematica for Smart Placement, Schematic Only, Schematic Lock and Schematic Aura.", new Object[0]);
            this.sE();
            return;
         }

         if (!EasyPlace.LitematicaBridge.initialize()) {
            this.error("The installed Litematica version is not compatible with Easy Place.", new Object[0]);
            this.sE();
            return;
         }
      }

      if (!this.vy()) {
         this.error("Disable or remove the standalone Easy Redstone mod before using Easy Place.", new Object[0]);
         this.sE();
      } else {
         uZ = this;
         super.onActivate();
         this.vA(true);
      }
   }

   @Override
   public void onDeactivate() {
      if (uZ == this) {
         uZ = null;
      }

      this.vx();
      this.pN = null;
      this.to();
      this.vA(false);
      super.onDeactivate();
      this.vz();
   }

   private boolean sD() {
      return (Boolean)this.CD.get() || (Boolean)this.gw.get() || (Boolean)this.xs.get() || (Boolean)this.xZ.get();
   }

   private void sE() {
      this.vx();
      this.pN = null;
      this.to();
      this.disable();
   }

   @AliceEventHandler
   public void sF(InteractBlockEvent var1) {
      if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
         int var10000 = !this.xJ ? 4075 : 4074;

         while (true) {
            int var3 = var10000;
            if (var3 == 4074) {
               return;
            }

            if (var3 != 4075) {
               BlockHitResult var2 = var1.result;
               if (var2 == null || !this.uK(var2.getBlockPos()) && !this.uK(this.getPlacePos(var2))) {
                  if (!this.xJ && this.sH(var1.hand, var1.result)) {
                     var1.cancel();
                     return;
                  }

                  if (this.sZ(var1.hand, var1.result, false)) {
                     var1.cancel();
                  }

                  return;
               }

               var1.cancel();
               return;
            }

            var10000 = 4073;
         }
      }
   }

   @AliceEventHandler
   public void sG(InteractItemEvent var1) {
      if (this.sH(var1.hand, null)) {
         var1.toReturn = ActionResult.SUCCESS;
      }
   }

   private boolean sH(Hand var1, BlockHitResult var2) {
      if (this.mc.player == null || this.mc.world == null || this.mc.interactionManager == null || var1 == null || this.xJ) {
         return false;
      } else if (!(Boolean)this.CD.get() && !(Boolean)this.gw.get()) {
         return false;
      } else if (var2 != null && var2.getType() == Type.BLOCK) {
         BlockPos var7 = this.getPlacePos(var2);
         int var6 = EasyPlace.LitematicaBridge.sc(this.mc, var7, this::uY) ? '쒩' : '쒨';

         while (true) {
            switch (var6) {
               case 50344:
                  return false;
               case 50345:
                  var6 = 50343;
                  break;
               default:
                  return var7 == null ? false : this.sI(var1, var7);
            }
         }
      } else {
         BlockHitResult var4 = EasyPlace.LitematicaBridge.sf(this.mc, this::uY);
         BlockPos var10000;
         if (var4 == null) {
            var10000 = null;
         } else {
            char var5 = 'ꦮ';
            var10000 = var4.getBlockPos();
         }

         BlockPos var3 = var10000;
         return var3 == null ? false : this.sI(var1, var3);
      }
   }

   private boolean sI(Hand var1, BlockPos var2) {
      if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null && var1 != null && var2 != null && !this.xJ) {
         EasyPlace.PendingDependencyPlacement var3 = this.if;
         if (var3 != null && !var3.Fd.equals(var2)) {
            return true;
         } else if (!EasyPlace.LitematicaBridge.sc(this.mc, var2, this::uY)) {
            return false;
         } else {
            EasyPlace.DependencyResolution var4 = this.sK(var2);
            if (var4.state == EasyPlace.DependencyResolutionState.Invalid) {
               return true;
            } else if (var4.state == EasyPlace.DependencyResolutionState.Waiting) {
               this.sN(var1, var2);
               return true;
            } else {
               var3 = this.if;
               if (var3 != null) {
                  int var10000 = !var3.Fd.equals(var2) ? '\uf07c' : '\uf07b';

                  label45:
                  while (true) {
                     switch (var10000) {
                        case 61563:
                           if (!var4.placePos.equals(var3.fs)) {
                              var3.fs = var4.placePos;
                              var3.kD = 0;
                           }
                           break label45;
                        case 61564:
                           var10000 = 61562;
                           break;
                        default:
                           break label45;
                     }
                  }
               }

               if (!var4.placePos.equals(var2)) {
                  this.sN(var1, var2);
               }

               return this.sJ(var1, var4.placePos);
            }
         }
      } else {
         return false;
      }
   }

   private boolean sJ(Hand var1, BlockPos var2) {
      if (this.mc.player != null) {
         char var17;
         if (this.mc.world != null) {
            if (this.mc.interactionManager != null && var1 != null && var2 != null) {
               int var10000 = this.xJ ? 21142 : 21141;

               while (true) {
                  switch (var10000) {
                     case 21141:
                        if (EasyPlace.LitematicaBridge.sc(this.mc, var2, this::uY)) {
                           Item var3 = EasyPlace.LitematicaBridge.getRequiredItem(var2);
                           Block var4 = vu(var3);
                           boolean var5 = false;
                           BlockHitResult var6;
                           if (!(var4 instanceof HopperBlock)) {
                              if (var4 instanceof ChestBlock) {
                                 ChestType var7 = EasyPlace.LitematicaBridge.getChestType(var2);
                                 var5 = var7 != null;
                                 var6 = var7 == null ? this.uw(var2) : this.uA(var2, var7, var4);
                              } else {
                                 label85: {
                                    List var10 = EasyPlace.LitematicaBridge.getPlacementSupportDirections(var2);
                                    var6 = this.uz(var2, var10);
                                    if (var6 == null) {
                                       var10000 = var10.isEmpty() ? '\uf79a' : '\uf79b';

                                       while (true) {
                                          int var9 = var10000;
                                          if (var9 == 63386) {
                                             var6 = this.uw(var2);
                                             break label85;
                                          }

                                          if (var9 != 63387) {
                                             break;
                                          }

                                          var10000 = 63385;
                                       }
                                    }

                                    if (!var10.isEmpty()) {
                                       var5 = true;
                                    }
                                 }
                              }
                           } else {
                              Direction var11 = this.getWantedHopperFacing(var2);
                              if (var11 != null) {
                                 var5 = true;
                                 var6 = this.uy(var2, var11, (Boolean)this.yH.get() && (Boolean)this.HB.get());
                              } else {
                                 var6 = this.uw(var2);
                              }
                           }

                           if (var6 != null) {
                              return this.sZ(var1, var6, true);
                           }

                           if (var5 || !this.uE(var2)) {
                              this.uH(var2, !var5 ? "khong co block do" : "thieu mat do phia can thiet");
                           }

                           return true;
                        }

                        return false;
                     case 21142:
                        var10000 = 21140;
                        break;
                     default:
                        return false;
                  }
               }
            }
         } else {
            while (var17 == 'ꨂ') {
               var17 = 'ꨀ';
            }
         }
      }

      return false;
   }

   private EasyPlace.DependencyResolution sK(BlockPos var1) {
      return this.sL(var1, new HashSet<>());
   }

   private EasyPlace.DependencyResolution sL(BlockPos var1, Set<BlockPos> var2) {
      if (var1 != null && this.mc.world != null && var2.add(var1)) {
         BlockState var3 = EasyPlace.LitematicaBridge.getState(var1);
         if (var3 != null && !var3.isAir()) {
            BlockPos var4 = EasyPlace.LitematicaBridge.getPlacementAnchor(var1);
            if (var4 == null || var4.equals(var1)) {
               for (BlockPos var7 : this.getSchematicDependencies(var1)) {
                  if (!this.sM(var7)) {
                     if (this.gU.containsKey(var7)) {
                        return EasyPlace.DependencyResolution.qP();
                     }

                     BlockState var8 = EasyPlace.LitematicaBridge.getState(var7);
                     if (var8 != null && !var8.isAir()) {
                        if (!this.mc.world.getBlockState(var7).isReplaceable()) {
                           return EasyPlace.DependencyResolution.qQ();
                        }

                        return this.sL(var7, var2);
                     }

                     return EasyPlace.DependencyResolution.qQ();
                  }
               }

               return EasyPlace.DependencyResolution.qO(var1);
            } else if (this.gU.containsKey(var4)) {
               return EasyPlace.DependencyResolution.qP();
            } else if (this.sM(var4)) {
               return EasyPlace.DependencyResolution.qQ();
            } else if (!this.mc.world.getBlockState(var4).isReplaceable()) {
               return EasyPlace.DependencyResolution.qQ();
            } else {
               char var12;
               while (var12 == '谦') {
                  var12 = '谤';
               }

               return this.sL(var4, var2);
            }
         } else {
            return EasyPlace.DependencyResolution.qQ();
         }
      } else {
         return EasyPlace.DependencyResolution.qQ();
      }
   }

   private List<BlockPos> getSchematicDependencies(BlockPos var1) {
      ArrayList var2 = new ArrayList();

      for (Direction var4 : EasyPlace.LitematicaBridge.getPlacementSupportDirections(var1)) {
         var2.add(var1.offset(var4).toImmutable());
      }

      BlockState var6 = EasyPlace.LitematicaBridge.getState(var1);
      if (var6 != null && var6.getBlock() instanceof HopperBlock) {
         Direction var7 = this.getWantedHopperFacing(var1);
         if (var7 != null) {
            BlockPos var5 = var1.offset(var7).toImmutable();
            if (!var2.contains(var5)) {
               var2.add(var5);
            }
         }
      }

      var2.sort(Comparator.comparingInt(Vec3i::getY).thenComparingDouble(var1x -> var1x.getSquaredDistance(var1)));
      return var2;
   }

   private boolean sM(BlockPos var1) {
      if (var1 != null && this.mc.world != null) {
         short var7;
         while (var7 == 15419) {
            var7 = 15417;
         }

         BlockState var2 = EasyPlace.LitematicaBridge.getState(var1);
         if (this.uF(var1)) {
            return var2 == null || var2.isAir() || this.mc.world.getBlockState(var1).getBlock() == var2.getBlock();
         } else if (this.gt.get() != EasyPlace.DependencySyncMode.Immediate) {
            return false;
         } else {
            EasyPlace.PredictedPlacement var3 = this.gU.get(var1);
            return var3 != null && var3.block != null && var3.block != Blocks.AIR && (var2 == null || var2.isAir() || var3.block == var2.getBlock());
         }
      } else {
         return false;
      }
   }

   private void sN(Hand var1, BlockPos var2) {
      if (var1 != null) {
         if (var2 != null) {
            EasyPlace.PendingDependencyPlacement var3 = this.if;
            if (var3 == null || !var3.Fd.equals(var2)) {
               this.if = new EasyPlace.PendingDependencyPlacement(var1, var2.toImmutable());
            }

            return;
         }
      } else {
         char var4 = '쏫';
      }
   }

   private void sO() {
      EasyPlace.PendingDependencyPlacement var1 = this.if;
      if (var1 != null && this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
         var1.kD++;
         if (var1.kD > 40) {
            this.if = null;
            this.uH(var1.Fd, "chuoi block phu thuoc qua thoi gian cho");
         } else if (!this.gU.containsKey(var1.Fd) && this.mc.world.getBlockState(var1.Fd).isReplaceable()) {
            char var7;
            while (var7 == '\ud8aa') {
               var7 = '\ud8a8';
            }

            BlockState var2 = EasyPlace.LitematicaBridge.getState(var1.Fd);
            if (var2 != null && !var2.isAir()) {
               if ((Boolean)this.xZ.get() || this.mc.options != null && this.mc.options.useKey.isPressed()) {
                  if (this.mc.currentScreen == null && !this.HY && this.aD == null && this.Cg == null && !this.Ia && !this.xJ && this.ja <= 0) {
                     this.sI(var1.FR, var1.Fd);
                  }
               } else {
                  this.if = null;
               }
            } else {
               this.if = null;
            }
         } else {
            this.if = null;
         }
      }
   }

   @AliceEventHandler
   public void sP(Receive var1) {
      if (var1.packet instanceof BlockUpdateS2CPacket var2) {
         this.vh(var2.getPos());
      } else if (var1.packet instanceof ChunkDeltaUpdateS2CPacket var3 && this.vg()) {
         var3.visitUpdates((var1x, var2x) -> this.vh(var1x));
      }

      EasyPlace.PendingInventoryTransfer var5 = this.Cg;
      if (var5 != null
         && (
            var1.packet instanceof InventoryS2CPacket
               || var1.packet instanceof ScreenHandlerSlotUpdateS2CPacket
               || var1.packet instanceof UpdateSelectedSlotS2CPacket
         )) {
         this.mc.execute(() -> this.mc.execute(() -> {
            if (this.Cg == var5) {
               var5.CT = true;
               if (var5.oG == EasyPlace.InventoryTransferPhase.PickSettle) {
                  var5.be = 0;
               }
            }
         }));
      }
   }

   @AliceEventHandler(priority = 100)
   public void sQ(PlayerTickMovementEvent var1) {
      this.tn();
   }

   @AliceEventHandler(priority = -100)
   public void sR(Post var1) {
      this.Ga = this.us();
      this.ui();
      EasyPlace.PendingPlacement var2 = this.aD;
      if (var2 != null && var2.sV) {
         boolean var3 = var2.iF;
         this.tI(var2);
         var2.iF = false;
         if (!var3) {
            this.tO(var2.xi);
            this.tP();
         } else {
            var2.Gv = true;
            var2.cB = 0;
         }
      } else {
         this.tN();
      }
   }

   @AliceEventHandler
   public void sS(Pre var1) {
      EasyPlace.LitematicaBridge.rJ();
      if (this.pN != this.mc.world) {
         this.vx();
         this.pN = this.mc.world;
      }

      this.Ia = false;
      this.tn();
      this.up();
      this.ub();
      this.tP();
      this.uL();
      this.sO();
      this.sT();
      this.sU();
   }

   private void sT() {
      if (!(Boolean)this.xZ.get()
         && this.mc.currentScreen == null
         && this.mc.options != null
         && this.mc.options.useKey.isPressed()
         && !this.HY
         && this.Cg == null
         && !this.Ia
         && this.if == null
         && !this.xJ
         && this.ja <= 0) {
         BlockHitResult var1 = this.mc.crosshairTarget instanceof BlockHitResult var2 && var2.getType() == Type.BLOCK ? var2 : null;
         this.sH(Hand.MAIN_HAND, var1);
      }
   }

   private void sU() {
      if ((Boolean)this.xZ.get()) {
         if (!(Boolean)this.CD.get() && !(Boolean)this.gw.get()) {
            this.vB("Schematic Aura can bat Smart Placement hoac Schematic Only");
         } else {
            if (this.xc <= 0 && this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null && this.mc.currentScreen == null) {
               char var26;
               if (!this.mc.player.isSneaking()) {
                  if (!this.HY && this.aD == null && this.Cg == null && !this.Ia && this.if == null && !this.xJ && this.ja <= 0) {
                     double var1 = Math.min((Double)this.tB.get(), this.mc.player.getBlockInteractionRange());
                     double var3 = var1 * var1;
                     Vec3d var5 = this.mc.player.getEyePos();
                     BlockPos var6 = BlockPos.ofFloored(var5);
                     int var7 = (int)Math.ceil(var1);
                     BlockPos var8 = null;
                     int var9 = Integer.MAX_VALUE;
                     double var10 = Double.MAX_VALUE;
                     Iterator var12 = BlockPos.iterate(var6.add(-var7, -var7, -var7), var6.add(var7, var7, var7)).iterator();

                     while (true) {
                        int var10000 = var12.hasNext() ? '\uf70a' : '\uf70b';

                        while (true) {
                           int var22 = var10000;
                           if (var22 == 63242) {
                              BlockPos var13 = (BlockPos)var12.next();
                              double var14 = var5.squaredDistanceTo(Vec3d.ofCenter(var13));
                              if (!(var14 > var3) && !this.zx.containsKey(var13) && EasyPlace.LitematicaBridge.sc(this.mc, var13, this::uY)) {
                                 EasyPlace.DependencyResolution var16 = this.sK(var13);
                                 if (var16.state == EasyPlace.DependencyResolutionState.Ready) {
                                    BlockPos var17 = var16.placePos;
                                    double var18 = var5.squaredDistanceTo(Vec3d.ofCenter(var17));
                                    int var20 = var17.getY();
                                    if (!(var18 > var3) && var20 <= var9 && (var20 != var9 || !(var18 >= var10)) && this.sW(var17)) {
                                       var8 = var13.toImmutable();
                                       var9 = var20;
                                       var10 = var18;
                                    }
                                 }
                              }
                              break;
                           }

                           if (var22 != 63243) {
                              if (var8 == null) {
                                 return;
                              }

                              this.sI(Hand.MAIN_HAND, var8);
                              if (this.aD == null && !this.HY && this.Cg == null && this.if == null && !this.gU.containsKey(var8)) {
                                 this.zx.put(var8, 40);
                              }

                              this.xc = (Integer)this.Dc.get();
                              return;
                           }

                           var10000 = 63241;
                        }
                     }
                  }
               } else {
                  while (var26 == '\ue68a') {
                     var26 = '\ue688';
                  }
               }
            }
         }
      }
   }

   private void sV() {
      if (!this.zx.isEmpty()) {
         this.zx.replaceAll((var0, var1) -> var1 - 1);
         this.zx.values().removeIf(var0 -> var0 <= 0);
      }
   }

   private boolean sW(BlockPos var1) {
      if (this.mc.world != null) {
         if (this.mc.world.isPosLoaded(var1)) {
            Item var2 = EasyPlace.LitematicaBridge.getRequiredItem(var1);
            Block var3 = vu(var2);
            if (var2 != null && var2 != Items.AIR && var3 != Blocks.AIR && ((Boolean)this.nD.get() || ((List)this.CJ.get()).contains(var3)) && this.sX(var2)) {
               if (!(var3 instanceof HopperBlock)) {
                  if (var3 instanceof ChestBlock) {
                     ChestType var8 = EasyPlace.LitematicaBridge.getChestType(var1);
                     return var8 == null ? this.uw(var1) != null : this.uA(var1, var8, var3) != null;
                  }

                  List var7 = EasyPlace.LitematicaBridge.getPlacementSupportDirections(var1);
                  return var7.isEmpty() ? this.uw(var1) != null : this.uz(var1, var7) != null;
               }

               Direction var4 = this.getWantedHopperFacing(var1);
               if (var4 == null) {
                  return this.uw(var1) != null;
               }

               BlockPos var5 = var1.offset(var4);
               if (!this.sM(var5)) {
                  return false;
               }

               return this.uy(var1, var4, (Boolean)this.yH.get() && (Boolean)this.HB.get()) != null;
            }

            return false;
         }
      } else {
         short var6 = 2221;
      }

      return false;
   }

   private boolean sX(Item var1) {
      if (var1 != null && var1 != Items.AIR && this.mc.player != null) {
         if (this.mc.player.getMainHandStack().isOf(var1) || this.mc.player.getOffHandStack().isOf(var1)) {
            return true;
         } else if (!(Boolean)this.CD.get()) {
            return false;
         } else {
            FindItemResult var2 = InvUtils.find(new Item[]{var1});
            return var2.found() && (var2.isHotbar() || var2.isMain());
         }
      } else {
         return false;
      }
   }

   @AliceEventHandler
   public void sY(meteordevelopment.meteorclient.events.world.TickEvent.Post var1) {
      this.vi();
      this.uZ();
      this.uW();
      this.sV();
      if (this.Ds > 0) {
         this.Ds--;
      }

      if (this.eh > 0) {
         this.eh--;
      }

      if (this.xc > 0) {
         this.xc--;
      }

      if (this.ja > 0) {
         this.ja--;
      }

      if (this.IJ > 0) {
         this.IJ--;
      }
   }

   private boolean sZ(Hand var1, BlockHitResult var2, boolean var3) {
      BlockPos var4 = this.getPlacePos(var2);
      Hand var5 = var1;
      Item var6 = !this.CD.get() && !this.gw.get() ? null : EasyPlace.LitematicaBridge.getRequiredItem(var4);
      if ((Boolean)this.CD.get()) {
         if (var6 != null && var6 != Items.AIR) {
            EasyPlace.InventoryAcquireResult var7 = this.ua(var6, var4);
            if (var7 == EasyPlace.InventoryAcquireResult.Waiting) {
               return true;
            }

            int var10000 = var7 != EasyPlace.InventoryAcquireResult.Ready ? '쿌' : '쿋';

            label281:
            while (true) {
               switch (var10000) {
                  case 53195:
                     var5 = Hand.MAIN_HAND;
                     break label281;
                  case 53196:
                     var10000 = 53194;
                     break;
                  default:
                     break label281;
               }
            }
         }
      } else {
         short var19 = 28357;
      }

      ItemStack var24 = this.mc.player.getStackInHand(var5);
      if (!this.vq(var24)) {
         if (this.vq(this.mc.player.getMainHandStack())) {
            var5 = Hand.MAIN_HAND;
         } else if (this.vq(this.mc.player.getOffHandStack())) {
            var5 = Hand.OFF_HAND;
         }

         var24 = this.mc.player.getStackInHand(var5);
      }

      if (this.vr(var24, var4)) {
         return true;
      } else if ((Boolean)this.gw.get() && EasyPlace.LitematicaBridge.rF() && EasyPlace.LitematicaBridge.rS(var4) && vs(var24) && !vt(var24, var6)) {
         this.vB("Schematic Only: block trong tay khong dung o schematic nay");
         return true;
      } else {
         boolean var8 = this.vq(var24);
         boolean var9 = var8 || this.vq(this.mc.player.getMainHandStack()) || this.vq(this.mc.player.getOffHandStack());
         if (var9) {
            BlockHitResult var10 = var2;
            boolean var11 = false;
            boolean var12 = false;
            Block var13 = var24.getItem() instanceof BlockItem var14 ? var14.getBlock() : null;
            if (var13 instanceof ChestBlock) {
               ChestType var25 = EasyPlace.LitematicaBridge.getChestType(var4);
               if (var25 != null) {
                  BlockHitResult var29 = this.uA(var4, var25, var13);
                  if (var29 == null) {
                     this.uH(var4, "khong tim thay chest ben canh cho chest doi");
                     return true;
                  }

                  var10 = var29;
               }
            }

            if (var13 instanceof HopperBlock) {
               Direction var26 = this.getWantedHopperFacing(var4);
               if (var26 != null) {
                  BlockHitResult var36;
                  if (!var3) {
                     var36 = this.vj(var4, var26, true);
                  } else {
                     boolean var10003;
                     label256: {
                        if ((Boolean)this.yH.get()) {
                           if ((Boolean)this.HB.get()) {
                              var10003 = true;
                              break label256;
                           }

                           short var42;
                           while (var42 == 14457) {
                              var42 = 14455;
                           }
                        }

                        var10003 = false;
                     }

                     var36 = this.uy(var4, var26, var10003);
                  }

                  BlockHitResult var30 = var36;
                  if (!var3 && var30 == null && (Boolean)this.yH.get()) {
                     int var37 = !this.HB.get() ? '\ue129' : '\ue128';

                     while (true) {
                        int var21 = var37;
                        if (var21 == 57640) {
                           var30 = this.vl(var4, var26);
                           var12 = var30 != null;
                           break;
                        }

                        if (var21 != 57641) {
                           break;
                        }

                        var37 = 57639;
                     }
                  }

                  if (var30 == null) {
                     this.uH(
                        var4,
                        StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","khong thay mat de hopper huong ">(
                           var26.asString()
                        )
                     );
                     return true;
                  }

                  var10 = var30;
                  var11 = true;
                  if (!this.vn(var30.getBlockPos(), var30.getPos())) {
                  }
               }
            } else if ((Boolean)this.CD.get() && EasyPlace.LitematicaBridge.sb(var4)) {
               BlockHitResult var27 = this.uv(var4);
               if (var27 != null) {
                  var10 = var27;
               }
            }

            BlockState var28 = EasyPlace.LitematicaBridge.getState(var4);
            if (this.tc(var24, var28, var4)) {
               BlockHitResult var31 = this.td(var4, var5, var13, var28, var10);
               if (var31 == null) {
                  this.uH(var4, "khong thay mat cho state dung theo schematic");
                  return true;
               }

               var10 = var31;
               var11 = true;
            }

            if (this.gU.containsKey(var10.getBlockPos()) && this.gt.get() == EasyPlace.DependencySyncMode.ServerAck) {
               return true;
            } else {
               label227:
               if (var8 && !this.xJ) {
                  if (!(Boolean)this.vI.get()) {
                     int var38 = !var3 ? '齏' : '齐';

                     while (true) {
                        int var22 = var38;
                        if (var22 == 40783) {
                           if (!var11) {
                              break label227;
                           }
                           break;
                        }

                        if (var22 != 40784) {
                           break;
                        }

                        var38 = 40782;
                     }
                  }

                  if (this.HY) {
                     return true;
                  }

                  if (var11) {
                     BlockHitResult var33 = this.ta(var10, var4);
                     if (var33 != null && this.tb(var5, var33, var4, var13)) {
                        this.tg(var5, var33, var4);
                        return true;
                     }

                     this.ti(var5, var10, var4, var12);
                     return true;
                  }

                  EasyPlace.RotationMode var32 = (EasyPlace.RotationMode)this.rR.get();
                  Orientation var16 = null;
                  if (var32 == EasyPlace.RotationMode.Auto) {
                     if (var13 instanceof CrafterBlock) {
                        var16 = EasyPlace.LitematicaBridge.getOrientation(var4);
                     } else {
                        char var23 = '쯬';
                     }

                     var32 = EasyPlace.LitematicaBridge.getRotation(var4);
                  }

                  if (var32 != null && var32 != EasyPlace.RotationMode.Auto) {
                     BlockHitResult var17 = this.ta(var10, var4);
                     if (var17 != null && this.tb(var5, var17, var4, var13)) {
                        this.tg(var5, var17, var4);
                        return true;
                     }

                     if (var13 instanceof ObserverBlock) {
                        var32 = var32.su();
                     }

                     BlockHitResult var18 = var3 ? var10 : this.ut(var4, var10, var32, var13);
                     if (var18 == null) {
                        return true;
                     }

                     this.th(var5, var18, var4, var32, var13, var16, var12);
                     return true;
                  }

                  if (var3) {
                     this.ti(var5, var10, var4, var12);
                     return true;
                  }
               }

               if (var3) {
                  return true;
               } else if (!this.xJ && this.vb(var4, var10, var24.getItem())) {
                  this.ti(var5, var10, var4, false);
                  return true;
               } else {
                  return false;
               }
            }
         } else {
            return false;
         }
      }
   }

   private BlockHitResult ta(BlockHitResult var1, BlockPos var2) {
      if (this.mc.player != null && this.mc.world != null && var1 != null && var2 != null) {
         double var3 = this.mc.player.getBlockInteractionRange();
         if (this.mc.player.raycast(var3, 1.0F, false) instanceof BlockHitResult var6 && var6.getType() == Type.BLOCK) {
            int var10000 = tH(this.mc.player.getYaw(), this.mc.player.lastYaw) > 5.0 ? 14171 : 14170;

            while (true) {
               switch (var10000) {
                  case 14170:
                     if (!(Math.abs(this.mc.player.getPitch() - this.mc.player.lastPitch) > 5.0)
                        && var6.getBlockPos().equals(var1.getBlockPos())
                        && var6.getSide() == var1.getSide()
                        && this.getPlacePos(var6).equals(var2)
                        && this.vn(var6.getBlockPos(), var6.getPos())) {
                        return var6;
                     }

                     return null;
                  case 14171:
                     var10000 = 14169;
                     break;
                  default:
                     return null;
               }
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private boolean tb(Hand var1, BlockHitResult var2, BlockPos var3, Block var4) {
      if (this.mc.player != null && var1 != null && var2 != null && var3 != null && var4 != null) {
         ItemStack var5 = this.mc.player.getStackInHand(var1);
         if (var5.isEmpty()) {
            return false;
         } else {
            ItemPlacementContext var6 = new ItemPlacementContext(this.mc.player, var1, var5, var2);
            if (!var6.getBlockPos().equals(var3)) {
               return false;
            } else {
               BlockState var7 = this.getPlacementStateForHit(var5, var4, var6);
               return var7 != null && EasyPlace.LitematicaBridge.rY(var3, var7);
            }
         }
      } else {
         return false;
      }
   }

   private boolean tc(ItemStack var1, BlockState var2, BlockPos var3) {
      if (var1 != null) {
         int var10000 = var1.isEmpty() ? 25517 : 25516;

         while (true) {
            switch (var10000) {
               case 25516:
                  if (var2 != null && !var2.isAir()) {
                     return var2.contains(SlabBlock.TYPE)
                        || var1.getItem() instanceof VerticallyAttachableBlockItem
                        || var2.contains(Properties.AXIS)
                        || var2.contains(Properties.BLOCK_HALF)
                        || var2.contains(Properties.VERTICAL_DIRECTION)
                        || var2.contains(Properties.DOOR_HINGE)
                        || var2.contains(Properties.ROTATION)
                        || !EasyPlace.LitematicaBridge.getPlacementSupportDirections(var3).isEmpty()
                           && (
                              var2.contains(Properties.FACING)
                                 || var2.contains(Properties.HORIZONTAL_FACING)
                                 || var2.contains(Properties.BLOCK_FACE)
                                 || var2.contains(Properties.ATTACHMENT)
                                 || var2.contains(Properties.HANGING)
                           );
                  }

                  return false;
               case 25517:
                  var10000 = 25515;
                  break;
               default:
                  return false;
            }
         }
      } else {
         return false;
      }
   }

   private BlockHitResult td(BlockPos var1, Hand var2, Block var3, BlockState var4, BlockHitResult var5) {
      if (this.mc.player != null && this.mc.world != null && var1 != null && var2 != null && var3 != null && var4 != null) {
         if (this.te(var1, var2, var3, var4, var5)) {
            return var5;
         } else {
            Vec3d var6 = this.mc.player.getEyePos();
            float var7 = this.mc.player.getYaw();
            float var8 = this.mc.player.getPitch();
            BlockHitResult var9 = null;
            double var10 = Double.MAX_VALUE;

            label60:
            for (Direction var15 : Direction.values()) {
               BlockPos var16 = var1.offset(var15);
               int var10000 = this.uG(var16) ? '\udebb' : '\udeba';

               while (true) {
                  switch (var10000) {
                     case 57018:
                        continue label60;
                     case 57019:
                        var10000 = 57017;
                        break;
                     default:
                        Direction var17 = var15.getOpposite();

                        for (Vec3d var21 : this.vm(var16, var17)) {
                           if (this.vn(var16, var21)) {
                              BlockHitResult var22 = new BlockHitResult(var21, var17, var16, false);
                              if (this.te(var1, var2, var3, var4, var22)) {
                                 double var23 = var6.squaredDistanceTo(var21);
                                 if (this.tz(var6, var7, var8, var16)) {
                                    var23 -= 1000.0;
                                 }

                                 if (var23 < var10) {
                                    var10 = var23;
                                    var9 = var22;
                                 }
                              }
                           }
                        }
                        continue label60;
                  }
               }
            }

            return var9;
         }
      } else {
         return null;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean te(BlockPos var1, Hand var2, Block var3, BlockState var4, BlockHitResult var5) {
      if (var5 != null
         && (!this.gU.containsKey(var5.getBlockPos()) || this.gt.get() != EasyPlace.DependencySyncMode.ServerAck)
         && this.getPlacePos(var5).equals(var1)
         && this.vn(var5.getBlockPos(), var5.getPos())) {
         ItemStack var6 = this.mc.player.getStackInHand(var2);
         boolean var7 = var6.getItem() instanceof VerticallyAttachableBlockItem || !EasyPlace.LitematicaBridge.getPlacementSupportDirections(var1).isEmpty();
         float var8 = this.mc.player.getYaw();
         float var9 = this.mc.player.getPitch();
         if (var7) {
            float[] var10 = tj(this.mc.player.getEyePos(), var5.getPos());
            this.mc.player.setYaw(tF(var10[0], var8));
            this.mc.player.setPitch(var10[1]);
         }

         boolean var15 = false /* VF: Semaphore variable */;

         boolean var18;
         label119: {
            boolean var12;
            try {
               var15 = true;
               ItemPlacementContext var17 = new ItemPlacementContext(this.mc.player, var2, var6, var5);
               if (!var17.getBlockPos().equals(var1)) {
                  var18 = false;
                  var15 = false;
                  break label119;
               }

               BlockState var11 = this.getPlacementStateForHit(var6, var3, var17);
               var12 = var11 != null && EasyPlace.LitematicaBridge.rZ(var4, var11);
               var15 = false;
            } finally {
               if (var15) {
                  if (var7) {
                     this.mc.player.setYaw(var8);
                     this.mc.player.setPitch(var9);
                  }
               }
            }

            if (var7) {
               this.mc.player.setYaw(var8);
               this.mc.player.setPitch(var9);
            }

            return var12;
         }

         if (var7) {
            this.mc.player.setYaw(var8);
            this.mc.player.setPitch(var9);
         }

         return var18;
      } else {
         return false;
      }
   }

   private BlockState getPlacementStateForHit(ItemStack var1, Block var2, ItemPlacementContext var3) {
      if (var1 != null && !var1.isEmpty() && var2 != null && var3 != null) {
         BlockPos var4;
         BlockPos var5;
         boolean var10000;
         label113: {
            var4 = var3.getBlockPos();
            var5 = var4.offset(var3.getSide().getOpposite());
            char var22;
            if (this.gt.get() == EasyPlace.DependencySyncMode.Immediate) {
               if (this.gU.containsKey(var5)) {
                  var10000 = true;
                  break label113;
               }
            } else {
               while (var22 == '쥘') {
                  var22 = '쥖';
               }
            }

            var10000 = false;
         }

         boolean var6 = var10000;
         BlockState var7 = EasyPlace.LitematicaBridge.getState(var4);
         if (!(var1.getItem() instanceof VerticallyAttachableBlockItem)) {
            BlockState var17 = var2.getPlacementState(var3);
            return var17 == null && var6 && var7 != null && var7.getBlock() == var2 ? var7 : var17;
         } else {
            Block var8 = tf(var2);
            if (var7 != null && var7.getBlock() != var2 && var7.getBlock().asItem() == var1.getItem()) {
               var8 = var7.getBlock();
            }

            BlockState var9 = var2.getPlacementState(var3);
            BlockState var10 = var8 == null ? null : var8.getPlacementState(var3);
            if (var6 && var7 != null) {
               if (var7.getBlock() == var2 && var9 == null) {
                  var9 = var7;
               } else if (var7.getBlock() == var8 && var10 == null) {
                  var10 = var7;
               }
            }

            for (Direction var14 : var3.getPlacementDirections()) {
               if (var14 != Direction.DOWN) {
                  BlockState var15 = var14 != Direction.UP ? var10 : var9;
                  if (var15 != null && (var15.canPlaceAt(this.mc.world, var4) || var6 && var4.offset(var14.getOpposite()).equals(var5))) {
                     return var15;
                  }
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private static Block tf(Block var0) {
      if (var0 != Blocks.TORCH) {
         if (var0 == Blocks.SOUL_TORCH) {
            return Blocks.SOUL_WALL_TORCH;
         } else {
            return var0 != Blocks.REDSTONE_TORCH ? null : Blocks.REDSTONE_WALL_TORCH;
         }
      } else {
         return Blocks.WALL_TORCH;
      }
   }

   private void tg(Hand var1, BlockHitResult var2, BlockPos var3) {
      if (this.mc.player != null) {
         ItemStack var4 = this.mc.player.getStackInHand(var1);
         if (!var4.isEmpty()) {
            Item var5 = var4.getItem();
            if (this.vb(var3, var2, var5)) {
               this.ti(var1, var2, var3, false);
            } else {
               if (this.tX(var1, var2)) {
                  this.uX(var3, var5);
               }
            }
         }
      }
   }

   private BlockPos getPlacePos(BlockHitResult var1) {
      BlockPos var2 = var1.getBlockPos();
      BlockState var3 = this.mc.world.getBlockState(var2);
      return var3.isReplaceable() && !this.uY(var2) ? var2 : var2.offset(var1.getSide());
   }

   private void th(Hand var1, BlockHitResult var2, BlockPos var3, EasyPlace.RotationMode var4, Block var5, Orientation var6, boolean var7) {
      if (this.mc.player != null && var4.dk != null) {
         this.tl(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   private void ti(Hand var1, BlockHitResult var2, BlockPos var3, boolean var4) {
      if (this.mc.player != null) {
         ItemStack var5 = this.mc.player.getStackInHand(var1);
         Block var6 = var5.getItem() instanceof BlockItem var7 ? var7.getBlock() : null;
         this.tl(var1, var2, var3, null, var6, null, var4);
      }
   }

   private static float[] tj(Vec3d var0, Vec3d var1) {
      double var2 = var1.x - var0.x;
      double var4 = var1.y - var0.y;
      double var6 = var1.z - var0.z;
      double var8 = Math.sqrt(var2 * var2 + var6 * var6);
      float var10 = (float)Math.toDegrees(Math.atan2(var6, var2)) - 90.0F;
      float var11 = (float)(-Math.toDegrees(Math.atan2(var4, var8)));
      return new float[]{var10, var11};
   }

   private static boolean tk(Block var0) {
      return var0 != null && (var0.getDefaultState().contains(Properties.FACING) || var0.getDefaultState().contains(Properties.ORIENTATION));
   }

   private void tl(Hand var1, BlockHitResult var2, BlockPos var3, EasyPlace.RotationMode var4, Block var5, Orientation var6, boolean var7) {
      if (this.mc.player != null) {
         ItemStack var8 = this.mc.player.getStackInHand(var1);
         if (!var8.isEmpty()) {
            short var16;
            while (var16 == 20810) {
               var16 = 20808;
            }

            boolean var9 = this.vb(var3, var2, var8.getItem());
            int var10 = ++this.gp;
            this.HY = true;
            EasyPlace.PendingPlacement var11 = new EasyPlace.PendingPlacement(var10, var1, var2, var3, var8.getItem(), var7, var4, var5, var6, var9);
            if (var4 != null && this.tm(var2)) {
               var11.FP = this.mc.player.getYaw();
               var11.Bg = this.mc.player.getPitch();
               var11.Gv = true;
            }

            this.aD = var11;
            this.tn();
         }
      }
   }

   private boolean tm(BlockHitResult var1) {
      ClientPlayerEntity var2 = this.mc.player;
      return var2 != null && var1 != null
         ? tH(var2.getYaw(), var2.lastYaw) <= 5.0
            && Math.abs(var2.getPitch() - var2.lastPitch) <= 5.0
            && this.tz(var2.getEyePos(), var2.getYaw(), var2.getPitch(), var1.getBlockPos())
         : false;
   }

   private void tn() {
      ClientPlayerEntity var1 = this.mc.player;
      if (var1 != null) {
         int var10000 = this.Hs != null ? '꙱' : '꙲';

         while (true) {
            int var2 = var10000;
            if (var2 == 42609) {
               if (var1.input == this.Hs) {
                  return;
               }
               break;
            }

            if (var2 != 42610) {
               break;
            }

            var10000 = 42608;
         }

         this.xI = var1.input;
         this.Hs = new EasyPlace.PlacementInput(this.xI);
         var1.input = this.Hs;
      }
   }

   private void to() {
      if (this.Hs != null) {
         DelegatingInput.qM(this.mc.player, this.Hs);
      }

      this.Hs = null;
      this.xI = null;
   }

   private void tp(EasyPlace.PlacementInput var1, PlayerInput var2, Vec2f var3) {
      EasyPlace.PendingPlacement var4 = this.aD;
      ClientPlayerEntity var5 = this.mc.player;
      if (var4 != null && var5 != null) {
         char var12;
         if (!var4.FM) {
            if (!var4.sV && var4.xi == this.gp && this.HY && this.isActive()) {
               boolean var6 = false;
               EasyPlace.RotationPlan var7;
               if (var4.aZ == null) {
                  var7 = this.tq(var4.lX, var4.vL, var4.bJ, var4.dd != null, var2, var3);
               } else if (var4.Gv) {
                  var7 = this.tr(var4, var2, var3);
               } else {
                  EasyPlace.RotationPlan var8 = this.tr(var4, var2, var3);
                  if (var8 != null && this.tz(var5.getEyePos(), var8.yaw, var8.pitch, var4.lX.getBlockPos())) {
                     var4.FP = var8.yaw;
                     var4.Bg = var8.pitch;
                     var4.Gv = true;
                     var7 = var8;
                  } else {
                     var6 = true;
                     var7 = this.tq(var4.lX, var4.vL, var4.bJ, false, var2, var3);
                  }
               }

               if (var7 == null) {
                  return;
               }

               if (!var6 && var4.aZ != null && var4.Gv) {
                  var7 = new EasyPlace.RotationPlan(tF(var7.yaw, var4.FP), var7.pitch, var7.input, var7.movement);
               }

               var4.Iq = var5;
               var4.lF = var5.getYaw();
               var4.gz = var5.getPitch();
               var4.eM = var2;
               var4.yt = var3;
               var4.lc = var7.yaw;
               var4.AN = var7.pitch;
               var4.iF = var6;
               if (var6) {
                  var4.FP = var7.yaw;
                  var4.Bg = var7.pitch;
               }

               var4.sV = true;
               var1.setState(var7.input, var7.movement);
               var5.setYaw(var7.yaw);
               var5.setPitch(var7.pitch);
               return;
            }
         } else {
            while (var12 == 'ꊊ') {
               var12 = 'ꊈ';
            }
         }
      }
   }

   private EasyPlace.RotationPlan tq(BlockHitResult var1, boolean var2, boolean var3, boolean var4, PlayerInput var5, Vec2f var6) {
      ClientPlayerEntity var7 = this.mc.player;
      if (var7 == null) {
         return null;
      } else {
         float var8 = var7.getYaw();
         float var9 = var7.getPitch();
         Vec3d var10 = var7.getEyePos();
         Vec3d var11 = var2 ? var1.getPos() : Vec3d.ofCenter(var1.getBlockPos());
         float[] var12 = tj(var10, var11);
         var12[0] = tF(var12[0], var8);
         boolean var13 = var6 != null && var6.lengthSquared() > 1.0E-5F;
         if (var3) {
            return var13 && var4 ? null : new EasyPlace.RotationPlan(var12[0], var12[1], tE(var5), Vec2f.ZERO);
         } else {
            EasyPlace.RotationPlan var14 = null;
            double var15 = Double.MAX_VALUE;
            if (var13) {
               double var17 = Math.toDegrees(Math.atan2(var6.y, var6.x));

               for (EasyPlace.InputDirection var22 : EasyPlace.InputDirection.zu) {
                  float var23 = tF((float)(var8 + var17 - var22.rf()), var8);
                  float var24 = var12[1];
                  boolean var25 = var2 ? this.tA(var10, var23, var24, var1.getBlockPos()) : this.tz(var10, var23, var24, var1.getBlockPos());
                  if (var25) {
                     double var26 = tC(var7, var5, var22, var23, var24, var8, var12);
                     if (var26 < var15) {
                        var15 = var26;
                        var14 = new EasyPlace.RotationPlan(var23, var24, var22.rh(var5), var22.rg());
                     }
                  }
               }
            }

            if (var14 != null) {
               return var14;
            } else if (var13 && var4) {
               return null;
            } else {
               float[] var28 = tj(var10, var11);
               var28[0] = tF(var28[0], var8);
               return new EasyPlace.RotationPlan(var28[0], var28[1], tE(var5), Vec2f.ZERO);
            }
         }
      }
   }

   private EasyPlace.RotationPlan tr(EasyPlace.PendingPlacement var1, PlayerInput var2, Vec2f var3) {
      ClientPlayerEntity var4 = this.mc.player;
      if (var4 == null) {
         return null;
      } else {
         float var5 = var4.getYaw();
         float var6 = var4.getPitch();
         Vec3d var7 = var4.getEyePos();
         Vec3d var8 = Vec3d.ofCenter(var1.lX.getBlockPos());
         float[] var9 = tj(var7, var8);
         boolean var10 = var3 != null && var3.lengthSquared() > 1.0E-5F;
         EasyPlace.RotationPlan var11 = null;
         EasyPlace.RotationPlan var12 = null;
         double var13 = Double.MAX_VALUE;
         double var15 = Double.MAX_VALUE;
         if (var10) {
            double var17 = Math.toDegrees(Math.atan2(var3.y, var3.x));

            for (EasyPlace.InputDirection var22 : EasyPlace.InputDirection.zu) {
               float var23 = tF((float)(var5 + var17 - var22.rf()), var5);
               float var24 = this.tt(var1, var23, var9[1], var6);
               if (Float.isFinite(var24) && this.tu(var1, var23, var24)) {
                  PlayerInput var25 = var22.rh(var2);
                  Vec2f var26 = var22.rg();
                  boolean var27 = this.tz(var7, var23, var24, var1.lX.getBlockPos());
                  double var28 = tC(var4, var2, var22, var23, var24, var5, var9);
                  EasyPlace.RotationPlan var30 = new EasyPlace.RotationPlan(var23, var24, var25, var26);
                  if (var28 < var15) {
                     var15 = var28;
                     var12 = var30;
                  }

                  if (var27 && var28 < var13) {
                     var13 = var28;
                     var11 = var30;
                  }
               }
            }
         }

         if (var11 != null) {
            return var11;
         } else {
            float[] var32 = this.ts(var1, var7, var5, var6);
            if (var32 != null) {
               PlayerInput var33 = tE(var2);
               return new EasyPlace.RotationPlan(var32[0], var32[1], var33, Vec2f.ZERO);
            } else if (var12 != null) {
               return var12;
            } else {
               float var18;
               float var34;
               if (var1.Bz != null) {
                  var18 = tx(var1.Bz, var5);
                  var34 = ty(var1.Bz);
               } else {
                  short var38;
                  while (var38 == 409) {
                     var38 = 407;
                  }

                  var18 = tF(var1.aZ.ss(var5), var5);
                  var34 = this.tt(var1, var18, var9[1], var6);
               }

               if (!Float.isFinite(var34)) {
                  var34 = var1.aZ.st(var6);
               }

               return new EasyPlace.RotationPlan(var18, var34, tE(var2), Vec2f.ZERO);
            }
         }
      }
   }

   private float[] ts(EasyPlace.PendingPlacement var1, Vec3d var2, float var3, float var4) {
      BlockPos var5 = var1.lX.getBlockPos();
      double[] var6 = new double[]{0.1, 0.3, 0.5, 0.7, 0.9};
      float[] var7 = null;
      double var8 = Double.MAX_VALUE;
      Vec3d[] var10 = new Vec3d[]{var1.lX.getPos(), Vec3d.ofCenter(var5), vp(var5, var1.lX.getSide())};

      for (Vec3d var14 : var10) {
         float[] var15 = tj(var2, var14);
         var15[0] = tF(var15[0], var3);
         if (this.tu(var1, var15[0], var15[1])) {
            double var16 = tH(var15[0], var3) + Math.abs(var15[1] - var4);
            if (var16 < var8) {
               var8 = var16;
               var7 = var15;
            }
         }
      }

      for (double var33 : var6) {
         for (double var19 : var6) {
            for (double var24 : var6) {
               Vec3d var26 = new Vec3d(var5.getX() + var33, var5.getY() + var19, var5.getZ() + var24);
               float[] var27 = tj(var2, var26);
               var27[0] = tF(var27[0], var3);
               if (this.tu(var1, var27[0], var27[1])) {
                  double var28 = tH(var27[0], var3) + Math.abs(var27[1] - var4);
                  if (var28 < var8) {
                     var8 = var28;
                     var7 = var27;
                  }
               }
            }
         }
      }

      return var7;
   }

   private float tt(EasyPlace.PendingPlacement var1, float var2, float var3, float var4) {
      char var15;
      if (var1.aZ != null) {
         if (tk(var1.Jc)) {
            if (this.tu(var1, var2, var3)) {
               return var3;
            }

            Direction var5 = var1.aZ.dk.getOpposite();

            float var6 = switch (var5) {
               case UP -> -89.9F;
               case DOWN -> 89.9F;
               default -> 0.0F;
            };

            for (int var7 = 1; var7 <= 360; var7++) {
               float var8 = var7 / 360.0F;
               float var9 = var3 + (var6 - var3) * var8;
               if (this.tu(var1, var2, var9)) {
                  return var9;
               }
            }

            if (this.tu(var1, var2, var4)) {
               return var4;
            }

            return Float.NaN;
         }
      } else {
         while (var15 == '견') {
            var15 = '겪';
         }
      }

      return var3;
   }

   private boolean tu(EasyPlace.PendingPlacement var1, float var2, float var3) {
      return var1.Bz != null ? tw(var2, var3) == var1.Bz : tv(var1.aZ, var1.Jc, var2, var3);
   }

   private static boolean tv(EasyPlace.RotationMode var0, Block var1, float var2, float var3) {
      if (var0 != null && var0.dk != null) {
         Direction var4 = var0.dk.getOpposite();
         if (tk(var1)) {
            int var10000 = Direction.getFacing(Vec3d.fromPolar(var3, var2)) == var4 ? '쿵' : '쿶';

            while (true) {
               int var5 = var10000;
               if (var5 == 53237) {
                  var6 = true;
                  break;
               }

               if (var5 != 53238) {
                  var6 = false;
                  break;
               }

               var10000 = 53236;
            }

            return var6;
         } else {
            return var4.getAxis() != Axis.Y && Direction.fromHorizontalDegrees(var2) == var4;
         }
      } else {
         return true;
      }
   }

   private static Orientation tw(float var0, float var1) {
      Direction var2 = Direction.getFacing(Vec3d.fromPolar(var1, var0));
      Direction var3 = var2.getOpposite();
      Direction var4 = Direction.fromHorizontalDegrees(var0);
      int var6 = <unrepresentable>.Fx[var3.ordinal()];
      Direction var5 = var6 != 1 ? (var6 != 2 ? Direction.UP : var4.getOpposite()) : var4;
      return Orientation.byDirections(var3, var5);
   }

   private static float tx(Orientation var0, float var1) {
      Direction var2 = switch (var0.getFacing()) {
         case UP -> var0.getRotation();
         case DOWN -> var0.getRotation().getOpposite();
         default -> var0.getFacing().getOpposite();
      };
      int var4 = <unrepresentable>.Fx[var2.ordinal()];
      float var3 = var4 == 3 ? 180.0F : (var4 != 4 ? (var4 == 5 ? 90.0F : (var4 == 6 ? -90.0F : var1)) : 0.0F);
      return tF(var3, var1);
   }

   private static float ty(Orientation var0) {
      return switch (var0.getFacing().getOpposite()) {
         case UP -> -89.9F;
         case DOWN -> 89.9F;
         default -> 0.0F;
      };
   }

   private boolean tz(Vec3d var1, float var2, float var3, BlockPos var4) {
      if (this.mc.player == null) {
         return false;
      } else {
         double var5 = this.mc.player.getBlockInteractionRange();
         Vec3d var7 = var1.add(Vec3d.fromPolar(var3, var2).multiply(var5));
         return new Box(var4).raycast(var1, var7).isPresent();
      }
   }

   private boolean tA(Vec3d var1, float var2, float var3, BlockPos var4) {
      boolean var10000;
      if (this.tB(var1, var2, var3, var4) != null) {
         var10000 = true;
      } else {
         char var8;
         while (var8 == '\ue972') {
            var8 = '\ue970';
         }

         var10000 = false;
      }

      return var10000;
   }

   private BlockHitResult tB(Vec3d var1, float var2, float var3, BlockPos var4) {
      if (this.mc.player != null && this.mc.world != null) {
         double var5 = this.mc.player.getBlockInteractionRange();
         Vec3d var7 = var1.add(Vec3d.fromPolar(var3, var2).multiply(var5));
         BlockHitResult var8 = this.mc.world.raycast(new RaycastContext(var1, var7, ShapeType.OUTLINE, FluidHandling.NONE, this.mc.player));
         return var8.getType() == Type.BLOCK && var8.getBlockPos().equals(var4) ? var8 : null;
      } else {
         return null;
      }
   }

   private static double tC(ClientPlayerEntity var0, PlayerInput var1, EasyPlace.InputDirection var2, float var3, float var4, float var5, float[] var6) {
      double var7 = tH(var3, var6[0]) * 2.0 + Math.abs(var4 - var6[1]) + tH(var3, var5) * 0.05;
      if (tD(var1, var2)) {
         var7 -= 40.0;
      }

      if (var0.isSprinting()) {
         if (var2.forwardValue > 0.0F) {
            var7 -= 120.0;
         } else {
            var7 += 240.0;
         }
      }

      return var7;
   }

   private static boolean tD(PlayerInput var0, EasyPlace.InputDirection var1) {
      if (var0.forward() == var1.forward) {
         int var10000 = var0.backward() != var1.backward ? '꾹' : '꾸';

         while (true) {
            switch (var10000) {
               case 44984:
                  if (var0.left() == var1.left && var0.right() == var1.right) {
                     return true;
                  }

                  return false;
               case 44985:
                  var10000 = 44983;
                  break;
               default:
                  return false;
            }
         }
      } else {
         return false;
      }
   }

   private static PlayerInput tE(PlayerInput var0) {
      return new PlayerInput(false, false, false, false, var0.jump(), var0.sneak(), var0.sprint());
   }

   private static float tF(float var0, float var1) {
      return var1 + tG(var0 - var1);
   }

   private static float tG(float var0) {
      float var1 = var0 % 360.0F;
      if (var1 >= 180.0F) {
         var1 -= 360.0F;
      }

      if (var1 < -180.0F) {
         var1 += 360.0F;
      }

      return var1;
   }

   private static double tH(float var0, float var1) {
      return Math.abs(tG(var0 - var1));
   }

   private void tI(EasyPlace.PendingPlacement var1) {
      if (var1.sV) {
         if (this.mc.player == var1.Iq) {
            this.mc.player.setYaw(var1.lF);
            this.mc.player.setPitch(var1.gz);
         }

         if (this.Hs != null && this.mc.player != null) {
            int var10000 = this.mc.player.input != this.Hs ? '숗' : '숖';

            label30:
            while (true) {
               switch (var10000) {
                  case 49686:
                     if (var1.eM != null && var1.yt != null) {
                        this.Hs.setState(var1.eM, var1.yt);
                     }
                     break label30;
                  case 49687:
                     var10000 = 49685;
                     break;
                  default:
                     break label30;
               }
            }
         }

         var1.Iq = null;
         var1.eM = null;
         var1.yt = null;
         var1.sV = false;
      }
   }

   private void tJ(EasyPlace.PlacementInput var1, PlayerInput var2, Vec2f var3) {
      EasyPlace.PostPlacementRotation var4 = this.BU;
      ClientPlayerEntity var5 = this.mc.player;
      if (var4 != null && !var4.Bk && var5 != null) {
         EasyPlace.RotationPlan var6 = this.tq(var4.kJ, false, false, false, var2, var3);
         if (var6 == null) {
            this.BU = null;
         } else {
            var6 = new EasyPlace.RotationPlan(tF(var6.yaw, var4.vz), var6.pitch, var6.input, var6.movement);
            var4.ob = var5;
            var4.li = var5.getYaw();
            var4.sT = var5.getPitch();
            var4.zH = var2;
            var4.xv = var3;
            var4.bh = var6.yaw;
            var4.mS = var6.pitch;
            var4.Bk = true;
            var1.setState(var6.input, var6.movement);
            var5.setYaw(var6.yaw);
            var5.setPitch(var6.pitch);
         }
      }
   }

   private boolean tK(EasyPlace.PlacementInput var1, PlayerInput var2, Vec2f var3) {
      return this.tL(var1, var2, var3) ? true : this.tM(var1, var2, var3);
   }

   private boolean tL(EasyPlace.PlacementInput var1, PlayerInput var2, Vec2f var3) {
      EasyPlace.PendingInventoryTransfer var4 = this.Cg;
      ClientPlayerEntity var5 = this.mc.player;
      if (var4 != null && var4.oG == EasyPlace.InventoryTransferPhase.NeutralArmed && var5 != null && !var5.hasVehicle()) {
         int var10000 = this.HY ? 27926 : 27925;

         while (true) {
            int var8 = var10000;
            if (var8 == 27925) {
               if (this.aD == null && this.BU == null) {
                  PlayerInput var6 = var2 == null ? PlayerInput.DEFAULT : var2;
                  var4.vV = var5;
                  var4.hU = var6;
                  var4.aA = var3 == null ? Vec2f.ZERO : var3;
                  PlayerInput var7 = new PlayerInput(false, false, false, false, false, var6.sneak(), false);
                  var1.setState(var7, Vec2f.ZERO);
                  var5.setSprinting(false);
                  var4.oG = EasyPlace.InventoryTransferPhase.NeutralApplied;
                  return true;
               }
               break;
            }

            if (var8 != 27926) {
               break;
            }

            var10000 = 27924;
         }
      }

      return false;
   }

   private boolean tM(EasyPlace.PlacementInput var1, PlayerInput var2, Vec2f var3) {
      EasyPlace.PendingExternalInventoryWindow var4 = this.gL;
      ClientPlayerEntity var5 = this.mc.player;
      if (var4 != null
         && var4.dL == EasyPlace.ExternalInventoryWindowPhase.NeutralArmed
         && this.Cg == null
         && var5 != null
         && !var5.hasVehicle()
         && !this.HY
         && this.aD == null
         && this.BU == null) {
         PlayerInput var6 = var2 == null ? PlayerInput.DEFAULT : var2;
         var4.ki = var5;
         var4.gP = var6;
         var4.dz = var3 == null ? Vec2f.ZERO : var3;
         PlayerInput var7 = new PlayerInput(false, false, false, false, false, var6.sneak(), false);
         var1.setState(var7, Vec2f.ZERO);
         var5.setSprinting(false);
         var4.dL = EasyPlace.ExternalInventoryWindowPhase.NeutralApplied;
         return true;
      } else {
         return false;
      }
   }

   private void tN() {
      EasyPlace.PostPlacementRotation var1 = this.BU;
      if (var1 != null) {
         if (var1.Bk && this.mc.player == var1.ob) {
            this.mc.player.setYaw(var1.li);
            this.mc.player.setPitch(var1.sT);
         }

         if (var1.Bk && this.Hs != null && this.mc.player != null && this.mc.player.input == this.Hs && var1.zH != null && var1.xv != null) {
            this.Hs.setState(var1.zH, var1.xv);
         }

         EasyPlace.PendingPlacement var2 = this.aD;
         if (var1.Bk
            && var2 != null
            && var2.aZ != null
            && !var2.Gv
            && this.mc.player != null
            && this.tz(this.mc.player.getEyePos(), var1.bh, var1.mS, var2.lX.getBlockPos())) {
            var2.FP = var1.bh;
            var2.Bg = var1.mS;
            var2.Gv = true;
            var2.cB = 0;
         }

         this.BU = null;
      }
   }

   private void tO(int var1) {
      EasyPlace.PendingPlacement var2 = this.aD;
      if (var2 != null && var2.xi == var1 && var1 == this.gp && this.HY && this.isActive()) {
         var2.FM = true;
      }
   }

   private void tP() {
      EasyPlace.PendingPlacement var1 = this.aD;
      if (var1 != null) {
         if (var1.xi != this.gp || !this.HY || !this.isActive()) {
            this.tZ();
         } else if (!var1.FM) {
            var1.cB++;
            if (var1.cB > 2) {
               this.tZ();
            }
         } else {
            this.tZ();
            PlayerInput var2 = null;

            try {
               if (this.tV(var1)) {
                  if (var1.Dr) {
                     var2 = this.tQ();
                     if (var2 == null) {
                        return;
                     }
                  } else {
                     char var15;
                     while (var15 == '공') {
                        var15 = '곳';
                     }

                     if (var1.hZ && this.mc.player.isSneaking()) {
                        var2 = this.tR();
                        if (var2 == null) {
                           return;
                        }
                     }
                  }

                  BlockState var3 = var1.dd != null && this.mc.world != null ? this.mc.world.getBlockState(var1.dd) : null;
                  this.sN = false;
                  boolean var4 = this.tU(var1);
                  if (var1.dd != null) {
                     EasyPlace.PostPlacementAdjustment var5 = this.yV.get(var1.dd);
                     if (var5 != null) {
                        int var10000 = !this.sN ? 'ꎬ' : 'ꎫ';

                        while (true) {
                           switch (var10000) {
                              case 41899:
                                 var5.lw++;
                                 var5.Fy = true;
                                 var5.Dl = var3;
                                 return;
                              case 41900:
                                 var10000 = 41898;
                                 break;
                              default:
                                 return;
                           }
                        }
                     }
                  } else if (var4) {
                     this.uX(var1.gD, var1.fH);
                     if (var1.aZ != null && var1.Gv || var1.sd) {
                        this.BU = new EasyPlace.PostPlacementRotation(var1.lX, var1.aZ == null ? var1.lc : var1.FP);
                     }
                  }
               }
            } finally {
               this.tT(var2);
            }
         }
      }
   }

   private PlayerInput tQ() {
      return this.tS(true);
   }

   private PlayerInput tR() {
      return this.tS(false);
   }

   private PlayerInput tS(boolean var1) {
      ClientPlayerEntity var2 = this.mc.player;
      if (var2 != null && var2.networkHandler != null) {
         PlayerInput var3 = var2.getLastPlayerInput();
         if (var3 == null) {
            var3 = PlayerInput.DEFAULT;
         }

         PlayerInput var4 = new PlayerInput(var3.forward(), var3.backward(), var3.left(), var3.right(), var3.jump(), var1, var3.sprint());
         var2.networkHandler.sendPacket(new PlayerInputC2SPacket(var4));
         return var3;
      } else {
         return null;
      }
   }

   private void tT(PlayerInput var1) {
      if (var1 != null && this.mc.player != null && this.mc.player.networkHandler != null) {
         this.mc.player.networkHandler.sendPacket(new PlayerInputC2SPacket(var1));
      }
   }

   private boolean tU(EasyPlace.PendingPlacement var1) {
      ClientPlayerEntity var2 = this.mc.player;
      if (var2 == null) {
         return false;
      } else {
         float var3 = var2.getYaw();
         float var4 = var2.getPitch();
         boolean var5 = var2.isSneaking();
         PlayerInput var6 = var2.input == null ? null : var2.input.playerInput;
         var2.setYaw(var1.lc);
         var2.setPitch(var1.AN);
         if (var1.Dr && !var5) {
            var2.setSneaking(true);
         }

         if (var1.hZ && var5) {
            var2.setSneaking(false);
         }

         BlockHitResult var7 = var1.lX;
         if (var1.dd != null) {
            var7 = this.tW(var1);
            if (var7 == null) {
               boolean var14 = false;
               if (this.mc.player == var2) {
                  var2.setYaw(var3);
                  var2.setPitch(var4);
                  int var16 = var2.isSneaking() == var5 ? 23737 : 23736;

                  label81:
                  while (true) {
                     switch (var16) {
                        case 23736:
                           var2.setSneaking(var5);
                           break label81;
                        case 23737:
                           var16 = 23735;
                           break;
                        default:
                           break label81;
                     }
                  }

                  if (var6 != null && var2.input != null) {
                     var2.input.playerInput = var6;
                  }
               }

               return var14;
            }
         } else {
            ItemStack var8 = var2.getStackInHand(var1.ce);
            BlockState var9 = EasyPlace.LitematicaBridge.getState(var1.gD);
            if (this.tc(var8, var9, var1.gD)) {
               int var10000 = this.tb(var1.ce, var1.lX, var1.gD, var1.Jc) ? '辇' : '辆';

               label105:
               while (true) {
                  switch (var10000) {
                     case 36742:
                        boolean var10 = false;
                        char var19;
                        if (this.mc.player == var2) {
                           var2.setYaw(var3);
                           var2.setPitch(var4);
                           if (var2.isSneaking() != var5) {
                              var2.setSneaking(var5);
                           }

                           if (var6 != null && var2.input != null) {
                              var2.input.playerInput = var6;
                           }
                        } else {
                           while (var19 == '쿨') {
                              var19 = '쿦';
                           }
                        }

                        return var10;
                     case 36743:
                        var10000 = 36741;
                        break;
                     default:
                        break label105;
                  }
               }
            }
         }

         boolean var13 = this.tX(var1.ce, var7);
         if (this.mc.player == var2) {
            var2.setYaw(var3);
            var2.setPitch(var4);
            if (var2.isSneaking() != var5) {
               var2.setSneaking(var5);
            }

            if (var6 != null && var2.input != null) {
               var2.input.playerInput = var6;
            }
         }

         return var13;
      }
   }

   private boolean tV(EasyPlace.PendingPlacement var1) {
      if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
         if (this.mc.currentScreen == null) {
            if (!this.mc.player.isSpectator()) {
               if (this.mc.player.isAlive()) {
                  if (var1.dd != null) {
                     EasyPlace.PostPlacementAdjustment var8 = this.yV.get(var1.dd);
                     return var8 != null && !var8.Fy && this.uP(var1.dd, var8.lG) && this.tW(var1) != null;
                  }

                  ItemStack var2 = this.mc.player.getStackInHand(var1.ce);
                  if (!var2.isEmpty() && var2.getItem() == var1.fH && this.vq(var2)) {
                     if (this.vr(var2, var1.gD)) {
                        return false;
                     }

                     if (!this.uY(var1.gD) && this.mc.world.getBlockState(var1.gD).isReplaceable()) {
                        if (!this.getPlacePos(var1.lX).equals(var1.gD)) {
                           return false;
                        }

                        boolean var3 = var1.Jc instanceof HopperBlock
                           && (Boolean)this.yH.get()
                           && (Boolean)this.HB.get()
                           && !this.uD(var1.lX.getBlockPos(), var1.lX.getSide());
                        float var4 = var1.aZ == null ? var1.lc : var1.FP;
                        float var5 = var1.aZ == null ? var1.AN : var1.Bg;
                        boolean var6 = var1.vL
                           ? this.tA(this.mc.player.getEyePos(), var4, var5, var1.lX.getBlockPos())
                           : this.tz(this.mc.player.getEyePos(), var4, var5, var1.lX.getBlockPos());
                        if (!var3 && !var6) {
                           return false;
                        }

                        if (!var1.Hf) {
                           return this.vn(var1.lX.getBlockPos(), var1.lX.getPos());
                        }

                        if (var3) {
                           return true;
                        }

                        return this.vo(var1.lX.getPos()) && this.uD(var1.lX.getBlockPos(), var1.lX.getSide());
                     }

                     return false;
                  }

                  return false;
               }
            } else {
               char var7 = '鉎';
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private BlockHitResult tW(EasyPlace.PendingPlacement var1) {
      if (this.mc.player != null && var1 != null && var1.dd != null) {
         BlockHitResult var2 = this.tB(this.mc.player.getEyePos(), var1.lc, var1.AN, var1.dd);
         return var2 != null && this.vo(var2.getPos()) && this.uD(var2.getBlockPos(), var2.getSide()) ? var2 : null;
      } else {
         return null;
      }
   }

   private boolean tX(Hand var1, BlockHitResult var2) {
      if (this.mc.player == null || this.mc.world == null || this.mc.interactionManager == null || var1 == null || var2 == null) {
         return false;
      } else if (this.ja > 0) {
         return false;
      } else {
         this.xJ = true;

         boolean var4;
         try {
            this.sN = true;
            ActionResult var3 = this.mc.interactionManager.interactBlock(this.mc.player, var1, var2);
            this.ja = this.tY();
            if (var3.isAccepted()) {
               this.mc.player.swingHand(var1);
            }

            var4 = var3.isAccepted();
         } finally {
            this.xJ = false;
         }

         return var4;
      }
   }

   private int tY() {
      return !this.xZ.get() ? 4 : Math.max(1, (Integer)this.Dc.get());
   }

   private void tZ() {
      EasyPlace.PendingPlacement var1 = this.aD;
      if (var1 != null) {
         this.tI(var1);
      }

      this.HY = false;
      this.aD = null;
   }

   private EasyPlace.InventoryAcquireResult ua(Item var1, BlockPos var2) {
      if (this.mc.player == null) {
         return EasyPlace.InventoryAcquireResult.Unavailable;
      } else if (this.mc.player.getMainHandStack().isOf(var1)) {
         return EasyPlace.InventoryAcquireResult.Ready;
      } else if (this.Cg != null) {
         return EasyPlace.InventoryAcquireResult.Waiting;
      } else {
         FindItemResult var3 = InvUtils.find(new Item[]{var1});
         if (!var3.found()) {
            this.vB("khong tim thay block schematic can dat trong tui do");
            return EasyPlace.InventoryAcquireResult.Unavailable;
         } else if (var3.isHotbar()) {
            return InvUtils.swap(var3.slot(), false) ? EasyPlace.InventoryAcquireResult.Ready : EasyPlace.InventoryAcquireResult.Unavailable;
         } else if (var3.isMain() && var2 != null) {
            this.Cg = new EasyPlace.PendingInventoryTransfer(var1, var2.toImmutable());
            return EasyPlace.InventoryAcquireResult.Waiting;
         } else {
            return EasyPlace.InventoryAcquireResult.Unavailable;
         }
      }
   }

   private void ub() {
      EasyPlace.PendingInventoryTransfer var1 = this.Cg;
      if (var1 != null && this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
         var1.be++;
         if (var1.be > 40) {
            this.vB("het thoi gian cho server xac nhan chuyen item");
            this.ul();
         } else if (var1.oG == EasyPlace.InventoryTransferPhase.PickVerify) {
            if (var1.CT) {
               if (this.mc.player.getMainHandStack().isOf(var1.ck)) {
                  this.ul();
               } else {
                  this.tZ();
                  var1.oG = EasyPlace.InventoryTransferPhase.PickSettle;
                  var1.be = 0;
               }
            }
         } else {
            int var10000 = var1.oG != EasyPlace.InventoryTransferPhase.PickSettle ? 27251 : 27250;

            while (true) {
               int var4 = var10000;
               if (var4 == 27250) {
                  if (this.mc.player.getMainHandStack().isOf(var1.ck)) {
                     if (!var1.ea) {
                        this.ug(var1);
                     } else {
                        this.ul();
                     }

                     return;
                  }

                  if (var1.be < 2) {
                     return;
                  }

                  FindItemResult var7 = InvUtils.find(new Item[]{var1.ck});
                  if (!var7.found()) {
                     this.ul();
                  } else if (var7.isHotbar()) {
                     if (InvUtils.swap(var7.slot(), false)) {
                        this.ug(var1);
                     } else {
                        char var11;
                        while (var11 == '鈣') {
                           var11 = '鈡';
                        }

                        this.ul();
                     }
                  } else if (var7.isMain()) {
                     var1.oG = EasyPlace.InventoryTransferPhase.NeutralArmed;
                     var1.yC = true;
                     var1.CT = false;
                     var1.be = 0;
                  } else {
                     this.ul();
                  }

                  return;
               }

               if (var4 != 27251) {
                  if (!this.uc(var1)) {
                     this.ul();
                     return;
                  }

                  if (var1.oG == EasyPlace.InventoryTransferPhase.PickWait) {
                     if (var1.CT) {
                        if (this.mc.player.getMainHandStack().isOf(var1.ck)) {
                           this.ug(var1);
                        } else {
                           var1.oG = EasyPlace.InventoryTransferPhase.PickSettle;
                           var1.be = 0;
                        }

                        return;
                     }

                     return;
                  }

                  if (var1.oG != EasyPlace.InventoryTransferPhase.NeutralArmed && var1.oG != EasyPlace.InventoryTransferPhase.NeutralApplied) {
                     if (var1.oG == EasyPlace.InventoryTransferPhase.NeutralReady) {
                        if (!var1.xq) {
                           var1.oG = EasyPlace.InventoryTransferPhase.NeutralArmed;
                           return;
                        }

                        if (!this.uf(var1.ck)) {
                           this.ul();
                        } else {
                           this.ug(var1);
                        }

                        return;
                     }

                     char var6 = '肠';
                     if (!this.mc.player.getMainHandStack().isOf(var1.ck)) {
                        FindItemResult var2 = InvUtils.find(new Item[]{var1.ck});
                        if (!var2.found()) {
                           this.ul();
                           return;
                        }

                        if (!var2.isHotbar()) {
                           if (!var2.isMain()) {
                              this.ul();
                              return;
                           }

                           if (this.ur()) {
                              if (this.uf(var1.ck)) {
                                 this.ug(var1);
                              } else {
                                 this.ul();
                              }

                              return;
                           }

                           EasyPlace.InventoryPickPlan var3 = var1.yC ? null : this.ud(var1.ck);
                           if (var3 == null) {
                              var1.oG = EasyPlace.InventoryTransferPhase.NeutralArmed;
                              var1.be = 0;
                              return;
                           }

                           var1.CT = false;
                           this.mc.player.networkHandler.sendPacket(new PickItemFromBlockC2SPacket(var3.pos, false));
                           if (this.DI.get() == EasyPlace.InventoryPickSyncMode.SameTick && this.ue(var3, var1.ck)) {
                              var1.oG = EasyPlace.InventoryTransferPhase.PickVerify;
                              var1.be = 0;
                              var1.ea = true;
                              this.uh(var1);
                           } else {
                              var1.oG = EasyPlace.InventoryTransferPhase.PickWait;
                              var1.be = 0;
                           }

                           return;
                        }

                        if (InvUtils.swap(var2.slot(), false)) {
                           this.ug(var1);
                        } else {
                           this.ul();
                        }

                        return;
                     }

                     this.ug(var1);
                     return;
                  }

                  return;
               }

               var10000 = 27249;
            }
         }
      }
   }

   private boolean uc(EasyPlace.PendingInventoryTransfer var1) {
      if (this.mc.currentScreen == null && InvUtils.find(new Item[]{var1.ck}).found() && EasyPlace.LitematicaBridge.sc(this.mc, var1.oK, this::uY)) {
         double var2 = this.mc.player.getBlockInteractionRange() + 1.0;
         return new Box(var1.oK).squaredMagnitude(this.mc.player.getEyePos()) < var2 * var2;
      } else {
         return false;
      }
   }

   private EasyPlace.InventoryPickPlan ud(Item var1) {
      if (this.mc.player != null && this.mc.world != null) {
         double var2 = this.mc.player.getBlockInteractionRange() + 1.0;
         double var4 = var2 * var2;
         Vec3d var6 = this.mc.player.getEyePos();
         Vec3d var7 = this.mc.player.getEntityPos();
         BlockPos var8 = BlockPos.ofFloored(var6);
         int var9 = (int)Math.ceil(var2);
         EasyPlace.InventoryPickPlan var10 = null;
         double var11 = Double.MAX_VALUE;

         for (BlockPos var14 : BlockPos.iterate(var8.add(-var9, -var9, -var9), var8.add(var9, var9, var9))) {
            if (this.mc.world.isPosLoaded(var14) && !this.gU.containsKey(var14)) {
               Box var15 = new Box(var14);
               double var16 = var15.squaredMagnitude(var6);
               double var18 = Vec3d.ofCenter(var14).squaredDistanceTo(var7);
               if (!(var16 >= var4)
                  && !(var18 > 30.25)
                  && !(var16 >= var11)
                  && (this.fw.get() != EasyPlace.InventoryPickSourceMode.VisibleOnly || this.uN(var14) != null)) {
                  BlockState var20 = this.mc.world.getBlockState(var14);
                  ItemStack var21 = var20.getPickStack(this.mc.world, var14, false);
                  if (!var21.isEmpty() && var21.isOf(var1)) {
                     int var22 = this.mc.player.getInventory().getSlotWithStack(var21);
                     if (var22 >= 9 && var22 < 36) {
                        var10 = new EasyPlace.InventoryPickPlan(var14.toImmutable(), var21.copy(), var22);
                        var11 = var16;
                     }
                  }
               }
            }
         }

         return var10;
      } else {
         return null;
      }
   }

   private boolean ue(EasyPlace.InventoryPickPlan var1, Item var2) {
      int var10000 = this.mc.player != null ? '럽' : '럾';

      while (true) {
         int var6 = var10000;
         if (var6 == 47101) {
            var10000 = !GrimCombatCoordinator.xf() ? '訢' : '訡';

            while (true) {
               int var7 = var10000;
               if (var7 == 35361) {
                  return false;
               }

               if (var7 != 35362) {
                  int var3 = this.mc.player.getInventory().getSlotWithStack(var1.pickStack);
                  int var4 = this.mc.player.getInventory().getSwappableHotbarSlot();
                  int var5 = this.getTranslatedPickHotbarSlot();
                  if (var3 == var1.sourceSlot && var3 >= 9 && var3 < 36 && var4 >= 0 && var4 <= 8 && var4 == var5) {
                     this.mc.player.getInventory().swapSlotWithHotbar(var3);
                     return this.mc.player.getMainHandStack().isOf(var2);
                  }

                  return false;
               }

               var10000 = 35360;
            }
         }

         if (var6 != 47102) {
            return false;
         }

         var10000 = 47100;
      }
   }

   private int getTranslatedPickHotbarSlot() {
      if (this.mc.player == null) {
         return -1;
      } else {
         for (int var1 = 0; var1 < 9; var1++) {
            if (this.mc.player.getInventory().getStack(var1).isEmpty()) {
               return var1;
            }
         }

         return this.mc.player.getInventory().getSelectedSlot();
      }
   }

   private boolean uf(Item var1) {
      if (this.mc.player != null && this.mc.interactionManager != null && !GrimCombatCoordinator.xf()) {
         FindItemResult var2 = InvUtils.find(new Item[]{var1});
         if (var2.found() && var2.isMain()) {
            int var3 = this.mc.player.getInventory().getSelectedSlot();
            int var4 = SlotUtils.indexToId(var2.slot());
            if (var3 >= 0 && var3 <= 8 && var4 >= 0) {
               this.mc.interactionManager.clickSlot(this.mc.player.currentScreenHandler.syncId, var4, var3, SlotActionType.SWAP, this.mc.player);
               return this.mc.player.getMainHandStack().isOf(var1);
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

   private void ug(EasyPlace.PendingInventoryTransfer var1) {
      if (this.Cg == var1) {
         this.ul();
         this.uh(var1);
      }
   }

   private void uh(EasyPlace.PendingInventoryTransfer var1) {
      if (this.mc.player != null && this.mc.world != null && this.mc.player.getMainHandStack().isOf(var1.ck)) {
         this.Ia = true;
         this.sI(Hand.MAIN_HAND, var1.oK);
      }
   }

   private void ui() {
      this.uj();
      this.uk();
   }

   private void uj() {
      EasyPlace.PendingInventoryTransfer var1 = this.Cg;
      if (var1 != null && var1.oG == EasyPlace.InventoryTransferPhase.NeutralApplied) {
         if (this.Hs != null && this.mc.player == var1.vV && this.mc.player.input == this.Hs && var1.hU != null && var1.aA != null) {
            this.Hs.setState(var1.hU, var1.aA);
         }

         var1.vV = null;
         var1.hU = null;
         var1.aA = null;
         var1.xq = this.Ga;
         var1.oG = EasyPlace.InventoryTransferPhase.NeutralReady;
      }
   }

   private void uk() {
      EasyPlace.PendingExternalInventoryWindow var1 = this.gL;
      if (var1 != null && var1.dL == EasyPlace.ExternalInventoryWindowPhase.NeutralApplied) {
         if (this.Hs != null && this.mc.player == var1.ki && this.mc.player.input == this.Hs && var1.gP != null && var1.dz != null) {
            this.Hs.setState(var1.gP, var1.dz);
         }

         var1.ki = null;
         var1.gP = null;
         var1.dz = null;
         var1.dL = !this.Ga ? EasyPlace.ExternalInventoryWindowPhase.NeutralArmed : EasyPlace.ExternalInventoryWindowPhase.NeutralReady;
         var1.zJ = 0;
      }
   }

   private void ul() {
      EasyPlace.PendingInventoryTransfer var1 = this.Cg;
      if (var1 != null && this.Hs != null && this.mc.player == var1.vV && this.mc.player.input == this.Hs && var1.hU != null && var1.aA != null) {
         this.Hs.setState(var1.hU, var1.aA);
      }

      this.Cg = null;
   }

   static EasyPlace.ExternalInventoryWindowState um(Object var0) {
      EasyPlace var1 = uZ;
      if (var0 != null && var1 != null) {
         int var10000 = var1.isActive() ? '\ufb1a' : '\ufb19';

         while (true) {
            switch (var10000) {
               case 64281:
                  return EasyPlace.ExternalInventoryWindowState.Unavailable;
               case 64282:
                  var10000 = 64280;
                  break;
               default:
                  return var1.uo(var0);
            }
         }
      } else {
         return EasyPlace.ExternalInventoryWindowState.Unavailable;
      }
   }

   static void un(Object var0) {
      EasyPlace var1 = uZ;
      if (var0 != null && var1 != null) {
         EasyPlace.PendingExternalInventoryWindow var2 = var1.gL;
         if (var2 != null && var2.ay == var0) {
            var1.uq();
         }
      }
   }

   private EasyPlace.ExternalInventoryWindowState uo(Object var1) {
      if (GrimCombatCoordinator.xf()) {
         this.uq();
         return EasyPlace.ExternalInventoryWindowState.Waiting;
      } else {
         boolean var2 = this.Cg != null || this.Ia;
         EasyPlace.PendingExternalInventoryWindow var3 = this.gL;
         if (var3 != null) {
            if (var3.ay != var1) {
               return EasyPlace.ExternalInventoryWindowState.Waiting;
            } else {
               var3.zJ = 0;
               if (var3.dL == EasyPlace.ExternalInventoryWindowPhase.NeutralReady && var2) {
                  var3.dL = EasyPlace.ExternalInventoryWindowPhase.NeutralArmed;
               }

               return var3.dL == EasyPlace.ExternalInventoryWindowPhase.NeutralReady
                  ? EasyPlace.ExternalInventoryWindowState.Ready
                  : EasyPlace.ExternalInventoryWindowState.Waiting;
            }
         } else {
            short var4 = 12021;
            var3 = new EasyPlace.PendingExternalInventoryWindow(var1);
            this.gL = var3;
            return EasyPlace.ExternalInventoryWindowState.Waiting;
         }
      }
   }

   private void up() {
      EasyPlace.PendingExternalInventoryWindow var1 = this.gL;
      if (var1 != null) {
         var1.zJ++;
         if (var1.zJ > 40) {
            this.uq();
         }
      }
   }

   private void uq() {
      EasyPlace.PendingExternalInventoryWindow var1 = this.gL;
      if (var1 != null && this.Hs != null && this.mc.player == var1.ki && this.mc.player.input == this.Hs && var1.gP != null && var1.dz != null) {
         this.Hs.setState(var1.gP, var1.dz);
      }

      this.gL = null;
   }

   private boolean ur() {
      return this.Ga && this.us();
   }

   private boolean us() {
      ClientPlayerEntity var1 = this.mc.player;
      if (var1 == null) {
         return false;
      } else if (var1.isSprinting()) {
         return false;
      } else {
         Input var2 = var1.input;
         if (var2 == null) {
            return true;
         } else {
            Vec2f var3 = var2.getMovementInput();
            if (var3 != null && var3.lengthSquared() > 1.0E-5F) {
               return false;
            } else {
               PlayerInput var4 = var2.playerInput;
               return var4 == null || !var4.forward() && !var4.backward() && !var4.left() && !var4.right();
            }
         }
      }
   }

   private Direction getWantedHopperFacing(BlockPos var1) {
      if ((Boolean)this.yH.get() && ((Boolean)this.CD.get() || (Boolean)this.gw.get())) {
         Direction var2 = EasyPlace.LitematicaBridge.getHopperFacing(var1);
         if (var2 != null) {
            return var2;
         }
      }

      EasyPlace.RotationMode var3 = (EasyPlace.RotationMode)this.rR.get();
      return var3 != null && var3 != EasyPlace.RotationMode.Auto ? var3.dk : null;
   }

   private BlockHitResult ut(BlockPos var1, BlockHitResult var2, EasyPlace.RotationMode var3, Block var4) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.uu(var2.getBlockPos(), var3, var4)) {
            return var2;
         } else {
            Vec3d var5 = this.mc.player.getEyePos();
            BlockHitResult var6 = null;
            double var7 = Double.MAX_VALUE;

            for (Direction var12 : Direction.values()) {
               BlockHitResult var13 = this.vk(var1, var12);
               if (var13 != null && this.uu(var13.getBlockPos(), var3, var4)) {
                  double var14 = var5.squaredDistanceTo(var13.getPos());
                  if (var14 < var7) {
                     var7 = var14;
                     var6 = var13;
                  }
               }
            }

            return var6;
         }
      } else {
         return var2;
      }
   }

   private boolean uu(BlockPos var1, EasyPlace.RotationMode var2, Block var3) {
      if (this.mc.player == null) {
         return false;
      } else {
         Vec3d var4 = this.mc.player.getEyePos();
         double[] var5 = new double[]{0.15, 0.5, 0.85};

         for (double var9 : var5) {
            for (double var14 : var5) {
               for (double var19 : var5) {
                  float[] var21 = tj(var4, new Vec3d(var1.getX() + var9, var1.getY() + var14, var1.getZ() + var19));
                  if (tv(var2, var3, var21[0], var21[1])) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private BlockHitResult uv(BlockPos var1) {
      Direction var2 = EasyPlace.LitematicaBridge.getHopperFacing(var1);
      return this.vj(var1, var2, false);
   }

   private BlockHitResult uw(BlockPos var1) {
      Direction var2 = EasyPlace.LitematicaBridge.getHopperFacing(var1);
      return this.ux(var1, var2, false);
   }

   private BlockHitResult ux(BlockPos var1, Direction var2, boolean var3) {
      return this.uB(var1, var2, var3, false, false, false);
   }

   private BlockHitResult uy(BlockPos var1, Direction var2, boolean var3) {
      BlockHitResult var4 = this.uC(var1, var2);
      if (var4 == null && var3) {
         var4 = this.vl(var1, var2);
      }

      return var4;
   }

   private BlockHitResult uz(BlockPos var1, List<Direction> var2) {
      if (this.mc.player != null && var1 != null && var2 != null && !var2.isEmpty()) {
         Vec3d var3 = this.mc.player.getEyePos();
         BlockHitResult var4 = null;
         double var5 = Double.MAX_VALUE;

         for (Direction var8 : var2) {
            BlockHitResult var9 = this.uC(var1, var8);
            if (var9 != null) {
               double var10 = var3.squaredDistanceTo(var9.getPos());
               if (var10 < var5) {
                  var5 = var10;
                  var4 = var9;
               }
            }
         }

         return var4;
      } else {
         return null;
      }
   }

   private BlockHitResult uA(BlockPos var1, ChestType var2, Block var3) {
      if (var2 == null || var3 == null) {
         return null;
      } else if (var2 == ChestType.SINGLE) {
         return this.uB(var1, null, false, true, true, true);
      } else {
         Direction var4 = EasyPlace.LitematicaBridge.getChestPartnerDirection(var1);
         if (var4 == null) {
            return null;
         } else {
            BlockPos var5 = var1.offset(var4);
            return this.ve(var5, var3) ? this.uC(var1, var4) : this.uB(var1, null, false, true, true, true);
         }
      }
   }

   private BlockHitResult uB(BlockPos var1, Direction var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      if (this.mc.player != null && this.mc.world != null) {
         if (var2 != null) {
            BlockHitResult var7 = this.uC(var1, var2);
            if (var7 != null || var3) {
               return var7;
            }
         }

         Vec3d var23 = this.mc.player.getEyePos();
         float var8 = this.mc.player.getYaw();
         float var9 = this.mc.player.getPitch();
         BlockHitResult var10 = null;
         double var11 = Double.MAX_VALUE;

         for (Direction var16 : Direction.values()) {
            BlockHitResult var17 = this.uC(var1, var16);
            if (var17 != null && (!var6 || !this.vf(var17.getBlockPos()))) {
               double var18 = var23.squaredDistanceTo(var17.getPos());
               boolean var20 = this.tz(var23, var8, var9, var17.getBlockPos());
               double var21 = var18 - (!var20 ? 0.0 : 1000.0);
               if (var4 && this.va(var17.getBlockPos())) {
                  var21 += 4000.0;
               }

               if (var5 && var16.getAxis() != Axis.Y) {
                  var21 += 2000.0;
               }

               if (var21 < var11) {
                  var11 = var21;
                  var10 = var17;
               }
            }
         }

         return var10;
      } else {
         return null;
      }
   }

   private BlockHitResult uC(BlockPos var1, Direction var2) {
      if (this.mc.world != null && var2 != null) {
         BlockPos var3 = var1.offset(var2);
         if (!this.uG(var3)) {
            return null;
         } else {
            Direction var4 = var2.getOpposite();
            if (!this.uD(var3, var4)) {
               return null;
            } else {
               Vec3d var5 = this.mc.player.getEyePos();
               Vec3d var6 = null;
               double var7 = Double.MAX_VALUE;

               for (Vec3d var12 : this.vm(var3, var4)) {
                  if (this.vn(var3, var12)) {
                     double var13 = var5.squaredDistanceTo(var12);
                     if (var13 < var7) {
                        var7 = var13;
                        var6 = var12;
                     }
                  }
               }

               return var6 == null ? null : new BlockHitResult(var6, var4, var3, false);
            }
         }
      } else {
         return null;
      }
   }

   private boolean uD(BlockPos var1, Direction var2) {
      if (this.mc.player != null && this.mc.world != null) {
         short var19 = 5775;
         Vec3d var3 = this.mc.player.getEyePos();
         Box var4;
         if (this.uY(var1)) {
            var4 = new Box(var1);
         } else {
            BlockState var5 = this.mc.world.getBlockState(var1);
            VoxelShape var6 = var5.getCollisionShape(this.mc.world, var1);
            if (var6.isEmpty()) {
               var6 = var5.getOutlineShape(this.mc.world, var1);
            }

            var4 = !var6.isEmpty() ? var6.getBoundingBox().offset(var1) : new Box(var1);
         }

         double var21 = var4.minX;
         double var7 = var4.minY;
         double var9 = var4.minZ;
         double var11 = var4.maxX;
         double var13 = var4.maxY;
         double var15 = var4.maxZ;
         double var17 = 1.0E-4;
         if (var3.x >= var21 && var3.x <= var11 && var3.y >= var7 && var3.y <= var13 && var3.z >= var9 && var3.z <= var15) {
            return true;
         } else {
            int var20 = <unrepresentable>.Fx[var2.ordinal()] ^ 1235888769;
            boolean var10000;
            if (var20 != 1235888768) {
               if (var20 == 1235888770) {
                  var10000 = var3.z <= var9 + var17;
               } else if (var20 != 1235888771) {
                  if (var20 == 1235888772) {
                     var10000 = var3.x <= var21 + var17;
                  } else if (var20 != 1235888773) {
                     if (var20 != 1235888775) {
                        throw new MatchException(null, null);
                     }

                     var10000 = var3.x >= var11 - var17;
                  } else {
                     var10000 = var3.z >= var15 - var17;
                  }
               } else {
                  var10000 = var3.y <= var7 + var17;
               }
            } else {
               var10000 = var3.y >= var13 - var17;
            }

            return var10000;
         }
      } else {
         return false;
      }
   }

   private boolean uE(BlockPos var1) {
      if (this.mc.world == null) {
         return false;
      } else {
         for (Direction var5 : Direction.values()) {
            BlockPos var6 = var1.offset(var5);
            if (this.uG(var6)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean uF(BlockPos var1) {
      if (var1 != null && this.mc.world != null && this.mc.world.isPosLoaded(var1) && !this.gU.containsKey(var1)) {
         BlockState var2 = this.mc.world.getBlockState(var1);
         return !var2.isAir() && !var2.isReplaceable();
      } else {
         return false;
      }
   }

   private boolean uG(BlockPos var1) {
      if (this.uF(var1)) {
         return true;
      } else if (this.gt.get() == EasyPlace.DependencySyncMode.Immediate && var1 != null) {
         EasyPlace.PredictedPlacement var2 = this.gU.get(var1);
         return var2 != null && var2.block != null && var2.block != Blocks.AIR;
      } else {
         return false;
      }
   }

   private void uH(BlockPos var1, String var2) {
      if (this.Ds <= 0 && var1 != null) {
         String var3 = var1.toShortString() + var2;
         this.info(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Fail place: ">(var3), new Object[0]);
         this.vB(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","khong dat duoc ">(var3));
         this.Ds = 20;
      }
   }

   private void uI(BlockPos var1) {
      if (this.eh <= 0) {
         this.info(
            StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Fail place: no server confirmation at ">(var1.toShortString()),
            new Object[0]
         );
         this.eh = 100;
      }
   }

   private boolean uJ() {
      EasyPlace.PendingPlacement var1 = this.aD;
      return var1 != null && var1.dd != null;
   }

   private boolean uK(BlockPos var1) {
      if (var1 == null) {
         return false;
      } else {
         EasyPlace.PendingInventoryTransfer var2 = this.Cg;
         if (var2 != null && var1.equals(var2.oK)) {
            return true;
         } else {
            EasyPlace.PendingDependencyPlacement var3 = this.if;
            if (var3 == null || !var1.equals(var3.Fd) && !var1.equals(var3.fs)) {
               EasyPlace.PendingPlacement var4 = this.aD;
               return var4 == null || !var1.equals(var4.gD) && (var4.lX == null || !var1.equals(var4.lX.getBlockPos())) ? this.yV.containsKey(var1) : true;
            } else {
               return true;
            }
         }
      }
   }

   private void uL() {
      if (!this.yV.isEmpty()
         && this.mc.player != null
         && this.mc.world != null
         && this.mc.interactionManager != null
         && this.mc.currentScreen == null
         && !this.HY
         && this.aD == null
         && this.Cg == null
         && !this.Ia
         && !this.xJ
         && this.ja <= 0) {
         EasyPlace.PostPlacementAdjustment var1 = null;
         double var2 = Double.MAX_VALUE;

         for (EasyPlace.PostPlacementAdjustment var5 : this.yV.values()) {
            if (var5.Fy) {
               return;
            }

            if (var5.lw >= var5.wQ) {
               this.yV.remove(var5.yb, var5);
               this.vB(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","het luot chinh ">(var5.yb.toShortString()));
            } else if (!this.uP(var5.yb, var5.lG)) {
               this.yV.remove(var5.yb, var5);
            } else {
               double var6 = this.mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter(var5.yb));
               if (var6 < var2) {
                  var2 = var6;
                  var1 = var5;
               }
            }
         }

         if (var1 != null) {
            BlockHitResult var9 = this.uN(var1.yb);
            if (var9 != null) {
               char var15;
               while (var15 == '탺') {
                  var15 = '탸';
               }

               Hand var10 = this.uM(var1.yb);
               if (var10 == null) {
                  this.vB(
                     StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","can mot tay trong de chinh ">(
                        var1.yb.toShortString()
                     )
                  );
               } else {
                  int var11 = ++this.gp;
                  this.HY = true;
                  this.aD = new EasyPlace.PendingPlacement(var11, var10, var9, var1.yb);
                  this.tn();
               }
            }
         }
      }
   }

   private Hand uM(BlockPos var1) {
      Block var2 = this.mc.world.getBlockState(var1).getBlock();
      ItemStack var3 = this.mc.player.getMainHandStack();
      ItemStack var4 = this.mc.player.getOffHandStack();
      if (vu(var3.getItem()) == var2) {
         return Hand.MAIN_HAND;
      } else if (vu(var4.getItem()) == var2) {
         return Hand.OFF_HAND;
      } else {
         int var10000 = !var3.isEmpty() ? '\ue835' : '\ue834';

         while (true) {
            switch (var10000) {
               case 59444:
                  return Hand.MAIN_HAND;
               case 59445:
                  var10000 = 59443;
                  break;
               default:
                  if (var4.isEmpty()) {
                     return Hand.OFF_HAND;
                  }

                  return null;
            }
         }
      }
   }

   private BlockHitResult uN(BlockPos var1) {
      if (this.mc.player != null && this.mc.world != null && var1 != null) {
         BlockState var2 = this.mc.world.getBlockState(var1);
         VoxelShape var3 = var2.getOutlineShape(this.mc.world, var1);
         if (!var3.isEmpty()) {
            Box var4 = var3.getBoundingBox().offset(var1);
            Vec3d var5 = this.mc.player.getEyePos();
            BlockHitResult var6 = null;
            double var7 = Double.MAX_VALUE;

            for (Direction var12 : Direction.values()) {
               if (this.uD(var1, var12)) {
                  Vec3d var13 = uO(var4, var12);
                  if (this.vn(var1, var13)) {
                     double var14 = var5.squaredDistanceTo(var13);
                     if (var14 < var7) {
                        var7 = var14;
                        var6 = new BlockHitResult(var13, var12, var1, false);
                     }
                  }
               }
            }

            return var6;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static Vec3d uO(Box var0, Direction var1) {
      double var2 = (var0.minX + var0.maxX) * 0.5;
      double var4 = (var0.minY + var0.maxY) * 0.5;
      double var6 = (var0.minZ + var0.maxZ) * 0.5;
      int var8 = <unrepresentable>.Fx[var1.ordinal()] ^ -1969140471;
      Vec3d var10000;
      if (var8 == -1969140472) {
         var10000 = new Vec3d(var2, var0.maxY, var6);
      } else if (var8 == -1969140470) {
         var10000 = new Vec3d(var2, var4, var0.minZ);
      } else if (var8 == -1969140469) {
         var10000 = new Vec3d(var2, var0.minY, var6);
      } else if (var8 != -1969140468) {
         if (var8 == -1969140467) {
            var10000 = new Vec3d(var2, var4, var0.maxZ);
         } else {
            if (var8 != -1969140465) {
               throw new MatchException(null, null);
            }

            var10000 = new Vec3d(var0.maxX, var4, var6);
         }
      } else {
         var10000 = new Vec3d(var0.minX, var4, var6);
      }

      return var10000;
   }

   private boolean uP(BlockPos var1, BlockState var2) {
      if (this.mc.world != null && var1 != null && var2 != null) {
         BlockState var3 = this.mc.world.getBlockState(var1);
         return uV(var3, var2) > 0;
      } else {
         return false;
      }
   }

   private static int uQ(int var0, int var1) {
      return Math.floorMod(var1 - var0, 4);
   }

   private static int uR(BlockState var0, BlockState var1, BooleanProperty var2) {
      if (var0.contains(var2) && var1.contains(var2)) {
         int var10000 = !((Boolean)var0.get(var2)).equals(var1.get(var2)) ? '\uf540' : '\uf53f';

         while (true) {
            switch (var10000) {
               case 62783:
                  return 0;
               case 62784:
                  var10000 = 62782;
                  break;
               default:
                  return 1;
            }
         }
      } else {
         return 0;
      }
   }

   private static int uS(BlockState var0, BlockState var1, BooleanProperty var2, BooleanProperty var3) {
      return var0.contains(var3) && var0.get(var3) ? 0 : uR(var0, var1, var2);
   }

   private static int uT(BlockState var0, BlockState var1, IntProperty var2) {
      if (var0.contains(var2)) {
         if (var1.contains(var2)) {
            char var5 = '\ue924';
            int var3 = var2.getValues().size();
            if (var3 <= 0) {
               return 0;
            }

            return Math.floorMod((Integer)var1.get(var2) - (Integer)var0.get(var2), var3);
         }
      } else {
         char var4 = '锈';
      }

      return 0;
   }

   private void uU(BlockPos var1, BlockState var2) {
      if (var1 != null && var2 != null) {
         if (!this.uP(var1, var2)) {
            this.yV.remove(var1);
         } else {
            int var3 = uV(this.mc.world.getBlockState(var1), var2);
            this.yV.compute(var1.toImmutable(), (var3x, var4) -> {
               if (var4 != null && var4.lG.equals(var2)) {
                  if (var4.Fy && var4.Dl != null && uV(var4.Dl, var2) == var3) {
                     return (EasyPlace.PostPlacementAdjustment)var4;
                  } else {
                     var4.Fy = false;
                     var4.Dl = null;
                     var4.wQ = Math.min(Math.max(var4.wQ, var4.lw + var3), var4.yX);
                     if (var4.lw >= var4.wQ) {
                        this.vB(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","het luot chinh ">(var3x.toShortString()));
                        return null;
                     } else {
                        return (EasyPlace.PostPlacementAdjustment)var4;
                     }
                  }
               } else {
                  return new EasyPlace.PostPlacementAdjustment(var3x, var2, var3, 80);
               }
            });
         }
      }
   }

   private static int uV(BlockState var0, BlockState var1) {
      if (var0 != null && var1 != null) {
         if (var0.getBlock() == var1.getBlock()) {
            Block var2 = var0.getBlock();
            if (var2 instanceof RepeaterBlock) {
               return uQ((Integer)var0.get(RepeaterBlock.DELAY), (Integer)var1.get(RepeaterBlock.DELAY));
            } else if (var2 instanceof ComparatorBlock) {
               return var0.get(ComparatorBlock.MODE) == var1.get(ComparatorBlock.MODE) ? 0 : 1;
            } else if (var2 instanceof NoteBlock) {
               return uT(var0, var1, NoteBlock.NOTE);
            } else if (var2 instanceof DaylightDetectorBlock) {
               return uR(var0, var1, DaylightDetectorBlock.INVERTED);
            } else if (var2 instanceof LeverBlock) {
               return uR(var0, var1, LeverBlock.POWERED);
            } else if (var2 instanceof DoorBlock) {
               return DoorBlock.canOpenByHand(var0) ? uS(var0, var1, DoorBlock.OPEN, DoorBlock.POWERED) : 0;
            } else if (var2 instanceof TrapdoorBlock) {
               return var0.isOf(Blocks.IRON_TRAPDOOR) ? 0 : uS(var0, var1, TrapdoorBlock.OPEN, TrapdoorBlock.POWERED);
            } else {
               return var2 instanceof FenceGateBlock ? uS(var0, var1, FenceGateBlock.OPEN, FenceGateBlock.POWERED) : 0;
            }
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private void uW() {
      this.yV.entrySet().removeIf(var1 -> {
         EasyPlace.PostPlacementAdjustment var2 = var1.getValue();
         var2.iL--;
         if (var2.iL <= 0) {
            if (this.uP(var2.yb, var2.lG)) {
               this.vB(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","khong chinh duoc ">(var2.yb.toShortString()));
            }

            return true;
         } else {
            return !var2.Fy && !this.uP(var2.yb, var2.lG);
         }
      });
   }

   private void uX(BlockPos var1, Item var2) {
      if (var1 != null) {
         this.gU.put(var1.toImmutable(), new EasyPlace.PredictedPlacement(vu(var2), EasyPlace.LitematicaBridge.getState(var1), 10, 100));
      }
   }

   private boolean uY(BlockPos var1) {
      return var1 != null && this.gU.containsKey(var1) && (this.mc.world == null || this.mc.world.getBlockState(var1).isReplaceable());
   }

   private void uZ() {
      if (!this.gU.isEmpty()) {
         this.gU.replaceAll((var0, var1) -> var1.sn());
         this.gU.entrySet().removeIf(var1 -> {
            if (var1.getValue().ticks > 0) {
               return false;
            } else {
               boolean var2 = this.mc.world == null || this.mc.world.getBlockState(var1.getKey()).isReplaceable();
               int var10000 = var2 ? '긃' : '긄';

               while (true) {
                  int var3 = var10000;
                  if (var3 == 44547) {
                     return true;
                  }

                  if (var3 != 44548) {
                     if (var1.getValue().abandonTicks > 0) {
                        return false;
                     }

                     this.uI(var1.getKey());
                     return true;
                  }

                  var10000 = 44546;
               }
            }
         });
      }
   }

   private boolean va(BlockPos var1) {
      if (var1 == null || this.mc.world == null) {
         return false;
      } else if (vw(this.mc.world.getBlockState(var1))) {
         return true;
      } else {
         EasyPlace.PredictedPlacement var2 = this.gU.get(var1);
         return var2 != null && var2.block != null && var2.block != Blocks.AIR && vw(var2.block.getDefaultState());
      }
   }

   private boolean vb(BlockPos var1, BlockHitResult var2, Item var3) {
      return this.mc.player != null && var2 != null && !this.mc.player.isSneaking() && (this.va(var2.getBlockPos()) || this.vc(var1, var3));
   }

   private boolean vc(BlockPos var1, Item var2) {
      Block var3 = vu(var2);
      if (var3 instanceof ChestBlock) {
         int var10000 = EasyPlace.LitematicaBridge.getChestType(var1) != ChestType.SINGLE ? '쭳' : '쭲';

         while (true) {
            switch (var10000) {
               case 52082:
                  if (this.mc.world != null) {
                     return this.vd(var1, var3);
                  }

                  return false;
               case 52083:
                  var10000 = 52081;
                  break;
               default:
                  return false;
            }
         }
      } else {
         return false;
      }
   }

   private boolean vd(BlockPos var1, Block var2) {
      if (var1 != null && var2 != null && this.mc.world != null) {
         for (Direction var6 : Direction.values()) {
            if (var6.getAxis() != Axis.Y) {
               BlockPos var7 = var1.offset(var6);
               if (this.ve(var7, var2)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean ve(BlockPos var1, Block var2) {
      if (var1 != null && var2 != null && this.mc.world != null) {
         if (this.mc.world.getBlockState(var1).getBlock() == var2) {
            return true;
         } else {
            EasyPlace.PredictedPlacement var3 = this.gU.get(var1);
            return var3 != null && var3.block == var2;
         }
      } else {
         return false;
      }
   }

   private boolean vf(BlockPos var1) {
      if (var1 == null || this.mc.world == null) {
         return false;
      } else if (this.mc.world.getBlockState(var1).getBlock() instanceof ChestBlock) {
         return true;
      } else {
         EasyPlace.PredictedPlacement var2 = this.gU.get(var1);
         return var2 != null && var2.block instanceof ChestBlock;
      }
   }

   private boolean vg() {
      return !this.gU.isEmpty() || !this.yV.isEmpty();
   }

   private void vh(BlockPos var1) {
      if (var1 != null) {
         if (this.gU.containsKey(var1) || this.yV.containsKey(var1)) {
            this.iT.add(var1.toImmutable());
         }
      }
   }

   private void vi() {
      boolean var1 = false;

      BlockPos var2;
      while ((var2 = this.iT.poll()) != null) {
         var1 = true;
         EasyPlace.PredictedPlacement var3 = this.gU.remove(var2);
         if (var3 != null && var3.desiredState != null) {
            this.uU(var2, var3.desiredState);
         } else {
            EasyPlace.PostPlacementAdjustment var4 = this.yV.get(var2);
            if (var4 != null) {
               this.uU(var2, var4.lG);
            }
         }
      }

      int var10000 = var1 ? 3026 : 3027;

      while (true) {
         int var5 = var10000;
         if (var5 == 3026) {
            EasyPlace.LitematicaBridge.rJ();
            break;
         }

         if (var5 != 3027) {
            break;
         }

         var10000 = 3025;
      }
   }

   private BlockHitResult vj(BlockPos var1, Direction var2, boolean var3) {
      if (this.mc.player != null && this.mc.world != null) {
         if (var2 != null) {
            BlockHitResult var4 = this.vk(var1, var2);
            if (var4 != null || var3) {
               return var4;
            }
         }

         Vec3d var15 = this.mc.player.getEyePos();
         BlockHitResult var5 = null;
         double var6 = Double.MAX_VALUE;

         for (Direction var11 : Direction.values()) {
            BlockHitResult var12 = this.vk(var1, var11);
            if (var12 != null) {
               double var13 = var15.squaredDistanceTo(var12.getPos());
               if (var13 < var6) {
                  var6 = var13;
                  var5 = var12;
               }
            }
         }

         return var5;
      } else {
         return null;
      }
   }

   private BlockHitResult vk(BlockPos var1, Direction var2) {
      BlockPos var3 = var1.offset(var2);
      if (this.uG(var3)) {
         Direction var4 = var2.getOpposite();
         Vec3d[] var5 = this.vm(var3, var4);
         Vec3d var6 = this.mc.player.getEyePos();
         BlockHitResult var7 = null;
         double var8 = Double.MAX_VALUE;

         for (Vec3d var13 : var5) {
            if (this.vn(var3, var13)) {
               double var14 = var6.squaredDistanceTo(var13);
               if (var14 < var8) {
                  var8 = var14;
                  var7 = new BlockHitResult(var13, var4, var3, false);
               }
            }
         }

         return var7;
      } else {
         return null;
      }
   }

   private BlockHitResult vl(BlockPos var1, Direction var2) {
      if (this.mc.world != null && var2 != null) {
         BlockPos var3 = var1.offset(var2);
         if (!this.uF(var3)) {
            return null;
         } else {
            Direction var4 = var2.getOpposite();
            return new BlockHitResult(vp(var3, var4), var4, var3, false);
         }
      } else {
         return null;
      }
   }

   private Vec3d[] vm(BlockPos var1, Direction var2) {
      Vec3d var3 = vp(var1, var2);
      double var4 = 0.32;

      return switch (var2.getAxis()) {
         case X -> new Vec3d[]{var3, var3.add(0.0, var4, 0.0), var3.add(0.0, -var4, 0.0), var3.add(0.0, 0.0, var4), var3.add(0.0, 0.0, -var4)};
         case Y -> new Vec3d[]{var3, var3.add(var4, 0.0, 0.0), var3.add(-var4, 0.0, 0.0), var3.add(0.0, 0.0, var4), var3.add(0.0, 0.0, -var4)};
         case Z -> new Vec3d[]{var3, var3.add(var4, 0.0, 0.0), var3.add(-var4, 0.0, 0.0), var3.add(0.0, var4, 0.0), var3.add(0.0, -var4, 0.0)};
         default -> throw new MatchException(null, null);
      };
   }

   private boolean vn(BlockPos var1, Vec3d var2) {
      if (this.mc.player != null && this.mc.world != null) {
         Vec3d var3 = this.mc.player.getEyePos();
         if (!this.vo(var2)) {
            return false;
         } else {
            BlockHitResult var4 = this.mc.world.raycast(new RaycastContext(var3, var2, ShapeType.COLLIDER, FluidHandling.NONE, this.mc.player));
            return var4.getType() == Type.MISS || var4.getBlockPos().equals(var1);
         }
      } else {
         return false;
      }
   }

   private boolean vo(Vec3d var1) {
      if (this.mc.player != null && var1 != null) {
         double var2 = this.mc.player.getBlockInteractionRange();
         return this.mc.player.getEyePos().squaredDistanceTo(var1) <= var2 * var2;
      } else {
         return false;
      }
   }

   private static Vec3d vp(BlockPos var0, Direction var1) {
      return new Vec3d(var0.getX() + 0.5 + var1.getOffsetX() * 0.5, var0.getY() + 0.5 + var1.getOffsetY() * 0.5, var0.getZ() + 0.5 + var1.getOffsetZ() * 0.5);
   }

   private boolean vq(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         Block var2 = vu(var1.getItem());
         return var2 == Blocks.AIR ? false : (Boolean)this.nD.get() || ((List)this.CJ.get()).contains(var2);
      } else {
         return false;
      }
   }

   private boolean vr(ItemStack var1, BlockPos var2) {
      if (!(Boolean)this.gw.get() || !(Boolean)this.xs.get() || !vs(var1)) {
         return false;
      } else if (!EasyPlace.LitematicaBridge.rS(var2)) {
         return false;
      } else if (EasyPlace.LitematicaBridge.rR(var2)) {
         return false;
      } else {
         BlockState var3 = EasyPlace.LitematicaBridge.getState(var2);
         boolean var4 = var3 == null || var3.isAir() || !vt(var1, EasyPlace.LitematicaBridge.getRequiredItem(var3));
         if (var4) {
            this.vB("o nay bi Schematic Lock chan");
         }

         return var4;
      }
   }

   private static boolean vs(ItemStack var0) {
      return var0 != null && !var0.isEmpty() && vu(var0.getItem()) != Blocks.AIR;
   }

   private static boolean vt(ItemStack var0, Item var1) {
      if (var1 != null && var1 != Items.AIR) {
         Block var2 = vu(var0.getItem());
         Block var3 = vu(var1);
         return var3 != Blocks.AIR && var2 == var3;
      } else {
         return false;
      }
   }

   private static Block vu(Item var0) {
      return var0 == Items.REDSTONE ? Blocks.REDSTONE_WIRE : Block.getBlockFromItem(var0);
   }

   private static boolean vv(Block var0) {
      return var0 != null && var0 != Blocks.AIR && var0.asItem() != Items.AIR;
   }

   private static boolean vw(BlockState var0) {
      Block var1 = var0.getBlock();
      return var1 instanceof BlockWithEntity
         || var1 instanceof AnvilBlock
         || var1 instanceof CraftingTableBlock
         || var1 instanceof LoomBlock
         || var1 instanceof CartographyTableBlock
         || var1 instanceof GrindstoneBlock
         || var1 instanceof SmithingTableBlock
         || var1 instanceof StonecutterBlock
         || var1 instanceof RepeaterBlock
         || var1 instanceof ComparatorBlock
         || var1 instanceof ButtonBlock
         || var1 instanceof LeverBlock
         || var1 instanceof TrapdoorBlock
         || var1 instanceof DoorBlock
         || var1 instanceof FenceGateBlock
         || var1 instanceof JukeboxBlock
         || var1 instanceof NoteBlock
         || var1 instanceof CakeBlock;
   }

   private void vx() {
      this.gp++;
      this.tZ();
      this.ul();
      this.uq();
      this.if = null;
      this.tN();
      this.xJ = false;
      this.gU.clear();
      this.yV.clear();
      this.iT.clear();
      this.zx.clear();
      this.Ds = 0;
      this.eh = 0;
      this.xc = 0;
      this.ja = 0;
      this.IJ = 0;
      this.zj = null;
      this.Ga = false;
      this.Ia = false;
      EasyPlace.LitematicaBridge.rJ();
   }

   private boolean vy() {
      if (FabricLoader.getInstance().isModLoaded("easyredstone")) {
         try {
            ClassLoader var1 = Thread.currentThread().getContextClassLoader();
            Class var2 = Class.forName("net.easyhopper.EasyHopperMod", false, var1);
            this.Fz = var2.getMethod("isEnabled");
            this.jD = var2.getMethod("setEnabled", boolean.class);
            this.Jp = Boolean.TRUE.equals(this.Fz.invoke(null));
            if (this.Jp) {
               this.jD.invoke(null, false);
               this.info("Temporarily disabled standalone Easy Redstone to prevent duplicate placement hooks.", new Object[0]);
            }

            this.lt = true;
            return true;
         } catch (LinkageError | ReflectiveOperationException var3) {
            this.Fz = null;
            this.jD = null;
            this.Jp = false;
            this.lt = false;
            return false;
         }
      } else {
         return true;
      }
   }

   private void vz() {
      if (this.lt) {
         try {
            if (this.Jp && this.jD != null) {
               this.jD.invoke(null, true);
            }
         } catch (ReflectiveOperationException var5) {
         } finally {
            this.Fz = null;
            this.jD = null;
            this.Jp = false;
            this.lt = false;
         }
      }
   }

   private void vA(boolean var1) {
      if ((Boolean)this.AW.get() && this.mc.inGameHud != null) {
         this.mc
            .inGameHud
            .setOverlayMessage(
               Text.literal(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","[Easy Place] ">(!var1 ? "OFF" : "ON")), false
            );
      }
   }

   private void vB(String var1) {
      if ((Boolean)this.AW.get() && this.mc.inGameHud != null) {
         int var10000 = var1.equals(this.zj) ? 'ꗧ' : 'ꗨ';

         while (true) {
            int var2 = var10000;
            if (var2 == 42471) {
               if (this.IJ > 0) {
                  return;
               }
               break;
            }

            if (var2 != 42472) {
               break;
            }

            var10000 = 42470;
         }

         this.zj = var1;
         this.IJ = 60;
         this.mc
            .inGameHud
            .setOverlayMessage(Text.literal(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","[Easy Place] ">(var1)), false);
      }
   }

   private record DependencyResolution(EasyPlace.DependencyResolutionState state, BlockPos placePos) {
      private static EasyPlace.DependencyResolution qO(BlockPos var0) {
         return new EasyPlace.DependencyResolution(EasyPlace.DependencyResolutionState.Ready, var0.toImmutable());
      }

      private static EasyPlace.DependencyResolution qP() {
         return new EasyPlace.DependencyResolution(EasyPlace.DependencyResolutionState.Waiting, null);
      }

      private static EasyPlace.DependencyResolution qQ() {
         return new EasyPlace.DependencyResolution(EasyPlace.DependencyResolutionState.Invalid, null);
      }
   }

   private static enum DependencyResolutionState {
      Ready,
      Waiting,
      Invalid;
   }

   public static enum DependencySyncMode {
      ServerAck,
      Immediate;
   }

   private static enum ExternalInventoryWindowPhase {
      NeutralArmed,
      NeutralApplied,
      NeutralReady;
   }

   static enum ExternalInventoryWindowState {
      Unavailable,
      Waiting,
      Ready;
   }

   private record InputDirection(boolean forward, boolean backward, boolean left, boolean right, float sidewaysValue, float forwardValue) {
      private static final EasyPlace.InputDirection[] zu = new EasyPlace.InputDirection[]{
         new EasyPlace.InputDirection(true, false, false, false, 0.0F, 1.0F),
         new EasyPlace.InputDirection(true, false, true, false, 1.0F, 1.0F),
         new EasyPlace.InputDirection(false, false, true, false, 1.0F, 0.0F),
         new EasyPlace.InputDirection(false, true, true, false, 1.0F, -1.0F),
         new EasyPlace.InputDirection(false, true, false, false, 0.0F, -1.0F),
         new EasyPlace.InputDirection(false, true, false, true, -1.0F, -1.0F),
         new EasyPlace.InputDirection(false, false, false, true, -1.0F, 0.0F),
         new EasyPlace.InputDirection(true, false, false, true, -1.0F, 1.0F)
      };

      private double rf() {
         return Math.toDegrees(Math.atan2(this.forwardValue, this.sidewaysValue));
      }

      private Vec2f rg() {
         return new Vec2f(this.sidewaysValue, this.forwardValue).normalize();
      }

      private PlayerInput rh(PlayerInput var1) {
         return new PlayerInput(this.forward, this.backward, this.left, this.right, var1.jump(), var1.sneak(), var1.sprint());
      }
   }

   private static enum InventoryAcquireResult {
      Ready,
      Waiting,
      Unavailable;
   }

   private record InventoryPickPlan(BlockPos pos, ItemStack pickStack, int sourceSlot) {
   }

   public static enum InventoryPickSourceMode {
      ServerRange,
      VisibleOnly;
   }

   public static enum InventoryPickSyncMode {
      SameTick,
      ServerAck;
   }

   private static enum InventoryTransferPhase {
      Queued,
      PickWait,
      PickVerify,
      PickSettle,
      NeutralArmed,
      NeutralApplied,
      NeutralReady;
   }

   private static final class LitematicaBridge {
      private static final int HG = 20;
      private static final Method[] bx = new Method[0];
      private static volatile boolean rB;
      private static volatile boolean mL;
      private static volatile Method rN;
      private static volatile boolean sH;
      private static volatile boolean HV;
      private static volatile Method Ft;
      private static volatile Method qM;
      private static volatile Method Iy;
      private static volatile Method Dt;
      private static volatile Object od;
      private static volatile Object yj;
      private static volatile boolean nQ;
      private static volatile Class<?> ry;
      private static volatile Method[] eV;
      private static volatile boolean CW;
      private static volatile boolean jq;
      private static volatile Method sb;
      private static volatile Method cE;
      private static volatile boolean Bp;
      private static volatile boolean oB;
      private static volatile Method o;
      private static volatile Object BL;
      private static volatile Object gu;
      private static volatile Object jM;
      private static final Map<BlockPos, BlockState> oh = new HashMap<>();
      private static final Map<BlockPos, List<Direction>> pu = new HashMap<>();
      private static boolean yr;
      private static Object cL;
      private static boolean oZ;
      private static BlockView jd;
      private static BlockView Fw;
      private static boolean sm;
      private static boolean le;
      private static boolean G;
      private static List<Object> zm = List.of();
      private static volatile int iw;
      private static volatile boolean EL;

      private static boolean initialize() {
         if (rB) {
            return mL;
         } else {
            Class var0 = Class.forName("fi.dy.masa.litematica.world.SchematicWorldHandler");
            rN = var0.getMethod("getSchematicWorld");
            mL = initializeRenderLayerLookup();
            initializeAreaLookup();
            initializeRenderToggleLookup();
            rB = true;
            return mL;
         }
      }

      private static void rD(String var0, Throwable var1) {
         if (!EL) {
            EL = true;
            EasyPlace var2 = EasyPlace.uZ;
            if (var2 != null && var2.isActive()) {
               var2.error(var0 + var1.getClass().getSimpleName(), new Object[0]);
               var2.toggle();
            }
         }
      }

      private static void rE(String var0, Throwable var1) {
         if (++iw >= 20) {
            mL = false;
            rD(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001\u0002"," kept failing">(var0), var1);
         }
      }

      private static boolean rF() {
         if (!initializeAreaLookup()) {
            rG();
            return false;
         } else {
            return true;
         }
      }

      private static void rG() {
         if (!nQ) {
            EasyPlace var0 = EasyPlace.uZ;
            if (var0 != null && var0.isActive()) {
               nQ = true;
               var0.warning("This Litematica version does not expose its schematic placements. Only Schematic and Schematic Lock are off.", new Object[0]);
               var0.gw.set(false);
               var0.xs.set(false);
            }
         }
      }

      private static Iterable<?> rH(Object var0) {
         if (var0 instanceof Map var2) {
            return var2.values();
         } else {
            return var0 instanceof Iterable var1 ? var1 : null;
         }
      }

      private static Object rI(Class<?> var0, String var1) {
         Object[] var2 = var0.getEnumConstants();
         if (var2 == null) {
            return null;
         } else {
            for (Object var6 : var2) {
               if (var6 instanceof Enum var7 && var7.name().equals(var1)) {
                  return var6;
               }
            }

            return null;
         }
      }

      private static void rJ() {
         oh.clear();
         pu.clear();
         yr = false;
         cL = null;
         oZ = false;
         jd = null;
         sm = false;
         G = false;
         zm = List.of();
      }

      private static Object getCurrentLayerRange() throws ReflectiveOperationException {
         if (yr) {
            return cL;
         } else {
            cL = sb.invoke(null);
            yr = true;
            return cL;
         }
      }

      private static BlockState getState(BlockPos var0) {
         if (initialize() && var0 != null) {
            BlockPos var1 = var0.toImmutable();
            if (!oh.containsKey(var1)) {
               BlockState var2 = null;
               if (rN(var0)) {
                  BlockView var3 = getSchematicBlockView();
                  if (var3 != null) {
                     var2 = var3.getBlockState(var0);
                     iw = 0;
                  }
               }

               oh.put(var1, var2);
               return var2;
            } else {
               return oh.get(var1);
            }
         } else {
            return null;
         }
      }

      private static BlockPos getPlacementAnchor(BlockPos var0) {
         BlockState var1 = getState(var0);
         if (var1 != null && !var1.isAir()) {
            if (var1.contains(Properties.DOUBLE_BLOCK_HALF) && var1.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
               return var0.down().toImmutable();
            } else {
               return var1.contains(Properties.BED_PART) && var1.get(Properties.BED_PART) == BedPart.HEAD && var1.contains(Properties.HORIZONTAL_FACING)
                  ? var0.offset(((Direction)var1.get(Properties.HORIZONTAL_FACING)).getOpposite()).toImmutable()
                  : var0.toImmutable();
            }
         } else {
            return null;
         }
      }

      private static List<Direction> getPlacementSupportDirections(BlockPos var0) {
         if (var0 == null) {
            return List.of();
         } else {
            BlockPos var1 = var0.toImmutable();
            List var2 = pu.get(var1);
            if (var2 != null) {
               return var2;
            } else {
               List var3 = List.of();
               BlockState var4 = getState(var0);
               if (var4 != null && !var4.isAir()) {
                  try {
                     if (getSchematicBlockView() instanceof WorldView var6 && var4.canPlaceAt(var6, var0)) {
                        HashSet var7 = new HashSet();

                        for (Direction var11 : Direction.values()) {
                           var7.add(var0.offset(var11).toImmutable());
                        }

                        if (!rM(var4, var6, var0, var7)) {
                           var3 = rK(var4, var6, var0, var7);
                        }
                     }
                  } catch (LinkageError | ReflectiveOperationException var12) {
                     mL = false;
                     rD("support direction lookup", var12);
                  }
               }

               pu.put(var1, var3);
               return var3;
            }
         }
      }

      private static List<Direction> rK(BlockState var0, WorldView var1, BlockPos var2, Set<BlockPos> var3) {
         ArrayList var4 = new ArrayList();

         for (Direction var8 : Direction.values()) {
            BlockPos var9 = var2.offset(var8).toImmutable();
            if (!rL(var9) && !rM(var0, var1, var2, Set.of(var9))) {
               var4.add(var8);
            }
         }

         if (!var4.isEmpty()) {
            return List.copyOf(var4);
         } else {
            Direction[] var12 = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};

            for (Direction var16 : var12) {
               BlockPos var10 = var2.offset(var16).toImmutable();
               if (!rL(var10)) {
                  HashSet var11 = new HashSet(var3);
                  var11.remove(var10);
                  if (rM(var0, var1, var2, var11)) {
                     return List.of(var16);
                  }
               }
            }

            return List.of();
         }
      }

      private static boolean rL(BlockPos var0) {
         BlockState var1 = getState(var0);
         return var1 == null || var1.isAir();
      }

      private static boolean rM(BlockState var0, WorldView var1, BlockPos var2, Set<BlockPos> var3) {
         return var0.canPlaceAt(new EasyPlace.LitematicaBridge.MaskedWorldView(var1, var3), var2);
      }

      private static BlockView getSchematicBlockView() throws ReflectiveOperationException {
         if (!oZ) {
            jd = rN.invoke(null) instanceof BlockView var1 ? var1 : null;
            oZ = true;
            if (jd != Fw) {
               Fw = jd;
               oh.clear();
               pu.clear();
            }

            return jd;
         } else {
            return jd;
         }
      }

      private static boolean initializeRenderLayerLookup() {
         if (CW) {
            return jq;
         } else {
            try {
               Class var0 = Class.forName("fi.dy.masa.litematica.data.DataManager");
               sb = var0.getMethod("getRenderLayerRange");
               Class var1 = sb.getReturnType();
               cE = var1.getMethod("isPositionWithinRange", BlockPos.class);
               jq = true;
            } catch (LinkageError | RuntimeException | ReflectiveOperationException var2) {
               jq = false;
               rD("render layer lookup", var2);
            }

            CW = true;
            return jq;
         }
      }

      private static boolean rN(BlockPos var0) {
         if (var0 == null || !initializeRenderLayerLookup()) {
            return false;
         } else if (!rO()) {
            return false;
         } else if (rQ(var0)) {
            try {
               Object var1 = getCurrentLayerRange();
               return var1 == null ? false : Boolean.TRUE.equals(cE.invoke(var1, var0));
            } catch (LinkageError | ReflectiveOperationException var2) {
               jq = false;
               mL = false;
               rD("render layer query", var2);
               return false;
            } catch (RuntimeException var3) {
               rE("render layer query", var3);
               return false;
            }
         } else {
            return false;
         }
      }

      private static boolean initializeRenderToggleLookup() {
         if (Bp) {
            return oB;
         } else {
            try {
               Class var0 = Class.forName("fi.dy.masa.litematica.config.Configs$Visuals");
               Class var1 = Class.forName("fi.dy.masa.malilib.config.IConfigBoolean");
               o = var1.getMethod("getBooleanValue");
               BL = var0.getField("ENABLE_RENDERING").get(null);
               gu = var0.getField("ENABLE_SCHEMATIC_RENDERING").get(null);
               jM = var0.getField("ENABLE_SCHEMATIC_BLOCKS").get(null);
               oB = BL != null && gu != null && jM != null;
            } catch (LinkageError | RuntimeException | ReflectiveOperationException var2) {
               oB = false;
            }

            Bp = true;
            return oB;
         }
      }

      private static boolean rO() {
         if (!initializeRenderToggleLookup()) {
            return true;
         } else if (sm) {
            return le;
         } else {
            boolean var10000;
            label28: {
               boolean var0 = true;
               if (rP(BL) && rP(gu)) {
                  if (rP(jM)) {
                     var10000 = true;
                     break label28;
                  }

                  byte var6;
                  while (var6 == 94) {
                     var6 = 92;
                  }
               }

               var10000 = false;
            }

            boolean var3 = var10000;
            le = var3;
            sm = true;
            return var3;
         }
      }

      private static boolean rP(Object var0) throws ReflectiveOperationException {
         return Boolean.TRUE.equals(o.invoke(var0));
      }

      private static boolean rQ(BlockPos var0) {
         if (var0 == null) {
            return false;
         } else if (initializeAreaLookup() && yj != null) {
            for (Object var2 : getRenderedSubRegionBoxes()) {
               if (var2 != null && rT(var0, var2)) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      }

      private static List<Object> getRenderedSubRegionBoxes() throws ReflectiveOperationException {
         if (G) {
            return zm;
         } else {
            ArrayList var0 = new ArrayList();
            Object var1 = Ft.invoke(null);
            if (qM.invoke(var1) instanceof Iterable var3) {
               for (Object var5 : var3) {
                  Iterable var6 = rH(Dt.invoke(var5, yj));
                  if (var6 != null) {
                     for (Object var8 : var6) {
                        if (var8 != null) {
                           var0.add(var8);
                        }
                     }
                  }
               }
            }

            zm = List.copyOf(var0);
            G = true;
            return zm;
         }
      }

      private static boolean rR(BlockPos var0) {
         if (var0 == null || !initializeRenderLayerLookup()) {
            return false;
         } else {
            return !rN(var0) ? jq : false;
         }
      }

      private static Item getRequiredItem(BlockPos var0) {
         return getRequiredItem(getState(var0));
      }

      private static Item getRequiredItem(BlockState var0) {
         if (var0 != null && !var0.isAir()) {
            Item var1 = var0.getBlock().asItem();
            return var1 != Items.AIR ? var1 : EasyPlace.LitematicaBridge.GrownBlockItems.ap.getOrDefault(var0.getBlock(), Items.AIR);
         } else {
            return null;
         }
      }

      private static boolean rS(BlockPos var0) {
         if (var0 != null && initializeAreaLookup()) {
            try {
               Object var1 = Ft.invoke(null);
               Object var2 = qM.invoke(var1);
               int var10000 = !(var2 instanceof Iterable) ? 26141 : 26140;

               while (true) {
                  int var9 = var10000;
                  if (var9 == 26140) {
                     for (Object var5 : (Iterable)var2) {
                        if (Boolean.TRUE.equals(Iy.invoke(var5))) {
                           Iterable var6 = rH(Dt.invoke(var5, od));
                           if (var6 != null) {
                              label79:
                              for (Object var8 : var6) {
                                 if (var8 != null) {
                                    var10000 = !rT(var0, var8) ? '뿿' : '뿾';

                                    while (true) {
                                       switch (var10000) {
                                          case 49150:
                                             return true;
                                          case 49151:
                                             var10000 = 49149;
                                             break;
                                          default:
                                             continue label79;
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }

                     return false;
                  }

                  if (var9 != 26141) {
                     return false;
                  }

                  var10000 = 26139;
               }
            } catch (LinkageError | ReflectiveOperationException var10) {
               HV = false;
               rD("schematic area lookup", var10);
               return false;
            }
         } else {
            return false;
         }
      }

      private static boolean rT(BlockPos var0, Object var1) throws ReflectiveOperationException {
         Method[] var2 = rU(var1.getClass());
         if (var2.length == 2) {
            return rX(var0, (BlockPos)var2[0].invoke(var1), (BlockPos)var2[1].invoke(var1));
         } else {
            return var2.length != 6
               ? false
               : rX(
                  var0,
                  new BlockPos((Integer)var2[0].invoke(var1), (Integer)var2[1].invoke(var1), (Integer)var2[2].invoke(var1)),
                  new BlockPos((Integer)var2[3].invoke(var1), (Integer)var2[4].invoke(var1), (Integer)var2[5].invoke(var1))
               );
         }
      }

      private static Method[] rU(Class<?> var0) {
         Class var1 = ry;
         Method[] var2 = eV;
         if (var2 != null && var0 == var1) {
            return var2;
         } else {
            Method[] var3 = rV(var0, "getPos1", "getPos2");
            if (var3 == null) {
               var3 = rV(var0, "minX", "minY", "minZ", "maxX", "maxY", "maxZ");
            }

            if (var3 == null) {
               var3 = bx;
               rD(
                  StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","sub-region box "," corner lookup">(var0.getName()),
                  new NoSuchMethodException("no known corner accessors")
               );
            }

            eV = var3;
            ry = var0;
            return var3;
         }
      }

      private static Method[] rV(Class<?> var0, String... var1) {
         Method[] var2 = new Method[var1.length];

         for (int var3 = 0; var3 < var1.length; var3++) {
            try {
               var2[var3] = var0.getMethod(var1[var3]);
            } catch (LinkageError | ReflectiveOperationException var5) {
               return null;
            }
         }

         return var2;
      }

      private static boolean initializeAreaLookup() {
         if (!sH) {
            try {
               Class var0 = Class.forName("fi.dy.masa.litematica.data.DataManager");
               Class var1 = Class.forName("fi.dy.masa.litematica.schematic.placement.SchematicPlacementManager");
               Class var2 = Class.forName("fi.dy.masa.litematica.schematic.placement.SchematicPlacement");
               Ft = var0.getMethod("getSchematicPlacementManager");
               qM = var1.getMethod("getAllSchematicsPlacements");
               Iy = var2.getMethod("isEnabled");
               Dt = rW(var2, "getSubRegionBoxes");
               if (Dt == null) {
                  throw new NoSuchMethodException("SchematicPlacement.getSubRegionBoxes(<enum>)");
               }

               od = Enum.valueOf(Dt.getParameterTypes()[0], "PLACEMENT_ENABLED");
               yj = rI(Dt.getParameterTypes()[0], "RENDERING_ENABLED");
               HV = true;
            } catch (LinkageError | RuntimeException | ReflectiveOperationException var3) {
               HV = false;
            }

            sH = true;
            return HV;
         } else {
            return HV;
         }
      }

      private static Method rW(Class<?> var0, String var1) {
         for (Method var5 : var0.getMethods()) {
            if (var5.getName().equals(var1)) {
               Class[] var6 = var5.getParameterTypes();
               if (var6.length == 1 && var6[0].isEnum()) {
                  return var5;
               }
            }
         }

         return null;
      }

      private static boolean rX(BlockPos var0, BlockPos var1, BlockPos var2) {
         return var1 != null && var2 != null
            ? var0.getX() >= Math.min(var1.getX(), var2.getX())
               && var0.getX() <= Math.max(var1.getX(), var2.getX())
               && var0.getY() >= Math.min(var1.getY(), var2.getY())
               && var0.getY() <= Math.max(var1.getY(), var2.getY())
               && var0.getZ() >= Math.min(var1.getZ(), var2.getZ())
               && var0.getZ() <= Math.max(var1.getZ(), var2.getZ())
            : false;
      }

      private static EasyPlace.RotationMode getRotation(BlockPos var0) {
         BlockState var1 = getState(var0);
         if (var1 != null && !var1.isAir()) {
            Object var2 = null;
            if (var1.contains(Properties.FACING)) {
               var2 = (Direction)var1.get(Properties.FACING);
            } else if (var1.contains(Properties.HORIZONTAL_FACING)) {
               var2 = (Direction)var1.get(Properties.HORIZONTAL_FACING);
            } else {
               if (var1.contains(Properties.HOPPER_FACING)) {
                  return null;
               }

               if (var1.contains(Properties.ORIENTATION)) {
                  var2 = ((Orientation)var1.get(Properties.ORIENTATION)).getFacing();
               } else {
                  var2 = getOrientationDirection(var1);
               }
            }

            return EasyPlace.RotationMode.sv((Direction)var2);
         } else {
            return null;
         }
      }

      private static boolean rY(BlockPos var0, BlockState var1) {
         BlockState var2 = getState(var0);
         return rZ(var2, var1);
      }

      private static boolean rZ(BlockState var0, BlockState var1) {
         if (var0 != null && !var0.isAir() && var1 != null) {
            if (var0.getBlock() != var1.getBlock()) {
               return false;
            } else if (var0.contains(SlabBlock.TYPE)) {
               if (!var1.contains(SlabBlock.TYPE)) {
                  return false;
               } else {
                  SlabType var8 = (SlabType)var0.get(SlabBlock.TYPE);
                  return var8 == SlabType.DOUBLE ? true : var1.get(SlabBlock.TYPE) == var8;
               }
            } else {
               boolean var2 = false;
               Property[] var3 = new Property[]{
                  Properties.ORIENTATION,
                  Properties.FACING,
                  Properties.HORIZONTAL_FACING,
                  Properties.BLOCK_FACE,
                  Properties.ATTACHMENT,
                  Properties.HANGING,
                  Properties.HOPPER_FACING,
                  Properties.AXIS,
                  Properties.BLOCK_HALF,
                  Properties.VERTICAL_DIRECTION,
                  Properties.DOOR_HINGE,
                  Properties.ROTATION
               };

               for (Property var7 : var3) {
                  if (var0.contains(var7)) {
                     var2 = true;
                     if (!sa(var0, var1, var7)) {
                        return false;
                     }
                  }
               }

               if (!var2) {
                  Direction var9 = getOrientationDirection(var0);
                  Direction var10 = getOrientationDirection(var1);
                  return var9 == null ? var10 == null : var9 == var10;
               } else {
                  return true;
               }
            }
         } else {
            return false;
         }
      }

      private static boolean sa(BlockState var0, BlockState var1, Property var2) {
         return var1.contains(var2) && var1.get(var2).equals(var0.get(var2));
      }

      private static Orientation getOrientation(BlockPos var0) {
         BlockState var1 = getState(var0);
         return var1 != null && var1.contains(Properties.ORIENTATION) ? (Orientation)var1.get(Properties.ORIENTATION) : null;
      }

      private static ChestType getChestType(BlockPos var0) {
         BlockState var1 = getState(var0);
         return var1 != null && var1.contains(Properties.CHEST_TYPE) ? (ChestType)var1.get(Properties.CHEST_TYPE) : null;
      }

      private static Direction getChestPartnerDirection(BlockPos var0) {
         BlockState var1 = getState(var0);
         if (var1 != null && var1.getBlock() instanceof ChestBlock) {
            int var10000 = !var1.contains(Properties.CHEST_TYPE) ? '瘝' : '画';

            while (true) {
               switch (var10000) {
                  case 64163:
                     if (var1.get(Properties.CHEST_TYPE) != ChestType.SINGLE) {
                        return ChestBlock.getFacing(var1);
                     }

                     return null;
                  case 64164:
                     var10000 = 64162;
                     break;
                  default:
                     return null;
               }
            }
         } else {
            return null;
         }
      }

      private static boolean sb(BlockPos var0) {
         BlockState var1 = getState(var0);
         return var1 != null && var1.contains(Properties.HOPPER_FACING);
      }

      private static Direction getHopperFacing(BlockPos var0) {
         BlockState var1 = getState(var0);
         return var1 != null && var1.contains(Properties.HOPPER_FACING) ? (Direction)var1.get(Properties.HOPPER_FACING) : null;
      }

      private static boolean sc(MinecraftClient var0, BlockPos var1, Predicate<BlockPos> var2) {
         return var0.world != null && var1 != null ? sd(var0, var1, getState(var1), var2) : false;
      }

      private static boolean sd(MinecraftClient var0, BlockPos var1, BlockState var2, Predicate<BlockPos> var3) {
         short var8;
         if (var0.world != null) {
            if (var1 != null) {
               if (var2 != null && !var2.isAir()) {
                  BlockState var4 = var0.world.getBlockState(var1);
                  if (!var4.isReplaceable() && !se(var2, var4)) {
                     return false;
                  }

                  return var3 == null || !var3.test(var1);
               }

               return false;
            }
         } else {
            while (var8 == 29894) {
               var8 = 29892;
            }
         }

         return false;
      }

      private static boolean se(BlockState var0, BlockState var1) {
         return var0.contains(SlabBlock.TYPE) && var1.contains(SlabBlock.TYPE) && var1.getBlock() == var0.getBlock()
            ? var0.get(SlabBlock.TYPE) == SlabType.DOUBLE && var1.get(SlabBlock.TYPE) != SlabType.DOUBLE
            : false;
      }

      private static BlockHitResult sf(MinecraftClient var0, Predicate<BlockPos> var1) {
         if (var0.player == null || var0.world == null) {
            return null;
         } else if (!initialize()) {
            return null;
         } else {
            BlockView var2;
            try {
               var2 = getSchematicBlockView();
            } catch (RuntimeException var12) {
               rE("schematic world lookup", var12);
               return null;
            }

            if (var2 == null) {
               return null;
            } else {
               Vec3d var3 = var0.player.getEyePos();
               double var4 = var0.player.getBlockInteractionRange();
               Vec3d var6 = var3.add(var0.player.getRotationVec(1.0F).multiply(var4));
               BlockHitResult var7 = var0.world.raycast(new RaycastContext(var3, var6, ShapeType.COLLIDER, FluidHandling.NONE, var0.player));
               boolean var8 = var7.getType() == Type.BLOCK && !var0.world.getBlockState(var7.getBlockPos()).isReplaceable();
               Vec3d var9 = var8 ? var7.getPos() : var6;
               Box var10 = var0.player.getBoundingBox();
               ShapeContext var11 = ShapeContext.of(var0.player);
               return (BlockHitResult)BlockView.raycast(var3, var9, Boolean.TRUE, (var7x, var8x) -> {
                  if (var10.intersects(var8x.getX(), var8x.getY(), var8x.getZ(), var8x.getX() + 1.0, var8x.getY() + 1.0, var8x.getZ() + 1.0)) {
                     return null;
                  } else {
                     BlockState var9x = getState(var8x);
                     int var10000 = sd(var0, var8x, var9x, var1) ? 1160 : 1159;

                     while (true) {
                        switch (var10000) {
                           case 1159:
                              return null;
                           case 1160:
                              var10000 = 1158;
                              break;
                           default:
                              VoxelShape var10x = var9x.getOutlineShape(var2, var8x, var11);
                              if (var10x.isEmpty()) {
                                 return null;
                              }

                              return var10x.raycast(var3, var9, var8x);
                        }
                     }
                  }
               }, var0x -> null);
            }
         }
      }

      private static Direction getOrientationDirection(BlockState var0) {
         for (Property var2 : var0.getProperties()) {
            if ("orientation".equals(var2.getName())) {
               String var3 = String.valueOf(var0.get(var2));
               String var4 = var3.contains("_") ? var3.substring(0, var3.indexOf(95)) : var3;

               for (Direction var8 : Direction.values()) {
                  if (var8.getId().equalsIgnoreCase(var4) || var8.name().equalsIgnoreCase(var4)) {
                     return var8;
                  }
               }
            }
         }

         return null;
      }

      private static final class GrownBlockItems {
         private static final Map<Block, Item> ap = Map.ofEntries(
            Map.entry(Blocks.KELP_PLANT, Items.KELP),
            Map.entry(Blocks.CAVE_VINES_PLANT, Items.GLOW_BERRIES),
            Map.entry(Blocks.TWISTING_VINES_PLANT, Items.TWISTING_VINES),
            Map.entry(Blocks.WEEPING_VINES_PLANT, Items.WEEPING_VINES),
            Map.entry(Blocks.BAMBOO_SAPLING, Items.BAMBOO),
            Map.entry(Blocks.TALL_SEAGRASS, Items.SEAGRASS)
         );
      }

      private static final class MaskedWorldView implements WorldView {
         private final WorldView hi;
         private final Set<BlockPos> T;

         private MaskedWorldView(WorldView var1, Set<BlockPos> var2) {
            this.hi = var1;
            this.T = var2;
         }

         public BlockState getBlockState(BlockPos pos) {
            int var10000 = this.T.contains(pos) ? 8767 : 8768;

            while (true) {
               int var2 = var10000;
               if (var2 == 8767) {
                  return Blocks.AIR.getDefaultState();
               }

               if (var2 != 8768) {
                  return this.hi.getBlockState(pos);
               }

               var10000 = 8766;
            }
         }

         public FluidState getFluidState(BlockPos pos) {
            return !this.T.contains(pos) ? this.hi.getFluidState(pos) : Fluids.EMPTY.getDefaultState();
         }

         public BlockEntity getBlockEntity(BlockPos pos) {
            return this.T.contains(pos) ? null : this.hi.getBlockEntity(pos);
         }

         public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
            return this.hi.getChunk(chunkX, chunkZ, leastStatus, create);
         }

         public boolean isChunkLoaded(int chunkX, int chunkZ) {
            return this.hi.isChunkLoaded(chunkX, chunkZ);
         }

         public int getTopY(net.minecraft.world.Heightmap.Type heightmap, int x, int z) {
            return this.hi.getTopY(heightmap, x, z);
         }

         public int getAmbientDarkness() {
            return this.hi.getAmbientDarkness();
         }

         public BiomeAccess getBiomeAccess() {
            return this.hi.getBiomeAccess();
         }

         public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
            return this.hi.getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
         }

         public boolean isClient() {
            return this.hi.isClient();
         }

         public int getSeaLevel() {
            return this.hi.getSeaLevel();
         }

         public DimensionType getDimension() {
            return this.hi.getDimension();
         }

         public DynamicRegistryManager getRegistryManager() {
            return this.hi.getRegistryManager();
         }

         public FeatureSet getEnabledFeatures() {
            return this.hi.getEnabledFeatures();
         }

         public EnvironmentAttributeAccess getEnvironmentAttributes() {
            return this.hi.getEnvironmentAttributes();
         }

         public float getBrightness(Direction direction, boolean shaded) {
            return this.hi.getBrightness(direction, shaded);
         }

         public LightingProvider getLightingProvider() {
            return this.hi.getLightingProvider();
         }

         public WorldBorder getWorldBorder() {
            return this.hi.getWorldBorder();
         }

         public List<VoxelShape> getEntityCollisions(Entity entity, Box box) {
            return this.hi.getEntityCollisions(entity, box);
         }
      }
   }

   private static final class PendingDependencyPlacement {
      private final Hand FR;
      private final BlockPos Fd;
      private BlockPos fs;
      private int kD;

      private PendingDependencyPlacement(Hand var1, BlockPos var2) {
         this.FR = var1;
         this.Fd = var2;
      }
   }

   private static final class PendingExternalInventoryWindow {
      private final Object ay;
      private EasyPlace.ExternalInventoryWindowPhase dL = EasyPlace.ExternalInventoryWindowPhase.NeutralArmed;
      private ClientPlayerEntity ki;
      private PlayerInput gP;
      private Vec2f dz;
      private int zJ;

      private PendingExternalInventoryWindow(Object var1) {
         this.ay = var1;
      }
   }

   private static final class PendingInventoryTransfer {
      private final Item ck;
      private final BlockPos oK;
      private EasyPlace.InventoryTransferPhase oG = EasyPlace.InventoryTransferPhase.Queued;
      private ClientPlayerEntity vV;
      private PlayerInput hU;
      private Vec2f aA;
      private boolean xq;
      private boolean CT;
      private boolean yC;
      private boolean ea;
      private int be;

      private PendingInventoryTransfer(Item var1, BlockPos var2) {
         this.ck = var1;
         this.oK = var2;
      }
   }

   private static final class PendingPlacement {
      private final int xi;
      private final Hand ce;
      private final BlockHitResult lX;
      private final BlockPos gD;
      private final Item fH;
      private final boolean Hf;
      private final EasyPlace.RotationMode aZ;
      private final Block Jc;
      private final Orientation Bz;
      private final boolean Dr;
      private final boolean hZ;
      private final boolean sd;
      private final BlockPos dd;
      private final boolean vL;
      private final boolean bJ;
      private float lc;
      private float AN;
      private float FP;
      private float Bg;
      private float lF;
      private float gz;
      private ClientPlayerEntity Iq;
      private PlayerInput eM;
      private Vec2f yt;
      private boolean sV;
      private boolean FM;
      private boolean Gv;
      private boolean iF;
      private int cB;

      private PendingPlacement(
         int var1,
         Hand var2,
         BlockHitResult var3,
         BlockPos var4,
         Item var5,
         boolean var6,
         EasyPlace.RotationMode var7,
         Block var8,
         Orientation var9,
         boolean var10
      ) {
         this.xi = var1;
         this.ce = var2;
         this.lX = var3;
         this.gD = var4;
         this.fH = var5;
         this.Hf = var6;
         this.aZ = var7;
         this.Jc = var8;
         this.Bz = var9;
         this.Dr = var10;
         this.hZ = false;
         this.sd = EasyPlace.vu(var5) instanceof HopperBlock;
         this.dd = null;
         this.vL = var8 instanceof SlabBlock
            || var5 instanceof VerticallyAttachableBlockItem
            || !EasyPlace.LitematicaBridge.getPlacementSupportDirections(var4).isEmpty();
         this.bJ = var5 instanceof VerticallyAttachableBlockItem;
      }

      private PendingPlacement(int var1, Hand var2, BlockHitResult var3, BlockPos var4) {
         this.xi = var1;
         this.ce = var2;
         this.lX = var3;
         this.gD = var4;
         this.fH = Items.AIR;
         this.Hf = false;
         this.aZ = null;
         this.Jc = null;
         this.Bz = null;
         this.Dr = false;
         this.hZ = true;
         this.sd = false;
         this.dd = var4;
         this.vL = true;
         this.bJ = false;
      }
   }

   private final class PlacementInput extends DelegatingInput {
      private PlacementInput(Input var2) {
         super(var2);
      }

      @Override
      public void tick() {
         super.tick();
         if (!this.qK()) {
            PlayerInput var1 = this.qI().playerInput;
            Vec2f var2 = this.qI().getMovementInput();
            if (!EasyPlace.this.tK(this, var1, var2)) {
               if (EasyPlace.this.BU != null) {
                  EasyPlace.this.tJ(this, var1, var2);
               } else {
                  EasyPlace.this.tp(this, var1, var2);
               }
            }
         }
      }

      public void jump() {
         super.jump();
      }
   }

   private static final class PostPlacementAdjustment {
      private final BlockPos yb;
      private final BlockState lG;
      private final int yX;
      private boolean Fy;
      private int lw;
      private int wQ;
      private int iL;
      private BlockState Dl;

      private PostPlacementAdjustment(BlockPos var1, BlockState var2, int var3, int var4) {
         this.yb = var1;
         this.lG = var2;
         this.wQ = var3;
         this.yX = var3 + 4;
         this.iL = var4;
      }
   }

   private static final class PostPlacementRotation {
      private final BlockHitResult kJ;
      private final float vz;
      private float bh;
      private float mS;
      private float li;
      private float sT;
      private ClientPlayerEntity ob;
      private PlayerInput zH;
      private Vec2f xv;
      private boolean Bk;

      private PostPlacementRotation(BlockHitResult var1, float var2) {
         this.kJ = var1;
         this.vz = var2;
      }
   }

   private record PredictedPlacement(Block block, BlockState desiredState, int ticks, int abandonTicks) {
      private EasyPlace.PredictedPlacement sn() {
         return new EasyPlace.PredictedPlacement(this.block, this.desiredState, this.ticks - 1, this.abandonTicks - 1);
      }
   }

   public static enum RotationMode {
      Auto(null),
      Down(Direction.DOWN),
      Up(Direction.UP),
      North(Direction.NORTH),
      South(Direction.SOUTH),
      West(Direction.WEST),
      East(Direction.EAST);

      private final Direction dk;

      private RotationMode(Direction var3) {
         this.dk = var3;
      }

      public float ss(float var1) {
         if (this.dk == null) {
            return var1;
         } else {
            int var2 = <unrepresentable>.Fx[this.dk.getOpposite().ordinal()];
            return var2 == 3 ? 180.0F : (var2 != 4 ? (var2 == 5 ? 90.0F : (var2 == 6 ? -90.0F : var1)) : 0.0F);
         }
      }

      public float st(float var1) {
         if (this.dk == null) {
            return var1;
         } else {
            return switch (this.dk.getOpposite()) {
               case UP -> -90.0F;
               case DOWN -> 90.0F;
               default -> 0.0F;
            };
         }
      }

      public EasyPlace.RotationMode su() {
         return switch (this) {
            case Auto -> Auto;
            case Down -> Up;
            case Up -> Down;
            case North -> South;
            case South -> North;
            case West -> East;
            case East -> West;
         };
      }

      private static EasyPlace.RotationMode sv(Direction var0) {
         if (var0 == null) {
            return null;
         } else {
            return switch (var0) {
               case UP -> Up;
               case DOWN -> Down;
               case NORTH -> North;
               case SOUTH -> South;
               case WEST -> West;
               case EAST -> East;
               default -> throw new MatchException(null, null);
            };
         }
      }
   }

   private record RotationPlan(float yaw, float pitch, PlayerInput input, Vec2f movement) {
   }
}
