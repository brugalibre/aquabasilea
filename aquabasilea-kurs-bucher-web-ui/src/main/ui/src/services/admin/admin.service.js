import axios from 'axios';
import authHeader from "@/services/auth/auth-header";

const API_URL = '/api/v1/admin/';

class AdminService {
    getAdminOverview(store) {
        return axios.get(API_URL + 'overview', {headers: authHeader()})
            .then(response => response.data)
            .then(data => store.dispatch('aquabasilea/setAdminOverview', data))
    }
}

export default new AdminService();
