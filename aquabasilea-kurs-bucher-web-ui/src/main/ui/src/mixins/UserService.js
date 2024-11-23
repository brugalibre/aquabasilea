import aquabasileaCourseBookerApi from '../mixins/AquabasileaCourseBookerApi';
import store from "../store/index.js";

export default {
    name: 'UserService',
    mixins: [aquabasileaCourseBookerApi],
    methods: {
        currentUser() {
            return store.state.auth.user;
        },
        hasCurrentUserRole(role) {
            return this.currentUser?.roles
                .find(currentUserRole => currentUserRole === role);
        }
    }
}
