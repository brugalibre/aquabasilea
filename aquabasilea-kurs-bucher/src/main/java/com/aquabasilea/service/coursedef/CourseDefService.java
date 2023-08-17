package com.aquabasilea.service.coursedef;

import com.aquabasilea.domain.course.CourseLocation;
import com.aquabasilea.domain.coursedef.model.CourseDef;
import com.aquabasilea.domain.coursedef.model.repository.CourseDefRepository;
import com.aquabasilea.domain.coursedef.update.notify.CourseDefUpdatedNotifier;
import com.aquabasilea.domain.coursedef.update.notify.OnCourseDefsUpdatedContext;
import com.aquabasilea.domain.coursedef.update.service.CourseDefUpdaterService;
import com.aquabasilea.service.userconfig.UserConfigService;
import com.brugalibre.domain.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseDefService implements CourseDefUpdatedNotifier {
    private final CourseDefUpdaterService courseDefUpdaterService;
    private final CourseDefRepository courseDefRepository;
    private final UserConfigService userConfigService;
    private final Map<String, List<CourseDef>> userId2CachedCourseDefs;

    @Autowired
    public CourseDefService(CourseDefRepository courseDefRepository, CourseDefUpdaterService courseDefUpdaterService,
                            UserConfigService userConfigService) {
        this.courseDefUpdaterService = courseDefUpdaterService;
        this.courseDefRepository = courseDefRepository;
        this.userConfigService = userConfigService;
        this.courseDefUpdaterService.addCourseDefUpdatedNotifier(this);
        this.userId2CachedCourseDefs = new HashMap<>();
    }

    public boolean isCourseDefUpdateRunning(String currentUserId) {
        return courseDefUpdaterService.isCourseDefUpdateRunning(currentUserId);
    }

    public void updateCourseDefs(String userId, List<CourseLocation> courseLocations) {
        courseDefUpdaterService.updateAquabasileaCourses(userId, courseLocations);
    }

    /**
     * Returns a list with {@link CourseLocation}s for all existing {@link CourseLocation}s. All {@link CourseLocation}
     * which are actually stored for the user are marked as 'selected'
     *
     * @param userId the technical id of a {@link User}
     * @return a {@link List} with {@link CourseLocation}s for all existing {@link CourseLocation}s
     */
    public List<CourseLocation> getCourseLocationsByUserId(String userId) {
        return userConfigService.getCourseLocations4UserId(userId);
    }

    /**
     * @param userId the id of the {@link User}
     * @return a {@link List} with all {@link CourseDef}s for the given user id
     */
    public List<CourseDef> getAllByUserId(String userId) {
        return getAllCourseDefs(userId);
    }

    private List<CourseDef> getAllCourseDefs(String userId) {
        synchronized (userId2CachedCourseDefs) {
            if (!userId2CachedCourseDefs.containsKey(userId)) {
                List<CourseDef> cachedCourseDefs = courseDefRepository.getAllByUserId(userId);
                userId2CachedCourseDefs.put(userId, cachedCourseDefs);
            }
            return userId2CachedCourseDefs.get(userId);
        }
    }

    /**
     * Updates all {@link CourseDef} according user's configuration (which defines the {@link CourseLocation}s to consider)
     * According to those the Aquabasilea-courses defined on their course-page are considered
     *
     * @param userId          the id of the {@link User}
     * @param courseLocations the {@link CourseLocation}s which are considered when updating the {@link CourseDef}s
     */
    public void updateAquabasileaCourses(String userId, List<CourseLocation> courseLocations) {
        this.courseDefUpdaterService.updateAquabasileaCourses(userId, courseLocations);
    }

    @Override
    public void courseDefsUpdated(OnCourseDefsUpdatedContext onCourseDefsUpdatedContext) {
        String userId = onCourseDefsUpdatedContext.userId();
        List<CourseDef> updatedCourseDefs = onCourseDefsUpdatedContext.updatedCourseDefs();
        synchronized (userId2CachedCourseDefs) {
            userId2CachedCourseDefs.put(userId, updatedCourseDefs);
        }
    }
}
