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
            pauseOrResumeButtonText: 'Reaktiviere Kurs-Bucher',
            state: 'OFFLINE',
        },
        weeklyCourses: {
            courseDtos: [],
        },
        adminOverview: {
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
        adminOverview(state) {
            return state.adminOverview;
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
        setAdminOverview(state, adminOverview) {
            state.adminOverview = adminOverview;
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
        setAdminOverview(context, adminOverview) {
            context.commit("setAdminOverview", adminOverview);
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
