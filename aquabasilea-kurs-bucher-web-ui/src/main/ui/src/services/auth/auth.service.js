import axios from 'axios';
import authHeader from "@/services/auth/auth-header";

const API_URL = '/api/auth/';

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
        return axios.post(API_URL + 'changePassword', changeUserPasswordRequest, {headers: authHeader()});
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
        return error === 'Unauthorized';// not very elegant, but it does the trick..
    }
}

export default new AuthService();
