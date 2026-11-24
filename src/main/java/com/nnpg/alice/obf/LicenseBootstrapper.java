package com.nnpg.alice.obf;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nnpg.alice.bridge.VelocityBridge;
import java.io.IOException;
import java.lang.invoke.StringConcatFactory;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Util;
import net.minecraft.util.math.Easing;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.JNI;
import org.lwjgl.util.freetype.TT_MaxProfile;

public final class LicenseBootstrapper {
   private static final AtomicBoolean aP = new AtomicBoolean();
   private static final AtomicBoolean b = new AtomicBoolean();
   private static final AtomicBoolean c = new AtomicBoolean();

   private LicenseBootstrapper() {
   }

   public static void bA() {
      if (aP.compareAndSet(false, true)) {
         Thread.ofVirtual().name("alice-access").start(LicenseBootstrapper::bB);
      }
   }

   private static void bB() {
      LicenseBootstrapper.FailureState var0 = new LicenseBootstrapper.FailureState();

      try {
         var0.br("ALC-E101");
         ByteBufferHolder.bq();
         LicenseStateCodec var1 = new LicenseStateCodec();
         var0.br("ALC-E123");
         LicenseApiClient var2 = new LicenseApiClient();
         var0.br("ALC-E112");
         LicenseConfig var3 = var1.bZ();
         if (!bD(var1, var2, var3, var0)) {
            var0.br("ALC-E201");
            String var4 = LicenseSessionManager.af();
            String var5 = LicenseSessionManager.ao(var4);
            LicenseApiClient.Challenge var6 = var2.y(var3, var5);
            char var10000 = '\ude95';

            while (true) {
               switch (var10000) {
                  case '\ude94':
                     bA();
                     return;
                  case '\ude95':
                     var10000 = '\ude93';
                     break;
                  default:
                     if (var6.verificationRequired()) {
                        bC(var6.verificationUrl(), var6.deviceFingerprint());
                        var0.br("ALC-E212");
                        bG(var2, var6);
                     }

                     var0.br("ALC-E223");
                     LicenseApiClient.Grant var7 = var2.B(var6, var4);
                     var3.accessToken = var7.accessToken();
                     var3.manifestToken = var7.manifestToken();
                     var3.leaseToken = var7.leaseToken();
                     var3.clientBuild = PayloadConstants.d;
                     var3.payloadRevision = var7.payloadRevision();
                     var3.expiresAt = var7.expiresAt().getEpochSecond();
                     var3.graceExpiresAt = var7.graceExpiresAt().getEpochSecond();
                     var3.leaseExpiresAt = var7.leaseExpiresAt().getEpochSecond();
                     var3.trustedTime = var7.serverTime().getEpochSecond();
                     var0.br("ALC-E234");
                     LicenseBootstrapper.VerifiedGrant var8 = bH(var3, var2.F());
                     var0.br("ALC-E245");
                     byte[] var9 = var2.D(var3);
                     var0.br("ALC-E256");
                     bJ(var9, var8.manifest(), var8.watermarks());
                     var0.br("ALC-E267");
                     var1.cp(var3);
                     var0.br("ALC-E278");
                     bM(var9, var8.watermarks(), var8.seed());
                     return;
               }
            }
         }
      } catch (LinkageError | Exception var10) {
         if (var10 instanceof InterruptedException) {
            Thread.currentThread().interrupt();
         }

         bE(var0.bs());
      }
   }

   private static void bC(String var0, String var1) {
      int var10000 = PayloadConstants.Y() ? 29220 : 29219;

      while (true) {
         switch (var10000) {
            case 29219:
               Util.getOperatingSystem().open(var0);
               return;
            case 29220:
               var10000 = 29218;
               break;
            default:
               if (c.compareAndSet(false, true)) {
                  Thread.ofVirtual().name("alice-mobile-verification-ui").start(() -> {
                     long var2 = System.nanoTime() + TimeUnit.SECONDS.toNanos(120L);

                     while (System.nanoTime() < var2) {
                        MinecraftClient var4 = MinecraftClient.getInstance();
                        CompletableFuture var5 = new CompletableFuture();
                        var4.execute(() -> {
                           Screen var4x = var4.currentScreen;
                           if (var4x instanceof TitleScreen && var4.getOverlay() == null) {
                              var4.setScreen(new LicenseScreen(var0, var1, var4x));
                              var5.complete(true);
                           } else {
                              var5.complete(false);
                           }
                        });
                        if ((Boolean)var5.get(5L, TimeUnit.SECONDS)) {
                           c.set(false);
                           return;
                        }

                        Thread.sleep(250L);
                     }

                     bE("ALC-E289");
                     c.set(false);
                     char var10000x = 'ꦥ';

                     while (true) {
                        switch (var10000x) {
                           case 'ꦤ':
                              var10000x = '\ueeee';

                              while (true) {
                                 char var7 = var10000x;
                                 if (var7 == '\ueeee') {
                                    TT_MaxProfile.nmaxInstructionDefs(-3800676668600373130L);
                                    bA();
                                    return;
                                 }

                                 if (var7 != '\ueeef') {
                                    bA();
                                    return;
                                 }

                                 var10000x = '\ueeed';
                              }
                           case 'ꦥ':
                              var10000x = 'ꦣ';
                              break;
                           default:
                              int var8 = -1796789386;
                              return;
                        }
                     }
                  });
                  return;
               }

               return;
         }
      }
   }

   private static boolean bD(LicenseStateCodec var0, LicenseApiClient var1, LicenseConfig var2, LicenseBootstrapper.FailureState var3) throws Exception {
      if (PayloadConstants.d.equals(var2.clientBuild) && var2.ac()) {
         var3.br("ALC-E145");
         LicenseBootstrapper.VerifiedGrant var4 = bH(var2, var1.F());
         long var5 = Math.max(Instant.now().getEpochSecond(), var2.trustedTime);
         long var7 = var4.access().get("exp").getAsLong();
         if (var5 >= var7) {
            return false;
         } else {
            var3.br("ALC-E134");
            LicenseApiClient.Session var9 = var1.C(var2);
            bF(var2, var9);
            var3.br("ALC-E145");
            LicenseBootstrapper.VerifiedGrant var10 = bH(var2, var1.F());
            var3.br("ALC-E156");
            byte[] var11 = var1.D(var2);
            var3.br("ALC-E167");
            bJ(var11, var10.manifest(), var10.watermarks());
            var3.br("ALC-E178");
            var0.cp(var2);
            var3.br("ALC-E189");
            bM(var11, var10.watermarks(), var10.seed());
            return true;
         }
      } else {
         return false;
      }
   }

   private static void bE(String var0) {
      byte var3 = -1;
      int var5 = var0.hashCode() ^ 1511331230;
      if (var5 == -748553699) {
         if (var0.equals("ALC-E289")) {
            var3 = 17;
         }
      } else if (var5 == -748553571) {
         if (var0.equals("ALC-E245")) {
            var3 = 13;
         }
      } else if (var5 != -748553539) {
         if (var5 != -748553507) {
            if (var5 == -748553475) {
               if (var0.equals("ALC-E278")) {
                  var3 = 16;
               }
            } else if (var5 != -748553443) {
               if (var5 == -748553411) {
                  if (var0.equals("ALC-E212")) {
                     var3 = 10;
                  }
               } else if (var5 != -748553379) {
                  if (var5 == -748553347) {
                     if (var0.equals("ALC-E234")) {
                        var3 = 12;
                     }
                  } else if (var5 != -748552676) {
                     if (var5 == -748552644) {
                        if (var0.equals("ALC-E178")) {
                           var3 = 7;
                        }
                     } else if (var5 != -748552612) {
                        if (var5 != -748552548) {
                           if (var5 != -748552516) {
                              if (var5 != -748552484) {
                                 if (var5 != -748552452) {
                                    if (var5 != -748552356) {
                                       if (var5 == -748552324 && var0.equals("ALC-E112")) {
                                          var3 = 1;
                                       }
                                    } else {
                                       char var13;
                                       if (!var0.equals("ALC-E101")) {
                                          int var6 = -1895737095;
                                          var13 = '쯄';
                                       } else {
                                          var13 = '쯃';
                                       }

                                       while (true) {
                                          char var4 = var13;
                                          if (var4 == '쯃') {
                                             var3 = 0;
                                             break;
                                          }

                                          if (var4 != '쯄') {
                                             break;
                                          }

                                          var13 = '쯂';
                                       }
                                    }
                                 } else if (var0.equals("ALC-E156")) {
                                    var3 = 5;
                                 }
                              } else if (var0.equals("ALC-E145")) {
                                 var3 = 4;
                              }
                           } else if (var0.equals("ALC-E134")) {
                              var3 = 3;
                           }
                        } else if (var0.equals("ALC-E123")) {
                           var3 = 2;
                        }
                     } else if (var0.equals("ALC-E189")) {
                        var3 = 8;
                     }
                  } else if (var0.equals("ALC-E167")) {
                     var3 = 6;
                  }
               } else {
                  char var16;
                  if (!var0.equals("ALC-E223")) {
                     while (var16 == '앋') {
                        var16 = '앉';
                     }
                  } else {
                     var3 = 11;
                  }
               }
            } else if (var0.equals("ALC-E201")) {
               var3 = 9;
            }
         } else if (var0.equals("ALC-E267")) {
            var3 = 15;
         }
      } else if (var0.equals("ALC-E256")) {
         var3 = 14;
         char var10000 = '\ud88c';

         label194:
         while (true) {
            switch (var10000) {
               case '\ud88c':
                  break label194;
               case '\ud88d':
                  var10000 = '\ud88b';
                  break;
               default:
                  bA();
                  return;
            }
         }
      }

      String var1 = var3 != 0
            && var3 != 1
            && var3 != 2
            && var3 != 3
            && var3 != 4
            && var3 != 5
            && var3 != 6
            && var3 != 7
            && var3 != 8
            && var3 != 9
            && var3 != 10
            && var3 != 11
            && var3 != 12
            && var3 != 13
            && var3 != 14
            && var3 != 15
            && var3 != 16
            && var3 != 17
         ? "ALC-E900"
         : var0;
      System.err.println(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","[ALICE] ">(var1));
   }

   private static void bF(LicenseConfig var0, LicenseApiClient.Session var1) {
      var0.accessToken = var1.accessToken();
      var0.manifestToken = var1.manifestToken();
      var0.leaseToken = var1.leaseToken();
      var0.leaseExpiresAt = var1.leaseExpiresAt().getEpochSecond();
      var0.payloadRevision = var1.payloadRevision();
      var0.trustedTime = Math.max(var0.trustedTime, var1.serverTime().getEpochSecond());
   }

   private static void bG(LicenseApiClient var0, LicenseApiClient.Challenge var1) throws IOException, InterruptedException {
      while (Instant.now().isBefore(var1.expiresAt())) {
         String var2 = var0.A(var1);
         if (!"completed".equals(var2)) {
            if (!"expired".equals(var2) && !"redeemed".equals(var2)) {
               Thread.sleep(PayloadConstants.bi);
               continue;
            }

            throw new IOException("Access challenge is no longer valid");
         }

         return;
      }

      throw new IOException("Access challenge expired");
   }

   private static LicenseBootstrapper.VerifiedGrant bH(LicenseConfig var0, String var1) throws GeneralSecurityException {
      JsonObject var2 = LicenseSessionManager.am(var0.accessToken);
      JsonObject var3 = LicenseSessionManager.am(var0.manifestToken);
      JsonObject var4 = LicenseSessionManager.am(var0.leaseToken);
      long var5 = Math.max(Instant.now().getEpochSecond(), var0.trustedTime);
      String var7 = LicenseSessionManager.an(LicenseSessionManager.ah(var0.publicKey).getEncoded());
      bL(var2, "iss", PayloadConstants.aE);
      bL(var2, "aud", "glazed-addon");
      bL(var2, "pk", var7);
      bL(var2, "build", PayloadConstants.d);
      bL(var2, "loader", PayloadConstants.d);
      bL(var2, "loaderSha256", var1);
      bL(var3, "iss", PayloadConstants.aE);
      bL(var3, "aud", "glazed-payload");
      bL(var3, "path", "/v1/payload/current");
      bL(var3, "pk", var7);
      bL(var3, "loader", PayloadConstants.d);
      bL(var3, "loaderSha256", var1);
      bL(var3, "rev", var0.payloadRevision);
      bL(var4, "iss", PayloadConstants.aE);
      bL(var4, "aud", "glazed-lease");
      bL(var4, "pk", var7);
      bL(var4, "loader", PayloadConstants.d);
      bL(var4, "loaderSha256", var1);
      bL(var4, "rev", var0.payloadRevision);
      if (var2.has("sub")) {
         short var10000;
         if (var2.has("jti")) {
            var10000 = 9364;
            int var8 = 889035324;
         } else {
            var10000 = 9365;
         }

         while (true) {
            switch (var10000 ^ 687205214) {
               case 687198154:
                  char var26;
                  if (!var3.has("sub")) {
                     while (var26 == '튍') {
                        var26 = '튋';
                     }
                  } else if (var3.has("installation") && var3.has("grant") && var4.has("sub")) {
                     var10000 = var4.has("grant") ? '즹' : '즺';

                     while (true) {
                        switch (var10000 ^ -1017148602) {
                           case -1017167108:
                              var10000 = 51640;
                              break;
                           case -1017167105:
                              if (var2.has("runtime")
                                 && var3.has("runtime")
                                 && var4.has("runtime")
                                 && var2.get("sub").getAsString().equals(var3.get("installation").getAsString())
                                 && var2.get("sub").getAsString().equals(var4.get("sub").getAsString())) {
                                 short var14;
                                 if (var2.get("jti").getAsString().equals(var3.get("grant").getAsString())) {
                                    var14 = 25808;
                                 } else {
                                    var14 = 25809;
                                    char var10001 = '\ueadf';

                                    label228:
                                    while (true) {
                                       switch (var10001) {
                                          case '\ueade':
                                             VelocityBridge.cM();
                                             bA();
                                             return null;
                                          case '\ueadf':
                                             var10001 = '\ueadd';
                                             break;
                                          default:
                                             break label228;
                                       }
                                    }
                                 }

                                 while (true) {
                                    switch (var14) {
                                       case 25808:
                                          if (var2.get("jti").getAsString().equals(var4.get("grant").getAsString())
                                             && var2.get("runtime").getAsString().equals(var3.get("runtime").getAsString())
                                             && var2.get("runtime").getAsString().equals(var4.get("runtime").getAsString())) {
                                             if (!var2.has("iat")) {
                                                char var21 = '鐭';

                                                while (true) {
                                                   switch (var21) {
                                                      case '鐭':
                                                         throw new GeneralSecurityException("Signed session is missing expiry claims");
                                                      case '鐮':
                                                         var21 = '鐬';
                                                         break;
                                                      default:
                                                         JNI.callPPPPPPPPPPPPI(
                                                            8481847788445499581L,
                                                            8279106318729157734L,
                                                            6903535598968666088L,
                                                            1678292144001266597L,
                                                            8984898491977684959L,
                                                            2257521527263030092L,
                                                            4760087494966302807L,
                                                            9036269549194944927L,
                                                            735124493740603154L,
                                                            385599835763492703L,
                                                            -1868398413,
                                                            6528191520846139259L,
                                                            335946777054678823L,
                                                            5201220069422118919L
                                                         );
                                                         bA();
                                                         return null;
                                                   }
                                                }
                                             } else if (var2.has("nbf") && var2.has("exp") && var2.has("graceExp") && var3.has("iat")) {
                                                var14 = var3.has("nbf") ? '\uda11' : '\uda10';

                                                while (true) {
                                                   int var10 = var14;
                                                   if (var10 == 55824) {
                                                      break;
                                                   }

                                                   if (var10 != 55825) {
                                                      if (!var3.has("exp") || !var4.has("iat") || !var4.has("nbf")) {
                                                         break;
                                                      }

                                                      var14 = var4.has("exp") ? 18528 : 18527;

                                                      while (true) {
                                                         switch (var14) {
                                                            case 18527:
                                                               throw new GeneralSecurityException("Signed session is missing expiry claims");
                                                            case 18528:
                                                               var14 = 18526;
                                                               break;
                                                            default:
                                                               if (var2.get("exp").getAsLong() == var3.get("exp").getAsLong()
                                                                  && var2.get("nbf").getAsLong() <= var5 + 30L
                                                                  && var2.get("iat").getAsLong() <= var5 + 30L
                                                                  && var3.get("nbf").getAsLong() <= var5 + 30L
                                                                  && var3.get("iat").getAsLong() <= var5 + 30L) {
                                                                  var14 = var4.get("nbf").getAsLong() > var5 + 30L ? 24066 : 24065;

                                                                  while (true) {
                                                                     switch (var14) {
                                                                        case 24065:
                                                                           if (var4.get("iat").getAsLong() > var5 + 30L) {
                                                                              char var18 = '韥';

                                                                              while (true) {
                                                                                 char var11 = var18;
                                                                                 if (var11 == '韤') {
                                                                                    bA();
                                                                                    return null;
                                                                                 }

                                                                                 if (var11 != '韥') {
                                                                                    throw new GeneralSecurityException(
                                                                                       "Signed session has inconsistent time claims"
                                                                                    );
                                                                                 }

                                                                                 var18 = '韣';
                                                                              }
                                                                           } else {
                                                                              var14 = var2.get("graceExp").getAsLong() < var2.get("exp").getAsLong()
                                                                                 ? '꼷'
                                                                                 : '꼶';

                                                                              while (true) {
                                                                                 switch (var14) {
                                                                                    case 44854:
                                                                                       if (var4.get("exp").getAsLong() <= var2.get("graceExp").getAsLong()) {
                                                                                          var14 = var5 < var2.get("graceExp").getAsLong() ? 25749 : 25748;

                                                                                          while (true) {
                                                                                             switch (var14) {
                                                                                                case 25748:
                                                                                                   throw new GeneralSecurityException(
                                                                                                      "Signed payload manifest is too old"
                                                                                                   );
                                                                                                case 25749:
                                                                                                   var14 = 25747;
                                                                                                   break;
                                                                                                default:
                                                                                                   return new LicenseBootstrapper.VerifiedGrant(
                                                                                                      var2, var3, var4, bK(var3), bI(var3)
                                                                                                   );
                                                                                             }
                                                                                          }
                                                                                       }

                                                                                       throw new GeneralSecurityException(
                                                                                          "Signed session has inconsistent time claims"
                                                                                       );
                                                                                    case 44855:
                                                                                       var14 = 44853;
                                                                                       break;
                                                                                    default:
                                                                                       throw new GeneralSecurityException(
                                                                                          "Signed session has inconsistent time claims"
                                                                                       );
                                                                                 }
                                                                              }
                                                                           }
                                                                        case 24066:
                                                                           var14 = 24064;
                                                                           break;
                                                                        default:
                                                                           throw new GeneralSecurityException("Signed session has inconsistent time claims");
                                                                     }
                                                                  }
                                                               }

                                                               throw new GeneralSecurityException("Signed session has inconsistent time claims");
                                                         }
                                                      }
                                                   }

                                                   var14 = 55823;
                                                }
                                             }

                                             throw new GeneralSecurityException("Signed session is missing expiry claims");
                                          }

                                          throw new GeneralSecurityException("Signed session binding does not match");
                                       case 25809:
                                          var14 = 25807;
                                          break;
                                       default:
                                          throw new GeneralSecurityException("Signed session binding does not match");
                                    }
                                 }
                              }

                              throw new GeneralSecurityException("Signed session binding does not match");
                           default:
                              throw new GeneralSecurityException("Signed session binding does not match");
                        }
                     }
                  }

                  throw new GeneralSecurityException("Signed session binding does not match");
               case 687198155:
                  var10000 = 9363;
                  break;
               default:
                  throw new GeneralSecurityException("Signed session binding does not match");
            }
         }
      } else {
         throw new GeneralSecurityException("Signed session binding does not match");
      }
   }

   private static long bI(JsonObject var0) throws GeneralSecurityException {
      if (!var0.has("seed")) {
         return 0L;
      } else {
         char var23;
         while (var23 == '鬰') {
            var23 = '鬮';
         }

         JsonElement var1 = var0.get("seed");
         if (var1.isJsonPrimitive()) {
            if (var1.getAsJsonPrimitive().isString()) {
               byte[] var2;
               try {
                  var2 = Base64.getUrlDecoder().decode(var1.getAsString());
               } catch (IllegalArgumentException var15) {
                  throw new GeneralSecurityException("Signed payload manifest has an invalid seed", var15);
               }

               try {
                  int var10000 = var2.length == 8 ? 'ꄱ' : 'ꄰ';

                  while (true) {
                     int var12 = var10000 ^ -1507688837;
                     if (var12 != -1507664054) {
                        if (var12 == -1507664053) {
                           throw new GeneralSecurityException("Signed payload manifest has an invalid seed");
                        }

                        long var3 = 0L;

                        for (byte var8 : var2) {
                           var3 = var3 << 8 | var8 & 255L;
                        }

                        return var3;
                     }

                     var10000 = 41263;
                  }
               } finally {
                  Arrays.fill(var2, (byte)0);
               }
            }
         } else {
            int var11 = 1561821416;
         }

         throw new GeneralSecurityException("Signed payload manifest has an invalid seed");
      }
   }

   private static void bJ(byte[] var0, JsonObject var1, List<String> var2) throws GeneralSecurityException {
      if (var1.has("size")
         && var1.get("size").getAsLong() == var0.length
         && var1.has("sha256")
         && var1.get("sha256").getAsString().equals(LicenseSessionManager.an(var0))) {
         PayloadClassLoader.verifyPersonalization(var0, var2);
      } else {
         throw new GeneralSecurityException("Signed payload integrity check failed");
      }
   }

   private static List<String> bK(JsonObject var0) throws GeneralSecurityException {
      if (var0.has("watermarks") && var0.get("watermarks").isJsonArray()) {
         ArrayList var1 = new ArrayList(5);

         for (JsonElement var3 : var0.getAsJsonArray("watermarks")) {
            if (!var3.isJsonPrimitive() || !var3.getAsJsonPrimitive().isString()) {
               throw new GeneralSecurityException("Signed payload manifest has invalid bytecode marks");
            }

            String var4 = var3.getAsString();
            if (!var4.matches("[A-Za-z0-9_-]{32}")) {
               throw new GeneralSecurityException("Signed payload manifest has invalid bytecode marks");
            }

            var1.add(var4);
         }

         if (var1.size() == 5) {
            int var5 = 1273261819;
            if (new HashSet(var1).size() == 5) {
               return List.copyOf(var1);
            }
         }

         throw new GeneralSecurityException("Signed payload manifest has invalid bytecode marks");
      } else {
         throw new GeneralSecurityException("Signed payload manifest has no bytecode marks");
      }
   }

   private static void bL(JsonObject var0, String var1, String var2) throws GeneralSecurityException {
      if (var0.has(var1) && var2.equals(var0.get(var1).getAsString())) {
         char var10000 = '\uda86';

         while (true) {
            switch (var10000) {
               case '\uda85':
                  Easing.outElastic(0.066732585F);
                  bA();
                  return;
               case '\uda86':
                  var10000 = '\uda84';
                  break;
               default:
                  return;
            }
         }
      } else {
         throw new GeneralSecurityException(
            StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Unexpected signed claim: ">(var1)
         );
      }
   }

   private static void bM(byte[] var0, List<String> var1, long var2) throws Exception {
      boolean var4 = false;
      ByteBufferHolder.bq();
      var4 = b.compareAndSet(false, true);
      if (!var4) {
         Arrays.fill(var0, (byte)0);
      } else {
         PayloadClassLoader var5 = PayloadClassLoader.fromJar(var0, LicenseBootstrapper.class.getClassLoader(), var1, var2);
         bN(var5, var2);
         Class var6 = var5.loadClass(PayloadConstants.aU);
         Method var7 = var6.getMethod(PayloadConstants.aN);
         bO(var7);
         var5.seal();
         Arrays.fill(var0, (byte)0);
      }
   }

   private static void bN(PayloadClassLoader var0, long var1) throws Exception {
      Class var4 = var0.loadClass(PayloadConstants.aO);
      Method var3 = var4.getMethod(PayloadConstants.ah, long.class);
      CompletableFuture var12 = new CompletableFuture();
      Thread.ofPlatform().daemon(true).priority(Math.max(1, 4)).name("alice-protection-warmup").start(() -> {
         var3.invoke(null, var1);
         var12.complete(null);
      });

      try {
         var12.get(15L, TimeUnit.SECONDS);
      } catch (ExecutionException var11) {
         Throwable var6 = var11.getCause();
         char var10000;
         if (!(var6 instanceof Exception)) {
            var10000 = '뙧';

            while (true) {
               char var10 = var10000;
               if (var10 == '뙧') {
                  var10000 = '앣';
                  break;
               }

               if (var10 != '뙨') {
                  GL11.glGetTexLevelParameterf(-231856490, -1731528230, 818331217);
                  bA();
                  return;
               }

               var10000 = '뙦';
            }
         } else {
            var10000 = '앢';
         }

         label49:
         while (true) {
            char var8 = var10000;
            if (var8 == '앢') {
               Exception var7 = (Exception)var6;
               throw var7;
            }

            if (var8 != '앣') {
               throw new RuntimeException(var6);
            }

            short var14 = 30356;

            while (true) {
               switch (var14) {
                  case 30355:
                     bA();
                     return;
                  case 30356:
                     var14 = 30354;
                     break;
                  default:
                     var10000 = '액';
                     continue label49;
               }
            }
         }
      }

      short var15 = 28059;

      while (true) {
         short var9 = var15;
         if (var9 == 28058) {
            bA();
            return;
         }

         if (var9 != 28059) {
            return;
         }

         var15 = 28057;
      }
   }

   private static void bO(Method var0) throws Exception {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1.isOnThread()) {
         var0.invoke(null);
      } else {
         CompletableFuture var2 = new CompletableFuture();
         var1.execute(() -> {
            var0.invoke(null);
            var2.complete(null);
            short var10000x = 12898;

            while (true) {
               switch (var10000x) {
                  case 12897:
                     bA();
                     return;
                  case 12898:
                     var10000x = 12896;
                     break;
                  default:
                     return;
               }
            }
         });

         try {
            var2.get(30L, TimeUnit.SECONDS);
         } catch (ExecutionException var6) {
            Throwable var4 = var6.getCause();
            if (!(var4 instanceof Exception)) {
               throw new RuntimeException(var4);
            } else {
               short var10000 = 15075;

               while (true) {
                  switch (var10000) {
                     case 15075:
                        Exception var5 = (Exception)var4;
                        throw var5;
                     case 15076:
                        var10000 = 15074;
                        break;
                     default:
                        MathHelper.magnitude(0.18791954621872375, 0.2546195571509414, 0.663748870733558);
                        bA();
                        return;
                  }
               }
            }
         }
      }
   }

   private static final class FailureState {
      private String aw = "ALC-E900";

      void br(String var1) {
         this.aw = var1;
      }

      String bs() {
         return this.aw;
      }
   }

   private record VerifiedGrant(JsonObject access, JsonObject manifest, JsonObject lease, List<String> watermarks, long seed) {
      public static int[] at;
   }
}
