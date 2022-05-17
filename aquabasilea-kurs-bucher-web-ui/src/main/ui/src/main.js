import {createApp} from 'vue'
import App from './App.vue'
import {store} from './store/store';
import VueLoading from 'vue-loading-overlay';
import Multiselect from "@vueform/multiselect";

const app = createApp(App);
app.use(store);
app.component('Multiselect', Multiselect);
app.use(VueLoading, {
    color: '#0095c9'
});
app.mount('#app');
