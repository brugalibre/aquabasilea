package com.aquabasilea.reflection;

import org.apache.commons.lang.ClassUtils;

public class ReflectionUtil {

   private ReflectionUtil() {
      //private
   }

   /**
    * @param object the object to test
    * @return <code>true</code> if the given Object is primitiv or an auto boxed primitive value. Otherwise returns <code>false</code>
    */
   public static boolean isPrimitive(Object object) {
      return object.getClass().isPrimitive() || ClassUtils.wrapperToPrimitive(object.getClass()) != null;
   }
}
