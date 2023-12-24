package com.aquabasilea.rest.model.course.mapper;

import com.aquabasilea.application.i18n.TextResources;
import com.aquabasilea.domain.course.model.Course;
import com.aquabasilea.domain.courselocation.model.repository.CourseLocationRepository;
import com.aquabasilea.rest.i18n.LocaleProvider;
import com.aquabasilea.rest.model.course.weeklycourses.CourseDto;
import com.aquabasilea.rest.model.coursebooker.CourseLocationDto;
import com.aquabasilea.util.DateUtil;
import org.springframework.stereotype.Service;

import java.time.format.TextStyle;
import java.util.Locale;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service
public class CourseDtoMapper {
   private final CourseLocationRepository courseLocationRepository;
   private final LocaleProvider localeProvider;

   public CourseDtoMapper(CourseLocationRepository courseLocationRepository, LocaleProvider localeProvider) {
      this.courseLocationRepository = courseLocationRepository;
      this.localeProvider = localeProvider;
   }

   /**
    * Returns a new {@link CourseDto} for the given {@link Course}, {@link Locale} and boolean which defines if the given course
    * is the current course or not
    *
    * @param course          the course for which a {@link CourseDto} is build
    * @param isCurrentCourse <code>true</code> if the given {@link Course} is the current course or <code>false</code> if not
    * @return a new {@link CourseDto}
    */
   public CourseDto mapToCourseDto(Course course, boolean isCurrentCourse) {
      return new CourseDto(course.getId(), course.getCourseName(), course.getCourseInstructor(), course.getCourseDate().getDayOfWeek().getDisplayName(TextStyle.FULL, localeProvider.getCurrentLocale()),
              DateUtil.getTimeAsString(course.getCourseDate()), course.getCourseDate(), CourseLocationDto.of(course.getCourseLocation()),
              course.getIsPaused(), course.getHasCourseDef(), isCurrentCourse, getTooltipText(course, isCurrentCourse), course.getBookingIdTac());
   }

   private static String getTooltipText(Course course, boolean isCurrentCourse) {
      if (course.getBookingIdTac() != null) {
         return TextResources.TOOLTIP_BOOKED_COURSE.formatted(course.getCourseName(), DateUtil.getTimeAsString(course.getCourseDate()),
                 course.getCourseLocation().name(), course.getCourseInstructor());
      } else if (!course.getHasCourseDef()) {
         return TextResources.TOOLTIP_COURSE_HAS_NO_COURSE_DEF;
      } else if (course.getIsPaused()) {
         return TextResources.TOOLTIP_COURSE_IS_PAUSED;
      } else if (isCurrentCourse) {
         return TextResources.TOOLTIP_COURSE_IS_CURRENT_COURSE.formatted(course.getCourseInstructor());
      }
      return "";
   }

   /**
    * Maps the given {@link CourseDto} to a {@link Course}
    *
    * @param courseDto the given {@link CourseDto} to map
    * @return a {@link Course} instance
    */
   public Course map2Course(CourseDto courseDto) {
      String currentId = isNull(courseDto.id()) ? UUID.randomUUID().toString() : courseDto.id();
      return Course.CourseBuilder.builder()
              .withId(currentId)
              .withCourseName(courseDto.courseName())
              .withCourseInstructor(courseDto.courseInstructor())
              .withCourseDate(courseDto.courseDate())
              .withIsPaused(courseDto.isPaused())
              .withHasCourseDef(courseDto.hasCourseDef())
              .withCourseLocation(courseLocationRepository.findByCenterId(courseDto.courseLocationDto().centerId()))
              .build();
   }
}
