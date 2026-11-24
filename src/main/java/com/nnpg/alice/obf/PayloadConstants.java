package com.nnpg.alice.obf;

import com.nnpg.alice.bridge.NameProtectionBridge;
import java.net.URI;
import java.time.Duration;
import java.util.Base64;
import java.util.Locale;
import org.lwjgl.util.freetype.TT_MaxProfile;

final class PayloadConstants {
   static final String d = X("1.21.11-alice.11");
   static final String B = X("https://auth.dungxip.xyz");
   static final String aE = X("https://auth.dungxip.xyz");
   static final String aU = X("com.nnpg.alice.AlicePayloadEntrypoint");
   static final String aN = X("init");
   static final String aO = X("com.nnpg.alice.protection.W");
   static final String ah = X("a");
   static final URI bC = URI.create(B);
   static final Duration V = Duration.ofSeconds(15L);
   static final Duration bi = Duration.ofSeconds(2L);
   static final byte[] o = Base64.getDecoder().decode("MCowBQYDK2VwAyEASzn8rJxyUQurzLZr+78tyLCw4XH4JDz5xy4lfJ4QWOg=");

   private PayloadConstants() {
   }

   private static String X(String var0) {
      return var0;
   }

   static boolean Y() {
      String var0 = String.join(
            " ",
            System.getProperty("os.name", ""),
            System.getProperty("java.vm.name", ""),
            System.getProperty("java.runtime.name", ""),
            System.getProperty("java.vendor", ""),
            System.getProperty("net.kdt.pojavlaunch", ""),
            System.getProperty("pojavlauncher", "")
         )
         .toLowerCase(Locale.ROOT);
      if (!var0.contains("android")) {
         int var10000 = var0.contains("dalvik") ? '뺆' : '뺅';

         while (true) {
            int var1 = var10000;
            if (var1 == 48773) {
               if (!var0.contains("pojav")) {
                  return Z("ANDROID_ROOT") || Z("ANDROID_DATA") || Z("POJAV_RENDERER") || Z("POJAVLAUNCHER");
               }
               break;
            }

            if (var1 != 48774) {
               break;
            }

            short var4 = 12622;

            while (true) {
               short var2 = var4;
               if (var2 == 12622) {
                  int var3 = 1816875576;
                  var10000 = 48772;
                  break;
               }

               if (var2 != 12623) {
                  TT_MaxProfile.nmaxTwilightPoints(-988861482230785995L);
                  return NameProtectionBridge.cJ();
               }

               var4 = 12621;
            }
         }
      }

      return true;
   }

   private static boolean Z(String var0) {
      String var1 = System.getenv(var0);
      return var1 != null && !var1.isBlank();
   }
}
