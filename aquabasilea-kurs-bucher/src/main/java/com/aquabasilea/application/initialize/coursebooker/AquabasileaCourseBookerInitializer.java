package com.aquabasilea.application.initialize.coursebooker;

import com.aquabasilea.application.config.ConfigYamlFilePaths;
import com.aquabasilea.application.initialize.api.user.InitializerForUser;
import com.aquabasilea.application.initialize.api.user.UserAddedEvent;
import com.aquabasilea.application.initialize.common.InitType;
import com.aquabasilea.application.initialize.common.InitializeOrder;
import com.aquabasilea.application.security.model.UserContext;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerExecutor;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.domain.coursebooker.states.booking.facade.CourseBookerFacadeFactory;
import com.aquabasilea.domain.coursebooker.config.AquabasileaCourseBookerConfig;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.statistics.service.BookingStatisticsUpdater;
import com.aquabasilea.notification.alertsend.CourseBookingAlertSender;
import com.aquabasilea.service.statistics.StatisticsService;
import com.brugalibre.notification.config.AlertSendConfigProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.aquabasilea.application.initialize.common.InitializationConst.AQUABASILEA_COURSE_BOOKER;

@Service
@InitializeOrder(order = AQUABASILEA_COURSE_BOOKER, type = {InitType.USER_ACTIVATED, InitType.USER_ADDED})
public class AquabasileaCourseBookerInitializer implements InitializerForUser {
   private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;
   private final WeeklyCoursesRepository weeklyCoursesRepository;
   private final CourseBookerFacadeFactory courseBookerFacadeFactory;
   private final StatisticsService statisticsService;
   private final CourseDefRepository courseDefRepository;
   private final AlertSendConfigProvider alertSendConfigProvider;
   private final String courseBookerConfigFilePath;

   @Autowired
   public AquabasileaCourseBookerInitializer(AquabasileaCourseBookerHolder AquabasileaCourseBookerHolder,
                                             WeeklyCoursesRepository weeklyCoursesRepository,
                                             CourseDefRepository courseDefRepository, StatisticsService statisticsService,
                                             CourseBookerFacadeFactory courseBookerFacadeFactory,
                                             AlertSendConfigProvider alertSendConfigProvider,
                                             ConfigYamlFilePaths configYamlFilePaths) {
      this.aquabasileaCourseBookerHolder = AquabasileaCourseBookerHolder;
      this.weeklyCoursesRepository = weeklyCoursesRepository;
      this.statisticsService = statisticsService;
      this.courseBookerFacadeFactory = courseBookerFacadeFactory;
      this.courseDefRepository = courseDefRepository;
      this.courseBookerConfigFilePath = configYamlFilePaths.getCourseBookerConfigFilePath();
      this.alertSendConfigProvider = alertSendConfigProvider;
   }

   @Override
   public void initialize(UserAddedEvent userAddedEvent) {
      AquabasileaCourseBooker aquabasileaCourseBooker = createAquabasileaCourseBooker(userAddedEvent);
      aquabasileaCourseBookerHolder.putForUserId(userAddedEvent.userId(), aquabasileaCourseBooker);
      aquabasileaCourseBooker.start();
   }

   private AquabasileaCourseBooker createAquabasileaCourseBooker(UserAddedEvent userAddedEvent) {
      AquabasileaCourseBooker aquabasileaCourseBooker = new AquabasileaCourseBooker(createUserContext(userAddedEvent), weeklyCoursesRepository,
              courseDefRepository, new AquabasileaCourseBookerConfig(courseBookerConfigFilePath), courseBookerFacadeFactory);
      aquabasileaCourseBooker.addCourseBookingEndResultConsumer(new CourseBookingAlertSender(alertSendConfigProvider));
      aquabasileaCourseBooker.addCourseBookingEndResultConsumer(new BookingStatisticsUpdater(statisticsService));
      new AquabasileaCourseBookerExecutor(aquabasileaCourseBooker, userAddedEvent.userId());
      return aquabasileaCourseBooker;
   }

   private UserContext createUserContext(UserAddedEvent userAddedEvent) {
      return new UserContext(userAddedEvent.userId(), userAddedEvent.phoneNr());
   }
}
