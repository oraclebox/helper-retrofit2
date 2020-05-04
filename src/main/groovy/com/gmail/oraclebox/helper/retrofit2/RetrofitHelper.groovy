package com.gmail.oraclebox.helper.retrofit2

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.lang3.StringUtils
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit

class RetrofitHelper {

    static final int CONN_TIMEOUT = 30;

    /**
     * Retrofit without proxy
     */
    static Retrofit getRetofitNonProxy(String baseurl, ObjectMapper objectMapper) {
        return getRetofit(baseurl, null, -1, objectMapper, null)
    }
    /**
     * Retrofit allow proxy
     */
    static Retrofit getRetofit(String baseurl, String proxyHost, int proxyPort, ObjectMapper objectMapper) {
        return getRetofit(baseurl, proxyHost, proxyPort, objectMapper, null)
    }

    static Retrofit getRetofit(String baseurl, String proxyHost, int proxyPort, ObjectMapper objectMapper, HttpLoggingInterceptor.Logger logger) {
        if (objectMapper != null) {
            objectMapper = new ObjectMapper();
        }
        if (logger == null)
            logger = new HttpLogger();
        OkHttpClient client = null;
        if (StringUtils.isNotBlank(proxyHost)) {
            client = okhttpclient(logger, CONN_TIMEOUT, TimeUnit.SECONDS, proxyHost, proxyPort);
        } else {
            client = okhttpclient(logger, CONN_TIMEOUT, TimeUnit.SECONDS);
        }
        return new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client)
                .build();
    }

    static Retrofit getUnsafeRetrofit(String baseurl, String proxyHost, int proxyPort, ObjectMapper objectMapper, HttpLoggingInterceptor.Logger logger) {
        if (objectMapper != null) {
            objectMapper = new ObjectMapper();
        }
        if (logger == null)
            logger = new HttpLogger();
        OkHttpClient client = null;
        if (StringUtils.isNotBlank(proxyHost)) {
            client = unsafeOkHttpClient(logger, CONN_TIMEOUT, TimeUnit.SECONDS, proxyHost, proxyPort);
        } else {
            client = unsafeOkHttpClient(logger, CONN_TIMEOUT, TimeUnit.SECONDS, null, -1);
        }
        return new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client)
                .build();
    }

    static Retrofit getRetrofitStringConverter(String baseurl, String proxyHost, int proxyPort, ObjectMapper objectMapper) {
        if (objectMapper != null) {
            objectMapper = new ObjectMapper();
        }
        OkHttpClient client = null;
        if (StringUtils.isNotBlank(proxyHost)) {
            client = okhttpclient(new HttpLogger(), CONN_TIMEOUT, TimeUnit.SECONDS, proxyHost, proxyPort);
        } else {
            client = okhttpclient(new HttpLogger(), CONN_TIMEOUT, TimeUnit.SECONDS);
        }
        return new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(client)
                .build();
    }

    static OkHttpClient okhttpclient(HttpLoggingInterceptor.Logger logger, int timeout, TimeUnit timeUnit) {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(logger);
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .connectTimeout(timeout, timeUnit)
                .addNetworkInterceptor(logInterceptor)
                .build();
    }

    /**
     * Retrofit 2 — How to Trust Unsafe SSL certificates (Self-signed, Expired)
     * https://futurestud.io/tutorials/retrofit-2-how-to-trust-unsafe-ssl-certificates-self-signed-expired
     */
    static OkHttpClient unsafeOkHttpClient(HttpLoggingInterceptor.Logger logger, int timeout, TimeUnit timeUnit, String proxyHost, int proxyPort) {
        // Use proxy for outgoing request
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (proxyHost != null && proxyPort > 0) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            builder.proxy(proxy);
        }

        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = [
                new X509TrustManager() {
                    @Override
                    void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    X509Certificate[] getAcceptedIssuers() {
                        return [];
                    }
                }
        ];

        // Create an ssl socket factory with our all-trusting manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

        // Prepare logger
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(logger);
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return builder.connectTimeout(timeout, timeUnit)
                .addNetworkInterceptor(logInterceptor)
                .build();
    }

    /**
     * Connect via proxy server
     */
    static OkHttpClient okhttpclient(HttpLoggingInterceptor.Logger logger, int timeout, TimeUnit timeUnit, String proxyHost, int proxyPort) {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(logger);
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Use proxy for outgoing request
        if (proxyHost != null && proxyPort > 0) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            return new OkHttpClient.Builder()
                    .proxy(proxy)
                    .connectTimeout(timeout, timeUnit)
                    .addNetworkInterceptor(logInterceptor)
                    .build();
        }
        return okhttpclient(logger, timeout, timeUnit);
    }

    static OkHttpClient okhttpclient(HttpLoggingInterceptor.Logger logger) {
        return okhttpclient(logger, 15, TimeUnit.SECONDS);
    }


    static <T> T getReplyDataObject(Reply reply, Class<T> type, ObjectMapper objectMapper) {
        return objectMapper.readValue(objectMapper.writeValueAsString(reply.dataObject), type);
    }

}