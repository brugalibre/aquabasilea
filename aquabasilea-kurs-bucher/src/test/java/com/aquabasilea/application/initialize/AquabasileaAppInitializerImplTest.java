package com.aquabasilea.application.initialize;

import com.aquabasilea.application.initialize.api.UserAddedEvent;
import com.aquabasilea.application.initialize.coursebooker.AquabasileaCourseBookerInitializer;
import com.aquabasilea.application.initialize.coursedef.CourseDefUpdaterInitializer;
import com.aquabasilea.application.initialize.persistence.PersistenceInitializer;
import com.aquabasilea.application.initialize.userconfig.UserConfigInitializer;
import com.aquabasilea.application.initialize.usercredentials.UserCredentialsHandler;
import com.aquabasilea.application.security.securestorage.WriteSecretToKeyStore;
import com.aquabasilea.application.security.service.login.AquabasileaLoginService;
import com.aquabasilea.application.security.service.securestorage.SecretStoreService;
import com.aquabasilea.domain.course.model.CourseLocation;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.coursedef.update.CourseDefUpdater;
import com.aquabasilea.domain.coursedef.update.facade.CourseExtractorFacade;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.service.statistics.StatisticsService;
import com.aquabasilea.service.userconfig.UserConfigService;
import com.aquabasilea.web.extractcourses.AquabasileaCourseExtractor;
import com.aquabasilea.web.login.AquabasileaLogin;
import com.brugalibre.domain.contactpoint.mobilephone.model.MobilePhone;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import com.brugalibre.domain.user.service.userrole.UserRoleConfigService;
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

   public static final String TEST_RESOURCES_AQUABASILEA_KEYSTORE_KEYSTORE = "test-aquabasilea.keystore";
   public static final String KEY_STORE_PASSWORD = "test123";
   public static final String USERNAME_1 = "username1";
   public static final String USERNAME_2 = "username2";
   @Autowired
   private UserConfigService userConfigService;

   @Autowired
   private PersistenceInitializer persistenceInitializer;

   @Mock
   private AquabasileaCourseBookerInitializer aquabasileaCourseBookerInitializer;

   @Mock
   private UserRoleConfigService userRoleConfigService;

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
      String phoneNr = "+41791234567";

      Supplier<AquabasileaCourseExtractor> aquabasileaCourseExtractorSupplier = () -> list -> List::of;
      CourseDefUpdater courseDefUpdater = new CourseDefUpdater(new CourseExtractorFacade(aquabasileaCourseExtractorSupplier, () -> null),
              statisticsService::needsCourseDefUpdate, courseDefRepository, userConfigRepository);

      UserAddedEvent userAddedEvent1 = createUserAddedEvent(USERNAME_1, userId1, password1, phoneNr);
      UserAddedEvent userAddedEvent2 = createUserAddedEvent(USERNAME_2, userId2, password2, phoneNr);
      AquabasileaLoginService aquabasileaLoginService = mockAquabasileaLoginService();
      UserCredentialsHandler userCredentialsHandler = new UserCredentialsHandler(aquabasileaLoginService, KEY_STORE_PASSWORD, TEST_RESOURCES_AQUABASILEA_KEYSTORE_KEYSTORE);
      AquabasileaAppInitializerImpl aquabasileaAppInitializer = new AquabasileaAppInitializerImpl(null, List.of(persistenceInitializer,
              userCredentialsHandler, aquabasileaCourseBookerInitializer, new CourseDefUpdaterInitializer(courseDefUpdater), new UserConfigInitializer(userRoleConfigService)));

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
      verify(userRoleConfigService).addMissingRoles(eq(userId1));
      verify(userRoleConfigService).addMissingRoles(eq(userId2));
   }

   @Test
   void initializeAquabasilaApp4ExistingUser() {
      // Given
      String userId1 = "userId1";
      String userId2 = "userId2";
      String password1 = "userPassword1";
      String password2 = "userPassword2";
      String phoneNr = "+41791234567";

      CourseDefUpdater courseDefUpdaterMock = mock(CourseDefUpdater.class);
      UserCredentialsHandler userCredentialsHandlerMock = mock(UserCredentialsHandler.class);
      PersistenceInitializer persistenceInitializerMock = mock(PersistenceInitializer.class);

      UserAddedEvent userAddedEvent1 = createUserAddedEvent(USERNAME_1, userId1, null, phoneNr);
      UserAddedEvent userAddedEvent2 = createUserAddedEvent(USERNAME_2, userId2, null, phoneNr);
      UserRepository userRepository = mock(UserRepository.class);
      User user1 = User.of(userId1, USERNAME_1, password1, MobilePhone.of(phoneNr));
      User user2 = User.of(userId2, USERNAME_2, password2, MobilePhone.of(phoneNr));
      when(userRepository.getAll()).thenReturn(List.of(user1, user2));
      AquabasileaAppInitializerImpl aquabasileaAppInitializer = new AquabasileaAppInitializerImpl(userRepository, List.of(persistenceInitializerMock,
              userCredentialsHandlerMock, aquabasileaCourseBookerInitializer, new CourseDefUpdaterInitializer(courseDefUpdaterMock), new UserConfigInitializer(userRoleConfigService)));

      // When
      aquabasileaAppInitializer.initializeOnAppStart();

      // Then
      verify(userCredentialsHandlerMock, never()).initialize(any());
      verify(persistenceInitializerMock, never()).initialize(any());
      verify(userRoleConfigService).addMissingRoles(eq(userId1));
      verify(userRoleConfigService).addMissingRoles(eq(userId2));
      verify(aquabasileaCourseBookerInitializer).initialize(eq(userAddedEvent1));
      verify(aquabasileaCourseBookerInitializer).initialize(eq(userAddedEvent2));
      verify(courseDefUpdaterMock).startScheduler(eq(userId1));
      verify(courseDefUpdaterMock).startScheduler(eq(userId2));
   }

   private static UserAddedEvent createUserAddedEvent(String username, String userId, String password, String phoneNr) {
      char[] userPwd = password == null ? null : password.toCharArray();
      return new UserAddedEvent(username, phoneNr, userId, userPwd);
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