import axios from 'axios';
import authHeader from "@/services/auth/auth-header";

const API_URL = '/api/brugalibre/common/v1/auth/';

class AuthService {
    login(user) {
        return axios.post(API_URL + 'login', user)
            .then(response => {
                if (response?.data?.accessToken) {
                    this.setCurrentUser(response.data);
                }
                return response.data;
            });
    }

    changePassword(changeUserPasswordRequest) {
        return axios.put(API_URL + 'changePassword', changeUserPasswordRequest, {headers: authHeader()});
    }

    logout() {
        localStorage.removeItem('user');
    }

    register(user) {
        return axios.post(API_URL + 'register', user);
    }

    setCurrentUser(currentUser) {
        localStorage.setItem('user', JSON.stringify(currentUser));
    }

    isAuthenticationFailed(error) {
        // not very elegant, but it does the trick..
        return error === 'Unauthorized'
            || error === 'AxiosError: Request failed with status code 401';
    }
}

export default new AuthService();
