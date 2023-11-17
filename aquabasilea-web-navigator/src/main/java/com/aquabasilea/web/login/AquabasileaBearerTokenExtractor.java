package com.aquabasilea.web.login;

import com.zeiterfassung.web.common.inout.PropertyReader;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v117.network.Network;
import org.openqa.selenium.devtools.v117.network.model.Request;
import org.openqa.selenium.devtools.v117.network.model.RequestWillBeSent;
import org.openqa.selenium.remote.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static com.aquabasilea.web.constant.AquabasileaWebConst.AQUABASILEA_WEB_KURS_BUCHER_PROPERTIES;
import static java.util.Objects.isNull;

public class AquabasileaBearerTokenExtractor extends AquabasileaLogin {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaBearerTokenExtractor.class);
   private static final String OAUTH_2_USERINFO = "oauth2/userinfo";
   private static final String AUTHORIZATION = "Authorization";
   private final long extractionTimeOutMillis;
   private String bearerToken;

   public AquabasileaBearerTokenExtractor(String userName, char[] userPassword, String propertiesName) {
      super(userName, userPassword, propertiesName);
      extractionTimeOutMillis = getExtractionTimeOut(propertiesName);
   }

   /**
    * Creates and prepares a new {@link AquabasileaBearerTokenExtractor}
    *
    * @param userName     the username
    * @param userPassword the user-password
    * @return a new {@link AquabasileaBearerTokenExtractor}
    */
   public static AquabasileaBearerTokenExtractor createAquabasileaBearerTokenExtractor(String userName, char[] userPassword) {
      return createAquabasileaBearerTokenExtractor(userName, userPassword, AQUABASILEA_WEB_KURS_BUCHER_PROPERTIES);
   }

   /**
    * Creates and prepares a new {@link AquabasileaBearerTokenExtractor}
    *
    * @param userName             the username
    * @param userPassword         the user-password
    * @param configPropertiesFile the properties file for the configuration
    * @return a new {@link AquabasileaLogin}
    */
   public static AquabasileaBearerTokenExtractor createAquabasileaBearerTokenExtractor(String userName, char[] userPassword, String configPropertiesFile) {
      AquabasileaBearerTokenExtractor aquabasileaBearerTokenExtractor = new AquabasileaBearerTokenExtractor(userName, userPassword, configPropertiesFile);
      aquabasileaBearerTokenExtractor.initWebDriver();
      return aquabasileaBearerTokenExtractor;
   }

   /**
    * Extracts the bearer token by doing a login using the migros login website while we listen to
    * all outgoing requests. One of those requests, which is sent after the login is done, uses the bearer token as the
    * Authorization header
    *
    * @return the bearer-token
    */
   public String extractBearerToken() {
      LOG.info("Start bearer token extraction");
      setUpDevTools();
      super.doLogin();
      return bearerToken;
   }

   @Override
   public void logout() {
      DevTools devTool = this.webNavigatorHelper.getDevTool();
      devTool.disconnectSession();
      super.logout();
   }

   @Override
   protected void wait4Navigate2CoursePageCompleted() {
      LOG.info("Now waiting for dev-tools..");
      long timeOut = this.extractionTimeOutMillis;
      long start = System.currentTimeMillis();
      int increment = 100;
      while (hasNoBearerToken() && timeOut > 0) {
         WebNavigateUtil.waitForMilliseconds(increment);
         timeOut = timeOut - increment;
      }
      LOG.info("Done with waiting. Bearer token found? {}, time elapsed={}ms", bearerToken != null ? "yes" : "no", (System.currentTimeMillis() - start));
   }

   private synchronized boolean hasNoBearerToken() {
      return isNull(bearerToken);
   }

   private void setUpDevTools() {
      final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
      DevTools devTool = this.webNavigatorHelper.getDevTool();
      devTool.createSession();
      devTool.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
      devTool.addListener(Network.requestWillBeSent(), requestSent -> interceptRequest(requestSent, copyOfContextMap));
   }

   private void interceptRequest(RequestWillBeSent requestSent, Map<String, String> copyOfContextMap) {
      MDC.setContextMap(copyOfContextMap);
      Request request = requestSent.getRequest();
      if (isOAuthUserInfoRequest(request) && hasNoBearerToken()) {
         setBearerToken(request);
         LOG.info("Bearer token found!");
      }
   }

   private synchronized void setBearerToken(Request request) {
      bearerToken = (String) request.getHeaders().get(AUTHORIZATION);
   }

   private static boolean isOAuthUserInfoRequest(Request request) {
      return request.getUrl().contains(OAUTH_2_USERINFO)
              && request.getMethod().equals(HttpMethod.GET.name())
              && request.getHeaders().containsKey(AUTHORIZATION);
   }

   private static long getExtractionTimeOut(String propertiesName) {
      PropertyReader propertyReader = new PropertyReader(propertiesName);
      int extractionTimeOutSeconds = Integer.parseInt(propertyReader.readValueOrDefault("extractionTimeOutSeconds", "60"));
      return Duration.ofSeconds(extractionTimeOutSeconds).toMillis();
   }
}
