package com.aquabasilea.application.initialize.common;

import com.aquabasilea.application.initialize.api.user.InitializerForUser;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

public final class InitializationConst {
   private InitializationConst() {
      //private
   }

   public static final int COURSE_LOCATIONS = 1;
   public static final int USER_CREDENTIALS = 100;
   public static final int AQUABASILEA_PERSISTENCE = 200;
   public static final int USER_CONFIG = 300;
   public static final int AQUABASILEA_COURSE_BOOKER = 400;
   public static final int COURSE_DEF_UPDATER = 900;

   /**
    * @param initType the {@link InitType} to filter with
    * @return a {@link Predicate} which only returns true if an {@link InitializerForUser}
    * is annotated with a {@link InitializeOrder} from the given {@link InitType}
    */
   public static Predicate<InitializerForUser> isInitializerForType(InitType initType) {
      return initializer -> {
         InitializeOrder initializeOrder1 = initializer.getClass().getAnnotation(InitializeOrder.class);
         return initializeOrder1 != null && Arrays.asList(initializeOrder1.type()).contains(initType);
      };
   }

   /**
    * @return a {@link Comparator} which compares two {@link InitializerForUser}s annotated with a
    * {@link InitializeOrder} according their order
    */
   public static Comparator<InitializerForUser> compareOrder() {
      return (initializer1, initializer2) -> {
         InitializeOrder initializeOrder1 = initializer1.getClass().getAnnotation(InitializeOrder.class);
         InitializeOrder initializeOrder2 = initializer2.getClass().getAnnotation(InitializeOrder.class);
         return Integer.compare(initializeOrder1.order(), initializeOrder2.order());
      };
   }
}
