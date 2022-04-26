import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';

export default {
    name: 'AquabasileaCourseBookerApi',
    methods: {
        pauseOrResumeAquabasileaCourseBooker: function () {
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/pauseOrResume', {method: 'POST'})
                .then(response => console.log('Response: ' + JSON.stringify(response)))
                .catch(error => console.error("Error occurred while pausing or resuming", error));
        },
        fetchCourseBookingStateDto: function () {
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/state', {method: 'GET'})
                .then(response => response.json())
                .then(data => {
                    this.$store.dispatch('setCourseBookingState', data);
                    this.$store.dispatch('setIsLoading', false);
                })
                .catch(error => console.error('Error occurred while fetching CourseBookingState', error));
        },
    }
}
