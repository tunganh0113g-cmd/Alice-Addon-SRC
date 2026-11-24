package com.nnpg.alice.internal;

import com.nnpg.alice.AliceAddon;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerTickMovementEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent.Post;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Receive;
import meteordevelopment.meteorclient.events.packets.PacketEvent.Sent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.DoubleSetting.Builder;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.meteorclient.utils.entity.DamageUtils.RaycastFactory;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Vector3d;

public final class CrystalAuraPlusPlus extends AliceModule implements GrimCombatCoordinator.Participant {
   private static final int Ck = 1;
   private static final int gh = 10;
   private static final double oz = 144.0;
   private static final int[] hG = new int[0];
   private static final long kb = 100L;
   private static final double IM = 0.0;
   private static final double hm = 0.03;
   private static final double[] oE = new double[]{0.0, -0.3, 0.3, 0.0, 0.0, -0.3, -0.3, 0.3, 0.3};
   private static final double[] cD = new double[]{0.0, 0.0, 0.0, -0.3, 0.3, -0.3, 0.3, -0.3, 0.3};
   private static final MethodHandle Bw = om();
   private final SettingGroup mr = this.settings.getDefaultGroup();
   private final SettingGroup qY = this.settings.createGroup("Burst");
   private final SettingGroup pK = this.settings.createGroup("Place");
   private final SettingGroup vC = this.settings.createGroup("Break");
   private final SettingGroup mJ = this.settings.createGroup("Rotation");
   private final SettingGroup qS = this.settings.createGroup("Pause");
   private final SettingGroup gm = this.settings.createGroup("Render");
   private final Setting<Double> CC = this.mr
      .add(
         ((Builder)((Builder)new Builder().name("target-range")).description("tam toi da toi muc tieu"))
            .defaultValue(10.0)
            .range(1.0, 16.0)
            .sliderRange(1.0, 16.0)
            .build()
      );
   private final Setting<Integer> If = this.mr
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("max-targets"))
                  .description("so luong muc tieu danh cung luc"))
               .defaultValue(2))
            .range(1, 8)
            .sliderRange(1, 8)
            .build()
      );
   private final Setting<Double> wE = this.mr
      .add(
         ((Builder)((Builder)new Builder().name("min-damage")).description("damage toi thieu de danh muc tieu"))
            .defaultValue(4.0)
            .range(0.0, 36.0)
            .sliderRange(0.0, 20.0)
            .build()
      );
   private final Setting<Double> vS = this.mr
      .add(
         ((Builder)((Builder)new Builder().name("face-place-health")).description("HP thap nhat cua ke dich de crystal lien tuc"))
            .defaultValue(9.0)
            .range(0.0, 36.0)
            .sliderRange(0.0, 20.0)
            .build()
      );
   private final Setting<Double> Ed = this.mr
      .add(
         ((Builder)((Builder)new Builder().name("max-self-damage")).description("sat thuong toi da ban than co the nhan khi danh muc tieu"))
            .defaultValue(5.0)
            .range(0.0, 36.0)
            .sliderRange(0.0, 20.0)
            .build()
      );
   private final Setting<Double> fU = this.mr
      .add(
         ((Builder)((Builder)new Builder().name("min-advantage")).description("loi the = sat thuong len muc tieu - sat thuong ban than"))
            .defaultValue(1.0)
            .range(-20.0, 36.0)
            .sliderRange(-5.0, 12.0)
            .build()
      );
   private final Setting<Boolean> FI = this.mr
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("anti-suicide"))
                  .description("bao ve ban than khoi viec danh chinh minh"))
               .defaultValue(false))
            .build()
      );
   private final Setting<Double> cU = this.mr
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("safety-reserve")).description("luong HP muon giu lai"))
               .defaultValue(1.0)
               .range(0.0, 20.0)
               .sliderRange(0.0, 10.0)
               .visible(this.FI::get))
            .build()
      );
   private final Setting<Boolean> qb = this.mr
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("predict-movement"))
                  .description("du doan chuyen dong cua dich"))
               .defaultValue(true))
            .build()
      );
   private final Setting<CrystalAuraPlusPlus.CombatMode> Bv = this.mr
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                     .name("combat-mode"))
                  .description(
                     "Sync: cho server xac nhan truoc khi dat/pha. Speed: break ngay khi packet spawn ve, bo look-gate. Danger: Speed + ghep break/place trong cung epoch va du doan destroy. Chi chon duoc 1 trong 3."
                  ))
               .defaultValue(CrystalAuraPlusPlus.CombatMode.Sync))
            .build()
      );
   private final Setting<Boolean> vt = this.qY
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("burst-enabled"))
                  .description("cho phep burst toc do cao trong cua so ngan de ket lieu autototem 1-tick"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Integer> pL = this.qY
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("burst-duration-ms"))
                     .description("thoi gian burst chay speed/danger voi delay 0ms"))
                  .defaultValue(3000))
               .range(500, 5000)
               .sliderRange(500, 5000)
               .visible(this.vt::get))
            .build()
      );
   private final Setting<Integer> uH = this.qY
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("burst-cooldown-ms"))
                     .description("thoi gian nghi ve sync sau burst truoc khi duoc burst lai"))
                  .defaultValue(5000))
               .range(2000, 15000)
               .sliderRange(2000, 15000)
               .visible(this.vt::get))
            .build()
      );
   private final Setting<CrystalAuraPlusPlus.BurstTrigger> EC = this.qY
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                        .name("burst-trigger"))
                     .description("dieu kien kich hoat burst"))
                  .defaultValue(CrystalAuraPlusPlus.BurstTrigger.LethalOrPop))
               .visible(this.vt::get))
            .build()
      );
   private final Setting<CrystalAuraPlusPlus.PickItemMode> gk = this.mr
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                     .name("pick-item"))
                  .description("chon item theo mode"))
               .defaultValue(CrystalAuraPlusPlus.PickItemMode.Silent))
            .build()
      );
   private final Setting<Boolean> Ar = this.pK
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("place"))
                  .description("dat crystal"))
               .defaultValue(true))
            .build()
      );
   private final Setting<CrystalAuraPlusPlus.RaycastMode> ep = this.mr
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                     .name("raycast-mode"))
                  .description("chon mode raycast"))
               .defaultValue(CrystalAuraPlusPlus.RaycastMode.Grim))
            .build()
      );
   private final Setting<Double> mm = this.pK
      .add(
         ((Builder)((Builder)new Builder().name("place-range")).description("toi da tam dat crystal"))
            .defaultValue(4.5)
            .range(1.0, 6.0)
            .sliderRange(1.0, 6.0)
            .build()
      );
   private final Setting<Integer> HN = this.pK
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("min-place-delay-ms"))
                  .description("so mili-giay toi thieu giua cac goi place va support-place"))
               .defaultValue(0))
            .range(0, 1000)
            .sliderRange(0, 500)
            .build()
      );
   private final Setting<Integer> Et = this.pK
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("max-place-delay-ms"))
                  .description("so mili-giay toi da giua cac goi place va support-place"))
               .defaultValue(0))
            .range(0, 1000)
            .sliderRange(0, 500)
            .build()
      );
   private final Setting<Boolean> EV = this.pK
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("multi-point-place"))
                  .description("phan tan place qua nhieu base gan tuong duong thay vi lap lien tuc mot diem"))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> Ju = this.pK
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("support"))
                  .description("dat obsidian"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> ab = this.vC
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("break"))
                  .description("tan cong crystal"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Double> Ja = this.vC
      .add(
         ((Builder)((Builder)new Builder().name("break-range")).description("tam tan cong crystal"))
            .defaultValue(4.5)
            .range(1.0, 6.0)
            .sliderRange(1.0, 6.0)
            .build()
      );
   private final Setting<Integer> Ey = this.vC
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("min-break-delay-ms"))
                  .description("so mili-giay toi thieu giua cac goi dap crystal"))
               .defaultValue(0))
            .range(0, 1000)
            .sliderRange(0, 500)
            .build()
      );
   private final Setting<Integer> lM = this.vC
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("max-break-delay-ms"))
                  .description("so mili-giay toi da giua cac goi dap crystal"))
               .defaultValue(75))
            .range(0, 1000)
            .sliderRange(0, 500)
            .build()
      );
   private final Setting<Integer> BM = this.vC
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("retry-delay"))
                  .description("khoang nghi giua cac lan thu lai tan cong crystal"))
               .defaultValue(1))
            .range(1, 10)
            .sliderRange(1, 10)
            .build()
      );
   private final Setting<Integer> rm = this.vC
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                     .name("max-attempts"))
                  .description("so lan thu dap lai cung crystal khi chua vo"))
               .defaultValue(3))
            .range(1, 5)
            .sliderRange(1, 5)
            .build()
      );
   private final Setting<Boolean> El = this.vC
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("predicted-id-break"))
                     .description("Danger: dap crystal ngay trong tick vua place bang entity ID du doan. Doan sai thi server bo qua packet. Khong Grim-safe."))
                  .defaultValue(true))
               .visible(() -> this.pf()))
            .build()
      );
   private final Setting<Integer> iO = this.vC
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("predicted-id-attacks"))
                     .description("ban bao nhieu entity ID lien tiep sau ID lon nhat da thay. 1 = doan don le (gan nhu luon truot), 3-5 = phu mot dai ID."))
                  .defaultValue(3))
               .range(1, 10)
               .sliderRange(1, 10)
               .visible(() -> this.pf() && (Boolean)this.El.get()))
            .build()
      );
   private final Setting<Boolean> zq = this.vC
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("explosion-inhibit"))
                  .description(
                     "coi moi crystal gan mot vu no vua nhan la da chet trong ping*2 ms, de khong dap vao crystal ma server da xoa va khong bi chan cho khi place lai."
                  ))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> bF = this.vC
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("anti-weakness"))
                  .description("dung vu khi trong hotbar co the pha crystal khi dang bi weakness"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Double> qH = this.mJ
      .add(
         ((Builder)((Builder)new Builder().name("yaw-step")).description("goc yaw thay doi toi da tren server moi chu ky"))
            .defaultValue(180.0)
            .range(1.0, 180.0)
            .sliderRange(1.0, 180.0)
            .build()
      );
   private final Setting<Double> iX = this.mJ
      .add(
         ((Builder)((Builder)new Builder().name("pitch-step"))
               .description(
                  "goc pitch thay doi toi da tren server moi chu ky. 180 = quay tuc thi nhu yaw-step; Danger khong bi anh huong vi no ban ngay voi goc da queue."
               ))
            .defaultValue(90.0)
            .range(1.0, 180.0)
            .sliderRange(1.0, 180.0)
            .build()
      );
   private final Setting<Boolean> iH = this.qS
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("pause-on-use"))
                  .description("tam dung khi ban dang dung item de tranh xung dot"))
               .defaultValue(false))
            .build()
      );
   private final Setting<Boolean> kN = this.qS
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("pause-on-mine"))
                  .description("tam dung khi giu nut danh de tranh xung dot"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Double> AG = this.qS
      .add(
         ((Builder)((Builder)new Builder().name("pause-health")).description("tam dung khi mau thap hon muc nay"))
            .defaultValue(0.0)
            .range(0.0, 36.0)
            .sliderRange(0.0, 20.0)
            .build()
      );
   private final Setting<Boolean> GO = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                     .name("render"))
                  .description("cong tac chinh cho moi overlay va nhan sat thuong cua Crystal Aura++"))
               .defaultValue(true))
            .build()
      );
   private final Setting<Boolean> wx = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("crystal-chams"))
                     .description("hien thi model crystal that xuyen tuong ma khong gay anh huong den server"))
                  .defaultValue(true))
               .visible(this.GO::get))
            .build()
      );
   private final Setting<Double> ma = this.gm
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("chams-range")).description("tam toi da de render crystal xuyen tuong"))
               .defaultValue(16.0)
               .range(4.0, 64.0)
               .sliderRange(4.0, 32.0)
               .visible(() -> {
                  if ((Boolean)this.GO.get()) {
                     if ((Boolean)this.wx.get()) {
                        return true;
                     }

                     short var5;
                     while (var5 == 26042) {
                        var5 = 26040;
                     }
                  }

                  return false;
               }))
            .build()
      );
   private final Setting<Double> Ji = this.gm
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("chams-scale")).description("ty le cua model crystal xuyen tuong"))
               .defaultValue(1.0)
               .range(0.25, 2.0)
               .sliderRange(0.5, 1.5)
               .visible(() -> (Boolean)this.GO.get() && (Boolean)this.wx.get()))
            .build()
      );
   private final Setting<ShapeMode> pO = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                        .name("chams-shape-mode"))
                     .description("ve cac mat, canh hoac ca hai cua model crystal xuyen tuong"))
                  .defaultValue(ShapeMode.Both))
               .visible(() -> (Boolean)this.GO.get() && (Boolean)this.wx.get()))
            .build()
      );
   private final Setting<SettingColor> HI = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                     .name("chams-side-color"))
                  .description("mau nen cua model crystal xuyen tuong"))
               .defaultValue(new SettingColor(190, 90, 255, 45))
               .visible(() -> (Boolean)this.GO.get() && (Boolean)this.wx.get() && ((ShapeMode)this.pO.get()).sides()))
            .build()
      );
   private final Setting<SettingColor> rr = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                     .name("chams-line-color"))
                  .description("mau vien cua model crystal xuyen tuong"))
               .defaultValue(new SettingColor(220, 150, 255, 220))
               .visible(() -> (Boolean)this.GO.get() && (Boolean)this.wx.get() && ((ShapeMode)this.pO.get()).lines()))
            .build()
      );
   private final Setting<Boolean> Hq = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("render-place"))
                     .description("hien thi khoi be do da duoc xac nhan"))
                  .defaultValue(true))
               .visible(this.GO::get))
            .build()
      );
   private final Setting<Boolean> lP = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("render-break"))
                     .description("hien thi hitbox cua crystal muc tieu da duoc xac nhan"))
                  .defaultValue(true))
               .visible(this.GO::get))
            .build()
      );
   private final Setting<Boolean> eN = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("render-pending"))
                     .description("hien thi cac hanh dong dang cho server cap nhat"))
                  .defaultValue(true))
               .visible(this.GO::get))
            .build()
      );
   private final Setting<Boolean> tm = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("render-damage"))
                     .description("hien thi sat thuong muc tieu va ban than"))
                  .defaultValue(true))
               .visible(this.GO::get))
            .build()
      );
   private final Setting<Boolean> jb = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)((meteordevelopment.meteorclient.settings.BoolSetting.Builder)new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
                        .name("render-fade"))
                     .description("giu overlay dat/pha da xong roi mo dan cho muot"))
                  .defaultValue(true))
               .visible(this.GO::get))
            .build()
      );
   private final Setting<Integer> xW = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)((meteordevelopment.meteorclient.settings.IntSetting.Builder)new meteordevelopment.meteorclient.settings.IntSetting.Builder()
                        .name("fade-time"))
                     .description("thoi gian (tick) de mo dan hanh dong da hoan thanh"))
                  .defaultValue(8))
               .range(1, 40)
               .sliderRange(1, 20)
               .visible(() -> (Boolean)this.GO.get() && (Boolean)this.jb.get()))
            .build()
      );
   private final Setting<ShapeMode> eG = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)((meteordevelopment.meteorclient.settings.EnumSetting.Builder)new meteordevelopment.meteorclient.settings.EnumSetting.Builder()
                        .name("shape-mode"))
                     .description("ve cac mat, vien hoac ca hai cua overlay"))
                  .defaultValue(ShapeMode.Both))
               .visible(this.GO::get))
            .build()
      );
   private final Setting<SettingColor> lZ = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                     .name("place-side-color"))
                  .description("mau nen cua overlay hien thi vi tri dat"))
               .defaultValue(new SettingColor(70, 255, 130, 45))
               .visible(() -> {
                  if ((Boolean)this.GO.get()) {
                     if (((ShapeMode)this.eG.get()).sides()) {
                        return true;
                     }

                     char var1 = '\ue357';
                  }

                  return false;
               }))
            .build()
      );
   private final Setting<SettingColor> ed = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                     .name("place-line-color"))
                  .description("mau vien cho lop phu dat"))
               .defaultValue(new SettingColor(70, 255, 130, 220))
               .visible(() -> (Boolean)this.GO.get() && ((ShapeMode)this.eG.get()).lines()))
            .build()
      );
   private final Setting<SettingColor> tQ = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                     .name("break-side-color"))
                  .description("mau nen cua overlay hien thi vi tri dap"))
               .defaultValue(new SettingColor(255, 70, 90, 45))
               .visible(() -> {
                  if ((Boolean)this.GO.get()) {
                     if (((ShapeMode)this.eG.get()).sides()) {
                        return true;
                     }

                     short var1 = 27103;
                  }

                  return false;
               }))
            .build()
      );
   private final Setting<SettingColor> jg = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                     .name("break-line-color"))
                  .description("mau vien cho lop phu dap"))
               .defaultValue(new SettingColor(255, 70, 90, 220))
               .visible(() -> (Boolean)this.GO.get() && ((ShapeMode)this.eG.get()).lines()))
            .build()
      );
   private final Setting<SettingColor> tF = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                     .name("pending-side-color"))
                  .description("mau nen cho cac hanh dong dang cho server xac nhan"))
               .defaultValue(new SettingColor(255, 185, 45, 35))
               .visible(() -> {
                  if ((Boolean)this.GO.get()) {
                     int var10000 = this.eN.get() ? 30845 : 30846;

                     while (true) {
                        int var1 = var10000;
                        if (var1 == 30845) {
                           if (((ShapeMode)this.eG.get()).sides()) {
                              return true;
                           }
                           break;
                        }

                        if (var1 != 30846) {
                           break;
                        }

                        var10000 = 30844;
                     }
                  }

                  return false;
               }))
            .build()
      );
   private final Setting<SettingColor> Em = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                     .name("pending-line-color"))
                  .description("mau vien cho cac thao tac dang cho server"))
               .defaultValue(new SettingColor(255, 185, 45, 220))
               .visible(() -> (Boolean)this.GO.get() && (Boolean)this.eN.get() && ((ShapeMode)this.eG.get()).lines()))
            .build()
      );
   private final Setting<SettingColor> Ew = this.gm
      .add(
         ((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)((meteordevelopment.meteorclient.settings.ColorSetting.Builder)new meteordevelopment.meteorclient.settings.ColorSetting.Builder()
                     .name("damage-color"))
                  .description("mau cua nhan hien thi sat thuong len muc tieu/ban than"))
               .defaultValue(new SettingColor(255, 255, 255, 255))
               .visible(() -> (Boolean)this.GO.get() && (Boolean)this.tm.get()))
            .build()
      );
   private final Setting<Double> qn = this.gm
      .add(
         ((Builder)((Builder)((Builder)new Builder().name("damage-scale")).description("ty le scale cho nhan hien thi sat thuong the gioi"))
               .defaultValue(1.0)
               .range(0.5, 3.0)
               .sliderRange(0.5, 2.0)
               .visible(() -> (Boolean)this.GO.get() && (Boolean)this.tm.get()))
            .build()
      );
   private final TargetTracker zE = new TargetTracker();
   private final InventoryRefillHelper wf = new InventoryRefillHelper(this);
   private static CrystalAuraPlusPlus An;
   private Item eJ;
   private int tP = -1;
   private int Dj = -1;
   private final Map<Integer, CrystalAuraPlusPlus.AttackRecord> xo = new HashMap<>();
   private final Map<Integer, Long> nv = new HashMap<>();
   private final Map<Integer, Long> E = new ConcurrentHashMap<>();
   private final Queue<Vec3d> nf = new ConcurrentLinkedQueue<>();
   private CrystalAuraPlusPlus.BurstState eq = CrystalAuraPlusPlus.BurstState.SUSTAIN;
   private long fO;
   private long FO;
   private long fy;
   private boolean cg;
   private final List<EndCrystalEntity> nx = new ArrayList<>();
   private boolean BY;
   private int cj = -1;
   private CrystalAuraPlusPlus.PlacePlan rG;
   private BlockPos bq;
   private int Eo;
   private long uj;
   private long sL;
   private BlockPos tK;
   private BlockPos dP;
   private long pq;
   private CrystalAuraPlusPlus.PendingSupport wd;
   private CrystalAuraPlusPlus.PendingCrystal CL;
   private CrystalAuraPlusPlus.RenderSnapshot Fn;
   private final List<CrystalAuraPlusPlus.RenderPulse> dC = new ArrayList<>();
   private final Vector3d fJ = new Vector3d();
   private float qV;
   private float Hj;
   private boolean mQ;
   private CrystalAuraPlusPlus.RotationIntent qs;
   private CrystalAuraPlusPlus.AuraRotationInput Dh;
   private final Map<BlockPos, CrystalAuraPlusPlus.SupportState> Ah = new HashMap<>();
   private final Map<Integer, BlockPos> ye = new HashMap<>();
   private BlockPos wW = null;
   private long Ik = 0L;

   public CrystalAuraPlusPlus() {
      super(AliceAddon.PVP_CATEGORY, "crystal-aura++", "Tu dong dat va pha pha le voi tinh toan sat thuong chinh xac cung goc xoay on dinh.");
   }

   @Override
   public void onActivate() {
      this.nm();
      An = this;
      GrimCombatCoordinator.wG(this);
      super.onActivate();
   }

   @Override
   public void onDeactivate() {
      this.oQ();
      GrimCombatCoordinator.wH(this);
      if (An == this) {
         An = null;
      }

      this.nm();
      super.onDeactivate();
   }

   private void nm() {
      this.xo.clear();
      this.E.clear();
      this.nf.clear();
      this.nv.clear();
      this.eq = CrystalAuraPlusPlus.BurstState.SUSTAIN;
      this.fO = 0L;
      this.FO = 0L;
      this.fy = 0L;
      this.cg = false;
      this.nx.clear();
      this.BY = false;
      this.cj = -1;
      this.rG = null;
      this.bq = null;
      this.wd = null;
      this.CL = null;
      this.zE.zi();
      this.wf.yQ();
      this.eJ = null;
      this.tP = -1;
      this.Dj = -1;
      this.Ah.clear();
      this.ye.clear();
      this.wW = null;
      this.Ik = 0L;
      this.oQ();
      this.Eo = 0;
      this.uj = 0L;
      this.sL = 0L;
      this.tK = null;
      this.dP = null;
      this.pq = 0L;
      this.Fn = null;
      this.dC.clear();
      this.qV = 0.0F;
      this.Hj = 0.0F;
      this.mQ = false;
      this.qs = null;
   }

   @AliceEventHandler(priority = 100)
   private void nn(PlayerTickMovementEvent var1) {
      this.BY = false;
      this.wf.yO();
      this.oP();
      this.ph();
   }

   private List<EndCrystalEntity> no() {
      if (!this.BY) {
         this.nx.clear();
         if (this.mc.world != null) {
            for (Entity var2 : this.mc.world.getEntities()) {
               if (var2 instanceof EndCrystalEntity var3 && var3.isAlive()) {
                  this.nx.add(var3);
               }
            }
         }

         this.BY = true;
      }

      return this.nx;
   }

   @AliceEventHandler(priority = -200)
   private void np(Post var1) {
      this.oQ();
      this.zE.zf();
      this.wf.yP();
   }

   @AliceEventHandler
   private void nq(Receive var1) {
      if (var1.packet instanceof ExplosionS2CPacket var5) {
         if ((Boolean)this.zq.get() && this.nf.size() < 64) {
            this.nf.add(var5.center());
         }
      } else if (var1.packet instanceof EntityStatusS2CPacket var2) {
         if (var2.getStatus() == 35) {
            if (var2.getEntity(this.mc.world) instanceof PlayerEntity var4) {
               if (var4 != this.mc.player) {
                  if (Friends.get().shouldAttack(var4)) {
                     this.nv.put(var4.getId(), System.currentTimeMillis());
                  }
               }
            }
         }
      }
   }

   private void nr() {
      if (!this.nf.isEmpty()) {
         if (this.mc.world != null && (Boolean)this.zq.get()) {
            long var1 = System.currentTimeMillis();

            Vec3d var3;
            while ((var3 = this.nf.poll()) != null) {
               for (EndCrystalEntity var5 : this.no()) {
                  if (!(var5.squaredDistanceTo(var3) > 144.0)) {
                     this.E.putIfAbsent(var5.getId(), var1);
                  }
               }
            }
         } else {
            this.nf.clear();
         }
      }
   }

   private long ns() {
      return Math.max(50L, this.nt() * 2L);
   }

   private int nt() {
      int var10000 = this.mc.getNetworkHandler() != null ? 32084 : 32085;

      while (true) {
         int var2 = var10000;
         if (var2 == 32084) {
            if (this.mc.player != null) {
               short var6;
               while (var6 == 20172) {
                  var6 = 20170;
               }

               PlayerListEntry var1 = this.mc.getNetworkHandler().getPlayerListEntry(this.mc.player.getUuid());
               return var1 == null ? 100 : Math.max(0, var1.getLatency());
            }
            break;
         }

         if (var2 != 32085) {
            break;
         }

         var10000 = 32083;
      }

      return 100;
   }

   private boolean nu(int var1) {
      Long var2 = this.E.get(var1);
      if (var2 == null) {
         return false;
      } else {
         int var10000 = System.currentTimeMillis() - var2 > this.ns() ? 15391 : 15390;

         while (true) {
            int var3 = var10000;
            if (var3 == 15390) {
               var4 = true;
               break;
            }

            if (var3 != 15391) {
               var4 = false;
               break;
            }

            var10000 = 15389;
         }

         return var4;
      }
   }

   @AliceEventHandler(priority = 200)
   private void nv(EntityAddedEvent var1) {
      if (var1.entity.getId() > this.cj) {
         this.cj = var1.entity.getId();
      }

      if (var1.entity instanceof EndCrystalEntity var2) {
         BlockPos var9 = var2.getBlockPos().down();
         this.ye.put(var2.getId(), var9);
         if (this.pe() || this.pg()) {
            CrystalAuraPlusPlus.PendingCrystal var4 = this.CL;
            if (var4 != null && py(var2, var4.base)) {
               this.CL = null;

               for (int var8 : var4.predictedIds()) {
                  if (var8 != var2.getId()) {
                     this.xo.remove(var8);
                  }
               }

               if (oL(var4.predictedIds(), var2.getId())) {
                  return;
               }

               if ((Boolean)this.ab.get() && this.pr(CrystalAuraPlusPlus.CrystalAction.Break, var9) && this.os(var2)) {
                  List var10 = this.nM();
                  if (!var10.isEmpty()) {
                     CrystalAuraPlusPlus.BreakPlan var11 = this.nO(var2, var10);
                     if (var11 != null) {
                        GrimCombatCoordinator.xo(this, () -> this.oM(var11));
                     }
                  }
               }
            }
         }
      }
   }

   @AliceEventHandler
   private void nw(EntityRemovedEvent var1) {
      if (var1.entity instanceof EndCrystalEntity) {
         this.xo.remove(var1.entity.getId());
         this.E.remove(var1.entity.getId());
         this.ye.remove(var1.entity.getId());
      }
   }

   @AliceEventHandler(priority = -1000)
   private void nx(Sent var1) {
      if (var1.packet instanceof PlayerMoveC2SPacket var2 && var2.changesLook()) {
         this.qV = var2.getYaw(this.qV);
         this.Hj = var2.getPitch(this.Hj);
         this.mQ = true;
         if (this.Dh != null && this.Dh.sw) {
            GrimCombatCoordinator.wN(this, this.qV, this.Hj);
         }
      }
   }

   @Override
   public GrimCombatCoordinator.Intent prepareCombatIntent() {
      this.oQ();
      this.qs = null;
      this.Fn = null;
      this.Eo++;
      this.oW();
      this.oX();
      int var10000 = this.gk.get() == CrystalAuraPlusPlus.PickItemMode.Silent ? 13336 : 13335;

      label168:
      while (true) {
         switch (var10000) {
            case 13335:
               if (GrimCombatCoordinator.wM(this)) {
                  return this.nB();
               }
               break label168;
            case 13336:
               var10000 = 13334;
               break;
            default:
               break label168;
         }
      }

      if (!this.nL()) {
         this.zE.zi();
         this.oe();
         return GrimCombatCoordinator.wM(this) ? this.nB() : null;
      } else {
         List var1 = this.nM();
         if (var1.isEmpty()) {
            this.zE.zi();
            this.oe();
            return GrimCombatCoordinator.wM(this) ? this.nB() : null;
         } else {
            if ((Boolean)this.ab.get()) {
               CrystalAuraPlusPlus.BreakPlan var2 = this.nN(var1);
               if (var2 != null && this.pr(CrystalAuraPlusPlus.CrystalAction.Break, var2.base) && this.oo(var2.crystal) != Integer.MIN_VALUE) {
                  if ((this.pe() || this.pg()) && (Boolean)this.Ar.get()) {
                     this.rG = this.nX(var1, false);
                     if (this.rG == null && (Boolean)this.Ju.get()) {
                        this.rG = this.nX(var1, true);
                     }
                  }

                  if (this.pf() && (Boolean)this.Ar.get() && this.rG != null) {
                     CrystalAuraPlusPlus.PlacePlan var6 = this.rG;
                     this.rG = null;
                     if (this.nR(var6) && this.pr(CrystalAuraPlusPlus.CrystalAction.Place, var6.base)) {
                        return this.ny(var2, var6);
                     }
                  }

                  return this.nz(var2);
               }
            }

            if (!(Boolean)this.Ar.get()) {
               this.zE.zi();
               this.oe();
               return GrimCombatCoordinator.wM(this) ? this.nB() : null;
            } else {
               CrystalAuraPlusPlus.PendingSupport var4 = this.wd;
               if (!this.pd() && var4 != null) {
                  if (this.Eo > var4.kK) {
                     CrystalAuraPlusPlus.PlacePlan var3 = this.nZ(var4.Fm, var1);
                     if (var3 != null && this.nS(Items.END_CRYSTAL)) {
                        if (this.pr(CrystalAuraPlusPlus.CrystalAction.Place, var3.base)) {
                           return this.nA(var3, true);
                        }

                        this.zE.zi();
                        return !GrimCombatCoordinator.wM(this) ? null : this.nB();
                     }
                  }

                  this.wd = null;
               }

               CrystalAuraPlusPlus.PlacePlan var5 = this.nX(var1, false);
               if ((this.pe() || this.pg()) && this.rG != null) {
                  var5 = this.rG;
                  this.rG = null;
               } else if (var5 == null) {
                  var10000 = !this.Ju.get() ? '맶' : '맵';

                  label147:
                  while (true) {
                     switch (var10000) {
                        case 47605:
                           var5 = this.nX(var1, true);
                           break label147;
                        case 47606:
                           var10000 = 47604;
                           break;
                        default:
                           break label147;
                     }
                  }
               }

               if (var5 == null || !this.nR(var5)) {
                  this.zE.zi();
                  return GrimCombatCoordinator.wM(this) ? this.nB() : null;
               } else if (!this.pr(CrystalAuraPlusPlus.CrystalAction.Place, var5.base)) {
                  this.zE.zi();
                  return GrimCombatCoordinator.wM(this) ? this.nB() : null;
               } else {
                  if (this.pe() || this.pg()) {
                     this.bq = var5.base;
                  }

                  return this.nA(var5, false);
               }
            }
         }
      }
   }

   @Override
   public int combatPriority() {
      return 100;
   }

   @Override
   public boolean canFollowEmergencyOffhandSwap() {
      return true;
   }

   private GrimCombatCoordinator.Intent ny(final CrystalAuraPlusPlus.BreakPlan var1, final CrystalAuraPlusPlus.PlacePlan var2) {
      return new GrimCombatCoordinator.Intent() {
         @Override
         public GrimCombatCoordinator.Participant owner() {
            return CrystalAuraPlusPlus.this;
         }

         @Override
         public void execute() {
            CrystalAuraPlusPlus.this.nP(var1);
            if (CrystalAuraPlusPlus.this.nR(var2)) {
               CrystalAuraPlusPlus.this.bq = var2.base;
               CrystalAuraPlusPlus.this.nQ(var2, false);
            }
         }
      };
   }

   private GrimCombatCoordinator.Intent nz(final CrystalAuraPlusPlus.BreakPlan var1) {
      return new GrimCombatCoordinator.Intent() {
         @Override
         public GrimCombatCoordinator.Participant owner() {
            return CrystalAuraPlusPlus.this;
         }

         @Override
         public void execute() {
            CrystalAuraPlusPlus.this.nP(var1);
         }
      };
   }

   private GrimCombatCoordinator.Intent nA(final CrystalAuraPlusPlus.PlacePlan var1, final boolean var2) {
      return new GrimCombatCoordinator.Intent() {
         @Override
         public GrimCombatCoordinator.Participant owner() {
            return CrystalAuraPlusPlus.this;
         }

         @Override
         public void execute() {
            CrystalAuraPlusPlus.this.nQ(var1, var2);
         }
      };
   }

   private GrimCombatCoordinator.Intent nB() {
      return new GrimCombatCoordinator.Intent() {
         @Override
         public GrimCombatCoordinator.Participant owner() {
            return CrystalAuraPlusPlus.this;
         }

         @Override
         public void execute() {
            CrystalAuraPlusPlus.this.ok();
         }
      };
   }

   @AliceEventHandler
   private void nC(Render3DEvent var1) {
      if ((Boolean)this.GO.get() && this.mc.player != null && this.mc.world != null) {
         if ((Boolean)this.wx.get()) {
            this.nD(var1);
         }

         if ((Boolean)this.jb.get()) {
            for (CrystalAuraPlusPlus.RenderPulse var3 : this.dC) {
               if (!this.nI(var3.snapshot) && this.nH(var3.snapshot.kind)) {
                  double var4 = this.nJ(var3, var1.tickDelta);
                  if (var4 > 0.0) {
                     this.nF(var1, var3.snapshot, false, var4);
                  }
               }
            }
         }

         CrystalAuraPlusPlus.RenderSnapshot var7 = this.Fn;
         if (var7 != null && !this.nI(var7) && this.nH(var7.kind)) {
            this.nF(var1, var7, false, 1.0);
         }

         if ((Boolean)this.eN.get()) {
            CrystalAuraPlusPlus.PendingSupport var8 = this.wd;
            if (var8 != null) {
               this.nF(var1, var8.wG, true, 1.0);
            }

            CrystalAuraPlusPlus.PendingCrystal var9 = this.CL;
            if (var9 != null) {
               this.nF(var1, var9.render, true, 1.0);
            }

            for (CrystalAuraPlusPlus.AttackRecord var6 : this.xo.values()) {
               if (var6.vD != null) {
                  this.nF(var1, var6.vD, true, 1.0);
               }
            }
         }
      }
   }

   private void nD(Render3DEvent var1) {
      double var2 = (Double)this.ma.get() * (Double)this.ma.get();

      for (Entity var5 : this.mc.world.getEntities()) {
         if (var5 instanceof EndCrystalEntity var6 && var6.isAlive() && !(this.mc.player.squaredDistanceTo(var6) > var2)) {
            WireframeEntityRenderer.render(var1, var6, (Double)this.Ji.get(), (Color)this.HI.get(), (Color)this.rr.get(), (ShapeMode)this.pO.get());
         }
      }
   }

   @AliceEventHandler
   private void nE(Render2DEvent var1) {
      if ((Boolean)this.GO.get() && (Boolean)this.tm.get() && this.mc.player != null && this.mc.world != null) {
         if ((Boolean)this.jb.get()) {
            for (CrystalAuraPlusPlus.RenderPulse var3 : this.dC) {
               if (!this.nI(var3.snapshot) && this.nH(var3.snapshot.kind)) {
                  double var4 = this.nJ(var3, var1.tickDelta);
                  if (var4 > 0.0) {
                     this.nG(var3.snapshot, var4);
                  }
               }
            }
         }

         CrystalAuraPlusPlus.RenderSnapshot var7 = this.Fn;
         if (var7 != null && !this.nI(var7) && this.nH(var7.kind)) {
            this.nG(var7, 1.0);
         }

         if ((Boolean)this.eN.get()) {
            CrystalAuraPlusPlus.PendingSupport var8 = this.wd;
            if (var8 != null) {
               this.nG(var8.wG, 1.0);
            }

            CrystalAuraPlusPlus.PendingCrystal var9 = this.CL;
            if (var9 != null) {
               this.nG(var9.render, 1.0);
            }

            for (CrystalAuraPlusPlus.AttackRecord var6 : this.xo.values()) {
               if (var6.vD != null) {
                  this.nG(var6.vD, 1.0);
               }
            }
         }
      }
   }

   private void nF(Render3DEvent var1, CrystalAuraPlusPlus.RenderSnapshot var2, boolean var3, double var4) {
      SettingColor var10000;
      if (var3) {
         var10000 = (SettingColor)this.tF.get();
      } else {
         int var8 = var2.kind != CrystalAuraPlusPlus.RenderKind.Place ? 157 : 156;

         label37:
         while (true) {
            switch (var8) {
               case 156:
                  var10000 = (SettingColor)this.lZ.get();
                  break label37;
               case 157:
                  var8 = 155;
                  break;
               default:
                  var10000 = (SettingColor)this.tQ.get();
                  break label37;
            }
         }
      }

      SettingColor var6 = var10000;
      if (var3) {
         var10000 = (SettingColor)this.Em.get();
      } else {
         int var10 = var2.kind != CrystalAuraPlusPlus.RenderKind.Place ? '\ud91e' : '\ud91d';

         label30:
         while (true) {
            switch (var10) {
               case 55581:
                  var10000 = (SettingColor)this.ed.get();
                  break label30;
               case 55582:
                  var10 = 55580;
                  break;
               default:
                  var10000 = (SettingColor)this.jg.get();
                  break label30;
            }
         }
      }

      SettingColor var7 = var10000;
      var1.renderer.box(var2.box, nK(var6, var4), nK(var7, var4), (ShapeMode)this.eG.get(), 0);
   }

   private void nG(CrystalAuraPlusPlus.RenderSnapshot var1, double var2) {
      this.fJ.set(var1.label.x, var1.label.y, var1.label.z);
      if (NametagUtils.to2D(this.fJ, (Double)this.qn.get())) {
         String var4 = var1.damageLabel;
         TextRenderer var5 = TextRenderer.get();
         if (var5 != null) {
            NametagUtils.begin(this.fJ);
            var5.begin(1.0, false, true);
            double var6 = var5.getWidth(var4, true);
            double var8 = var5.getHeight(true);
            var5.render(var4, -var6 / 2.0, -var8 / 2.0, nK((Color)this.Ew.get(), var2), true);
            var5.end();
            NametagUtils.end();
         }
      }
   }

   private boolean nH(CrystalAuraPlusPlus.RenderKind var1) {
      return var1 == CrystalAuraPlusPlus.RenderKind.Place ? (Boolean)this.Hq.get() : (Boolean)this.lP.get();
   }

   private boolean nI(CrystalAuraPlusPlus.RenderSnapshot var1) {
      if (var1 == null) {
         return false;
      } else {
         CrystalAuraPlusPlus.PendingSupport var2 = this.wd;
         if (var2 != null && var2.wG == var1) {
            return true;
         } else {
            CrystalAuraPlusPlus.PendingCrystal var3 = this.CL;
            if (var3 != null && var3.render == var1) {
               return true;
            } else {
               for (CrystalAuraPlusPlus.AttackRecord var5 : this.xo.values()) {
                  if (var5.vD == var1) {
                     return true;
                  }
               }

               return false;
            }
         }
      }
   }

   private double nJ(CrystalAuraPlusPlus.RenderPulse var1, float var2) {
      double var3 = Math.max(0.0, (double)(this.Eo - var1.startedTick + var2));
      return MathHelper.clamp(1.0 - var3 / ((Integer)this.xW.get()).intValue(), 0.0, 1.0);
   }

   private static Color nK(Color var0, double var1) {
      return var1 >= 0.999 ? var0 : new Color(var0.r, var0.g, var0.b, MathHelper.clamp((int)Math.round(var0.a * var1), 0, 255));
   }

   private boolean nL() {
      if (this.mc.player != null) {
         int var10000 = this.mc.world != null ? 1081 : 1082;

         while (true) {
            int var1 = var10000;
            if (var1 == 1081) {
               if (this.mc.interactionManager != null && this.mc.getNetworkHandler() != null && this.mc.player.isAlive()) {
                  var10000 = this.mc.player.isCreative() ? '놭' : '놬';

                  while (true) {
                     switch (var10000) {
                        case 45484:
                           var10000 = this.mc.player.isSpectator() ? '깫' : '깪';

                           while (true) {
                              switch (var10000) {
                                 case 44650:
                                    if (!this.mc.player.hasVehicle() && !this.mc.player.isGliding() && !this.mc.player.getAbilities().flying) {
                                       if (pH(this.mc.player) < (Double)this.AG.get()) {
                                          return false;
                                       }

                                       if (!(Boolean)this.iH.get()
                                          || !this.mc.player.isUsingItem() && (this.mc.currentScreen != null || !this.mc.options.useKey.isPressed())) {
                                          return !(Boolean)this.kN.get() || this.mc.currentScreen != null || !this.mc.options.attackKey.isPressed();
                                       }

                                       return false;
                                    }

                                    return false;
                                 case 44651:
                                    var10000 = 44649;
                                    break;
                                 default:
                                    return false;
                              }
                           }
                        case 45485:
                           var10000 = 45483;
                           break;
                        default:
                           return false;
                     }
                  }
               }
               break;
            }

            if (var1 != 1082) {
               break;
            }

            var10000 = 1080;
         }
      }

      return false;
   }

   private List<PlayerEntity> nM() {
      double var1 = (Double)this.CC.get() * (Double)this.CC.get();
      ArrayList var3 = new ArrayList();

      for (PlayerEntity var5 : this.mc.world.getPlayers()) {
         if (var5 != this.mc.player
            && var5.isAlive()
            && !var5.isCreative()
            && !var5.isSpectator()
            && Friends.get().shouldAttack(var5)
            && !(this.mc.player.squaredDistanceTo(var5) > var1)) {
            var3.add(var5);
         }
      }

      var3.sort(Comparator.<PlayerEntity>comparingDouble(var1x -> this.mc.player.squaredDistanceTo(var1x)).thenComparingInt(Entity::getId));
      return var3.size() <= this.If.get() ? var3 : new ArrayList<>(var3.subList(0, (Integer)this.If.get()));
   }

   private CrystalAuraPlusPlus.BreakPlan nN(List<PlayerEntity> var1) {
      CrystalAuraPlusPlus.BreakPlan var2 = null;

      for (EndCrystalEntity var4 : this.no()) {
         CrystalAuraPlusPlus.BreakPlan var5 = this.nO(var4, var1);
         if (var5 != null) {
            if (var2 == null) {
               var2 = var5;
            } else {
               double var6 = var5.damage.score() - var2.damage.score();
               if (Math.abs(var6) > 0.5) {
                  if (var6 > 0.0) {
                     var2 = var5;
                  }
               } else if (var5.adaptiveScore > var2.adaptiveScore || pp(var5.adaptiveScore, var2.adaptiveScore) && var5.crystal.getId() < var2.crystal.getId()) {
                  var2 = var5;
               }
            }
         }
      }

      return var2;
   }

   private CrystalAuraPlusPlus.BreakPlan nO(EndCrystalEntity var1, List<PlayerEntity> var2) {
      if (!var1.isAlive() || !this.os(var1)) {
         return null;
      } else if (this.nu(var1.getId())) {
         return null;
      } else {
         CrystalAuraPlusPlus.AttackRecord var3 = this.xo.get(var1.getId());
         if (var3 != null) {
            if (var3.zX >= (Integer)this.rm.get()) {
               return null;
            }

            if (this.Eo - var3.fm < (Integer)this.BM.get()) {
               return null;
            }
         }

         Vec3d var4 = this.oF(var1);
         if (var4 == null) {
            return null;
         } else {
            CrystalAuraPlusPlus.DamageProfile var5 = this.op(var1.getEntityPos(), null, var2);
            if (var5 == null) {
               return null;
            } else {
               BlockPos var6 = this.ye.get(var1.getId());
               if (var6 == null) {
                  var6 = var1.getBlockPos().down();
               }

               double var7 = var5.score();
               return new CrystalAuraPlusPlus.BreakPlan(var1, var6, var4, var5, var7);
            }
         }
      }
   }

   private void nP(CrystalAuraPlusPlus.BreakPlan var1) {
      int var2 = this.oo(var1.crystal);
      if (var2 != Integer.MIN_VALUE) {
         if (this.oi(var2)) {
            if (this.os(var1.crystal) && this.oq(var1.crystal.getEntityPos(), null, var1.damage.target)) {
               this.Fn = pb(var1);
               double[] var3 = pF(var1.aim);
               if (this.oI(var1.crystal, var3[0], var3[1])) {
                  if (this.oV(CrystalAuraPlusPlus.CrystalAction.Break, var1.base)) {
                     this.mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(var1.crystal, this.mc.player.isSneaking()));
                     this.mc.player.swingHand(Hand.MAIN_HAND);
                     this.ps(CrystalAuraPlusPlus.CrystalAction.Break, var1.base);
                     this.nU(var1.base);
                     CrystalAuraPlusPlus.AttackRecord var4 = this.xo.computeIfAbsent(var1.crystal.getId(), var0 -> new CrystalAuraPlusPlus.AttackRecord());
                     var4.vD = this.Fn;
                     var4.zX++;
                     var4.fm = this.Eo;
                     this.oY(this.Fn);
                  }
               }
            } else {
               this.zE.zi();
            }
         }
      } else {
         this.zE.zi();
         this.ok();
      }
   }

   private void nQ(CrystalAuraPlusPlus.PlacePlan var1, boolean var2) {
      if (!var1.support) {
         if (this.oa(var1, var2) && var2) {
            this.wd = null;
         }
      } else {
         this.ob(var1);
      }
   }

   private boolean nR(CrystalAuraPlusPlus.PlacePlan var1) {
      return !var1.support ? this.nS(Items.END_CRYSTAL) : this.nS(Items.OBSIDIAN) && this.nS(Items.END_CRYSTAL);
   }

   private boolean nS(Item var1) {
      if (this.mc.player.getOffHandStack().isOf(var1)) {
         return true;
      } else {
         int var2 = this.mc.player.getInventory().getSelectedSlot();
         return this.gk.get() != CrystalAuraPlusPlus.PickItemMode.None
            ? InvUtils.find(new Item[]{var1}).found()
            : this.mc.player.getInventory().getStack(var2).isOf(var1);
      }
   }

   private void nT(BlockPos var1) {
      if ((Boolean)this.EV.get()) {
         CrystalAuraPlusPlus.SupportState var2 = this.Ah.computeIfAbsent(var1, CrystalAuraPlusPlus.SupportState::new);
         var2.nh(System.currentTimeMillis());
         if (this.wW != null && this.wW.equals(var1)) {
            var2.oS++;
         } else {
            if (this.wW != null) {
               CrystalAuraPlusPlus.SupportState var3 = this.Ah.get(this.wW);
               if (var3 != null) {
                  var3.oS = 0;
               }
            }

            var2.oS = 1;
            this.wW = var1;
         }
      }
   }

   private void nU(BlockPos var1) {
      if ((Boolean)this.EV.get() && var1 != null) {
         CrystalAuraPlusPlus.SupportState var2 = this.Ah.computeIfAbsent(var1, CrystalAuraPlusPlus.SupportState::new);
         var2.ni(System.currentTimeMillis());
      }
   }

   private double nV(double var1, BlockPos var3) {
      if (!(Boolean)this.EV.get()) {
         return var1;
      } else {
         CrystalAuraPlusPlus.SupportState var4 = this.Ah.get(var3);
         if (var4 == null) {
            return var1;
         } else {
            long var5 = System.currentTimeMillis();
            int var7 = var4.nj(var5, 500L);
            int var8 = var4.nk(var5, 500L);
            double var9 = var7 * 0.1 + var8 * 0.1 + var4.oS * 0.05;
            return var1 - Math.min(0.5, var9);
         }
      }
   }

   private void nW() {
      if (!(Boolean)this.EV.get()) {
         this.Ah.clear();
         this.wW = null;
      } else {
         long var1 = System.currentTimeMillis();
         if (var1 - this.Ik > 1000L) {
            this.Ah.values().removeIf(var2 -> var1 - var2.nl() > 2000L);
            this.Ik = var1;
         }
      }
   }

   private CrystalAuraPlusPlus.PlacePlan nX(List<PlayerEntity> var1, boolean var2) {
      this.nW();
      int var3 = MathHelper.ceil((Double)this.mm.get()) + 1;
      Vec3d var4 = this.mc.player.getEyePos();
      double var5 = (Double)this.mm.get() * (Double)this.mm.get();
      CrystalAuraPlusPlus.PlacePlan var7 = null;

      for (BlockPos var9 : BlockPos.iterateOutwards(this.mc.player.getBlockPos(), var3, var3, var3)) {
         double var10 = pG(var4, new Box(var9));
         if (!(var10 > var5) && this.mc.player.canInteractWithBlockAt(var9, 0.0)) {
            BlockPos var12 = var9.toImmutable();
            CrystalAuraPlusPlus.PlacePlan var13 = this.nY(var12, var2, var1);
            if (var13 != null) {
               if (var7 == null) {
                  var7 = var13;
               } else {
                  double var14 = var13.damage.score - var7.damage.score;
                  if (Math.abs(var14) > 0.5) {
                     if (var14 > 0.0) {
                        var7 = var13;
                     }
                  } else if (var13.adaptiveScore > var7.adaptiveScore || pp(var13.adaptiveScore, var7.adaptiveScore) && pq(var13.base, var7.base) < 0) {
                     var7 = var13;
                  }
               }
            }
         }
      }

      return var7;
   }

   private CrystalAuraPlusPlus.PlacePlan nY(BlockPos var1, boolean var2, List<PlayerEntity> var3) {
      if (var2) {
         if (!this.ov(var1)) {
            return null;
         }
      } else if (!this.ou(var1)) {
         return null;
      }

      Vec3d var4 = pw(var1);
      CrystalAuraPlusPlus.DamageProfile var5 = this.op(var4, var2 ? var1 : null, var3);
      if (var5 == null) {
         return null;
      } else {
         BlockHitResult var6 = !var2 ? this.oB(var1) : this.oz(var1);
         if (var6 == null) {
            return null;
         } else {
            double var7 = this.nV(var5.score, var1);
            return new CrystalAuraPlusPlus.PlacePlan(var1, var6, var2, var5, var7);
         }
      }
   }

   private CrystalAuraPlusPlus.PlacePlan nZ(BlockPos var1, List<PlayerEntity> var2) {
      if (!this.ow(var1)) {
         return null;
      } else {
         CrystalAuraPlusPlus.DamageProfile var3 = this.op(pw(var1), var1, var2);
         if (var3 == null) {
            return null;
         } else {
            BlockHitResult var4 = this.oA(var1, Direction.UP);
            if (var4 == null) {
               return null;
            } else {
               double var5 = this.nV(var3.score, var1);
               return new CrystalAuraPlusPlus.PlacePlan(var1, var4, false, var3, var5);
            }
         }
      }
   }

   private boolean oa(CrystalAuraPlusPlus.PlacePlan var1, boolean var2) {
      CrystalAuraPlusPlus.HeldItem var3 = this.oc(Items.END_CRYSTAL, this.pm());
      if (var3 == null) {
         return false;
      } else {
         boolean var4 = !var2 ? this.ou(var1.base) : this.ow(var1.base);
         if (var4 && this.oq(pw(var1.base), var2 ? var1.base : null, var1.damage.target)) {
            BlockHitResult var5 = this.oA(var1.base, var1.hit.getSide());
            if (var5 == null) {
               this.zE.zi();
               return false;
            } else {
               this.Fn = pa(var1);
               double[] var6 = pF(var5.getPos());
               if (!this.oH(var5, var6[0], var6[1])) {
                  return false;
               } else if (!this.oV(CrystalAuraPlusPlus.CrystalAction.Place, var1.base)) {
                  return false;
               } else if (!this.ol(var3, var5)) {
                  return false;
               } else {
                  this.mc.player.swingHand(var3.hand);
                  this.ps(CrystalAuraPlusPlus.CrystalAction.Place, var1.base);
                  this.pq++;
                  int[] var7 = hG;
                  if (this.pf() && (Boolean)this.El.get() && (Boolean)this.ab.get()) {
                     var7 = this.oJ(var1.base);
                  }

                  this.CL = new CrystalAuraPlusPlus.PendingCrystal(var1.base, this.Eo, this.Fn, var7);
                  this.nT(var1.base);
                  this.oY(this.Fn);
                  return true;
               }
            }
         } else {
            this.zE.zi();
            return false;
         }
      }
   }

   private void ob(CrystalAuraPlusPlus.PlacePlan var1) {
      CrystalAuraPlusPlus.HeldItem var2 = this.oc(Items.OBSIDIAN, this.pm());
      if (var2 != null) {
         if (this.ov(var1.base) && this.oq(pw(var1.base), var1.base, var1.damage.target)) {
            BlockHitResult var3 = this.oz(var1.base);
            if (var3 == null) {
               this.zE.zi();
            } else {
               this.Fn = pa(var1);
               double[] var4 = pF(var3.getPos());
               if (this.oH(var3, var4[0], var4[1])) {
                  if (this.oV(CrystalAuraPlusPlus.CrystalAction.Place, var1.base)) {
                     if (this.ol(var2, var3)) {
                        this.mc.player.swingHand(var2.hand);
                        this.ps(CrystalAuraPlusPlus.CrystalAction.Place, var1.base);
                        this.pq++;
                        this.wd = new CrystalAuraPlusPlus.PendingSupport(var1.base, this.Eo, this.Fn);
                        this.nT(var1.base);
                        this.oY(this.Fn);
                     }
                  }
               }
            }
         } else {
            this.zE.zi();
         }
      }
   }

   private CrystalAuraPlusPlus.HeldItem oc(Item var1, int var2) {
      int var10000 = !this.mc.player.getOffHandStack().isOf(var1) ? '龒' : '龑';

      while (true) {
         switch (var10000) {
            case 40849:
               this.oe();
               this.ok();
               return new CrystalAuraPlusPlus.HeldItem(Hand.OFF_HAND, -1);
            case 40850:
               var10000 = 40848;
               break;
            default:
               int var3 = this.mc.player.getInventory().getSelectedSlot();
               if (this.gk.get() != CrystalAuraPlusPlus.PickItemMode.None) {
                  int var4 = this.pl(var1);
                  if (var4 >= 0) {
                     this.oe();
                     this.oh(var1, var4);
                     if (!this.oj(var4)) {
                        return null;
                     }

                     return new CrystalAuraPlusPlus.HeldItem(Hand.MAIN_HAND, var4);
                  }

                  int var6;
                  label51:
                  if (var1 == Items.END_CRYSTAL) {
                     if (this.Dj >= 0) {
                        var6 = this.Dj <= 8 ? '鉼' : '鉽';

                        while (true) {
                           int var5 = var6;
                           if (var5 == 37500) {
                              var6 = this.Dj;
                              break label51;
                           }

                           if (var5 != 37501) {
                              break;
                           }

                           var6 = 37499;
                        }
                     }

                     var6 = this.pm();
                  } else {
                     var6 = var2;
                  }

                  this.od(var1, var6);
                  this.zE.zi();
                  return null;
               }

               this.oe();
               this.ok();
               if (!this.mc.player.getInventory().getStack(var3).isOf(var1)) {
                  return null;
               }

               this.oh(var1, var3);
               return new CrystalAuraPlusPlus.HeldItem(Hand.MAIN_HAND, var3);
         }
      }
   }

   private void od(Item var1, int var2) {
      if (var1 != null && var2 >= 0 && var2 <= 8) {
         this.eJ = var1;
         this.tP = var2;
      } else {
         this.oe();
      }
   }

   private void oe() {
      this.eJ = null;
      this.tP = -1;
      this.wf.yQ();
   }

   static void of() {
      CrystalAuraPlusPlus var0 = An;
      int var10000 = var0 != null ? '\uf48f' : '\uf490';

      while (true) {
         int var1 = var10000;
         if (var1 == 62607) {
            var0.og();
            break;
         }

         if (var1 != 62608) {
            break;
         }

         var10000 = 62606;
      }
   }

   private void og() {
      Item var1 = this.eJ;
      if (var1 != null) {
         if (this.isActive() && this.mc.player != null && this.mc.interactionManager != null) {
            if (!this.mc.player.getOffHandStack().isOf(var1) && this.pl(var1) < 0) {
               RefillOutcome var2 = this.wf.yJ(var1, this.tP);
               if (var2 instanceof RefillOutcome.Ready var3) {
                  this.oh(var1, var3.hotbarSlot());
                  this.oe();
               } else if (var2 == RefillOutcome.td) {
                  this.oe();
               }
            } else {
               this.oe();
            }
         } else {
            this.oe();
         }
      }
   }

   private void oh(Item var1, int var2) {
      if (var1 == Items.END_CRYSTAL && var2 >= 0 && var2 <= 8) {
         this.Dj = var2;
      }
   }

   private boolean oi(int var1) {
      if (var1 < 0) {
         if (this.gk.get() == CrystalAuraPlusPlus.PickItemMode.Silent) {
            this.ok();
         }

         return true;
      } else if (this.gk.get() != CrystalAuraPlusPlus.PickItemMode.None) {
         return this.oj(var1);
      } else {
         this.ok();
         return this.mc.player.getInventory().getSelectedSlot() == var1;
      }
   }

   private boolean oj(int var1) {
      if (var1 >= 0 && var1 <= 8) {
         if (this.gk.get() == CrystalAuraPlusPlus.PickItemMode.Silent) {
            return GrimCombatCoordinator.wI(this, var1);
         } else {
            this.ok();
            return this.mc.player.getInventory().getSelectedSlot() == var1 || InvUtils.swap(var1, false);
         }
      } else {
         return false;
      }
   }

   private void ok() {
      GrimCombatCoordinator.wJ(this);
   }

   private boolean ol(CrystalAuraPlusPlus.HeldItem var1, BlockHitResult var2) {
      if (Bw == null) {
         return false;
      } else {
         SequencedPacketCreator var3 = var2x -> new PlayerInteractBlockC2SPacket(var1.hand, var2, var2x);

         try {
            Bw.invoke((ClientPlayerInteractionManager)this.mc.interactionManager, (ClientWorld)this.mc.world, (SequencedPacketCreator)var3);
            return true;
         } catch (Throwable var5) {
            return false;
         }
      }
   }

   private static MethodHandle om() {
      for (Method var3 : ClientPlayerInteractionManager.class.getDeclaredMethods()) {
         Class[] var4 = var3.getParameterTypes();
         if (var3.getReturnType() == void.class && var4.length == 2 && var4[0] == ClientWorld.class && var4[1] == SequencedPacketCreator.class) {
            var3.setAccessible(true);
            return MethodHandles.lookup().unreflect(var3);
         }
      }

      return null;
   }

   static boolean on() {
      return Bw != null;
   }

   private int oo(EndCrystalEntity var1) {
      if ((Boolean)this.bF.get() && this.mc.player.hasStatusEffect(StatusEffects.WEAKNESS)) {
         if (this.gk.get() == CrystalAuraPlusPlus.PickItemMode.None) {
            int var7 = this.mc.player.getInventory().getSelectedSlot();
            ItemStack var8 = this.mc.player.getInventory().getStack(var7);
            return DamageUtils.getAttackDamage(this.mc.player, var1, var8) > 0.0F ? var7 : Integer.MIN_VALUE;
         } else {
            int var2 = -1;
            float var3 = 0.0F;

            for (int var4 = 0; var4 < 9; var4++) {
               ItemStack var5 = this.mc.player.getInventory().getStack(var4);
               float var6 = DamageUtils.getAttackDamage(this.mc.player, var1, var5);
               if (var6 > var3) {
                  var3 = var6;
                  var2 = var4;
               }
            }

            return var2 >= 0 ? var2 : Integer.MIN_VALUE;
         }
      } else {
         return -1;
      }
   }

   private CrystalAuraPlusPlus.DamageProfile op(Vec3d var1, BlockPos var2, List<PlayerEntity> var3) {
      float var4 = this.or(this.mc.player, var1, var2, false);
      if (var4 > (Double)this.Ed.get()) {
         return null;
      } else if ((Boolean)this.FI.get() && pH(this.mc.player) - var4 <= (Double)this.cU.get()) {
         return null;
      } else {
         CrystalAuraPlusPlus.DamageProfile var5 = null;

         for (PlayerEntity var7 : var3) {
            float var8 = this.or(var7, var1, var2, true);
            double var9 = pH(var7) <= this.vS.get() ? Math.min((Double)this.wE.get(), 1.0) : (Double)this.wE.get();
            if (!(var8 < var9) && !(var8 - var4 < (Double)this.fU.get())) {
               double var11 = var8 * 10.0 - var4;
               if (var8 >= pH(var7)) {
                  var11 += 1000.0;
               }

               CrystalAuraPlusPlus.DamageProfile var13 = new CrystalAuraPlusPlus.DamageProfile(var7, var8, var4, var11);
               if (var5 == null || pn(var13, var5)) {
                  var5 = var13;
               }
            }
         }

         return var5;
      }
   }

   private boolean oq(Vec3d var1, BlockPos var2, PlayerEntity var3) {
      if (var3 != null && var3.isAlive() && Friends.get().shouldAttack(var3)) {
         return !var3.isCreative() && !var3.isSpectator() && !(this.mc.player.squaredDistanceTo(var3) > this.CC.get() * this.CC.get())
            ? this.op(var1, var2, List.of(var3)) != null
            : false;
      } else {
         return false;
      }
   }

   private float or(PlayerEntity var1, Vec3d var2, BlockPos var3, boolean var4) {
      RaycastFactory var5 = (var2x, var3x) -> {
         BlockState var4x = var3 != null && var3x.equals(var3) ? Blocks.OBSIDIAN.getDefaultState() : this.mc.world.getBlockState(var3x);
         return var4x.getCollisionShape(this.mc.world, var3x).raycast(var2x.start(), var2x.end(), var3x);
      };
      float var6 = DamageUtils.crystalDamage(var1, var1.getEntityPos(), var1.getBoundingBox(), var2, var5);
      if (!(Boolean)this.qb.get()) {
         return var6;
      } else {
         Vec3d var7 = var1.getVelocity();
         float var8 = DamageUtils.crystalDamage(var1, var1.getEntityPos().add(var7), var1.getBoundingBox().offset(var7), var2, var5);
         return var4 ? Math.min(var6, var8) : Math.max(var6, var8);
      }
   }

   private boolean os(EndCrystalEntity var1) {
      if (!this.mc.player.canAttackEntityIn(var1.getBoundingBox(), 0.0)) {
         return false;
      } else {
         double var2 = this.ot();
         int var10000 = !(pG(this.mc.player.getEyePos(), var1.getBoundingBox()) <= var2 * var2) ? '꤭' : '꤬';

         while (true) {
            switch (var10000) {
               case 43308:
                  return true;
               case 43309:
                  var10000 = 43307;
                  break;
               default:
                  return false;
            }
         }
      }
   }

   private double ot() {
      return (Double)this.Ja.get();
   }

   private boolean ou(BlockPos var1) {
      BlockState var2 = this.mc.world.getBlockState(var1);
      if (!var2.isOf(Blocks.OBSIDIAN) && !var2.isOf(Blocks.BEDROCK)) {
         return false;
      } else if (!this.mc.world.isAir(var1.up())) {
         return false;
      } else {
         int var10000 = this.mc.player.canInteractWithBlockAt(var1, 0.0) ? '\uf3e7' : '\uf3e6';

         while (true) {
            switch (var10000) {
               case 62438:
                  return false;
               case 62439:
                  var10000 = 62437;
                  break;
               default:
                  if (pG(this.mc.player.getEyePos(), new Box(var1)) > (Double)this.mm.get() * (Double)this.mm.get()) {
                     return false;
                  }

                  short var3 = 23128;
                  return this.ox(var1);
            }
         }
      }
   }

   private boolean ov(BlockPos var1) {
      if (this.mc.world.getBlockState(var1).isReplaceable()
         && this.mc.world.isAir(var1.up())
         && Blocks.OBSIDIAN.getDefaultState().canPlaceAt(this.mc.world, var1)
         && this.ox(var1)) {
         char var5;
         while (var5 == '艫') {
            var5 = '艩';
         }

         return this.oz(var1) != null;
      } else {
         return false;
      }
   }

   private boolean ow(BlockPos var1) {
      if (!this.mc.player.canInteractWithBlockAt(var1, 0.0)) {
         return false;
      } else {
         return pG(this.mc.player.getEyePos(), new Box(var1)) > this.mm.get() * this.mm.get() ? false : this.mc.world.isAir(var1.up()) && this.ox(var1);
      }
   }

   private boolean ox(BlockPos var1) {
      for (Entity var3 : this.mc.world.getOtherEntities(null, px(var1))) {
         if (!(var3 instanceof EndCrystalEntity var4) || this.pd() || !this.oy(var4)) {
            return false;
         }
      }

      return true;
   }

   private boolean oy(EndCrystalEntity var1) {
      if (this.nu(var1.getId())) {
         return true;
      } else {
         CrystalAuraPlusPlus.AttackRecord var2 = this.xo.get(var1.getId());
         if (var2 == null) {
            return false;
         } else {
            return var2.zX >= this.rm.get() ? true : this.Eo - var2.fm <= 10;
         }
      }
   }

   private BlockHitResult oz(BlockPos var1) {
      for (Direction var5 : Direction.values()) {
         BlockPos var6 = var1.offset(var5.getOpposite());
         BlockState var7 = this.mc.world.getBlockState(var6);
         if (!var7.isAir()
            && !var7.isReplaceable()
            && this.mc.player.canInteractWithBlockAt(var6, 0.0)
            && !(pG(this.mc.player.getEyePos(), new Box(var6)) > (Double)this.mm.get() * (Double)this.mm.get())) {
            BlockHitResult var8 = this.oA(var6, var5);
            if (var8 != null) {
               return var8;
            }
         }
      }

      return null;
   }

   private BlockHitResult oA(BlockPos var1, Direction var2) {
      BlockHitResult var3 = this.oC(var1, var2);
      if (var3 != null) {
         return var3;
      } else if (this.ep.get() == CrystalAuraPlusPlus.RaycastMode.Visible) {
         return null;
      } else {
         Direction var4 = var2 == null ? pB(var1, this.mc.player.getEyePos()) : var2;
         if (var4 != null && pC(var1, var4, this.mc.player.getEyePos())) {
            Vec3d var5 = this.oD(var1, var4, 0);
            return new BlockHitResult(var5, var4, var1, false);
         } else {
            return null;
         }
      }
   }

   private BlockHitResult oB(BlockPos var1) {
      BlockHitResult var2 = this.oA(var1, Direction.UP);
      return var2 != null ? var2 : this.oA(var1, null);
   }

   private BlockHitResult oC(BlockPos var1, Direction var2) {
      Vec3d var3 = this.mc.player.getEyePos();
      Direction[] var4 = var2 == null ? Direction.values() : new Direction[]{var2};
      Direction[] var5 = var4;
      int var6 = var4.length;
      int var7 = 0;

      label44:
      while (true) {
         int var10000 = var7 >= var6 ? '\ud822' : '\ud821';

         while (true) {
            switch (var10000) {
               case 55329:
                  Direction var8 = var5[var7];

                  for (int var9 = 0; var9 < oE.length; var9++) {
                     Vec3d var10 = this.oD(var1, var8, var9);
                     Vec3d var11 = var10.subtract(var8.getOffsetX() * 0.001, var8.getOffsetY() * 0.001, var8.getOffsetZ() * 0.001);
                     BlockHitResult var12 = this.mc.world.raycast(new RaycastContext(var3, var11, ShapeType.COLLIDER, FluidHandling.NONE, this.mc.player));
                     if (var12.getType() == Type.BLOCK && var12.getBlockPos().equals(var1) && (var2 == null || var12.getSide() == var2)) {
                        return var12;
                     }
                  }

                  var7++;
                  continue label44;
               case 55330:
                  var10000 = 55328;
                  break;
               default:
                  return null;
            }
         }
      }
   }

   private Vec3d oD(BlockPos var1, Direction var2, int var3) {
      int var4 = oE(var1, var2, this.pq, var3);
      return pA(var1, var2, oE[var4], cD[var4]);
   }

   static int oE(BlockPos var0, Direction var1, long var2, int var4) {
      long var5 = (var0.asLong() ^ var1.ordinal() * -4417276706812531889L) + var2;
      int var7 = Math.floorMod(var5, oE.length);
      return Math.floorMod(var7 + var4, oE.length);
   }

   private Vec3d oF(EndCrystalEntity var1) {
      Box var2 = var1.getBoundingBox();
      if (this.ep.get() == CrystalAuraPlusPlus.RaycastMode.Grim) {
         return var2.getCenter();
      } else {
         double var3 = (var2.minX + var2.maxX) * 0.5;
         double var5 = (var2.minY + var2.maxY) * 0.5;
         double var7 = (var2.minZ + var2.maxZ) * 0.5;
         double var9 = Math.min(0.35, (var2.maxX - var2.minX) * 0.35);
         double var11 = Math.min(0.65, (var2.maxY - var2.minY) * 0.35);
         double var13 = Math.min(0.35, (var2.maxZ - var2.minZ) * 0.35);

         for (double var18 : new double[]{var5, var5 - var11, var5 + var11}) {
            for (double var23 : new double[]{var3, var3 - var9, var3 + var9}) {
               for (double var28 : new double[]{var7, var7 - var13, var7 + var13}) {
                  Vec3d var30 = new Vec3d(var23, var18, var28);
                  if (this.oG(var30)) {
                     return var30;
                  }
               }
            }
         }

         return null;
      }
   }

   private boolean oG(Vec3d var1) {
      BlockHitResult var2 = this.mc.world.raycast(new RaycastContext(this.mc.player.getEyePos(), var1, ShapeType.COLLIDER, FluidHandling.NONE, this.mc.player));
      if (var2.getType() == Type.MISS) {
         return true;
      } else {
         double var3 = this.mc.player.getEyePos().squaredDistanceTo(var1);
         return this.mc.player.getEyePos().squaredDistanceTo(var2.getPos()) + 1.0E-4 >= var3;
      }
   }

   private boolean oH(BlockHitResult var1, double var2, double var4) {
      if (this.oT(var1)) {
         return true;
      } else {
         double var6 = !this.mQ ? (GrimCombatCoordinator.wS() ? GrimCombatCoordinator.wQ() : Rotations.serverYaw) : this.qV;
         double var8 = this.mQ ? this.Hj : (!GrimCombatCoordinator.wS() ? Rotations.serverPitch : GrimCombatCoordinator.wR());
         if (Math.abs(MathHelper.wrapDegrees(var2 - var6)) < 0.5 && Math.abs(var4 - var8) < 0.5) {
            return true;
         } else {
            boolean var10 = this.oN(var2, var4);
            if (!this.pf()) {
               int var10000 = !this.pg() ? '\ud8a1' : '\ud8a0';

               label61:
               while (true) {
                  switch (var10000) {
                     case 55456:
                        break label61;
                     case 55457:
                        var10000 = 55455;
                        break;
                     default:
                        if (!var10) {
                           this.zE.zi();
                           return false;
                        }

                        if (!this.zE.zg(var1)) {
                           return false;
                        }

                        boolean var11 = this.oT(var1);
                        if (!var11) {
                           this.zE.zi();
                        }

                        return var11;
                  }
               }
            }

            this.zE.zi();
            return true;
         }
      }
   }

   private boolean oI(EndCrystalEntity var1, double var2, double var4) {
      if (!this.oU(var1)) {
         double var6 = this.mQ ? this.qV : (GrimCombatCoordinator.wS() ? GrimCombatCoordinator.wQ() : Rotations.serverYaw);
         double var8 = this.mQ ? this.Hj : (GrimCombatCoordinator.wS() ? GrimCombatCoordinator.wR() : Rotations.serverPitch);
         if (Math.abs(MathHelper.wrapDegrees(var2 - var6)) < 0.5 && Math.abs(var4 - var8) < 0.5) {
            return true;
         } else {
            boolean var10 = this.oN(var2, var4);
            if (!this.pf()) {
               int var10000 = this.pg() ? 8369 : 8370;

               while (true) {
                  int var12 = var10000;
                  if (var12 == 8369) {
                     break;
                  }

                  if (var12 != 8370) {
                     if (!var10) {
                        this.zE.zi();
                        return false;
                     }

                     if (!this.zE.zh(var1.getId())) {
                        return false;
                     }

                     boolean var11 = this.oU(var1);
                     if (!var11) {
                        this.zE.zi();
                     }

                     return var11;
                  }

                  var10000 = 8368;
               }
            }

            this.zE.zi();
            return true;
         }
      } else {
         return true;
      }
   }

   private int[] oJ(BlockPos var1) {
      if (this.cj < 0) {
         this.oK();
      }

      if (this.cj < 0) {
         return hG;
      } else {
         Vec3d var2 = pw(var1);
         EndCrystalEntity var3 = new EndCrystalEntity(this.mc.world, var2.x, var2.y, var2.z);
         int var4 = this.oo(var3);
         if (var4 == Integer.MIN_VALUE) {
            return hG;
         } else {
            char var14 = '\uf103';
            if (!this.oi(var4)) {
               return hG;
            } else {
               double[] var5 = pF(pk(var3.getBoundingBox()));
               this.oN(var5[0], var5[1]);
               int var6 = (Integer)this.iO.get();
               int[] var7 = new int[var6];
               int var8 = 0;

               label68:
               for (int var9 = 1; var9 <= var6; var9++) {
                  int var10 = this.cj + var9;
                  Entity var11 = this.mc.world.getEntityById(var10);
                  int var10000 = var11 != null ? 27728 : 27729;

                  while (true) {
                     int var15 = var10000;
                     if (var15 == 27728) {
                        if (!(var11 instanceof EndCrystalEntity)) {
                           break;
                        }
                     } else if (var15 == 27729) {
                        var10000 = 27727;
                        continue;
                     }

                     if (!GrimCombatCoordinator.xn(this)) {
                        break label68;
                     }

                     var3.setId(var10);
                     int var12 = GrimCombatCoordinator.xG();
                     this.mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(var3, this.mc.player.isSneaking()));
                     if (GrimCombatCoordinator.xG() == var12) {
                        break label68;
                     }

                     this.mc.player.swingHand(Hand.MAIN_HAND);
                     var7[var8++] = var10;
                     CrystalAuraPlusPlus.AttackRecord var13 = this.xo.computeIfAbsent(var10, var0 -> new CrystalAuraPlusPlus.AttackRecord());
                     var13.vD = this.Fn;
                     var13.zX++;
                     var13.fm = this.Eo;
                     break;
                  }
               }

               if (var8 == 0) {
                  return hG;
               } else {
                  this.ps(CrystalAuraPlusPlus.CrystalAction.Break, var1);
                  this.nU(var1);
                  return var8 != var7.length ? Arrays.copyOf(var7, var8) : var7;
               }
            }
         }
      }
   }

   private void oK() {
      if (this.mc.world != null) {
         for (Entity var2 : this.mc.world.getEntities()) {
            if (var2.getId() > this.cj) {
               this.cj = var2.getId();
            }
         }
      }
   }

   private static boolean oL(int[] var0, int var1) {
      if (var0 == null) {
         return false;
      } else {
         for (int var5 : var0) {
            if (var5 == var1) {
               return true;
            }
         }

         return false;
      }
   }

   private void oM(CrystalAuraPlusPlus.BreakPlan var1) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.pr(CrystalAuraPlusPlus.CrystalAction.Break, var1.base)) {
            if (this.pf()) {
               if (!GrimCombatCoordinator.xn(this)) {
                  return;
               }
            } else if (!GrimCombatCoordinator.wO(this)) {
               return;
            }

            int var2 = this.oo(var1.crystal);
            if (var2 != Integer.MIN_VALUE) {
               if (this.oi(var2)) {
                  Vec3d var3 = this.oF(var1.crystal);
                  if (var3 != null) {
                     double[] var4 = pF(var3);
                     this.oN(var4[0], var4[1]);
                     this.mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(var1.crystal, this.mc.player.isSneaking()));
                     this.mc.player.swingHand(Hand.MAIN_HAND);
                     this.ps(CrystalAuraPlusPlus.CrystalAction.Break, var1.base);
                     CrystalAuraPlusPlus.AttackRecord var5 = this.xo.computeIfAbsent(var1.crystal.getId(), var0 -> new CrystalAuraPlusPlus.AttackRecord());
                     var5.zX++;
                     var5.fm = this.Eo;
                  }
               }
            }
         }
      }
   }

   private boolean oN(double var1, double var3) {
      double var10000;
      if (this.mQ) {
         var10000 = this.qV;
      } else {
         int var16 = !GrimCombatCoordinator.wS() ? '耹' : '耸';

         label38:
         while (true) {
            switch (var16) {
               case 32824:
                  var10000 = GrimCombatCoordinator.wQ();
                  break label38;
               case 32825:
                  var16 = 32823;
                  break;
               default:
                  var10000 = Rotations.serverYaw;
                  break label38;
            }
         }
      }

      double var5 = var10000;
      double var7 = this.mQ ? this.Hj : (!GrimCombatCoordinator.wS() ? Rotations.serverPitch : GrimCombatCoordinator.wR());
      double var9 = var3 - var7;
      double var11 = pD(var5, var1, (Double)this.qH.get());
      double var13 = MathHelper.clamp(var7 + MathHelper.clamp(var9, -(Double)this.iX.get(), (Double)this.iX.get()), -90.0, 90.0);
      boolean var15 = Math.abs(MathHelper.wrapDegrees(var1 - var11)) <= 1.0E-4 && Math.abs(MathHelper.clamp(var3, -90.0, 90.0) - var13) <= 1.0E-4;
      this.qs = new CrystalAuraPlusPlus.RotationIntent((float)var11, (float)var13);
      return var15;
   }

   private void oO() {
      this.qs = new CrystalAuraPlusPlus.RotationIntent(this.qV, this.Hj);
   }

   private void oP() {
      CrystalAuraPlusPlus.RotationIntent var1 = this.qs;
      this.qs = null;
      ClientPlayerEntity var2 = this.mc.player;
      if (var1 != null && var2 != null && !var2.hasVehicle() && var2.input != null) {
         this.oQ();
         this.Dh = new CrystalAuraPlusPlus.AuraRotationInput(var2.input, var2, var1);
         var2.input = this.Dh;
      }
   }

   private void oQ() {
      CrystalAuraPlusPlus.AuraRotationInput var1 = this.Dh;
      if (var1 != null) {
         var1.mj();
         DelegatingInput.qM(this.mc.player, var1);
         this.Dh = null;
      }
   }

   @Override
   public void suspendForItemUse() {
      this.oQ();
      this.qs = null;
      this.zE.zi();
   }

   static PlayerInput oR(PlayerInput var0, Vec2f var1, float var2, float var3) {
      PlayerInput var4 = var0 == null ? PlayerInput.DEFAULT : var0;
      Vec2f var5 = var1 == null ? Vec2f.ZERO : var1;
      if (var5.lengthSquared() <= 1.0E-5F) {
         return new PlayerInput(false, false, false, false, var4.jump(), var4.sneak(), false);
      } else {
         float var6 = (var2 - var3) * (float) (Math.PI / 180.0);
         float var7 = MathHelper.cos(var6);
         float var8 = MathHelper.sin(var6);
         int var9 = MathHelper.clamp(Math.round(var5.x * var7 - var5.y * var8), -1, 1);
         int var10 = MathHelper.clamp(Math.round(var5.y * var7 + var5.x * var8), -1, 1);
         PlayerInput var10000 = new PlayerInput;
         boolean var10002 = var10 > 0;
         boolean var10003 = var10 < 0;
         boolean var10004 = var9 > 0;
         int var10005 = var9 < 0 ? 'ｫ' : 'ｬ';

         while (true) {
            int var11 = var10005;
            if (var11 == 65387) {
               var12 = true;
               break;
            }

            if (var11 != 65388) {
               var12 = false;
               break;
            }

            var10005 = 65386;
         }

         var10000./* $VF: Unable to resugar constructor */<init>(var10002, var10003, var10004, var12, var4.jump(), var4.sneak(), var4.sprint() && var10 > 0);
         return var10000;
      }
   }

   private static Vec2f oS(PlayerInput var0) {
      float var1 = (var0.left() ? 1.0F : 0.0F) - (!var0.right() ? 0.0F : 1.0F);
      float var2 = (!var0.forward() ? 0.0F : 1.0F) - (var0.backward() ? 1.0F : 0.0F);
      return new Vec2f(var1, var2).normalize();
   }

   private boolean oT(BlockHitResult var1) {
      if (this.mQ && var1 != null && this.mc.player != null) {
         Vec3d var2 = this.mc.player.getEyePos();
         double var3 = (Double)this.mm.get();
         if (!pE(var2, this.qV, this.Hj, var3, new Box(var1.getBlockPos()))) {
            return false;
         } else if (this.ep.get() == CrystalAuraPlusPlus.RaycastMode.Grim) {
            return true;
         } else {
            Vec3d var5 = var2.add(Vec3d.fromPolar(this.Hj, this.qV).multiply(var3));
            BlockHitResult var6 = this.mc.world.raycast(new RaycastContext(var2, var5, ShapeType.COLLIDER, FluidHandling.NONE, this.mc.player));
            if (var6.getType() == Type.BLOCK) {
               int var10000 = !var6.getBlockPos().equals(var1.getBlockPos()) ? 'ﶾ' : 'ﶽ';

               while (true) {
                  switch (var10000) {
                     case 64957:
                        if (var6.getSide() == var1.getSide()) {
                           return true;
                        }

                        return false;
                     case 64958:
                        var10000 = 64956;
                        break;
                     default:
                        return false;
                  }
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private boolean oU(EndCrystalEntity var1) {
      return this.mQ && var1 != null && this.mc.player != null && pE(this.mc.player.getEyePos(), this.qV, this.Hj, this.ot(), var1.getBoundingBox());
   }

   private boolean oV(CrystalAuraPlusPlus.CrystalAction var1, BlockPos var2) {
      if (!(Boolean)this.iH.get() || !this.mc.player.isUsingItem() && (this.mc.currentScreen != null || !this.mc.options.useKey.isPressed())) {
         if ((Boolean)this.kN.get() && this.mc.currentScreen == null && this.mc.options.attackKey.isPressed()) {
            return false;
         } else if (this.pf()) {
            return (this.pg() || this.pr(var1, var2)) && GrimCombatCoordinator.xn(this);
         } else {
            return this.pg() ? GrimCombatCoordinator.wO(this) : this.pr(var1, var2) && GrimCombatCoordinator.wO(this);
         }
      } else {
         return false;
      }
   }

   private void oW() {
      this.nr();
      long var1 = this.ns();
      long var3 = System.currentTimeMillis();
      this.E.entrySet().removeIf(var4 -> var3 - var4.getValue() > var1);
      this.xo.entrySet().removeIf(var1x -> {
         if (this.Eo - var1x.getValue().fm <= 10) {
            return false;
         } else {
            Entity var2 = this.mc.world == null ? null : this.mc.world.getEntityById(var1x.getKey());
            return !(var2 instanceof EndCrystalEntity) || !var2.isAlive();
         }
      });
      if (this.CL != null && this.Eo - this.CL.sentTick > 1) {
         this.CL = null;
      }
   }

   private void oX() {
      if ((Boolean)this.GO.get()) {
         int var10000 = this.jb.get() ? '\udf60' : '\udf5f';

         while (true) {
            int var1 = var10000;
            if (var1 == 57183) {
               break;
            }

            if (var1 != 57184) {
               this.dC.removeIf(var1x -> this.Eo - var1x.startedTick > (Integer)this.xW.get());
               return;
            }

            var10000 = 57182;
         }
      }

      this.dC.clear();
   }

   private void oY(CrystalAuraPlusPlus.RenderSnapshot var1) {
      if (var1 != null && (Boolean)this.GO.get() && (Boolean)this.jb.get()) {
         this.dC.removeIf(var1x -> oZ(var1x.snapshot, var1));
         if (this.dC.size() >= 64) {
            this.dC.remove(0);
         }

         this.dC.add(new CrystalAuraPlusPlus.RenderPulse(var1, this.Eo));
      }
   }

   private static boolean oZ(CrystalAuraPlusPlus.RenderSnapshot var0, CrystalAuraPlusPlus.RenderSnapshot var1) {
      if (var0.kind != var1.kind) {
         return false;
      } else {
         Vec3d var2 = pk(var0.box);
         Vec3d var3 = pk(var1.box);
         return var2.squaredDistanceTo(var3) < 1.0E-4;
      }
   }

   private static CrystalAuraPlusPlus.RenderSnapshot pa(CrystalAuraPlusPlus.PlacePlan var0) {
      BlockPos var1 = var0.base;
      return new CrystalAuraPlusPlus.RenderSnapshot(
         CrystalAuraPlusPlus.RenderKind.Place, new Box(var1), new Vec3d(var1.getX() + 0.5, var1.getY() + 1.25, var1.getZ() + 0.5), pc(var0.damage)
      );
   }

   private static CrystalAuraPlusPlus.RenderSnapshot pb(CrystalAuraPlusPlus.BreakPlan var0) {
      Box var1 = var0.crystal.getBoundingBox();
      Box var2 = new Box(var1.minX, var1.minY, var1.minZ, var1.maxX, var1.maxY, var1.maxZ);
      Vec3d var3 = pk(var2);
      return new CrystalAuraPlusPlus.RenderSnapshot(CrystalAuraPlusPlus.RenderKind.Break, var2, new Vec3d(var3.x, var2.maxY + 0.2, var3.z), pc(var0.damage));
   }

   private static String pc(CrystalAuraPlusPlus.DamageProfile var0) {
      return String.format(Locale.ROOT, "T %.1f | S %.1f", var0.targetDamage, var0.selfDamage);
   }

   private boolean pd() {
      return this.Bv.get() == CrystalAuraPlusPlus.CombatMode.Sync;
   }

   private boolean pe() {
      return this.Bv.get() != CrystalAuraPlusPlus.CombatMode.Sync;
   }

   private boolean pf() {
      return this.Bv.get() == CrystalAuraPlusPlus.CombatMode.Danger;
   }

   private boolean pg() {
      return (Boolean)this.vt.get() && this.eq == CrystalAuraPlusPlus.BurstState.BURST;
   }

   private void ph() {
      if (!(Boolean)this.vt.get()) {
         if (this.eq != CrystalAuraPlusPlus.BurstState.SUSTAIN) {
            this.eq = CrystalAuraPlusPlus.BurstState.SUSTAIN;
            this.FO = System.currentTimeMillis();
         }

         this.nv.entrySet().removeIf(var0 -> System.currentTimeMillis() - var0.getValue() > 10000L);
      } else {
         long var1 = System.currentTimeMillis();
         this.nv.entrySet().removeIf(var2 -> var1 - var2.getValue() > 10000L);
         if (this.eq == CrystalAuraPlusPlus.BurstState.BURST) {
            if (var1 - this.fO >= ((Integer)this.pL.get()).intValue()) {
               this.eq = CrystalAuraPlusPlus.BurstState.COAST;
               this.FO = var1;
            }
         } else if (this.eq != CrystalAuraPlusPlus.BurstState.COAST) {
            if (this.mc.player != null && this.mc.world != null) {
               if (var1 - this.fy >= 100L) {
                  this.fy = var1;
                  this.cg = this.pi();
               }

               if (this.cg) {
                  this.cg = false;
                  this.eq = CrystalAuraPlusPlus.BurstState.BURST;
                  this.fO = var1;
               }
            }
         } else {
            int var10000 = var1 - this.FO < ((Integer)this.uH.get()).intValue() ? 11808 : 11807;

            while (true) {
               int var3 = var10000;
               if (var3 == 11807) {
                  this.eq = CrystalAuraPlusPlus.BurstState.SUSTAIN;
                  break;
               }

               if (var3 != 11808) {
                  break;
               }

               var10000 = 11806;
            }
         }
      }
   }

   private boolean pi() {
      if ((Boolean)this.vt.get() && this.eq == CrystalAuraPlusPlus.BurstState.SUSTAIN) {
         if (this.mc.player != null) {
            if (this.mc.world != null) {
               List var1 = this.nM();
               if (var1.isEmpty()) {
                  return false;
               }

               char var15 = '騧';
               CrystalAuraPlusPlus.BurstTrigger var2 = (CrystalAuraPlusPlus.BurstTrigger)this.EC.get();
               if (var2 == CrystalAuraPlusPlus.BurstTrigger.Always) {
                  return true;
               }

               long var3 = System.currentTimeMillis();
               boolean var5 = this.nv.values().stream().anyMatch(var2x -> var3 - var2x < 2500L);
               if (var5) {
                  int var10000 = var2 == CrystalAuraPlusPlus.BurstTrigger.Lethal ? '\ue099' : '\ue098';

                  label131:
                  while (true) {
                     switch (var10000) {
                        case 57496:
                           return true;
                        case 57497:
                           var10000 = 57495;
                           break;
                        default:
                           break label131;
                     }
                  }
               }

               for (PlayerEntity var7 : var1) {
                  boolean var8 = pH(var7) <= (Double)this.vS.get() + 0.001;
                  if (var2 == CrystalAuraPlusPlus.BurstTrigger.LethalFacePop && var8) {
                     return true;
                  }

                  int var18 = !this.nv.containsKey(var7.getId()) ? '\uda47' : '\uda46';

                  label98: {
                     label97:
                     while (true) {
                        switch (var18) {
                           case 55878:
                              if (var3 - this.nv.get(var7.getId()) < 2500L) {
                                 var19 = true;
                                 break label98;
                              }
                              break label97;
                           case 55879:
                              var18 = 55877;
                              break;
                           default:
                              break label97;
                        }
                     }

                     var19 = false;
                  }

                  boolean var9 = var19;
                  if (var9 && var2 != CrystalAuraPlusPlus.BurstTrigger.Lethal) {
                     return true;
                  }

                  CrystalAuraPlusPlus.DamageProfile var10 = null;

                  for (EndCrystalEntity var12 : this.no()) {
                     CrystalAuraPlusPlus.DamageProfile var13 = this.pj(var12, var7);
                     if (var13 != null && (var10 == null || var13.score > var10.score)) {
                        var10 = var13;
                     }
                  }

                  var18 = var10 == null ? '\ue101' : '\ue102';

                  while (true) {
                     int var16 = var18;
                     if (var16 == 57601) {
                        CrystalAuraPlusPlus.PlacePlan var17 = this.nX(List.of(var7), false);
                        if (var17 == null && (Boolean)this.Ju.get()) {
                           var17 = this.nX(List.of(var7), true);
                        }

                        if (var17 != null) {
                           var10 = var17.damage();
                        }
                        break;
                     }

                     if (var16 != 57602) {
                        break;
                     }

                     var18 = 57600;
                  }

                  if (var10 != null && var10.targetDamage >= pH(var7) - 0.001) {
                     return true;
                  }
               }

               return false;
            }
         } else {
            char var14 = '\ue41e';
         }

         return false;
      } else {
         return false;
      }
   }

   private CrystalAuraPlusPlus.DamageProfile pj(EndCrystalEntity var1, PlayerEntity var2) {
      if (var1.isAlive() && this.os(var1)) {
         Vec3d var3 = this.oF(var1);
         return var3 == null ? null : this.op(var1.getEntityPos(), null, List.of(var2));
      } else {
         return null;
      }
   }

   private static Vec3d pk(Box var0) {
      return new Vec3d((var0.minX + var0.maxX) * 0.5, (var0.minY + var0.maxY) * 0.5, (var0.minZ + var0.maxZ) * 0.5);
   }

   private int pl(Item var1) {
      for (int var2 = 0; var2 < 9; var2++) {
         if (this.mc.player.getInventory().getStack(var2).isOf(var1)) {
            return var2;
         }
      }

      return -1;
   }

   private int pm() {
      int var1 = this.mc.player.getInventory().getSelectedSlot();
      if (this.mc.player.getInventory().getStack(var1).isEmpty()) {
         return var1;
      } else {
         for (int var2 = 0; var2 < 9; var2++) {
            if (this.mc.player.getInventory().getStack(var2).isEmpty()) {
               return var2;
            }
         }

         return -1;
      }
   }

   private static boolean pn(CrystalAuraPlusPlus.DamageProfile var0, CrystalAuraPlusPlus.DamageProfile var1) {
      if (Math.abs(var0.score - var1.score) > 1.0E-4) {
         return var0.score > var1.score;
      } else if (Math.abs(var0.targetDamage - var1.targetDamage) > 1.0E-4) {
         return var0.targetDamage > var1.targetDamage;
      } else {
         return Math.abs(var0.selfDamage - var1.selfDamage) > 1.0E-4 ? var0.selfDamage < var1.selfDamage : var0.target.getId() < var1.target.getId();
      }
   }

   private static boolean po(CrystalAuraPlusPlus.DamageProfile var0, CrystalAuraPlusPlus.DamageProfile var1) {
      return Math.abs(var0.score - var1.score) <= 1.0E-4;
   }

   private static boolean pp(double var0, double var2) {
      return Math.abs(var0 - var2) <= 1.0E-4;
   }

   private static int pq(BlockPos var0, BlockPos var1) {
      int var2 = Integer.compare(var0.getY(), var1.getY());
      if (var2 != 0) {
         return var2;
      } else {
         int var3 = Integer.compare(var0.getX(), var1.getX());
         return var3 != 0 ? var3 : Integer.compare(var0.getZ(), var1.getZ());
      }
   }

   private boolean pr(CrystalAuraPlusPlus.CrystalAction var1, BlockPos var2) {
      if (this.pg()) {
         return true;
      } else {
         long var3 = System.nanoTime();
         long var5 = var1 == CrystalAuraPlusPlus.CrystalAction.Place ? this.uj : this.sL;
         BlockPos var7 = var1 == CrystalAuraPlusPlus.CrystalAction.Place ? this.tK : this.dP;
         return pt(var7, var2, var5, var3);
      }
   }

   private void ps(CrystalAuraPlusPlus.CrystalAction var1, BlockPos var2) {
      long var3 = System.nanoTime();
      if (var1 != CrystalAuraPlusPlus.CrystalAction.Place) {
         this.dP = var2.toImmutable();
         this.sL = var3 + pv((Integer)this.Ey.get(), (Integer)this.lM.get());
      } else {
         this.tK = var2.toImmutable();
         this.uj = var3 + pv((Integer)this.HN.get(), (Integer)this.Et.get());
      }
   }

   static boolean pt(BlockPos var0, BlockPos var1, long var2, long var4) {
      if (var1 == null) {
         char var6 = '뛯';
      } else {
         if (var0 == null) {
            return true;
         }

         int var10000 = !var0.equals(var1) ? '鸄' : '鸃';

         while (true) {
            int var7 = var10000;
            if (var7 == 40451) {
               if (pu(var2, var4)) {
                  return true;
               }
               break;
            }

            if (var7 != 40452) {
               return true;
            }

            var10000 = 40450;
         }
      }

      return false;
   }

   private static boolean pu(long var0, long var2) {
      return var0 == 0L || var2 - var0 >= 0L;
   }

   static long pv(int var0, int var1) {
      int var2 = Math.max(0, Math.min(var0, var1));
      int var3 = Math.max(0, Math.max(var0, var1));
      long var4 = var2 != var3 ? ThreadLocalRandom.current().nextLong(var2, var3 + 1L) : var2;
      return var4 * 1000000L;
   }

   private static Vec3d pw(BlockPos var0) {
      return new Vec3d(var0.getX() + 0.5, var0.getY() + 1.0, var0.getZ() + 0.5);
   }

   private static Box px(BlockPos var0) {
      BlockPos var1 = var0.up();
      return new Box(var1.getX(), var1.getY(), var1.getZ(), var1.getX() + 1.0, var1.getY() + 2.0, var1.getZ() + 1.0);
   }

   private static boolean py(EndCrystalEntity var0, BlockPos var1) {
      Vec3d var2 = pw(var1);
      return var0.getEntityPos().squaredDistanceTo(var2) < 0.26;
   }

   private static Vec3d pz(BlockPos var0, Direction var1) {
      return new Vec3d(var0.getX() + 0.5 + var1.getOffsetX() * 0.5, var0.getY() + 0.5 + var1.getOffsetY() * 0.5, var0.getZ() + 0.5 + var1.getOffsetZ() * 0.5);
   }

   private static Vec3d pA(BlockPos var0, Direction var1, double var2, double var4) {
      Vec3d var6 = pz(var0, var1);

      return switch (<unrepresentable>.pE[var1.getAxis().ordinal()] ^ -1603555487) {
         case -1603555488 -> var6.add(var2, 0.0, var4);
         case -1603555486 -> var6.add(0.0, var4, var2);
         case -1603555485 -> var6.add(var2, var4, 0.0);
         default -> throw new MatchException(null, null);
      };
   }

   static Direction pB(BlockPos var0, Vec3d var1) {
      Direction var2 = null;
      double var3 = Double.NEGATIVE_INFINITY;

      for (Direction var8 : Direction.values()) {
         if (pC(var0, var8, var1)) {
            double var9 = switch (var8) {
               case DOWN -> var0.getY() - var1.y;
               case UP -> var1.y - (var0.getY() + 1.0);
               case NORTH -> var0.getZ() - var1.z;
               case SOUTH -> var1.z - (var0.getZ() + 1.0);
               case WEST -> var0.getX() - var1.x;
               case EAST -> var1.x - (var0.getX() + 1.0);
               default -> throw new MatchException(null, null);
            };
            if (var9 > var3) {
               var3 = var9;
               var2 = var8;
            }
         }
      }

      return var2;
   }

   static boolean pC(BlockPos var0, Direction var1, Vec3d var2) {
      return switch (var1) {
         case DOWN -> var2.y <= var0.getY() + 0.03;
         case UP -> var2.y >= var0.getY() + 1.0 - 0.03;
         case NORTH -> var2.z <= var0.getZ() + 0.03;
         case SOUTH -> var2.z >= var0.getZ() + 1.0 - 0.03;
         case WEST -> var2.x <= var0.getX() + 0.03;
         case EAST -> var2.x >= var0.getX() + 1.0 - 0.03;
         default -> throw new MatchException(null, null);
      };
   }

   static double pD(double var0, double var2, double var4) {
      return var0 + MathHelper.clamp(MathHelper.wrapDegrees(var2 - var0), -var4, var4);
   }

   static boolean pE(Vec3d var0, float var1, float var2, double var3, Box var5) {
      Vec3d var6 = var0.add(Vec3d.fromPolar(var2, var1).multiply(var3));
      return var5.contains(var0) || var5.raycast(var0, var6).isPresent();
   }

   private static double[] pF(Vec3d var0) {
      Vec3d var1 = MinecraftClient.getInstance().player.getEyePos();
      double var2 = var0.x - var1.x;
      double var4 = var0.y - var1.y;
      double var6 = var0.z - var1.z;
      double var8 = Math.sqrt(var2 * var2 + var6 * var6);
      return new double[]{Math.toDegrees(Math.atan2(var6, var2)) - 90.0, -Math.toDegrees(Math.atan2(var4, var8))};
   }

   private static double pG(Vec3d var0, Box var1) {
      double var2 = Math.max(var1.minX - var0.x, Math.max(0.0, var0.x - var1.maxX));
      double var4 = Math.max(var1.minY - var0.y, Math.max(0.0, var0.y - var1.maxY));
      double var6 = Math.max(var1.minZ - var0.z, Math.max(0.0, var0.z - var1.maxZ));
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   private static double pH(PlayerEntity var0) {
      return var0.getHealth() + var0.getAbsorptionAmount();
   }

   private static final class AttackRecord {
      private int zX;
      private int fm;
      private CrystalAuraPlusPlus.RenderSnapshot vD;
   }

   private final class AuraRotationInput extends DelegatingInput {
      private final ClientPlayerEntity wV;
      private final CrystalAuraPlusPlus.RotationIntent sW;
      private boolean sw;
      private float gB;
      private float rT;

      private AuraRotationInput(Input var2, ClientPlayerEntity var3, CrystalAuraPlusPlus.RotationIntent var4) {
         super(var2);
         this.wV = var3;
         this.sW = var4;
      }

      @Override
      protected void qL() {
         if (CrystalAuraPlusPlus.this.mc.player == this.wV && CrystalAuraPlusPlus.this.mc.player.input == this) {
            PlayerInput var1 = this.qI().playerInput == null ? PlayerInput.DEFAULT : this.qI().playerInput;
            Vec2f var2 = this.qI().getMovementInput();
            this.gB = this.wV.getYaw();
            this.rT = this.wV.getPitch();
            PlayerInput var3 = CrystalAuraPlusPlus.oR(var1, var2, this.gB, this.sW.yaw);
            this.setState(var3, CrystalAuraPlusPlus.oS(var3));
            this.wV.setYaw(this.sW.yaw);
            this.wV.setPitch(this.sW.pitch);
            this.sw = true;
         }
      }

      private void mj() {
         if (this.sw && CrystalAuraPlusPlus.this.mc.player == this.wV) {
            this.wV.setYaw(this.gB);
            this.wV.setPitch(this.rT);
            this.sw = false;
         }
      }
   }

   private record BreakPlan(EndCrystalEntity crystal, BlockPos base, Vec3d aim, CrystalAuraPlusPlus.DamageProfile damage, double adaptiveScore) {
   }

   private static enum BurstState {
      SUSTAIN,
      BURST,
      COAST;
   }

   public static enum BurstTrigger {
      Lethal,
      LethalOrPop,
      LethalFacePop,
      Always;
   }

   public static enum CombatMode {
      Sync,
      Speed,
      Danger;
   }

   private static enum CrystalAction {
      Place,
      Break;
   }

   private record DamageProfile(PlayerEntity target, float targetDamage, float selfDamage, double score) {
   }

   private record HeldItem(Hand hand, int slot) {
   }

   private record PendingCrystal(BlockPos base, int sentTick, CrystalAuraPlusPlus.RenderSnapshot render, int[] predictedIds) {
   }

   private static final class PendingSupport {
      private final BlockPos Fm;
      private final int kK;
      private final CrystalAuraPlusPlus.RenderSnapshot wG;

      private PendingSupport(BlockPos var1, int var2, CrystalAuraPlusPlus.RenderSnapshot var3) {
         this.Fm = var1;
         this.kK = var2;
         this.wG = var3;
      }
   }

   public static enum PickItemMode {
      Silent,
      Pick,
      None;
   }

   private record PlacePlan(BlockPos base, BlockHitResult hit, boolean support, CrystalAuraPlusPlus.DamageProfile damage, double adaptiveScore) {
   }

   public static enum RaycastMode {
      Visible,
      Grim;
   }

   private static enum RenderKind {
      Place,
      Break;
   }

   private record RenderPulse(CrystalAuraPlusPlus.RenderSnapshot snapshot, int startedTick) {
   }

   private record RenderSnapshot(CrystalAuraPlusPlus.RenderKind kind, Box box, Vec3d label, String damageLabel) {
   }

   private record RotationIntent(float yaw, float pitch) {
   }

   private static final class SupportState {
      public final BlockPos Jy;
      public final LongArrayList DS = new LongArrayList();
      public final LongArrayList ml = new LongArrayList();
      public int oS;

      public SupportState(BlockPos var1) {
         this.Jy = var1;
      }

      public void nh(long var1) {
         this.DS.add(var1);
      }

      public void ni(long var1) {
         this.ml.add(var1);
      }

      public int nj(long var1, long var3) {
         long var5 = var1 - var3;
         int var7 = 0;

         for (int var8 = this.DS.size() - 1; var8 >= 0 && this.DS.getLong(var8) >= var5; var8--) {
            var7++;
         }

         return var7;
      }

      public int nk(long var1, long var3) {
         long var5 = var1 - var3;
         int var7 = 0;

         for (int var8 = this.ml.size() - 1; var8 >= 0 && this.ml.getLong(var8) >= var5; var8--) {
            var7++;
         }

         return var7;
      }

      public long nl() {
         long var1 = this.DS.isEmpty() ? 0L : this.DS.getLong(this.DS.size() - 1);
         long var3 = this.ml.isEmpty() ? 0L : this.ml.getLong(this.ml.size() - 1);
         return Math.max(var1, var3);
      }
   }
}
