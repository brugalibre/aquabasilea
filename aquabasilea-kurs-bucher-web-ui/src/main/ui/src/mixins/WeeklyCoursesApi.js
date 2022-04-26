import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';

export default {
    name: 'WeeklyCoursesApi',
    data() {
        return {
            // the possible days of the week
            daysOfTheWeek: [],
        };
    },
    methods: {
        logError: function (text, error) {
            this.$store.dispatch('setIsLoading', false);
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
        addCourse: function () {
            const requestOptions = {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    courseName: this.courseName,
                    dayOfWeek: this.dayOfWeek,
                    isPaused: false,
                    isCurrentCourse: false,
                    timeOfTheDay: this.timeOfTheDay
                })
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
        changeCourse: function (course) {
            const requestOptions = {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    courseName: course.courseName,
                    id: course.id,
                    dayOfWeek: course.dayOfWeek,
                    timeOfTheDay: course.timeOfTheDay,
                })
            };
            this.postErrorDetails = null;
            // Call finally the api in order to change the course
            fetch(weeklyCoursesApiUrl + '/changeCourse', requestOptions)
                .then(response => {
                    if (!response.ok) {
                        return handleResponseNok.call(this, response);
                    }
                }).catch(error => this.logError('Error occurred while changing a course\'' + JSON.stringify(course) + '\'', error));
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
