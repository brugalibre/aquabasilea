package com.aquabasilea.app.initialize.coursebooker;

import com.aquabasilea.alerting.config.AlertSendConfigProviderImpl;
import com.aquabasilea.alerting.consumer.impl.CourseBookingAlertSender;
import com.aquabasilea.app.initialize.Initializer;
import com.aquabasilea.app.initialize.api.UserAddedEvent;
import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.coursebooker.AquabasileaCourseBookerHolder;
import com.aquabasilea.model.course.coursedef.repository.CourseDefRepository;
import com.aquabasilea.model.course.weeklycourses.repository.WeeklyCoursesRepository;
import com.aquabasilea.model.statistics.BookingStatisticsUpdater;
import com.aquabasilea.service.security.securestorage.SecretStoreService;
import com.aquabasilea.service.statistics.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AquabasileaCourseBookerInitializer implements Initializer {
   private final AquabasileaCourseBookerHolder aquabasileaCourseBookerHolder;
   private final WeeklyCoursesRepository weeklyCoursesRepository;
   private final StatisticsService statisticsService;
   private final CourseDefRepository courseDefRepository;
   private final SecretStoreService secretStoreService;

   private final AquabasileaCourseBookerSupplier aquabasileaCourseBookerSupplier = new AquabasileaCourseBookerSupplier();

   @Autowired
   public AquabasileaCourseBookerInitializer(AquabasileaCourseBookerHolder AquabasileaCourseBookerHolder,
                                             WeeklyCoursesRepository weeklyCoursesRepository,
                                             CourseDefRepository courseDefRepository, StatisticsService statisticsService,
                                             SecretStoreService secretStoreService) {
      this.aquabasileaCourseBookerHolder = AquabasileaCourseBookerHolder;
      this.secretStoreService = secretStoreService;
      this.weeklyCoursesRepository = weeklyCoursesRepository;
      this.statisticsService = statisticsService;
      this.courseDefRepository = courseDefRepository;
   }

   @Override
   public void initialize(UserAddedEvent userAddedEvent) {
      AquabasileaCourseBooker aquabasileaCourseBooker = createAquabasileaCourseBooker(userAddedEvent);
      aquabasileaCourseBooker.start();
      aquabasileaCourseBookerHolder.putForUserId(userAddedEvent.userId(), aquabasileaCourseBooker);
   }

   private AquabasileaCourseBooker createAquabasileaCourseBooker(UserAddedEvent userAddedEvent) {
      AquabasileaCourseBooker aquabasileaCourseBooker = new AquabasileaCourseBooker(createUserContext(userAddedEvent), weeklyCoursesRepository,
              courseDefRepository, createAquabasileaCourseBookerThread());
      aquabasileaCourseBooker.addCourseBookingEndResultConsumer(new CourseBookingAlertSender(AlertSendConfigProviderImpl.of()));
      aquabasileaCourseBooker.addCourseBookingEndResultConsumer(new BookingStatisticsUpdater(statisticsService));
      aquabasileaCourseBookerSupplier.aquabasileaCourseBooker = aquabasileaCourseBooker;
      return aquabasileaCourseBooker;
   }

   private Thread createAquabasileaCourseBookerThread() {
      Runnable threadRunnable = () -> aquabasileaCourseBookerSupplier.aquabasileaCourseBooker.run();
      return new Thread(threadRunnable);
   }

   private AquabasileaCourseBooker.UserContext createUserContext(UserAddedEvent userAddedEvent) {
      return new AquabasileaCourseBooker.UserContext(userAddedEvent.userId(), userAddedEvent.username(), userAddedEvent.phoneNr(),
              () -> secretStoreService.getUserPassword(userAddedEvent.username()));
   }

   /**
    * Hack/helper class in order to first instantiate the Thread instance and afterwards the AquabasileaCourseBooker
    */
   private static class AquabasileaCourseBookerSupplier {
      private AquabasileaCourseBooker aquabasileaCourseBooker;
   }
}