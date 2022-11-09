import {createRouter, createWebHistory} from "vue-router";
import AquabasileaDashboard from "./components/AquabasileaDashboard.vue";
import Login from "./components/auth/Login.vue";
import Register from "./components/auth/Register.vue";
// lazy-loaded
const Profile = () => import("./components/user/Profile.vue")

const routes = [
    {
        path: "/",
        component: AquabasileaDashboard,
    },
    {
        path: "/login",
        component: Login,
    },
    {
        path: "/register",
        component: Register,
    },
    {
        path: "/profile",
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
    const publicPages = ['/login', '/register'];
    const authRequired = !publicPages.includes(to.path);
    const loggedIn = localStorage.getItem('user');
    // trying to access a restricted page + not logged in
    // redirect to login page
    if (authRequired && !loggedIn) {
        next('/login');
    } else {
        next();
    }
});

export default router;