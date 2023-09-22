package com.aquabasilea.rest.app.config.security.auth;

import com.brugalibre.common.security.auth.config.WebSecurityConfigHelper;
import com.brugalibre.persistence.user.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.aquabasilea.rest.api.smsinbound.SmsInboundRestApiController.API_V_1_SMS_INBOUND;

@Service
public class AquabasileaWebSecurityConfigHelper implements WebSecurityConfigHelper {

   @Value("server:servlet:context-path:'/'")
   private String loginProcessingUrl;

   private static final String V_1_AQUABASILEA_COURSE_BOOKER = "/api/v1/aquabasilea-course-booker/**";
   private static final String API_V_1_ADMIN = "/api/v1/admin/**";

   @Override
   public String[] getRequestMatcherForRole(String role) {
      if (Role.USER.name().equals(role)) {
         return new String[]{V_1_AQUABASILEA_COURSE_BOOKER};
      } else if (Role.ADMIN.name().equals(role)) {
         return new String[]{API_V_1_ADMIN};
      }
      return new String[]{};
   }

   @Override
   public String[] getOptionalPermittedPatterns() {
      return new String[]{API_V_1_SMS_INBOUND + "/**"};
   }

   @Override
   public String getLoginProcessingUrl() {
      return loginProcessingUrl;
   }
}
