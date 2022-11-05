import axios from 'axios';
import LoggingService from "@/services/log/logging.service";

const API_URL = '/api/auth/';

class AuthService {
    login(user, store) {
        console.log('user: ' + user + ', store: ' + store);
        return axios.post(API_URL + 'login', user)
            .then(response => {
                const responseUser = JSON.stringify(response.data);
                console.log('Response from login ' + responseUser);
                if (response.data.accessToken) {
                    localStorage.setItem('user', responseUser);
                    store.dispatch('auth/login', responseUser);
                }
                return response.data;
            })
            .catch(error => {
                LoggingService.logError("Error while log in", error);
            });
    }

    logout() {
        localStorage.removeItem('user');
    }

    register(user) {
        return axios.post(API_URL + 'register', user);
    }
}

export default new AuthService();
