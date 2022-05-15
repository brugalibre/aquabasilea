import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';

export default {
    name: 'WeeklyCoursesApi',
    data() {
        return {
            // the possible days of the week
            daysOfTheWeek: [],
            postErrorDetails: '',
        };
    },
    methods: {
        logError: function (text, error) {
            this.$store.dispatch('setIsLoading', false);
            this.postErrorDetails = error;
            console.error(text, error);
        },
        handleResponseNok: function (response) {
            const plainData = response.text();
            const data = (plainData ? JSON.parse(plainData) : {});
            // get error message from body or default to response status
            const error = (data && data.message) || response.status;
            this.postErrorDetails = data;
            return Promise.reject(error);
        },
        fetchDaysOfTheWeek4Course: function (courseName) {
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/getDaysOfTheWeek4Course/' + courseName)
                .then(response => response.json())
                .then(data => this.daysOfTheWeek = data)
                .catch(error => this.logError('Error occurred while fetching WeeklyCourses', error));
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
                .then(response => {
                    // check for error response
                    if (!response.ok) {
                        return this.handleResponseNok(response);
                    }
                    return response;
                }).catch(error => this.logError('There was an error!', error));
        },
        deleteCourse: function (course) {
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/' + course.id, {method: 'DELETE'})
                .then(response => response.json())
                .then(data => this.postId = data.id)
                .catch(error => this.logError('Error occurred during deleting course\'' + course + '\'', error));
        },
        pauseResumeCourse: function (course) {
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/weekly-courses/pauseResumeCourse/' + course.id, {method: 'POST'})
                .then(response => response.json())
                .then(data => this.postId = data.id)
                .catch(error => this.logError('Error occurred during pausing/resuming course\'' + course + '\'', error));
        },
    }
}
