package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.lang.invoke.StringConcatFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.ItemListSetting.Builder;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterInit;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterRender;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class SpawnerDropper extends AliceModule {
   private static final double iG = 4.5;
   private static final int oV = 44;
   private static final int vE = 52;
   private static final int DR = 50;
   private static final int kP = 100;
   private static final int FV = -1273752556;
   private static final int yn = -11141206;
   private static final int zL = -1;
   private static final int mB = -8856321;
   private static final Vec3d[] BD = new Vec3d[]{
      new Vec3d(0.5, 0.5, 0.5),
      new Vec3d(0.5, 0.25, 0.5),
      new Vec3d(0.5, 0.75, 0.5),
      new Vec3d(0.25, 0.5, 0.5),
      new Vec3d(0.75, 0.5, 0.5),
      new Vec3d(0.5, 0.5, 0.25),
      new Vec3d(0.5, 0.5, 0.75)
   };
   private final SettingGroup cr = this.settings.getDefaultGroup();
   private final SettingGroup GQ = this.settings.createGroup("HUD");
   private final Setting<List<Item>> wY = this.cr
      .add(
         ((Builder)((Builder)new Builder().name("items-to-drop")).description("cac item duoc phep vut tu GUI spawner"))
            .defaultValue(new Item[]{Items.BLAZE_ROD, Items.LEATHER, Items.BONE})
            .build()
      );
   private final Setting<Integer> aP = this.cr
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("delay"))
                  .description("tick giua luc mo lai GUI va thao tac ranh"))
               .defaultValue(4))
            .min(1)
            .max(200)
            .sliderMax(40)
            .build()
      );
   private final Setting<Integer> ri = this.cr
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("session-waiting-seconds"))
                  .description("so giay phai cho sau khi ban tat ca truoc khi bat dau dot khac"))
               .defaultValue(60))
            .min(1)
            .max(3600)
            .sliderMax(300)
            .build()
      );
   private final Setting<Boolean> xH = this.GQ
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("show-hud"))
                  .description("hien thi thong ke cua SpawnerDropper"))
               .defaultValue(true))
            .build()
      );
   private final Set<Screen> yz = Collections.newSetFromMap(new WeakHashMap<>());
   private SpawnerDropper.State de = SpawnerDropper.State.Scanning;
   private long bS;
   private long bi;
   private long hV;
   private long bB;
   private long GU;
   private BlockPos ps;
   private int tr = -1;
   private GuiCycleController.CyclePhase fx = GuiCycleController.CyclePhase.DropAll;
   private SpawnerDropper.PendingDrop bK;

   public SpawnerDropper() {
      super(AliceAddon.CATEGORY, "SpawnerDropper", "luan phien Drop All va Sell All qua moi lan mo GUI spawner");
      this.EL();
   }

   @Override
   public void onActivate() {
      super.onActivate();
      this.bS = 0L;
      this.hV = System.currentTimeMillis();
      this.bB = 0L;
      this.GU = 0L;
      this.ps = null;
      this.tr = -1;
      this.fx = GuiCycleController.CyclePhase.DropAll;
      this.bK = null;
      if (this.mc.player != null && this.mc.currentScreen instanceof GenericContainerScreen) {
         this.mc.player.closeHandledScreen();
      }

      this.EC(SpawnerDropper.State.Scanning, 0);
      this.EM(this.mc.currentScreen, this.mc);
   }

   @Override
   public void onDeactivate() {
      this.de = SpawnerDropper.State.Inactive;
      this.bK = null;
      this.ps = null;
      this.tr = -1;
      this.fx = GuiCycleController.CyclePhase.DropAll;
      super.onDeactivate();
   }

   public String getInfoString() {
      return this.isActive() ? this.EJ() : null;
   }

   @AliceEventHandler
   private void Eh(Post var1) {
      this.bS++;
      if (this.isActive() && this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
         int var2 = this.de.ordinal();
         if (var2 == 1) {
            this.Ek();
         } else if (var2 == 2) {
            this.El();
         } else if (var2 != 3) {
            if (var2 != 4) {
               if (var2 == 5) {
                  this.Eo();
               } else if (var2 != 6) {
                  if (var2 == 7) {
                     this.Eq();
                  }
               } else {
                  this.Ep();
               }
            } else {
               this.En();
            }
         } else {
            this.Em();
         }
      }
   }

   @AliceEventHandler
   private void Ei(Receive var1) {
      if (!(var1.packet instanceof ScreenHandlerSlotUpdateS2CPacket var5)) {
         if (!(var1.packet instanceof InventoryS2CPacket var4)) {
            return;
         }

         int var8 = var4.syncId();
         int var9 = var4.revision();
         if (this.bK != null && this.bK.BG == var8 && var9 >= this.bK.vJ) {
            this.Et(this.bK.Cl.Fo(this.Ev(var4.contents())));
         }
      } else {
         int var2 = var5.getSyncId();
         int var3 = var5.getRevision();
         int var10000 = var5.getSlot() >= 0 ? '뺔' : '뺕';

         while (true) {
            int var7 = var10000;
            if (var7 == 48788) {
               if (var5.getSlot() > 44) {
                  return;
               }

               if (this.bK != null && this.bK.BG == var2 && var3 >= this.bK.vJ) {
                  this.Et(this.bK.Cl.Fp(this.Eu(var5.getSlot(), var5.getStack())));
               }
               break;
            }

            if (var7 != 48789) {
               return;
            }

            var10000 = 48787;
         }
      }
   }

   @AliceEventHandler
   private void Ej(Render2DEvent var1) {
      if (!(this.mc.currentScreen instanceof HandledScreen)) {
         this.EN(var1.drawContext);
      }
   }

   private void Ek() {
      this.ps = this.EE();
      if (this.ps != null) {
         if (!this.EF(this.ps)) {
            this.ps = null;
         } else {
            this.EC(SpawnerDropper.State.Opening, 100);
         }
      }
   }

   private void El() {
      Screen var2 = this.mc.currentScreen;
      int var10000 = !(var2 instanceof GenericContainerScreen) ? '膴' : '膳';

      while (true) {
         switch (var10000) {
            case 33203:
               GenericContainerScreen var1 = (GenericContainerScreen)var2;
               GenericContainerScreenHandler var3 = (GenericContainerScreenHandler)var1.getScreenHandler();
               this.tr = var3.syncId;
               this.bK = null;
               this.EC(SpawnerDropper.State.Inspecting, 0);
               return;
            case 33204:
               var10000 = 33202;
               break;
            default:
               if (this.ED()) {
                  this.EB();
               }

               return;
         }
      }
   }

   private void Em() {
      GenericContainerScreenHandler var1 = this.Ez();
      if (var1 == null) {
         this.EB();
      } else {
         List var2 = this.Ew(var1, false);
         HashSet var3 = new HashSet((Collection)this.wY.get());
         GuiCycleController.GuiAction var4 = GuiCycleController.Fx(this.fx, GuiCycleController.Ft(var2, var3));
         if (var4 == GuiCycleController.GuiAction.StartJunkDrop) {
            this.EC(SpawnerDropper.State.WaitingJunkThrow, (Integer)this.aP.get());
         } else if (var4 == GuiCycleController.GuiAction.DropAll) {
            List var5 = this.Ew(var1, true);
            this.Es(var1, var5);
            if (!this.Ex(var1, 52, 0, SlotActionType.PICKUP)) {
               this.bK = null;
               this.EB();
            } else {
               this.fx = GuiCycleController.Fw(this.fx);
               this.EA();
               this.EC(SpawnerDropper.State.WaitingReopen, (Integer)this.aP.get());
            }
         } else {
            this.bK = null;
            if (!this.Ex(var1, 50, 0, SlotActionType.PICKUP)) {
               this.EB();
            } else {
               this.fx = GuiCycleController.Fw(this.fx);
               this.EA();
               this.EC(SpawnerDropper.State.SessionWaiting, (Integer)this.ri.get() * 20);
            }
         }
      }
   }

   private void En() {
      int var10000 = this.ED() ? 23254 : 23253;

      while (true) {
         switch (var10000) {
            case 23253:
               return;
            case 23254:
               var10000 = 23252;
               break;
            default:
               this.ps = this.EE();
               if (this.ps == null) {
                  this.EC(SpawnerDropper.State.Scanning, 0);
                  return;
               }

               if (!this.EF(this.ps)) {
                  this.EC(SpawnerDropper.State.Scanning, 0);
                  return;
               }

               this.EC(SpawnerDropper.State.Opening, 100);
               return;
         }
      }
   }

   private void Eo() {
      if (this.ED()) {
         GenericContainerScreenHandler var1 = this.Ez();
         if (var1 == null) {
            this.EB();
         } else {
            HashSet var2 = new HashSet((Collection)this.wY.get());
            this.Er(var1, var2);
            this.EC(SpawnerDropper.State.WaitingJunkSellAll, (Integer)this.aP.get());
         }
      }
   }

   private void Ep() {
      if (this.ED()) {
         GenericContainerScreenHandler var1 = this.Ez();
         if (var1 == null) {
            this.EB();
         } else {
            this.bK = null;
            if (this.Ex(var1, 50, 0, SlotActionType.PICKUP)) {
               this.fx = GuiCycleController.CyclePhase.DropAll;
               this.EA();
               this.EC(SpawnerDropper.State.SessionWaiting, (Integer)this.ri.get() * 20);
            } else {
               this.EB();
            }
         }
      }
   }

   private void Eq() {
      if (this.ED()) {
         if (this.mc.currentScreen instanceof GenericContainerScreen) {
            this.mc.player.closeHandledScreen();
         }

         this.EB();
      }
   }

   private void Er(GenericContainerScreenHandler var1, Set<Item> var2) {
      List var3 = this.Ew(var1, true);
      this.Es(var1, var3);

      for (GuiCycleController.SlotView var5 : var3) {
         if (var2.contains((Item)var5.item())) {
            this.Ex(var1, var5.slot(), 1, SlotActionType.THROW);
         }
      }
   }

   private void Es(GenericContainerScreenHandler var1, List<GuiCycleController.SlotView> var2) {
      this.bK = new SpawnerDropper.PendingDrop(var1.syncId, var1.getRevision(), GuiCycleController.Fu(var2));
   }

   private void Et(GuiCycleController.ConfirmedDelta var1) {
      this.GU = this.GU + var1.items();
      this.bB = this.bB + var1.stacks();
   }

   private GuiCycleController.SlotView Eu(int var1, ItemStack var2) {
      return new GuiCycleController.SlotView(var1, var2.isEmpty() ? Items.AIR : var2.getItem(), !var2.isEmpty() ? var2.getCount() : 0);
   }

   private List<GuiCycleController.SlotView> Ev(List<ItemStack> var1) {
      int var2 = Math.min(45, var1.size());
      ArrayList var3 = new ArrayList(var2);

      for (int var4 = 0; var4 < var2; var4++) {
         var3.add(this.Eu(var4, (ItemStack)var1.get(var4)));
      }

      return var3;
   }

   private List<GuiCycleController.SlotView> Ew(GenericContainerScreenHandler var1, boolean var2) {
      Object var3 = var2 ? new HashSet((Collection)this.wY.get()) : Collections.emptySet();
      int var4 = Math.min(45, var1.slots.size());
      ArrayList var5 = new ArrayList();

      for (int var6 = 0; var6 < var4; var6++) {
         ItemStack var7 = var1.getSlot(var6).getStack();
         if (!var7.isEmpty() && (!var2 || var3.contains(var7.getItem()))) {
            var5.add(new GuiCycleController.SlotView(var6, var7.getItem(), var7.getCount()));
         }
      }

      return var5;
   }

   private boolean Ex(GenericContainerScreenHandler var1, int var2, int var3, SlotActionType var4) {
      if (var2 >= 0 && var2 < var1.slots.size()) {
         this.mc.interactionManager.clickSlot(var1.syncId, var2, var3, var4, this.mc.player);
         return true;
      } else {
         return false;
      }
   }

   private GenericContainerScreenHandler Ey() {
      return this.mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var1 ? var1 : null;
   }

   private GenericContainerScreenHandler Ez() {
      GenericContainerScreenHandler var1 = this.Ey();
      return var1 != null && var1.syncId == this.tr ? var1 : null;
   }

   private void EA() {
      if (this.Ez() != null) {
         this.mc.player.closeHandledScreen();
      }

      this.tr = -1;
   }

   private void EB() {
      this.ps = null;
      this.tr = -1;
      this.bK = null;
      this.EC(SpawnerDropper.State.Scanning, 0);
   }

   private void EC(SpawnerDropper.State var1, int var2) {
      this.de = var1;
      this.bi = this.bS + Math.max(0, var2);
   }

   private boolean ED() {
      return this.bS >= this.bi;
   }

   private BlockPos EE() {
      BlockPos var1 = this.mc.player.getBlockPos();
      BlockPos var2 = null;
      double var3 = Double.MAX_VALUE;

      for (int var5 = -5; var5 <= 5; var5++) {
         for (int var6 = -5; var6 <= 5; var6++) {
            for (int var7 = -5; var7 <= 5; var7++) {
               BlockPos var8 = var1.add(var5, var6, var7);
               if (this.mc.world.getBlockState(var8).isOf(Blocks.SPAWNER)) {
                  double var9 = this.mc.player.squaredDistanceTo(Vec3d.ofCenter(var8));
                  if (!(var9 > 20.25)) {
                     if (!(var9 >= var3) && this.EI(var8) != null) {
                        var2 = var8;
                        var3 = var9;
                     }
                  } else {
                     short var11 = 10039;
                  }
               }
            }
         }
      }

      return var2;
   }

   private boolean EF(BlockPos var1) {
      BlockHitResult var2 = this.EI(var1);
      if (var2 != null && !(this.mc.player.squaredDistanceTo(var2.getPos()) > 20.25)) {
         float var3 = this.mc.player.getYaw();
         float var4 = this.mc.player.getPitch();
         float[] var5 = this.EH(var2.getPos());
         float var6 = GuiCycleController.Fv(var3, var5[0]);
         float var7 = GuiCycleController.Fv(var6, var3);
         this.EG(var6, var5[1]);
         this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, var2);
         this.EG(var7, var4);
         return true;
      } else {
         return false;
      }
   }

   private void EG(float var1, float var2) {
      this.mc.player.networkHandler.sendPacket(new LookAndOnGround(var1, var2, this.mc.player.isOnGround(), this.mc.player.horizontalCollision));
   }

   private float[] EH(Vec3d var1) {
      Vec3d var2 = this.mc.player.getEyePos();
      double var3 = var1.x - var2.x;
      double var5 = var1.y - var2.y;
      double var7 = var1.z - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      return new float[]{(float)Math.toDegrees(Math.atan2(-var3, var7)), (float)(-Math.toDegrees(Math.atan2(var5, var9)))};
   }

   private BlockHitResult EI(BlockPos var1) {
      Vec3d var2 = this.mc.player.getEyePos();

      for (Vec3d var6 : BD) {
         Vec3d var7 = new Vec3d(var1.getX() + var6.x, var1.getY() + var6.y, var1.getZ() + var6.z);
         BlockHitResult var8 = this.mc.world.raycast(new RaycastContext(var2, var7, ShapeType.OUTLINE, FluidHandling.NONE, this.mc.player));
         if (var8.getType() == Type.BLOCK && var8.getBlockPos().equals(var1)) {
            return var8;
         }
      }

      return null;
   }

   private String EJ() {
      if (this.de == SpawnerDropper.State.SessionWaiting) {
         long var1 = Math.max(0L, this.bi - this.bS);
         long var3 = (var1 + 19L) / 20L;
         return var3 + "";
      } else {
         return switch (this.de.ordinal() ^ 228171163) {
            case 228171160 -> StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","Inspecting (",")">(this.EK());
            case 228171161 -> StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","Opening (",")">(this.EK());
            case 228171162 -> StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","Scanning (",")">(this.EK());
            case 228171163 -> "Inactive";
            case 228171164 -> "Session Waiting";
            case 228171165 -> "Junk Sell All";
            case 228171166 -> "Junk Drop";
            case 228171167 -> StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","Reopening (",")">(this.EK());
            default -> throw new MatchException(null, null);
         };
      }
   }

   private String EK() {
      return this.fx == GuiCycleController.CyclePhase.DropAll ? "Drop All" : "Sell All";
   }

   private void EL() {
      ScreenEvents.AFTER_INIT.register(new AfterInit() {
         public void afterInit(MinecraftClient var1, Screen var2, int var3, int var4) {
            SpawnerDropper.this.EM(var2, var1);
         }
      });
   }

   private void EM(Screen var1, final MinecraftClient var2) {
      if (var1 instanceof HandledScreen && this.yz.add(var1)) {
         ScreenEvents.afterRender(var1).register(new AfterRender() {
            public void afterRender(Screen var1, DrawContext var2x, int var3, int var4, float var5) {
               if (var2.currentScreen == var1) {
                  SpawnerDropper.this.EN(var2x);
               }
            }
         });
      }
   }

   private void EN(DrawContext var1) {
      if (this.isActive() && (Boolean)this.xH.get() && this.mc.player != null && var1 != null) {
         String[] var2 = new String[]{
            this.bB + "",
            this.GU + "",
            StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","State: ">(this.EJ()),
            StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Uptime: ">(EO(System.currentTimeMillis() - this.hV))
         };
         int[] var3 = new int[]{-1, -1, -8856321, -1};
         byte var4 = 4;
         byte var5 = 2;
         byte var6 = 9;
         int var7 = 0;

         for (String var11 : var2) {
            var7 = Math.max(var7, this.mc.textRenderer.getWidth(var11));
         }

         var7 += var4 * 2;
         int var14 = var2.length * var6 + (var2.length - 1) * var5 + var4 * 2 + 2;
         int var15 = this.mc.getWindow().getScaledWidth() / 2 - var7 - 10;
         int var16 = this.mc.getWindow().getScaledHeight() / 2 - var14 - 10;
         var1.fill(var15, var16, var15 + var7, var16 + var14, -1273752556);
         var1.fill(var15, var16, var15 + var7, var16 + 2, -11141206);
         int var17 = var16 + var4 + 3;

         for (int var12 = 0; var12 < var2.length; var12++) {
            var1.drawTextWithShadow(this.mc.textRenderer, var2[var12], var15 + var4, var17, var3[var12]);
            var17 += var6 + var5;
         }
      }
   }

   private static String EO(long var0) {
      long var2 = Math.max(0L, var0 / 1000L);
      long var4 = var2 / 3600L;
      long var6 = var2 % 3600L / 60L;
      long var8 = var2 % 60L;
      if (var4 > 0L) {
         return "" + var4 + var6 + var8;
      } else {
         return var6 > 0L ? "" + var6 + var8 : var8 + "";
      }
   }

   private static final class PendingDrop {
      private final int BG;
      private final int vJ;
      private final GuiCycleController.PendingAccounting Cl;

      private PendingDrop(int var1, int var2, GuiCycleController.PendingAccounting var3) {
         this.BG = var1;
         this.vJ = var2;
         this.Cl = var3;
      }
   }

   private static enum State {
      Inactive,
      Scanning,
      Opening,
      Inspecting,
      WaitingReopen,
      WaitingJunkThrow,
      WaitingJunkSellAll,
      SessionWaiting;
   }
}
