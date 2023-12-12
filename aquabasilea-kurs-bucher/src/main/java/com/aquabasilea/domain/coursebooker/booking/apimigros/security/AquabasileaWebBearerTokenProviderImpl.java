package com.aquabasilea.domain.coursebooker.booking.apimigros.security;

import com.aquabasilea.migrosapi.v1.service.security.bearertoken.BearerTokenProvider;
import com.aquabasilea.web.login.AquabasileaBearerTokenExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * The {@link AquabasileaWebBearerTokenProviderImpl} provides a bearer token based on the {@link AquabasileaBearerTokenExtractor}
 * Its save to instantiate this class once, during each getBearerToken call a new instance of a {@link AquabasileaBearerTokenExtractor} is created
 */
public class AquabasileaWebBearerTokenProviderImpl implements BearerTokenProvider {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaWebBearerTokenProviderImpl.class);
   public static final int TIME_OUT_MARGIN_IN_SECONDS = 6;
   private final ExecutorService executorService;
   private final String propertiesFile;

   public AquabasileaWebBearerTokenProviderImpl(String propertiesFile) {
      this.propertiesFile = propertiesFile;
      this.executorService = Executors.newCachedThreadPool();
   }

   @Override
   public String getBearerToken(String username, Supplier<char[]> userPwd) {
      LOG.info("Extract token");
      AquabasileaBearerTokenExtractor aquabasileaBearerTokenExtractor = AquabasileaBearerTokenExtractor
              .createAquabasileaBearerTokenExtractor(username, userPwd.get(), propertiesFile);
      return tryGetBearerTokenRecursively(aquabasileaBearerTokenExtractor);
   }

   /*
    * Yes this has to be done recursively, since the browser-start may fail under ubuntu..
    */
   private String tryGetBearerTokenRecursively(AquabasileaBearerTokenExtractor aquabasileaBearerTokenExtractor) {
      Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
      int counter = 5;
      while (counter > 0) {
         try {
            long extractionTimeout = getExtractionTimeout(aquabasileaBearerTokenExtractor);
            return executorService.submit(extractBearerToken(aquabasileaBearerTokenExtractor, copyOfContextMap))
                    .get(extractionTimeout, TimeUnit.SECONDS);
         } catch (java.util.concurrent.TimeoutException e) {
            LOG.error("Bearer token extraction took longer than allowed, abort!", e);
            counter = 0;// no retry
         } catch (ExecutionException | InterruptedException e) {
            if (isBearerTokenExtractionTimeout(e)) {
               LOG.error("Timeout during bearer token extraction! Retries left={}", counter - 1, e);
               counter--;
            } else {
               LOG.error("Exception during bearer token extraction! Retries left={}. Setting retries to 0", counter - 1, e);
               counter = 0;// we give up
            }
         }
      }
      LOG.warn("No retries left, giving up..!");
      return null;
   }

   private static Callable<String> extractBearerToken(AquabasileaBearerTokenExtractor aquabasileaBearerTokenExtractor,
                                                      Map<String, String> copyOfContextMap) {
      return () -> {
         MDC.setContextMap(copyOfContextMap);
         String bearerToken = aquabasileaBearerTokenExtractor.extractBearerToken();
         MDC.clear();
         return bearerToken;
      };
   }

   private static boolean isBearerTokenExtractionTimeout(Exception e) {
      return e.getCause() instanceof org.openqa.selenium.TimeoutException;
   }

   /**
    * Add a little bit more to the internal timeout of the extractor, so we are sure not to interrupt him to early
    */
   private static long getExtractionTimeout(AquabasileaBearerTokenExtractor aquabasileaBearerTokenExtractor) {
      return aquabasileaBearerTokenExtractor.getExtractionTimeOut()
              .plusSeconds(TIME_OUT_MARGIN_IN_SECONDS)
              .toSeconds();
   }
}
