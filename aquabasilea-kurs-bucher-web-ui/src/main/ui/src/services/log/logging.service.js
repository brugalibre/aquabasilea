class LoggingService {
    logError(text, error) {
        console.error(text, error);
    }

    extractErrorText(error) {
        return (error.response
                && error.response.data
                && error.response.data.message)
            || error.message
            || error.toString();
    }
}

export default new LoggingService();
