package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.StringConcatFactory;
import java.lang.invoke.MethodHandles.Lookup;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AutoReconnect;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class SpawnerProtect extends AliceModule {
   private final SettingGroup nU = this.settings.getDefaultGroup();
   private final SettingGroup qz = this.settings.createGroup("Whitelist");
   private final SettingGroup EN = this.settings.createGroup("Webhook");
   private final Setting<Boolean> He = this.EN
      .add(((Builder)((Builder)((Builder)new Builder().name("webhook")).description("bat thong bao qua webhook")).defaultValue(false)).build());
   private final Setting<String> C = this.EN
      .add(
         ((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)new meteordevelopment.meteorclient.settings.StringSetting.Builder()
                        .name("webhook-url"))
                     .description("dia chi webhook discord de gui thong bao"))
                  .defaultValue(""))
               .visible(this.He::get))
            .build()
      );
   private final Setting<Boolean> oj = this.EN
      .add(
         ((Builder)((Builder)((Builder)((Builder)new Builder().name("self-ping")).description("tag ten ban vao tin nhan webhook")).defaultValue(false))
               .visible(this.He::get))
            .build()
      );
   private final Setting<String> qa = this.EN
      .add(
         ((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)new meteordevelopment.meteorclient.settings.StringSetting.Builder()
                        .name("discord-id"))
                     .description("ID nguoi dung discord cua ban de ping"))
                  .defaultValue(""))
               .visible(() -> (Boolean)this.He.get() && (Boolean)this.oj.get()))
            .build()
      );
   private final Setting<Boolean> Jb = this.nU
      .add(((Builder)((Builder)((Builder)new Builder().name("notifications")).description("hien thi tin nhan phan hoi")).defaultValue(true)).build());
   private final Setting<Integer> Dn = this.nU
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("spawner-range"))
                  .description("tam quet cac spawner con lai"))
               .defaultValue(16))
            .min(1)
            .max(50)
            .sliderMax(50)
            .build()
      );
   private final Setting<Integer> oR = this.nU
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("emergency-distance"))
                  .description("khoang cach (block) de kich hoat tu dong thoat (0 de tat)"))
               .defaultValue(7))
            .min(0)
            .max(20)
            .sliderMax(20)
            .build()
      );
   private final Setting<Integer> FF = this.nU
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("min-detection-range"))
                  .description("khoang cach toi thieu de phat hien nguoi choi"))
               .defaultValue(0))
            .min(0)
            .max(50)
            .sliderMax(50)
            .build()
      );
   private final Setting<Integer> Gq = this.nU
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("max-detection-range"))
                  .description("khoang cach toi da de phat hien nguoi choi"))
               .defaultValue(50))
            .min(1)
            .max(100)
            .sliderMax(100)
            .build()
      );
   private final Setting<Integer> mc = this.nU
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("spawner-check-delay-ms"))
                  .description("thoi gian tre (ms) truoc khi xac nhan da het spawner"))
               .defaultValue(3000))
            .min(1000)
            .max(10000)
            .sliderMax(10000)
            .build()
      );
   private final Setting<Integer> yu = this.nU
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("spawner-timeout-ms"))
                  .description("thoi gian (ms) truoc khi bo qua spawner khong the dao duoc"))
               .defaultValue(4000))
            .min(4000)
            .max(30000)
            .sliderMax(30000)
            .build()
      );
   private final Setting<Boolean> K = this.nU
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("deposit-to-echest")).description("cat spawner vao ender chest sau khi dao")).defaultValue(true))
            .build()
      );
   private final Setting<Integer> uB = this.nU
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("sell-retries"))
                  .description("so lan toi da thu ban do rac truoc khi bo qua"))
               .defaultValue(2))
            .min(1)
            .max(10)
            .sliderMax(5)
            .build()
      );
   private final Setting<Boolean> Fo = this.qz
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("enable-whitelist"))
                  .description("bat danh sach trang (nguoi choi trong danh sach se khong kich hoat bao ve)"))
               .defaultValue(false))
            .build()
      );
   private final Setting<List<String>> ua = this.qz
      .add(
         ((meteordevelopment.meteorclient.settings.StringListSetting.Builder)((meteordevelopment.meteorclient.settings.StringListSetting.Builder)((meteordevelopment.meteorclient.settings.StringListSetting.Builder)((meteordevelopment.meteorclient.settings.StringListSetting.Builder)new meteordevelopment.meteorclient.settings.StringListSetting.Builder()
                        .name("whitelisted-players"))
                     .description("danh sach nguoi choi de bo qua"))
                  .defaultValue(new ArrayList()))
               .visible(this.Fo::get))
            .build()
      );
   private SpawnerProtect.State dr = SpawnerProtect.State.IDLE;
   private String ok = "";
   private long EF = 0L;
   private boolean wh = false;
   private boolean Fj = false;
   private int aI = 0;
   private int sc = 0;
   private int bM = -1;
   private BlockPos vy = null;
   private long zl = -1L;
   private long tE = -1L;
   private long ax = -1L;
   private BlockPos wM = null;
   private int py = 0;
   private boolean il = false;
   private String BZ = "";
   private World nm = null;
   private int ip = 0;
   private final int wj = 3;
   private float BQ;
   private float tt;
   private boolean fA = false;
   private final float cS = 8.0F;
   private long ah = -1L;
   private final Set<BlockPos> IE = new HashSet<>();
   private long fC = -1L;
   private volatile boolean Dw = false;
   private BlockPos EP = null;
   private Direction ih = null;
   private float bH = 0.0F;
   private float nY = 0.0F;
   private boolean Cn = false;
   private boolean EB = false;
   private int gX = -1;
   private int vn = -1;
   private int on = -1;
   private int DE = 0;
   private int zO = 0;
   private int yM = 0;
   private SpawnerProtect.State dg = null;
   private boolean yx = false;
   private long tH = 0L;
   private long Jo = 0L;
   private boolean gF = false;
   private static boolean tG = false;

   private static void Gz(Object... var0) {
   }

   public static Lookup getLookup() {
      return MethodHandles.lookup();
   }

   public SpawnerProtect() {
      super(AliceAddon.CATEGORY, "spawner-protect", "Tu dong pha va cat spawner vao tui do khi phat hien nguoi choi.");
   }

   @Override
   public void onActivate() {
      super.onActivate();
      this.GA();
      this.GB();
      if (this.mc.world != null) {
         this.nm = this.mc.world;
         this.ip = 0;
         if ((Boolean)this.Jb.get()) {
            this.HG(this.mc.world.getRegistryKey().getValue() + "");
         }

         if ((Boolean)this.Jb.get()) {
            this.HG("Monitoring for players...");
         }
      }

      if ((Boolean)this.Jb.get()) {
         Gz("Make sure to have an empty inventory with only a pickaxe and an ender chest nearby!");
      }
   }

   private void GA() {
      this.dr = SpawnerProtect.State.IDLE;
      this.ok = "";
      this.EF = 0L;
      this.wh = false;
      this.Fj = false;
      this.aI = 0;
      this.sc = 0;
      this.bM = -1;
      this.vy = null;
      this.zl = -1L;
      this.tE = -1L;
      this.ax = -1L;
      this.wM = null;
      this.py = 0;
      this.il = false;
      this.BZ = "";
      this.IE.clear();
      this.fA = false;
      this.fC = -1L;
      this.Dw = false;
      this.EP = null;
      this.ih = null;
      this.Cn = false;
      this.EB = false;
      this.gX = -1;
      this.vn = -1;
      this.on = -1;
      this.DE = 0;
      this.zO = 0;
      this.yM = 0;
      this.dg = null;
      this.yx = false;
      this.gF = false;
   }

   private void GB() {
      if ((Boolean)this.Jb.get()) {
         this.HG("Manual mining mode activated");
      }
   }

   private void GC() {
      Module var1 = Modules.get().get(AutoReconnect.class);
      if (var1 != null && var1.isActive()) {
         var1.toggle();
         if ((Boolean)this.Jb.get()) {
            this.HG("Meteor AutoReconnect disabled");
         }
      }

      Module var2 = Modules.get().get(AliceAutoReconnect.class);
      if (var2 != null && var2.isActive()) {
         var2.toggle();
         if ((Boolean)this.Jb.get()) {
            this.HG("AliceAutoReconnect disabled");
         }
      }
   }

   @AliceEventHandler(priority = -100)
   public void GD(Pre var1) {
      this.GE(var1);
   }

   private void GE(Pre var1) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.dr == SpawnerProtect.State.GOING_TO_SPAWNERS) {
            this.fA = false;
         }

         if (this.fA) {
            float var2 = this.mc.player.getYaw();
            float var3 = this.mc.player.getPitch();
            float var4 = this.GI(this.BQ - var2);
            float var5 = this.tt - var3;
            float var6 = var2 + Math.signum(var4) * Math.min(Math.abs(var4), 8.0F);
            float var7 = var3 + Math.signum(var5) * Math.min(Math.abs(var5), 8.0F);
            this.mc.player.setYaw(var6);
            this.mc.player.setPitch(var7);
            if (Math.abs(var4) < 1.0F && Math.abs(var5) < 1.0F) {
               this.fA = false;
            }
         }

         if (this.dr == SpawnerProtect.State.GOING_TO_SPAWNERS) {
            this.mc.options.sneakKey.setPressed(true);
         }

         this.aI++;
         if (this.nm == null) {
            this.nm = this.mc.world;
            this.ip = 0;
            this.dr = SpawnerProtect.State.IDLE;
            if ((Boolean)this.Jb.get()) {
               this.HG("SpawnerProtect world initialized - monitoring resumed");
            }
         } else {
            char var13;
            while (var13 == '걖') {
               var13 = '걔';
            }

            if (this.mc.world != this.nm) {
               this.GH();
               return;
            }
         }

         int var10000 = this.dr != SpawnerProtect.State.WORLD_CHANGED_ONCE ? '뢔' : '뢓';

         while (true) {
            switch (var10000) {
               case 47251:
                  return;
               case 47252:
                  var10000 = 47250;
                  break;
               default:
                  if (this.dr == SpawnerProtect.State.WORLD_CHANGED_TWICE) {
                     this.dr = SpawnerProtect.State.IDLE;
                     if ((Boolean)this.Jb.get()) {
                        this.HG("Returned to spawner world - resuming player monitoring");
                     }
                  }

                  if (this.dr == SpawnerProtect.State.IDLE && this.GL()) {
                     this.HJ("IDLE skipped: AliceAutoReconnect login flow active");
                     return;
                  }

                  if (!this.GL() && this.GJ()) {
                     return;
                  }

                  if (this.sc > 0) {
                     this.sc--;
                     return;
                  }

                  switch (this.dr) {
                     case IDLE:
                        this.GK();
                        break;
                     case DISABLE_MODULES:
                        this.GX();
                        break;
                     case SELLING_JUNK:
                        this.GY();
                        break;
                     case WAITING_SELL_GUI:
                        this.GZ();
                        break;
                     case DEPOSITING_SELL_ITEMS:
                        this.Ha();
                        break;
                     case CHECK_PICKAXE:
                        this.Hb();
                        break;
                     case GETTING_PICKAXE:
                        this.Hc();
                        break;
                     case OPENING_ECHEST_PICKUP:
                        this.Hd();
                        break;
                     case TAKING_PICKAXE:
                        this.He();
                        break;
                     case WAITING_PICKAXE_TRANSFER:
                        this.Hf();
                        break;
                     case GOING_TO_SPAWNERS:
                        this.Hi();
                        break;
                     case PICKING_UP_ITEMS:
                        this.Hh();
                        break;
                     case GOING_TO_CHEST:
                        this.Hs();
                        break;
                     case OPENING_CHEST:
                        this.Hv();
                        break;
                     case DEPOSITING_ITEMS:
                        this.Hw();
                        break;
                     case DISCONNECTING:
                        this.HC();
                     case WORLD_CHANGED_ONCE:
                     case WORLD_CHANGED_TWICE:
                  }

                  if (this.dr != SpawnerProtect.State.IDLE && this.dr != SpawnerProtect.State.SELLING_JUNK && this.dr != SpawnerProtect.State.WAITING_SELL_GUI) {
                     if (this.dr != SpawnerProtect.State.DEPOSITING_SELL_ITEMS) {
                        if (this.dr != SpawnerProtect.State.DISCONNECTING
                           && this.dr != SpawnerProtect.State.WORLD_CHANGED_ONCE
                           && this.dr != SpawnerProtect.State.WORLD_CHANGED_TWICE
                           && this.dr != SpawnerProtect.State.DISABLE_MODULES
                           && !this.yx
                           && this.GV()) {
                           this.dg = this.dr;
                           this.dr = SpawnerProtect.State.SELLING_JUNK;
                           this.Hr();
                           this.mc.options.sneakKey.setPressed(false);
                           KeyBinding.setKeyPressed(this.mc.options.forwardKey.getDefaultKey(), false);
                           if ((Boolean)this.Jb.get()) {
                              this.HG("" + (this.zO + 1) + this.uB.get());
                           }
                        }
                     } else {
                        short var9 = 1755;
                     }
                  }

                  return;
            }
         }
      }
   }

   @AliceEventHandler
   public void GF(Receive var1) {
      if (!(var1.packet instanceof BlockUpdateS2CPacket var2)) {
         if (var1.packet instanceof ChunkDeltaUpdateS2CPacket var3) {
            var3.visitUpdates(this::GG);
         }
      } else {
         this.GG(var2.getPos(), var2.getState());
      }
   }

   private void GG(BlockPos var1, BlockState var2) {
      BlockPos var3 = this.vy;
      if (var3 != null && var1 != null && var3.equals(var1)) {
         this.Dw = var2.getBlock() != Blocks.SPAWNER;
      }
   }

   private void GH() {
      this.Hr();
      this.mc.options.sneakKey.setPressed(false);
      this.ip++;
      this.nm = this.mc.world;
      if (this.ip != 1) {
         if (this.ip == 2) {
            this.dr = SpawnerProtect.State.WORLD_CHANGED_TWICE;
            this.ip = 0;
            if ((Boolean)this.Jb.get()) {
               this.HG("World changed (back to spawners) - will resume monitoring");
            }
         }
      } else {
         this.dr = SpawnerProtect.State.WORLD_CHANGED_ONCE;
         if ((Boolean)this.Jb.get()) {
            this.HG("World changed (TP to spawn) - pausing player detection until return");
         }
      }
   }

   private float GI(float var1) {
      var1 %= 360.0F;
      if (var1 >= 180.0F) {
         var1 -= 360.0F;
      }

      if (var1 < -180.0F) {
         var1 += 360.0F;
      }

      return var1;
   }

   private boolean GJ() {
      if ((Integer)this.oR.get() <= 0) {
         return false;
      } else {
         long var1 = this.mc.world.getPlayers().stream().filter(var1x -> var1x != this.mc.player).count();
         if (var1 >= 3L) {
            return false;
         } else {
            for (PlayerEntity var4 : this.mc.world.getPlayers()) {
               int var10000 = var4 == this.mc.player ? 16331 : 16330;

               while (true) {
                  int var8 = var10000;
                  if (var8 == 16330) {
                     if (var4 != null && var4 instanceof AbstractClientPlayerEntity) {
                        String var5 = var4.getName().getString();
                        if (!this.GN(var5)) {
                           double var6 = this.mc.player.distanceTo(var4);
                           if (var6 <= ((Integer)this.oR.get()).intValue() && this.GM()) {
                              if ((Boolean)this.Jb.get()) {
                                 this.HG(var5 + String.format("%.1f", var6));
                              }

                              this.il = true;
                              this.BZ = StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","User "," came too close">(
                                 var5
                              );
                              this.ok = var5;
                              this.EF = System.currentTimeMillis();
                              this.GC();
                              if (!this.gF) {
                                 this.HD();
                                 this.gF = true;
                              }

                              this.HI();
                              if ((Boolean)this.Jb.get()) {
                                 this.HG("Disconnected due to emergency player detection.");
                              }

                              this.toggle();
                              return true;
                           }
                        }
                     }
                     break;
                  }

                  if (var8 != 16331) {
                     break;
                  }

                  var10000 = 16329;
               }
            }

            return false;
         }
      }
   }

   private void GK() {
      long var1 = this.mc.world.getPlayers().stream().filter(var1x -> var1x != this.mc.player).count();
      boolean var3 = this.GM();
      this.HJ("" + var1 + var3 + this.FF.get() + this.Gq.get() + this.oR.get());
      if (var1 >= 3L) {
         this.HJ(var1 + "");
      } else {
         for (PlayerEntity var5 : this.mc.world.getPlayers()) {
            if (var5 != this.mc.player && var5 != null && var5 instanceof AbstractClientPlayerEntity) {
               String var6 = var5.getName().getString();
               double var7 = new Vec3d(this.mc.player.getX(), this.mc.player.getY(), this.mc.player.getZ())
                  .distanceTo(new Vec3d(var5.getX(), var5.getY(), var5.getZ()));
               this.HJ(var6 + String.format("%.1f", var7));
               if (var7 < ((Integer)this.FF.get()).intValue() || var7 > ((Integer)this.Gq.get()).intValue()) {
                  this.HJ(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","Skipped ",": out of range">(var6));
               } else if (this.GN(var6)) {
                  this.HJ(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","Skipped ",": whitelisted">(var6));
               } else {
                  this.ok = var6;
                  this.EF = System.currentTimeMillis();
                  if (this.GM()) {
                     if ((Boolean)this.Jb.get()) {
                        this.HG(String.format("%.1f", var7) + this.ok);
                     }

                     this.GC();
                     this.dr = SpawnerProtect.State.DISABLE_MODULES;
                     if ((Boolean)this.Jb.get()) {
                        this.HG("Player detected! Starting protection sequence...");
                     }
                     break;
                  }

                  this.HJ(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","Skipped ",": no spawners nearby">(var6));
               }
            }
         }
      }
   }

   private boolean GL() {
      AliceAutoReconnect var1 = (AliceAutoReconnect)Modules.get().get(AliceAutoReconnect.class);
      return var1 != null && var1.ab();
   }

   private boolean GM() {
      if (this.mc.player != null && this.mc.world != null) {
         BlockPos var1 = this.mc.player.getBlockPos();
         int var2 = (Integer)this.Dn.get();

         for (BlockPos var4 : BlockPos.iterate(var1.add(-var2, -var2, -var2), var1.add(var2, var2, var2))) {
            if (this.mc.world.getBlockState(var4).getBlock() == Blocks.SPAWNER) {
               return true;
            }
         }

         short var5 = 30275;
         return false;
      } else {
         return false;
      }
   }

   private boolean GN(String var1) {
      return this.Fo.get() && !((List)this.ua.get()).isEmpty() ? ((List)this.ua.get()).stream().anyMatch(var1x -> var1x.equalsIgnoreCase(var1)) : false;
   }

   private boolean GO(Item var1) {
      if (var1 != Items.NETHERITE_PICKAXE) {
         if (var1 != Items.DIAMOND_PICKAXE
            && var1 != Items.IRON_PICKAXE
            && var1 != Items.GOLDEN_PICKAXE
            && var1 != Items.STONE_PICKAXE
            && var1 != Items.WOODEN_PICKAXE) {
            return false;
         }
      } else {
         char var2 = '뼧';
      }

      return true;
   }

   private int GP(Item var1) {
      if (var1 == Items.NETHERITE_PICKAXE) {
         return 6;
      } else {
         int var10000 = var1 != Items.DIAMOND_PICKAXE ? '踟' : '踞';

         while (true) {
            switch (var10000) {
               case 36382:
                  return 5;
               case 36383:
                  var10000 = 36381;
                  break;
               default:
                  if (var1 == Items.IRON_PICKAXE) {
                     return 4;
                  }

                  if (var1 == Items.GOLDEN_PICKAXE) {
                     return 3;
                  }

                  if (var1 == Items.STONE_PICKAXE) {
                     return 2;
                  }

                  var10000 = var1 != Items.WOODEN_PICKAXE ? 16040 : 16039;

                  while (true) {
                     switch (var10000) {
                        case 16039:
                           return 1;
                        case 16040:
                           var10000 = 16038;
                           break;
                        default:
                           return -1;
                     }
                  }
            }
         }
      }
   }

   private int GQ(ItemStack var1) {
      return var1.isDamageable() ? Math.max(0, var1.getMaxDamage() - var1.getDamage()) : Integer.MAX_VALUE;
   }

   private boolean GR(ItemStack var1, int var2, int var3) {
      int var4 = this.GP(var1.getItem());
      return var4 != var2 ? var4 > var2 : var4 >= 0 && this.GQ(var1) > var3;
   }

   private int GS() {
      if (this.mc.player == null) {
         return -1;
      } else {
         int var1 = -1;
         int var2 = -1;
         int var3 = -1;

         for (int var4 = 0; var4 < this.mc.player.getInventory().size(); var4++) {
            ItemStack var5 = this.mc.player.getInventory().getStack(var4);
            if (!var5.isEmpty() && this.GO(var5.getItem()) && this.GR(var5, var2, var3)) {
               var1 = var4;
               var2 = this.GP(var5.getItem());
               var3 = this.GQ(var5);
            }
         }

         return var1;
      }
   }

   private boolean GT() {
      if (this.mc.player == null) {
         return false;
      } else {
         int var1 = this.GS();
         if (var1 < 0) {
            return false;
         } else {
            int var2 = this.mc.player.getInventory().getSelectedSlot();
            if (var1 == var2) {
               return true;
            } else if (var1 > 8) {
               if (var1 >= 36) {
                  return false;
               } else {
                  InvUtils.quickSwap().fromHotbar(var2).to(var1);
                  return true;
               }
            } else {
               return InvUtils.swap(var1, false);
            }
         }
      }
   }

   private boolean GU(ItemStack var1) {
      if (var1.isEmpty()) {
         return true;
      } else {
         return this.GO(var1.getItem()) ? true : var1.getItem() == Items.SPAWNER;
      }
   }

   private boolean GV() {
      if (this.mc.player == null) {
         return false;
      } else {
         for (int var1 = 0; var1 < 36; var1++) {
            ItemStack var2 = this.mc.player.getInventory().getStack(var1);
            if (!var2.isEmpty()) {
               if (!this.GU(var2)) {
                  return true;
               }

               short var3 = 32701;
            }
         }

         return false;
      }
   }

   private boolean GW() {
      return this.GS() != -1;
   }

   private void GX() {
      if (this.mc.currentScreen != null && this.mc.player != null) {
         this.mc.player.closeHandledScreen();
      }

      Module var1 = Modules.get().get(SpawnerOrder.class);
      if (var1 != null && var1.isActive()) {
         var1.toggle();
         if ((Boolean)this.Jb.get()) {
            this.HG("Disabled SpawnerOrder");
         }
      }

      this.dr = SpawnerProtect.State.CHECK_PICKAXE;
      if ((Boolean)this.Jb.get()) {
         this.HG("Modules disabled. Checking for pickaxe...");
      }
   }

   private void GY() {
      if (this.GV()) {
         if (this.mc.currentScreen != null) {
            this.mc.player.closeHandledScreen();
         } else if (this.zO >= (Integer)this.uB.get()) {
            this.yx = true;
            if ((Boolean)this.Jb.get()) {
               this.HG("" + this.uB.get() + this.uB.get());
            }

            if (this.dg != null) {
               this.dr = this.dg;
               this.dg = null;
            } else {
               this.dr = SpawnerProtect.State.CHECK_PICKAXE;
            }
         } else {
            ChatUtils.sendPlayerMsg("/sell");
            this.zO++;
            this.dr = SpawnerProtect.State.WAITING_SELL_GUI;
            this.aI = 0;
            if ((Boolean)this.Jb.get()) {
               this.HG(this.zO + "");
            }
         }
      } else {
         if (this.dg != null) {
            if ((Boolean)this.Jb.get()) {
               this.HG(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","Junk sold. Resuming ","...">(this.dg.name()));
            }

            this.dr = this.dg;
            this.dg = null;
         } else {
            if ((Boolean)this.Jb.get()) {
               this.HG("Inventory clean. Disconnecting...");
            }

            this.dr = SpawnerProtect.State.DISCONNECTING;
         }
      }
   }

   private void GZ() {
      if (this.mc.currentScreen instanceof GenericContainerScreen) {
         this.dr = SpawnerProtect.State.DEPOSITING_SELL_ITEMS;
         this.aI = 0;
      } else {
         if (this.aI > 60) {
            this.dr = SpawnerProtect.State.SELLING_JUNK;
         }
      }
   }

   private void Ha() {
      if (this.mc.currentScreen instanceof GenericContainerScreen var1) {
         ScreenHandler var6 = var1.getScreenHandler();

         for (int var3 = 0; var3 < var6.slots.size(); var3++) {
            Slot var4 = var6.getSlot(var3);
            ItemStack var5 = var4.getStack();
            if (var4.inventory == this.mc.player.getInventory() && !var5.isEmpty() && !this.GU(var5)) {
               this.mc.interactionManager.clickSlot(var6.syncId, var3, 0, SlotActionType.QUICK_MOVE, this.mc.player);
            }
         }

         this.mc.player.closeHandledScreen();
         this.sc = 10;
         if (this.dg != null) {
            if ((Boolean)this.Jb.get()) {
               this.HG("" + this.zO + this.uB.get() + this.dg.name());
            }

            this.dr = this.dg;
            this.dg = null;
         } else {
            if ((Boolean)this.Jb.get()) {
               this.HG("" + this.zO + this.uB.get());
            }

            this.dr = SpawnerProtect.State.CHECK_PICKAXE;
         }
      } else {
         this.dr = SpawnerProtect.State.SELLING_JUNK;
      }
   }

   private void Hb() {
      if (this.GW()) {
         if ((Boolean)this.Jb.get()) {
            this.HG("Pickaxe found. Moving to spawners...");
         }

         this.dr = SpawnerProtect.State.GOING_TO_SPAWNERS;
         this.vy = null;
         this.zl = -1L;
         this.tE = -1L;
         this.ax = -1L;
         this.Dw = false;
         this.fA = false;
         this.Hn(false);
         this.IE.clear();
      } else {
         if ((Boolean)this.Jb.get()) {
            this.HG("No pickaxe found. Getting from ender chest...");
         }

         this.wM = null;
         this.dr = SpawnerProtect.State.GETTING_PICKAXE;
      }
   }

   private void Hc() {
      if (this.wM == null) {
         this.wM = this.Ht();
         if (this.wM == null) {
            if ((Boolean)this.Jb.get()) {
               this.HG("No ender chest found! Trying to mine without pickaxe...");
            }

            this.dr = SpawnerProtect.State.GOING_TO_SPAWNERS;
            return;
         }
      }

      this.Hp(this.wM);
      this.dr = SpawnerProtect.State.OPENING_ECHEST_PICKUP;
      this.yM = 0;
      this.aI = 0;
   }

   private void Hd() {
      if (this.wM == null) {
         this.dr = SpawnerProtect.State.GETTING_PICKAXE;
      } else {
         this.mc.options.sneakKey.setPressed(false);
         KeyBinding.setKeyPressed(this.mc.options.forwardKey.getDefaultKey(), false);
         if (this.yM < 20) {
            this.Hp(this.wM);
         }

         if (this.yM % 5 == 0 && this.mc.interactionManager != null && this.mc.player != null) {
            this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(this.wM), Direction.UP, this.wM, false));
         }

         this.yM++;
         if (this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
            this.dr = SpawnerProtect.State.TAKING_PICKAXE;
            this.aI = 0;
            if ((Boolean)this.Jb.get()) {
               this.HG("Ender chest opened. Looking for pickaxe...");
            }
         }

         if (this.yM > 200) {
            if ((Boolean)this.Jb.get()) {
               Gz("Failed to open ender chest for pickaxe!");
            }

            this.dr = SpawnerProtect.State.GOING_TO_SPAWNERS;
         }
      }
   }

   private void He() {
      if (!(this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var1)) {
         this.dr = SpawnerProtect.State.GETTING_PICKAXE;
      } else {
         int var9 = var1.slots.size();
         int var3 = var9 - 36;
         int var4 = -1;
         int var5 = -1;
         int var6 = -1;

         for (int var7 = 0; var7 < var3; var7++) {
            ItemStack var8 = var1.getSlot(var7).getStack();
            if (!var8.isEmpty() && this.GO(var8.getItem()) && this.GR(var8, var5, var6)) {
               var4 = var7;
               var5 = this.GP(var8.getItem());
               var6 = this.GQ(var8);
            }
         }

         if (var4 != -1) {
            int var10 = this.mc.player.getInventory().getSelectedSlot();

            for (int var11 = 0; var11 < 9; var11++) {
               if (this.mc.player.getInventory().getStack(var11).isEmpty()) {
                  var10 = var11;
                  break;
               }
            }

            this.gX = var1.getRevision();
            this.vn = var10;
            this.on = var5;
            this.DE = 0;
            this.mc.interactionManager.clickSlot(var1.syncId, var4, var10, SlotActionType.SWAP, this.mc.player);
            this.dr = SpawnerProtect.State.WAITING_PICKAXE_TRANSFER;
            if ((Boolean)this.Jb.get()) {
               this.HG(var4 + "");
            }
         } else {
            int var10000 = !this.Jb.get() ? 19072 : 19071;

            label57:
            while (true) {
               switch (var10000) {
                  case 19071:
                     this.HG("No pickaxe found in ender chest!");
                     break label57;
                  case 19072:
                     var10000 = 19070;
                     break;
                  default:
                     break label57;
               }
            }

            this.Hg();
         }
      }
   }

   private void Hf() {
      this.DE++;
      if (!(this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var1)) {
         if (this.GW()) {
            this.Hg();
         } else {
            this.wM = null;
            this.dr = SpawnerProtect.State.GETTING_PICKAXE;
         }
      } else {
         int var10000 = var1.getRevision() == this.gX ? '\uf688' : '\uf687';

         label56:
         while (true) {
            switch (var10000) {
               case 63111:
                  var5 = true;
                  break label56;
               case 63112:
                  var10000 = 63110;
                  break;
               default:
                  var5 = false;
                  break label56;
            }
         }

         boolean var4 = var5;
         boolean var3 = this.vn >= 0 && this.vn < 9 && this.GP(this.mc.player.getInventory().getStack(this.vn).getItem()) >= this.on;
         if (var4 && var3) {
            if ((Boolean)this.Jb.get()) {
               this.HG("Strongest pickaxe moved to hotbar.");
            }

            this.Hg();
         } else if (this.DE > 60) {
            if ((Boolean)this.Jb.get()) {
               this.HG("Pickaxe transfer was not acknowledged; reopening ender chest...");
            }

            this.mc.player.closeHandledScreen();
            this.wM = null;
            this.dr = SpawnerProtect.State.GETTING_PICKAXE;
         }
      }
   }

   private void Hg() {
      if (this.mc.currentScreen != null) {
         this.mc.player.closeHandledScreen();
      }

      this.sc = 5;
      this.dr = SpawnerProtect.State.GOING_TO_SPAWNERS;
      this.vy = null;
      this.zl = -1L;
      this.tE = -1L;
      this.ax = -1L;
      this.Dw = false;
      this.fA = false;
      this.gX = -1;
      this.vn = -1;
      this.on = -1;
      this.DE = 0;
      this.IE.clear();
   }

   private void Hh() {
      if (this.fC == -1L) {
         this.fC = System.currentTimeMillis();
      }

      long var1 = System.currentTimeMillis() - this.fC;
      boolean var3 = false;

      label55:
      for (Entity var5 : this.mc.world.getEntities()) {
         int var10000 = !(var5 instanceof ItemEntity) ? '蘷' : '蘶';

         while (true) {
            switch (var10000) {
               case 34358:
                  ItemEntity var6 = (ItemEntity)var5;
                  if (var6.getStack().getItem() == Items.SPAWNER) {
                     double var7 = this.mc.player.distanceTo(var6);
                     if (var7 < 16.0) {
                        var3 = true;
                        if (!(var7 > 2.0)) {
                           KeyBinding.setKeyPressed(this.mc.options.forwardKey.getDefaultKey(), false);
                        } else {
                           this.Hp(var6.getBlockPos());
                           KeyBinding.setKeyPressed(this.mc.options.forwardKey.getDefaultKey(), true);
                        }
                        break label55;
                     }
                  }
                  continue label55;
               case 34359:
                  var10000 = 34357;
                  break;
               default:
                  continue label55;
            }
         }
      }

      if (!var3 || var1 > 5000L) {
         KeyBinding.setKeyPressed(this.mc.options.forwardKey.getDefaultKey(), false);
         this.fC = -1L;
         BlockPos var9 = this.Ho();
         if (var9 != null) {
            this.vy = null;
            this.dr = SpawnerProtect.State.GOING_TO_SPAWNERS;
            if ((Boolean)this.Jb.get()) {
               this.HG("More spawners found, continuing mining...");
            }
         } else {
            this.wh = true;
            this.dr = SpawnerProtect.State.GOING_TO_CHEST;
            this.wM = null;
            this.aI = 0;
            if ((Boolean)this.Jb.get()) {
               this.HG("All spawners mined! Moving to ender chest...");
            }
         }
      }
   }

   private void Hi() {
      this.mc.options.sneakKey.setPressed(true);
      if (this.vy != null) {
         if (this.Dw) {
            if ((Boolean)this.Jb.get()) {
               this.HG(this.vy + "");
            }

            this.Hr();
            this.vy = null;
            this.tE = -1L;
            this.ax = -1L;
            this.Dw = false;
            this.zl = -1L;
            return;
         }

         long var1 = this.ax >= 0L ? this.ax : this.tE;
         if (var1 != -1L && System.currentTimeMillis() - var1 > ((Integer)this.yu.get()).intValue()) {
            if ((Boolean)this.Jb.get()) {
               this.HG("" + this.vy + this.yu.get());
            }

            this.IE.add(this.vy);
            this.vy = null;
            this.tE = -1L;
            this.ax = -1L;
            this.Dw = false;
            this.Hr();
            return;
         }

         if (this.mc.world.getBlockState(this.vy).getBlock() != Blocks.SPAWNER) {
            this.Hm();
            return;
         }
      }

      if (this.vy == null) {
         this.vy = this.Ho();
         if (this.vy == null) {
            if (this.zl != -1L) {
               long var4 = System.currentTimeMillis() - this.zl;
               if (var4 >= ((Integer)this.mc.get()).intValue()) {
                  this.IE.clear();
                  this.Hr();
                  this.mc.options.sneakKey.setPressed(false);
                  this.fC = -1L;
                  this.dr = SpawnerProtect.State.PICKING_UP_ITEMS;
                  this.zl = -1L;
                  if ((Boolean)this.Jb.get()) {
                     this.HG("All spawners mined! Picking up dropped items...");
                  }

                  return;
               }

               return;
            }

            this.zl = System.currentTimeMillis();
            if ((Boolean)this.Jb.get()) {
               this.HG(this.mc.get() + "");
            }

            return;
         }

         this.tE = System.currentTimeMillis();
         this.ax = -1L;
         this.Dw = false;
         this.Hn(false);
         if ((Boolean)this.Jb.get()) {
            this.HG(
               this.vy
                  + String.format("%.1f", Math.sqrt(this.vy.getSquaredDistance(new Vec3d(this.mc.player.getX(), this.mc.player.getY(), this.mc.player.getZ()))))
            );
         }
      }

      this.zl = -1L;
      if (!this.GT()) {
         this.Hr();
         this.dr = SpawnerProtect.State.GOING_TO_CHEST;
         if ((Boolean)this.Jb.get()) {
            this.HG("No usable pickaxe found, moving to ender chest...");
         }
      } else {
         BlockHitResult var3 = this.Hj(this.vy);
         if (var3 == null) {
            this.Hm();
         } else if (!this.Hk(var3)) {
            this.Hm();
         } else {
            if (this.ax < 0L) {
               this.ax = System.currentTimeMillis();
            }

            this.mc.options.attackKey.setPressed(false);
            if (this.mc.interactionManager.updateBlockBreakingProgress(this.vy, var3.getSide())) {
               this.mc.world.spawnBlockBreakingParticle(this.vy, var3.getSide());
               this.mc.player.swingHand(Hand.MAIN_HAND);
            }
         }
      }
   }

   private BlockHitResult Hj(BlockPos var1) {
      if (this.mc.player != null && this.mc.world != null && var1 != null) {
         Vec3d var2 = this.mc.player.getEyePos();
         Vec3d var3 = Vec3d.ofCenter(var1);
         double var4 = this.mc.player.getBlockInteractionRange();
         int var10000 = var2.squaredDistanceTo(var3) > (var4 + 0.75) * (var4 + 0.75) ? '꼠' : '꼡';

         while (true) {
            int var8 = var10000;
            if (var8 == 44832) {
               return null;
            }

            if (var8 != 44833) {
               if (this.mc.world.raycast(new RaycastContext(var2, var3, ShapeType.OUTLINE, FluidHandling.NONE, this.mc.player)) instanceof BlockHitResult var7
                  && var7.getBlockPos().equals(var1)) {
                  return var7;
               }

               return null;
            }

            var10000 = 44831;
         }
      } else {
         return null;
      }
   }

   private boolean Hk(BlockHitResult var1) {
      BlockPos var2 = var1.getBlockPos().toImmutable();
      Direction var3 = var1.getSide();
      if (!var2.equals(this.EP) || var3 != this.ih) {
         this.EP = var2;
         this.ih = var3;
         this.bH = this.mc.player.getYaw();
         this.nY = this.mc.player.getPitch();
         this.Cn = false;
         this.EB = true;
      }

      if (this.Cn) {
         return true;
      } else {
         Vec3d var4 = var1.getPos().subtract(this.mc.player.getEyePos());
         double var5 = Math.sqrt(var4.x * var4.x + var4.z * var4.z);
         float var7 = (float)Math.toDegrees(Math.atan2(-var4.x, var4.z));
         float var8 = (float)(-Math.toDegrees(Math.atan2(var4.y, var5)));
         float var9 = this.GI(var7 - this.bH);
         float var10 = var8 - this.nY;
         this.bH = this.bH + Math.signum(var9) * Math.min(Math.abs(var9), 8.0F);
         this.nY = this.nY + Math.signum(var10) * Math.min(Math.abs(var10), 8.0F);
         if (Math.abs(var9) <= 8.0F && Math.abs(var10) <= 8.0F) {
            this.bH = var7;
            this.nY = var8;
            this.Cn = true;
         }

         this.Hl(this.bH, this.nY);
         return false;
      }
   }

   private void Hl(float var1, float var2) {
      if (this.mc.player != null && this.mc.player.networkHandler != null) {
         this.mc.player.networkHandler.sendPacket(new LookAndOnGround(var1, var2, this.mc.player.isOnGround(), false));
      }
   }

   private void Hm() {
      this.mc.options.attackKey.setPressed(false);
   }

   private void Hn(boolean var1) {
      if (var1 && this.EB && this.mc.player != null) {
         if (this.mc.player.networkHandler != null) {
            this.Hl(this.mc.player.getYaw(), this.mc.player.getPitch());
         } else {
            short var2 = 28713;
         }
      }

      this.EP = null;
      this.ih = null;
      this.Cn = false;
      this.EB = false;
   }

   private BlockPos Ho() {
      BlockPos var1 = this.mc.player.getBlockPos();
      BlockPos var2 = null;
      double var3 = Double.MAX_VALUE;
      double var5 = (Integer)this.Dn.get() * (Integer)this.Dn.get();

      for (BlockPos var8 : BlockPos.iterate(
         var1.add(-(Integer)this.Dn.get(), -(Integer)this.Dn.get(), -(Integer)this.Dn.get()),
         var1.add((Integer)this.Dn.get(), (Integer)this.Dn.get(), (Integer)this.Dn.get())
      )) {
         if (this.mc.world.getBlockState(var8).getBlock() == Blocks.SPAWNER && !this.IE.contains(var8)) {
            double var9 = var8.getSquaredDistance(new Vec3d(this.mc.player.getX(), this.mc.player.getY(), this.mc.player.getZ()));
            if (!(var9 > var5) && var9 < var3) {
               var3 = var9;
               var2 = var8.toImmutable();
            }
         }
      }

      return var2;
   }

   private void Hp(BlockPos var1) {
      this.Hq(var1, Direction.UP);
   }

   private void Hq(BlockPos var1, Direction var2) {
      Vec3d var3 = Vec3d.ofCenter(var1).add(Vec3d.of(var2.getVector()).multiply(0.5));
      Vec3d var4 = this.mc.player.getEyePos();
      Vec3d var5 = var3.subtract(var4).normalize();
      this.BQ = (float)Math.toDegrees(Math.atan2(-var5.x, var5.z));
      this.tt = (float)Math.toDegrees(-Math.asin(var5.y));
      this.fA = true;
   }

   private void Hr() {
      this.Hm();
      if (this.mc.interactionManager != null) {
         this.mc.interactionManager.cancelBlockBreaking();
      }

      this.Hn(true);
   }

   private void Hs() {
      if (!(Boolean)this.K.get()) {
         this.dr = SpawnerProtect.State.DISCONNECTING;
         if ((Boolean)this.Jb.get()) {
            this.HG("Deposit to ender chest disabled, disconnecting...");
         }
      } else {
         if (this.wM == null) {
            this.wM = this.Ht();
            if (this.wM == null) {
               if ((Boolean)this.Jb.get()) {
                  this.HG("No ender chest found nearby!");
               }

               this.dr = SpawnerProtect.State.DISCONNECTING;
               return;
            }

            if ((Boolean)this.Jb.get()) {
               this.HG(this.wM + "");
            }
         }

         this.Hu(this.wM);
         if (this.mc.player.getBlockPos().getSquaredDistance(this.wM) <= 9.0) {
            this.dr = SpawnerProtect.State.OPENING_CHEST;
            this.py = 0;
            if ((Boolean)this.Jb.get()) {
               this.HG("Reached ender chest. Attempting to open...");
            }
         }

         if (this.aI > 600) {
            if ((Boolean)this.Jb.get()) {
               Gz("Timed out trying to reach ender chest!");
            }

            this.dr = SpawnerProtect.State.DISCONNECTING;
         }
      }
   }

   private BlockPos Ht() {
      BlockPos var1 = this.mc.player.getBlockPos();
      BlockPos var2 = null;
      double var3 = Double.MAX_VALUE;

      for (BlockPos var6 : BlockPos.iterate(var1.add(-16, -8, -16), var1.add(16, 8, 16))) {
         if (this.mc.world.getBlockState(var6).getBlock() == Blocks.ENDER_CHEST) {
            double var7 = var6.getSquaredDistance(new Vec3d(this.mc.player.getX(), this.mc.player.getY(), this.mc.player.getZ()));
            if (var7 < var3) {
               var3 = var7;
               var2 = var6.toImmutable();
            }
         }
      }

      return var2;
   }

   private void Hu(BlockPos var1) {
      Vec3d var2 = new Vec3d(this.mc.player.getX(), this.mc.player.getY(), this.mc.player.getZ());
      Vec3d var3 = Vec3d.ofCenter(var1);
      Vec3d var4 = var3.subtract(var2).normalize();
      double var5 = Math.toDegrees(Math.atan2(-var4.x, var4.z));
      this.mc.player.setYaw((float)var5);
      KeyBinding.setKeyPressed(this.mc.options.forwardKey.getDefaultKey(), true);
   }

   private void Hv() {
      if (this.wM == null) {
         this.dr = SpawnerProtect.State.GOING_TO_CHEST;
      } else {
         this.mc.options.sneakKey.setPressed(false);
         KeyBinding.setKeyPressed(this.mc.options.forwardKey.getDefaultKey(), false);
         KeyBinding.setKeyPressed(this.mc.options.jumpKey.getDefaultKey(), true);
         if (this.py < 20) {
            this.Hp(this.wM);
         }

         if (this.py % 5 == 0 && this.mc.interactionManager != null && this.mc.player != null) {
            this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(this.wM), Direction.UP, this.wM, false));
            if ((Boolean)this.Jb.get()) {
               this.HG(this.py / 5 + 1 + "");
            }
         }

         this.py++;
         if (this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
            KeyBinding.setKeyPressed(this.mc.options.jumpKey.getDefaultKey(), false);
            this.dr = SpawnerProtect.State.DEPOSITING_ITEMS;
            this.bM = -1;
            this.aI = 0;
            if ((Boolean)this.Jb.get()) {
               this.HG("Ender chest opened successfully! Made by GLZD ");
            }
         }

         int var10000 = this.py <= 200 ? '軝' : '軜';

         while (true) {
            switch (var10000) {
               case 36572:
                  KeyBinding.setKeyPressed(this.mc.options.jumpKey.getDefaultKey(), false);
                  if ((Boolean)this.Jb.get()) {
                     Gz("Failed to open ender chest after multiple attempts!");
                  }

                  this.dr = SpawnerProtect.State.DISCONNECTING;
                  return;
               case 36573:
                  var10000 = 36571;
                  break;
               default:
                  return;
            }
         }
      }
   }

   private void Hw() {
      if ((Boolean)this.K.get()) {
         this.mc.options.sneakKey.setPressed(false);
         if (!(this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var1)) {
            this.dr = SpawnerProtect.State.OPENING_CHEST;
            this.py = 0;
         } else {
            if (!this.Hy()) {
               this.Fj = true;
               if ((Boolean)this.Jb.get()) {
                  this.HG("All spawners deposited!");
               }

               this.mc.player.closeHandledScreen();
               this.sc = 10;
               this.dr = SpawnerProtect.State.DISCONNECTING;
               return;
            }

            this.Hz(var1);
         }

         if (this.aI > 900) {
            if ((Boolean)this.Jb.get()) {
               Gz("Timed out depositing items!");
            }

            this.dr = SpawnerProtect.State.DISCONNECTING;
         }
      } else {
         this.dr = SpawnerProtect.State.DISCONNECTING;
         if ((Boolean)this.Jb.get()) {
            this.HG("Deposit to ender chest disabled, skipping deposit.");
         }
      }
   }

   private boolean Hx(ItemStack var1) {
      return var1.isEmpty() || var1.getItem() == Items.AIR ? true : var1.getItem() != Items.SPAWNER;
   }

   private boolean Hy() {
      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = this.mc.player.getInventory().getStack(var1);
         if (!var2.isEmpty() && !this.Hx(var2)) {
            return true;
         }
      }

      return false;
   }

   private void Hz(GenericContainerScreenHandler var1) {
      int var2 = var1.slots.size();
      int var3 = var2 - 36;
      int var4 = var3;
      if (this.HB(var1, var3)) {
         if ((Boolean)this.Jb.get()) {
            this.HH("Ender chest is full! Disconnecting for safety.");
         }

         this.dr = SpawnerProtect.State.DISCONNECTING;
      } else {
         for (int var5 = 0; var5 < 36; var5++) {
            int var6 = var4 + var5;
            ItemStack var7 = var1.getSlot(var6).getStack();
            if (!var7.isEmpty() && var7.getItem() == Items.SPAWNER) {
               this.HA(var1, var6, var7);
               return;
            }
         }

         if (this.bM >= var4) {
            this.bM = var4 - 1;
            this.sc = 3;
         }
      }
   }

   private void HA(GenericContainerScreenHandler var1, int var2, ItemStack var3) {
      if ((Boolean)this.Jb.get()) {
         this.HG(var2 + var3.getItem().toString());
      }

      if (this.mc.interactionManager != null) {
         this.mc.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.QUICK_MOVE, this.mc.player);
      }

      this.bM = var2;
      this.sc = 2;
   }

   private boolean HB(GenericContainerScreenHandler var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         if (var1.getSlot(var3).getStack().isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private void HC() {
      KeyBinding.setKeyPressed(this.mc.options.forwardKey.getDefaultKey(), false);
      KeyBinding.setKeyPressed(this.mc.options.jumpKey.getDefaultKey(), false);
      this.mc.options.sneakKey.setPressed(false);
      this.GC();
      if (!this.gF) {
         this.HD();
         this.gF = true;
      }

      if (this.il) {
         if ((Boolean)this.Jb.get()) {
            this.HG(
               StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","SpawnerProtect: ",". Successfully disconnected.">(
                  this.BZ
               )
            );
         }
      } else if ((Boolean)this.Jb.get()) {
         this.HG(
            StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","SpawnerProtect: "," detected. Successfully disconnected.">(
               this.ok
            )
         );
      }

      this.HI();
      if ((Boolean)this.Jb.get()) {
         this.HG("Disconnected due to player detection.");
      }

      this.toggle();
   }

   private void HD() {
      if ((Boolean)this.He.get() && this.C.get() != null && !((String)this.C.get()).trim().isEmpty()) {
         String var1 = ((String)this.C.get()).trim();
         long var2 = this.EF / 1000L;
         String var4 = "";
         if ((Boolean)this.oj.get() && this.qa.get() != null && !((String)this.qa.get()).trim().isEmpty()) {
            var4 = String.format("<@%s>", ((String)this.qa.get()).trim());
         }

         String var5 = this.HE(var4, var2);
         new Thread(
               () -> {
                  HttpClient var3 = HttpClient.newHttpClient();
                  HttpRequest var4x = HttpRequest.newBuilder()
                     .uri(URI.create(var1))
                     .header("Content-Type", "application/json")
                     .POST(BodyPublishers.ofString(var5))
                     .build();
                  HttpResponse var5x = var3.send(var4x, BodyHandlers.ofString());
                  if (var5x.statusCode() >= 200 && var5x.statusCode() < 300) {
                     if ((Boolean)this.Jb.get()) {
                        this.HG("Webhook notification sent successfully!");
                     } else {
                        char var6x = '\uf881';
                     }
                  } else if ((Boolean)this.Jb.get()) {
                     Gz(var5x.statusCode() + "");
                  }
               }
            )
            .start();
      } else {
         int var10000 = !this.Jb.get() ? 24991 : 24990;

         while (true) {
            int var6 = var10000;
            if (var6 == 24990) {
               this.HG("Webhook disabled or URL not configured.");
               break;
            }

            if (var6 != 24991) {
               break;
            }

            var10000 = 24989;
         }
      }
   }

   private String HE(String var1, long var2) {
      String var4 = this.il ? "SpawnerProtect Emergency Alert" : "SpawnerProtect Alert";
      String var5;
      if (!this.il) {
         var5 = String.format(
            "**Player Detected:** %s\\n**Detection Time:** <t:%d:R>\\n**Spawners Mined:** %s\\n**Items Deposited:** %s\\n**Disconnected:** Yes",
            this.HF(this.ok),
            var2,
            !this.wh ? "❌ Failed" : "✅ Success",
            this.Fj ? "✅ Success" : "❌ Failed"
         );
      } else {
         var5 = String.format(
            "**Player Detected:** %s\\n**Detection Time:** <t:%d:R>\\n**Reason:** %s\\n**Disconnected:** Yes", this.HF(this.ok), var2, this.HF(this.BZ)
         );
      }

      int var6 = this.il ? 16711680 : 16766720;
      return String.format(
         "{\n    \"username\": \"ALICE Webhook\",\n    \"avatar_url\": \"https://i.imgur.com/OL2y1cr.png\",\n    \"content\": \"%s\",\n    \"embeds\": [{\n        \"title\": \"%s\",\n        \"description\": \"%s\",\n        \"color\": %d,\n        \"timestamp\": \"%s\",\n        \"footer\": {\n            \"text\": \"Sent by ALICE\"\n        }\n    }]\n}",
         this.HF(var1),
         var4,
         var5,
         var6,
         Instant.now().toString()
      );
   }

   private String HF(String var1) {
      return var1 == null ? "" : var1.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
   }

   private void HG(String var1) {
      if ((Boolean)this.Jb.get()) {
         Gz(var1);
      }
   }

   private void HH(String var1) {
      if ((Boolean)this.Jb.get()) {
         Gz(var1);
      }
   }

   private void HI() {
      if (this.mc.getNetworkHandler() != null) {
         this.mc.getNetworkHandler().getConnection().disconnect(Text.literal("SpawnerProtect"));
      }
   }

   private void HJ(String var1) {
      long var2 = System.currentTimeMillis();
      if (var2 - this.tH > 1000L) {
         this.tH = var2;
         Gz(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","[SP-DEBUG] ">(var1));
      }
   }

   @Override
   public void onDeactivate() {
      super.onDeactivate();
      this.Hr();
      KeyBinding.setKeyPressed(this.mc.options.forwardKey.getDefaultKey(), false);
      KeyBinding.setKeyPressed(this.mc.options.jumpKey.getDefaultKey(), false);
   }

   public static void init() {
      if (!tG) {
         tG = true;
      }
   }

   private static enum State {
      IDLE,
      DISABLE_MODULES,
      SELLING_JUNK,
      WAITING_SELL_GUI,
      DEPOSITING_SELL_ITEMS,
      CHECK_PICKAXE,
      GETTING_PICKAXE,
      OPENING_ECHEST_PICKUP,
      TAKING_PICKAXE,
      WAITING_PICKAXE_TRANSFER,
      GOING_TO_SPAWNERS,
      PICKING_UP_ITEMS,
      GOING_TO_CHEST,
      OPENING_CHEST,
      DEPOSITING_ITEMS,
      DISCONNECTING,
      WORLD_CHANGED_ONCE,
      WORLD_CHANGED_TWICE;
   }
}
