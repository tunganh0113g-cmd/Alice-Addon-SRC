package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import meteordevelopment.meteorclient.events.entity.player.PlayerTickMovementEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent.Post;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.BoolSetting.Builder;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterInit;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterRender;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CrafterScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

public class CrafterSetup extends AliceModule {
   private static final int qX = 9;
   private static final int tl = 20;
   private static final Color gv = new Color(40, 220, 100, 45);
   private static final Color gr = new Color(80, 255, 130, 220);
   private static final Color fi = new Color(255, 210, 45, 45);
   private static final Color wp = new Color(255, 225, 70, 230);
   private static final int mk = -1273752556;
   private static final int HQ = -11141206;
   private static final int eX = -8847496;
   private static final int xh = -9131;
   private final SettingGroup BH = this.settings.getDefaultGroup();
   private final SettingGroup gW = this.settings.createGroup("Pattern");
   private final SettingGroup j = this.settings.createGroup("HUD");
   private final List<Setting<Boolean>> wK = new ArrayList<>(9);
   private final Setting<Boolean> Es = this.BH
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("crafter-aura")).description("lang le doi huong xoay de dieu chinh crafter ma khong xoay camera"))
               .defaultValue(false))
            .build()
      );
   private final Setting<Integer> lv = this.BH
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("max-render-crafter"))
                  .description("so luong crafter toi da de hien thi tren ESP, tracer va HUD"))
               .defaultValue(64))
            .range(1, 256)
            .sliderRange(1, 128)
            .build()
      );
   private final Setting<Boolean> ag = this.j
      .add(((Builder)((Builder)((Builder)new Builder().name("show-hud")).description("hien thi so luong crafter dang cho")).defaultValue(true)).build());
   private final Set<Screen> nI = Collections.newSetFromMap(new WeakHashMap<>());
   private final Map<BlockPos, Long> ir = new HashMap<>();
   private final Set<BlockPos> eD = new HashSet<>();
   private List<BlockPos> mZ = List.of();
   private List<BlockPos> ds = List.of();
   private ClientWorld gR;
   private CrafterSetup.AuraState As = CrafterSetup.AuraState.Idle;
   private BlockPos dh;
   private boolean Do;
   private boolean EX;
   private long vT;
   private long et = -1L;
   private long zi = -1L;
   private Input fM;
   private CrafterSetup.AuraInput fN;
   private CrafterSetup.AppliedAuraRotation pU;
   private CrafterSetup.AuraRotationPlan kG;
   private int sg;

   public CrafterSetup() {
      super(AliceAddon.CATEGORY, "CrafterSetup", "Ap dung mau khoa o 3x3 da luu va co the tu dong thiet lap cac may che tao o gan.");

      for (int var1 = 0; var1 < 9; var1++) {
         int var2 = var1 + 1;
         this.wK
            .add(
               this.gW
                  .add(
                     ((Builder)((Builder)((Builder)((Builder)new Builder().name(var2 + "")).description(var2 + "")).defaultValue(false)).visible(() -> false))
                        .build()
                  )
            );
      }

      this.sg = this.lR();
      this.lV();
   }

   @Override
   public void onActivate() {
      super.onActivate();
      this.lT();
   }

   @Override
   public void onDeactivate() {
      super.onDeactivate();
      this.lT();
   }

   public WWidget getWidget(GuiTheme var1) {
      WTable var2 = var1.table();
      var2.add(var1.label("Crafter Pattern (checked = disabled)"));
      var2.row();
      WTable var3 = var1.table();

      label33:
      for (int var4 = 0; var4 < 3; var4++) {
         int var5 = 0;

         while (true) {
            int var10000 = var5 >= 3 ? '큍' : '큌';

            while (true) {
               int var9 = var10000;
               if (var9 == 53324) {
                  int var6 = var4 * 3 + var5;
                  Setting var7 = this.wK.get(var6);
                  WCheckbox var8 = (WCheckbox)var3.add(var1.checkbox((Boolean)var7.get())).widget();
                  var8.action = () -> var7.set(var8.checked);
                  var5++;
                  break;
               }

               if (var9 != 53325) {
                  var3.row();
                  continue label33;
               }

               var10000 = 53323;
            }
         }
      }

      var2.add(var3);
      return var2;
   }

   @AliceEventHandler
   public void lo(OpenScreenEvent var1) {
      if (this.isActive() && var1.screen instanceof CrafterScreen) {
         this.lB();
         this.Do = true;
         this.EX = (Boolean)this.Es.get() && this.dh != null && this.As == CrafterSetup.AuraState.WaitingForScreen;
      }
   }

   @AliceEventHandler
   public void lp(Pre var1) {
      if (this.isActive()) {
         this.vT++;
         if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
            if (this.gR != this.mc.world) {
               this.lU();
               this.gR = this.mc.world;
            }

            int var2 = this.lR();
            if (var2 != this.sg) {
               this.sg = var2;
               this.ir.clear();
               this.lS();
            }

            this.ly();
            if (this.Do && this.mc.player.currentScreenHandler instanceof CrafterScreenHandler var3) {
               this.lu(var3);
            }

            if (this.zi >= 0L && this.vT >= this.zi) {
               this.lx();
            } else if (!(Boolean)this.Es.get()) {
               this.lS();
               this.lE();
            } else {
               this.lD();
               if (this.As == CrafterSetup.AuraState.WaitingForClick) {
                  this.lC();
               } else if (this.As == CrafterSetup.AuraState.WaitingForScreen) {
                  if (this.vT >= this.et) {
                     this.lS();
                  }
               } else {
                  char var5 = '謨';
                  if (this.As != CrafterSetup.AuraState.WaitingForClose) {
                     char var11;
                     while (var11 == 'ꯧ') {
                        var11 = 'ꯥ';
                     }

                     if (this.mc.currentScreen == null) {
                        if (this.dh == null) {
                           this.dh = this.lL();
                           if (this.dh == null) {
                              this.As = CrafterSetup.AuraState.Idle;
                              return;
                           }

                           this.As = CrafterSetup.AuraState.Rotating;
                        }
                     }
                  }
               }
            }
         } else {
            this.lU();
         }
      }
   }

   @AliceEventHandler(priority = 90)
   public void lq(PlayerTickMovementEvent var1) {
      if (this.isActive()) {
         int var10000 = this.Es.get() ? '\uf4fe' : '\uf4fd';

         while (true) {
            switch (var10000) {
               case 62717:
                  return;
               case 62718:
                  var10000 = 62716;
                  break;
               default:
                  this.lD();
                  return;
            }
         }
      }
   }

   @AliceEventHandler(priority = -50)
   public void lr(Post var1) {
      CrafterSetup.AppliedAuraRotation var2 = this.pU;
      if (var2 != null) {
         this.lH(var2);
         this.pU = null;
         if (this.isActive() && (Boolean)this.Es.get() && this.As == CrafterSetup.AuraState.Rotating && this.dh != null && this.dh.equals(var2.nW.target())) {
            this.kG = var2.nW;
            this.As = CrafterSetup.AuraState.WaitingForClick;
         }
      }
   }

   @AliceEventHandler
   public void ls(Render3DEvent var1) {
      if (this.isActive() && this.mc.player != null && this.mc.world != null) {
         Vec3d var2 = this.mc.player.getLerpedPos(var1.tickDelta);
         Vec3d var3 = new Vec3d(var2.x, var2.y + this.mc.player.getEyeHeight(this.mc.player.getPose()), var2.z);
         if (this.mc.options.getPerspective().isFirstPerson()) {
            var3 = var3.add(this.mc.player.getRotationVector().multiply(0.45));
         }

         for (BlockPos var5 : this.ds) {
            boolean var6 = this.lO(var5);
            var1.renderer.box(var5, var6 ? gv : fi, !var6 ? wp : gr, ShapeMode.Both, 0);
            if (!var6) {
               Vec3d var7 = Vec3d.ofCenter(var5);
               var1.renderer.line(var3.x, var3.y, var3.z, var7.x, var7.y, var7.z, wp);
            }
         }
      }
   }

   @AliceEventHandler
   public void lt(Render2DEvent var1) {
      if (!(this.mc.currentScreen instanceof HandledScreen)) {
         this.lX(var1.drawContext);
      }
   }

   private void lu(CrafterScreenHandler var1) {
      this.Do = false;
      BlockPos var2 = this.lv(var1);

      for (int var3 = 0; var3 < 9; var3++) {
         boolean var4 = (Boolean)this.wK.get(var3).get();
         if (var1.isSlotDisabled(var3) != var4) {
            boolean var5 = CombatMathHelper.DC(var4);
            var1.setSlotEnabled(var3, var5);
            this.mc.interactionManager.slotChangedState(var3, var1.syncId, var5);
         }
      }

      if (var2 != null) {
         this.eD.add(var2);
         this.lw(var2);
         this.ir.put(var2, this.vT);
      }

      if (this.EX && this.dh != null) {
         this.As = CrafterSetup.AuraState.WaitingForClose;
         this.zi = this.vT + 1L;
      }

      this.EX = false;
   }

   private BlockPos lv(CrafterScreenHandler var1) {
      if (this.dh != null && this.As == CrafterSetup.AuraState.WaitingForScreen) {
         return this.dh;
      } else {
         return var1.getInputInventory() instanceof CrafterBlockEntity var2 ? var2.getPos().toImmutable() : null;
      }
   }

   private void lw(BlockPos var1) {
      if (this.mc.world.getBlockEntity(var1) instanceof CrafterBlockEntity var3) {
         for (int var4 = 0; var4 < 9; var4++) {
            boolean var5 = CombatMathHelper.DC((Boolean)this.wK.get(var4).get());
            var3.setSlotEnabled(var4, var5);
         }
      }
   }

   private void lx() {
      this.zi = -1L;
      if (this.mc.player != null && this.mc.player.currentScreenHandler instanceof CrafterScreenHandler) {
         this.mc.player.closeHandledScreen();
      }

      this.lS();
   }

   private void ly() {
      this.lA();
      if (this.eD.isEmpty() || this.vT % 10L == 1L) {
         this.lz();
      }

      for (BlockEntity var2 : this.mc.world.getBlockEntities()) {
         if (var2 instanceof CrafterBlockEntity) {
            this.eD.add(var2.getPos().toImmutable());
         }
      }

      this.eD.removeIf(var1 -> !this.lQ(var1));
      this.ir.keySet().removeIf(var1 -> !this.lQ(var1));
      ArrayList var4 = new ArrayList<>(this.eD);
      Vec3d var5 = this.mc.player.getEyePos();
      var4.sort(Comparator.comparingDouble(var1 -> Vec3d.ofCenter(var1).squaredDistanceTo(var5)));
      this.mZ = List.copyOf(var4);
      int var3 = CombatMathHelper.DE(var4.size(), (Integer)this.lv.get());
      this.ds = List.copyOf(var4.subList(0, var3));
   }

   private void lz() {
      int var1 = (Integer)this.mc.options.getViewDistance().getValue() + 1;
      int var2 = this.mc.player.getBlockX() >> 4;
      int var3 = this.mc.player.getBlockZ() >> 4;

      for (int var4 = var2 - var1; var4 <= var2 + var1; var4++) {
         for (int var5 = var3 - var1; var5 <= var3 + var1; var5++) {
            WorldChunk var6 = this.mc.world.getChunkManager().getChunk(var4, var5, ChunkStatus.FULL, false);
            if (var6 != null) {
               for (Entry var8 : var6.getBlockEntities().entrySet()) {
                  BlockPos var9 = (BlockPos)var8.getKey();
                  if (var8.getValue() instanceof CrafterBlockEntity || var6.getBlockState(var9).isOf(Blocks.CRAFTER)) {
                     this.eD.add(var9.toImmutable());
                  }
               }
            }
         }
      }
   }

   private void lA() {
      Vec3d var1 = this.mc.player.getEyePos();
      int var2 = MathHelper.ceil(this.mc.player.getBlockInteractionRange()) + 1;
      BlockPos var3 = BlockPos.ofFloored(var1);
      BlockPos var4 = var3.add(-var2, -var2, -var2);
      BlockPos var5 = var3.add(var2, var2, var2);

      for (BlockPos var7 : BlockPos.iterate(var4, var5)) {
         if (this.mc.world.getBlockState(var7).isOf(Blocks.CRAFTER)) {
            this.eD.add(var7.toImmutable());
         }
      }
   }

   private void lB() {
      if (this.dh != null && this.lQ(this.dh)) {
         this.eD.add(this.dh.toImmutable());
      } else {
         if (this.mc.crosshairTarget instanceof BlockHitResult var1 && var1.getType() == Type.BLOCK && this.lQ(var1.getBlockPos())) {
            this.eD.add(var1.getBlockPos().toImmutable());
         }
      }
   }

   private void lC() {
      CrafterSetup.AuraRotationPlan var1 = this.kG;
      this.kG = null;
      if (this.isActive()
         && (Boolean)this.Es.get()
         && this.As == CrafterSetup.AuraState.WaitingForClick
         && var1 != null
         && this.dh != null
         && this.dh.equals(var1.target())
         && this.mc.player != null
         && this.mc.interactionManager != null
         && this.lQ(var1.target())
         && this.lM(var1.target())) {
         BlockHitResult var2 = this.lI(this.mc.player.getEyePos(), var1.yaw(), var1.pitch());
         if (var2.getType() == Type.BLOCK && var2.getBlockPos().equals(var1.target())) {
            float var3 = this.mc.player.getYaw();
            float var4 = this.mc.player.getPitch();
            this.mc.player.setYaw(var1.yaw());
            this.mc.player.setPitch(var1.pitch());

            ActionResult var5;
            try {
               var5 = this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, var2);
            } finally {
               this.mc.player.setYaw(var3);
               this.mc.player.setPitch(var4);
            }

            if (!var5.isAccepted()) {
               this.lS();
            } else {
               this.As = CrafterSetup.AuraState.WaitingForScreen;
               this.et = this.vT + 20L;
            }
         } else {
            this.As = CrafterSetup.AuraState.Rotating;
         }
      } else {
         this.lS();
      }
   }

   private void lD() {
      ClientPlayerEntity var1 = this.mc.player;
      if (var1 != null) {
         if (this.fN == null || var1.input != this.fN) {
            this.fM = var1.input;
            this.fN = new CrafterSetup.AuraInput(this.fM);
            var1.input = this.fN;
         }
      }
   }

   private void lE() {
      CrafterSetup.AppliedAuraRotation var1 = this.pU;
      if (var1 != null) {
         this.lH(var1);
         this.pU = null;
      }

      if (this.fN != null) {
         DelegatingInput.qM(this.mc.player, this.fN);
      }

      this.fN = null;
      this.fM = null;
   }

   private void lF(CrafterSetup.AuraInput var1, PlayerInput var2, Vec2f var3) {
      ClientPlayerEntity var4 = this.mc.player;
      BlockPos var5 = this.dh;
      if (var4 != null
         && var5 != null
         && this.pU == null
         && this.As == CrafterSetup.AuraState.Rotating
         && this.isActive()
         && (Boolean)this.Es.get()
         && this.lQ(var5)
         && this.lM(var5)) {
         char var13;
         while (var13 == '\ue4cb') {
            var13 = '\ue4c9';
         }

         BlockHitResult var6 = this.lN(var5);
         if (var6 == null) {
            this.lS();
         } else {
            CrafterSetup.AuraRotationPlan var7 = this.lG(var5, var2, var3);
            if (var7 == null) {
               this.lS();
            } else {
               boolean var8 = var7.stopSprinting() && var4.isSprinting();
               CrafterSetup.AppliedAuraRotation var9 = new CrafterSetup.AppliedAuraRotation(
                  var7, var4, var4.getYaw(), var4.getPitch(), var2, var3, var4.isSprinting(), var8
               );
               this.pU = var9;
               var1.setState(var7.input(), var7.movement());
               if (var8) {
                  var4.setSprinting(false);
               }

               var4.setYaw(var7.yaw());
               var4.setPitch(var7.pitch());
            }
         }
      }
   }

   private CrafterSetup.AuraRotationPlan lG(BlockPos var1, PlayerInput var2, Vec2f var3) {
      ClientPlayerEntity var4 = this.mc.player;
      if (var4 == null) {
         return null;
      } else {
         Vec3d var5;
         float var6;
         float var7;
         float[] var8;
         boolean var10000;
         label111: {
            var5 = var4.getEyePos();
            var6 = var4.getYaw();
            var7 = var4.getPitch();
            var8 = lJ(var5, Vec3d.ofCenter(var1));
            if (var3 != null) {
               if (var3.lengthSquared() > 1.0E-5F) {
                  var10000 = 1;
                  break label111;
               }

               char var32;
               while (var32 == '襋') {
                  var32 = '襉';
               }
            }

            var10000 = 0;
         }

         boolean var9 = (boolean)var10000;
         CrafterSetup.AuraRotationPlan var10 = null;
         double var11 = Double.MAX_VALUE;
         if (var9) {
            for (CrafterSetup.InputDirection var16 : CrafterSetup.InputDirection.GE) {
               float var17 = CombatMathHelper.DI(var6, var3.x, var3.y, var16.sidewaysValue, var16.forwardValue);
               float var18 = var8[1];
               BlockHitResult var19 = this.lI(var5, var17, var18);
               if (var19.getType() == Type.BLOCK && var19.getBlockPos().equals(var1) && (!var4.isSprinting() && !var2.sprint() || var16.forward)) {
                  double var20 = Math.abs(MathHelper.wrapDegrees(var17 - var6)) + Math.abs(var18 - var7) * 0.2;
                  short var23;
                  if (var16.lg(var2)) {
                     var20 -= 40.0;
                  } else {
                     while (var23 == 2593) {
                        var23 = 2591;
                     }
                  }

                  if (var16.lh(var2)) {
                     var20 -= 8.0;
                  }

                  if (var2.sprint()) {
                     var20 += !var16.forward ? 160.0 : -80.0;
                  }

                  if (var20 < var11) {
                     var11 = var20;
                     var10 = new CrafterSetup.AuraRotationPlan(var1, var17, var18, var16.lf(var2), var16.le(), false);
                  }
               }
            }
         }

         if (var10 != null) {
            return var10;
         } else {
            float[] var24 = lJ(var5, Vec3d.ofCenter(var1));
            BlockHitResult var25 = this.lI(var5, var24[0], var24[1]);
            if (var25.getType() == Type.BLOCK) {
               var10000 = var25.getBlockPos().equals(var1) ? '起' : '赶';

               while (true) {
                  switch (var10000) {
                     case 36214:
                        return null;
                     case 36215:
                        var10000 = 36213;
                        break;
                     default:
                        return new CrafterSetup.AuraRotationPlan(var1, var24[0], var24[1], lK(var2), Vec2f.ZERO, true);
                  }
               }
            } else {
               return null;
            }
         }
      }
   }

   private void lH(CrafterSetup.AppliedAuraRotation var1) {
      if (this.mc.player == var1.eT) {
         this.mc.player.setYaw(var1.oF);
         this.mc.player.setPitch(var1.IQ);
      }

      if (this.fN != null && this.mc.player != null && this.mc.player.input == this.fN) {
         this.fN.setState(var1.fd, var1.tL);
      }

      if (var1.tY && this.mc.player == var1.eT) {
         this.mc.player.setSprinting(var1.ic);
      }
   }

   private BlockHitResult lI(Vec3d var1, float var2, float var3) {
      Vec3d var4 = Vec3d.fromPolar(var3, var2);
      Vec3d var5 = var1.add(var4.multiply(this.mc.player.getBlockInteractionRange()));
      return this.mc.world.raycast(new RaycastContext(var1, var5, ShapeType.OUTLINE, FluidHandling.NONE, this.mc.player));
   }

   private static float[] lJ(Vec3d var0, Vec3d var1) {
      Vec3d var2 = var1.subtract(var0);
      float var3 = MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(var2.z, var2.x)) - 90.0F);
      float var4 = MathHelper.clamp((float)(-Math.toDegrees(Math.atan2(var2.y, Math.hypot(var2.x, var2.z)))), -90.0F, 90.0F);
      return new float[]{var3, var4};
   }

   private static PlayerInput lK(PlayerInput var0) {
      return new PlayerInput(false, false, false, false, var0.jump(), var0.sneak(), false);
   }

   private BlockPos lL() {
      for (BlockPos var2 : this.mZ) {
         if (!this.lO(var2) && this.lM(var2) && this.lN(var2) != null) {
            return var2;
         }
      }

      return null;
   }

   private boolean lM(BlockPos var1) {
      Vec3d var2 = this.mc.player.getEyePos();
      Box var3 = new Box(var1);
      double var4 = MathHelper.clamp(var2.x, var3.minX, var3.maxX);
      double var6 = MathHelper.clamp(var2.y, var3.minY, var3.maxY);
      double var8 = MathHelper.clamp(var2.z, var3.minZ, var3.maxZ);
      double var10 = this.mc.player.getBlockInteractionRange();
      return var2.squaredDistanceTo(var4, var6, var8) <= var10 * var10;
   }

   private BlockHitResult lN(BlockPos var1) {
      Vec3d var2 = this.mc.player.getEyePos();
      Vec3d var3 = Vec3d.ofCenter(var1);
      BlockHitResult var4 = this.mc.world.raycast(new RaycastContext(var2, var3, ShapeType.OUTLINE, FluidHandling.NONE, this.mc.player));
      return var4.getType() == Type.BLOCK && var4.getBlockPos().equals(var1) ? var4 : null;
   }

   private boolean lO(BlockPos var1) {
      if ((this.mc.world == null ? null : this.mc.world.getBlockEntity(var1)) instanceof CrafterBlockEntity var3 && this.lP(var3)) {
         this.ir.put(var1, this.vT);
         return true;
      } else {
         return this.ir.containsKey(var1);
      }
   }

   private boolean lP(CrafterBlockEntity var1) {
      for (int var2 = 0; var2 < 9; var2++) {
         if (var1.isSlotDisabled(var2) != (Boolean)this.wK.get(var2).get()) {
            return false;
         }
      }

      return true;
   }

   private boolean lQ(BlockPos var1) {
      return this.mc.world != null && this.mc.world.getBlockState(var1).isOf(Blocks.CRAFTER);
   }

   private int lR() {
      boolean[] var1 = new boolean[9];

      for (int var2 = 0; var2 < 9; var2++) {
         var1[var2] = (Boolean)this.wK.get(var2).get();
      }

      return CombatMathHelper.DD(var1);
   }

   private void lS() {
      if (this.pU != null) {
         this.lH(this.pU);
         this.pU = null;
      }

      this.As = CrafterSetup.AuraState.Idle;
      this.dh = null;
      this.kG = null;
      this.EX = false;
      this.et = -1L;
      this.zi = -1L;
   }

   private void lT() {
      this.lE();
      this.gR = this.mc.world;
      this.vT = 0L;
      this.sg = this.lR();
      this.ir.clear();
      this.eD.clear();
      this.mZ = List.of();
      this.ds = List.of();
      this.Do = false;
      this.lS();
   }

   private void lU() {
      this.lE();
      this.gR = null;
      this.ir.clear();
      this.eD.clear();
      this.mZ = List.of();
      this.ds = List.of();
      this.Do = false;
      this.lS();
   }

   private void lV() {
      ScreenEvents.AFTER_INIT.register(new AfterInit() {
         public void afterInit(MinecraftClient var1, Screen var2, int var3, int var4) {
            CrafterSetup.this.lW(var2, var1);
         }
      });
   }

   private void lW(Screen var1, final MinecraftClient var2) {
      if (var1 instanceof HandledScreen && this.nI.add(var1)) {
         ScreenEvents.afterRender(var1).register(new AfterRender() {
            public void afterRender(Screen var1, DrawContext var2x, int var3, int var4, float var5) {
               if (var2.currentScreen == var1) {
                  CrafterSetup.this.lX(var2x);
               }
            }
         });
      }
   }

   private void lX(DrawContext var1) {
      if (this.isActive() && (Boolean)this.ag.get() && this.mc.player != null && var1 != null) {
         int var2 = 0;

         for (BlockPos var4 : this.ds) {
            if (this.lO(var4)) {
               var2++;
            }
         }

         int var15 = CombatMathHelper.DF(this.ds.size(), var2);
         String[] var16 = new String[]{var2 + "", "" + var15 + this.lv.get()};
         int[] var5 = new int[]{-8847496, -9131};
         byte var6 = 4;
         byte var7 = 2;
         byte var8 = 9;
         int var9 = 0;

         for (String var13 : var16) {
            var9 = Math.max(var9, this.mc.textRenderer.getWidth(var13));
         }

         var9 += var6 * 2;
         int var18 = var16.length * var8 + var7 + var6 * 2 + 2;
         byte var19 = 10;
         int var20 = this.mc.getWindow().getScaledHeight() / 2 - var18 / 2;
         var1.fill(10, var20, 10 + var9, var20 + var18, -1273752556);
         var1.fill(10, var20, 10 + var9, var20 + 2, -11141206);
         int var21 = var20 + var6 + 3;

         for (int var14 = 0; var14 < var16.length; var14++) {
            var1.drawTextWithShadow(this.mc.textRenderer, var16[var14], var19 + var6, var21, var5[var14]);
            var21 += var8 + var7;
         }
      }
   }

   private static final class AppliedAuraRotation {
      private final CrafterSetup.AuraRotationPlan nW;
      private final ClientPlayerEntity eT;
      private final float oF;
      private final float IQ;
      private final PlayerInput fd;
      private final Vec2f tL;
      private final boolean ic;
      private final boolean tY;

      private AppliedAuraRotation(
         CrafterSetup.AuraRotationPlan var1, ClientPlayerEntity var2, float var3, float var4, PlayerInput var5, Vec2f var6, boolean var7, boolean var8
      ) {
         this.nW = var1;
         this.eT = var2;
         this.oF = var3;
         this.IQ = var4;
         this.fd = var5;
         this.tL = var6;
         this.ic = var7;
         this.tY = var8;
      }
   }

   private final class AuraInput extends DelegatingInput {
      private AuraInput(Input var2) {
         super(var2);
      }

      @Override
      protected void qL() {
         PlayerInput var1 = this.qI().playerInput;
         Vec2f var2 = this.qI().getMovementInput();
         CrafterSetup.this.lF(this, var1, var2);
      }

      public void jump() {
         super.jump();
      }
   }

   private record AuraRotationPlan(BlockPos target, float yaw, float pitch, PlayerInput input, Vec2f movement, boolean stopSprinting) {
   }

   private static enum AuraState {
      Idle,
      Rotating,
      WaitingForClick,
      WaitingForScreen,
      WaitingForClose;
   }

   private record InputDirection(boolean forward, boolean backward, boolean left, boolean right, float sidewaysValue, float forwardValue) {
      private static final CrafterSetup.InputDirection[] GE = new CrafterSetup.InputDirection[]{
         new CrafterSetup.InputDirection(true, false, false, false, 0.0F, 1.0F),
         new CrafterSetup.InputDirection(true, false, true, false, 1.0F, 1.0F),
         new CrafterSetup.InputDirection(false, false, true, false, 1.0F, 0.0F),
         new CrafterSetup.InputDirection(false, true, true, false, 1.0F, -1.0F),
         new CrafterSetup.InputDirection(false, true, false, false, 0.0F, -1.0F),
         new CrafterSetup.InputDirection(false, true, false, true, -1.0F, -1.0F),
         new CrafterSetup.InputDirection(false, false, false, true, -1.0F, 0.0F),
         new CrafterSetup.InputDirection(true, false, false, true, -1.0F, 1.0F)
      };

      private Vec2f le() {
         return new Vec2f(this.sidewaysValue, this.forwardValue).normalize();
      }

      private PlayerInput lf(PlayerInput var1) {
         return new PlayerInput(this.forward, this.backward, this.left, this.right, var1.jump(), var1.sneak(), var1.sprint());
      }

      private boolean lg(PlayerInput var1) {
         return var1.forward() == this.forward && var1.backward() == this.backward && var1.left() == this.left && var1.right() == this.right;
      }

      private boolean lh(PlayerInput var1) {
         boolean var2 = var1.forward() != var1.backward();
         boolean var3 = var1.left() != var1.right();
         boolean var4 = this.forward != this.backward;
         boolean var5 = this.left != this.right;
         return (var2 && var3) == (var4 && var5);
      }
   }
}
