package com.nnpg.alice.obf;

import com.azure.json.implementation.jackson.core.io.CharTypes;
import java.lang.invoke.StringConcatFactory;
import java.net.URI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

final class LicenseScreen extends Screen {
   private static final int bf = 520;
   private static final int an = 42;
   private final String bv;
   private final String bL;
   private final Screen ab;
   private boolean av;

   LicenseScreen(String var1, String var2, Screen var3) {
      super(Text.literal("ALICE verification required"));
      this.bv = var1;
      this.bL = var2;
      this.ab = var3;
   }

   protected void init() {
      int var1 = this.width / 2;
      int var2 = Math.min(220, Math.max(140, this.width - 32));
      int var3 = var1 - var2 / 2;
      int var4 = this.height - 82;
      char var10001;
      if (!this.av) {
         var10001 = '\ue762';
      } else {
         var10001 = '\ue761';
         char var10002 = '낭';

         label54:
         while (true) {
            switch (var10002) {
               case '낭':
                  break label54;
               case '낮':
                  var10002 = '났';
                  break;
               default:
                  MathHelper.absMax(0.7794077764185353, 0.37585891283986594);
                  LicenseBootstrapper.bA();
                  return;
            }
         }
      }

      label44:
      while (true) {
         char var5 = var10001;
         if (var5 != '\ue761') {
            if (var5 == '\ue762') {
               int var6 = 965640467;
               var10001 = '\ue760';
               continue;
            }

            var8 = "Copy verification URL";
            break;
         }

         var8 = "URL copied";
         char var9 = '깧';

         while (true) {
            char var7 = var9;
            if (var7 == '깦') {
               var9 = '跁';

               while (true) {
                  switch (var9) {
                     case '跀':
                        CharTypes.hexToChar(-1066287611);
                        LicenseBootstrapper.bA();
                        return;
                     case '跁':
                        var9 = '趿';
                        break;
                     default:
                        LicenseBootstrapper.bA();
                        return;
                  }
               }
            }

            if (var7 != '깧') {
               break label44;
            }

            var9 = '깥';
         }
      }

      this.addDrawableChild(ButtonWidget.builder(Text.literal(var8), var1x -> {
         if (this.client != null) {
            this.client.keyboard.setClipboard(this.bv);
            this.av = true;
            this.clearAndInit();
         }
      }).dimensions(var3, var4, var2, 20).build());
      this.addDrawableChild(
         ButtonWidget.builder(Text.literal("Open in browser"), var1x -> Util.getOperatingSystem().open(URI.create(this.bv).toString()))
            .dimensions(var3, var4 + 25, var2, 20)
            .build()
      );
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      context.fill(0, 0, this.width, this.height, -15724528);
      int var5 = this.width / 2;
      int var6 = Math.min(520, this.width - 20);
      int var7 = var5 - var6 / 2;
      int var8 = Math.min(42, Math.max(12, this.height / 8));
      int var9 = Math.max(var8 + 130, this.height - 24);
      context.fill(var7, var8, var7 + var6, var9, -535818224);
      context.fill(var7, var8, var7 + var6, var8 + 2, -11141206);
      context.drawCenteredTextWithShadow(this.textRenderer, this.title, var5, var8 + 14, -1);
      int var10 = var8 + 40;

      for (OrderedText var12 : this.textRenderer
         .wrapLines(Text.literal("Open the verification link on your phone, complete the check, then return to Minecraft."), var6 - 30)) {
         context.drawCenteredTextWithShadow(this.textRenderer, var12, var5, var10, -2039584);
         var10 += 12;
      }

      var10 += 8;
      context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Copy this URL:"), var5, var10, -5592406);
      var10 += 14;

      for (OrderedText var13 : this.textRenderer.wrapLines(Text.literal(this.bv), var6 - 30)) {
         context.drawCenteredTextWithShadow(this.textRenderer, var13, var5, var10, -11141206);
         var10 += 12;
      }

      if (this.bL != null) {
         var10 += 10;
         context.drawCenteredTextWithShadow(
            this.textRenderer,
            Text.literal(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","This device's code: ">(this.bL)),
            var5,
            var10,
            -9882
         );
         var10 += 14;

         for (OrderedText var21 : this.textRenderer
            .wrapLines(Text.literal("Discord must ask you to confirm this same code. Never confirm a code someone sent you."), var6 - 30)) {
            context.drawCenteredTextWithShadow(this.textRenderer, var21, var5, var10, -5592406);
            var10 += 12;
         }
      }

      super.render(context, mouseX, mouseY, deltaTicks);
   }

   public void close() {
      char var4;
      if (this.client != null) {
         this.client.setScreen(this.ab);
      } else {
         while (var4 == '\uf30a') {
            var4 = '\uf308';
         }
      }
   }

   public boolean shouldPause() {
      return false;
   }
}
