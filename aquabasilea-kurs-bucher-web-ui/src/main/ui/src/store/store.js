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
    },
    getters: {
        courseBookingStateDto: state => {
            return state.courseBookingStateDto;
        },
        weeklyCourses: state => {
            return state.weeklyCourses;
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
        setIsLoading(context, isLoading) {
            context.commit("setIsLoading", isLoading);
        },
    },
});
