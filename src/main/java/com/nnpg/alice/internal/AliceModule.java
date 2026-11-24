package com.nnpg.alice.internal;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.listeners.IListener;

public abstract class AliceModule extends Module {
   private final List<IListener> bp = new ArrayList<>();

   public AliceModule(Category var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public void onActivate() {
      this.aF();
   }

   public void onDeactivate() {
      this.aG();
   }

   private void aF() {
      this.aG();

      for (Class var1 = this.getClass(); var1 != null && var1 != AliceModule.class && var1 != Module.class; var1 = var1.getSuperclass()) {
         label55:
         for (Method var5 : var1.getDeclaredMethods()) {
            if (var5.isAnnotationPresent(AliceEventHandler.class) && var5.getParameterCount() == 1) {
               int var10000 = var5.getReturnType() != void.class ? 18407 : 18406;

               while (true) {
                  switch (var10000) {
                     case 18406:
                        final Class var6 = var5.getParameterTypes()[0];
                        final int var7 = var5.getAnnotation(AliceEventHandler.class).priority();

                        final MethodHandle var8;
                        try {
                           var8 = MethodHandles.privateLookupIn(var1, MethodHandles.lookup())
                              .unreflect(var5)
                              .bindTo(this)
                              .asType(MethodType.methodType(void.class, Object.class));
                        } catch (IllegalAccessException var10) {
                           throw new IllegalStateException(var5 + "", var10);
                        }

                        IListener var9 = new IListener() {
                           public void call(Object var1) {
                              var8.invokeExact((Object)var1);
                           }

                           public Class<?> getTarget() {
                              return var6;
                           }

                           public int getPriority() {
                              return var7;
                           }

                           public boolean isStatic() {
                              return false;
                           }
                        };
                        this.bp.add(var9);
                        MeteorClient.EVENT_BUS.subscribe(var9);
                        continue label55;
                     case 18407:
                        var10000 = 18405;
                        break;
                     default:
                        continue label55;
                  }
               }
            }
         }
      }
   }

   private void aG() {
      Iterator var1 = this.bp.iterator();

      while (true) {
         int var10000 = var1.hasNext() ? '턥' : '턦';

         while (true) {
            int var3 = var10000;
            if (var3 == 53541) {
               IListener var2 = (IListener)var1.next();
               MeteorClient.EVENT_BUS.unsubscribe(var2);
               break;
            }

            if (var3 != 53542) {
               this.bp.clear();
               return;
            }

            var10000 = 53540;
         }
      }
   }
}
