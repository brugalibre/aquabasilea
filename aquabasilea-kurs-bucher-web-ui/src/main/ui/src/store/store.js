import {createStore} from 'vuex'

export const store = createStore({
    state: {
        isLoading: false,
        courseBookingStateDto: {
            stateMsg: '',
            pauseOrResumeButtonText: '',
            state: '',
        },
        weeklyCourses: {
            courseDtos: [],
        },
        courseDefDtos: [],
        courseLocationsDtos: [],
    },
    getters: {
        courseBookingStateDto: state => {
            return state.courseBookingStateDto;
        },
        weeklyCourses: state => {
            return state.weeklyCourses;
        },
        courseDefDtos: state => {
            return state.courseDefDtos;
        },
        courseLocationsDtos: state => {
            return state.courseLocationsDtos;
        },
        isLoading: state => {
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
        setIsLoading(context, isLoading) {
            context.commit("setIsLoading", isLoading);
        },
    },
});
