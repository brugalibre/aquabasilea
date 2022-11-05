import {library} from "@fortawesome/fontawesome-svg-core";
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {
    faHome,
    faMagnifyingGlassLocation,
    faSignInAlt,
    faSignOutAlt,
    faSlash,
    faSquareParking,
    faTerminal,
    faUser,
    faUserPlus
} from "@fortawesome/free-solid-svg-icons";

library.add(faHome, faTerminal, faSlash, faUser, faUserPlus, faSignInAlt, faSignOutAlt, faSquareParking, faMagnifyingGlassLocation);

export {FontAwesomeIcon};
