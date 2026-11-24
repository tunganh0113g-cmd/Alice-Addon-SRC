package com.nnpg.alice.internal;

import java.util.HashSet;

public final class PayloadIdentityVerifier {
   private PayloadIdentityVerifier() {
   }

   public static void S() {
      String[] var0 = new String[]{PayloadPartA.T(), PayloadPartB.U(), PayloadPartC.V(), PayloadPartD.W(), PayloadPartE.X()};
      HashSet var1 = new HashSet();

      for (int var2 = 0; var2 < var0.length; var2++) {
         String var3 = "ALICE-BC2-" + (char)(65 + var2) + ":";
         String var4 = var0[var2];
         if (var4 == null || var4.length() != var3.length() + 32 || !var4.startsWith(var3)) {
            throw new LinkageError("ALICE payload identity is incomplete");
         }

         String var5 = var4.substring(var3.length());

         for (int var6 = 0; var6 < var5.length(); var6++) {
            char var7 = var5.charAt(var6);
            if (!Character.isLetterOrDigit(var7) && var7 != '_' && var7 != '-') {
               throw new LinkageError("ALICE payload identity is malformed");
            }
         }

         if (!var1.add(var5)) {
            throw new LinkageError("ALICE payload identity is duplicated");
         }
      }
   }
}
