import {createStore} from "vuex";
import {auth} from "./auth.module";
import {aquabasilea} from "./aquabasilea.module";

const store = createStore({
    modules: {
        auth,
        aquabasilea,
    },
});

export default store;
