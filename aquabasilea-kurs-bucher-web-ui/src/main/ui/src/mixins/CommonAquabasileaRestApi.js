import dayjs from "dayjs";

export const AQUABASILEA_COURSE_BOOKER_API_URL = '/api/activfitness/v1/course-booker';

export default {
    name: 'CommonAquabasileaRestApi',
    methods: {
        formatDate(dateString) {
            const date = dayjs(dateString);
            return date.format('DD.MM.YYYY HH:mm');
        }
    }
}