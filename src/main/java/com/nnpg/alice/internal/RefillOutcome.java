package com.nnpg.alice.internal;

sealed interface RefillOutcome permits RefillOutcome.Ready, RefillOutcome.Missing, RefillOutcome.Waiting {
   RefillOutcome td = new RefillOutcome.Missing();
   RefillOutcome hz = new RefillOutcome.Waiting();

   public static final class Missing implements RefillOutcome {
      private Missing() {
      }
   }

   public record Ready(int hotbarSlot) implements RefillOutcome {
   }

   public static final class Waiting implements RefillOutcome {
      private Waiting() {
      }
   }
}
