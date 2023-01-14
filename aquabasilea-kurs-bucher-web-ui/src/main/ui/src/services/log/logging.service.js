class LoggingService {
    logError(text, error) {
        console.error(text, error);
    }

    extractErrorText(error) {
        return error?.response?.data?.error ? error.response.data.error : error.toString();
    }
}

export default new LoggingService();
