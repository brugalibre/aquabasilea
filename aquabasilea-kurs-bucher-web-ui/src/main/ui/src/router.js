import {createRouter, createWebHistory} from "vue-router";
import AquabasileaDashboard from "./components/AquabasileaDashboard.vue";
import Login from "./components/auth/Login.vue";
import Register from "./components/auth/Register.vue";
import Admin from "./components/admin/Admin.vue";
import RouterConstants from "@/router-constants";
// lazy-loaded
const Profile = () => import("./components/user/Profile.vue")

const routes = [
    {
        path: RouterConstants.COURSE_OVERVIEW_PATH,
        component: AquabasileaDashboard,
    },
    {
        path: RouterConstants.LOGIN_PATH,
        component: Login,
    },
    {
        path: RouterConstants.REGISTER_PATH,
        component: Register,
    },
    {
        path: RouterConstants.ADMIN_PATH,
        component: Admin,
    },
    {
        path: RouterConstants.PROFILE_PATH,
        name: "profile",
        // lazy-loaded
        component: Profile,
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    const publicPages = [RouterConstants.LOGIN_PATH, RouterConstants.REGISTER_PATH];
    const authRequired = !publicPages.includes(to.path);
    const loggedIn = localStorage.getItem('user');
    // trying to access a restricted page + not logged in
    // redirect to login page
    if (authRequired && !loggedIn) {
        next(RouterConstants.LOGIN_PATH);
    } else {
        next();
    }
});

export default router;