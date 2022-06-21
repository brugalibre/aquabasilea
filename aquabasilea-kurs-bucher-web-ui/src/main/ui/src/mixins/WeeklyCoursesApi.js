import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';

export default {
    name: 'WeeklyCoursesApi',
    methods: {
        logError: function (errorMsg) {
            this.$store.dispatch('setIsLoading', false);
            console.error(errorMsg);
            this.$emit('error-occurred', errorMsg);
        },
        fetchWeeklyCourses: function () {
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/getWeeklyCourses')
                .then(response => response.json())
                .then(data => {
                    this.$store.dispatch('setWeeklyCourses', data);
                    this.$store.dispatch('setIsLoading', false);
                })
                .catch(error => this.logError('Error occurred while fetching WeeklyCourses', error));
        },
        addCourse: function (courseBody) {
            const requestOptions = {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: courseBody
            };
            console.log('Adding course \'' + courseBody + '\'');
            const courseName = courseBody.courseName;
            // Call finally the api in order to add the new user
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/addCourse', requestOptions)
                .then(async response => {
                    this.$emit('error-occurred', null);
                    // retrieve the response in two steps: text -> JSON.parse. That's necessary in case we receive an empty response (which leads always to an exception)
                    const plainData = await response.text();
                    const data = await (plainData ? JSON.parse(plainData) : {});
                    // check for error response
                    if (!response.ok) {
                        // get error message from body or default to response status
                        const errorDetails = data ? data.error : 'upsidupsi, fehler nicht gefunden';
                        const errorMsg = 'Fehler beim Hinzufügen des Kurses \'' + courseName + '\': ' + errorDetails + ' (Fehlercode ' + response.status + ')';
                        return Promise.reject(errorMsg);
                    }
                }).catch(error => this.logError(error));
        },
        deleteCourse: function (course) {
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/' + course.id, {method: 'DELETE'})
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
                .catch(error => this.logError(error));
        },
        pauseResumeCourse: function (course) {
            this.$emit('error-occurred', null);
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/pauseResumeCourse/' + course.id, {method: 'POST'})
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
                .catch(error => this.logError(error));
        },
    }
}
