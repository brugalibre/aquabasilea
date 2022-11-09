package com.aquabasilea.rest.app.config.security.auth;

import com.brugalibre.common.security.auth.config.WebSecurityConfigHelper;
import org.springframework.stereotype.Service;

@Service
public class AquabasileaWebSecurityConfigHelper implements WebSecurityConfigHelper {
   @Override
   public String[] getRequestMatcherForRole(String s) {
      return new String[]{"/api/v1/aquabasilea-course-booker/**"};
   }

   @Override
   public String getLoginProcessingUrl() {
      return "/";
   }
}
