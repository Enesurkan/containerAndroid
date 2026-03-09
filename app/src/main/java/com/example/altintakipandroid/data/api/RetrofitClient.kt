package com.example.altintakipandroid.data.api

import android.util.Log
import com.example.altintakipandroid.domain.AppConstants
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
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

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d(LOG_TAG, "--> REQUEST ${request.method} ${request.url}")
            request.headers.forEach { (name, value) ->
                Log.d(LOG_TAG, "    $name: $value")
            }
            val built = request.newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            val response = chain.proceed(built)
            Log.d(LOG_TAG, "<-- RESPONSE ${response.code} ${response.message} ${response.request.url}")
            response.headers.forEach { (name, value) ->
                Log.d(LOG_TAG, "    $name: $value")
            }
            response
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(AppConstants.BASE_URL.ensureTrailingSlash())
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

private fun String.ensureTrailingSlash(): String = if (this.endsWith("/")) this else "$this/"
