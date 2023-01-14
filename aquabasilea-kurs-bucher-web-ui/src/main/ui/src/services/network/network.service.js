class NetworkService {

    isNetworkError(error) {
        return error === 'AxiosError: Network Error';// not very elegant, but it does the trick..
    }
}

export default new NetworkService();
