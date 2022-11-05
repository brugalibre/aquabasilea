import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';
import axios from "axios";
import authHeader from "@/services/auth/auth-header";
import LoggingService from "@/services/log/logging.service";

export default {
    name: 'WeeklyCoursesApi',
    methods: {
        logError: function (errorMsg) {
            this.$store.dispatch('aquabasilea/setIsLoading', false);
            console.error(errorMsg);
            this.$emit('error-occurred', errorMsg);
        },
        fetchWeeklyCourses: function () {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/weeklyCourses', {headers: authHeader()})
                .then(response => response.data)
                .then(data => {
                    this.$store.dispatch('aquabasilea/setWeeklyCourses', data);
                    this.$store.dispatch('aquabasilea/setIsLoading', false);
                })
                .catch(error => this.logError('Error occurred while fetching WeeklyCourses', error));
        },
        addCourse: function (courseBody) {

            console.log('Adding course \'' + courseBody + '\'');
            // Call finally the api in order to add the new user
            axios.post(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/addCourse', courseBody, {headers: authHeader()})
                .then(response => response.data)
                .then(async response => {
                    this.$emit('error-occurred', null);
                    // retrieve the response in two steps: text -> JSON.parse. That's necessary in case we receive an empty response (which leads always to an exception)
                    const plainData = await response.text();
                    const data = await (plainData ? JSON.parse(plainData) : {});
                    // check for error response
                    if (!response.ok) {
                        // get error message from body or default to response status
                        const errorDetails = data ? data.error : 'upsidupsi, fehler nicht gefunden';
                        const errorMsg = 'Fehler beim Hinzufügen des Kurses \'' + courseBody.courseNam + '\': ' + errorDetails + ' (Fehlercode ' + response.status + ')';
                        return Promise.reject(errorMsg);
                    }
                }).catch(error => this.logError(error));
        },
        deleteCourse: function (course) {
            axios.delete(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/' + course.id, {headers: authHeader()})
                .then(async response => {
                    this.$emit('error-occurred', null);
                    // retrieve the response in two steps: text -> JSON.parse. That's necessary in case we receive an empty response (which leads always to an exception)
                    const plainData = await response.text();
                    const data = await (plainData ? JSON.parse(plainData) : {});
                    // check for error response
                    if (!response.ok) {
                        // get error message from body or default to response status
                        const errorDetails = data ? data.error : 'upsidupsi, fehler nicht gefunden';
                        const errorMsg = 'Fehler beim Löschen des Kurses \'' + course.courseName + '\': ' + errorDetails + ' (Fehlercode ' + response.status + ')';
                        return Promise.reject(errorMsg);
                    }
                })
                .catch(error => LoggingService.logError("error while deleting course '" + course + "'", error))
                .finally(()=> this.$store.dispatch('aquabasilea/setIsLoading', false));
        },
        pauseResumeCourse: function (course) {
            this.$emit('error-occurred', null);
            axios.post(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/pauseResumeCourse/' + course.id, {headers: authHeader()})
                .then(async response => {
                    // retrieve the response in two steps: text -> JSON.parse. That's necessary in case we receive an empty response (which leads always to an exception)
                    const plainData = await response.text();
                    const data = await (plainData ? JSON.parse(plainData) : {});
                    // check for error response
                    if (!response.ok) {
                        // get error message from body or default to response status
                        const errorDetails = data ? data.error : 'upsidupsi, fehler nicht gefunden';
                        const errorMsg = 'Fehler beim pausieren des Kurses \'' + course.courseName + '\': ' + errorDetails + ' (Fehlercode ' + response.status + ')';
                        return Promise.reject(errorMsg);
                    }
                })
                .catch(error => this.logError(error))
                .catch(error => LoggingService.logError("Error beim Pausieren oder Resumen..", error))
                .finally(()=> this.$store.dispatch('aquabasilea/setIsLoading', false));
        },
    }
}
