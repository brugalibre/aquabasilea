import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';

function logError(text, error) {
    this.$store.dispatch('setIsLoading', false);
    console.error(text, error);
}

export default {
    name: 'StatisticsApi',
    methods: {
        fetchStatisticsDto: function () {
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/statistics/getStatistics')
                .then(response => response.json())
                .then(data => this.$store.dispatch('setStatisticsDto', data))
                .catch(error => logError('Error occurred while fetching StatisticsDto', error));
        },
    }
}
