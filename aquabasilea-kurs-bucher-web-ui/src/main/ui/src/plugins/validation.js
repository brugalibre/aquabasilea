import {ErrorMessage, Field, Form,} from "vee-validate";

export default {
    install(app) {
        app.component("VeeForm", Form);
        app.component("VeeField", Field);
        app.component("ErrorMessage", ErrorMessage);
    },
};
