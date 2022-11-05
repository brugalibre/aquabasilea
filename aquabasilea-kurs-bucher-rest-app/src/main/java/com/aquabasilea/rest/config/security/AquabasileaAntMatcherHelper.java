package com.aquabasilea.rest.config.security;

import com.brugalibre.common.security.auth.config.AntMatcherHelper;
import org.springframework.stereotype.Service;

@Service
public class AquabasileaAntMatcherHelper implements AntMatcherHelper {
   @Override
   public String[] getAntMatcherForRole(String role) {
      return new String[]{"/api/v1/aquabasilea-course-booker/**"};
   }

   @Override
   public String getDefaultSuccessUrl() {
      return "/manage";
   }
}
