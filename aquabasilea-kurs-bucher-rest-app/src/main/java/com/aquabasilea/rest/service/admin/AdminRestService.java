package com.aquabasilea.rest.service.admin;

import com.aquabasilea.domain.admin.model.AdminOverview;
import com.aquabasilea.rest.i18n.LocaleProvider;
import com.aquabasilea.rest.model.admin.AdminOverviewDto;
import com.aquabasilea.rest.model.admin.Course4AdminViewDto;
import com.aquabasilea.rest.service.statistics.StatisticsRestService;
import com.aquabasilea.service.admin.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminRestService {

   private final AdminService adminService;
   private final LocaleProvider localeProvider;
   private final StatisticsRestService statisticsRestService;

   public AdminRestService(AdminService adminService, LocaleProvider localeProvider,
                           StatisticsRestService statisticsRestService) {
      this.adminService = adminService;
      this.statisticsRestService = statisticsRestService;
      this.localeProvider = localeProvider;
   }

   public AdminOverviewDto getAdminOverviewDto() {
      AdminOverview adminOverview = adminService.getAdminOverviewDto();
      return new AdminOverviewDto(adminOverview.totalAquabasileaCourseBooker(),
              adminOverview.totalBookingCounter(), adminOverview.bookingSuccessRate(),
              statisticsRestService.getDurationString(), adminOverview.nextCurrentCourses()
              .stream()
              .map(course4AdminView -> Course4AdminViewDto.of(course4AdminView, localeProvider.getCurrentLocale()))
              .toList());
   }
}
