class LoggingService {
    logError(text, error) {
        console.error(text, error);
    }

    extractErrorText(error) {
        return error?.response?.data?.error ? error.response.data.error : error.toString();
    }

    isAuthenticationFailed(error) {
        return error === 'Unauthorized';// not very elegant, but it does the trick..
    }
}

export default new LoggingService();
