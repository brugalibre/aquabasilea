import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';
import LoggingService from "@/services/log/logging.service";
import authHeader from "@/services/auth/auth-header";
import axios from "axios";
import store from "../store/index.js";

export default {
    name: 'StatisticsApi',
    methods: {
        fetchStatisticsDto: function (errorCallback) {
            axios.get(AQUABASILEA_COURSE_BOOKER_API_URL + '/statistics', {headers: authHeader()})
                .then(response => response.data)
                .then(data => store.dispatch('aquabasilea/setStatisticsDto', data))
                .catch(error => {
                    LoggingService.logError('Error occurred while fetching StatisticsDto', error);
                    errorCallback(LoggingService.extractErrorText(error));
                });
        },
    }
}
