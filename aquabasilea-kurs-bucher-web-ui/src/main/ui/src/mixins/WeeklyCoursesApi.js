import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';

export default {
    name: 'WeeklyCoursesApi',
    methods: {
        logError: function (text, error) {
            this.$store.dispatch('setIsLoading', false);
            console.error(text, error);
            this.$emit('error-occurred', 'Unerwarteter Fehler: ' + text);
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
                        const error = (data && data.message) || response.status;
                        this.$emit('error-occurred', 'Fehler beim Hinzufügen des Kurses \'' + courseBody.courseName +  '\' (Fehlercode ' + error + ')');
                        return Promise.reject(error);
                    }
                }).catch(error => this.logError('There was an error!', error));
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
                        const error = (data && data.message) || response.status;
                        this.$emit('error-occurred', 'Fehler beim Löschen des Kurses \'' + course.courseName + '\' (Fehlercode ' + error + ')');
                        return Promise.reject(error);
                    }
                })
                .catch(error => this.logError('Error occurred during deleting course\'' + course + '\'', error));
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
                        const error = (data && data.message) || response.status;
                        this.$emit('error-occurred', 'Fehler beim pausieren des Kurses \'' + course.courseName + '\' (Fehlercode ' + error + ')');
                        return Promise.reject(error);
                    }
                })
                .catch(error => this.logError('Error occurred during pausing/resuming course\'' + course.courseName + '\'', error));
        },
    }
}
