import dayjs from "dayjs";

export const AQUABASILEA_COURSE_BOOKER_API_URL = '/api/v1/aquabasilea-course-booker';

export default {
    name: 'CommonAquabasileaRestApi',
    methods: {
        formatDate(dateString) {
            const date = dayjs(dateString);
            return date.format('DD.MM.YYYY HH:mm');
        }
    }
}