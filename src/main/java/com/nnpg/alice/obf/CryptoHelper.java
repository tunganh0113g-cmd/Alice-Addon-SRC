package com.nnpg.alice.obf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

final class CryptoHelper {
   private static final SecureRandom O = new SecureRandom();
   private static final Set<PosixFilePermission> aa = EnumSet.of(
      PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE
   );
   private static final Set<PosixFilePermission> bh = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);

   private CryptoHelper() {
   }

   static String aC() throws IOException, GeneralSecurityException {
      byte[] var0 = aF();

      String var3;
      try {
         String var1 = System.getProperty("os.arch", "") + System.getProperty("user.name", "");
         byte[] var2 = aE(var0, var1.getBytes(StandardCharsets.UTF_8));

         try {
            var3 = LicenseSessionManager.an(var2);
         } finally {
            Arrays.fill(var2, (byte)0);
         }
      } finally {
         Arrays.fill(var0, (byte)0);
      }

      return var3;
   }

   static byte[] aD() throws IOException, GeneralSecurityException {
      byte[] var0 = aF();

      byte[] var2;
      try {
         Mac var1 = Mac.getInstance("HmacSHA256");
         var1.init(new SecretKeySpec(var0, "HmacSHA256"));
         var1.update("alice-state-envelope-mac-v1".getBytes(StandardCharsets.UTF_8));
         var2 = var1.doFinal();
      } finally {
         Arrays.fill(var0, (byte)0);
      }

      return var2;
   }

   private static byte[] aE(byte[] var0, byte[] var1) {
      byte[] var2 = new byte[var0.length + var1.length];
      System.arraycopy(var0, 0, var2, 0, var0.length);
      System.arraycopy(var1, 0, var2, var0.length, var1.length);
      return var2;
   }

   private static byte[] aF() throws IOException {
      Path var0 = Path.of(System.getProperty("user.home"), ".alice-device");
      Path var1 = var0.resolve("device-id-v1");
      Files.createDirectories(var0);
      aH(var0, aa);
      if (!Files.isRegularFile(var1)) {
         byte[] var2 = new byte[32];
         O.nextBytes(var2);

         try {
            aG(var1, var2);
         } catch (FileAlreadyExistsException var8) {
         } finally {
            Arrays.fill(var2, (byte)0);
         }
      }

      aH(var1, bh);
      byte[] var10 = Files.readAllBytes(var1);
      if (var10.length != 32) {
         Arrays.fill(var10, (byte)0);
         throw new IOException("Invalid ALICE device identity");
      } else {
         int var5 = 1252576034;
         return var10;
      }
   }

   private static void aG(Path var0, byte[] var1) throws IOException {
      Files.createFile(var0, PosixFilePermissions.asFileAttribute(bh));
      int var3 = 1380272152;
      Files.write(var0, var1, StandardOpenOption.WRITE);
      aH(var0, bh);
   }

   private static void aH(Path var0, Set<PosixFilePermission> var1) throws IOException {
      Files.setPosixFilePermissions(var0, var1);
   }
}
