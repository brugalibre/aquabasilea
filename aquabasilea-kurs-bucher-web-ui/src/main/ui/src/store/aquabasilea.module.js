const APP_OFFLINE_TXT = 'Application offline';
export const aquabasilea = {
    namespaced: true,
    state: () => ({
        isLoading: false,
        statisticsDto: {
            lastCourseDefUpdate: ' - ',
            nextCourseDefUpdate: ' - ',
            uptimeRepresentation: ' - ',
            totalBookingCounter: 0,
            bookingSuccessRate: 0,

        },
        courseBookingStateDto: {
            stateMsg: APP_OFFLINE_TXT,
            pauseOrResumeButtonText: APP_OFFLINE_TXT,
            state: 'OFFLINE',
        },
        weeklyCourses: {
            courseDtos: [],
        },
        courseDefDtos: [],
        courseLocationsDtos: [],
    }),
    getters: {
        courseBookingStateDto(state) {
            return state.courseBookingStateDto;
        },
        weeklyCourses(state) {
            return state.weeklyCourses;
        },
        courseDefDtos(state) {
            return state.courseDefDtos;
        },
        courseLocationsDtos(state) {
            return state.courseLocationsDtos;
        },
        statisticsDto(state) {
            return state.statisticsDto;
        },
        isLoading(state) {
            return state.isLoading;
        }
    },
    mutations: {
        setCourseBookingState(state, courseBookingState) {
            state.courseBookingStateDto = courseBookingState;
        },
        setWeeklyCourses(state, weeklyCourses) {
            state.weeklyCourses = weeklyCourses;
        },
        setCourseDefDtos(state, courseDefDtos) {
            state.courseDefDtos = courseDefDtos;
        },
        setCourseLocationsDtos(state, courseLocationsDtos) {
            state.courseLocationsDtos = courseLocationsDtos;
        },
        setStatisticsDto(state, statisticsDto) {
            state.statisticsDto = statisticsDto;
        },
        setIsLoading(state, isLoading) {
            state.isLoading = isLoading;
        },
    },
    actions: {
        setCourseBookingState(context, courseBookingState) {
            context.commit("setCourseBookingState", courseBookingState);
        },
        setWeeklyCourses(context, weeklyCourses) {
            context.commit("setWeeklyCourses", weeklyCourses);
        },
        setCourseDefDtos(context, courseDefDtos) {
            context.commit("setCourseDefDtos", courseDefDtos);
        },
        setCourseLocationsDtos(context, courseLocationsDtos) {
            context.commit("setCourseLocationsDtos", courseLocationsDtos);
        },
        setStatisticsDto(context, statisticsDto) {
            context.commit("setStatisticsDto", statisticsDto);
        },
        setIsLoading(context, isLoading) {
            context.commit("setIsLoading", isLoading);
        },
    },
};
