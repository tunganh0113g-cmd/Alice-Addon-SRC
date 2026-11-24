package com.nnpg.alice.obf;

import com.google.common.base.Ascii;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.nnpg.alice.bridge.VelocityBridge;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.util.Chars;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GL45C;
import org.lwjgl.system.JNI;
import org.lwjgl.system.macosx.CGPoint;

final class LicenseStateCodec {
   private static final int W = 2;
   private static final Gson p = new GsonBuilder().disableHtmlEscaping().create();
   private static final Set<PosixFilePermission> aC = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
   private final Path i;
   private final Path Z;
   private final Path aH;
   private final Path Y;
   private final Path g;

   LicenseStateCodec() {
      this(cl(), FabricLoader.getInstance().getGameDir().resolve("config").resolve("glazed-access"));
   }

   LicenseStateCodec(Path var1, Path var2) {
      this.i = var1.toAbsolutePath().normalize();
      this.g = var2.toAbsolutePath().normalize();
      this.Z = this.i.resolve("state.json");
      this.aH = this.i.resolve("payload.cache");
      this.Y = this.i.resolve("store.lock");
   }

   LicenseConfig bZ() throws IOException, GeneralSecurityException {
      Files.createDirectories(this.i);
      this.cw(this.i);
      FileChannel var1 = FileChannel.open(this.Y, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
      FileLock var2 = var1.lock();

      LicenseConfig var3;
      try {
         this.cw(this.Y);
         this.co();
         Files.deleteIfExists(this.aH);
         var3 = this.cb();
      } catch (Throwable var6) {
         int var10000 = var2 != null ? '퍫' : '퍬';

         while (true) {
            switch (var10000 ^ -1419308512) {
               case -1419263669:
                  var2.close();
                  throw var6;
               case -1419263668:
                  var10000 = 54122;
                  break;
               default:
                  throw var6;
            }
         }
      }

      short var10;
      if (var2 != null) {
         var2.close();
      } else {
         while (var10 == 25273) {
            var10 = 25271;
         }
      }

      if (var1 != null) {
         var1.close();
      }

      return var3;
   }

   LicenseConfig ca(LicenseConfig var1) throws IOException, GeneralSecurityException {
      Files.createDirectories(this.i);
      this.cw(this.i);
      FileChannel var2 = FileChannel.open(this.Y, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
      FileLock var3 = var2.lock();
      this.cw(this.Y);
      LicenseConfig var4 = this.cb();
      if (!Objects.equals(var4.publicKey, var1.publicKey)) {
         if (var3 != null) {
            var3.close();
         }

         if (var2 != null) {
            var2.close();
         }

         return var4;
      } else {
         KeyPair var5 = LicenseSessionManager.ad();
         LicenseConfig var6 = new LicenseConfig();
         var6.privateKey = LicenseSessionManager.ap(var5.getPrivate().getEncoded());
         var6.publicKey = LicenseSessionManager.ap(var5.getPublic().getEncoded());
         var6.trustedTime = var1.trustedTime;
         this.cq(var6);
         Files.deleteIfExists(this.aH);
         char var11;
         if (var3 != null) {
            var3.close();
         } else {
            while (var11 == '쓛') {
               var11 = '쓙';
            }
         }

         if (var2 != null) {
            var2.close();
         }

         return var6;
      }
   }

   private LicenseConfig cb() throws IOException, GeneralSecurityException {
      if (Files.isRegularFile(this.Z)) {
         try {
            JsonObject var5 = JsonParser.parseString(Files.readString(this.Z)).getAsJsonObject();
            LicenseStateCodec.DecodedState var7 = this.cc(var5);
            LicenseConfig var3 = var7.state();
            this.cs(var3);
            this.cw(this.Z);
            if (var7.rewrite()) {
               this.cq(var3);
            }

            return var3;
         } catch (GeneralSecurityException | RuntimeException var4) {
            Path var2 = this.i.resolve(System.currentTimeMillis() + "");
            Files.move(this.Z, var2, StandardCopyOption.REPLACE_EXISTING);
            this.cw(var2);
         }
      }

      KeyPair var1 = LicenseSessionManager.ad();
      LicenseConfig var6 = new LicenseConfig();
      var6.privateKey = LicenseSessionManager.ap(var1.getPrivate().getEncoded());
      var6.publicKey = LicenseSessionManager.ap(var1.getPublic().getEncoded());
      this.cq(var6);
      return var6;
   }

   private LicenseStateCodec.DecodedState cc(JsonObject var1) throws GeneralSecurityException, IOException {
      int var22;
      label128: {
         if (var1.has("version")) {
            var22 = !var1.has("protection") ? 30333 : 30332;

            while (true) {
               int var12 = var22;
               if (var12 == 30332) {
                  if (var1.has("blob")) {
                     var22 = 1;
                     break label128;
                  }
                  break;
               }

               if (var12 != 30333) {
                  break;
               }

               var22 = 30331;
            }
         }

         var22 = 0;
      }

      boolean var2 = (boolean)var22;
      var22 = !var2 ? '齅' : '齄';

      label115:
      while (true) {
         int var11 = var22;
         if (var11 == 40772) {
            var26 = (LicenseStateCodec.StateEnvelope)p.fromJson(var1, LicenseStateCodec.StateEnvelope.class);
            break;
         }

         var22 = var11 == 40773 ? '킿' : '킾';

         while (true) {
            switch (var22) {
               case 53438:
                  int var13 = 1483222469;
                  var26 = this.cd(var1);
                  break label115;
               case 53439:
                  var22 = 53437;
                  break;
               default:
                  int var14 = -1813140602;
                  char var25 = '龜';

                  while (true) {
                     char var15 = var25;
                     if (var15 == '龜') {
                        var22 = 40771;
                        continue label115;
                     }

                     if (var15 != '契') {
                        LicenseBootstrapper.bA();
                        return null;
                     }

                     var25 = '龜';
                  }
            }
         }
      }

      LicenseStateCodec.StateEnvelope var3 = var26;
      if (var3 == null) {
         throw new GeneralSecurityException("Unprotected or unrecognised state document");
      } else if (var3.version == 2) {
         if (ck(var3.protection) && var3.blob != null) {
            boolean var4 = var3.J == null;
            if (!var4) {
               byte[] var5 = cr(var3);
               byte[] var6 = Base64.getDecoder().decode(var3.J);
               boolean var7 = MessageDigest.isEqual(var5, var6);
               Arrays.fill(var5, (byte)0);
               if (!var7) {
                  throw new GeneralSecurityException("State envelope MAC mismatch");
               }
            }

            byte[] var19 = Base64.getDecoder().decode(var3.blob);
            char var28 = '龜';

            while (true) {
               switch (var28) {
                  case '句':
                     int var17 = 1186995551;
                     Ascii.toLowerCase('秚');
                     LicenseBootstrapper.bA();
                     return null;
                  case '龜':
                     var28 = '串';
                     break;
                  default:
                     byte[] var20 = ProtectedValueStore.bi(var3.protection, var19);
                     JsonObject var21 = JsonParser.parseString(new String(var20, StandardCharsets.UTF_8)).getAsJsonObject();
                     LicenseStateCodec.DecodedState var8 = this.ce(var21);
                     LicenseStateCodec.DecodedState var29 = new LicenseStateCodec.DecodedState;
                     LicenseConfig var10002 = var8.state();
                     boolean var10003;
                     if (var2 && !var4 && !var8.rewrite()) {
                        var10003 = false;
                     } else {
                        var10003 = true;
                        int var18 = -776212307;
                     }

                     var29./* $VF: Unable to resugar constructor */<init>(var10002, var10003);
                     LicenseStateCodec.DecodedState var9 = var29;
                     Arrays.fill(var20, (byte)0);
                     Arrays.fill(var19, (byte)0);
                     return var9;
               }
            }
         } else {
            throw new GeneralSecurityException("Unsupported protected state envelope");
         }
      } else {
         int var16 = -2083965671;
         char var27 = 'ꖤ';

         while (true) {
            switch (var27) {
               case 'ꖤ':
                  throw new GeneralSecurityException("Unsupported protected state envelope");
               case 'ꖥ':
                  var27 = 'ꖣ';
                  break;
               default:
                  GL45.glGetnMapd(1783654829, -531962397);
                  LicenseBootstrapper.bA();
                  return null;
            }
         }
      }
   }

   private LicenseStateCodec.StateEnvelope cd(JsonObject var1) throws GeneralSecurityException {
      int var10000 = var1.size() != 3 ? '鰁' : '鰀';

      while (true) {
         switch (var10000) {
            case 39936:
               int var10 = 2042993470;
               Integer var2 = null;
               String var3 = null;
               String var4 = null;

               label143:
               for (JsonElement var6 : var1.asMap().values()) {
                  if (!var6.isJsonPrimitive()) {
                     return null;
                  }

                  if (var6.getAsJsonPrimitive().isNumber()) {
                     int var16 = var6.getAsInt();
                     var10000 = var16 == 2 ? 8981 : 8980;

                     while (true) {
                        int var11 = var10000;
                        if (var11 == 8980) {
                           break;
                        }

                        if (var11 != 8981) {
                           if (var2 == null) {
                              var2 = var16;
                              char var21 = '쫅';

                              while (true) {
                                 switch (var21) {
                                    case '쫅':
                                       continue label143;
                                    case '쫆':
                                       var21 = '쫄';
                                       break;
                                    default:
                                       JNI.invokePPPPP(
                                          3896868992051375364L, -3745564531635330375L, 4357890929418189394L, -3146943248355708553L, -6055641958797501931L
                                       );
                                       LicenseBootstrapper.bA();
                                       return null;
                                 }
                              }
                           }
                           break;
                        }

                        var10000 = 8979;
                     }

                     return null;
                  } else {
                     char var17;
                     if (!var6.getAsJsonPrimitive().isString()) {
                        var17 = '뢜';
                        int var12 = -18853308;
                     } else {
                        var17 = '뢝';
                     }

                     while (true) {
                        char var9 = var17;
                        if (var9 == '뢜') {
                           return null;
                        }

                        if (var9 != '뢝') {
                           String var7 = var6.getAsString();
                           if (!ck(var7)) {
                              if (var4 != null) {
                                 return null;
                              }

                              var4 = var7;
                           } else {
                              if (var3 != null) {
                                 return null;
                              }

                              var3 = var7;
                              short var18 = 30713;

                              label134:
                              while (true) {
                                 switch (var18) {
                                    case 30712:
                                       JNI.invokePP(-7961542417947876809L, 1480459419, 1046709387, -5163864390387401064L);
                                       LicenseBootstrapper.bA();
                                       return null;
                                    case 30713:
                                       var18 = 30711;
                                       break;
                                    default:
                                       break label134;
                                 }
                              }
                           }

                           short var19 = 22095;

                           while (true) {
                              switch (var19) {
                                 case 22094:
                                    GL45C.glGetVertexArrayIndexed64i(1142558579, 1341394484, 1027588942);
                                    LicenseBootstrapper.bA();
                                    return null;
                                 case 22095:
                                    var19 = 22093;
                                    break;
                                 default:
                                    continue label143;
                              }
                           }
                        }

                        var17 = '뢛';
                     }
                  }
               }

               if (var2 != null && var3 != null && var4 != null) {
                  var10000 = Base64.getDecoder().decode(var4).length < 32 ? 2984 : 2983;

                  while (true) {
                     switch (var10000) {
                        case 2983:
                           char var23 = 'ꔰ';

                           while (true) {
                              char var14 = var23;
                              if (var14 == 'ꔯ') {
                                 LicenseBootstrapper.bA();
                                 return null;
                              }

                              if (var14 != 'ꔰ') {
                                 LicenseStateCodec.StateEnvelope var15 = new LicenseStateCodec.StateEnvelope();
                                 var15.version = var2;
                                 var15.protection = var3;
                                 var15.blob = var4;
                                 return var15;
                              }

                              var23 = 'ꔮ';
                           }
                        case 2984:
                           var10000 = 2982;
                           break;
                        default:
                           return null;
                     }
                  }
               }

               return null;
            case 39937:
               var10000 = 39935;
               break;
            default:
               return null;
         }
      }
   }

   private LicenseStateCodec.DecodedState ce(JsonObject var1) throws GeneralSecurityException {
      if (var1.has("privateKey")) {
         if (var1.has("publicKey")) {
            return new LicenseStateCodec.DecodedState((LicenseConfig)p.fromJson(var1, LicenseConfig.class), false);
         } else {
            char var10000 = '컈';

            while (true) {
               switch (var10000) {
                  case '컇':
                     LicenseBootstrapper.bA();
                     return null;
                  case '컈':
                     var10000 = '컆';
                     break;
                  default:
                     return new LicenseStateCodec.DecodedState(this.cf(var1), true);
               }
            }
         }
      } else {
         return new LicenseStateCodec.DecodedState(this.cf(var1), true);
      }
   }

   private LicenseConfig cf(JsonObject var1) throws GeneralSecurityException {
      LicenseConfig var2 = new LicenseConfig();
      ArrayList var3 = new ArrayList();
      JsonObject var4 = null;
      JsonObject var5 = null;
      JsonObject var6 = null;

      label229:
      for (Entry var8 : var1.entrySet()) {
         JsonElement var9 = (JsonElement)var8.getValue();
         if (var9.isJsonPrimitive()) {
            if (!var9.getAsJsonPrimitive().isNumber()) {
               if (var9.getAsJsonPrimitive().isString()) {
                  int var17 = 270547415;
                  String var10 = var9.getAsString();
                  JsonObject var11 = this.ch(var10);
                  if (var11 != null && var11.has("aud")) {
                     byte var13;
                     String var12 = var11.get("aud").getAsString();
                     var13 = -1;
                     label208:
                     switch (var12.hashCode()) {
                        case -1103791938:
                           if (var12.equals("glazed-payload")) {
                              var13 = 1;
                           }
                           break;
                        case 427525488:
                           if (!var12.equals("glazed-addon")) {
                              break;
                           }

                           var13 = 0;
                           short var28 = 2436;

                           while (true) {
                              switch (var28) {
                                 case 2436:
                                    break label208;
                                 case 2437:
                                    var28 = 2435;
                                    break;
                                 default:
                                    GL45.glGetTextureParameterIui(540469517, -387608426);
                                    LicenseBootstrapper.bA();
                                    return null;
                              }
                           }
                        case 437711242:
                           if (var12.equals("glazed-lease")) {
                              var13 = 2;
                           }
                     }

                     if (var13 == 0) {
                        var2.accessToken = var10;
                        var4 = var11;
                        short var30 = 9260;

                        label190:
                        while (true) {
                           switch (var30) {
                              case 9260:
                                 int var19 = -172547581;
                                 break label190;
                              case 9261:
                                 var30 = 9259;
                                 break;
                              default:
                                 LicenseBootstrapper.bA();
                                 return null;
                           }
                        }
                     } else if (var13 == 1) {
                        var2.manifestToken = var10;
                        var5 = var11;
                     } else if (var13 == 2) {
                        var2.leaseToken = var10;
                        var6 = var11;
                        char var29 = '뮇';

                        label198:
                        while (true) {
                           switch (var29) {
                              case '뮆':
                                 int var20 = -1523157488;
                                 VelocityBridge.cO();
                                 LicenseBootstrapper.bA();
                                 return null;
                              case '뮇':
                                 var29 = '뮅';
                                 break;
                              default:
                                 break label198;
                           }
                        }
                     }

                     char var31 = '좗';

                     while (true) {
                        switch (var31) {
                           case '좖':
                              Chars.getUpperCaseHex(421353759);
                              LicenseBootstrapper.bA();
                              return null;
                           case '좗':
                              var31 = '좕';
                              break;
                           default:
                              continue label229;
                        }
                     }
                  } else {
                     this.cg(var2, var10);
                     char var10000 = '螟';

                     while (true) {
                        switch (var10000) {
                           case '螟':
                              continue label229;
                           case '螠':
                              var10000 = '螞';
                              break;
                           default:
                              CGPoint.nx(4744084382446403796L);
                              LicenseBootstrapper.bA();
                              return null;
                        }
                     }
                  }
               } else {
                  int var16 = -1265048839;
               }
            } else {
               var3.add(var9.getAsLong());
            }
         }
      }

      if (var4 != null) {
         var2.clientBuild = ci(var4, "loader");
         char var38;
         if (var2.clientBuild == null) {
            var2.clientBuild = ci(var4, "build");
         } else {
            while (var38 == '띃') {
               var38 = '띁';
            }
         }

         var2.expiresAt = cj(var4, "exp");
         var2.graceExpiresAt = cj(var4, "graceExp");
      }

      if (var5 != null) {
         var2.payloadRevision = ci(var5, "rev");
      }

      if (var6 != null) {
         if (var2.payloadRevision == null) {
            var2.payloadRevision = ci(var6, "rev");
         }

         var2.leaseExpiresAt = cj(var6, "exp");
      }

      long var24 = Math.max(cj(var4, "iat"), Math.max(cj(var5, "iat"), cj(var6, "iat")));
      var2.trustedTime = var24;

      label144:
      for (long var26 : var3) {
         if (var26 != var2.expiresAt) {
            int var32 = var26 != var2.graceExpiresAt ? '죃' : '죄';

            label133:
            while (true) {
               int var14 = var32;
               if (var14 == 51395) {
                  var32 = var26 != var2.leaseExpiresAt ? 27831 : 27832;

                  while (true) {
                     switch (var32 ^ 379453726) {
                        case 379481510:
                           var32 = 27830;
                           break;
                        case 379481513:
                           if (var26 >= var24 && (var2.graceExpiresAt == 0L || var26 <= var2.graceExpiresAt)) {
                              var2.trustedTime = Math.max(var2.trustedTime, var26);
                           }
                        default:
                           break label133;
                     }
                  }
               }

               if (var14 != 51396) {
                  int var23 = 726211250;
                  break;
               }

               var32 = 51394;
            }
         }

         short var34 = 19320;

         while (true) {
            switch (var34) {
               case 19319:
                  JNI.callSPSS((short)3247, -5905892748765998294L, (short)-4883, -7532679773218126313L);
                  LicenseBootstrapper.bA();
                  return null;
               case 19320:
                  var34 = 19318;
                  break;
               default:
                  continue label144;
            }
         }
      }

      short var35 = 2973;

      while (true) {
         short var22 = var35;
         if (var22 == 2973) {
            return var2;
         }

         if (var22 != 2974) {
            LicenseBootstrapper.bA();
            return null;
         }

         var35 = 2972;
      }
   }

   private void cg(LicenseConfig var1, String var2) {
      byte[] var3 = Base64.getDecoder().decode(var2);
      int var6 = 882104348;
      if (var1.privateKey == null) {
         LicenseSessionManager.ag(var2);
         var1.privateKey = var2;
         Arrays.fill(var3, (byte)0);
      } else {
         if (var1.publicKey == null) {
            LicenseSessionManager.ah(var2);
            var1.publicKey = var2;
         }

         Arrays.fill(var3, (byte)0);
      }
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   private JsonObject ch(String var1) {
      int var10000 = var1 != null ? '蓡' : '蓢';

      while (true) {
         switch (var10000) {
            case 34017:
               if (var1.length() <= 8192) {
                  String[] var2 = var1.split("\\.");
                  if (var2.length != 3) {
                     return null;
                  }

                  char var5 = '뮹';
                  return JsonParser.parseString(new String(Base64.getUrlDecoder().decode(var2[1]), StandardCharsets.UTF_8)).getAsJsonObject();
               }

               return null;
            case 34018:
               var10000 = 34016;
               break;
            default:
               char var10;
               if (var10 != '\ue507') {
                  return null;
               }

               var10 = '\ue505';
               var10000 = 34018;
         }
      }
   }

   private static String ci(JsonObject var0, String var1) {
      short var6;
      if (var0 != null) {
         if (var0.has(var1) && var0.get(var1).isJsonPrimitive()) {
            return var0.get(var1).getAsString();
         }
      } else {
         while (var6 == 23449) {
            var6 = 23447;
         }
      }

      return null;
   }

   private static long cj(JsonObject var0, String var1) {
      if (var0 != null && var0.has(var1) && var0.get(var1).isJsonPrimitive()) {
         try {
            return var0.get(var1).getAsLong();
         } catch (RuntimeException var3) {
            return 0L;
         }
      } else {
         return 0L;
      }
   }

   private static boolean ck(String var0) {
      return "windows-dpapi-current-user-v1".equals(var0) || "local-device-key-v1".equals(var0);
   }

   static Path cl() {
      return cm(System.getProperty("os.name", ""), System.getProperty("user.home", ""), System.getenv("LOCALAPPDATA"), System.getenv("XDG_DATA_HOME"));
   }

   static Path cm(String var0, String var1, String var2, String var3) {
      if (var1 != null && !var1.isBlank()) {
         String var10000;
         if (var0 == null) {
            var10000 = "";
            int var7 = -222444073;
         } else {
            var10000 = var0.toLowerCase(Locale.ROOT);
         }

         String var4 = var10000;
         Path var5 = Path.of(var1);
         if (var4.contains("windows")) {
            Path var11 = cn(var2);
            if (var11 == null) {
               var11 = var5.resolve("AppData").resolve("Local");
            }

            return var11.resolve("ALICE").resolve("access");
         } else {
            short var13;
            if (var4.contains("mac")) {
               var13 = 26651;
            } else {
               int var8 = 1424432014;
               var13 = 26652;
            }

            while (true) {
               int var9 = var13 ^ -925942327;
               if (var9 == -925936174) {
                  return var5.resolve("Library").resolve("Application Support").resolve("ALICE").resolve("access");
               }

               if (var9 != -925936171) {
                  Path var6 = cn(var3);
                  short var14;
                  if (var6 == null) {
                     var14 = 808;
                     short var10001 = 13527;

                     label60:
                     while (true) {
                        switch (var10001) {
                           case 13526:
                              LicenseBootstrapper.bA();
                              return null;
                           case 13527:
                              var10001 = 13525;
                              break;
                           default:
                              break label60;
                        }
                     }
                  } else {
                     char var19;
                     while (var19 == '鮀') {
                        var19 = '魾';
                     }

                     var14 = 809;
                  }

                  while (true) {
                     switch (var14) {
                        case 808:
                           var6 = var5.resolve(".local").resolve("share");
                           return var6.resolve("alice").resolve("access");
                        case 809:
                           var14 = 807;
                           break;
                        default:
                           return var6.resolve("alice").resolve("access");
                     }
                  }
               }

               var13 = 26650;
            }
         }
      } else {
         throw new IllegalStateException("The operating system user directory is unavailable");
      }
   }

   private static Path cn(String var0) {
      if (var0 != null && !var0.isBlank()) {
         Path var1 = Path.of(var0);
         int var10000 = !var1.isAbsolute() ? 6823 : 6822;

         while (true) {
            int var2 = var10000;
            if (var2 == 6822) {
               var3 = var1;
               break;
            }

            if (var2 != 6823) {
               var3 = null;
               break;
            }

            var10000 = 6821;
         }

         return var3;
      } else {
         return null;
      }
   }

   private void co() throws IOException {
      int var10000 = !this.i.equals(this.g) ? 29508 : 29507;

      while (true) {
         char var4;
         switch (var10000) {
            case 29507:
               return;
            case 29508:
               var10000 = 29506;
               continue;
            default:
               if (!Files.isRegularFile(this.Z)) {
                  int var3 = -597160621;
                  var4 = '\ueefa';
               } else {
                  var4 = '\ueef9';
               }
         }

         while (true) {
            switch (var4) {
               case '\ueef9':
                  return;
               case '\ueefa':
                  var4 = '\ueef8';
                  break;
               default:
                  Path var1 = this.g.resolve("state.json");
                  if (!Files.isRegularFile(var1)) {
                     return;
                  }

                  Path var2 = this.g.resolve("payload.cache");
                  this.cu(var1, this.Z);
                  this.cw(this.Z);
                  Files.deleteIfExists(var2);
                  Files.deleteIfExists(var1);
                  return;
            }
         }
      }
   }

   void cp(LicenseConfig var1) throws IOException, GeneralSecurityException {
      Files.createDirectories(this.i);
      this.cw(this.i);
      FileChannel var2 = FileChannel.open(this.Y, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
      FileLock var3 = var2.lock();
      this.cw(this.Y);
      this.cq(var1);
      if (var3 != null) {
         var3.close();
      }

      if (var2 != null) {
         var2.close();
      }
   }

   private void cq(LicenseConfig var1) throws IOException, GeneralSecurityException {
      byte[] var2 = p.toJson(var1).getBytes(StandardCharsets.UTF_8);
      ProtectedValueStore.ProtectedValue var3 = ProtectedValueStore.protect(var2);
      Arrays.fill(var2, (byte)0);
      char var10000 = 'ꈞ';

      while (true) {
         switch (var10000) {
            case 'ꈞ':
               LicenseStateCodec.StateEnvelope var4 = new LicenseStateCodec.StateEnvelope();
               var4.version = 2;
               var4.protection = var3.scheme();
               var4.blob = Base64.getEncoder().encodeToString(var3.value());
               Arrays.fill(var3.value(), (byte)0);
               var4.J = Base64.getEncoder().encodeToString(cr(var4));
               this.ct(this.Z, p.toJson(var4).getBytes(StandardCharsets.UTF_8));
               this.cw(this.Z);
               return;
            case 'ꈟ':
               var10000 = 'ꈝ';
               break;
            default:
               int var5 = 499032599;
               GL11.glGetMapf(1308862816, -1911969098);
               LicenseBootstrapper.bA();
               return;
         }
      }
   }

   private static byte[] cr(LicenseStateCodec.StateEnvelope var0) throws GeneralSecurityException, IOException {
      byte[] var1 = CryptoHelper.aD();

      byte[] var3;
      try {
         Mac var2 = Mac.getInstance("HmacSHA256");
         var2.init(new SecretKeySpec(var1, "HmacSHA256"));
         var2.update((var0.version + var0.protection + var0.blob).getBytes(StandardCharsets.UTF_8));
         var3 = var2.doFinal();
      } finally {
         Arrays.fill(var1, (byte)0);
      }

      return var3;
   }

   private void cs(LicenseConfig var1) throws GeneralSecurityException {
      if (var1 != null) {
         if (var1.privateKey != null) {
            char var10000;
            if (var1.publicKey == null) {
               var10000 = '뛟';
            } else {
               var10000 = '뛠';
               short var10001 = 32454;

               label44:
               while (true) {
                  switch (var10001) {
                     case 32453:
                        LicenseBootstrapper.bA();
                        return;
                     case 32454:
                        var10001 = 32452;
                        break;
                     default:
                        break label44;
                  }
               }
            }

            label38:
            while (true) {
               char var3 = var10000;
               if (var3 == '뛟') {
                  break;
               }

               if (var3 != '뛠') {
                  LicenseSessionManager.ag(var1.privateKey);
                  LicenseSessionManager.ah(var1.publicKey);
                  return;
               }

               short var4 = 157;

               while (true) {
                  switch (var4) {
                     case 156:
                        JNI.invokePP(-1689558497981687070L, 0.472369525128169, -48127406841779355L);
                        LicenseBootstrapper.bA();
                        return;
                     case 157:
                        var4 = 155;
                        break;
                     default:
                        var10000 = '뛞';
                        continue label38;
                  }
               }
            }
         }
      } else {
         char var2 = 'ꑢ';
      }

      throw new GeneralSecurityException("Incomplete installation identity");
   }

   private void ct(Path var1, byte[] var2) throws IOException {
      Files.createDirectories(var1.getParent());
      Path var3 = Files.createTempFile(var1.getParent(), var1.getFileName() + "", ".tmp");
      Files.write(var3, var2, StandardOpenOption.TRUNCATE_EXISTING);
      this.cw(var3);
      this.cv(var3, var1);
      Files.deleteIfExists(var3);
   }

   private void cu(Path var1, Path var2) throws IOException {
      Files.createDirectories(var2.getParent());
      Path var3 = Files.createTempFile(var2.getParent(), var2.getFileName() + "", ".tmp");
      Files.copy(var1, var3, StandardCopyOption.REPLACE_EXISTING);
      this.cw(var3);
      this.cv(var3, var2);
      Files.deleteIfExists(var3);
   }

   private void cv(Path var1, Path var2) throws IOException {
      Files.move(var1, var2, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
   }

   private void cw(Path var1) throws IOException {
      int var10001 = Files.isDirectory(var1) ? '뙶' : '뙷';

      while (true) {
         int var5 = var10001;
         if (var5 == 46710) {
            var6 = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE);
            break;
         }

         if (var5 != 46711) {
            var6 = aC;
            break;
         }

         var10001 = 46709;
      }

      Files.setPosixFilePermissions(var1, (Set<PosixFilePermission>)var6);
   }

   private record DecodedState(LicenseConfig state, boolean rewrite) {
      public static int[] bd;
   }

   private static final class StateEnvelope {
      @SerializedName("version")
      int version;
      @SerializedName("protection")
      String protection;
      @SerializedName("blob")
      String blob;
      @SerializedName("mac")
      String J;
      public static int[] I;
   }
}
