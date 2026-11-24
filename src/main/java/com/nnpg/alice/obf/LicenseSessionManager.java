package com.nnpg.alice.obf;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.lang.invoke.StringConcatFactory;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.client.gui.hud.ChatHud;
import org.lwjgl.util.freetype.TT_MaxProfile;

final class LicenseSessionManager {
   private static final SecureRandom N = new SecureRandom();
   private static final byte[] aT = new byte[]{65, 76, 73, 67, 69, 69, 49, 0};

   private LicenseSessionManager() {
   }

   static KeyPair ad() throws GeneralSecurityException {
      return KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
   }

   static KeyPair ae() throws GeneralSecurityException {
      return KeyPairGenerator.getInstance("X25519").generateKeyPair();
   }

   static String af() {
      byte[] var0 = new byte[32];
      N.nextBytes(var0);
      return aq(var0);
   }

   static PrivateKey ag(String var0) throws GeneralSecurityException {
      return KeyFactory.getInstance("Ed25519").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(var0)));
   }

   static PublicKey ah(String var0) throws GeneralSecurityException {
      return KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(var0)));
   }

   static byte[] ai(byte[] var0, PrivateKey var1, String var2, String var3) throws GeneralSecurityException {
      short var10000;
      if (var0 != null) {
         var10000 = 10638;
         char var10001 = '\udb02';

         while (true) {
            char var17 = var10001;
            if (var17 == '\udb01') {
               TT_MaxProfile.nmaxPoints(7123545688730270722L);
               throw null;
            }

            if (var17 != '\udb02') {
               break;
            }

            var10001 = '\udb00';
         }
      } else {
         var10000 = 10639;
      }

      while (true) {
         switch (var10000 ^ -973202795) {
            case -973196518:
               var10000 = 10637;
               break;
            case -973196517:
               if (var0.length >= aT.length + 2 + 12 + 16) {
                  ByteBuffer var4 = ByteBuffer.wrap(var0);
                  byte[] var5 = new byte[aT.length];
                  var4.get(var5);
                  if (!MessageDigest.isEqual(var5, aT)) {
                     throw new GeneralSecurityException("Unexpected encrypted payload envelope");
                  }

                  int var6 = Short.toUnsignedInt(var4.getShort());
                  if (var6 >= 32 && var6 <= 256 && var4.remaining() >= var6 + 28) {
                     var10000 = 19048;

                     while (true) {
                        switch (var10000) {
                           case 19048:
                              byte[] var7 = new byte[var6];
                              var4.get(var7);
                              byte[] var8 = new byte[12];
                              var4.get(var8);
                              byte[] var9 = new byte[var4.remaining()];
                              var4.get(var9);
                              Object var10 = null;
                              Object var11 = null;
                              PublicKey var12 = KeyFactory.getInstance("X25519").generatePublic(new X509EncodedKeySpec(var7));
                              KeyAgreement var13 = KeyAgreement.getInstance("X25519");
                              var13.init(var1);
                              var13.doPhase(var12, true);
                              var10 = var13.generateSecret();
                              var11 = aj(
                                 (byte[])var10,
                                 var2.getBytes(StandardCharsets.UTF_8),
                                 StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","ALICE-PAYLOAD-E1\n">(var3)
                                    .getBytes(StandardCharsets.UTF_8)
                              );
                              Cipher var14 = Cipher.getInstance("AES/GCM/NoPadding");
                              var14.init(2, new SecretKeySpec((byte[])var11, "AES"), new GCMParameterSpec(128, var8));
                              var14.updateAAD((var3 + var2).getBytes(StandardCharsets.UTF_8));
                              byte[] var15 = var14.doFinal(var9);
                              Arrays.fill(var5, (byte)0);
                              Arrays.fill(var7, (byte)0);
                              Arrays.fill(var8, (byte)0);
                              Arrays.fill(var9, (byte)0);
                              if (var10 != null) {
                                 Arrays.fill((byte[])var10, (byte)0);
                              }

                              if (var11 != null) {
                                 Arrays.fill((byte[])var11, (byte)0);
                              }

                              return var15;
                           case 19049:
                              var10000 = 19047;
                              break;
                           default:
                              ChatHud.getWidth(0.7537631585854778);
                              throw null;
                        }
                     }
                  }

                  throw new GeneralSecurityException("Invalid payload agreement key");
               }

               throw new GeneralSecurityException("Invalid encrypted payload envelope");
            default:
               throw new GeneralSecurityException("Invalid encrypted payload envelope");
         }
      }
   }

   private static byte[] aj(byte[] var0, byte[] var1, byte[] var2) throws GeneralSecurityException {
      Mac var3 = Mac.getInstance("HmacSHA256");
      var3.init(new SecretKeySpec(var1, "HmacSHA256"));
      byte[] var4 = var3.doFinal(var0);

      byte[] var6;
      try {
         Mac var5 = Mac.getInstance("HmacSHA256");
         var5.init(new SecretKeySpec(var4, "HmacSHA256"));
         var5.update(var2);
         var5.update((byte)1);
         var6 = var5.doFinal();
      } finally {
         Arrays.fill(var4, (byte)0);
      }

      return var6;
   }

   static PublicKey ak() throws GeneralSecurityException {
      return KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(PayloadConstants.o));
   }

   static String al(PrivateKey var0, String var1) throws GeneralSecurityException {
      Signature var2 = Signature.getInstance("Ed25519");
      var2.initSign(var0);
      var2.update(var1.getBytes(StandardCharsets.UTF_8));
      return aq(var2.sign());
   }

   static JsonObject am(String var0) throws GeneralSecurityException {
      if (var0 != null && var0.length() <= 8192) {
         String[] var1 = var0.split("\\.");
         if (var1.length != 3) {
            throw new GeneralSecurityException("Malformed signed token");
         } else {
            JsonObject var2 = JsonParser.parseString(new String(Base64.getUrlDecoder().decode(var1[0]), StandardCharsets.UTF_8)).getAsJsonObject();
            if (var2.has("alg") && "EdDSA".equals(var2.get("alg").getAsString()) && var2.has("typ") && "GLAZED+JWT".equals(var2.get("typ").getAsString())) {
               Signature var3 = Signature.getInstance("Ed25519");
               var3.initVerify(ak());
               var3.update((var1[0] + var1[1]).getBytes(StandardCharsets.US_ASCII));
               int var10000 = var3.verify(Base64.getUrlDecoder().decode(var1[2])) ? 'ａ' : '｀';

               while (true) {
                  switch (var10000) {
                     case 65344:
                        throw new GeneralSecurityException("Invalid token signature");
                     case 65345:
                        var10000 = 65343;
                        break;
                     default:
                        return JsonParser.parseString(new String(Base64.getUrlDecoder().decode(var1[1]), StandardCharsets.UTF_8)).getAsJsonObject();
                  }
               }
            } else {
               throw new GeneralSecurityException("Unexpected signed token header");
            }
         }
      } else {
         throw new GeneralSecurityException("Invalid signed token");
      }
   }

   static String an(byte[] var0) throws GeneralSecurityException {
      return aq(MessageDigest.getInstance("SHA-256").digest(var0));
   }

   static String ao(String var0) throws GeneralSecurityException {
      return an(var0.getBytes(StandardCharsets.UTF_8));
   }

   static String ap(byte[] var0) {
      return Base64.getEncoder().encodeToString(var0);
   }

   private static String aq(byte[] var0) {
      return Base64.getUrlEncoder().withoutPadding().encodeToString(var0);
   }
}
