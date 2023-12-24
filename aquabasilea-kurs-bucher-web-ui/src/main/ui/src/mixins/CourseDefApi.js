import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';
import axios from 'axios';
import authHeader from "@/services/auth/auth-header";
import LoggingService from "@/services/log/logging.service";
import store from "../store/index.js";

export default {
    name: 'CourseDefApi',
    data() {
        return {
            isCourseDefUpdateRunning: false,
        }
    },
    methods: {
        fetchCourseDefDtos: function (onErrorCallback) {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/courseDefDtos4Filter', {headers: authHeader()})
                .then(response => response.data)
                .then(data => store.dispatch('aquabasilea/setCourseDefDtos', data))
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching CourseDefDtos', error)
                    onErrorCallback(LoggingService.extractErrorText(error));
                });
        },
        fetchCourseLocations: function (onErrorCallback) {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/allCourseLocationsDtos', {headers: authHeader()})
                .then(response => response.data)
                .then(data => store.dispatch('aquabasilea/setCourseLocationsDtos', data))
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching CourseLocations', error);
                    onErrorCallback(LoggingService.extractErrorText(error));
                });
        },
        fetchIsCourseDefUpdateRunning: function (onErrorCallback) {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/isCourseDefUpdateRunning', {headers: authHeader()})
                .then(response => response.data)
                .then(data => this.isCourseDefUpdateRunning = data)
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching isCourseDefUpdateRunning', error);
                    onErrorCallback(LoggingService.extractErrorText(error));
                });
        },
        updateCourseDefsAndRefresh: function (selectedCourseDefLocation, onErrorCallback, onSuccessCallback) {
            store.dispatch('aquabasilea/setIsLoading', true);
            axios.post(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/updateAll', JSON.parse(selectedCourseDefLocation), {headers: authHeader()})
                .then(() => onSuccessCallback())
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching updateCourseDefs', error);
                    onErrorCallback(LoggingService.extractErrorText(error));
                });
        },
    }
}
