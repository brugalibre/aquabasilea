package com.aquabasilea.application.initialize.common;

public enum InitType {
   /**
    * New user added -> create all necessary resources
    */
   USER_ADDED,

   /**
    * User activated / App started -> only create the resources which have not been created during {@link #USER_ADDED}
    */
   USER_ACTIVATED
}
