package com.aquabasilea.rest.app.config.mvc;

import com.aquabasilea.rest.service.logging.MdcLoggingRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

   private final MdcLoggingRequestInterceptor mdcLoggingRequestInterceptor;

   @Autowired
   public WebMvcConfig(MdcLoggingRequestInterceptor mdcLoggingRequestInterceptor) {
      this.mdcLoggingRequestInterceptor = mdcLoggingRequestInterceptor;
   }

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(mdcLoggingRequestInterceptor);
   }
}