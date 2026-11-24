package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import meteordevelopment.meteorclient.events.world.TickEvent.Post;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.KeybindSetting.Builder;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

public final class KeyPearl extends AliceModule implements GrimCombatCoordinator.Participant {
   private static final int Cd = 250;
   private static final MethodHandle wc = Bf();
   private final Setting<Keybind> AI = this.settings
      .getDefaultGroup()
      .add(((Builder)((Builder)new Builder().name("throw-key")).description("Nem pearl silent; slot dang chon tren client khong thay doi.")).build());
   private boolean Cz;
   private boolean hl;
   private int Gh = -1;

   public KeyPearl() {
      super(AliceAddon.PVP_CATEGORY, "key-pearl", "Nem ngoc Ender silent va tu nap lai dung o hotbar da ghi nho.");
   }

   @Override
   public void onActivate() {
      this.Cz = false;
      this.hl = false;
      this.Gh = this.Bd();
      GrimCombatCoordinator.wG(this);
      super.onActivate();
   }

   @Override
   public void onDeactivate() {
      GrimCombatCoordinator.wH(this);
      this.Cz = false;
      this.hl = false;
      this.Gh = -1;
      super.onDeactivate();
   }

   @AliceEventHandler
   private void AZ(Post var1) {
      if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
         int var2 = this.Bd();
         if (var2 >= 0) {
            this.Gh = var2;
         }

         int var5;
         label50: {
            if (this.mc.currentScreen == null) {
               var5 = !this.mc.player.isUsingItem() ? '킋' : '킌';

               while (true) {
                  int var4 = var5;
                  if (var4 == 53387) {
                     if (((Keybind)this.AI.get()).isPressed()) {
                        var5 = 1;
                        break label50;
                     }
                     break;
                  }

                  if (var4 != 53388) {
                     break;
                  }

                  var5 = 53386;
               }
            }

            var5 = 0;
         }

         boolean var3 = (boolean)var5;
         if (!var3) {
            this.Cz = false;
         } else if (!this.Cz) {
            this.Cz = true;
            this.hl = true;
         }

         if (this.hl && var2 < 0) {
            this.Bc();
         }
      } else {
         this.Cz = false;
         this.hl = false;
      }
   }

   @Override
   public GrimCombatCoordinator.Intent prepareCombatIntent() {
      if (this.hl && this.mc.player != null && this.mc.world != null) {
         final int var1 = this.Bd();
         if (var1 < 0) {
            return null;
         } else {
            this.Gh = var1;
            return new GrimCombatCoordinator.Intent() {
               @Override
               public GrimCombatCoordinator.Participant owner() {
                  return KeyPearl.this;
               }

               @Override
               public void execute() {
                  KeyPearl.this.Ba(var1);
               }
            };
         }
      } else {
         return null;
      }
   }

   private void Ba(int var1) {
      if (this.hl
         && this.mc.player != null
         && this.mc.world != null
         && this.mc.player.getInventory().getStack(var1).isOf(Items.ENDER_PEARL)
         && GrimCombatCoordinator.wO(this)
         && GrimCombatCoordinator.wI(this, var1)) {
         try {
            if (this.Bb()) {
               this.hl = false;
            }
         } finally {
            GrimCombatCoordinator.wJ(this);
         }
      }
   }

   private boolean Bb() {
      if (wc == null) {
         return false;
      } else {
         float var1 = this.mc.player.getYaw();
         float var2 = this.mc.player.getPitch();
         SequencedPacketCreator var3 = var2x -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, var2x, var1, var2);
         int var4 = GrimCombatCoordinator.xG();
         wc.invoke((ClientPlayerInteractionManager)this.mc.interactionManager, (ClientWorld)this.mc.world, (SequencedPacketCreator)var3);
         return GrimCombatCoordinator.xG() > var4;
      }
   }

   private boolean Bc() {
      if (this.mc.player != null
         && this.mc.interactionManager != null
         && this.mc.currentScreen == null
         && this.mc.player.currentScreenHandler == this.mc.player.playerScreenHandler) {
         int var1 = this.Be();
         if (var1 < 0) {
            this.hl = false;
            return false;
         } else {
            if (this.Gh < 0 || this.Gh > 8) {
               this.Gh = this.mc.player.getInventory().getSelectedSlot();
            }

            int var2 = SlotUtils.indexToId(var1);
            if (var2 < 0
               || var2 >= this.mc.player.playerScreenHandler.slots.size()
               || !this.mc.player.playerScreenHandler.getSlot(var2).getStack().isOf(Items.ENDER_PEARL)) {
               return false;
            } else if (!GrimCombatCoordinator.wY() && !GrimCombatCoordinator.xb()) {
               return false;
            } else {
               boolean var3 = false;
               this.mc.interactionManager.clickSlot(this.mc.player.playerScreenHandler.syncId, var2, this.Gh, SlotActionType.SWAP, this.mc.player);
               var3 = true;
               boolean var4 = true;
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private int Bd() {
      if (this.mc.player == null) {
         return -1;
      } else if (this.Gh >= 0 && this.Gh <= 8 && this.mc.player.getInventory().getStack(this.Gh).isOf(Items.ENDER_PEARL)) {
         return this.Gh;
      } else {
         for (int var1 = 0; var1 < 9; var1++) {
            if (this.mc.player.getInventory().getStack(var1).isOf(Items.ENDER_PEARL)) {
               return var1;
            }
         }

         return -1;
      }
   }

   private int Be() {
      if (this.mc.player == null) {
         return -1;
      } else {
         for (int var1 = 9; var1 < 36; var1++) {
            if (this.mc.player.getInventory().getStack(var1).isOf(Items.ENDER_PEARL)) {
               return var1;
            }
         }

         return -1;
      }
   }

   @Override
   public int combatPriority() {
      return 250;
   }

   @Override
   public void suspendForItemUse() {
   }

   @Override
   public boolean yieldToManualInput() {
      return true;
   }

   private static MethodHandle Bf() {
      for (Method var3 : ClientPlayerInteractionManager.class.getDeclaredMethods()) {
         Class[] var4 = var3.getParameterTypes();
         if (var3.getReturnType() == void.class && var4.length == 2) {
            if (var4[0] == ClientWorld.class) {
               if (var4[1] == SequencedPacketCreator.class) {
                  var3.setAccessible(true);
                  return MethodHandles.lookup().unreflect(var3);
               }
            } else {
               short var6 = 13925;
            }
         }
      }

      return null;
   }
}
