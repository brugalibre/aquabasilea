module.exports = {
    devServer: {
        port: 3000,
        proxy: {
            '/api': {
                // target: 'https://localhost:8443',
                target: 'http://localhost:8080/',
                ws: true,
                // https: true,
                changeOrigin: true
            }
        }
    }
}