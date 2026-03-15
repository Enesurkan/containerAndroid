package com.example.altintakipandroid.data.api

import android.util.Log
import com.example.altintakipandroid.BuildConfig
import com.example.altintakipandroid.domain.AppConstants
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val LOG_TAG = "Api"
    private const val MAX_LOG_CHUNK = 3500

    private val gsonPretty = GsonBuilder().setPrettyPrinting().create()

    private fun prettyLog(tag: String, message: String) {
        val text = message.trim()
        val pretty = try {
            if (text.startsWith("{") || text.startsWith("[")) {
                val element = JsonParser.parseString(text)
                gsonPretty.toJson(element)
            } else text
        } catch (_: Exception) {
            text
        }
        if (pretty.length <= MAX_LOG_CHUNK) {
            Log.d(tag, pretty)
        } else {
            var start = 0
            var part = 1
            while (start < pretty.length) {
                val end = minOf(start + MAX_LOG_CHUNK, pretty.length)
                Log.d(tag, "($part) ${pretty.substring(start, end)}")
                start = end
                part++
            }
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        prettyLog(LOG_TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /** Release'de API istek/cevap ve header loglanmaz (reverse eng. riski azaltır). */
    private val okHttpClientBuilder = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "--> REQUEST ${request.method} ${request.url}")
                request.headers.forEach { (name, value) ->
                    Log.d(LOG_TAG, "    $name: $value")
                }
            }
            val built = request.newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            val response = chain.proceed(built)
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "<-- RESPONSE ${response.code} ${response.message} ${response.request.url}")
                response.headers.forEach { (name, value) ->
                    Log.d(LOG_TAG, "    $name: $value")
                }
            }
            response
        }
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)

    init {
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        // Certificate pinning: Release'de API host için pin eklenebilir (SHA-256).
        // Pin almak: openssl s_client -servername api.dienu.work -connect api.dienu.work:443 | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64
        val certificatePinner = buildCertificatePinner()
        if (certificatePinner != null) {
            okHttpClientBuilder.certificatePinner(certificatePinner)
        }
    }

    /**
     * Sertifika pinning. Pin değerini almak için:
     * echo | openssl s_client -servername api.dienu.work -connect api.dienu.work:443 2>/dev/null | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64
     * Detay: docs/CERT_PINNING.md
     */
    private fun buildCertificatePinner(): CertificatePinner? {
        val pin = BuildConfig.CERT_PIN_API_DIENU_WORK
        if (pin.isBlank()) return null
        val fullPin = if (pin.startsWith("sha256/")) pin else "sha256/$pin"
        return CertificatePinner.Builder()
            .add("api.dienu.work", fullPin)
            .build()
    }

    private val okHttpClient = okHttpClientBuilder.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(AppConstants.BASE_URL.ensureTrailingSlash())
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

private fun String.ensureTrailingSlash(): String = if (this.endsWith("/")) this else "$this/"
