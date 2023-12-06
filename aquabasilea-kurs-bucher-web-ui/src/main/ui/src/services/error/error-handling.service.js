import AuthService from "@/services/auth/auth.service";
import store from "../../store/index.js";
import router from "../../router.js";
import RouterConstants from "@/router-constants";

class ErrorHandlingService {
    handleError(errorBoxRef, error) {
        store.dispatch('aquabasilea/setIsLoading', false);
        if (AuthService.isAuthenticationFailed(error)) {
            store.dispatch('auth/logout')
                .then(() => router.push(RouterConstants.LOGIN_PATH));
        } else {
            errorBoxRef.errorDetails = error;
        }
    }
}

export default new ErrorHandlingService();
