import axios from 'axios';
import authHeader from "@/services/auth/auth-header";

const API_URL = '/api/user/';

class UserService {

    changePhoneNumber(changeUser) {
        return axios.post(API_URL + 'change', changeUser, {headers: authHeader()});
    }
}

export default new UserService();
