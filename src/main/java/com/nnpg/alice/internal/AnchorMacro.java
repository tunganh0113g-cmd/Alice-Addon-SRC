package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import meteordevelopment.meteorclient.events.world.TickEvent.Pre;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.KeybindSetting.Builder;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public final class AnchorMacro extends AliceModule implements GrimCombatCoordinator.Participant {
   private final SettingGroup rs = this.settings.getDefaultGroup();
   private final Setting<Keybind> Y = this.rs
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("active-key")).description("Phim kich hoat anchor 1 vong lap.")).defaultValue(Keybind.none()))
            .build()
      );
   private final Setting<Integer> nS = this.rs
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("explode-slot"))
                  .description("Slot hotbar (1-9) de switch khi kich no anchor."))
               .defaultValue(1))
            .min(1)
            .max(9)
            .sliderMax(9)
            .build()
      );
   private final Setting<Integer> AT = this.rs
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("place-delay"))
                  .description("So tick cho truoc khi dat anchor."))
               .defaultValue(0))
            .min(0)
            .sliderRange(0, 20)
            .build()
      );
   private final Setting<Integer> jf = this.rs
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("charge-delay"))
                  .description("So tick cho sau khi dat va truoc khi nap glowstone."))
               .defaultValue(0))
            .min(0)
            .sliderRange(0, 20)
            .build()
      );
   private final Setting<Integer> se = this.rs
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("explode-delay"))
                  .description("So tick cho sau khi nap va truoc khi kich no anchor."))
               .defaultValue(0))
            .min(0)
            .sliderRange(0, 20)
            .build()
      );
   private final Setting<Boolean> hS = this.rs
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("switch-back"))
                  .description("Tu dong switch ve anchor sau khi no."))
               .defaultValue(false))
            .build()
      );
   private boolean iV;
   private AnchorMacro.CycleStep ie;
   private ClientWorld yK;
   private BlockPos ss;
   private BlockHitResult CM;
   private int lJ;
   private boolean of;
   private int eC;
   private int nB = -1;

   public AnchorMacro() {
      super(AliceAddon.PVP_CATEGORY, "anchor-macro", "Tu dong thuc hien chuoi thao tac voi mo neo hoi sinh theo do tre da thiet lap.");
   }

   @Override
   public void onActivate() {
      this.bg();
      GrimCombatCoordinator.wG(this);
      super.onActivate();
   }

   @Override
   public void onDeactivate() {
      GrimCombatCoordinator.wH(this);
      this.bg();
      super.onDeactivate();
   }

   @AliceEventHandler
   private void aN(Pre var1) {
      boolean var2 = ((Keybind)this.Y.get()).isPressed();
      boolean var3 = this.bc();
      this.aO();
      if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
         if (this.iV && this.yK != this.mc.world) {
            this.be();
         }

         if (this.mc.currentScreen == null && !this.bb()) {
            boolean var4 = var3 && this.mc.player.getMainHandStack().isOf(Items.RESPAWN_ANCHOR);
            if (var2 || var4) {
               GrimCombatCoordinator.xp(this);
               if (var4) {
                  this.mc.options.useKey.setPressed(false);
               }

               if (!this.iV) {
                  this.aP(true);
               }

               int var10000 = this.iV ? '\ue448' : '\ue449';

               while (true) {
                  int var5 = var10000;
                  if (var5 == 58440) {
                     this.aQ();
                     break;
                  }

                  if (var5 != 58441) {
                     break;
                  }

                  var10000 = 58439;
               }
            }
         } else {
            this.be();
         }
      } else {
         this.be();
      }
   }

   private void aO() {
      if (this.of) {
         if (this.eC > 0) {
            this.eC--;
            if (this.eC > 0) {
               return;
            }
         }

         this.of = false;
         if (this.mc.player == null) {
            this.nB = -1;
         } else {
            int var1 = this.nB;
            this.nB = -1;
            if (var1 < 0 || var1 > 8 || !this.mc.player.getInventory().getStack(var1).isOf(Items.RESPAWN_ANCHOR)) {
               var1 = this.bd(Items.RESPAWN_ANCHOR);
            }

            if (var1 >= 0) {
               this.mc.player.getInventory().setSelectedSlot(var1);
            }
         }
      }
   }

   private void aP(boolean var1) {
      if (this.mc.crosshairTarget instanceof BlockHitResult var2 && var2.getType() == Type.BLOCK && this.aZ(var2.getPos())) {
         BlockPos var6 = var2.getBlockPos();
         BlockState var4 = this.mc.world.getBlockState(var6);
         this.yK = this.mc.world;
         this.CM = null;
         if (var4.isOf(Blocks.RESPAWN_ANCHOR)) {
            this.ss = var6.toImmutable();
            int var7 = (Integer)var4.get(RespawnAnchorBlock.CHARGES);
            this.aR(var7 <= 0 ? AnchorMacro.CycleStep.Charge : AnchorMacro.CycleStep.Explode);
         } else if (var1) {
            BlockPos var5 = this.aY(var2);
            if (var5 != null && this.mc.world.getBlockState(var5).isReplaceable()) {
               this.ss = var5.toImmutable();
               this.CM = this.ba(var2);
               this.aR(AnchorMacro.CycleStep.Place);
            } else {
               this.bf();
            }
         } else {
            this.bf();
         }
      }
   }

   private void aQ() {
      if (this.lJ > 0) {
         this.lJ--;
      } else {
         for (int var1 = 0; var1 < 3 && this.iV; var1++) {
            AnchorMacro.CycleStep var2 = this.ie;
            int var3 = this.aT(var2);
            if (var3 < 0) {
               this.be();
               return;
            }

            this.mc.player.getInventory().setSelectedSlot(var3);
            BlockHitResult var4 = this.aU(var2);
            if (var4 == null) {
               this.be();
               return;
            }

            this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, var4);
            this.mc.player.swingHand(Hand.MAIN_HAND);
            switch (var2) {
               case Place:
                  this.aR(AnchorMacro.CycleStep.Charge);
                  break;
               case Charge:
                  this.aR(AnchorMacro.CycleStep.Explode);
                  break;
               case Explode:
                  this.aS();
            }

            if (this.lJ > 0) {
               return;
            }
         }
      }
   }

   @Override
   public GrimCombatCoordinator.Intent prepareCombatIntent() {
      return null;
   }

   private void aR(AnchorMacro.CycleStep var1) {
      this.iV = true;
      this.ie = var1;
      int var2 = var1.ordinal();
      int var10001;
      if (var2 == 0) {
         var10001 = (Integer)this.AT.get();
      } else if (var2 == 1) {
         var10001 = (Integer)this.jf.get();
      } else {
         if (var2 != 2) {
            throw new MatchException(null, null);
         }

         var10001 = (Integer)this.se.get();
      }

      this.lJ = var10001;
   }

   private void aS() {
      if ((Boolean)this.hS.get()) {
         this.nB = this.bd(Items.RESPAWN_ANCHOR);
         this.eC = 1;
         this.of = true;
      }

      this.bf();
   }

   private int aT(AnchorMacro.CycleStep var1) {
      return switch (var1) {
         case Place -> this.bd(Items.RESPAWN_ANCHOR);
         case Charge -> this.bd(Items.GLOWSTONE);
         case Explode -> Math.max(0, Math.min(8, (Integer)this.nS.get() - 1));
      };
   }

   private BlockHitResult aU(AnchorMacro.CycleStep var1) {
      if (this.ss == null || this.mc.player == null) {
         return null;
      } else {
         return var1 != AnchorMacro.CycleStep.Place ? this.aW(this.ss) : this.aV();
      }
   }

   private BlockHitResult aV() {
      if (this.mc.player.raycast(this.mc.player.getBlockInteractionRange(), 1.0F, false) instanceof BlockHitResult var1) {
         if (var1.getType() == Type.BLOCK && this.aZ(var1.getPos()) && this.ss.equals(this.aY(var1))) {
            this.CM = this.ba(var1);
            return this.CM;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private BlockHitResult aW(BlockPos var1) {
      Vec3d var2 = this.mc.player.getEyePos();
      double var3 = this.mc.player.getBlockInteractionRange();
      Vec3d var5 = var2.add(this.mc.player.getRotationVec(1.0F).multiply(var3));
      Vec3d var6 = (Vec3d)new Box(var1).raycast(var2, var5).orElse(null);
      return var6 != null && this.aZ(var6) ? new BlockHitResult(var6, this.aX(var1, var6), var1, false) : null;
   }

   private Direction aX(BlockPos var1, Vec3d var2) {
      double var3 = Math.abs(var2.x - var1.getX());
      double var5 = Math.abs(var2.x - (var1.getX() + 1.0));
      double var7 = Math.abs(var2.y - var1.getY());
      double var9 = Math.abs(var2.y - (var1.getY() + 1.0));
      double var11 = Math.abs(var2.z - var1.getZ());
      double var13 = Math.abs(var2.z - (var1.getZ() + 1.0));
      double var15 = var3;
      Direction var17 = Direction.WEST;
      if (var5 < var3) {
         var15 = var5;
         var17 = Direction.EAST;
      }

      if (var7 < var15) {
         var15 = var7;
         var17 = Direction.DOWN;
      }

      if (var9 < var15) {
         var15 = var9;
         var17 = Direction.UP;
      }

      if (var11 < var15) {
         var15 = var11;
         var17 = Direction.NORTH;
      }

      if (var13 < var15) {
         var17 = Direction.SOUTH;
      }

      return var17;
   }

   private BlockPos aY(BlockHitResult var1) {
      if (this.mc.player != null && var1 != null) {
         ItemPlacementContext var2 = new ItemPlacementContext(this.mc.player, Hand.MAIN_HAND, new ItemStack(Items.RESPAWN_ANCHOR), var1);
         return var2.getBlockPos();
      } else {
         return null;
      }
   }

   private boolean aZ(Vec3d var1) {
      double var2 = this.mc.player.getBlockInteractionRange();
      return this.mc.player.getEyePos().squaredDistanceTo(var1) <= var2 * var2;
   }

   private BlockHitResult ba(BlockHitResult var1) {
      return new BlockHitResult(var1.getPos(), var1.getSide(), var1.getBlockPos().toImmutable(), var1.isInsideBlock());
   }

   private boolean bb() {
      boolean var1 = this.mc.player.getMainHandStack().getItem().getComponents().contains(DataComponentTypes.FOOD)
         || this.mc.player.getOffHandStack().getItem().getComponents().contains(DataComponentTypes.FOOD);
      boolean var2 = this.mc.player.getMainHandStack().getItem() instanceof ShieldItem || this.mc.player.getOffHandStack().getItem() instanceof ShieldItem;
      char var7;
      if (!var1 && !var2) {
         while (var7 == 'ꛌ') {
            var7 = 'ꛊ';
         }
      } else if (this.mc.player.isUsingItem()) {
         return true;
      }

      return false;
   }

   private boolean bc() {
      return this.mc.getWindow() == null ? false : GLFW.glfwGetMouseButton(this.mc.getWindow().getHandle(), 1) == 1;
   }

   private int bd(Item var1) {
      if (this.mc.player == null) {
         return -1;
      } else {
         for (int var2 = 0; var2 < 9; var2++) {
            if (this.mc.player.getInventory().getStack(var2).isOf(var1)) {
               return var2;
            }
         }

         return -1;
      }
   }

   @Override
   public boolean canYieldEpoch() {
      return true;
   }

   @Override
   public boolean reservesLowerPriorityEpochs() {
      return false;
   }

   @Override
   public void yieldToHigherPriority() {
   }

   @Override
   public int combatPriority() {
      return 300;
   }

   @Override
   public void suspendForItemUse() {
      this.be();
   }

   private void be() {
      this.bf();
   }

   private void bf() {
      this.iV = false;
      this.ie = null;
      this.yK = null;
      this.ss = null;
      this.CM = null;
      this.lJ = 0;
   }

   private void bg() {
      this.bf();
      this.of = false;
      this.eC = 0;
      this.nB = -1;
   }

   private static enum CycleStep {
      Place,
      Charge,
      Explode;
   }
}
