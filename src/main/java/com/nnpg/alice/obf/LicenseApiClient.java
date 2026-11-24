package com.nnpg.alice.obf;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.icu.text.UTF16;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.StringConcatFactory;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.util.freetype.FreeType;
import org.lwjgl.util.freetype.TT_Postscript;

final class LicenseApiClient {
   private static final Gson bq = new Gson();
   private static final int Q = 65536;
   private static final int bx = 33554432;
   private static final ProxySelector bb = new ProxySelector() {
      public static int[] bp;

      @Override
      public List<Proxy> select(URI var1) {
         return List.of(Proxy.NO_PROXY);
      }

      @Override
      public void connectFailed(URI var1, SocketAddress var2, IOException var3) {
      }
   };
   private final URI bu;
   private final String ao;
   private final String L = LicenseSessionManager.af();
   private final HttpClient ag = HttpClient.newBuilder().connectTimeout(PayloadConstants.V).followRedirects(Redirect.NEVER).proxy(bb).build();

   LicenseApiClient() throws IOException, GeneralSecurityException {
      this(PayloadConstants.bC, HardwareFingerprint.aw());
   }

   LicenseApiClient(String var1) {
      this(PayloadConstants.bC, var1);
   }

   LicenseApiClient(URI var1, String var2) {
      if (var1 == null || !var1.isAbsolute()) {
         throw new IllegalArgumentException("Invalid API base URI");
      } else if (var2 != null && var2.matches("[0-9a-f]{64}")) {
         this.bu = var1;
         this.ao = var2;
      } else {
         throw new IllegalArgumentException("Invalid loader SHA-256");
      }
   }

   LicenseApiClient.Challenge y(LicenseConfig var1, String var2) throws IOException, InterruptedException, GeneralSecurityException {
      String var3 = Long.toString(Math.max(Instant.now().getEpochSecond(), var1.trustedTime));
      String var4 = LicenseSessionManager.an(LicenseSessionManager.ah(var1.publicKey).getEncoded());
      String var5 = CryptoHelper.aC();
      String var6 = String.join("\n", "POST", "/v1/challenges", var4, var2, PayloadConstants.d, this.ao, this.L, var3, var5);
      JsonObject var7 = new JsonObject();
      var7.addProperty("installPublicKey", var1.publicKey);
      var7.addProperty("codeChallenge", var2);
      var7.addProperty("loaderVersion", PayloadConstants.d);
      var7.addProperty("loaderSha256", this.ao);
      var7.addProperty("runtimeId", this.L);
      var7.addProperty("timestamp", var3);
      var7.addProperty("machineFingerprint", var5);
      var7.addProperty("signature", LicenseSessionManager.al(LicenseSessionManager.ag(var1.privateKey), var6));
      JsonObject var8 = this.H("/v1/challenges", var7, null);
      boolean var10000;
      if (var8.has("verificationRequired") && !var8.get("verificationRequired").getAsBoolean()) {
         var10000 = 23333;

         label53:
         while (true) {
            short var10 = var10000;
            if (var10 == 23333) {
               var10000 = 17263;

               while (true) {
                  switch (var10000) {
                     case 17262:
                        LicenseBootstrapper.bA();
                        return null;
                     case 17263:
                        var10000 = 17261;
                        break;
                     default:
                        var10000 = 0;
                        break label53;
                  }
               }
            }

            if (var10 != 23334) {
               UTF16.getLeadSurrogate(-177745107);
               LicenseBootstrapper.bA();
               return null;
            }

            var10000 = 23332;
         }
      } else {
         var10000 = 1;
      }

      boolean var9 = (boolean)var10000;
      LicenseApiClient.Challenge var14 = new LicenseApiClient.Challenge;
      String var10002 = var8.get("challengeId").getAsString();
      String var10003 = var9 ? this.L(var8) : null;
      String var10004 = var8.get("pollToken").getAsString();
      Instant var10005 = Instant.parse(var8.get("expiresAt").getAsString());
      int var10007 = var8.has("deviceFingerprint") ? 4801 : 4800;

      label40:
      while (true) {
         switch (var10007) {
            case 4801:
               var10007 = 4799;
               break;
            default:
               if (var8.get("deviceFingerprint").isJsonPrimitive()) {
                  var15 = var8.get("deviceFingerprint").getAsString();
                  int var11 = -626168132;
                  break label40;
               }
            case 4800:
               var15 = null;
               break label40;
         }
      }

      var14./* $VF: Unable to resugar constructor */<init>(var10002, var10003, var10004, var10005, var9, var15, z(var8));
      return var14;
   }

   private static LicenseApiClient.LoaderProof z(JsonObject var0) {
      if (var0.has("loaderProof") && var0.get("loaderProof").isJsonObject()) {
         JsonObject var1 = var0.getAsJsonObject("loaderProof");
         if (var1.has("offset") && var1.has("length")) {
            long var2 = var1.get("offset").getAsLong();
            long var4 = var1.get("length").getAsLong();
            return var2 >= 0L && var4 > 0L && var4 <= 1048576L ? new LicenseApiClient.LoaderProof(var2, (int)var4) : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   String A(LicenseApiClient.Challenge var1) throws IOException, InterruptedException {
      String var2 = O(var1.id()) + O(var1.pollToken());
      return this.G(var2, null).get("status").getAsString();
   }

   LicenseApiClient.Grant B(LicenseApiClient.Challenge var1, String var2) throws IOException, InterruptedException {
      JsonObject var3 = new JsonObject();
      var3.addProperty("pollToken", var1.pollToken());
      var3.addProperty("codeVerifier", var2);
      if (var1.loaderProof() != null) {
         var3.addProperty("loaderProofDigest", HardwareFingerprint.ax(var1.id(), var1.loaderProof().offset(), var1.loaderProof().length()));
      }

      JsonObject var4 = this.H(
         StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001\u0002","/v1/challenges/","/redeem">(O(var1.id())), var3, null
      );
      return new LicenseApiClient.Grant(
         var4.get("accessToken").getAsString(),
         var4.get("manifestToken").getAsString(),
         var4.get("leaseToken").getAsString(),
         Instant.parse(var4.get("expiresAt").getAsString()),
         Instant.parse(var4.get("graceExpiresAt").getAsString()),
         Instant.parse(var4.get("leaseExpiresAt").getAsString()),
         var4.get("payloadRevision").getAsString(),
         Instant.parse(var4.get("serverTime").getAsString())
      );
   }

   LicenseApiClient.Session C(LicenseConfig var1) throws IOException, InterruptedException {
      String var2 = Long.toString(Math.max(Instant.now().getEpochSecond(), var1.trustedTime));
      String var3 = String.join("\n", "POST", "/v1/session/refresh", LicenseSessionManager.ao(var1.accessToken), var2, this.L);
      String var4 = LicenseSessionManager.al(LicenseSessionManager.ag(var1.privateKey), var3);
      char var10000 = '댨';

      while (true) {
         switch (var10000) {
            case '댨':
               JsonObject var5 = new JsonObject();
               var5.addProperty("runtimeId", this.L);
               var5.addProperty("timestamp", var2);
               var5.addProperty("signature", var4);
               JsonObject var6 = this.H("/v1/session/refresh", var5, var1.accessToken);
               return new LicenseApiClient.Session(
                  var6.get("accessToken").getAsString(),
                  var6.get("manifestToken").getAsString(),
                  var6.get("leaseToken").getAsString(),
                  Instant.parse(var6.get("leaseExpiresAt").getAsString()),
                  var6.get("payloadRevision").getAsString(),
                  Instant.parse(var6.get("serverTime").getAsString())
               );
            case '댩':
               var10000 = '댧';
               break;
            default:
               LicenseBootstrapper.bA();
               return null;
         }
      }
   }

   byte[] D(LicenseConfig var1) throws IOException, InterruptedException, GeneralSecurityException {
      String var2 = Long.toString(Math.max(Instant.now().getEpochSecond(), var1.trustedTime));
      String var3 = String.join("\n", "POST", "/v1/download/nonce", LicenseSessionManager.ao(var1.accessToken), var1.payloadRevision, this.L, var2);
      JsonObject var4 = new JsonObject();
      var4.addProperty("payloadRevision", var1.payloadRevision);
      var4.addProperty("timestamp", var2);
      var4.addProperty("signature", LicenseSessionManager.al(LicenseSessionManager.ag(var1.privateKey), var3));
      JsonObject var5 = this.I("/v1/download/nonce", var4, var1.accessToken, this.L);
      String var6 = var5.get("nonce").getAsString();
      String var7 = Long.toString(Math.max(Instant.now().getEpochSecond(), var1.trustedTime));
      String var8 = "/v1/payload/current";
      KeyPair var9 = LicenseSessionManager.ae();
      byte[] var10 = var9.getPublic().getEncoded();
      String var11 = LicenseSessionManager.ap(var10);
      String var12 = LicenseSessionManager.an(var10);
      Arrays.fill(var10, (byte)0);
      String var13 = String.join("\n", "GET", var8, LicenseSessionManager.ao(var1.accessToken), var7, var6, var1.payloadRevision, var12, this.L);
      PrivateKey var14 = LicenseSessionManager.ag(var1.privateKey);
      String var15 = LicenseSessionManager.al(var14, var13);
      HttpRequest var16 = this.K(var8)
         .setHeader("Accept", "application/octet-stream")
         .header("Authorization", StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Bearer ">(var1.accessToken))
         .header("X-Install-Timestamp", var7)
         .header("X-Install-Nonce", var6)
         .header("X-Install-Signature", var15)
         .header("X-Install-Encryption-Key", var11)
         .header("X-ALICE-Runtime", this.L)
         .header("X-Payload-Revision", var1.payloadRevision)
         .GET()
         .build();
      HttpResponse var17 = this.ag.send(var16, BodyHandlers.ofInputStream());
      if (var17.statusCode() != 200) {
         ((InputStream)var17.body()).close();
         throw new LicenseApiClient.ApiException(var17.statusCode(), "Payload download rejected");
      } else {
         short var10000 = 2916;

         while (true) {
            switch (var10000) {
               case 2915:
                  FreeType.nFT_New_Library(9195211478252216271L, 6906905748454886360L);
                  throw null;
               case 2916:
                  var10000 = 2914;
                  break;
               default:
                  long var18 = var17.headers().firstValueAsLong("Content-Length").orElse(-1L);
                  if (var18 <= 33554432L) {
                     byte[] var20;
                     try (InputStream var21 = (InputStream)var17.body()) {
                        var20 = var21.readNBytes(33554433);
                     }

                     if (var20.length != 0) {
                        var10000 = 16901;
                     } else {
                        int var26 = 910822711;
                        var10000 = 16902;
                     }

                     label77:
                     while (true) {
                        short var25 = var10000;
                        if (var25 != 16901) {
                           if (var25 == 16902) {
                              char var33 = '\uda27';

                              while (true) {
                                 switch (var33) {
                                    case '\uda27':
                                       int var27 = 701440886;
                                       var10000 = 20567;

                                       while (true) {
                                          switch (var10000) {
                                             case 20566:
                                                throw null;
                                             case 20567:
                                                var10000 = 20565;
                                                break;
                                             default:
                                                var10000 = 16900;
                                                continue label77;
                                          }
                                       }
                                    case '\uda28':
                                       var33 = '\uda26';
                                       break;
                                    default:
                                       TT_Postscript.nitalicAngle(-5802450476175166360L);
                                       throw null;
                                 }
                              }
                           }
                        } else if ((var18 < 0L || var20.length == var18) && var20.length <= 33554432) {
                           byte[] var30 = LicenseSessionManager.ai(var20, var9.getPrivate(), var6, var1.payloadRevision);
                           Arrays.fill(var20, (byte)0);
                           return var30;
                        }

                        Arrays.fill(var20, (byte)0);
                        throw new IOException("Encrypted payload response is incomplete or oversized");
                     }
                  }

                  ((InputStream)var17.body()).close();
                  throw new IOException("Encrypted payload exceeds the download safety limit");
            }
         }
      }
   }

   boolean E() throws IOException, InterruptedException {
      HttpRequest var1 = this.K("/health").GET().build();
      HttpResponse var2 = this.ag.send(var1, BodyHandlers.discarding());
      boolean var10000;
      if (var2.statusCode() == 200) {
         int var3 = 742143394;
         var10000 = true;
      } else {
         var10000 = false;
      }

      return var10000;
   }

   String F() {
      return this.ao;
   }

   private JsonObject G(String var1, String var2) throws IOException, InterruptedException {
      Builder var3 = this.K(var1).GET();
      if (var2 != null) {
         var3.header("Authorization", StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Bearer ">(var2));
      }

      return this.J(var3.build());
   }

   private JsonObject H(String var1, JsonObject var2, String var3) throws IOException, InterruptedException {
      return this.I(var1, var2, var3, null);
   }

   private JsonObject I(String var1, JsonObject var2, String var3, String var4) throws IOException, InterruptedException {
      Builder var5 = this.K(var1).header("Content-Type", "application/json").POST(BodyPublishers.ofString(bq.toJson(var2)));
      int var10000 = var3 != null ? '\ue88c' : '\ue88d';

      while (true) {
         int var6 = var10000;
         if (var6 == 59532) {
            var5.header("Authorization", StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","Bearer ">(var3));
            break;
         }

         if (var6 != 59533) {
            break;
         }

         var10000 = 59531;
      }

      if (var4 != null) {
         var5.header("X-ALICE-Runtime", var4);
      }

      return this.J(var5.build());
   }

   private JsonObject J(HttpRequest var1) throws IOException, InterruptedException {
      HttpResponse var2 = this.ag.send(var1, BodyHandlers.ofInputStream());
      long var3 = var2.headers().firstValueAsLong("Content-Length").orElse(-1L);
      if (var3 <= 65536L) {
         byte[] var5;
         try (InputStream var6 = (InputStream)var2.body()) {
            var5 = var6.readNBytes(65537);
         }

         if (var5.length <= 65536 && (var3 < 0L || var5.length == var3)) {
            String var13 = new String(var5, StandardCharsets.UTF_8);
            Arrays.fill(var5, (byte)0);
            if (var2.statusCode() < 200) {
               int var9 = 1504208494;
            } else if (var2.statusCode() < 300) {
               return JsonParser.parseString(var13).getAsJsonObject();
            }

            String var7 = "API request failed";
            JsonObject var8 = JsonParser.parseString(var13).getAsJsonObject();
            if (var8.has("error")) {
               var7 = var8.get("error").getAsString();
            }

            short var10000 = 4042;

            while (true) {
               switch (var10000) {
                  case 4042:
                     int var10 = 1175633796;
                     throw new LicenseApiClient.ApiException(var2.statusCode(), var7);
                  case 4043:
                     var10000 = 4041;
                     break;
                  default:
                     LicenseBootstrapper.bA();
                     return null;
               }
            }
         } else {
            Arrays.fill(var5, (byte)0);
            throw new IOException("API response is incomplete or oversized");
         }
      } else {
         ((InputStream)var2.body()).close();
         throw new IOException("API response exceeds the safety limit");
      }
   }

   private Builder K(String var1) {
      return HttpRequest.newBuilder(this.bu.resolve(var1))
         .timeout(PayloadConstants.V)
         .header("Accept", "application/json")
         .header("User-Agent", StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0002\u0001","ALICE-Loader/">(PayloadConstants.d));
   }

   private String L(JsonObject var1) throws IOException {
      int var10000 = !var1.has("verificationUrl") ? '뫳' : '뫴';

      while (true) {
         int var4 = var10000;
         if (var4 == 47859) {
            throw new IOException("Access server omitted the verification URL");
         }

         if (var4 != 47860) {
            URI var2 = URI.create(var1.get("verificationUrl").getAsString());
            if (var2.isAbsolute()) {
               if (var2.getUserInfo() == null && var2.getFragment() == null) {
                  var10000 = !"/v1/verify".equals(var2.getPath()) ? '뉁' : '뉀';

                  while (true) {
                     int var3 = var10000;
                     if (var3 == 45632) {
                        if (M(this.bu, var2)) {
                           return var2.toString();
                        }
                        break;
                     }

                     if (var3 != 45633) {
                        break;
                     }

                     var10000 = 45631;
                  }
               }
            } else {
               short var5 = 13368;

               while (true) {
                  switch (var5) {
                     case 13367:
                        LicenseBootstrapper.bA();
                        return null;
                     case 13368:
                        var5 = 13366;
                        break;
                     default:
                        throw new IOException("Access server returned an untrusted verification URL");
                  }
               }
            }

            throw new IOException("Access server returned an untrusted verification URL");
         }

         var10000 = 47858;
      }
   }

   private static boolean M(URI var0, URI var1) {
      int var10000 = var0.getScheme() != null ? '谊' : '谋';

      while (true) {
         int var2 = var10000;
         if (var2 == 35850) {
            if (var0.getScheme().equalsIgnoreCase(var1.getScheme())
               && var0.getHost() != null
               && var0.getHost().equalsIgnoreCase(var1.getHost())
               && N(var0) == N(var1)) {
               var3 = true;
               break;
            }
         } else if (var2 == 35851) {
            var10000 = 35849;
            continue;
         }

         var3 = false;
         break;
      }

      return var3;
   }

   private static int N(URI var0) {
      if (var0.getPort() >= 0) {
         return var0.getPort();
      } else {
         return "https".equalsIgnoreCase(var0.getScheme()) ? 443 : (!"http".equalsIgnoreCase(var0.getScheme()) ? -1 : 80);
      }
   }

   private static String O(String var0) {
      return URLEncoder.encode(var0, StandardCharsets.UTF_8);
   }

   static final class ApiException extends IOException {
      private final int y;
      public static int[] aZ;

      ApiException(int var1, String var2) {
         super(var2);
         this.y = var1;
      }

      int a() {
         return this.y;
      }
   }

   record Challenge(
      String id,
      String verificationUrl,
      String pollToken,
      Instant expiresAt,
      boolean verificationRequired,
      String deviceFingerprint,
      LicenseApiClient.LoaderProof loaderProof
   ) {
      public static int[] bl;
   }

   record Grant(
      String accessToken,
      String manifestToken,
      String leaseToken,
      Instant expiresAt,
      Instant graceExpiresAt,
      Instant leaseExpiresAt,
      String payloadRevision,
      Instant serverTime
   ) {
      public static int[] ae;
   }

   record LoaderProof(long offset, int length) {
      public static int[] f;
   }

   record Session(String accessToken, String manifestToken, String leaseToken, Instant leaseExpiresAt, String payloadRevision, Instant serverTime) {
      public static int[] bw;
   }
}
