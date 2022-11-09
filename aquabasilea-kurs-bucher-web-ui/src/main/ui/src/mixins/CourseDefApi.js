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
            console.log('Get courses for filter \'' + filter + '\'');
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/courseDefDtos4Filter/' + filter, {headers: authHeader()})
                .then(response => response.data)
                .then(data => this.$store.dispatch('aquabasilea/setCourseDefDtos', data))
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching CourseDefDtos', error)
                    this.$emit('error-occurred', LoggingService.extractErrorText(error));
                });
        },
        fetchCourseLocations: function () {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/allCourseLocationsDtos', {headers: authHeader()})
                .then(response => response.data)
                .then(data => this.$store.dispatch('aquabasilea/setCourseLocationsDtos', data))
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching CourseLocations', error);
                    this.$emit('error-occurred', LoggingService.extractErrorText(error));
                });
        },
        fetchIsCourseDefUpdateRunning: function () {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/isCourseDefUpdateRunning', {headers: authHeader()})
                .then(response => response.data)
                .then(data => this.isCourseDefUpdateRunning = data)
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching isCourseDefUpdateRunning', error);
                    this.$emit('error-occurred', LoggingService.extractErrorText(error));
                });
        },
        updateCourseDefs: function (selectedCourseDefLocation) {
            axios.post(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/updateAll', JSON.parse(selectedCourseDefLocation), {headers: authHeader()})
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching updateCourseDefs', error);
                    this.$emit('error-occurred', LoggingService.extractErrorText(error));
                });
        },
    }
}
