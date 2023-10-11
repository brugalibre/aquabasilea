package com.aquabasilea.rest.service.logging;

import com.brugalibre.common.security.user.service.IUserProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.aquabasilea.application.config.logging.MdcConst.USER_ID;

@Component
public class MdcLoggingRequestInterceptor implements HandlerInterceptor {

   private final IUserProvider userProvider;

   @Autowired
   public MdcLoggingRequestInterceptor(IUserProvider userProvider) {
      this.userProvider = userProvider;
   }

   @Override
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
      MDC.put(USER_ID, userProvider.getCurrentUserId());
      return true;
   }

   @Override
   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
      MDC.remove(USER_ID);
   }
}