import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';
import axios from 'axios';
import authHeader from "@/services/auth/auth-header";
import LoggingService from "@/services/log/logging.service";

export default {
    name: 'CourseDefApi',
    data() {
        return {
            isCourseDefUpdateRunning: false,
        }
    },
    methods: {
        fetchCourseDefDtos: function (filter) {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/courseDefDtos4Filter/' + filter, {headers: authHeader()})
                .then(response => response.data)
                .then(data => this.$store.dispatch('aquabasilea/setCourseDefDtos', data))
                .catch(error => LoggingService.logError('Error occurred while fetching CourseDefDtos', error))
                .finally(() => this.$store.dispatch('aquabasilea/setIsLoading', false));
        },
        fetchCourseLocations: function () {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/allCourseLocationsDtos/', {headers: authHeader()})
                .then(response => response.data)
                .then(data => this.$store.dispatch('aquabasilea/setCourseLocationsDtos', data))
                .catch(error => LoggingService.logError('Error occurred while fetching CourseLocations', error))
                .finally(() => this.$store.dispatch('aquabasilea/setIsLoading', false));
        },
        fetchIsCourseDefUpdateRunning: function () {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/isCourseDefUpdateRunning/', {headers: authHeader()})
                .then(response => response.data)
                .then(data => this.isCourseDefUpdateRunning = data)
                .catch(error => LoggingService.logError('Error occurred while fetching isCourseDefUpdateRunning', error))
                .finally(() => this.$store.dispatch('aquabasilea/setIsLoading', false));
        },
        updateCourseDefs: function (selectedCourseDefLocation) {
            axios.post(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/updateCourseDefs/', selectedCourseDefLocation, {headers: authHeader()})
                .catch(error => LoggingService.logError('Error occurred while fetching updateCourseDefs', error))
                .finally(() => this.$store.dispatch('aquabasilea/setIsLoading', false));
        },
    }
}
