package com.aquabasilea.application.initialize;

import com.aquabasilea.application.initialize.api.AppInitializer;
import com.aquabasilea.application.initialize.api.user.InitializerForUser;
import com.aquabasilea.application.initialize.api.user.UserAddedEvent;
import com.aquabasilea.application.initialize.coursebooker.AquabasileaCourseBookerInitializer;
import com.aquabasilea.application.initialize.coursedef.CourseDefUpdaterInitializer;
import com.aquabasilea.application.initialize.persistence.PersistenceInitializer;
import com.aquabasilea.application.initialize.persistence.courselocation.CourseLocationInitializer;
import com.aquabasilea.application.initialize.persistence.userconfig.UserConfigInitializer;
import com.aquabasilea.application.initialize.usercredentials.UserCredentialsHandler;
import com.aquabasilea.application.security.securestorage.WriteSecretToKeyStore;
import com.aquabasilea.application.security.service.login.AquabasileaLoginService;
import com.aquabasilea.application.security.service.securestorage.SecretStoreService;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.domain.coursebooker.model.booking.CourseBookContainer;
import com.aquabasilea.domain.coursebooker.model.booking.cancel.CourseCancelResultDetails;
import com.aquabasilea.domain.coursebooker.model.booking.result.CourseBookingResultDetails;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacadeFactory;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseDefExtractorFacade;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseLocationExtractorFacade;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.coursedef.update.CourseDefExtractionResult;
import com.aquabasilea.domain.coursedef.update.CourseDefUpdater;
import com.aquabasilea.domain.courselocation.model.CourseLocation;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.domain.statistics.model.repository.StatisticsRepository;
import com.aquabasilea.domain.userconfig.model.UserConfig;
import com.aquabasilea.domain.userconfig.repository.UserConfigRepository;
import com.aquabasilea.persistence.config.TestAquabasileaCourseBookerPersistenceConfig;
import com.aquabasilea.service.coursebooker.migros.MigrosApiProvider;
import com.aquabasilea.service.statistics.StatisticsService;
import com.aquabasilea.service.userconfig.UserConfigService;
import com.aquabasilea.web.login.AquabasileaBearerTokenExtractor;
import com.brugalibre.domain.contactpoint.mobilephone.model.MobilePhone;
import com.brugalibre.domain.user.model.User;
import com.brugalibre.domain.user.repository.UserRepository;
import com.brugalibre.domain.user.service.userrole.UserRoleConfigService;
import org.awaitility.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.aquabasilea.test.TestConstants.FITNESSPARK_HEUWAAGE;
import static com.aquabasilea.test.TestConstants.MIGROS_FITNESSCENTER_AQUABASILEA;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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

   @Autowired
   private AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;

   @Autowired
   private AquabasileaCourseBookerInitializer aquabasileaCourseBookerInitializer;

   @Mock
   private UserRoleConfigService userRoleConfigService;

   @Mock
   private CourseLocationExtractorFacade courseLocationExtractorFacade;

   @Autowired
   private WeeklyCoursesRepository weeklyCoursesRepository;

   @Autowired
   private UserConfigRepository userConfigRepository;

   @Autowired
   private CourseDefRepository courseDefRepository;

   @Autowired
   private CourseLocationRepository courseLocationRepository;

   @Autowired
   private StatisticsRepository statisticsRepository;

   @Autowired
   private StatisticsService statisticsService;

   @BeforeEach
   public void setUp() {
      when(courseLocationExtractorFacade.getCourseLocations()).thenReturn(List.of(MIGROS_FITNESSCENTER_AQUABASILEA, FITNESSPARK_HEUWAAGE));
   }

   @AfterEach
   public void cleanUp() {
      weeklyCoursesRepository.deleteAll();
      userConfigRepository.deleteAll();
      courseLocationRepository.deleteAll();
      statisticsRepository.deleteAll();
      resetUserPasswordSecretStore();
   }

   @Test
   void initialize4UserAdded() {
      // Given
      String userId1 = "userId1";
      String userId2 = "userId2";
      String password1 = "userPassword1";
      String password2 = "userPassword2";
      String phoneNr = "+41791234567";

      CourseDefExtractorFacade courseBookerFacade = new TestCourseBookerFacade();
      SecretStoreService secretStoreService = new SecretStoreService(KEY_STORE_PASSWORD, TEST_RESOURCES_AQUABASILEA_KEYSTORE_KEYSTORE);
      CourseDefUpdater courseDefUpdater = getCourseDefUpdater(courseBookerFacade);

      UserAddedEvent userAddedEvent1 = createUserAddedEvent(USERNAME_1, userId1, password1, phoneNr);
      UserAddedEvent userAddedEvent2 = createUserAddedEvent(USERNAME_2, userId2, password2, phoneNr);
      AquabasileaLoginService aquabasileaLoginService = mockAquabasileaLoginService();
      UserRepository userRepository = mockUserRepository();
      AquabasileaAppInitializerImpl aquabasileaAppInitializer = getAquabasileaAppInitializer(userRepository, aquabasileaLoginService, courseDefUpdater);

      // When
      aquabasileaAppInitializer.initializeOnAppStart();
      aquabasileaAppInitializer.initialize(userAddedEvent1);
      aquabasileaAppInitializer.initialize(userAddedEvent2);

      // now wait until the course-booker is idle (since there are no courses defined)
      await().atMost(new Duration(20, TimeUnit.SECONDS)).until(() -> {
         AquabasileaCourseBooker aquabasileaCourseBookerUser1 = aquabasileaCourseBookerHolder.getForUserId(userId1);
         AquabasileaCourseBooker aquabasileaCourseBookerUser2 = aquabasileaCourseBookerHolder.getForUserId(userId2);
         return aquabasileaCourseBookerUser1.isPaused() && aquabasileaCourseBookerUser2.isPaused();
      });

      // Update courselocation: get heuwaage & aquabasilea from repository (this also verifies, that the CourseLocationInitializer has worked)
      CourseLocation aquabasilea = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      CourseLocation heuwaage = courseLocationRepository.findByCenterId(FITNESSPARK_HEUWAAGE.centerId());
      userConfigService.updateCourseLocations(userId1, List.of(heuwaage, aquabasilea));
      userConfigService.updateCourseLocations(userId2, List.of(aquabasilea));

      // Then
      // verify we validated the right credentials
      verify(aquabasileaLoginService).validateCredentials(eq(USERNAME_1), eq(password1.toCharArray()));
      verify(aquabasileaLoginService).validateCredentials(eq(USERNAME_2), eq(password2.toCharArray()));

      // verify we created a default config and updated the one of user 2
      UserConfig userConfig1 = userConfigService.getByUserId(userId1);
      assertThat(userConfig1.getCourseLocations().containsAll(List.of(heuwaage, aquabasilea)), is(true));
      UserConfig userConfig2 = userConfigService.getByUserId(userId2);
      assertThat(userConfig2.getCourseLocations().size(), is(1));
      assertThat(userConfig2.getCourseLocations().get(0), is(aquabasilea));

      // verify that we have stored the right passwords
      assertThat(secretStoreService.getUserPassword(USERNAME_1), is(password1.toCharArray()));
      assertThat(secretStoreService.getUserPassword(USERNAME_2), is(password2.toCharArray()));
      verify(userRoleConfigService).addMissingRoles(eq(userId1));
      verify(userRoleConfigService).addMissingRoles(eq(userId2));
   }

   private CourseDefUpdater getCourseDefUpdater(CourseDefExtractorFacade courseBookerFacade) {
      return new CourseDefUpdater(courseBookerFacade, statisticsService::needsCourseDefUpdate, courseDefRepository, userConfigRepository);
   }

   @Test
   void initializeOnAppStartForUserActivated() {
      // Given
      String userId1 = "userId1";
      String userId2 = "userId2";
      String password1 = "userPassword1";
      String password2 = "userPassword2";
      String phoneNr = "+41791234567";

      CourseDefUpdater courseDefUpdaterMock = mock(CourseDefUpdater.class);
      UserCredentialsHandler userCredentialsHandlerMock = mock(UserCredentialsHandler.class);
      PersistenceInitializer persistenceInitializerMock = mock(PersistenceInitializer.class);

      User user1 = User.of(userId1, USERNAME_1, password1, MobilePhone.of(phoneNr));
      User user2 = User.of(userId2, USERNAME_2, password2, MobilePhone.of(phoneNr));
      UserRepository userRepository = mockUserRepository(user1, user2);
      List<InitializerForUser> initializerForUsers = List.of(persistenceInitializerMock,
              userCredentialsHandlerMock, aquabasileaCourseBookerInitializer, new CourseDefUpdaterInitializer(courseDefUpdaterMock), new UserConfigInitializer(userRoleConfigService));
      AquabasileaAppInitializerImpl aquabasileaAppInitializer = new AquabasileaAppInitializerImpl(userRepository, initializerForUsers, getAppInitializers());

      // When
      aquabasileaAppInitializer.initializeOnAppStart();

      // Then
      verify(userCredentialsHandlerMock, never()).initialize(any());
      verify(persistenceInitializerMock, never()).initialize(any());
      verify(userRoleConfigService).addMissingRoles(eq(userId1));
      assertThat(courseLocationRepository.getAll().size(), is(2));
      CourseLocation aquabasilea = courseLocationRepository.findByCenterId(MIGROS_FITNESSCENTER_AQUABASILEA.centerId());
      CourseLocation heuwaage = courseLocationRepository.findByCenterId(FITNESSPARK_HEUWAAGE.centerId());
      assertThat(courseLocationRepository.getAll().containsAll(List.of(aquabasilea, heuwaage)), is(true));
      assertThat(aquabasileaCourseBookerHolder.getForUserId(userId1), is(notNullValue()));
      assertThat(aquabasileaCourseBookerHolder.getForUserId(userId2), is(notNullValue()));
      verify(userRoleConfigService).addMissingRoles(eq(userId2));
      verify(courseDefUpdaterMock).startScheduler(eq(userId1));
      verify(courseDefUpdaterMock).startScheduler(eq(userId2));

      aquabasileaCourseBookerHolder.removeForUserId(userId1);
      aquabasileaCourseBookerHolder.removeForUserId(userId2);
   }

   private static UserRepository mockUserRepository(User... users) {
      UserRepository userRepository = mock(UserRepository.class);
      when(userRepository.getAll()).thenReturn(Arrays.asList(users));
      return userRepository;
   }

   private AquabasileaAppInitializerImpl getAquabasileaAppInitializer(UserRepository userRepository, AquabasileaLoginService aquabasileaLoginService,
                                                                      CourseDefUpdater courseDefUpdater) {
      UserCredentialsHandler userCredentialsHandler = new UserCredentialsHandler(aquabasileaLoginService, KEY_STORE_PASSWORD, TEST_RESOURCES_AQUABASILEA_KEYSTORE_KEYSTORE);
      List<InitializerForUser> persistenceInitializer = List.of(this.persistenceInitializer, userCredentialsHandler,
              aquabasileaCourseBookerInitializer, new CourseDefUpdaterInitializer(courseDefUpdater), new UserConfigInitializer(userRoleConfigService));
      return new AquabasileaAppInitializerImpl(userRepository, persistenceInitializer, getAppInitializers());
   }

   private List<AppInitializer> getAppInitializers() {
      MigrosApiProvider migrosApiProvider = mock(MigrosApiProvider.class);
      when(migrosApiProvider.getMigrosApiCourseLocationExtractorFacade()).thenReturn(courseLocationExtractorFacade);
      return List.of(new CourseLocationInitializer(courseLocationRepository, new CourseBookerFacadeFactory(migrosApiProvider)));
   }

   private static UserAddedEvent createUserAddedEvent(String username, String userId, String password, String phoneNr) {
      char[] userPwd = password == null ? null : password.toCharArray();
      return new UserAddedEvent(username, phoneNr, userId, userPwd);
   }

   private static AquabasileaLoginService mockAquabasileaLoginService() {
      AquabasileaBearerTokenExtractor aquabasileaLogin = mock(AquabasileaBearerTokenExtractor.class);
      when(aquabasileaLogin.extractBearerToken()).thenReturn("true");
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

   private static class TestCourseBookerFacade implements CourseBookerFacade {
      @Override
      public CourseBookingResultDetails bookCourse(CourseBookContainer courseBookContainer) {
         return null;
      }

      @Override
      public List<Course> getBookedCourses() {
         return List.of();
      }

      @Override
      public CourseCancelResultDetails cancelCourses(String bookingId) {
         return CourseCancelResultDetails.notCanceled();
      }

      @Override
      public CourseDefExtractionResult getCourseDefs(String userId, List<CourseLocation> courseLocations) {
         return CourseDefExtractionResult.empty();
      }
   }
}