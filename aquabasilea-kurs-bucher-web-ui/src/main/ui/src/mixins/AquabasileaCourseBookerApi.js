import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';
import authHeader from "@/services/auth/auth-header";
import axios from "axios";

export default {
    name: 'AquabasileaCourseBookerApi',
    methods: {
        pauseOrResumeAquabasileaCourseBooker: function () {
            axios.put(AQUABASILEA_COURSE_BOOKER_API_URL + '/pauseOrResume', null, {headers: authHeader()})
                .catch(error => console.error("Error occurred while pausing or resuming", error));
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
    }
}
