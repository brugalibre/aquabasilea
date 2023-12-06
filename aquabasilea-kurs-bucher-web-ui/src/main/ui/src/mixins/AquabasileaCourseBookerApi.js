import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';
import authHeader from "@/services/auth/auth-header";
import axios from "axios";
import store from "../store/index.js";
import LoggingService from "@/services/log/logging.service";

export default {
    name: 'AquabasileaCourseBookerApi',
    methods: {
        pauseOrResumeAquabasileaCourseBookerAndRefresh: function (onErrorCallback, onSuccessCallback) {
            axios.put(AQUABASILEA_COURSE_BOOKER_API_URL + '/pauseOrResume', null, {headers: authHeader()})
                .then(() => onSuccessCallback())
                .catch(error => {
                    LoggingService.logError('Error occurred while pausing or resuming', error)
                    onErrorCallback(LoggingService.extractErrorText(error));
                });
        },
        bookCurrentCourseDryRun: function (courseId) {
            axios.put(AQUABASILEA_COURSE_BOOKER_API_URL + '/bookCourseDryRun/' + courseId, null, {headers: authHeader()})
                .catch(error => console.error("Error occurred while pausing or resuming", error));
        },
        cancelBookedCourseAndRefresh: function (bookingId, onErrorCallback, onSuccessCallback) {
            axios.delete(AQUABASILEA_COURSE_BOOKER_API_URL + '/cancel/' + bookingId, {headers: authHeader()})
                .then(() => onSuccessCallback())
                .catch(error => {
                    LoggingService.logError('Error occurred while canceling course ' + bookingId, error)
                    store.dispatch('aquabasilea/setIsBookedCoursesLoading', false)
                        .then(() => onErrorCallback(LoggingService.extractErrorText(error)));
                });
        },
        fetchCourseBookingStateDto: function (onErrorCallback) {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/state', {headers: authHeader()})
                .then(response => response.data)
                .then(data => {
                    store.dispatch('aquabasilea/setCourseBookingState', data)
                        .then(() => store.dispatch('aquabasilea/setIsLoading', false));
                })
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching CourseBookingState', error)
                    onErrorCallback(LoggingService.extractErrorText(error));
                });
        },
        fetchBookedCourses: function (onErrorCallback) {
            store.dispatch('aquabasilea/setIsBookedCoursesLoading', true)
                .then(() => this.fetchBookedCoursesInternal(onErrorCallback));
        },
        fetchBookedCoursesInternal: function (onErrorCallback) {
            return axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/booked-courses', {headers: authHeader()})
                .then(response => response.data)
                .then(data => {
                    store.dispatch('aquabasilea/setBookedCourseDtos', data)
                        .then(() => store.dispatch('aquabasilea/setIsBookedCoursesLoading', false));
                })
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching the booked courses', error)
                    store.dispatch('aquabasilea/setIsBookedCoursesLoading', false)
                        .then(() => onErrorCallback(LoggingService.extractErrorText(error)));
                });
        }
    }
}
