package com.aquabasilea.domain.coursebooker.booking.apimigros.security;

import com.aquabasilea.migrosapi.v1.service.security.bearertoken.BearerTokenProvider;
import com.aquabasilea.web.login.AquabasileaBearerTokenExtractor;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * The {@link AquabasileaWebBearerTokenProviderImpl} provides a bearer token based on the {@link AquabasileaBearerTokenExtractor}
 * Its save to instantiate this class once, during each getBearerToken call a new instance of a {@link AquabasileaBearerTokenExtractor} is created
 */
public class AquabasileaWebBearerTokenProviderImpl implements BearerTokenProvider {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaWebBearerTokenProviderImpl.class);

   @Override
   public String getBearerToken(String username, Supplier<char[]> userPwd) {
      AquabasileaBearerTokenExtractor aquabasileaBearerTokenExtractor = AquabasileaBearerTokenExtractor.createAquabasileaBearerTokenExtractor(username, userPwd.get());
      return tryGetBearerTokenRecursively(aquabasileaBearerTokenExtractor);
   }

   /*
    * Yes this has to be done recursively, since the browser-start may fail under ubuntu..
    */
   private static String tryGetBearerTokenRecursively(AquabasileaBearerTokenExtractor aquabasileaBearerTokenExtractor) {
      int counter = 5;
      while (counter > 0) {
         try {
            return aquabasileaBearerTokenExtractor.extractBearerToken();
         } catch (TimeoutException e) {
            LOG.error("Timeout during bearer token extraction! Retries left={}", counter - 1, e);
            counter--;
         }
      }
      LOG.warn("No retries left, giving up..!");
      return null;
   }
}
