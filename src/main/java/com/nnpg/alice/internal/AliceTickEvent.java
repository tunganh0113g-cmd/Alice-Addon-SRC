package com.nnpg.alice.internal;

import net.minecraft.util.math.BlockPos;

public final class AliceTickEvent {
   private AliceTickEvent() {
   }

   public static final class Post {
      private final boolean dn;
      private final BlockPos Ii;

      public Post(BlockPos var1, boolean var2) {
         this.Ii = var1;
         this.dn = var2;
      }

      public boolean getIsRebreak() {
         return this.dn;
      }

      public BlockPos getBlockPos() {
         return this.Ii;
      }
   }

   public static final class Pre {
      private final boolean zk;
      private final BlockPos ER;

      public Pre(BlockPos var1, boolean var2) {
         this.ER = var1;
         this.zk = var2;
      }

      public boolean getIsRebreak() {
         return this.zk;
      }

      public BlockPos getBlockPos() {
         return this.ER;
      }
   }
}
