package com.nnpg.alice;

import com.nnpg.alice.internal.AliceModuleBootstrap;

public class AlicePayloadEntrypoint {
   private static boolean initialized = false;

   public static synchronized void init() {
      if (!initialized) {
         AliceModuleBootstrap.NH();
         initialized = true;
      }
   }
}
