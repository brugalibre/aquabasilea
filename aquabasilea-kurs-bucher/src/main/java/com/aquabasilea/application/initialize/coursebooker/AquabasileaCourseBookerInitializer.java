package com.aquabasilea.application.initialize.coursebooker;

import com.aquabasilea.application.initialize.Initializer;
import com.aquabasilea.application.initialize.api.UserAddedEvent;
import com.aquabasilea.application.security.service.securestorage.SecretStoreService;
import com.aquabasilea.domain.course.repository.WeeklyCoursesRepository;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerExecutor;
import com.aquabasilea.domain.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.domain.coursebooker.booking.facade.AquabasileaCourseBookerFacadeFactory;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.statistics.service.BookingStatisticsUpdater;
import com.aquabasilea.notification.alertsend.CourseBookingAlertSender;
import com.aquabasilea.notification.alertsend.config.AlertSendConfigProviderImpl;
import com.aquabasilea.service.statistics.StatisticsService;
import com.brugalibre.notification.api.v1.alerttype.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AquabasileaCourseBookerInitializer implements Initializer {
   private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;
   private final WeeklyCoursesRepository weeklyCoursesRepository;
   private final AquabasileaCourseBookerFacadeFactory aquabasileaCourseBookerFacadeFactory;
   private final StatisticsService statisticsService;
   private final CourseDefRepository courseDefRepository;
   private final SecretStoreService secretStoreService;

   @Autowired
   public AquabasileaCourseBookerInitializer(AquabasileaCourseBookerHolder AquabasileaCourseBookerHolder,
                                             WeeklyCoursesRepository weeklyCoursesRepository,
                                             CourseDefRepository courseDefRepository, StatisticsService statisticsService,
                                             AquabasileaCourseBookerFacadeFactory aquabasileaCourseBookerFacadeFactory,
                                             SecretStoreService secretStoreService) {
      this.aquabasileaCourseBookerHolder = AquabasileaCourseBookerHolder;
      this.secretStoreService = secretStoreService;
      this.weeklyCoursesRepository = weeklyCoursesRepository;
      this.statisticsService = statisticsService;
      this.aquabasileaCourseBookerFacadeFactory = aquabasileaCourseBookerFacadeFactory;
      this.courseDefRepository = courseDefRepository;
   }

   @Override
   public void initialize(UserAddedEvent userAddedEvent) {
      AquabasileaCourseBooker aquabasileaCourseBooker = createAquabasileaCourseBooker(userAddedEvent);
      aquabasileaCourseBookerHolder.putForUserId(userAddedEvent.userId(), aquabasileaCourseBooker);
      aquabasileaCourseBooker.start();
   }

   private AquabasileaCourseBooker createAquabasileaCourseBooker(UserAddedEvent userAddedEvent) {
      AquabasileaCourseBooker aquabasileaCourseBooker = new AquabasileaCourseBooker(createUserContext(userAddedEvent), weeklyCoursesRepository,
              courseDefRepository, aquabasileaCourseBookerFacadeFactory);
      aquabasileaCourseBooker.addCourseBookingEndResultConsumer(new CourseBookingAlertSender(AlertSendConfigProviderImpl.of(() -> List.of(AlertType.SMS))));
      aquabasileaCourseBooker.addCourseBookingEndResultConsumer(new BookingStatisticsUpdater(statisticsService));
      new AquabasileaCourseBookerExecutor(aquabasileaCourseBooker);
      return aquabasileaCourseBooker;
   }

   private AquabasileaCourseBooker.UserContext createUserContext(UserAddedEvent userAddedEvent) {
      return new AquabasileaCourseBooker.UserContext(userAddedEvent.userId(), userAddedEvent.username(), userAddedEvent.phoneNr(),
              () -> secretStoreService.getUserPassword(userAddedEvent.username()));
   }
}
