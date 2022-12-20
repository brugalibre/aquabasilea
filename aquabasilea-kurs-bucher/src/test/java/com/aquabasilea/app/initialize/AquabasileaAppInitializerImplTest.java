package com.aquabasilea.app.initialize;

import com.aquabasilea.app.initialize.api.UserAddedEvent;
import com.aquabasilea.app.initialize.coursebooker.AquabasileaCourseBookerInitializer;
import com.aquabasilea.app.initialize.persistence.PersistenceInitializer;
import com.aquabasilea.app.initialize.usercredentials.UserCredentialsInitializer;
import com.aquabasilea.model.course.CourseLocation;
import com.aquabasilea.model.course.coursedef.repository.CourseDefRepository;
import com.aquabasilea.model.course.coursedef.update.CourseDefUpdater;
import com.aquabasilea.model.course.coursedef.update.facade.CourseExtractorFacade;
import com.aquabasilea.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.model.statistics.repository.StatisticsRepository;
import com.aquabasilea.model.userconfig.UserConfig;
import com.aquabasilea.model.userconfig.repository.UserConfigRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.security.securestorage.WriteSecretToKeyStore;
import com.aquabasilea.service.security.login.AquabasileaLoginService;
import com.aquabasilea.service.security.securestorage.SecretStoreService;
import com.aquabasilea.service.statistics.StatisticsService;
import com.aquabasilea.service.userconfig.UserConfigService;
import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.login.AquabasileaLogin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestAquabasileaCourseBookerPersistenceConfig.class)
class AquabasileaAppInitializerImplTest {

   public static final String TEST_RESOURCES_AQUABASILEA_KEYSTORE_KEYSTORE = "F:\\Dominic\\Documents\\Eigene Dateien\\Programmierung\\Java only\\aquabasilea\\aquabasilea-kurs-bucher\\src\\" +
           "test\\resources\\aquabasilea.keystore";
   public static final String KEY_STORE_PASSWORD = "test123";
   public static final String USERNAME_1 = "username1";
   public static final String USERNAME_2 = "username2";
   @Autowired
   private UserConfigService userConfigService;

   @Autowired
   private PersistenceInitializer persistenceInitializer;

   @Mock
   private AquabasileaCourseBookerInitializer aquabasileaCourseBookerInitializer;

   @Autowired
   private WeeklyCoursesRepository weeklyCoursesRepository;

   @Autowired
   private UserConfigRepository userConfigRepository;
   @Autowired
   private CourseDefRepository courseDefRepository;

   @Autowired
   private StatisticsRepository statisticsRepository;

   @Autowired
   private StatisticsService statisticsService;

   @AfterEach
   public void cleanUp() {
      weeklyCoursesRepository.deleteAll();
      userConfigRepository.deleteAll();
      statisticsRepository.deleteAll();
      resetUserPasswordSecretStore();
   }

   @Test
   void initializeAquabasilaApp4User() {
      // Given
      String userId1 = "userId1";
      String userId2 = "userId2";
      String password1 = "userPassword1";
      String password2 = "userPassword2";

      Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractorSupplier = () -> list -> List::of;
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(new CourseExtractorFacade(aquabasileaCourseExtractorSupplier, () -> null),
              statisticsService, courseDefRepository, userConfigRepository);

      UserAddedEvent userAddedEvent1 = createUserAddedEvent(USERNAME_1, userId1, password1);
      UserAddedEvent userAddedEvent2 = createUserAddedEvent(USERNAME_2, userId2, password2);
      AquabasileaLoginService aquabasileaLoginService = mockAquabasileaLoginService();
      UserCredentialsInitializer userCredentialsInitializer = new UserCredentialsInitializer(aquabasileaLoginService, KEY_STORE_PASSWORD, TEST_RESOURCES_AQUABASILEA_KEYSTORE_KEYSTORE);
      AquabasileaAppInitializerImpl aquabasileaAppInitializer = new AquabasileaAppInitializerImpl(persistenceInitializer,
              userCredentialsInitializer, aquabasileaCourseBookerInitializer, courseDefUpdater, null);

      // When
      aquabasileaAppInitializer.initialize(userAddedEvent1);
      aquabasileaAppInitializer.initialize(userAddedEvent2);
      userConfigService.updateCourseLocations(userId2, List.of(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));

      // Then
      // verify we validated the right credentials
      verify(aquabasileaLoginService).validateCredentials(eq(USERNAME_1), eq(password1.toCharArray()));
      verify(aquabasileaLoginService).validateCredentials(eq(USERNAME_2), eq(password2.toCharArray()));

      // verify we initialized the aquabasileaCourseBookerInitializer
      verify(aquabasileaCourseBookerInitializer).initialize(eq(userAddedEvent1));
      verify(aquabasileaCourseBookerInitializer).initialize(eq(userAddedEvent2));

      // verify we created a default config and updated the one of user 2
      UserConfig userConfig1 = userConfigService.getByUserId(userId1);
      UserConfig userConfig2 = userConfigService.getByUserId(userId2);
      assertThat(userConfig1.getCourseLocations().size(), is(3));
      assertThat(userConfig2.getCourseLocations().get(0), is(CourseLocation.MIGROS_FITNESSCENTER_AQUABASILEA));

      // verify that we have stored the right passwords
      SecretStoreService secretStoreService = new SecretStoreService(KEY_STORE_PASSWORD, TEST_RESOURCES_AQUABASILEA_KEYSTORE_KEYSTORE);
      assertThat(secretStoreService.getUserPassword(USERNAME_1), is(password1.toCharArray()));
      assertThat(secretStoreService.getUserPassword(USERNAME_2), is(password2.toCharArray()));
   }

   private static UserAddedEvent createUserAddedEvent(String username, String userId, String password) {
      char[] userPwd = password.toCharArray();
      return new UserAddedEvent(username, "+41791234567", userId, userPwd);
   }

   private static AquabasileaLoginService mockAquabasileaLoginService() {
      AquabasileaLogin aquabasileaLogin = mock(AquabasileaLogin.class);
      when(aquabasileaLogin.doLogin()).thenReturn(true);
      return spy(new AquabasileaLoginService((u, pw) -> aquabasileaLogin));
   }

   /**
    * Reset the passwords in the store so that we actually can verify if there were written into the store during the test
    */
   private static void resetUserPasswordSecretStore() {
      WriteSecretToKeyStore writeSecretToKeyStore = new WriteSecretToKeyStore();
      writeSecretToKeyStore.writeSecretToKeyStore(TEST_RESOURCES_AQUABASILEA_KEYSTORE_KEYSTORE, KEY_STORE_PASSWORD.toCharArray(),
              USERNAME_1, "not-set".toCharArray());
      writeSecretToKeyStore.writeSecretToKeyStore(TEST_RESOURCES_AQUABASILEA_KEYSTORE_KEYSTORE, KEY_STORE_PASSWORD.toCharArray(),
              USERNAME_2, "not-set".toCharArray());
   }
}