package com.nnpg.alice.bridge;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;

final class BridgeGuard {
   private static final StackWalker n = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);

   private BridgeGuard() {
   }

   static boolean cD() {
      try {
         return n.walk(
            var0 -> var0.map(StackFrame::getDeclaringClass)
               .filter(var0x -> !var0x.getName().startsWith("com.nnpg.alice.bridge."))
               .findFirst()
               .map(var0x -> var0x.getClassLoader() instanceof PayloadRuntimeHost)
               .orElse(false)
         );
      } catch (LinkageError | RuntimeException var1) {
         return false;
      }
   }
}
