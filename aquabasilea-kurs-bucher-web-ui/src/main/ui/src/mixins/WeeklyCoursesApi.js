import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';
import axios from "axios";
import store from "../store/index.js";
import aquabasileaCourseBookerApi from '../mixins/AquabasileaCourseBookerApi';
import authHeader from "@/services/auth/auth-header";
import LoggingService from "@/services/log/logging.service";

export default {
    name: 'WeeklyCoursesApi',
    mixins: [aquabasileaCourseBookerApi],
    methods: {
        /**
         * Clears all CourseDtos of the weeklyCourses (which is currently stored in the store)
         * and stores this updated instance again
         */
        resetWeeklyCourseDtosAndStore: function () {
            this.weeklyCourses.courseDtos = store.state.aquabasilea.weeklyCourses.courseDtos
                .filter(() => false);
            store.dispatch('aquabasilea/setWeeklyCourses', this.weeklyCourses);
        },
        fetchWeeklyCourses: function (onErrorCallback) {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/weeklyCourses', {headers: authHeader()})
                .then(response => response.data)
                .then(data => store.dispatch('aquabasilea/setWeeklyCourses', data))
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching WeeklyCourses', error);
                    onErrorCallback(LoggingService.extractErrorText(error));
                });
        },
        /**
         * Adds the given course to the weekly-courses. If this is the first course we add, then we will resume
         * the application async. after we added the course.
         * @param courseBody
         * @param onErrorCallback callback-handler which displays any error which might occur during the http-call
         * @param onSuccessCallback callback-handler which is called when the axios post was successful
         */
        addCourseAndRefresh: function (courseBody, onErrorCallback, onSuccessCallback) {
            store.dispatch('aquabasilea/setIsLoading', true);
            const hadOtherCourses = store.state.aquabasilea.weeklyCourses.courseDtos.length > 0;
            axios.post(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/course', JSON.parse(courseBody), {headers: authHeader()})
                .then(() => this.resumeApplicationAsyncIfNecessary(!hadOtherCourses, onErrorCallback, onSuccessCallback))
                .then(() => onSuccessCallback())
                .catch(error => {
                    LoggingService.logError("Error while adding course '" + courseBody + "'", error);
                    onErrorCallback(LoggingService.extractErrorText(error));
                });
        },
        deleteCourseAndRefresh: function (course, onErrorCallback, onSuccessCallback) {
            store.dispatch('aquabasilea/setIsLoading', true);
            axios.delete(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/' + course.id, {headers: authHeader()})
                .then(() => onSuccessCallback())
                .catch(error => {
                    LoggingService.logError("error while deleting course '" + JSON.stringify(course) + "'", error);
                    onErrorCallback(LoggingService.extractErrorText(error));
                });
        },
        /**
         * Resumes the given course and the application - if necessary.
         * If this is the only course, and it's paused right now, then we resume the application right after we resumed the course
         * @param course the course to resume
         * @param onErrorCallback callback-handler which displays any error which might occur during the http-call
         * @param onSuccessCallback callback-handler which is called when the axios post was successful
         */
        pauseResumeCourseAndRefresh: function (course, onErrorCallback, onSuccessCallback) {
            store.dispatch('aquabasilea/setIsLoading', true);
            const amountOfCourses = store.state.aquabasilea.weeklyCourses.courseDtos.length;
            axios.put(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/pauseResumeCourse/' + course.id, null, {headers: authHeader()})
                .then(() => this.resumeApplicationAsyncIfNecessary(course.isPaused && amountOfCourses === 1, onErrorCallback, onSuccessCallback))
                .then(() => onSuccessCallback())
                .catch(error => {
                    LoggingService.logError("Error beim Pausieren oder Resumen..", error);
                    onErrorCallback(LoggingService.extractErrorText(error));
                });
        },
        resumeApplicationAsyncIfNecessary(resume, onErrorCallback, onSuccessCallback) {
            if (resume) {
                setTimeout(() => {
                    this.pauseOrResumeAquabasileaCourseBookerAndRefresh(false, onErrorCallback, onSuccessCallback);
                }, 10);
            }
        }
    }
}
