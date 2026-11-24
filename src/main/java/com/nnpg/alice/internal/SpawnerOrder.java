package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.StringConcatFactory;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.IntSetting.Builder;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class SpawnerOrder extends AliceModule {
   private final SettingGroup lK = this.settings.getDefaultGroup();
   private final Setting<Integer> Ae = this.lK
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("drop-click-count")).description("so lan click vao dispenser moi spawner")).defaultValue(2))
            .min(1)
            .max(10)
            .build()
      );
   private final Setting<Boolean> AS = this.lK
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("fast-mode"))
                  .description("dung logic che do nhanh cho GUI"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> dS = this.lK
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("show-hud"))
                  .description("hien thi bang thong ke HUD"))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> uw = this.lK
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("player-trigger-mode"))
                  .description("dung /order <player> thay vi /order <item>. khong check gia, chi giao dung item."))
               .defaultValue(false))
            .build()
      );
   private final Setting<String> ra = this.lK
      .add(
         ((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)((meteordevelopment.meteorclient.settings.StringSetting.Builder)new meteordevelopment.meteorclient.settings.StringSetting.Builder()
                        .name("trigger-player-name"))
                     .description("ten player de gui lenh /order <player>"))
                  .defaultValue(""))
               .visible(this.uw::get))
            .build()
      );
   private final Queue<Entry<Item, String>> mf = new LinkedList<>();
   private Item xy = Items.AIR;
   private String cP = "";
   private boolean fq = false;
   private static final Map<Item, String> va = Map.of(
      Items.LEATHER, "leather", Items.IRON_INGOT, "iron", Items.BONE, "bone", Items.GUNPOWDER, "gunpowder", Items.BLAZE_ROD, "blaze rod"
   );
   private SpawnerOrder.State cl = SpawnerOrder.State.WAIT_CLEANUP;
   private SpawnerOrder.State kU = SpawnerOrder.State.WAIT_CLEANUP;
   private int eB = 0;
   private int zy = 0;
   private int dc = 0;
   private int wH = 3;
   private int u = 0;
   private final List<BlockPos> qy = new ArrayList<>();
   private int Jg = 0;
   private BlockPos BK = null;
   private int hA = 0;
   private int pP = 0;
   private static final int sK = 20;
   private static final int sP = 60;
   private float EO = 0.0F;
   private float tJ = 0.0F;
   private float hO = 0.0F;
   private float Az = 0.0F;
   private int hX = 0;
   private static final int vM = 3;
   private int AA = 0;
   private int aq = 0;
   private static final int ni = 200;
   private int bQ = 0;
   private int yY = 0;
   private int xP = 0;
   private static final int Gm = 1;
   private static final int ys = 1;
   private static final int HC = 40;
   private int mM = 0;
   private boolean ao = false;
   private static SpawnerOrder db;
   public long uC = 0L;
   public double hI = 0.0;
   public int GN = 0;
   private String az = "";
   private static final Random Gt = new Random();
   public boolean mI = false;
   private static final Color Gs = new Color(120, 220, 255, 255);
   private static final Color IW = new Color(255, 255, 255, 255);
   private static final Color pB = new Color(120, 255, 120, 255);
   private static boolean rD = false;
   private static Method bX = null;
   private static boolean Ek = false;

   public static Lookup getLookup() {
      return MethodHandles.lookup();
   }

   private boolean FB() {
      return true;
   }

   public SpawnerOrder() {
      super(AliceAddon.CATEGORY, "Spawner-Order", "Tu dong vut vat pham tu spawner va dat don cho cac vat pham cung loai trong tui do.");
   }

   @Override
   public void onActivate() {
      super.onActivate();
      db = this;
      this.AS.set(true);
      this.FP();
      this.uC = System.currentTimeMillis();
      this.hI = 0.0;
      this.GN = 0;
      this.mM = 0;
      this.ao = false;
   }

   @Override
   public void onDeactivate() {
      super.onDeactivate();
      db = null;
      if (this.mc.currentScreen != null) {
         this.mc.player.closeHandledScreen();
      }
   }

   @AliceEventHandler
   public void FC(ReceiveMessageEvent var1) {
      if (this.isActive() && !this.mI) {
         String var2 = var1.getMessage().getString();
         if (var2.contains("Số lượng giao vượt quá số lượng còn lại")) {
            if (this.mc.currentScreen instanceof GenericContainerScreen var8) {
               GenericContainerScreenHandler var9 = (GenericContainerScreenHandler)var8.getScreenHandler();
               boolean var5 = false;

               for (int var6 = 0; var6 < var9.slots.size(); var6++) {
                  ItemStack var7 = ((Slot)var9.slots.get(var6)).getStack();
                  if (!var7.isEmpty() && var7.getItem() == this.xy) {
                     var5 = true;
                     break;
                  }
               }

               if (var5) {
                  this.mc.player.closeHandledScreen();
                  this.cl = SpawnerOrder.State.ORDER_WAIT_CONFIRM;
                  this.AA = this.FH() * 3;
                  this.aq = 0;
               } else {
                  this.mc.player.closeHandledScreen();
                  this.AA = this.FH() * 3;
                  this.aq = 0;
                  if (this.FG()) {
                     this.cl = SpawnerOrder.State.ORDER_SEND;
                  } else {
                     this.FN();
                  }
               }
            }
         } else if (!var2.contains("Có lỗi xảy ra khi giao hàng") && !var2.contains("Co loi xay ra khi giao hang")) {
            if (var2.contains("nhận được") || var2.contains("nhan duoc")) {
               Matcher var3 = Pattern.compile("\\+\\$\\s*([0-9]+\\.?[0-9]*[KkMmBb]?)").matcher(var2);
               if (var3.find()) {
                  this.hI = this.hI + this.FQ(var3.group(1));
               }

               this.GN++;
            }
         } else {
            if (this.mc.currentScreen != null) {
               this.mc.player.closeHandledScreen();
            }

            this.cl = SpawnerOrder.State.WAITING_CYCLE;
            this.dc = 0;
            this.AA = 0;
            this.aq = 0;
         }
      }
   }

   private boolean FD() {
      if (this.mc.world != null && this.mc.player != null) {
         Box var1 = new Box(this.mc.player.getBlockPos()).expand(2.0);
         List var2 = this.mc.world.getEntitiesByClass(ItemEntity.class, var1, var0 -> true);
         return !var2.isEmpty();
      } else {
         return false;
      }
   }

   private boolean FE(Item var1) {
      if (var1 == Items.AIR) {
         return true;
      } else {
         for (int var2 = 0; var2 < this.mc.player.getInventory().size(); var2++) {
            ItemStack var3 = this.mc.player.getInventory().getStack(var2);
            if (!var3.isEmpty() && var3.getItem() == var1) {
               return true;
            }
         }

         return false;
      }
   }

   private void FF() {
      this.mf.clear();
      this.fq = false;

      for (int var1 = 0; var1 < 36; var1++) {
         ItemStack var2 = this.mc.player.getInventory().getStack(var1);
         if (!var2.isEmpty()) {
            Item var3 = var2.getItem();
            if (va.containsKey(var3)) {
               if (!this.mf.stream().anyMatch(var1x -> var1x.getKey() == var3)) {
                  this.mf.add(new SimpleEntry<>(var3, va.get(var3)));
               }
            } else if (var3.getMaxCount() > 1) {
               this.fq = true;
            }
         }
      }
   }

   private boolean FG() {
      while (!this.mf.isEmpty()) {
         Entry var1 = this.mf.poll();
         if (this.FE((Item)var1.getKey())) {
            this.xy = (Item)var1.getKey();
            this.cP = (String)var1.getValue();
            return true;
         }
      }

      return false;
   }

   private long getPing() {
      if (this.mc.getNetworkHandler() == null) {
         return 0L;
      } else {
         PlayerListEntry var1 = this.mc.getNetworkHandler().getPlayerListEntry(this.mc.player.getUuid());
         return var1 != null ? var1.getLatency() : 0L;
      }
   }

   private int FH() {
      long var1 = this.getPing();
      if (var1 < 50L) {
         return 2;
      } else {
         int var10000 = var1 < 100L ? 'ꂰ' : 'ꂱ';

         while (true) {
            int var3 = var10000;
            if (var3 == 41136) {
               return 3;
            }

            if (var3 != 41137) {
               if (var1 >= 200L) {
                  if (var1 < 300L) {
                     return 8;
                  }

                  return 12;
               }

               return 5;
            }

            var10000 = 41135;
         }
      }
   }

   private float FI(BlockPos var1) {
      double var2 = Vec3d.ofCenter(var1).x - this.mc.player.getX();
      double var4 = Vec3d.ofCenter(var1).z - this.mc.player.getZ();
      return (float)Math.toDegrees(Math.atan2(var4, var2)) - 90.0F;
   }

   private float FJ(BlockPos var1) {
      double var2 = Vec3d.ofCenter(var1).x - this.mc.player.getX();
      double var4 = Vec3d.ofCenter(var1).y - this.mc.player.getEyeY();
      double var6 = Vec3d.ofCenter(var1).z - this.mc.player.getZ();
      return (float)(-Math.toDegrees(Math.atan2(var4, Math.sqrt(var2 * var2 + var6 * var6))));
   }

   private void FK(float var1, float var2) {
      this.mc.player.networkHandler.sendPacket(new LookAndOnGround(var1, var2, this.mc.player.isOnGround(), false));
   }

   private void FL(BlockPos var1) {
      Vec3d var2 = Vec3d.ofCenter(var1);
      Vec3d var3 = new Vec3d(this.mc.player.getX(), this.mc.player.getY(), this.mc.player.getZ());
      double var4 = var3.x - var2.x;
      double var6 = var3.y - var2.y;
      double var8 = var3.z - var2.z;
      double var10 = Math.abs(var4);
      double var12 = Math.abs(var6);
      double var14 = Math.abs(var8);
      Direction var16;
      if (var12 >= var10 && var12 >= var14) {
         var16 = var6 > 0.0 ? Direction.UP : Direction.DOWN;
      } else if (var10 >= var14) {
         var16 = !(var4 > 0.0) ? Direction.WEST : Direction.EAST;
      } else {
         var16 = var8 > 0.0 ? Direction.SOUTH : Direction.NORTH;
      }

      this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, new BlockHitResult(var2, var16, var1, false));
   }

   private void FM() {
      this.FF();
      this.AA = this.FB() ? this.FH() : this.FH() * 3;
      this.aq = 0;
      this.bQ = 0;
      if (this.fq) {
         this.cl = SpawnerOrder.State.SELL_SEND;
      } else if (!this.FG()) {
         this.FN();
      } else {
         this.cl = SpawnerOrder.State.ORDER_SEND;
      }
   }

   private void FN() {
      this.cl = SpawnerOrder.State.WAIT_CLEANUP;
   }

   private void FO() {
      this.hA = 0;
      this.hX = 0;
      this.yY = 0;
      this.xP = 0;
      this.mf.clear();
      this.Jg++;
      if (this.Jg >= this.qy.size()) {
         this.cl = SpawnerOrder.State.WAITING_CYCLE;
         this.dc = 0;
      } else {
         this.BK = this.qy.get(this.Jg);
         this.cl = SpawnerOrder.State.MOVING;
         this.pP = 0;
      }
   }

   private void FP() {
      this.qy.clear();
      this.Jg = 0;
      this.BK = null;
      this.hA = 0;
      this.hX = 0;
      this.pP = 0;
      this.eB = 0;
      this.AA = 0;
      this.aq = 0;
      this.bQ = 0;
      this.yY = 0;
      this.xP = 0;
      this.u = 0;
      this.mf.clear();
      this.xy = Items.AIR;
      this.cP = "";
      this.fq = false;
      this.zy = 0;
      this.cl = SpawnerOrder.State.WAIT_CLEANUP;
      this.kU = SpawnerOrder.State.WAIT_CLEANUP;
      this.u = 0;
   }

   private double FQ(String var1) {
      var1 = var1.trim();
      Matcher var2 = Pattern.compile("([0-9]+\\.?[0-9]*)([KkMmBb]?)").matcher(var1);
      if (!var2.find()) {
         return 0.0;
      } else {
         double var3 = Double.parseDouble(var2.group(1));
         String var5 = var2.group(2).toUpperCase();
         byte var6 = -1;
         int var7 = var5.hashCode() ^ 2083360553;
         if (var7 != 2083360610) {
            if (var7 == 2083360612) {
               if (var5.equals("M")) {
                  var6 = 1;
               }
            } else if (var7 == 2083360619 && var5.equals("B")) {
               var6 = 2;
            }
         } else if (var5.equals("K")) {
            var6 = 0;
         }

         return var6 == 0 ? var3 * 1000.0 : (var6 == 1 ? var3 * 1000000.0 : (var6 == 2 ? var3 * 1.0E9 : var3));
      }
   }

   private LoreComponent getLore(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         for (Method var5 : var1.getClass().getMethods()) {
            if ((var5.getName().equals("get") || var5.getName().equals("method_57824")) && var5.getParameterCount() == 1) {
               Class var6 = var5.getParameterTypes()[0];
               if (var6.isInstance(DataComponentTypes.LORE) && var5.invoke(var1, DataComponentTypes.LORE) instanceof LoreComponent var8) {
                  return var8;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private double getPriceFromSlot(GenericContainerScreen var1, int var2) {
      GenericContainerScreenHandler var3 = (GenericContainerScreenHandler)var1.getScreenHandler();
      if (var2 >= 0 && var2 < var3.slots.size()) {
         ItemStack var4 = ((Slot)var3.slots.get(var2)).getStack();
         if (var4.isEmpty()) {
            return 0.0;
         } else {
            LoreComponent var5 = this.getLore(var4);
            if (var5 != null) {
               for (Text var7 : var5.lines()) {
                  String var8 = var7.getString();
                  if (var8.contains("$")) {
                     double var9 = this.FQ(var8.replace("$", "").trim());
                     if (var9 > 0.0) {
                        return var9;
                     }

                     char var11;
                     while (var11 == '遂') {
                        var11 = '遀';
                     }
                  }
               }
            }

            String var12 = var4.getName().getString();
            if (var12.contains("$")) {
               double var13 = this.FQ(var12.replace("$", "").trim());
               if (var13 > 0.0) {
                  return var13;
               }
            }

            return 0.0;
         }
      } else {
         return 0.0;
      }
   }

   private boolean FR(Item var1) {
      if (var1 != null && var1 != Items.AIR) {
         String var2 = var1.getTranslationKey().toLowerCase();
         return var2.contains("glass_pane") || var2.contains("barrier") || var2.contains("structure_void");
      } else {
         return true;
      }
   }

   private boolean FS(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         Item var2 = var1.getItem();
         if (var2 != Items.ARROW && var2 != Items.FEATHER && var2 != Items.PAPER) {
            String var3 = var1.getName().getString().toLowerCase();
            return var3.contains("next") || var3.contains("sau") || var3.contains("trang") || var3.contains("»");
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private int getRemainingFromSlot(GenericContainerScreen var1, int var2) {
      GenericContainerScreenHandler var3 = (GenericContainerScreenHandler)var1.getScreenHandler();
      if (var2 >= 0 && var2 < var3.slots.size()) {
         ItemStack var4 = ((Slot)var3.slots.get(var2)).getStack();
         if (var4.isEmpty()) {
            return 0;
         } else {
            LoreComponent var5 = this.getLore(var4);
            if (var5 == null) {
               return Integer.MAX_VALUE;
            } else {
               for (Text var7 : var5.lines()) {
                  String var8 = var7.getString();
                  Matcher var9 = Pattern.compile("(\\d+)\\s*/\\s*(\\d+)").matcher(var8);
                  if (var9.find()) {
                     int var10 = Integer.parseInt(var9.group(1));
                     int var11 = Integer.parseInt(var9.group(2));
                     return Math.max(0, var11 - var10);
                  }
               }

               return Integer.MAX_VALUE;
            }
         }
      } else {
         return 0;
      }
   }

   private double getPricePerItemFromSlot(GenericContainerScreen var1, int var2) {
      GenericContainerScreenHandler var3 = (GenericContainerScreenHandler)var1.getScreenHandler();
      if (var2 >= 0 && var2 < var3.slots.size()) {
         char var10 = 'ꚅ';
         ItemStack var4 = ((Slot)var3.slots.get(var2)).getStack();
         if (var4.isEmpty()) {
            return 0.0;
         } else {
            LoreComponent var5 = this.getLore(var4);
            if (var5 == null) {
               return 0.0;
            } else {
               for (Text var7 : var5.lines()) {
                  String var8 = var7.getString();
                  if (var8.contains("$") || var8.toLowerCase().contains("giá") || var8.toLowerCase().contains("price")) {
                     Matcher var9 = Pattern.compile("\\$\\s*([0-9]+\\.?[0-9]*)").matcher(var8);
                     if (var9.find()) {
                        return Double.parseDouble(var9.group(1));
                     }
                  }
               }

               return 0.0;
            }
         }
      } else {
         return 0.0;
      }
   }

   private int FT(GenericContainerScreen var1, Item var2) {
      GenericContainerScreenHandler var3 = (GenericContainerScreenHandler)var1.getScreenHandler();
      int var4 = -1;
      double var5 = -1.0;

      for (int var7 = 0; var7 <= 44 && var7 < var3.slots.size(); var7++) {
         ItemStack var8 = ((Slot)var3.slots.get(var7)).getStack();
         if (!var8.isEmpty() && !this.FR(var8.getItem()) && (var2 == Items.AIR || var8.getItem() == var2 || (Boolean)this.uw.get())) {
            int var9 = this.getRemainingFromSlot(var1, var7);
            if (var9 >= 64) {
               if ((Boolean)this.uw.get()) {
                  return var7;
               }

               double var10 = this.getPricePerItemFromSlot(var1, var7);
               if (var10 > var5) {
                  var5 = var10;
                  var4 = var7;
               }
            }
         }
      }

      return var4;
   }

   private boolean FU() {
      return switch (this.cl) {
         case SELL_SEND, SELL_WAIT_GUI, SELL_FILL, SELL_CLOSE, ORDER_SEND, ORDER_WAIT_GUI, ORDER_CAULDRON, ORDER_WAIT_REOPEN, ORDER_FIND_SLOT, ORDER_CLICK_SLOT2, ORDER_FILL, ORDER_WAIT_CONFIRM, ORDER_CONFIRM, ORDER_WAIT_CLOSE, ORDER_CLOSE, FAST_ORDER_WAIT_NEXT_PAGE -> true;
         default -> false;
      };
   }

   @AliceEventHandler
   public void FV(Post var1) {
      if (!this.mI) {
         if (this.mc.player != null && this.mc.world != null) {
            boolean var2 = this.mc.getWindow() != null && GLFW.glfwGetKey(this.mc.getWindow().getHandle(), 256) == 1;
            if (var2) {
               if (!this.ao) {
                  this.mM = 40;
               }
            } else {
               char var3 = '耥';
            }

            this.ao = var2;
            if (this.mM > 0) {
               this.mM--;
            } else {
               if (this.cl == this.kU) {
                  this.u++;
               } else {
                  this.kU = this.cl;
                  this.u = 0;
               }

               if (this.cl != SpawnerOrder.State.WAITING_CYCLE && this.cl != SpawnerOrder.State.WAIT_CLEANUP && this.u >= 100) {
                  this.u = 0;
                  if (this.mc.currentScreen != null) {
                     this.mc.player.closeHandledScreen();
                  }

                  this.cl = SpawnerOrder.State.WAIT_CLEANUP;
               } else if (this.cl == SpawnerOrder.State.WAITING_CYCLE) {
                  if (++this.dc >= 1) {
                     this.dc = 0;
                     this.FP();
                  }
               } else if (this.cl == SpawnerOrder.State.WAIT_CLEANUP) {
                  this.FF();
                  if (this.mf.isEmpty() && !this.fq) {
                     if (this.FD()) {
                        if (++this.zy <= 100) {
                           return;
                        }

                        this.zy = 0;
                     }

                     this.zy = 0;
                     if (this.qy.isEmpty()) {
                        this.cl = SpawnerOrder.State.SCANNING;
                     } else {
                        this.FO();
                     }
                  } else {
                     this.FM();
                  }
               } else if (this.cl == SpawnerOrder.State.WAIT_DISPENSER_DROP) {
                  if (++this.yY >= this.FH()) {
                     this.yY = 0;
                     if (this.FB()) {
                        this.cl = SpawnerOrder.State.FAST_DROP_CLICK;
                     } else {
                        this.xP++;
                        if (this.xP >= (Integer)this.Ae.get()) {
                           this.xP = 0;
                           if (this.mc.currentScreen != null) {
                              this.mc.player.closeHandledScreen();
                           }

                           this.cl = SpawnerOrder.State.CLOSING_SPAWNER;
                        } else {
                           this.cl = SpawnerOrder.State.CLICK_SLOT52;
                        }
                     }
                  }
               } else if (!this.FU()) {
                  this.wH = 1;
                  if (++this.eB >= this.wH) {
                     this.FW();
                     this.eB = 0;
                  }
               } else if (this.AA > 0) {
                  this.AA--;
               } else {
                  int var10000 = ++this.aq >= 200 ? '騾' : '騿';

                  while (true) {
                     int var4 = var10000;
                     if (var4 == 39486) {
                        this.aq = 0;
                        if (this.mc.currentScreen != null) {
                           this.mc.player.closeHandledScreen();
                        }

                        this.FN();
                        return;
                     }

                     if (var4 != 39487) {
                        this.FW();
                        return;
                     }

                     var10000 = 39485;
                  }
               }
            }
         }
      }
   }

   private void FW() {
      switch (this.cl.ordinal() ^ -367748775) {
         case -367748800:
            if (this.mc.currentScreen instanceof GenericContainerScreen var28) {
               if (this.FE(this.xy)) {
                  if (this.FB()) {
                     String var58 = var28.getTitle().getString().replaceAll("§.", "");
                     if (var58.contains("ʙỏ ᴠậᴛ ᴘʜẩᴍ")) {
                        this.cl = SpawnerOrder.State.ORDER_FILL;
                        this.AA = this.FH();
                        this.aq = 0;
                     } else if (var58.contains("(") && var58.contains("/")) {
                        this.cl = SpawnerOrder.State.ORDER_FIND_SLOT;
                        this.AA = this.FH();
                        this.aq = 0;
                     } else {
                        this.AA = this.FH();
                     }
                  } else {
                     this.cl = SpawnerOrder.State.ORDER_FILL;
                     this.AA = this.FH();
                     this.aq = 0;
                  }
               } else {
                  this.mc.player.closeHandledScreen();
                  this.cl = SpawnerOrder.State.ORDER_CLOSE;
                  this.AA = this.FB() ? this.FH() : this.FH() * 2;
               }
            } else if (this.FE(this.xy) && this.FB()) {
               this.cl = SpawnerOrder.State.ORDER_SEND;
               this.AA = this.FH();
            } else {
               this.AA = this.FH();
            }
            break;
         case -367748799:
            if (!(this.mc.currentScreen instanceof GenericContainerScreen var27)) {
               this.cl = SpawnerOrder.State.ORDER_CLOSE;
               return;
            }

            GenericContainerScreenHandler var56 = (GenericContainerScreenHandler)var27.getScreenHandler();
            if (this.FB()) {
               if (var56.slots.size() > 16) {
                  this.mc.interactionManager.clickSlot(var56.syncId, 16, 0, SlotActionType.PICKUP, this.mc.player);
                  this.GN++;
                  this.AA = this.FH();
                  this.aq = 0;
                  this.cl = SpawnerOrder.State.ORDER_WAIT_CLOSE;
                  return;
               }
            } else {
               for (int var70 = 0; var70 < var56.slots.size(); var70++) {
                  Item var77 = ((Slot)var56.slots.get(var70)).getStack().getItem();
                  if (var77 == Items.LIME_STAINED_GLASS_PANE || var77 == Items.GREEN_STAINED_GLASS_PANE) {
                     this.mc.interactionManager.clickSlot(var56.syncId, var70, 0, SlotActionType.PICKUP, this.mc.player);
                     this.GN++;
                     double var82 = this.getPriceFromSlot(var27, var70);
                     if (var82 > 0.0) {
                        this.hI += var82;
                     }

                     this.AA = this.FH() * 10;
                     this.aq = 0;
                     this.cl = SpawnerOrder.State.ORDER_WAIT_CLOSE;
                     return;
                  }
               }
            }

            this.cl = SpawnerOrder.State.ORDER_CLOSE;
         case -367748798:
         case -367748783:
         case -367748782:
         default:
            break;
         case -367748797:
            if (this.mc.currentScreen instanceof GenericContainerScreen) {
               this.mc.player.closeHandledScreen();
            }

            this.AA = this.FB() ? this.FH() : this.FH() * 3;
            this.aq = 0;
            this.FF();
            if (!this.FG()) {
               this.FN();
            } else {
               this.cl = SpawnerOrder.State.ORDER_SEND;
            }
            break;
         case -367748796:
            if (!(this.mc.currentScreen instanceof GenericContainerScreen var26)) {
               this.cl = SpawnerOrder.State.ORDER_SEND;
               return;
            }

            String var54 = var26.getTitle().getString().replaceAll("§.", "");
            if (!var54.equals(this.az)) {
               this.az = var54;
               this.cl = SpawnerOrder.State.ORDER_FIND_SLOT;
               this.AA = 1;
               this.aq = 0;
            } else {
               this.AA = 1;
            }
            break;
         case -367748795:
            if (!(this.mc.currentScreen instanceof GenericContainerScreen var25)) {
               this.cl = SpawnerOrder.State.WAIT_CLEANUP;
               return;
            }

            GenericContainerScreenHandler var52 = (GenericContainerScreenHandler)var25.getScreenHandler();
            int var69 = (Integer)this.Ae.get();
            if (var69 == 1) {
               if (var52.slots.size() > 52) {
                  this.mc.interactionManager.clickSlot(var52.syncId, 52, 0, SlotActionType.PICKUP, this.mc.player);
               }

               this.cl = SpawnerOrder.State.CLOSING_SPAWNER;
               break;
            } else {
               int var76 = var69 * 2 - 1;
               int var81 = this.xP % 2 == 0 ? 52 : 53;
               if (var52.slots.size() > var81) {
                  this.mc.interactionManager.clickSlot(var52.syncId, var81, 0, SlotActionType.PICKUP, this.mc.player);
               }

               this.xP++;
               int var86 = this.xP < var76 ? '병' : '볐';

               while (true) {
                  switch (var86) {
                     case 48336:
                        this.cl = SpawnerOrder.State.CLOSING_SPAWNER;
                        return;
                     case 48337:
                        var86 = 48335;
                        break;
                     default:
                        this.yY = 0;
                        this.cl = SpawnerOrder.State.WAIT_DISPENSER_DROP;
                        return;
                  }
               }
            }
         case -367748792:
            if (this.mc.currentScreen instanceof GenericContainerScreen var24) {
               if (this.FB()) {
                  this.az = var24.getTitle().getString().replaceAll("§.", "");
                  this.cl = SpawnerOrder.State.ORDER_FIND_SLOT;
                  this.AA = this.FH();
                  this.aq = 0;
               } else {
                  this.cl = SpawnerOrder.State.ORDER_CAULDRON;
                  this.AA = this.FH();
                  this.aq = 0;
               }
            }
            break;
         case -367748791:
            if (!this.FE(this.xy)) {
               this.AA = this.FB() ? this.FH() : this.FH() * 3;
               this.aq = 0;
               if (!this.FG()) {
                  this.FN();
               } else {
                  this.cl = SpawnerOrder.State.ORDER_SEND;
               }

               return;
            }

            if ((Boolean)this.uw.get()) {
               String var23 = ((String)this.ra.get()).trim();
               if (var23.isEmpty()) {
                  this.FN();
                  return;
               }

               this.mc
                  .player
                  .networkHandler
                  .sendChatCommand(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","order ">(var23));
            } else {
               this.mc
                  .player
                  .networkHandler
                  .sendChatCommand(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","order ">(this.cP));
            }

            this.cl = SpawnerOrder.State.ORDER_WAIT_GUI;
            this.AA = this.FB() ? this.FH() : this.FH() * 3;
            this.aq = 0;
            break;
         case -367748790:
            if (this.mc.currentScreen instanceof GenericContainerScreen) {
               this.cl = SpawnerOrder.State.ORDER_FIND_SLOT;
               this.AA = this.FH();
               this.aq = 0;
               this.bQ = 0;
            }
            break;
         case -367748789:
            if (!(this.mc.currentScreen instanceof GenericContainerScreen var22)) {
               this.cl = SpawnerOrder.State.ORDER_SEND;
               return;
            }

            GenericContainerScreenHandler var49 = (GenericContainerScreenHandler)var22.getScreenHandler();

            for (int var68 = 0; var68 < var49.slots.size(); var68++) {
               if (((Slot)var49.slots.get(var68)).getStack().getItem() == Items.CAULDRON) {
                  this.mc.interactionManager.clickSlot(var49.syncId, var68, 0, SlotActionType.PICKUP, this.mc.player);
                  this.mc.player.closeHandledScreen();
                  this.cl = SpawnerOrder.State.ORDER_WAIT_REOPEN;
                  this.AA = this.FH() * 3;
                  this.aq = 0;
                  return;
               }
            }

            this.cl = SpawnerOrder.State.ORDER_FIND_SLOT;
            this.AA = this.FH();
            break;
         case -367748788:
            if (!(this.mc.currentScreen instanceof GenericContainerScreen var21)) {
               this.cl = SpawnerOrder.State.ORDER_SEND;
               return;
            }

            this.mc.interactionManager.clickSlot(((GenericContainerScreenHandler)var21.getScreenHandler()).syncId, 1, 0, SlotActionType.PICKUP, this.mc.player);
            this.cl = SpawnerOrder.State.ORDER_FILL;
            this.AA = this.FH();
            this.aq = 0;
            break;
         case -367748787:
            if (!(this.mc.currentScreen instanceof GenericContainerScreen var20)) {
               this.cl = SpawnerOrder.State.ORDER_SEND;
               return;
            }

            GenericContainerScreenHandler var46 = (GenericContainerScreenHandler)var20.getScreenHandler();
            if (this.FB()) {
               int var67 = this.FT(var20, this.xy);
               if (var67 != -1) {
                  this.bQ = 0;
                  this.mc.interactionManager.clickSlot(var46.syncId, var67, 0, SlotActionType.PICKUP, this.mc.player);
                  this.cl = SpawnerOrder.State.ORDER_FILL;
                  this.AA = this.FH();
                  this.aq = 0;
               } else if (var46.slots.size() > 53 && this.FS(((Slot)var46.slots.get(53)).getStack())) {
                  if (++this.bQ > 10) {
                     this.bQ = 0;
                     if (this.mc.currentScreen != null) {
                        this.mc.player.closeHandledScreen();
                     }

                     this.AA = this.FH() * 3;
                     this.aq = 0;
                     if (this.FG()) {
                        this.cl = SpawnerOrder.State.ORDER_SEND;
                        this.aq = 0;
                     } else {
                        this.FN();
                     }

                     return;
                  }

                  this.mc.interactionManager.clickSlot(var46.syncId, 53, 0, SlotActionType.PICKUP, this.mc.player);
                  this.az = var20.getTitle().getString().replaceAll("§.", "");
                  this.cl = SpawnerOrder.State.FAST_ORDER_WAIT_NEXT_PAGE;
                  this.AA = this.FH();
               } else {
                  if (this.mc.currentScreen != null) {
                     this.mc.player.closeHandledScreen();
                  }

                  this.AA = this.FH() * 3;
                  this.aq = 0;
                  if (this.FG()) {
                     this.cl = SpawnerOrder.State.ORDER_SEND;
                     this.aq = 0;
                  } else {
                     this.FN();
                  }
               }

               return;
            }

            int var66 = this.FT(var20, this.xy);
            if (var66 != -1) {
               this.bQ = 0;
               this.mc.interactionManager.clickSlot(var46.syncId, var66, 0, SlotActionType.PICKUP, this.mc.player);
               this.cl = SpawnerOrder.State.ORDER_CLICK_SLOT2;
               this.AA = this.FH();
               this.aq = 0;
            } else if (var46.slots.size() > 53 && this.FS(((Slot)var46.slots.get(53)).getStack())) {
               if (++this.bQ > 10) {
                  this.bQ = 0;
                  if (this.mc.currentScreen != null) {
                     this.mc.player.closeHandledScreen();
                  }

                  this.AA = this.FH() * 3;
                  this.aq = 0;
                  if (this.FG()) {
                     this.cl = SpawnerOrder.State.ORDER_SEND;
                     this.aq = 0;
                  } else {
                     this.FN();
                  }

                  return;
               }

               this.mc.interactionManager.clickSlot(var46.syncId, 53, 0, SlotActionType.PICKUP, this.mc.player);
               this.AA = this.FH() * 6;
            } else {
               if (this.mc.currentScreen != null) {
                  this.mc.player.closeHandledScreen();
               }

               this.AA = this.FH() * 3;
               this.aq = 0;
               if (this.FG()) {
                  this.cl = SpawnerOrder.State.ORDER_SEND;
                  this.aq = 0;
               } else {
                  this.FN();
               }
            }
            break;
         case -367748786:
            if (this.mc.currentScreen instanceof GenericContainerScreen var19) {
               GenericContainerScreenHandler var44 = (GenericContainerScreenHandler)var19.getScreenHandler();
               if (this.FB()) {
                  this.cl = SpawnerOrder.State.ORDER_CONFIRM;
                  this.AA = this.FH();
                  this.aq = 0;
                  return;
               }

               for (int var65 = 0; var65 < var44.slots.size(); var65++) {
                  if (((Slot)var44.slots.get(var65)).getStack().getItem() == Items.LIME_STAINED_GLASS_PANE) {
                     this.cl = SpawnerOrder.State.ORDER_CONFIRM;
                     this.AA = this.FH();
                     this.aq = 0;
                     return;
                  }
               }
            }
            break;
         case -367748785:
            if (!(this.mc.currentScreen instanceof GenericContainerScreen var18)) {
               this.cl = SpawnerOrder.State.ORDER_SEND;
               return;
            }

            String var42 = var18.getTitle().getString().replaceAll("§.", "");
            if (!var42.contains("ʙỏ ᴠậᴛ ᴘʜẩᴍ ᴠàᴏ để ɢɪᴀᴏ")) {
               this.mc.player.closeHandledScreen();
               this.AA = this.FH() * 3;
               this.aq = 0;
               if (this.FG()) {
                  this.cl = SpawnerOrder.State.ORDER_SEND;
               } else {
                  this.FN();
               }

               return;
            }

            char var9 = '靗';
            GenericContainerScreenHandler var64 = (GenericContainerScreenHandler)var18.getScreenHandler();
            boolean var75 = false;

            label539:
            for (int var79 = 0; var79 < 54 && var79 < var64.slots.size(); var79++) {
               int var10000 = ((Slot)var64.slots.get(var79)).inventory != this.mc.player.getInventory() ? '븝' : '븜';

               while (true) {
                  switch (var10000) {
                     case 48668:
                        continue label539;
                     case 48669:
                        var10000 = 48667;
                        break;
                     default:
                        if (((Slot)var64.slots.get(var79)).getStack().isEmpty()) {
                           var75 = true;
                           break label539;
                        }
                        continue label539;
                  }
               }
            }

            if (!var75) {
               this.mc.player.closeHandledScreen();
               this.cl = SpawnerOrder.State.ORDER_WAIT_CONFIRM;
               this.AA = this.FH() * 5;
               this.aq = 0;
               return;
            }

            boolean var80 = false;

            for (int var84 = 0; var84 < var64.slots.size(); var84++) {
               Slot var7 = (Slot)var64.slots.get(var84);
               if (var7.inventory == this.mc.player.getInventory()
                  && !var7.getStack().isEmpty()
                  && (this.xy == Items.AIR || var7.getStack().getItem() == this.xy)) {
                  this.mc.interactionManager.clickSlot(var64.syncId, var7.id, 0, SlotActionType.QUICK_MOVE, this.mc.player);
                  var80 = true;
               }
            }

            int var85 = !var80 ? 5635 : 5634;

            while (true) {
               int var10 = var85;
               if (var10 == 5634) {
                  this.AA = this.FH() * 2;
                  this.aq = 0;
                  this.cl = SpawnerOrder.State.ORDER_FILL;
                  return;
               }

               if (var10 != 5635) {
                  this.mc.player.closeHandledScreen();
                  this.cl = SpawnerOrder.State.ORDER_WAIT_CONFIRM;
                  this.AA = this.FH() * 5;
                  this.aq = 0;
                  return;
               }

               var85 = 5633;
            }
         case -367748784:
            if (!(this.mc.currentScreen instanceof GenericContainerScreen var17)) {
               this.cl = SpawnerOrder.State.CLOSING_SPAWNER;
               return;
            }

            GenericContainerScreenHandler var40 = (GenericContainerScreenHandler)var17.getScreenHandler();
            if (var40.slots.size() > 52) {
               this.mc.interactionManager.clickSlot(var40.syncId, 52, 0, SlotActionType.PICKUP, this.mc.player);
            }

            this.cl = SpawnerOrder.State.CLICK_DISPENSER;
            break;
         case -367748781:
            if (this.mc.currentScreen != null) {
               this.mc.player.closeHandledScreen();
            }

            this.eB = 0;
            this.cl = SpawnerOrder.State.WAIT_CLEANUP;
            break;
         case -367748780:
            if (this.mc.currentScreen instanceof GenericContainerScreen var16) {
               this.cl = SpawnerOrder.State.SELL_FILL;
               this.AA = this.FH();
               this.aq = 0;
            }
            break;
         case -367748779:
            this.mc.player.networkHandler.sendChatCommand("sell");
            this.cl = SpawnerOrder.State.SELL_WAIT_GUI;
            this.AA = this.FH() * 3;
            this.aq = 0;
            break;
         case -367748778:
            if (this.mc.currentScreen != null) {
               this.mc.player.closeHandledScreen();
            }

            this.AA = this.FH() * 2;
            this.aq = 0;
            this.fq = false;
            if (this.FG()) {
               this.cl = SpawnerOrder.State.ORDER_SEND;
            } else {
               this.FN();
            }
            break;
         case -367748777:
            if (this.mc.currentScreen instanceof GenericContainerScreen var15) {
               GenericContainerScreenHandler var37 = (GenericContainerScreenHandler)var15.getScreenHandler();
               boolean var63 = false;

               for (int var74 = 0; var74 < var37.slots.size(); var74++) {
                  Slot var78 = (Slot)var37.slots.get(var74);
                  if (var78.inventory == this.mc.player.getInventory() && !var78.getStack().isEmpty()) {
                     Item var83 = var78.getStack().getItem();
                     if (!va.containsKey(var83) && var83.getMaxCount() > 1) {
                        this.mc.interactionManager.clickSlot(var37.syncId, var78.id, 0, SlotActionType.QUICK_MOVE, this.mc.player);
                        var63 = true;
                     }
                  }
               }

               if (!var63) {
                  this.mc.player.closeHandledScreen();
                  this.cl = SpawnerOrder.State.SELL_CLOSE;
                  this.AA = this.FH() * 3;
                  this.aq = 0;
               } else {
                  this.AA = this.FH() * 2;
                  this.aq = 0;
                  this.cl = SpawnerOrder.State.SELL_FILL;
               }
            } else {
               this.cl = SpawnerOrder.State.SELL_CLOSE;
            }
            break;
         case -367748776:
            if (this.BK == null) {
               this.cl = SpawnerOrder.State.SCANNING;
               return;
            }

            if (new Vec3d(this.mc.player.getX(), this.mc.player.getY(), this.mc.player.getZ()).distanceTo(Vec3d.ofCenter(this.BK)) <= 4.0) {
               this.EO = this.mc.player.getYaw();
               this.tJ = this.mc.player.getPitch();
               this.pP = 0;
               this.hX = 0;
               this.hO = this.EO;
               this.Az = this.tJ;
               this.cl = SpawnerOrder.State.SILENT_ROTATE;
            } else if (++this.pP >= 20) {
               this.pP = 0;
               this.FO();
            }
            break;
         case -367748775:
            this.qy.clear();
            BlockPos var14 = this.mc.player.getBlockPos();
            byte var35 = 5;

            for (int var62 = -5; var62 <= var35; var62++) {
               for (int var73 = -var35; var73 <= var35; var73++) {
                  for (int var5 = -var35; var5 <= var35; var5++) {
                     BlockPos var6 = var14.add(var62, var73, var5);
                     if (this.mc.world.getBlockState(var6).getBlock() instanceof SpawnerBlock) {
                        this.qy.add(var6);
                     }
                  }
               }
            }

            if (this.qy.isEmpty()) {
               this.cl = SpawnerOrder.State.WAITING_CYCLE;
               this.dc = 0;
               return;
            }

            this.Jg = 0;
            this.BK = this.qy.get(0);
            this.cl = SpawnerOrder.State.MOVING;
            this.pP = 0;
            break;
         case -367748774:
            if (this.BK == null) {
               this.cl = SpawnerOrder.State.SCANNING;
               return;
            }

            float var13 = this.FI(this.BK);
            float var34 = this.FJ(this.BK);
            if (++this.hX >= 3) {
               this.FK(var13, var34);
               this.mc.player.setYaw(var13);
               this.mc.player.setPitch(var34);
               this.hA = 0;
               this.cl = SpawnerOrder.State.OPENING;
            } else {
               float var61 = this.hX / 3.0F;
               this.hO = this.hO + (var13 - this.hO) * var61;
               this.Az = this.Az + (var34 - this.Az) * var61;
               this.FK(this.hO, this.Az);
               this.mc.player.setYaw(this.hO);
               this.mc.player.setPitch(this.Az);
            }
            break;
         case -367748773:
            this.hX = 0;
            this.cl = SpawnerOrder.State.SILENT_ROTATE_SMOOTH;
            break;
         case -367748772:
            this.cl = SpawnerOrder.State.CLICK_BUNDLE;
            break;
         case -367748771:
            if (this.mc.currentScreen instanceof GenericContainerScreen var12) {
               this.hA = 0;
               if (this.FB()) {
                  String var33 = var12.getTitle().getString().trim();
                  if (var33.matches("\\d+\\s+.*") || ((GenericContainerScreenHandler)var12.getScreenHandler()).slots.size() >= 54) {
                     this.xP = 0;
                     this.cl = SpawnerOrder.State.FAST_DROP_CLICK;
                  }
               } else {
                  this.cl = SpawnerOrder.State.WAIT_CHEST_GUI;
               }

               return;
            }

            if (this.hA % 20 == 0 && this.BK != null) {
               this.FL(this.BK);
            }

            if (++this.hA >= 60) {
               this.FO();
            }
            break;
         case -367748770:
            if (!(this.mc.currentScreen instanceof GenericContainerScreen var11)) {
               this.cl = SpawnerOrder.State.CLOSING_SPAWNER;
               return;
            }

            ScreenHandler var31 = var11.getScreenHandler();

            for (Slot var72 : var31.slots) {
               if (var72.inventory != this.mc.player.getInventory() && var72.getStack().getItem() == Items.DISPENSER) {
                  this.mc.interactionManager.clickSlot(var31.syncId, var72.id, 0, SlotActionType.PICKUP, this.mc.player);
                  break;
               }
            }

            this.yY = 0;
            this.cl = SpawnerOrder.State.WAIT_DISPENSER_DROP;
            break;
         case -367748769:
            if (!(this.mc.currentScreen instanceof GenericContainerScreen var1)) {
               this.cl = SpawnerOrder.State.CLOSING_SPAWNER;
               return;
            }

            ScreenHandler var29 = var1.getScreenHandler();
            if (Gt.nextInt(100) >= 5) {
               for (Slot var71 : var29.slots) {
                  if (var71.inventory != this.mc.player.getInventory()) {
                     short var8 = 15287;
                     if (var71.getStack().getItem() == Items.BUNDLE) {
                        this.mc.interactionManager.clickSlot(var29.syncId, var71.id, 0, SlotActionType.PICKUP, this.mc.player);
                        break;
                     }
                  }
               }
            } else {
               for (Slot var4 : var29.slots) {
                  if (var4.inventory != this.mc.player.getInventory() && var4.getStack().getItem() == Items.EXPERIENCE_BOTTLE) {
                     this.mc.interactionManager.clickSlot(var29.syncId, var4.id, 0, SlotActionType.PICKUP, this.mc.player);
                     break;
                  }
               }
            }

            this.cl = SpawnerOrder.State.CLICK_DISPENSER;
      }
   }

   public static SpawnerOrder getActiveInstance() {
      return db;
   }

   public String getInfoString() {
      if (!this.isActive()) {
         return null;
      } else if (this.cl != SpawnerOrder.State.WAITING_CYCLE) {
         if (this.cl == SpawnerOrder.State.WAIT_DISPENSER_DROP) {
            return "" + this.yY + this.FH();
         } else {
            String var1 = this.cP.isEmpty() ? "" : StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001\u0002"," | ">(this.cP);
            return var1 + this.cl.name().replace("_", " ") + this.getPing();
         }
      } else {
         return 1 - this.dc + "";
      }
   }

   private String FX(double var1) {
      if (!(var1 >= 1.0E9)) {
         if (var1 >= 1000000.0) {
            return String.format("%.2fM", var1 / 1000000.0);
         } else {
            return var1 >= 1000.0 ? String.format("%.2fK", var1 / 1000.0) : String.format("%.0f", var1);
         }
      } else {
         return String.format("%.2fB", var1 / 1.0E9);
      }
   }

   private String FY(long var1) {
      long var3 = var1 / 1000L;
      long var5 = var3 / 3600L;
      long var7 = var3 % 3600L / 60L;
      long var9 = var3 % 60L;
      if (var5 > 0L) {
         return String.format("%dh %dm %ds", var5, var7, var9);
      } else {
         return var7 > 0L ? String.format("%dm %ds", var7, var9) : var9 + "";
      }
   }

   @AliceEventHandler
   public void FZ(Render2DEvent var1) {
      if (this.isActive() && (Boolean)this.dS.get()) {
         if (this.mc.player != null) {
            TextRenderer var2 = TextRenderer.get();
            if (var2 != null) {
               long var3 = this.uC > 0L ? System.currentTimeMillis() - this.uC : 0L;
               double var5 = var3 > 0L ? this.hI / (var3 / 3600000.0) : 0.0;
               SpawnerProtect var7 = (SpawnerProtect)Modules.get().get(SpawnerProtect.class);
               boolean var8 = var7.isActive();
               String var9 = var7.getInfoString();
               String var10 = var9 != null ? StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002"," (",")">(var9) : "";
               AliceAutoReconnect var11 = (AliceAutoReconnect)Modules.get().get(AliceAutoReconnect.class);
               boolean var12 = var11.isActive();
               String var13 = var11.getInfoString();
               String var14 = var13 != null ? StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002"," (",")">(var13) : "";
               String[] var15 = new String[]{
                  "Spawner-Order",
                  StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Earned: $">(this.FX(this.hI)),
                  this.GN + "",
                  StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","$/h: ">(this.FX(var5)),
                  StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Time: ">(this.FY(var3)),
                  StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Protect: ">(
                     var8 ? StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","ON">(var10) : "OFF"
                  ),
                  StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","AutoRe: ">(
                     var12 ? StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","ON">(var14) : "OFF"
                  )
               };
               Color var16 = new Color(85, 255, 85, 255);
               Color var17 = new Color(255, 85, 85, 255);
               Color[] var18 = new Color[]{Gs, pB, IW, pB, IW, var8 ? var16 : var17, var12 ? var16 : var17};
               double var19 = 0.0;

               for (String var24 : var15) {
                  var19 = Math.max(var19, var2.getWidth(var24, false));
               }

               double var42 = var2.getHeight(false);
               double var43 = 4.0;
               double var25 = 2.0;
               double var27 = var19 + var43 * 2.0;
               double var29 = var15.length * var42 + (var15.length - 1) * var25 + var43 * 2.0;
               double var31 = this.mc.getWindow().getScaledWidth();
               double var33 = this.mc.getWindow().getScaledHeight();
               double var35 = var31 / 2.0 - var27 - 10.0;
               double var37 = var33 / 2.0 - var29 - 10.0;
               Renderer2D.COLOR.begin();
               Renderer2D.COLOR.quad(var35, var37, var27, var29, new Color(20, 20, 20, 180));
               Renderer2D.COLOR.quad(var35, var37, var27, 2.0, new Color(85, 255, 170, 255));
               this.Ga(var1);
               var2.begin(1.0, false, true);
               double var39 = var37 + var43 + 3.0;

               for (int var41 = 0; var41 < var15.length; var41++) {
                  var2.render(var15[var41], var35 + var43, var39, var18[var41], true);
                  var39 += var42 + var25;
               }

               var2.end();
            }
         }
      }
   }

   public static void init() {
      if (!rD) {
         rD = true;
      }
   }

   private void Ga(Render2DEvent var1) {
      if (!Ek) {
         Ek = true;

         for (Method var5 : Renderer2D.COLOR.getClass().getMethods()) {
            if (var5.getName().equals("render") && (var5.getParameterCount() == 1 || var5.getParameterCount() == 0)) {
               bX = var5;
               break;
            }
         }
      }

      if (bX != null) {
         try {
            if (bX.getParameterCount() == 1) {
               Class var7 = bX.getParameterTypes()[0];
               if (var7.getName().contains("DrawContext")) {
                  bX.invoke(Renderer2D.COLOR, var1.drawContext);
               } else {
                  bX.invoke(Renderer2D.COLOR, var1.drawContext.getMatrices());
               }
            } else {
               bX.invoke(Renderer2D.COLOR);
            }
         } catch (Exception var6) {
         }
      }
   }

   private static enum State {
      SCANNING,
      MOVING,
      SILENT_ROTATE,
      SILENT_ROTATE_SMOOTH,
      OPENING,
      WAIT_CHEST_GUI,
      CLICK_BUNDLE,
      CLICK_DISPENSER,
      WAIT_DISPENSER_DROP,
      CLICK_SLOT52,
      CLOSING_SPAWNER,
      WAIT_CLEANUP,
      SELL_SEND,
      SELL_WAIT_GUI,
      SELL_FILL,
      SELL_CLOSE,
      ORDER_SEND,
      ORDER_WAIT_GUI,
      ORDER_CAULDRON,
      ORDER_WAIT_REOPEN,
      ORDER_FIND_SLOT,
      ORDER_CLICK_SLOT2,
      ORDER_FILL,
      ORDER_WAIT_CONFIRM,
      ORDER_CONFIRM,
      ORDER_WAIT_CLOSE,
      ORDER_CLOSE,
      WAITING_CYCLE,
      FAST_DROP_CLICK,
      FAST_ORDER_WAIT_NEXT_PAGE;
   }
}
