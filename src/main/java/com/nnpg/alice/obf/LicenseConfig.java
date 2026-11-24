package com.nnpg.alice.obf;

import com.google.gson.annotations.SerializedName;
import com.nnpg.alice.bridge.NameProtectionBridge;
import net.minecraft.class_5253.class_5254;

final class LicenseConfig {
   @SerializedName("privateKey")
   String privateKey;
   @SerializedName("publicKey")
   String publicKey;
   @SerializedName("accessToken")
   String accessToken;
   @SerializedName("manifestToken")
   String manifestToken;
   @SerializedName("leaseToken")
   String leaseToken;
   @SerializedName("clientBuild")
   String clientBuild;
   @SerializedName("payloadRevision")
   String payloadRevision;
   @SerializedName("expiresAt")
   long expiresAt;
   @SerializedName("graceExpiresAt")
   long graceExpiresAt;
   @SerializedName("leaseExpiresAt")
   long leaseExpiresAt;
   @SerializedName("trustedTime")
   long trustedTime;
   public static int[] as = new int[2];

   boolean ac() {
      if (this.accessToken != null && !this.accessToken.isBlank() && this.manifestToken != null) {
         int var10000 = !this.manifestToken.isBlank() ? '踖' : '踗';

         while (true) {
            int var1 = var10000;
            if (var1 == 36374) {
               if (this.leaseToken != null && !this.leaseToken.isBlank() && this.payloadRevision != null && !this.payloadRevision.isBlank()) {
                  char var10001 = '钀';

                  while (true) {
                     switch (var10001) {
                        case '钀':
                           return true;
                        case '钁':
                           var10001 = '鑿';
                           break;
                        default:
                           class_5254.method_27763(-1371044133, 473896487);
                           return NameProtectionBridge.cJ();
                     }
                  }
               }
               break;
            }

            if (var1 != 36375) {
               break;
            }

            var10000 = 36373;
         }
      }

      return false;
   }

   static {
      as[0] = 1;
      as[1] = 0;
   }
}
