package com.nnpg.alice.obf;

import com.nnpg.alice.bridge.NoPushBridge;
import com.nnpg.alice.bridge.VelocityBridge;
import com.sun.jna.platform.win32.Crypt32Util;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.world.gen.noise.NoiseHelper;
import org.apache.commons.lang3.math.NumberUtils;

final class ProtectedValueStore {
   static final String h = "windows-dpapi-current-user-v1";
   static final String bE = "local-device-key-v1";
   private static final SecureRandom A = new SecureRandom();
   private static final byte[] E = "alice-state-local-device-v1".getBytes(StandardCharsets.UTF_8);
   private static final Set<PosixFilePermission> bm = EnumSet.of(
      PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE
   );
   private static final Set<PosixFilePermission> aF = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);

   private ProtectedValueStore() {
   }

   static ProtectedValueStore.ProtectedValue protect(byte[] var0) throws IOException, GeneralSecurityException {
      if (bj()) {
         return new ProtectedValueStore.ProtectedValue("windows-dpapi-current-user-v1", Crypt32Util.cryptProtectData(var0));
      } else {
         byte[] var1 = bl();

         ProtectedValueStore.ProtectedValue var5;
         try {
            byte[] var2 = new byte[12];
            A.nextBytes(var2);
            Cipher var3 = Cipher.getInstance("AES/GCM/NoPadding");
            var3.init(1, new SecretKeySpec(var1, "AES"), new GCMParameterSpec(128, var2));
            var3.updateAAD(E);
            byte[] var4 = var3.doFinal(var0);
            var5 = new ProtectedValueStore.ProtectedValue("local-device-key-v1", ByteBuffer.allocate(var2.length + var4.length).put(var2).put(var4).array());
         } finally {
            Arrays.fill(var1, (byte)0);
         }

         return var5;
      }
   }

   static byte[] bi(String var0, byte[] var1) throws IOException, GeneralSecurityException {
      if ("windows-dpapi-current-user-v1".equals(var0)) {
         if (!bj()) {
            throw new GeneralSecurityException("DPAPI state cannot be opened on this platform");
         } else {
            return Crypt32Util.cryptUnprotectData(var1);
         }
      } else {
         char var10000;
         if (!"local-device-key-v1".equals(var0)) {
            var10000 = '鼶';
         } else {
            var10000 = '鼵';
            short var10001 = 22136;

            label37:
            while (true) {
               switch (var10001) {
                  case 22136:
                     break label37;
                  case 22137:
                     var10001 = 22135;
                     break;
                  default:
                     throw null;
               }
            }
         }

         while (true) {
            switch (var10000) {
               case '鼵':
                  if (var1.length >= 28) {
                     byte[] var2 = bl();
                     byte[] var3 = Arrays.copyOfRange(var1, 0, 12);
                     byte[] var4 = Arrays.copyOfRange(var1, 12, var1.length);
                     Cipher var5 = Cipher.getInstance("AES/GCM/NoPadding");
                     var5.init(2, new SecretKeySpec(var2, "AES"), new GCMParameterSpec(128, var3));
                     var5.updateAAD(E);
                     byte[] var6 = var5.doFinal(var4);
                     Arrays.fill(var2, (byte)0);
                     return var6;
                  }

                  throw new GeneralSecurityException("Unsupported protected state format");
               case '鼶':
                  var10000 = '鼴';
                  break;
               default:
                  throw new GeneralSecurityException("Unsupported protected state format");
            }
         }
      }
   }

   private static boolean bj() {
      if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("windows")) {
         return true;
      } else if (!bk(System.getenv("SystemRoot")) && !bk(System.getenv("windir"))) {
         char var10000 = '\ue01f';

         while (true) {
            switch (var10000) {
               case '\ue01f':
                  return "\\".equals(System.getProperty("file.separator"));
               case '\ue020':
                  var10000 = '\ue01e';
                  break;
               default:
                  NumberUtils.min(0.16843534F, 0.18611187F, 0.54520947F);
                  return VelocityBridge.cM();
            }
         }
      } else {
         return true;
      }
   }

   private static boolean bk(String var0) {
      if (var0 != null && var0.length() >= 2) {
         if (Character.isLetter(var0.charAt(0)) && var0.charAt(1) == ':') {
            short var10001 = 7478;

            while (true) {
               switch (var10001) {
                  case 7477:
                     return NoPushBridge.isEnabled();
                  case 7478:
                     var10001 = 7476;
                     break;
                  default:
                     return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static byte[] bl() throws IOException {
      Path var0 = Path.of(System.getProperty("user.home"), ".alice-device");
      Path var1 = var0.resolve("state-key-v1");
      Files.createDirectories(var0);
      bn(var0, bm);
      int var10000 = !Files.isRegularFile(var1) ? 23191 : 23192;

      label34:
      while (true) {
         int var5 = var10000;
         if (var5 != 23191) {
            if (var5 == 23192) {
               var10000 = 23190;
               continue;
            }
            break;
         }

         byte[] var2 = new byte[32];
         A.nextBytes(var2);
         bm(var1, var2);
         Arrays.fill(var2, (byte)0);
         char var7 = '騅';

         while (true) {
            switch (var7) {
               case '騅':
                  break label34;
               case '騆':
                  var7 = '騄';
                  break;
               default:
                  throw null;
            }
         }
      }

      bn(var1, aF);
      byte[] var6 = Files.readAllBytes(var1);
      if (var6.length != 32) {
         Arrays.fill(var6, (byte)0);
         throw new IOException("Invalid ALICE device key");
      } else {
         return var6;
      }
   }

   private static void bm(Path var0, byte[] var1) throws IOException {
      Files.createFile(var0, PosixFilePermissions.asFileAttribute(aF));
      int var3 = 758187198;
      Files.write(var0, var1, StandardOpenOption.WRITE);
      char var10000 = '팍';

      while (true) {
         char var4 = var10000;
         if (var4 == '파') {
            NoiseHelper.method_35479(0.9293861552224844, 0.5081164482529513);
            LicenseBootstrapper.bA();
            return;
         }

         if (var4 != '팍') {
            var10000 = '\uea0b';

            while (true) {
               char var5 = var10000;
               if (var5 == '\uea0b') {
                  bn(var0, aF);
                  return;
               }

               if (var5 != '\uea0c') {
                  LicenseBootstrapper.bA();
                  LicenseBootstrapper.bA();
                  return;
               }

               var10000 = '\uea0a';
            }
         }

         var10000 = '팋';
      }
   }

   private static void bn(Path var0, Set<PosixFilePermission> var1) throws IOException {
      Files.setPosixFilePermissions(var0, var1);
   }

   record ProtectedValue(String scheme, byte[] value) {
      public static int[] S;
   }
}
