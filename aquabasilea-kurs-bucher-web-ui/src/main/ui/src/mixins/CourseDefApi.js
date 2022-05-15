import {AQUABASILEA_COURSE_BOOKER_API_URL} from '@/mixins/CommonAquabasileaRestApi';

function logError(text, error) {
    this.$store.dispatch('setIsLoading', false);
    console.error(text, error);
}

export default {
    name: 'CourseDefApi',
    data() {
        return {
            isCourseDefUpdateRunning: false,
        }
    },
    methods: {
        fetchCourseDefDtos: function (filter) {
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/getCourseDefDtos4Filter/' + filter)
                .then(response => response.json())
                .then(data => this.$store.dispatch('setCourseDefDtos', data))
                .catch(error => logError('Error occurred while fetching CourseDefDtos', error));
        },
        fetchCourseLocations: function () {
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/getCourseLocationsDtos/')
                .then(response => response.json())
                .then(data => this.$store.dispatch('setCourseLocationsDtos', data))
                .catch(error => logError('Error occurred while fetching CourseLocations', error));
        },
        fetchIsCourseDefUpdateRunning: function () {
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/isCourseDefUpdateRunning/')
                .then(response => response.json())
                .then(data => this.isCourseDefUpdateRunning = data)
                .catch(error => logError('Error occurred while fetching isCourseDefUpdateRunning', error));
        },
        updateCourseDefs: function (selectedCourseDefLocation) {
            fetch(AQUABASILEA_COURSE_BOOKER_API_URL + '/coursedef/updateCourseDefs/', {
                method: 'POST', body: selectedCourseDefLocation,
                headers: {'Content-Type': 'application/json',}
            })
                .then(response => response.json())
                .catch(error => logError('Error occurred while fetching updateCourseDefs', error));
        },
    }
}
