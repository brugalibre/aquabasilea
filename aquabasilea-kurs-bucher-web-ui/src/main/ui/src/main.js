import {createApp} from 'vue'
import App from './App.vue'
import router from "./router";
import store from "./store";
import VueLoading from 'vue-loading-overlay';
import Multiselect from "@vueform/multiselect";
import {
    CAccordion,
    CAccordionBody,
    CAccordionHeader,
    CAccordionItem,
    CAlert,
    CButton,
    CDropdown,
    CDropdownItem,
    CDropdownMenu,
    CDropdownToggle,
    CFormInput,
    CTooltip,
    vctooltip
} from '@coreui/vue';

createApp(App)
    .component('CButton', CButton)
    .component('CAccordion', CAccordion)
    .component('CAccordionItem', CAccordionItem)
    .component('CAccordionHeader', CAccordionHeader)
    .component('CAccordionBody', CAccordionBody)
    .component('CFormInput', CFormInput)
    .component('CDropdown', CDropdown)
    .component('CDropdownToggle', CDropdownToggle)
    .component('CDropdownMenu', CDropdownMenu)
    .component('CDropdownItem', CDropdownItem)
    .component('CAlert', CAlert)
    .component('CTooltip', CTooltip)
    .component('Multiselect', Multiselect)
    .directive('c-tooltip', vctooltip)
    .use(router)
    .use(store)
    .use(VueLoading, {
        color: '#0095c9'
    })
    .mount('#app');
