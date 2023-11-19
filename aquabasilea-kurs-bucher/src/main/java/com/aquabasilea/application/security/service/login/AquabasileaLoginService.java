package com.aquabasilea.application.security.service.login;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.web.login.AquabasileaLogin;
import com.brugalibre.domain.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;

/**
 * For the actual booking of {@link Course}s on the migros-fitness page by the {@link com.aquabasilea.web.bookcourse.AquabasileaWebCourseBooker}
 * an external migros-fitness login is required. Those credentials are provided by the {@link User} data during the registration.
 * Therefore, when a new {@link User} account is created, we have to make sure, that the entered credentials matches with
 * the one for the migros-fitness login.
 * <p>
 * Otherwise, the user would not know, that his next booking of a course will fail, since the provided credentials are not valid for
 * migros-fitness
 */
@Service
public class AquabasileaLoginService {

   private static final Logger LOG = LoggerFactory.getLogger(AquabasileaLoginService.class);
   private final BiFunction<String, char[], AquabasileaLogin> aquabasileaLoginSupplier;
   private static final int RETRIES = 3;

   @Autowired
   public AquabasileaLoginService(@Value("${application.configuration.course-booker-config}") String propertiesFile) {
      this((username, pwd) -> AquabasileaLogin.createAquabasileaLogin(username, pwd, propertiesFile));
   }

   public AquabasileaLoginService(BiFunction<String, char[], AquabasileaLogin> aquabasileaLoginSupplier) {
      this.aquabasileaLoginSupplier = aquabasileaLoginSupplier;
   }

   /**
    * Does a login on the migros-fitness page with the given credentials
    *
    * @param username the users login-name
    * @param password the users login-password
    * @return <code>true</code> if the given credentials are valid for the migros-fitness course or throws a {@link CredentialsNotValidException} if not
    * @throws CredentialsNotValidException if the provided credentials are not valid
    */
   public boolean validateCredentials(String username, char[] password) {
      AquabasileaLogin aquabasileaLogin = aquabasileaLoginSupplier.apply(username, password);
      boolean isLoggedIn = tryLoginRecursively(aquabasileaLogin);
      if (!isLoggedIn) {
         LOG.error("Could not verify login infos!");
         throw new CredentialsNotValidException(TextResources.MIGROS_FITNESS_CREDENTIALS_NOT_VALID.formatted(username));
      }
      return true;
   }

   /*
    * Yes this has to be done recursively, since the browser-start may fail under ubuntu..
    */
   private static boolean tryLoginRecursively(AquabasileaLogin aquabasileaLogin) {
      int counter = RETRIES;
      while (counter > 0) {
         try {
            sleep();
            return aquabasileaLogin.doLogin();
         } catch (Exception e) {
            LOG.error("Error during login!", e);
            tryLogout(aquabasileaLogin);
            counter--;
         }
      }
      LOG.warn("No retries left, giving up..!");
      return false;
   }

   private static void sleep() {
      try {
         LOG.info("Wait until next try..");
         Thread.sleep(500);
      } catch (InterruptedException ex) {
         // ignore
      }
   }

   private static void tryLogout(AquabasileaLogin aquabasileaLogin) {
      try {
         aquabasileaLogin.logout();
      } catch (Exception e) {
         // SoSuchSessionException may occur if the login wasn't successful.
         LOG.warn("Error during logout {}", e.getMessage());
      }
   }
}
