package com.aquabasilea.web.login;

import com.aquabasilea.web.bookcourse.impl.AquabasileaWebCourseBookerImpl;
import com.zeiterfassung.web.common.navigate.util.WebNavigateUtil;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v108.network.Network;
import org.openqa.selenium.devtools.v108.network.model.Request;
import org.openqa.selenium.devtools.v108.network.model.RequestWillBeSent;
import org.openqa.selenium.remote.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;

import static com.aquabasilea.web.constant.AquabasileaWebConst.AQUABASILEA_WEB_KURS_BUCHER_PROPERTIES;
import static java.util.Objects.isNull;

public class AquabasileaBearerTokenExtractor extends AquabasileaLogin {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaWebCourseBookerImpl.class);
   private static final String OAUTH_2_USERINFO = "oauth2/userinfo";
   private static final String AUTHORIZATION = "Authorization";
   private String bearerToken;

   public AquabasileaBearerTokenExtractor(String userName, char[] userPassword, String propertiesName) {
      super(userName, userPassword, propertiesName);
   }

   /**
    * Creates and prepares a new {@link AquabasileaBearerTokenExtractor}
    *
    * @param userName             the username
    * @param userPassword         the user-password
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
    * @return the bearer
    */
   public String extractBearerToken() {
      setUpDevTools();
      super.doLogin();
      return bearerToken;
   }

   @Override
   protected void waitUntilLoginCompleted() {
      long timeOut = Duration.ofSeconds(60).toMillis();
      int increment = 100;
      while (isNull(bearerToken) && timeOut > 0) {
         WebNavigateUtil.waitForMilliseconds(increment);
         timeOut = timeOut - increment;
      }
      LOG.info("Done with waitUntilLoginCompleted. Bearer token found {}, time elapsed {}", bearerToken != null ? "yes" : "no", timeOut);
   }

   private void setUpDevTools() {
      DevTools devTool = this.webNavigatorHelper.getDevTool();
      devTool.createSession();
      devTool.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
      devTool.addListener(Network.requestWillBeSent(), this::interceptRequest);
   }

   private void interceptRequest(RequestWillBeSent requestSent) {
      Request request = requestSent.getRequest();
      if (isOAuthUserInfoRequest(request)) {
         bearerToken = (String) request.getHeaders().get(AUTHORIZATION);
         LOG.info("Found bearer token!");
      }
   }

   private static boolean isOAuthUserInfoRequest(Request request) {
      return request.getUrl().contains(OAUTH_2_USERINFO)
              && request.getMethod().equals(HttpMethod.GET.name())
              && request.getHeaders().containsKey(AUTHORIZATION);
   }
}
