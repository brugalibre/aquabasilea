import {createApp} from 'vue'
import App from './App.vue'
import {store} from './store/store';
import VueLoading from 'vue-loading-overlay';
import Multiselect from "@vueform/multiselect";
import {
    CAccordion,
    CAccordionBody,
    CAccordionHeader,
    CAccordionItem,
    CButton,
    CDropdown,
    CDropdownItem,
    CDropdownMenu,
    CDropdownToggle,
    CFormInput,
    CTooltip,
    CAlert,
    vctooltip
} from '@coreui/vue';

const app = createApp(App);
app.use(store);
app.component('CButton', CButton);
app.component('CAccordion', CAccordion);
app.component('CAccordionItem', CAccordionItem);
app.component('CAccordionHeader', CAccordionHeader);
app.component('CAccordionBody', CAccordionBody);
app.component('CFormInput', CFormInput);
app.component('CDropdown', CDropdown);
app.component('CDropdownToggle', CDropdownToggle);
app.component('CDropdownMenu', CDropdownMenu);
app.component('CDropdownItem', CDropdownItem);
app.component('CAlert', CAlert);
app.component('CTooltip', CTooltip);
app.directive('c-tooltip', vctooltip);
app.component('Multiselect', Multiselect);
app.use(VueLoading, {
    color: '#0095c9'
});
app.mount('#app');
