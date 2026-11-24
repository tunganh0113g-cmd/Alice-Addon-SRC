package com.nnpg.alice.obf;

import com.nnpg.alice.AliceAddon;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.ProtectionDomain;
import java.util.HexFormat;
import org.joml.Math;

final class HardwareFingerprint {
   private HardwareFingerprint() {
   }

   static String aw() throws IOException, GeneralSecurityException {
      ProtectionDomain var0 = AliceAddon.class.getProtectionDomain();
      CodeSource var10000;
      if (var0 != null) {
         var10000 = var0.getCodeSource();
      } else {
         var10000 = null;
         char var10001 = '둳';

         label25:
         while (true) {
            switch (var10001) {
               case '둲':
                  Math.clamp(0.9572933493229229, 0.600232266791669, 0.7848637866795741);
                  LicenseBootstrapper.bA();
                  return null;
               case '둳':
                  var10001 = '둱';
                  break;
               default:
                  break label25;
            }
         }
      }

      CodeSource var1 = var10000;
      if (var1 != null && var1.getLocation() != null) {
         return az(Path.of(var1.getLocation().toURI()));
      } else {
         throw new IOException("Loader code source is unavailable");
      }
   }

   static String ax(String var0, long var1, int var3) throws IOException, GeneralSecurityException {
      ProtectionDomain var4 = AliceAddon.class.getProtectionDomain();
      CodeSource var5 = var4 == null ? null : var4.getCodeSource();
      if (var5 != null && var5.getLocation() != null) {
         return ay(Path.of(var5.getLocation().toURI()), var0, var1, var3);
      } else {
         throw new IOException("Loader code source is unavailable");
      }
   }

   static String ay(Path var0, String var1, long var2, int var4) throws IOException, GeneralSecurityException {
      Path var5 = var0.toAbsolutePath().normalize();
      if (!Files.isRegularFile(var5)) {
         throw new IOException("Loader code source is not a JAR file");
      } else if (var2 >= 0L && var4 > 0) {
         int var10000 = var2 + var4 <= Files.size(var5) ? '覆' : '襁';

         while (true) {
            switch (var10000 ^ 531681169) {
               case 531641638:
                  var10000 = 64181;
                  break;
               case 531641639:
                  throw new IOException("Loader proof range falls outside the loader JAR");
               default:
                  MessageDigest var6 = MessageDigest.getInstance("SHA-256");
                  var6.update("alice-loader-range-v1|".getBytes(StandardCharsets.UTF_8));
                  var6.update(var1.getBytes(StandardCharsets.UTF_8));
                  var6.update((byte)124);
                  InputStream var7 = Files.newInputStream(var5);
                  var7.skipNBytes(var2);
                  byte[] var8 = var7.readNBytes(var4);
                  if (var8.length != var4) {
                     throw new IOException("Loader JAR is shorter than the proof range");
                  }

                  var6.update(var8);
                  if (var7 != null) {
                     var7.close();
                     char var11 = '\udb83';

                     while (true) {
                        char var10 = var11;
                        if (var10 == '\udb83') {
                           break;
                        }

                        if (var10 != '\udb84') {
                           LicenseBootstrapper.bA();
                           return null;
                        }

                        var11 = '\udb82';
                     }
                  }

                  return HexFormat.of().formatHex(var6.digest());
            }
         }
      } else {
         throw new IOException("Loader proof range falls outside the loader JAR");
      }
   }

   static String az(Path var0) throws IOException, GeneralSecurityException {
      Path var1 = var0.toAbsolutePath().normalize();
      if (!Files.isRegularFile(var1)) {
         throw new IOException("Loader code source is not a JAR file");
      } else {
         MessageDigest var2 = MessageDigest.getInstance("SHA-256");
         byte[] var3 = new byte[16384];
         InputStream var4 = Files.newInputStream(var1);

         int var5;
         while ((var5 = var4.read(var3)) != -1) {
            var2.update(var3, 0, var5);
         }

         if (var4 != null) {
            var4.close();
         }

         return HexFormat.of().formatHex(var2.digest());
      }
   }
}
