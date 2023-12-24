package com.aquabasilea.application.initialize.common;

import com.aquabasilea.application.initialize.api.user.InitializerForUser;

public enum InitType {
   /**
    * The {@link InitializerForUser} is called once when the application is started
    */
   APP_STARTED,

   /**
    * New user added -> create all necessary resources
    */
   USER_ADDED,

   /**
    * User activated / App started -> only create the resources which have not been created during {@link #USER_ADDED}
    */
   USER_ACTIVATED
}
