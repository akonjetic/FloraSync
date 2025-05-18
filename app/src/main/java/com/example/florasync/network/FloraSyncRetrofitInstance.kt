package com.example.florasync.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object FloraSyncRetrofitInstance {

    private const val BASE_URL = "http://192.168.1.37:5072/api/"
    private val floraSyncApiService: FloraSyncApiService


    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val httpClient = OkHttpClient.Builder().addInterceptor(interceptor).sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager).hostnameVerifier { _, _ -> true }

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(MoshiConverterFactory.create()).client(httpClient.build()).build()

        floraSyncApiService = retrofit.create(FloraSyncApiService::class.java)
    }

    fun getService() : FloraSyncApiService = floraSyncApiService
}
