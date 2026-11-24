package com.nnpg.alice.obf;

import com.nnpg.alice.bridge.PayloadRuntimeHost;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.StringConcatFactory;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

final class PayloadClassLoader extends ClassLoader implements PayloadRuntimeHost {
   private static final String r = "com.nnpg.alice.mixins.";
   private static final String m = "ALICE-BC2-";
   private static final int bA = 5;
   private static final int bk = 32;
   private static final String bF = "META-INF/alice/r/";
   private static final String ar = ".bin";
   private static final String aW = "ALICE-RSRC-1\u0000";
   private static final int M = 64;
   private static final int af = 2048;
   private static final int bB = 50331648;
   private static final int by = 16777216;
   private static final int aI = 512;
   private static final int bG = 16777216;
   private final Map<String, byte[]> C;
   private final Map<String, byte[]> be;
   private final AtomicBoolean bs = new AtomicBoolean(true);
   private final long bH;

   private PayloadClassLoader(Map<String, byte[]> var1, Map<String, byte[]> var2, ClassLoader var3, long var4) {
      super(var3);
      this.C = var1;
      this.be = var2;
      this.bH = var4;
   }

   static PayloadClassLoader fromJar(byte[] var0, ClassLoader var1, List<String> var2, long var3) throws IOException {
      PayloadClassLoader.ArchiveContent var5 = aS(var0);

      try {
         if (!var5.classes.containsKey(PayloadConstants.aU)) {
            throw new IOException("Signed payload is missing its entrypoint");
         } else {
            verifyPersonalization(var5.classes, var2);
            return new PayloadClassLoader(var5.classes, var5.resources, var1, var3);
         }
      } catch (RuntimeException | IOException var7) {
         ba(var5.classes);
         ba(var5.resources);
         throw var7;
      }
   }

   static void verifyPersonalization(byte[] var0, List<String> var1) throws IOException {
      PayloadClassLoader.ArchiveContent var2 = aS(var0);

      try {
         verifyPersonalization(var2.classes, var1);
      } finally {
         ba(var2.classes);
         ba(var2.resources);
      }
   }

   private static PayloadClassLoader.ArchiveContent aS(byte[] var0) throws IOException {
      HashMap var1 = new HashMap();
      HashMap var2 = new HashMap();
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      byte[] var6 = new byte[16384];

      try {
         ZipEntry var8;
         try (ZipInputStream var7 = new ZipInputStream(new ByteArrayInputStream(var0))) {
            while ((var8 = var7.getNextEntry()) != null) {
               var3 = Math.addExact(var3, 1);
               if (var3 > 2048) {
                  throw new IOException("Signed payload has too many archive entries");
               }

               if (!var8.isDirectory()) {
                  ByteArrayOutputStream var9 = new ByteArrayOutputStream();
                  int var10 = 0;

                  int var11;
                  while ((var11 = var7.read(var6)) != -1) {
                     var10 = Math.addExact(var10, var11);
                     var4 = Math.addExact(var4, var11);
                     if (var10 > 16777216 || var4 > 50331648) {
                        throw new IOException("Signed payload expands beyond its safety limit");
                     }

                     var9.write(var6, 0, var11);
                  }

                  byte[] var12 = var9.toByteArray();
                  if (var8.getName().endsWith(".class")) {
                     String var13 = var8.getName().substring(0, var8.getName().length() - 6).replace('/', '.');
                     byte[] var14 = var1.putIfAbsent(var13, var12);
                     if (var14 != null) {
                        ba(var1);
                        ba(var2);
                        throw new IOException("Signed payload contains a duplicate class");
                     }
                  } else if (!aX(var8.getName())) {
                     Arrays.fill(var12, (byte)0);
                  } else {
                     var5 = Math.addExact(var5, var12.length);
                     if (var2.size() >= 512 || var5 > 16777216 || var2.putIfAbsent(var8.getName(), var12) != null) {
                        Arrays.fill(var12, (byte)0);
                        ba(var1);
                        ba(var2);
                        throw new IOException("Signed payload has invalid hashed resources");
                     }
                  }
               }
            }
         } catch (ArithmeticException var23) {
            ba(var1);
            ba(var2);
            throw new IOException("Signed payload resource size overflow", var23);
         } catch (RuntimeException | IOException var24) {
            ba(var1);
            ba(var2);
            throw var24;
         }
      } finally {
         Arrays.fill(var6, (byte)0);
      }

      return new PayloadClassLoader.ArchiveContent(var1, var2);
   }

   private static void verifyPersonalization(Map<String, byte[]> var0, List<String> var1) throws IOException {
      if (var1 != null && var1.size() == 5 && !var1.stream().anyMatch(var0x -> !aT(var0x)) && new HashSet(var1).size() == 5) {
         HashSet var2 = new HashSet();

         for (int var3 = 0; var3 < 5; var3++) {
            char var4 = (char)(65 + var3);
            byte[] var5 = (var4 + "").getBytes(StandardCharsets.US_ASCII);
            byte[] var6 = ((String)var1.get(var3)).getBytes(StandardCharsets.US_ASCII);
            int var7 = 0;
            String var8 = null;
            boolean var9 = false;

            for (Entry var11 : var0.entrySet()) {
               byte[] var12 = (byte[])var11.getValue();
               int var13 = 0;

               while ((var13 = aU(var12, var5, var13)) >= 0) {
                  var7++;
                  int var14 = var13 + var5.length;
                  int var15 = var14 + 32;
                  if (var15 <= var12.length) {
                     var9 = MessageDigest.isEqual(Arrays.copyOfRange(var12, var14, var15), var6);
                  }

                  var8 = (String)var11.getKey();
                  var13 = var14;
               }
            }

            if (var7 != 1 || !var9 || var8 == null || !var2.add(var8)) {
               throw new IOException("Signed payload bytecode personalization check failed");
            }
         }
      } else {
         throw new IOException("Signed payload has an invalid personalization manifest");
      }
   }

   private static boolean aT(String var0) {
      if (var0 != null && var0.length() == 32) {
         for (int var1 = 0; var1 < var0.length(); var1++) {
            char var2 = var0.charAt(var1);
            if ((var2 < 'A' || var2 > 'Z') && (var2 < 'a' || var2 > 'z') && (var2 < '0' || var2 > '9') && var2 != '_' && var2 != '-') {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static int aU(byte[] var0, byte[] var1, int var2) {
      if (var1.length != 0 && var1.length <= var0.length) {
         for (int var3 = Math.max(0, var2); var3 <= var0.length - var1.length; var3++) {
            boolean var4 = true;

            for (int var5 = 0; var5 < var1.length; var5++) {
               if (var0[var3 + var5] != var1[var5]) {
                  var4 = false;
                  break;
               }
            }

            if (var4) {
               return var3;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   void seal() throws IOException {
      try {
         for (String var2 : new ArrayList<>(this.C.keySet())) {
            if (!aV(var2)) {
               this.loadClass(var2);
            }
         }
      } catch (LinkageError | ClassNotFoundException var6) {
         throw new IOException("Signed payload contains an unloadable class", var6);
      } finally {
         ba(this.C);
      }
   }

   private static boolean aV(String var0) {
      return var0.startsWith("com.nnpg.alice.mixins.");
   }

   @Override
   public long authorizePayload(Class<?> var1) {
      if (var1 != null && PayloadConstants.aU.equals(var1.getName()) && var1.getClassLoader() == this && this.bs.compareAndSet(true, false)) {
         return this.bH;
      } else {
         throw new LinkageError("ALICE payload runtime authorization failed");
      }
   }

   @Override
   protected Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
      synchronized (this.getClassLoadingLock(var1)) {
         Class var4 = this.findLoadedClass(var1);
         if (var4 == null && aW(var1) && this.C.containsKey(var1)) {
            var4 = this.findClass(var1);
         }

         if (var4 == null) {
            var4 = super.loadClass(var1, false);
         }

         if (var2) {
            this.resolveClass(var4);
         }

         return var4;
      }
   }

   private static boolean aW(String var0) {
      return var0.startsWith("com.nnpg.alice.")
         && !var0.equals("com.nnpg.alice.AliceAddon")
         && !var0.startsWith("com.nnpg.alice.bridge.")
         && !var0.startsWith("com.nnpg.alice.mixins.");
   }

   @Override
   protected Class<?> findClass(String var1) throws ClassNotFoundException {
      byte[] var2 = this.C.remove(var1);
      if (var2 == null) {
         return super.findClass(var1);
      } else {
         Class var3;
         try {
            var3 = this.defineClass(var1, var2, 0, var2.length);
         } finally {
            Arrays.fill(var2, (byte)0);
         }

         return var3;
      }
   }

   @Override
   public InputStream getResourceAsStream(String var1) {
      String var2 = var1 != null && var1.startsWith("/") ? var1.substring(1) : var1;
      if (var2 != null && !var2.isEmpty()) {
         String var3 = aX(var2) ? var2 : aY(var2);
         byte[] var4 = this.be.get(var3);
         if (var4 != null) {
            return new ByteArrayInputStream((byte[])var4.clone());
         }
      }

      return super.getResourceAsStream(var1);
   }

   private static boolean aX(String var0) {
      if (var0 != null && var0.startsWith("META-INF/alice/r/") && var0.endsWith(".bin")) {
         int var1 = "META-INF/alice/r/".length();
         int var2 = var0.length() - ".bin".length();
         if (var2 - var1 != 64) {
            return false;
         } else {
            for (int var3 = var1; var3 < var2; var3++) {
               char var4 = var0.charAt(var3);
               if ((var4 < '0' || var4 > '9') && (var4 < 'a' || var4 > 'f')) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static String aY(String var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance("SHA-256");
         var1.update("ALICE-RSRC-1\u0000".getBytes(StandardCharsets.UTF_8));
         return StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","META-INF/alice/r/",".bin">(
            aZ(var1.digest(var0.getBytes(StandardCharsets.UTF_8)))
         );
      } catch (Exception var2) {
         throw new IllegalStateException("Unable to hash payload resource", var2);
      }
   }

   private static String aZ(byte[] var0) {
      char[] var1 = "0123456789abcdef".toCharArray();
      char[] var2 = new char[var0.length * 2];

      for (int var3 = 0; var3 < var0.length; var3++) {
         int var4 = var0[var3] & 255;
         var2[var3 * 2] = var1[var4 >>> 4];
         var2[var3 * 2 + 1] = var1[var4 & 15];
      }

      return new String(var2);
   }

   private static void ba(Map<String, byte[]> var0) {
      for (byte[] var2 : var0.values()) {
         Arrays.fill(var2, (byte)0);
      }

      var0.clear();
   }

   private record ArchiveContent(Map<String, byte[]> classes, Map<String, byte[]> resources) {
      public static int[] bK;
   }
}
