import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';
import axios from "axios";
import aquabasileaCourseBookerApi from '../mixins/AquabasileaCourseBookerApi';
import authHeader from "@/services/auth/auth-header";
import LoggingService from "@/services/log/logging.service";

export default {
    name: 'WeeklyCoursesApi',
    mixins: [aquabasileaCourseBookerApi],
    methods: {
        fetchWeeklyCourses: function () {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/weeklyCourses', {headers: authHeader()})
                .then(response => response.data)
                .then(data => this.$store.dispatch('aquabasilea/setWeeklyCourses', data))
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching WeeklyCourses', error);
                    this.$emit('error-occurred', LoggingService.extractErrorText(error));
                });
        },
        /**
         * Adds the given course to the weekly-courses. If this is the first course we add, then we will resume
         * the application async. after we added the course.
         * @param courseBody
         */
        addCourse: function (courseBody) {
            const hadOtherCourses = this.$store.state.aquabasilea.weeklyCourses.courseDtos.length > 0;
            axios.post(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/course', JSON.parse(courseBody), {headers: authHeader()})
                .then(() => this.resumeApplicationAsyncIfNecessary(!hadOtherCourses))
                .catch(error => {
                    LoggingService.logError("Error while adding course '" + courseBody + "'", error);
                    this.$emit('error-occurred', LoggingService.extractErrorText(error));
                });
        },
        deleteCourse: function (course) {
            axios.delete(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/' + course.id, {headers: authHeader()})
                .catch(error => {
                    LoggingService.logError("error while deleting course '" + course + "'", error);
                    this.$emit('error-occurred', LoggingService.extractErrorText(error));
                });
        },
        /**
         * Resumes the given course and the application - if necessary.
         * If this is the only course, and it's paused right now, then we resume the application right after we resumed the course
         * @param course the course to resume
         */
        pauseResumeCourse: function (course) {
            this.$emit('error-occurred', null);
            const amountOfCourses = this.$store.state.aquabasilea.weeklyCourses.courseDtos.length;
            axios.put(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/pauseResumeCourse/' + course.id, null,{headers: authHeader()})
                .then(() => this.resumeApplicationAsyncIfNecessary(course.isPaused && amountOfCourses === 1))
                .catch(error => {
                    LoggingService.logError("Error beim Pausieren oder Resumen..", error);
                    this.$emit('error-occurred', LoggingService.extractErrorText(error));
                });
        },
        resumeApplicationAsyncIfNecessary(resume) {
            if (resume) {
                setTimeout(() => {
                    this.pauseOrResumeAquabasileaCourseBooker();
                }, 300);
            }
        }
    }
}
