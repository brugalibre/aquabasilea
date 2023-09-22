import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';
import authHeader from "@/services/auth/auth-header";
import axios from "axios";
import LoggingService from "@/services/log/logging.service";

export default {
    name: 'AquabasileaCourseBookerApi',
    methods: {
        pauseOrResumeAquabasileaCourseBooker: function () {
            axios.put(AQUABASILEA_COURSE_BOOKER_API_URL + '/pauseOrResume', null, {headers: authHeader()})
                .catch(error => console.error("Error occurred while pausing or resuming", error));
        },
        bookCurrentCourseDryRun: function (courseId) {
            axios.put(AQUABASILEA_COURSE_BOOKER_API_URL + '/bookCourseDryRun/' + courseId, null, {headers: authHeader()})
                .catch(error => console.error("Error occurred while pausing or resuming", error));
        },
        cancelBookedCourseAndRefresh: function (bookingId, errorCallback) {
            axios.delete(AQUABASILEA_COURSE_BOOKER_API_URL + '/cancel/' + bookingId, {headers: authHeader()})
                .then(() => this.$emit('refreshBookedCourses'))
                .catch(error => {
                    LoggingService.logError('Error occurred while canceling course ' + bookingId, error)
                    this.$store.dispatch('aquabasilea/setIsBookedCoursesLoading', false);
                    errorCallback(LoggingService.extractErrorText(error));
                });
        },
        fetchCourseBookingStateDto: function () {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/state', {headers: authHeader()})
                .then(response => response.data)
                .then(data => {
                    this.$store.dispatch('aquabasilea/setCourseBookingState', data);
                    this.$store.dispatch('aquabasilea/setIsLoading', false);
                })
                .catch(error => console.error('Error occurred while fetching CourseBookingState', error));
        },
        fetchBookedCourses: function (errorCallback) {
            this.$store.dispatch('aquabasilea/setIsBookedCoursesLoading', true);
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/booked-courses', {headers: authHeader()})
                .then(response => response.data)
                .then(data => {
                    this.$store.dispatch('aquabasilea/setBookedCourseDtos', data);
                    this.$store.dispatch('aquabasilea/setIsBookedCoursesLoading', false);
                })
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching the booked courses', error)
                    this.$store.dispatch('aquabasilea/setIsBookedCoursesLoading', false);
                    errorCallback(LoggingService.extractErrorText(error));
                });
        },
    }
}
