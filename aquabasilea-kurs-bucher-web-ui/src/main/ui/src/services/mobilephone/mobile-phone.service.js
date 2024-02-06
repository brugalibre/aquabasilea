import axios from 'axios';
import authHeader from "@/services/auth/auth-header";

const API_URL = '/api/brugalibre/common/v1/mobilephone/';

class MobilePhoneService {

    changePhoneNumber(changeMobilePhoneRequest) {
        return axios.post(API_URL + 'change', changeMobilePhoneRequest, {headers: authHeader()});
    }
}

export default new MobilePhoneService();
