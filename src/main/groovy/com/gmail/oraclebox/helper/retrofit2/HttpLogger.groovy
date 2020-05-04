package com.gmail.oraclebox.helper.retrofit2

import okhttp3.logging.HttpLoggingInterceptor
import java.util.logging.Logger

class HttpLogger implements HttpLoggingInterceptor.Logger {

    Logger log = Logger.getLogger(HttpLogger.class.getName());

    @Override
    void log(String message) {
        log.info(message);
    }
}
